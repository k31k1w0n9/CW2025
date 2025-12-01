package com.comp2042;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class GameSettingsTest {

    private GameSettings settings;

    @BeforeEach
    void setUp() {
        settings = GameSettings.getInstance();
        // Clean up custom pieces added by other tests or previous runs
        List<String> customNames = settings.getCustomPieceNames();
        for (String name : customNames) {
            if (!name.equals("Custom 1") && !name.equals("Custom 2") && !name.equals("Custom 3")) {
                settings.removeCustomPiece(name);
            }
        }
        // Reset default custom pieces
        settings.clearCustomPiece("Custom 1");
        settings.clearCustomPiece("Custom 2");
        settings.clearCustomPiece("Custom 3");
    }

    @Test
    void testSingleton() {
        GameSettings s1 = GameSettings.getInstance();
        GameSettings s2 = GameSettings.getInstance();
        assertSame(s1, s2, "GameSettings should be a singleton");
    }

    @Test
    void testStandardPiecesExist() {
        assertTrue(settings.getAllPieceNames().contains("I Piece"));
        assertTrue(settings.getAllPieceNames().contains("T Piece"));
        assertNotNull(settings.getPieceSettings("I Piece"));
    }

    @Test
    void testCustomPieceManagement() {
        String newName = settings.addNewCustomPiece();
        assertTrue(newName.startsWith("Custom"), "New piece name should start with Custom");
        assertTrue(settings.getCustomPieceNames().contains(newName));

        settings.removeCustomPiece(newName);
        assertFalse(settings.getCustomPieceNames().contains(newName));
    }

    @Test
    void testCustomPieceDesign() {
        boolean[][] design = new boolean[4][4];
        design[0][0] = true;
        settings.saveCustomPiece("Custom 1", design);

        boolean[][] retrieved = settings.getCustomPiece("Custom 1");
        assertTrue(retrieved[0][0], "Design should be saved");

        settings.clearCustomPiece("Custom 1");
        assertFalse(settings.getCustomPiece("Custom 1")[0][0], "Design should be cleared");
    }

    @Test
    void testPieceSettings() {
        settings.setPieceColor("Custom 1", Color.BLUE);
        assertEquals(Color.BLUE, settings.getPieceSettings("Custom 1").color);

        settings.setPieceSpawnRate("Custom 1", 8);
        assertEquals(8, settings.getPieceSettings("Custom 1").spawnRate);
    }
}
