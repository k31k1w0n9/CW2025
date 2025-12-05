package com.comp2042.core;

import com.comp2042.events.EventSource;
import com.comp2042.events.MoveEvent;
import com.comp2042.data.ClearRow;
import com.comp2042.data.DownData;
import com.comp2042.data.ViewData;
import com.comp2042.logic.Board;
import com.comp2042.logic.SimpleBoard;

/**
 * Controller class that manages the game logic and acts as an intermediary
 * between the game board model and the GUI controller.
 * Can be considered the "Controller" in the MVC pattern.
 * Implements {@link InputEventListener} to handle all player input events
 * including
 * movement, rotation, and drops.
 */
public class GameController implements InputEventListener {

    /**
     * The game board model. Uses lazy initialisation - created when game starts.
     */
    private Board board;

    /** Reference to the GUI controller for updating the view. */
    private final GuiController viewGuiController;

    /**
     * Constructs a new GameController and registers it as the event listener for
     * the GUI.
     *
     * @param c the GUI controller to communicate with
     */
    public GameController(GuiController c) {
        viewGuiController = c;
        viewGuiController.setEventListener(this);
    }

    /**
     * Updates the next piece preview display in the GUI.
     */
    private void refreshNextPiece() {
        if (board != null) {
            viewGuiController.updateNextPieces(board.getViewData().getNextBrickData());
        }
    }

    /**
     * Handles a soft drop (down) event. Moves the piece down and handles
     * locking, line clearing, and game over detection when the piece cannot move
     * further.
     *
     * @param event the move event containing type and source information
     * @return DownData containing the result of the move and any cleared rows
     */
    @Override
    public DownData onDownEvent(MoveEvent event) {
        if (board == null)
            return new DownData(null, null);

        boolean canMove = board.moveBrickDown();
        ClearRow clearRow = null;

        if (!canMove) {
            if (board.shouldLock()) {
                board.mergeBrickToBackground();
                clearRow = board.clearRows();
                if (clearRow.getLinesRemoved() > 0) {
                    board.getScore().add(clearRow.getScoreBonus());
                }

                boolean gameOver = board.createNewBrick();
                if (gameOver) {
                    viewGuiController.gameOver();
                } else {
                    refreshNextPiece();
                }

                viewGuiController.refreshGameBackground(board.getBoardMatrix());
            }
        } else {
            if (event.getEventSource() == EventSource.USER) {
                board.getScore().add(1);
            }
        }
        return new DownData(clearRow, board.getViewData());
    }

    /**
     * Handles a hard drop event. Instantly drops the piece to the bottom,
     * awards points, and processes line clears.
     *
     * @param event the move event containing type and source information
     * @return DownData containing the result of the drop and any cleared rows
     */
    @Override
    public DownData onHardDropEvent(MoveEvent event) {
        if (board == null)
            return new DownData(null, null);

        int dropDistance = board.hardDrop();
        board.getScore().add(dropDistance * 2);

        board.mergeBrickToBackground();
        ClearRow clearRow = board.clearRows();

        if (clearRow.getLinesRemoved() > 0) {
            board.getScore().add(clearRow.getScoreBonus());
        }

        boolean gameOver = board.createNewBrick();
        if (gameOver) {
            viewGuiController.gameOver();
        } else {
            refreshNextPiece();
        }

        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        return new DownData(clearRow, board.getViewData());
    }

    /**
     * Handles a left movement event.
     *
     * @param event the move event
     * @return ViewData representing the updated game state
     */
    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        if (board == null)
            return null;
        board.moveBrickLeft();
        return board.getViewData();
    }

    /**
     * Handles a right movement event.
     *
     * @param event the move event
     * @return ViewData representing the updated game state
     */
    /**
     * Handles a right movement event.
     * Moves the piece closer to the right wall.
     *
     * @param event the move event
     * @return ViewData representing the updated game state for UI rendering
     */
    @Override
    public ViewData onRightEvent(MoveEvent event) {
        if (board == null)
            return null;
        board.moveBrickRight();
        return board.getViewData();
    }

    /**
     * Handles a rotation event.
     *
     * @param event the move event
     * @return ViewData representing the updated game state
     */
    /**
     * Handles a rotation event.
     * Rotates the current piece 90 degrees counter-clockwise.
     * If the rotation is blocked, it may attempt a wall kick.
     *
     * @param event the move event
     * @return ViewData representing the updated game state including the new
     *         rotation
     */
    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        if (board == null)
            return null;
        board.rotateLeftBrick();
        return board.getViewData();
    }

    /**
     * Handles a hold piece event. Swaps the current piece with the held piece.
     *
     * @param event the move event
     * @return ViewData representing the updated game state
     */
    @Override
    public ViewData onHoldEvent(MoveEvent event) {
        if (board == null)
            return null;
        if (board.holdPiece()) {
            viewGuiController.updateHoldPiece(board.getHoldPieceShape());
            refreshNextPiece();
        }
        return board.getViewData();
    }

    /**
     * Creates a new game instance.
     * Can be called to start the first game or restart an existing one.
     * Initializes the Board model, binds UI properties like score,
     * and triggers the initial view refresh.
     */
    @Override
    public void createNewGame() {
        if (board == null) {
            board = new SimpleBoard(22, 10);
            viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
            viewGuiController.bindScore(board.getScore().scoreProperty());
        } else {
            board.newGame();
        }

        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        viewGuiController.updateHoldPiece(null);
        refreshNextPiece();
        viewGuiController.refreshBrick(board.getViewData());
    }
}