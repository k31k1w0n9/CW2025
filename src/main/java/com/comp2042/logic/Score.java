package com.comp2042.logic;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Manages the player's score in the Tetris game.
 * Uses JavaFX properties for easy binding to UI elements.
 */
public final class Score {

    /** The current score value as an observable property. */
    private final IntegerProperty score = new SimpleIntegerProperty(0);

    /**
     * Returns the score property for binding to UI elements.
     *
     * @return the IntegerProperty representing the current score
     */
    public IntegerProperty scoreProperty() {
        return score;
    }

    /**
     * Adds points to the current score.
     *
     * @param points the number of points to add
     */
    public void add(int points) {
        score.setValue(score.getValue() + points);
    }

    /**
     * Resets the score to zero.
     */
    public void reset() {
        score.setValue(0);
    }
}
