package com.comp2042.logic.bricks;

import java.util.ArrayList;
import java.util.List;

public class CustomBrick implements Brick {

    private final List<int[][]> brickMatrix = new ArrayList<>();

    public CustomBrick(boolean[][] design, int id) {
        // Convert boolean design to int matrix with provided ID
        int rows = design.length;
        int cols = design[0].length;
        int[][] baseShape = new int[rows][cols];

        boolean isEmpty = true;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (design[i][j]) {
                    baseShape[i][j] = id; // Use specific ID for custom pieces
                    isEmpty = false;
                } else {
                    baseShape[i][j] = 0;
                }
            }
        }

        // If empty, create a single block to avoid errors
        if (isEmpty) {
            baseShape[1][1] = id;
        }

        brickMatrix.add(baseShape);

        // Generate 3 rotations (90, 180, 270)
        int[][] current = baseShape;
        for (int i = 0; i < 3; i++) {
            current = rotateMatrix(current);
            brickMatrix.add(current);
        }
    }

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

    @Override
    public List<int[][]> getShapeMatrix() {
        return brickMatrix;
    }
}
