package com.comp2042;

public final class ClearRow {

    private final int linesRemoved;
    private final int[][] newMatrix;
    private final int scoreBonus;
    private final boolean isTSpin;
    private final boolean isBackToBack;

    public ClearRow(int linesRemoved, int[][] newMatrix, int scoreBonus) {
        this(linesRemoved, newMatrix, scoreBonus, false, false);
    }

    public ClearRow(int linesRemoved, int[][] newMatrix, int scoreBonus, boolean isTSpin, boolean isBackToBack) {
        this.linesRemoved = linesRemoved;
        this.newMatrix = newMatrix;
        this.scoreBonus = scoreBonus;
        this.isTSpin = isTSpin;
        this.isBackToBack = isBackToBack;
    }

    public int getLinesRemoved() {
        return linesRemoved;
    }

    public int[][] getNewMatrix() {
        return MatrixOperations.copy(newMatrix);
    }

    public int getScoreBonus() {
        return scoreBonus;
    }

    public boolean isTSpin() {
        return isTSpin;
    }

    public boolean isBackToBack() {
        return isBackToBack;
    }
}
