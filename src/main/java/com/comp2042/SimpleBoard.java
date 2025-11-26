package com.comp2042;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.RandomBrickGenerator;

public class SimpleBoard implements Board {

    private final int width; // 10 columns
    private final int height; // 22 rows total (2 hidden + 20 visible)
    private final BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;
    private int[][] currentGameMatrix;
    private Point currentOffset;
    private final Score score;
    private final List<Brick> nextPieceQueue = new ArrayList<>();

    private Brick holdPiece = null;
    private boolean canHold = true;

    // Lock delay system (GDD 4.1.2.1)
    private static final long LOCK_DELAY_MS = 500; // 500ms lock delay
    private long lockDelayStartTime = -1;
    private boolean isInLockDelay = false;
    private boolean lastActionWasRotation = false; // Track if last action was rotation for T-Spin detection
    private boolean pendingTSpin = false; // Store T-Spin state before merging

    // Back-to-back tracking (GDD 6.1)
    private boolean lastClearWasTetrisOrTSpin = false;

    public SimpleBoard(int height, int width) {
        this.width = width; // 10
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
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(),
                (int) p.getY());
        if (conflict) {
            // Piece can't move down - start lock delay
            if (!isInLockDelay) {
                isInLockDelay = true;
                lockDelayStartTime = System.currentTimeMillis();
            } else {
                // Check if lock delay has expired
                long elapsed = System.currentTimeMillis() - lockDelayStartTime;
                if (elapsed >= LOCK_DELAY_MS) {
                    // Lock delay expired - piece should lock
                    return false;
                }
            }
            return false;
        } else {
            // Piece can move down - reset lock delay
            isInLockDelay = false;
            lockDelayStartTime = -1;
            lastActionWasRotation = false;
            currentOffset = p;
            return true;
        }
    }

    @Override
    public boolean moveBrickLeft() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(-1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(),
                (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            lastActionWasRotation = false;
            // Reset lock delay if piece can move
            if (isInLockDelay) {
                isInLockDelay = false;
                lockDelayStartTime = -1;
            }
            return true;
        }
    }

    @Override
    public boolean moveBrickRight() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(),
                (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            lastActionWasRotation = false;
            // Reset lock delay if piece can move
            if (isInLockDelay) {
                isInLockDelay = false;
                lockDelayStartTime = -1;
            }
            return true;
        }
    }

    @Override
    public boolean rotateLeftBrick() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        NextShapeInfo nextShape = brickRotator.getNextShape();
        int currentX = (int) currentOffset.getX();
        int currentY = (int) currentOffset.getY();

        // Try rotation at current position
        boolean conflict = MatrixOperations.intersect(currentMatrix, nextShape.getShape(), currentX, currentY);

        if (!conflict) {
            // Rotation successful - apply it
            brickRotator.setCurrentShape(nextShape.getPosition());
            lastActionWasRotation = true;

            // GDD 4.1.2.1: Twist - reset lock delay if in lock delay
            if (isInLockDelay) {
                isInLockDelay = false;
                lockDelayStartTime = -1;
            }
            return true;
        }

        // Rotation failed - try wall kick (GDD 4.1.2.2)
        // Try shifting left first, then right
        int[] wallKickOffsets = { -1, 1, -2, 2 };
        for (int offset : wallKickOffsets) {
            int testX = currentX + offset;
            if (!MatrixOperations.intersect(currentMatrix, nextShape.getShape(), testX, currentY)) {
                // Wall kick successful
                currentOffset.setLocation(testX, currentY);
                brickRotator.setCurrentShape(nextShape.getPosition());
                lastActionWasRotation = true;

                // Reset lock delay if in lock delay (twist)
                if (isInLockDelay) {
                    isInLockDelay = false;
                    lockDelayStartTime = -1;
                }
                return true;
            }
        }

        // Wall kick failed - rotation not possible
        return false;
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
        currentOffset = new Point(3, 0); // Column 3 (center), Row 0 (buffer)
        canHold = true;
        isInLockDelay = false;
        lockDelayStartTime = -1;
        lastActionWasRotation = false;

        System.out.println("=== NEW BRICK SPAWNED ===");
        System.out.println("Brick type: " + currentBrick.getClass().getSimpleName());
        System.out.println("Spawn position: (" + currentOffset.x + ", " + currentOffset.y + ")");
        System.out.println("Game matrix position: Row " + currentOffset.y + " (buffer zone)");

        // Check collision for game over
        boolean collision = MatrixOperations.intersect(
                currentGameMatrix,
                brickRotator.getCurrentShape(),
                (int) currentOffset.getX(),
                (int) currentOffset.getY());

        if (collision) {
            System.out.println("[GAME OVER] COLLISION AT SPAWN");
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
                getGhostYPosition());
    }

    @Override
    public void mergeBrickToBackground() {
        // Check for T-Spin BEFORE merging (piece still exists)
        pendingTSpin = isTSpinPosition();

        currentGameMatrix = MatrixOperations.merge(
                currentGameMatrix,
                brickRotator.getCurrentShape(),
                (int) currentOffset.getX(),
                (int) currentOffset.getY());
    }

    @Override
    public int getGhostYPosition() {
        int ghostY = (int) currentOffset.getY();
        int currentX = (int) currentOffset.getX();
        int[][] currentShape = brickRotator.getCurrentShape();

        // FIXED: Move ghost down until it would collide
        // The loop checks ghostY+1, so when it finds a collision, ghostY is the last
        // valid position
        // CRITICAL: Stop before going out of bounds (row 22 is out of bounds for 22-row
        // matrix)
        while (ghostY + 1 < currentGameMatrix.length &&
                !MatrixOperations.intersect(currentGameMatrix, currentShape, currentX, ghostY + 1)) {
            ghostY++;
        }

        // FIXED: Ensure ghost doesn't go below the last visible row (row 21 = display
        // row 19)
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
            if (shapeBottomRow != -1)
                break;
        }
        if (shapeBottomRow == -1)
            shapeBottomRow = 0;

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

    /**
     * Check if current T piece is in a T-Spin position
     * T-Spin: T piece rotated with 3 of 4 corners filled
     */
    private boolean isTSpinPosition() {
        // Only check for T pieces
        if (!brickRotator.getBrick().getClass().getSimpleName().equals("TBrick")) {
            return false;
        }

        // Must have just rotated
        if (!lastActionWasRotation) {
            return false;
        }

        int[][] shape = brickRotator.getCurrentShape();
        int x = (int) currentOffset.getX();
        int y = (int) currentOffset.getY();

        // Find the center of the T piece (the middle block)
        int centerX = -1, centerY = -1;
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) {
                    // Check if this is the center (has blocks on 3 sides)
                    int neighbors = 0;
                    if (i > 0 && shape[i - 1][j] != 0)
                        neighbors++;
                    if (i < shape.length - 1 && shape[i + 1][j] != 0)
                        neighbors++;
                    if (j > 0 && shape[i][j - 1] != 0)
                        neighbors++;
                    if (j < shape[i].length - 1 && shape[i][j + 1] != 0)
                        neighbors++;

                    if (neighbors >= 3) {
                        centerX = x + j;
                        centerY = y + i;
                        break;
                    }
                }
            }
            if (centerX != -1)
                break;
        }

        if (centerX == -1)
            return false;

        // Check the 4 corners around the T center
        int filledCorners = 0;
        int[][] corners = {
                { centerX - 1, centerY - 1 }, // Top-left
                { centerX + 1, centerY - 1 }, // Top-right
                { centerX - 1, centerY + 1 }, // Bottom-left
                { centerX + 1, centerY + 1 } // Bottom-right
        };

        for (int[] corner : corners) {
            int cx = corner[0];
            int cy = corner[1];

            // Check if corner is out of bounds or filled
            if (cy < 0 || cy >= currentGameMatrix.length ||
                    cx < 0 || cx >= currentGameMatrix[0].length) {
                filledCorners++;
            } else if (currentGameMatrix[cy][cx] != 0) {
                filledCorners++;
            }
        }

        // T-Spin requires at least 3 corners filled
        return filledCorners >= 3;
    }

    @Override
    public ClearRow clearRows() {
        // Use stored T-Spin state (checked before merging)
        boolean isTSpin = pendingTSpin;

        // Count how many rows will be cleared (before actually clearing)
        int linesCleared = 0;
        for (int i = 0; i < currentGameMatrix.length; i++) {
            boolean rowToClear = true;
            for (int j = 0; j < currentGameMatrix[0].length; j++) {
                if (currentGameMatrix[i][j] == 0) {
                    rowToClear = false;
                    break;
                }
            }
            if (rowToClear) {
                linesCleared++;
            }
        }

        // GDD 6.2: Back-to-Back only occurs when a "difficult clear" (Tetris or T-Spin)
        // is followed by another "difficult clear" (Tetris or T-Spin)
        boolean isCurrentDifficultClear = false;
        if (isTSpin && linesCleared > 0) {
            // T-Spin with lines cleared is a difficult clear
            isCurrentDifficultClear = true;
        } else if (linesCleared == 4) {
            // Tetris (4 lines) is a difficult clear
            isCurrentDifficultClear = true;
        }

        // Back-to-back: previous was difficult AND current is difficult
        boolean isBackToBack = lastClearWasTetrisOrTSpin && isCurrentDifficultClear;

        ClearRow clearRow = MatrixOperations.checkRemoving(currentGameMatrix, isTSpin, isBackToBack);
        currentGameMatrix = clearRow.getNewMatrix();

        // Update back-to-back tracking for NEXT clear
        // Only set to true if current clear was a difficult clear
        lastClearWasTetrisOrTSpin = isCurrentDifficultClear;

        // Reset rotation tracking and T-Spin state
        lastActionWasRotation = false;
        pendingTSpin = false;

        return clearRow;
    }

    /**
     * Check if piece should lock (lock delay expired)
     */
    public boolean shouldLock() {
        if (!isInLockDelay) {
            return false;
        }
        long elapsed = System.currentTimeMillis() - lockDelayStartTime;
        return elapsed >= LOCK_DELAY_MS;
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
        isInLockDelay = false;
        lockDelayStartTime = -1;
        lastActionWasRotation = false;
        lastClearWasTetrisOrTSpin = false;

        // Reset generator to clear old bag and apply new settings immediately
        brickGenerator.reset();

        refillNextPieceQueue();
        createNewBrick();
    }
}