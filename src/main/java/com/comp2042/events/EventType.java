package com.comp2042.events;

/**
 * Enumeration representing the different types of movement events in the Tetris
 * game.
 * These events correspond to player input actions that control the falling
 * Tetromino.
 */
public enum EventType {
    /** Move the piece left. */
    LEFT,
    /** Move the piece right. */
    RIGHT,
    /** Soft drop - move the piece down one row. */
    DOWN,
    /** Rotate the piece clockwise. */
    ROTATE,
    /** Hard drop - instantly drop the piece to the bottom. */
    HARD_DROP,
    /** Hold the current piece and swap with the held piece. */
    HOLD
}