package com.comp2042.domain;

import java.util.List;

/**
 * Core domain interface representing a Tetromino brick in the game.
 * Located in the domain package to define the contract for all brick shapes.
 * Each brick defines its shape through a list of rotation matrices.
 */
public interface Brick {

    /**
     * Returns the list of shape matrices for all rotation states of this brick.
     * Each matrix represents a different rotation (0°, 90°, 180°, 270°).
     *
     * @return a list of 2D integer arrays representing each rotation state
     */
    List<int[][]> getShapeMatrix();
}