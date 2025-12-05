package com.comp2042.logic;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.comp2042.data.ClearRow;
import com.comp2042.data.NextShapeInfo;
import com.comp2042.data.ViewData;
import com.comp2042.domain.Brick;
import com.comp2042.domain.BrickGenerator;
import com.comp2042.domain.RandomBrickGenerator;

/**
 * Represents the game board logic for Tetris.
 * Manages the grid state, brick movement, collision detection, locking,
 * line clearing, and score tracking. It implements the {@link Board} interface
 * and serves as the core model for the game's state.
 */
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

    // Lock delay system
    private static final long LOCK_DELAY_MS = 500;
    private long lockDelayStartTime = -1;
    private boolean isInLockDelay = false;
    private boolean lastActionWasRotation = false;
    private boolean pendingTSpin = false;

    // Back-to-back tracking
    private boolean lastClearWasTetrisOrTSpin = false;

    /**
     * Constructs a new SimpleBoard with the specified dimensions.
     * Initializes the game matrix, brick generator, rotator, and score system.
     *
     * @param height The number of rows in the board (including buffer).
     * @param width  The number of columns in the board.
     */
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

    /**
     * Attempts to move the current brick down by one row.
     * Handles collision checks and manages the "lock delay" mechanism, allowing
     * the player a brief time to slide or rotate the piece before it locks
     * when it hits the bottom or another piece.
     *
     * @return true if the movement was successful, false if the piece is blocked.
     */
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

    /**
     * Attempts to move the current brick one column to the left.
     * Checks for collisions with the wall or other blocks.
     * If movement is successful, it resets the lock delay (if active).
     *
     * @return true if movement succeeded, false otherwise.
     */
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

    /**
     * Attempts to move the current brick one column to the right.
     * Checks for collisions with the wall or other blocks.
     * If movement is successful, it resets the lock delay (if active).
     *
     * @return true if movement succeeded, false otherwise.
     */
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

    /**
     * Rotates the current brick 90 degrees counter-clockwise.
     * Implements standard rotation rules and Wall Kick data (SRS-like) to
     * try shifting the piece if simple rotation is blocked by walls or blocks.
     *
     * @return true if rotation (or wall kick) was successful, false otherwise.
     */
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

            // Reset lock delay if rotating (Twist)
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

        // Spawn in hidden buffer zone (row 0)
        currentOffset = new Point(3, 0);
        canHold = true;
        isInLockDelay = false;
        lockDelayStartTime = -1;
        lastActionWasRotation = false;

        // Check collision for game over
        boolean collision = MatrixOperations.intersect(
                currentGameMatrix,
                brickRotator.getCurrentShape(),
                (int) currentOffset.getX(),
                (int) currentOffset.getY());

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

        while (ghostY + 1 < currentGameMatrix.length &&
                !MatrixOperations.intersect(currentGameMatrix, currentShape, currentX, ghostY + 1, true)) {
            ghostY++;
        }

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

        // Back-to-Back: only occurs when a "difficult clear" (Tetris or T-Spin)
        // is followed by another "difficult clear"
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