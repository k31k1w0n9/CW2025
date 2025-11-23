package com.comp2042;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

public class MatrixOperations {

    private MatrixOperations(){
        // Private constructor to prevent instantiation
    }

    /**
     * Check if brick intersects with existing blocks or boundaries
     * @param matrix The game board matrix (22x10)
     * @param brick The brick shape matrix
     * @param x Column position (0-9)
     * @param y Row position (0-21)
     * @return true if collision detected
     */
    public static boolean intersect(final int[][] matrix, final int[][] brick, int x, int y) {
        // FIXED: Check collision for all blocks in the brick shape
        // This prevents "piled-up" overlapping pieces
        for (int i = 0; i < brick.length; i++) {
            for (int j = 0; j < brick[i].length; j++) {
                if (brick[i][j] != 0) {
                    int targetX = x + j;
                    int targetY = y + i;
                    
                    // CRITICAL: Check bounds first - out of bounds is a collision
                    if (checkOutOfBound(matrix, targetX, targetY)) {
                        System.out.println("Collision detected at (" + targetX + ", " + targetY + ") - OUT OF BOUNDS");
                        return true;
                    }
                    
                    // Check if target cell is already filled
                    if (matrix[targetY][targetX] != 0) {
                        System.out.println("Collision detected at (" + targetX + ", " + targetY + ") - BLOCK EXISTS");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Check if position is out of bounds
     */
    private static boolean checkOutOfBound(int[][] matrix, int targetX, int targetY) {
        boolean returnValue = true;
        if (targetX >= 0 && targetY >= 0 && targetY < matrix.length && targetX < matrix[targetY].length) {
            returnValue = false;
        }
        return returnValue;
    }

    /**
     * Create a deep copy of a 2D array
     */
    public static int[][] copy(int[][] original) {
        int[][] myInt = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            int[] aMatrix = original[i];
            int aLength = aMatrix.length;
            myInt[i] = new int[aLength];
            System.arraycopy(aMatrix, 0, myInt[i], 0, aLength);
        }
        return myInt;
    }

    /**
     * Merge brick into the game board matrix
     * @param filledFields The game board matrix
     * @param brick The brick shape matrix
     * @param x Column position
     * @param y Row position (game coordinates 0-21)
     * @return Updated matrix with brick merged
     */
    public static int[][] merge(int[][] filledFields, int[][] brick, int x, int y) {
        System.out.println("\n=== MATRIX MERGE ===");
        System.out.println("Merging brick at GAME position: (" + x + ", " + y + ")");
        System.out.println("Brick size: " + brick.length + "x" + brick[0].length);

        int[][] copy = copy(filledFields);

        // FIXED: Only merge blocks that are within valid bounds
        // This prevents "piled-up" overlapping pieces and "chopped" bottom bricks
        for (int i = 0; i < brick.length; i++) {
            for (int j = 0; j < brick[i].length; j++) {
                int targetX = x + j;
                int targetY = y + i;

                // CRITICAL: Only merge if block is within valid matrix bounds
                // Matrix has 22 rows (0-21), so targetY must be 0-21
                // Matrix has 10 cols (0-9), so targetX must be 0-9
                if (brick[i][j] != 0 && 
                    targetX >= 0 && targetX < filledFields[0].length &&
                    targetY >= 0 && targetY < filledFields.length) {
                    System.out.println("  Block at brick[" + i + "][" + j + "] " +
                            "→ GAME matrix[" + targetY + "][" + targetX + "]" +
                            " (display row: " + (targetY - 2) + ")");
                    copy[targetY][targetX] = brick[i][j];
                } else if (brick[i][j] != 0) {
                    System.err.println("  WARNING: Block at brick[" + i + "][" + j + "] " +
                            "→ OUT OF BOUNDS (" + targetX + ", " + targetY + ") - NOT MERGED");
                }
            }
        }

        System.out.println("=== MERGE COMPLETE ===\n");
        return copy;
    }

    /**
     * Check for complete rows and remove them
     * @param matrix The game board matrix (22x10)
     * @return ClearRow object with cleared rows and new matrix
     */
    public static ClearRow checkRemoving(final int[][] matrix) {
        System.out.println("\n=== CHECK LINE CLEARING ===");
        System.out.println("Matrix size: " + matrix.length + " rows x " + matrix[0].length + " cols");

        int[][] tmp = new int[matrix.length][matrix[0].length];
        Deque<int[]> newRows = new ArrayDeque<>();
        List<Integer> clearedRows = new ArrayList<>();

        // Check each row
        for (int i = 0; i < matrix.length; i++) {
            int[] tmpRow = new int[matrix[i].length];
            boolean rowToClear = true;

            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] == 0) {
                    rowToClear = false;
                }
                tmpRow[j] = matrix[i][j];
            }

            if (rowToClear) {
                System.out.println("✓ GAME Row " + i + " (display row " + (i-2) + ") is FULL - will be cleared");
                clearedRows.add(i);
            } else {
                newRows.add(tmpRow);
            }
        }

        // Rebuild matrix without cleared rows
        for (int i = matrix.length - 1; i >= 0; i--) {
            int[] row = newRows.pollLast();
            if (row != null) {
                tmp[i] = row;
            } else {
                break;
            }
        }

        // GDD Section 8.0: Scoring System
        // Single: 100, Double: 300, Triple: 400, Tetris: 800
        // But code uses: 50 * n^2
        int scoreBonus = 50 * clearedRows.size() * clearedRows.size();

        System.out.println("Lines cleared: " + clearedRows.size());
        System.out.println("Score bonus: " + scoreBonus);
        System.out.println("=== LINE CLEARING COMPLETE ===\n");

        return new ClearRow(clearedRows.size(), tmp, scoreBonus);
    }

    /**
     * CRITICAL: Deep copy a list of 2D arrays
     * This method is used by Brick classes to get shape matrices
     * @param list List of shape matrices
     * @return Deep copied list
     */
    public static List<int[][]> deepCopyList(List<int[][]> list){
        return list.stream()
                .map(MatrixOperations::copy)
                .collect(Collectors.toList());
    }
}