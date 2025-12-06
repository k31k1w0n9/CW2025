package com.comp2042.domain.bricks;

import java.util.ArrayList;
import com.comp2042.domain.Brick;
import java.util.List;

/**
 * Represents a custom user-defined Tetromino brick.
 * Allows players to create their own brick shapes with automatic rotation
 * generation.
 */
public class CustomBrick implements Brick {

    /** List of rotation matrices for this custom brick. */
    private final List<int[][]> brickMatrix = new ArrayList<>();

    /**
     * Constructs a CustomBrick from a boolean design grid.
     * Automatically generates all four rotation states (0°, 90°, 180°, 270°).
     *
     * @param design a 2D boolean array where true represents a filled cell
     * @param id     the unique identifier for this brick's cells in the game matrix
     */
    public CustomBrick(boolean[][] design, int id) {
        int rows = design.length;
        int cols = design[0].length;
        int[][] baseShape = new int[rows][cols];

        boolean isEmpty = true;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (design[i][j]) {
                    baseShape[i][j] = id;
                    isEmpty = false;
                } else {
                    baseShape[i][j] = 0;
                }
            }
        }

        if (isEmpty) {
            baseShape[1][1] = id;
        }

        brickMatrix.add(baseShape);

        int[][] current = baseShape;
        for (int i = 0; i < 3; i++) {
            current = rotateMatrix(current);
            brickMatrix.add(current);
        }
    }

    /**
     * Rotates a matrix 90 degrees clockwise.
     *
     * @param matrix the matrix to rotate
     * @return the rotated matrix
     */
    private int[][] rotateMatrix(int[][] matrix) {
        int size = matrix.length;
        int[][] rotated = new int[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                rotated[j][size - 1 - i] = matrix[i][j];
            }
        }
        return rotated;
    }

    /**
     * Returns the list of shape matrices for all rotation states.
     *
     * @return the list of rotation matrices
     */
    @Override
    public List<int[][]> getShapeMatrix() {
        return brickMatrix;
    }
}
