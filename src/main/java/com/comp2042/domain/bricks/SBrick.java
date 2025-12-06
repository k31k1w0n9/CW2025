package com.comp2042.domain.bricks;

import com.comp2042.logic.MatrixOperations;
import com.comp2042.domain.Brick;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the S-shaped Tetromino (zigzag pattern).
 * Has two rotation states.
 */
public final class SBrick implements Brick {

    /** List of rotation matrices for the S-brick. */
    private final List<int[][]> brickMatrix = new ArrayList<>();

    /**
     * Constructs an S-brick with its two rotation states.
     */
    public SBrick() {
        brickMatrix.add(new int[][] {
                { 0, 0, 0, 0 },
                { 0, 4, 4, 0 },
                { 4, 4, 0, 0 },
                { 0, 0, 0, 0 }
        });
        brickMatrix.add(new int[][] {
                { 4, 0, 0, 0 },
                { 4, 4, 0, 0 },
                { 0, 4, 0, 0 },
                { 0, 0, 0, 0 }
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
