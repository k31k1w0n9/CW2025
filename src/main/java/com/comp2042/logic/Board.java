package com.comp2042.logic;

import com.comp2042.data.ClearRow;
import com.comp2042.data.ViewData;

/**
 * Interface defining the contract for a Tetris game board.
 * Provides methods for piece movement, rotation, and game state management.
 */
public interface Board {

    /**
     * Attempts to move the current piece down by one row.
     *
     * @return true if the piece was moved successfully, false if blocked
     */
    boolean moveBrickDown();

    /**
     * Attempts to move the current piece left by one column.
     *
     * @return true if the piece was moved successfully, false if blocked
     */
    boolean moveBrickLeft();

    /**
     * Attempts to move the current piece right by one column.
     *
     * @return true if the piece was moved successfully, false if blocked
     */
    boolean moveBrickRight();

    /**
     * Attempts to rotate the current piece clockwise.
     *
     * @return true if the rotation was successful, false if blocked
     */
    boolean rotateLeftBrick();

    /**
     * Creates a new brick at the spawn position.
     *
     * @return true if the spawn position is blocked (game over), false otherwise
     */
    boolean createNewBrick();

    /**
     * Returns the current state of the game board matrix.
     *
     * @return a 2D array representing the locked pieces on the board
     */
    int[][] getBoardMatrix();

    /**
     * Returns the Y position where the ghost piece would land.
     *
     * @return the Y coordinate of the ghost piece
     */
    int getGhostYPosition();

    /**
     * Returns the X position of the ghost piece.
     *
     * @return the X coordinate of the ghost piece
     */
    int getGhostXPosition();

    /**
     * Returns the current view data for rendering.
     *
     * @return ViewData containing all display information
     */
    ViewData getViewData();

    /**
     * Merges the current falling piece into the background matrix.
     */
    void mergeBrickToBackground();

    /**
     * Clears any completed rows and calculates the score bonus.
     *
     * @return ClearRow containing the result of the clear operation
     */
    ClearRow clearRows();

    /**
     * Returns the score tracker for this board.
     *
     * @return the Score object
     */
    Score getScore();

    /**
     * Resets the board for a new game.
     */
    void newGame();

    /**
     * Performs a hard drop, instantly moving the piece to the bottom.
     *
     * @return the number of rows the piece was dropped
     */
    int hardDrop();

    /**
     * Attempts to hold the current piece.
     *
     * @return true if the hold was successful, false if already used this turn
     */
    boolean holdPiece();

    /**
     * Returns the shape matrix of the currently held piece.
     *
     * @return the held piece shape, or null if no piece is held
     */
    int[][] getHoldPieceShape();

    /**
     * Checks if the piece should be locked after the lock delay.
     *
     * @return true if the lock delay has expired and piece should lock
     */
    boolean shouldLock();
}