package com.comp2042;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.RandomBrickGenerator;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleBoard implements Board {

    private final int width;
    private final int height;
    private final BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;
    private int[][] currentGameMatrix;
    private Point currentOffset;
    private final Score score;
    private final List<Brick> nextPieceQueue = new ArrayList<>();

    public SimpleBoard(int width, int height) {
        this.width = width;   // 20 (rows)
        this.height = height; // 10 (cols)
        currentGameMatrix = new int[width][height]; // [20][10]
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

        currentOffset = new Point(3, 0);
        return MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
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
        currentGameMatrix = MatrixOperations.merge(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    @Override
    public int getGhostYPosition() {
        int ghostY = (int) currentOffset.getY();
        int currentX = (int) currentOffset.getX();
        int[][] currentShape = brickRotator.getCurrentShape();

        while (!MatrixOperations.intersect(currentGameMatrix, currentShape, currentX, ghostY + 1)) {
            ghostY++;
        }

        return ghostY;
    }

    @Override
    public int getGhostXPosition() {
        return (int) currentOffset.getX();
    }

    @Override
    public int hardDrop() {
        int dropDistance = 0;
        int currentX = (int) currentOffset.getX();
        int currentY = (int) currentOffset.getY();
        int[][] currentShape = brickRotator.getCurrentShape();

        // Calculate how far the piece will drop
        int ghostY = getGhostYPosition();
        dropDistance = ghostY - currentY;

        // Move the piece to the ghost position
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
        currentGameMatrix = new int[width][height];
        score.reset();
        nextPieceQueue.clear();
        refillNextPieceQueue();
        createNewBrick();
    }
}