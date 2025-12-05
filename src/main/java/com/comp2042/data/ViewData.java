package com.comp2042.data;

import com.comp2042.logic.MatrixOperations;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Immutable data transfer object containing the current view state of the game.
 * Holds information about the current brick, its position, ghost piece
 * position,
 * and the queue of upcoming pieces to be displayed in the UI.
 */
public final class ViewData {

    private final int[][] brickData;
    private final int xPosition;
    private final int yPosition;
    private final List<int[][]> nextBrickData;
    private final int ghostXPosition;
    private final int ghostYPosition;

    /**
     * Constructs a new ViewData with all required display information.
     *
     * @param brickData      the shape matrix of the current falling piece
     * @param xPosition      the X position (column) of the current piece
     * @param yPosition      the Y position (row) of the current piece
     * @param nextBrickData  list of shape matrices for upcoming pieces
     * @param ghostXPosition the X position of the ghost piece
     * @param ghostYPosition the Y position of the ghost piece
     */
    public ViewData(int[][] brickData, int xPosition, int yPosition,
            List<int[][]> nextBrickData, int ghostXPosition, int ghostYPosition) {
        this.brickData = brickData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.nextBrickData = nextBrickData;
        this.ghostXPosition = ghostXPosition;
        this.ghostYPosition = ghostYPosition;
    }

    /**
     * Returns a copy of the current brick's shape matrix.
     *
     * @return a defensive copy of the brick data
     */
    public int[][] getBrickData() {
        return MatrixOperations.copy(brickData);
    }

    /**
     * Returns the X position (column) of the current piece.
     *
     * @return the X position
     */
    public int getxPosition() {
        return xPosition;
    }

    /**
     * Returns the Y position (row) of the current piece.
     *
     * @return the Y position
     */
    public int getyPosition() {
        return yPosition;
    }

    /**
     * Returns a defensive copy of the list of upcoming brick shapes.
     *
     * @return list of shape matrices for the next pieces
     */
    public List<int[][]> getNextBrickData() {
        return nextBrickData.stream()
                .map(MatrixOperations::copy)
                .collect(Collectors.toList());
    }

    /**
     * Returns the X position of the ghost piece.
     *
     * @return the ghost piece X position
     */
    public int getGhostXPosition() {
        return ghostXPosition;
    }

    /**
     * Returns the Y position of the ghost piece.
     *
     * @return the ghost piece Y position
     */
    public int getGhostYPosition() {
        return ghostYPosition;
    }
}