package com.comp2042.logic.bricks;

import com.comp2042.GameSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class RandomBrickGeneratorTest {

    private RandomBrickGenerator generator;

    @BeforeEach
    void setUp() {
        // Reset GameSettings to default state
        GameSettings settings = GameSettings.getInstance();

        // Enable all standard pieces
        String[] standardPieces = { "I Piece", "J Piece", "L Piece", "O Piece", "S Piece", "T Piece", "Z Piece" };
        for (String piece : standardPieces) {
            settings.setPieceEnableInGame(piece, true);
            settings.setPieceSpawnRate(piece, 5); // Default spawn rate
        }

        generator = new RandomBrickGenerator();
    }

    @Test
    void testGetBrick() {
        Brick brick = generator.getBrick();
        assertNotNull(brick, "Generator should return a brick");
        assertNotNull(brick.getShapeMatrix(), "Brick should have a shape matrix");
        assertFalse(brick.getShapeMatrix().isEmpty(), "Brick shape matrix should not be empty");
    }

    @Test
    void testGetNextBrick() {
        Brick next = generator.getNextBrick();
        assertNotNull(next, "Next brick should not be null");

        // Verify that getNextBrick doesn't remove the brick from queue
        Brick next2 = generator.getNextBrick();
        assertNotNull(next2, "Next brick should still be available");
    }

    @Test
    void testBrickVariety() {
        // Generate 50 bricks and verify we get different types
        Map<String, Integer> brickCounts = new HashMap<>();

        for (int i = 0; i < 50; i++) {
            Brick brick = generator.getBrick();
            String type = brick.getClass().getSimpleName();
            brickCounts.put(type, brickCounts.getOrDefault(type, 0) + 1);
        }

        // With 7 standard pieces enabled, we should see multiple types
        assertTrue(brickCounts.size() >= 3,
                "Should generate at least 3 different brick types in 50 attempts");
    }

    @Test
    void testReset() {
        // Get a few bricks
        generator.getBrick();
        generator.getBrick();

        // Reset the generator
        generator.reset();

        // Should still be able to get bricks after reset
        Brick brick = generator.getBrick();
        assertNotNull(brick, "Should get brick after reset");
    }

    @Test
    void testBagRefill() {
        // Generate enough bricks to force multiple bag refills
        // A standard bag has 7 pieces, so generating 20 should refill at least twice
        for (int i = 0; i < 20; i++) {
            Brick brick = generator.getBrick();
            assertNotNull(brick, "Should always get a brick, even after bag refills");
        }
    }

    @Test
    void testCustomBrickSpawnRate() {
        GameSettings settings = GameSettings.getInstance();

        // Add a custom piece with high spawn rate
        boolean[][] design = new boolean[3][3];
        design[1][1] = true; // Single block in center

        String customName = settings.addNewCustomPiece();
        settings.saveCustomPiece(customName, design);
        settings.setPieceEnableInGame(customName, true);
        settings.setPieceSpawnRate(customName, 10); // Maximum spawn rate

        // Reset generator to pick up new settings
        generator.reset();

        // Generate many bricks and count custom pieces
        int customCount = 0;
        int total = 100;

        for (int i = 0; i < total; i++) {
            Brick brick = generator.getBrick();
            if (brick instanceof CustomBrick) {
                customCount++;
            }
        }

        // With spawn rate 10, custom pieces should appear frequently
        // (At least 10% of the time, but likely more)
        assertTrue(customCount > 5,
                "Custom piece with high spawn rate should appear multiple times in " + total + " bricks");
    }

    @Test
    void testDisabledPieceNotGenerated() {
        GameSettings settings = GameSettings.getInstance();

        // Remove all custom pieces first
        for (String customName : settings.getCustomPieceNames()) {
            settings.removeCustomPiece(customName);
        }

        // Disable all pieces except I Piece
        String[] standardPieces = { "J Piece", "L Piece", "O Piece", "S Piece", "T Piece", "Z Piece" };
        for (String piece : standardPieces) {
            settings.setPieceEnableInGame(piece, false);
        }
        settings.setPieceEnableInGame("I Piece", true);

        // Reset generator to pick up new settings
        generator.reset();

        // Generate several bricks
        for (int i = 0; i < 10; i++) {
            Brick brick = generator.getBrick();
            assertEquals("IBrick", brick.getClass().getSimpleName(),
                    "Should only generate I Piece when others are disabled");
        }
    }

    @Test
    void testEmergencyFallback() {
        GameSettings settings = GameSettings.getInstance();

        // Remove all custom pieces
        for (String customName : settings.getCustomPieceNames()) {
            settings.removeCustomPiece(customName);
        }

        // Disable ALL standard pieces
        String[] standardPieces = { "I Piece", "J Piece", "L Piece", "O Piece", "S Piece", "T Piece", "Z Piece" };
        for (String piece : standardPieces) {
            settings.setPieceEnableInGame(piece, false);
        }

        // Reset generator
        generator.reset();

        // Should still get a brick (emergency fallback I-Piece)
        Brick brick = generator.getBrick();
        assertNotNull(brick, "Should get fallback brick even when all pieces disabled");
        assertEquals("IBrick", brick.getClass().getSimpleName(),
                "Fallback should be I Piece");
    }
}
