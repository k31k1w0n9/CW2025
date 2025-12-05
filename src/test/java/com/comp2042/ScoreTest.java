package com.comp2042;

import com.comp2042.logic.Score;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ScoreTest {

    private Score score;

    @BeforeEach
    void setUp() {
        score = new Score();
    }

    @Test
    void testInitialScore() {
        assertEquals(0, score.scoreProperty().get(), "Initial score should be 0");
    }

    @Test
    void testAddScore() {
        score.add(100);
        assertEquals(100, score.scoreProperty().get(), "Score should be 100 after adding 100");

        score.add(50);
        assertEquals(150, score.scoreProperty().get(), "Score should be 150 after adding 50 more");
    }

    @Test
    void testAddMultipleScores() {
        score.add(100);
        score.add(200);
        score.add(300);
        assertEquals(600, score.scoreProperty().get(), "Score should accumulate correctly");
    }

    @Test
    void testReset() {
        score.add(500);
        assertEquals(500, score.scoreProperty().get(), "Score should be 500");

        score.reset();
        assertEquals(0, score.scoreProperty().get(), "Score should be 0 after reset");
    }

    @Test
    void testScoreProperty() {
        // Verify that the property is observable
        assertNotNull(score.scoreProperty(), "Score property should not be null");

        score.add(250);
        assertEquals(250, score.scoreProperty().getValue(), "Property value should match score");
    }

    @Test
    void testNegativeScore() {
        // Edge case: adding negative values (e.g., penalties)
        score.add(100);
        score.add(-50);
        assertEquals(50, score.scoreProperty().get(), "Score should handle negative additions");
    }
}
