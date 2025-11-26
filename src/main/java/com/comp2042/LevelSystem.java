package com.comp2042;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Manages the level and gravity system for Tetris.
 * 
 * Rules:
 * - Every 10 lines cleared increases level by 1
 * - Gravity increases by 1G per level
 * - 1G = 1 cell per frame
 * - 0.1G = 1 cell per 10 frames
 * - At 20G, pieces instantly drop to lock position
 */
public class LevelSystem {

    private final IntegerProperty level = new SimpleIntegerProperty(1);
    private final IntegerProperty totalLinesCleared = new SimpleIntegerProperty(0);
    private final IntegerProperty gravity = new SimpleIntegerProperty(1);

    // Constants
    private static final int LINES_PER_LEVEL = 10;
    private static final int MAX_GRAVITY = 20;
    private static final int BASE_DROP_INTERVAL_MS = 1000; // 1 second at 1G (assuming 60fps, ~16.67ms per frame)

    public LevelSystem() {
        // Start at level 1, gravity 1G
        level.set(1);
        gravity.set(1);
        totalLinesCleared.set(0);
    }

    /**
     * Add cleared lines and update level/gravity accordingly
     */
    public void addLinesCleared(int lines) {
        if (lines <= 0)
            return;

        int oldTotal = totalLinesCleared.get();
        int newTotal = oldTotal + lines;
        totalLinesCleared.set(newTotal);

        System.out.println("[INFO] Lines cleared: +" + lines + " (Total: " + newTotal + ")");

        // Calculate new level (1-indexed: 0-9 lines = level 1, 10-19 lines = level 2,
        // etc.)
        int newLevel = (newTotal / LINES_PER_LEVEL) + 1;

        System.out.println("[DEBUG] Level calculation: " + newTotal + " / " + LINES_PER_LEVEL + " + 1 = " + newLevel);

        if (newLevel != level.get()) {
            level.set(newLevel);
            updateGravity();
            System.out.println("[EVENT] LEVEL UP! Now at Level " + newLevel + ", Gravity " + gravity.get() + "G");
        } else {
            System.out.println("[INFO] Still at Level " + level.get());
        }
    }

    /**
     * Update gravity based on current level
     * Gravity = Level, capped at 20G
     */
    private void updateGravity() {
        int newGravity = Math.min(level.get(), MAX_GRAVITY);
        gravity.set(newGravity);
    }

    /**
     * Calculate the drop interval in milliseconds based on current gravity
     * 
     * At 60 FPS (16.67ms per frame):
     * - 1G = 1 cell per frame = ~16.67ms per cell
     * - 0.1G = 1 cell per 10 frames = ~167ms per cell
     * 
     * For simplicity, we'll use a formula:
     * dropInterval = BASE_INTERVAL / gravity
     * 
     * At 20G, pieces should drop instantly (handled separately)
     */
    public int getDropIntervalMs() {
        int currentGravity = gravity.get();

        // At 20G, return minimal interval (instant drop handled in game logic)
        if (currentGravity >= MAX_GRAVITY) {
            return 1; // Minimal interval, actual instant drop handled by hard drop logic
        }

        // Calculate interval: slower at low gravity, faster at high gravity
        // Formula: 1000ms / gravity = drop interval
        // Level 1 (1G): 1000ms
        // Level 5 (5G): 200ms
        // Level 10 (10G): 100ms
        // Level 19 (19G): ~53ms
        return BASE_DROP_INTERVAL_MS / currentGravity;
    }

    /**
     * Check if we're at 20G (instant drop mode)
     */
    public boolean is20G() {
        return gravity.get() >= MAX_GRAVITY;
    }

    /**
     * Reset to initial state
     */
    public void reset() {
        level.set(1);
        gravity.set(1);
        totalLinesCleared.set(0);
    }

    // Getters for properties (for UI binding)
    public IntegerProperty levelProperty() {
        return level;
    }

    public IntegerProperty totalLinesClearedProperty() {
        return totalLinesCleared;
    }

    public IntegerProperty gravityProperty() {
        return gravity;
    }

    public int getLevel() {
        return level.get();
    }

    public int getTotalLinesCleared() {
        return totalLinesCleared.get();
    }

    public int getGravity() {
        return gravity.get();
    }
}
