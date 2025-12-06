package com.comp2042.domain;

/**
 * Domain service interface for generating Tetromino bricks.
 * Defines the contract for brick generation strategies used by the game logic.
 * Implementations provide the logic for determining which brick appears next.
 */
public interface BrickGenerator {

    /**
     * Returns the next brick and advances the generator state.
     *
     * @return the next Brick to be used in the game
     */
    Brick getBrick();

    /**
     * Returns the next brick without advancing the generator state.
     *
     * @return the upcoming Brick (preview)
     */
    Brick getNextBrick();

    /**
     * Resets the generator to its initial state.
     * Used when starting a new game.
     */
    void reset();
}
