package com.comp2042.logic;

import com.comp2042.data.NextShapeInfo;
import com.comp2042.domain.Brick;

/**
 * Manages the rotation states of a Tetromino brick.
 * Handles cycling through the different rotation positions and provides
 * methods to preview and apply rotations.
 */
public class BrickRotator {

    /** The current brick being rotated. */
    private Brick brick;

    /**
     * The current rotation index (0 to n-1 where n is the number of rotation
     * states).
     */
    private int currentShape = 0;

    /**
     * Returns information about the next rotation state without applying it.
     *
     * @return NextShapeInfo containing the shape and position of the next rotation
     */
    public NextShapeInfo getNextShape() {
        int nextShape = currentShape;
        nextShape = (++nextShape) % brick.getShapeMatrix().size();
        return new NextShapeInfo(brick.getShapeMatrix().get(nextShape), nextShape);
    }

    /**
     * Returns the shape matrix for the current rotation state.
     *
     * @return the current shape as a 2D array
     */
    public int[][] getCurrentShape() {
        return brick.getShapeMatrix().get(currentShape);
    }

    /**
     * Sets the current rotation position.
     *
     * @param currentShape the rotation index to set
     */
    public void setCurrentShape(int currentShape) {
        this.currentShape = currentShape;
    }

    /**
     * Sets the brick to be rotated and resets rotation to the initial state.
     *
     * @param brick the Brick to manage rotations for
     */
    public void setBrick(Brick brick) {
        this.brick = brick;
        currentShape = 0;
    }

    /**
     * Returns the currently managed brick.
     *
     * @return the current Brick
     */
    public Brick getBrick() {
        return brick;
    }
}