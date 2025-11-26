package com.comp2042;

public class GameController implements InputEventListener {

    private Board board = new SimpleBoard(22, 10);

    private final GuiController viewGuiController;

    public GameController(GuiController c) {
        viewGuiController = c;
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());
        viewGuiController.updateHoldPiece(null);
        refreshNextPiece(); // Initialize the next piece display
    }

    private void refreshNextPiece() {
        viewGuiController.updateNextPieces(board.getViewData().getNextBrickData());
    }

    @Override
    public DownData onDownEvent(MoveEvent event) {
        boolean canMove = board.moveBrickDown();
        ClearRow clearRow = null;

        if (!canMove) {
            // Check if lock delay has expired
            // Note: Per Tetris Guideline, Soft Drop does NOT lock the piece. Only Hard Drop
            // locks immediately.
            if (board.shouldLock()) {
                board.mergeBrickToBackground();
                clearRow = board.clearRows();
                if (clearRow.getLinesRemoved() > 0) {
                    board.getScore().add(clearRow.getScoreBonus());
                }

                // Check if new brick creation causes game over
                boolean gameOver = board.createNewBrick();
                if (gameOver) {
                    viewGuiController.gameOver();
                } else {
                    refreshNextPiece();
                }

                viewGuiController.refreshGameBackground(board.getBoardMatrix());
            }
            // If !shouldLock(), we do nothing (wait for next tick or user input)
            // The piece stays in the same position (in lock delay)

        } else {
            if (event.getEventSource() == EventSource.USER) {
                board.getScore().add(1);
            }
        }
        return new DownData(clearRow, board.getViewData());
    }

    @Override
    public DownData onHardDropEvent(MoveEvent event) {
        int dropDistance = board.hardDrop();

        // Award points for hard drop (2 points per cell dropped)
        board.getScore().add(dropDistance * 2);

        // Merge the brick immediately
        board.mergeBrickToBackground();
        ClearRow clearRow = board.clearRows();

        if (clearRow.getLinesRemoved() > 0) {
            board.getScore().add(clearRow.getScoreBonus());
        }

        // Check if new brick creation causes game over
        boolean gameOver = board.createNewBrick();
        if (gameOver) {
            viewGuiController.gameOver();
        } else {
            refreshNextPiece();
        }

        viewGuiController.refreshGameBackground(board.getBoardMatrix());

        return new DownData(clearRow, board.getViewData());
    }

    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        board.moveBrickLeft();
        return board.getViewData();
    }

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        board.moveBrickRight();
        return board.getViewData();
    }

    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        board.rotateLeftBrick();
        return board.getViewData();
    }

    @Override
    public ViewData onHoldEvent(MoveEvent event) {
        if (board.holdPiece()) {
            viewGuiController.updateHoldPiece(board.getHoldPieceShape());
            refreshNextPiece();
        }
        return board.getViewData();
    }

    @Override
    public void createNewGame() {
        board.newGame();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        viewGuiController.updateHoldPiece(null);
        refreshNextPiece();
        // Refresh the falling brick display so it's visible in the new game
        viewGuiController.refreshBrick(board.getViewData());
    }
}