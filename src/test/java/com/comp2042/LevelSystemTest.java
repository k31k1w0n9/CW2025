package com.comp2042;

import com.comp2042.system.LevelSystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link LevelSystem} class.
 * Verifies level progression, gravity calculation, and 20G mode behavior.
 */
class LevelSystemTest {

    private LevelSystem levelSystem;

    @BeforeEach
    void setUp() {
        levelSystem = new LevelSystem();
    }

    @Test
    void testInitialState() {
        assertEquals(1, levelSystem.getLevel(), "Initial level should be 1");
        assertEquals(0, levelSystem.getTotalLinesCleared(), "Initial lines cleared should be 0");
        assertEquals(1, levelSystem.getGravity(), "Initial gravity should be 1");
    }

    @Test
    void testLevelUp() {
        levelSystem.addLinesCleared(10);
        assertEquals(2, levelSystem.getLevel(), "Should be level 2 after 10 lines");
        assertEquals(10, levelSystem.getTotalLinesCleared());
        assertEquals(2, levelSystem.getGravity(), "Gravity should increase with level");
    }

    @Test
    void testMultipleLevelUps() {
        levelSystem.addLinesCleared(25);
        assertEquals(3, levelSystem.getLevel(), "Should be level 3 after 25 lines (10+10+5)");
        assertEquals(25, levelSystem.getTotalLinesCleared());
        assertEquals(3, levelSystem.getGravity());
    }

    @Test
    void testGravityCap() {
        levelSystem.addLinesCleared(250); // Level 26
        assertEquals(26, levelSystem.getLevel());
        assertEquals(20, levelSystem.getGravity(), "Gravity should be capped at 20");
        assertTrue(levelSystem.is20G(), "Should be in 20G mode");
    }

    @Test
    void testDropInterval() {
        // Level 1, Gravity 1
        assertEquals(1000, levelSystem.getDropIntervalMs(), "Drop interval at 1G should be 1000ms");

        // Level 2, Gravity 2
        levelSystem.addLinesCleared(10);
        assertEquals(500, levelSystem.getDropIntervalMs(), "Drop interval at 2G should be 500ms");

        // Max Gravity
        levelSystem.addLinesCleared(200); // Level 21
        assertEquals(1, levelSystem.getDropIntervalMs(), "Drop interval at 20G should be 1ms");
    }

    @Test
    void testReset() {
        levelSystem.addLinesCleared(50);
        levelSystem.reset();
        assertEquals(1, levelSystem.getLevel());
        assertEquals(0, levelSystem.getTotalLinesCleared());
        assertEquals(1, levelSystem.getGravity());
    }
}
