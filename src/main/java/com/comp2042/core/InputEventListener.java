package com.comp2042.core;

import com.comp2042.events.MoveEvent;
import com.comp2042.data.DownData;
import com.comp2042.data.ViewData;

/**
 * Interface defining the contract for handling game input events.
 * Implementations of this interface respond to user and system-generated
 * movement events and manage the game state accordingly.
 */
public interface InputEventListener {

    /**
     * Handles a downward movement event (soft drop).
     *
     * @param event the move event containing type and source information
     * @return DownData containing the result of the move and any cleared rows
     */
    DownData onDownEvent(MoveEvent event);

    /**
     * Handles a left movement event.
     *
     * @param event the move event containing type and source information
     * @return ViewData representing the updated game state
     */
    ViewData onLeftEvent(MoveEvent event);

    /**
     * Handles a right movement event.
     *
     * @param event the move event containing type and source information
     * @return ViewData representing the updated game state
     */
    ViewData onRightEvent(MoveEvent event);

    /**
     * Handles a rotation event.
     *
     * @param event the move event containing type and source information
     * @return ViewData representing the updated game state
     */
    ViewData onRotateEvent(MoveEvent event);

    /**
     * Handles a hard drop event (instant drop to bottom).
     *
     * @param event the move event containing type and source information
     * @return DownData containing the result of the drop and any cleared rows
     */
    DownData onHardDropEvent(MoveEvent event);

    /**
     * Handles a hold piece event (swap current piece with held piece).
     *
     * @param event the move event containing type and source information
     * @return ViewData representing the updated game state
     */
    ViewData onHoldEvent(MoveEvent event);

    /**
     * Initialises a new game, resetting the board and score.
     */
    void createNewGame();
}