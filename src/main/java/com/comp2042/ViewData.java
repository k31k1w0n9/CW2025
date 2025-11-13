package com.comp2042;

import java.util.List;
import java.util.stream.Collectors;

public final class ViewData {

    private final int[][] brickData;
    private final int xPosition;
    private final int yPosition;
    private final List<int[][]> nextBrickData;
    private final int ghostXPosition;
    private final int ghostYPosition;

    public ViewData(int[][] brickData, int xPosition, int yPosition, List<int[][]> nextBrickData, int ghostXPosition, int ghostYPosition) {
        this.brickData = brickData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.nextBrickData = nextBrickData;
        this.ghostXPosition = ghostXPosition;
        this.ghostYPosition = ghostYPosition;
    }

    public int[][] getBrickData() {
        return MatrixOperations.copy(brickData);
    }

    public int getxPosition() {
        return xPosition;
    }

    public int getyPosition() {
        return yPosition;
    }

    public List<int[][]> getNextBrickData() {
        return nextBrickData.stream()
                .map(MatrixOperations::copy)
                .collect(Collectors.toList());
    }

    public int getGhostXPosition() {
        return ghostXPosition;
    }

    public int getGhostYPosition() {
        return ghostYPosition;
    }
}