package com.comp2042.domain.bricks;

import com.comp2042.logic.MatrixOperations;
import com.comp2042.domain.Brick;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the I-shaped Tetromino (4 blocks in a straight line).
 * This is the only piece that can clear 4 lines at once (Tetris).
 */
public final class IBrick implements Brick {

    /** List of rotation matrices for the I-brick. */
    private final List<int[][]> brickMatrix = new ArrayList<>();

    /**
     * Constructs an I-brick with its two rotation states.
     * The I-brick alternates between horizontal and vertical orientations.
     */
    public IBrick() {
        brickMatrix.add(new int[][] {
                { 0, 0, 0, 0 },
                { 1, 1, 1, 1 },
                { 0, 0, 0, 0 },
                { 0, 0, 0, 0 }
        });
        brickMatrix.add(new int[][] {
                { 0, 1, 0, 0 },
                { 0, 1, 0, 0 },
                { 0, 1, 0, 0 },
                { 0, 1, 0, 0 }
        });
    }

    /**
     * Returns a deep copy of the shape matrices.
     *
     * @return list of rotation matrices for this brick
     */
    @Override
    public List<int[][]> getShapeMatrix() {
        return MatrixOperations.deepCopyList(brickMatrix);
    }
}
