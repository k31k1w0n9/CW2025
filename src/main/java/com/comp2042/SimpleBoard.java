package com.comp2042;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.RandomBrickGenerator;

public class SimpleBoard implements Board {

    private final int width;  // 10 columns
    private final int height; // 22 rows total (2 hidden + 20 visible)
    private final BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;
    private int[][] currentGameMatrix;
    private Point currentOffset;
    private final Score score;
    private final List<Brick> nextPieceQueue = new ArrayList<>();

    private Brick holdPiece = null;
    private boolean canHold = true;

    public SimpleBoard(int height, int width) {
        this.width = width;   // 10
        this.height = height; // 22
        currentGameMatrix = new int[height][width]; // 22 rows x 10 cols
        brickGenerator = new RandomBrickGenerator();
        brickRotator = new BrickRotator();
        score = new Score();
        refillNextPieceQueue();
        createNewBrick();
    }

    private void refillNextPieceQueue() {
        while (nextPieceQueue.size() < 5) {
            nextPieceQueue.add(brickGenerator.getBrick());
        }
    }

    @Override
    public boolean moveBrickDown() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(0, 1);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    @Override
    public boolean moveBrickLeft() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(-1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    @Override
    public boolean moveBrickRight() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    @Override
    public boolean rotateLeftBrick() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        NextShapeInfo nextShape = brickRotator.getNextShape();
        boolean conflict = MatrixOperations.intersect(currentMatrix, nextShape.getShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
        if (conflict) {
            return false;
        } else {
            brickRotator.setCurrentShape(nextShape.getPosition());
            return true;
        }
    }

    @Override
    public boolean createNewBrick() {
        if (nextPieceQueue.isEmpty()) {
            refillNextPieceQueue();
        }

        Brick currentBrick = nextPieceQueue.remove(0);
        brickRotator.setBrick(currentBrick);
        refillNextPieceQueue();

        // GDD Section 3.2: Spawn in hidden buffer zone (row 0)
        // This is the TOP of the 22-row matrix
        currentOffset = new Point(3, 0);  // Column 3 (center), Row 0 (buffer)
        canHold = true;

        System.out.println("=== NEW BRICK SPAWNED ===");
        System.out.println("Brick type: " + currentBrick.getClass().getSimpleName());
        System.out.println("Spawn position: (" + currentOffset.x + ", " + currentOffset.y + ")");
        System.out.println("Game matrix position: Row " + currentOffset.y + " (buffer zone)");

        // Check collision for game over
        boolean collision = MatrixOperations.intersect(
                currentGameMatrix,
                brickRotator.getCurrentShape(),
                (int) currentOffset.getX(),
                (int) currentOffset.getY()
        );

        if (collision) {
            System.out.println("❌ COLLISION AT SPAWN - GAME OVER");
        }

        return collision;
    }

    @Override
    public boolean holdPiece() {
        if (!canHold) {
            return false;
        }

        Brick currentBrick = brickRotator.getBrick();

        if (holdPiece == null) {
            holdPiece = currentBrick;
            createNewBrick();
        } else {
            Brick temp = holdPiece;
            holdPiece = currentBrick;
            brickRotator.setBrick(temp);
            currentOffset = new Point(3, 0); // Spawn at top center
        }

        canHold = false;
        return true;
    }

    @Override
    public int[][] getHoldPieceShape() {
        if (holdPiece == null) {
            return null;
        }
        return holdPiece.getShapeMatrix().get(0);
    }

    @Override
    public int[][] getBoardMatrix() {
        return currentGameMatrix;
    }

    @Override
    public ViewData getViewData() {
        List<int[][]> nextShapes = nextPieceQueue.stream()
                .map(brick -> brick.getShapeMatrix().get(0))
                .collect(Collectors.toList());

        return new ViewData(
                brickRotator.getCurrentShape(),
                (int) currentOffset.getX(),
                (int) currentOffset.getY(),
                nextShapes,
                getGhostXPosition(),
                getGhostYPosition()
        );
    }

    @Override
    public void mergeBrickToBackground() {
        currentGameMatrix = MatrixOperations.merge(
                currentGameMatrix,
                brickRotator.getCurrentShape(),
                (int) currentOffset.getX(),
                (int) currentOffset.getY()
        );
    }

    @Override
    public int getGhostYPosition() {
        int ghostY = (int) currentOffset.getY();
        int currentX = (int) currentOffset.getX();
        int[][] currentShape = brickRotator.getCurrentShape();

        // FIXED: Move ghost down until it would collide
        // The loop checks ghostY+1, so when it finds a collision, ghostY is the last valid position
        // CRITICAL: Stop before going out of bounds (row 22 is out of bounds for 22-row matrix)
        while (ghostY + 1 < currentGameMatrix.length && 
               !MatrixOperations.intersect(currentGameMatrix, currentShape, currentX, ghostY + 1)) {
            ghostY++;
        }

        // FIXED: Ensure ghost doesn't go below the last visible row (row 21 = display row 19)
        // The matrix has 22 rows (0-21), so the last valid row is 21
        // But we need to ensure no part of the shape goes beyond row 21
        // Find the bottommost block in the shape
        int shapeBottomRow = -1;
        for (int i = currentShape.length - 1; i >= 0; i--) {
            for (int j = 0; j < currentShape[i].length; j++) {
                if (currentShape[i][j] != 0) {
                    shapeBottomRow = i;
                    break;
                }
            }
            if (shapeBottomRow != -1) break;
        }
        if (shapeBottomRow == -1) shapeBottomRow = 0;
        
        // Ensure the bottommost block doesn't go beyond row 21
        int bottommostBlockRow = ghostY + shapeBottomRow;
        if (bottommostBlockRow >= currentGameMatrix.length) {
            // Adjust ghostY so bottommost block is at row 21
            ghostY = currentGameMatrix.length - 1 - shapeBottomRow;
        }

        return ghostY;
    }

    @Override
    public int getGhostXPosition() {
        return (int) currentOffset.getX();
    }

    @Override
    public int hardDrop() {
        int currentX = (int) currentOffset.getX();
        int currentY = (int) currentOffset.getY();
        int ghostY = getGhostYPosition();
        int dropDistance = ghostY - currentY;

        currentOffset.setLocation(currentX, ghostY);
        return dropDistance;
    }

    @Override
    public ClearRow clearRows() {
        ClearRow clearRow = MatrixOperations.checkRemoving(currentGameMatrix);
        currentGameMatrix = clearRow.getNewMatrix();
        return clearRow;
    }

    @Override
    public Score getScore() {
        return score;
    }

    @Override
    public void newGame() {
        currentGameMatrix = new int[height][width]; // 22x10
        score.reset();
        nextPieceQueue.clear();
        holdPiece = null;
        canHold = true;
        refillNextPieceQueue();
        createNewBrick();
    }
}