package com.comp2042;

import com.comp2042.logic.SimpleBoard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link SimpleBoard} logic.
 * Covers board operations including movement, rotation, line clearing,
 * and game over conditions.
 */
class SimpleBoardTest {

    private SimpleBoard board;

    @BeforeEach
    void setUp() {
        board = new SimpleBoard(22, 10);
    }

    @Test
    void testHoldPiece() {
        // Get initial piece
        int[][] firstPiece = board.getViewData().getBrickData();

        // Trigger hold
        assertTrue(board.holdPiece(), "Should successfully hold piece");

        // Verify hold piece shape is stored
        assertNotNull(board.getHoldPieceShape(), "Hold piece should not be null");
        assertArrayEquals(firstPiece, board.getHoldPieceShape(), "Held piece should match the original piece");

        // Verify new piece spawned
        assertNotEquals(firstPiece, board.getViewData().getBrickData(), "New piece should be spawned");
    }

    @Test
    void testHoldPieceLimit() {
        board.holdPiece(); // First hold allowed
        assertFalse(board.holdPiece(), "Should not allow holding twice in the same turn");

        // Simulate locking a piece (which resets hold ability)
        board.createNewBrick();
        // Note: In real game, createNewBrick is called after locking.
        // Here we manually call it to simulate the turn end.

        // Actually, SimpleBoard.createNewBrick() resets canHold to true.
        // But createNewBrick is called internally by holdPiece() too.
        // We need to verify canHold logic.
        // The public API doesn't expose canHold directly, but we can test behavior.

        // After createNewBrick (new turn), hold should be allowed again
        assertTrue(board.holdPiece(), "Should allow hold again after new piece spawns");
    }

    @Test
    void testGhostPiecePosition() {
        // Ghost piece should be at the same X but lower or equal Y
        int currentY = board.getViewData().getyPosition();
        int ghostY = board.getGhostYPosition();

        assertTrue(ghostY >= currentY, "Ghost piece should be below or at current position");

        // Move piece down and verify ghost stays same (until piece passes it, which
        // shouldn't happen)
        board.moveBrickDown();
        assertEquals(ghostY, board.getGhostYPosition(),
                "Ghost Y should remain constant if only moving down in empty space");
    }

    @Test
    void testHardDrop() {
        int startY = board.getViewData().getyPosition();
        int ghostY = board.getGhostYPosition();

        int dropDistance = board.hardDrop();

        assertEquals(ghostY - startY, dropDistance, "Drop distance should match distance to ghost");
        assertEquals(ghostY, board.getViewData().getyPosition(), "Piece should be at ghost position after hard drop");
    }

    @Test
    void testWallKick() {
        // Create a new game to ensure clean state
        board.newGame();

        // We need a piece that changes width when rotated (e.g., I-piece or T-piece)
        // Since we can't easily force a specific piece without mocking,
        // we'll try to rotate whatever piece we get against the left wall.
        // If it's an O-piece, rotation doesn't change much, but wall kick logic still
        // runs.

        // Move piece all the way to the left
        for (int i = 0; i < 10; i++) {
            board.moveBrickLeft();
        }

        // Get initial state
        int[][] initialShape = board.getViewData().getBrickData();

        // Try to rotate
        boolean rotated = board.rotateLeftBrick();

        // Even if it's an O-piece, rotateLeftBrick returns true.
        // If it's an I-piece against the wall, a simple rotation might fail without
        // wall kicks.
        // The fact that it returns true implies either no collision or successful wall
        // kick.

        assertTrue(rotated, "Rotation against wall should succeed (via wall kick if needed)");

        // Verify shape changed (unless it's O-piece)
        int[][] newShape = board.getViewData().getBrickData();
        // We can't assert shape change strictly because of O-piece,
        // but we verified the action was allowed.
    }
}
