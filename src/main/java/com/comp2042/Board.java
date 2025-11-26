package com.comp2042;

public interface Board {

    boolean moveBrickDown();

    boolean moveBrickLeft();

    boolean moveBrickRight();

    boolean rotateLeftBrick();

    boolean createNewBrick();

    int[][] getBoardMatrix();

    int getGhostYPosition();

    int getGhostXPosition();

    ViewData getViewData();

    void mergeBrickToBackground();

    ClearRow clearRows();

    Score getScore();

    void newGame();

    int hardDrop();

    boolean holdPiece();

    int[][] getHoldPieceShape();

    boolean shouldLock();
}