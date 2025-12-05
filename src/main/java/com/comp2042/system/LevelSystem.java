package com.comp2042.system;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Manages the level and gravity system for Tetris following standard
 * guidelines.
 * 
 * <p>
 * Rules:
 * <ul>
 * <li>Every 10 lines cleared increases level by 1</li>
 * <li>Gravity increases by 1G per level</li>
 * <li>1G = 1 cell per frame</li>
 * <li>0.1G = 1 cell per 10 frames</li>
 * <li>At 20G, pieces instantly drop to lock position</li>
 * </ul>
 */
public class LevelSystem {

    /** The current level (observable property for UI binding). */
    private final IntegerProperty level = new SimpleIntegerProperty(1);

    /** Total lines cleared across all levels (observable property). */
    private final IntegerProperty totalLinesCleared = new SimpleIntegerProperty(0);

    /** Current gravity level (observable property). */
    private final IntegerProperty gravity = new SimpleIntegerProperty(1);

    /** Number of lines required to advance to the next level. */
    private static final int LINES_PER_LEVEL = 10;

    /** Maximum gravity level (instant drop). */
    private static final int MAX_GRAVITY = 20;

    /** Base drop interval at 1G in milliseconds. */
    private static final int BASE_DROP_INTERVAL_MS = 1000;

    /**
     * Constructs a new LevelSystem at level 1 with 1G gravity.
     */
    public LevelSystem() {
        level.set(1);
        gravity.set(1);
        totalLinesCleared.set(0);
    }

    /**
     * Adds cleared lines and updates level/gravity accordingly.
     *
     * @param lines the number of lines cleared
     */
    public void addLinesCleared(int lines) {
        if (lines <= 0)
            return;

        int oldTotal = totalLinesCleared.get();
        int newTotal = oldTotal + lines;
        totalLinesCleared.set(newTotal);

        int newLevel = (newTotal / LINES_PER_LEVEL) + 1;

        if (newLevel != level.get()) {
            level.set(newLevel);
            updateGravity();
        }
    }

    /**
     * Updates gravity based on current level.
     * Gravity equals level, capped at 20G.
     */
    private void updateGravity() {
        int newGravity = Math.min(level.get(), MAX_GRAVITY);
        gravity.set(newGravity);
    }

    /**
     * Calculates the drop interval in milliseconds based on current gravity.
     * 
     * <p>
     * Formula: dropInterval = BASE_INTERVAL / gravity
     *
     * @return the drop interval in milliseconds
     */
    public int getDropIntervalMs() {
        int currentGravity = gravity.get();

        if (currentGravity >= MAX_GRAVITY) {
            return 1;
        }

        return BASE_DROP_INTERVAL_MS / currentGravity;
    }

    /**
     * Checks if the game is at 20G (instant drop mode).
     *
     * @return true if at maximum gravity
     */
    public boolean is20G() {
        return gravity.get() >= MAX_GRAVITY;
    }

    /**
     * Resets the level system to initial state.
     */
    public void reset() {
        level.set(1);
        gravity.set(1);
        totalLinesCleared.set(0);
    }

    /**
     * Returns the level property for UI binding.
     *
     * @return the level property
     */
    public IntegerProperty levelProperty() {
        return level;
    }

    /**
     * Returns the total lines cleared property for UI binding.
     *
     * @return the total lines cleared property
     */
    public IntegerProperty totalLinesClearedProperty() {
        return totalLinesCleared;
    }

    /**
     * Returns the gravity property for UI binding.
     *
     * @return the gravity property
     */
    public IntegerProperty gravityProperty() {
        return gravity;
    }

    /**
     * Returns the current level.
     *
     * @return the level number
     */
    public int getLevel() {
        return level.get();
    }

    /**
     * Returns the total lines cleared.
     *
     * @return total lines cleared count
     */
    public int getTotalLinesCleared() {
        return totalLinesCleared.get();
    }

    /**
     * Returns the current gravity level.
     *
     * @return the gravity value
     */
    public int getGravity() {
        return gravity.get();
    }
}
