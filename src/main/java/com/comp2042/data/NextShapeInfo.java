package com.comp2042.data;

import com.comp2042.logic.MatrixOperations;

/**
 * Immutable data transfer object containing information about the next rotation
 * state.
 * Used by the BrickRotator to preview and apply piece rotations.
 */
public final class NextShapeInfo {

    private final int[][] shape;
    private final int position;

    /**
     * Constructs a new NextShapeInfo with the specified shape and rotation
     * position.
     *
     * @param shape    the shape matrix for the next rotation state
     * @param position the rotation index position
     */
    public NextShapeInfo(final int[][] shape, final int position) {
        this.shape = shape;
        this.position = position;
    }

    /**
     * Returns a defensive copy of the shape matrix.
     *
     * @return the shape matrix for this rotation state
     */
    public int[][] getShape() {
        return MatrixOperations.copy(shape);
    }

    /**
     * Returns the rotation position index.
     *
     * @return the rotation position
     */
    public int getPosition() {
        return position;
    }
}
