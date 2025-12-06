package com.comp2042.domain.bricks;

import com.comp2042.logic.MatrixOperations;
import com.comp2042.domain.Brick;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the Z-shaped Tetromino (inverse zigzag pattern).
 * Has two rotation states.
 */
public final class ZBrick implements Brick {

    /** List of rotation matrices for the Z-brick. */
    private final List<int[][]> brickMatrix = new ArrayList<>();

    /**
     * Constructs a Z-brick with its two rotation states.
     */
    public ZBrick() {
        brickMatrix.add(new int[][] {
                { 0, 0, 0, 0 },
                { 5, 5, 0, 0 },
                { 0, 5, 5, 0 },
                { 0, 0, 0, 0 }
        });
        brickMatrix.add(new int[][] {
                { 0, 5, 0, 0 },
                { 5, 5, 0, 0 },
                { 5, 0, 0, 0 },
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
