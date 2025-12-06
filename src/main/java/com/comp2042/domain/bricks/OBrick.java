package com.comp2042.domain.bricks;

import com.comp2042.logic.MatrixOperations;
import com.comp2042.domain.Brick;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the O-shaped Tetromino (2x2 square).
 * Has only one rotation state as it is rotationally symmetric.
 */
public final class OBrick implements Brick {

    /** List of rotation matrices for the O-brick. */
    private final List<int[][]> brickMatrix = new ArrayList<>();

    /**
     * Constructs an O-brick with its single rotation state.
     */
    public OBrick() {
        brickMatrix.add(new int[][] {
                { 0, 0, 0, 0 },
                { 0, 2, 2, 0 },
                { 0, 2, 2, 0 },
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
