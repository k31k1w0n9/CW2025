package com.comp2042;

import com.comp2042.system.HighScoreManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class HighScoreManagerTest {

    private static final String TEST_FILE = "test_highscores.dat";
    private HighScoreManager manager;

    @BeforeEach
    void setUp() {
        // Ensure clean state
        File file = new File(TEST_FILE);
        if (file.exists()) {
            file.delete();
        }
        manager = new HighScoreManager(TEST_FILE);
    }

    @AfterEach
    void tearDown() {
        File file = new File(TEST_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void testInitialScores() {
        List<HighScoreManager.HighScoreEntry> scores = manager.getHighScores();
        assertEquals(5, scores.size(), "Should start with 5 default scores");
        assertEquals(500, scores.get(0).getScore(), "Top default score should be 500");
    }

    @Test
    void testIsHighScore() {
        assertTrue(manager.isHighScore(600), "600 should be a high score (beats 500)");
        assertTrue(manager.isHighScore(150), "150 should be a high score (beats 100)");
        assertFalse(manager.isHighScore(50), "50 should not be a high score (less than 100)");
    }

    @Test
    void testAddHighScore() {
        int rank = manager.addHighScore("Player1", 600);
        assertEquals(1, rank, "Should be rank 1");
        assertEquals(600, manager.getHighScore(), "Top score should now be 600");

        rank = manager.addHighScore("Player2", 250);
        assertEquals(5, rank, "Should be rank 5 (600, 500, 400, 300, 250)");

        List<HighScoreManager.HighScoreEntry> scores = manager.getHighScores();
        assertEquals(5, scores.size(), "Should still have max 5 scores");
        assertEquals("Player1", scores.get(0).getName());
        assertEquals("Player2", scores.get(4).getName());
    }

    @Test
    void testPersistence() {
        manager.addHighScore("PersistentPlayer", 1000);

        // Create new manager with same file
        HighScoreManager newManager = new HighScoreManager(TEST_FILE);
        assertEquals(1000, newManager.getHighScore(), "Should load persisted high score");
        assertEquals("PersistentPlayer", newManager.getHighScores().get(0).getName());
    }
}
