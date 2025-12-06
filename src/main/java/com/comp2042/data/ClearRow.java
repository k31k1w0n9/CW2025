package com.comp2042.data;

import com.comp2042.logic.MatrixOperations;

/**
 * Immutable data transfer object representing the result of a line clear
 * operation.
 * Contains information about rows removed, score bonus, and special clear
 * types.
 */
public final class ClearRow {

    private final int linesRemoved;
    private final int[][] newMatrix;
    private final int scoreBonus;
    private final boolean isTSpin;
    private final boolean isBackToBack;

    /**
     * Constructs a basic ClearRow without T-Spin or Back-to-Back information.
     *
     * @param linesRemoved the number of lines cleared
     * @param newMatrix    the updated game matrix after clearing
     * @param scoreBonus   the score bonus awarded for this clear
     */
    public ClearRow(int linesRemoved, int[][] newMatrix, int scoreBonus) {
        this(linesRemoved, newMatrix, scoreBonus, false, false);
    }

    /**
     * Constructs a ClearRow with full information including special clear types.
     *
     * @param linesRemoved the number of lines cleared
     * @param newMatrix    the updated game matrix after clearing
     * @param scoreBonus   the score bonus awarded for this clear
     * @param isTSpin      true if this was a T-Spin clear
     * @param isBackToBack true if this is a back-to-back difficult clear
     */
    public ClearRow(int linesRemoved, int[][] newMatrix, int scoreBonus,
            boolean isTSpin, boolean isBackToBack) {
        this.linesRemoved = linesRemoved;
        this.newMatrix = newMatrix;
        this.scoreBonus = scoreBonus;
        this.isTSpin = isTSpin;
        this.isBackToBack = isBackToBack;
    }

    /**
     * Returns the number of lines cleared.
     *
     * @return the number of lines removed
     */
    public int getLinesRemoved() {
        return linesRemoved;
    }

    /**
     * Returns a defensive copy of the updated game matrix.
     *
     * @return the new matrix after clearing rows
     */
    public int[][] getNewMatrix() {
        return MatrixOperations.copy(newMatrix);
    }

    /**
     * Returns the score bonus awarded for this clear.
     *
     * @return the score bonus
     */
    public int getScoreBonus() {
        return scoreBonus;
    }

    /**
     * Returns whether this was a T-Spin clear.
     *
     * @return true if this was a T-Spin, false otherwise
     */
    public boolean isTSpin() {
        return isTSpin;
    }

    /**
     * Returns whether this is a back-to-back difficult clear.
     *
     * @return true if this is a back-to-back clear, false otherwise
     */
    public boolean isBackToBack() {
        return isBackToBack;
    }
}
