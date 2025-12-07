package com.comp2042.system;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.scene.paint.Color;

/**
 * Singleton class that manages global game settings and configuration.
 * Stores per-piece settings (color, spawn rate, visibility) for both standard
 * and custom Tetrominoes. Also manages general game configuration defaults.
 * This class acts as the central repository for all customizable game data.
 */
public class GameSettings {
    private static GameSettings instance;

    private final Map<String, boolean[][]> customPieces;
    private final Map<String, PieceSettings> pieceSettings; // Per-piece settings
    private Color defaultCustomPieceColor; // Default for new pieces
    private boolean defaultOutlineEnabled;
    private boolean enableInGame;
    private int spawnRate; // 0 to 10

    /**
     * Data structure holding configuration for a specific Tetromino piece.
     * Includes visual properties (color, outline) and gameplay properties
     * (spawn rate, active status).
     */
    public static class PieceSettings {
        public Color color;
        public boolean outlineEnabled;
        public boolean enableInGame;
        public int spawnRate;
        public int id;
        public int gridSize;

        /**
         * Creates a new PieceSettings object with the specified configuration.
         *
         * @param color          The display color of the piece.
         * @param outlineEnabled Whether the piece should have an outline.
         * @param enableInGame   Whether the piece can spawn in the game.
         * @param spawnRate      The relative frequency of this piece spawning.
         * @param id             The unique identifier for the piece.
         * @param gridSize       The size of the grid (e.g., 4 for 4x4) used for this
         *                       piece.
         */
        public PieceSettings(Color color, boolean outlineEnabled, boolean enableInGame, int spawnRate, int id,
                int gridSize) {
            this.color = color;
            this.outlineEnabled = outlineEnabled;
            this.enableInGame = enableInGame;
            this.spawnRate = spawnRate;
            this.id = id;
            this.gridSize = gridSize;
        }
    }

    /**
     * Private constructor to enforce Singleton pattern.
     * Initializes standard pieces with official guideline colors and shapes.
     * Sets up default slots for custom pieces.
     */
    private GameSettings() {
        customPieces = new HashMap<>();
        pieceSettings = new HashMap<>();
        // Default settings
        defaultCustomPieceColor = Color.web("#FF00FF"); // Default hot pink
        defaultOutlineEnabled = true;
        enableInGame = true;
        spawnRate = 5; // Global default, now used as default for new pieces

        // Initialize standard Tetris pieces with their shapes
        initializeStandardPieces();

        // Initialize empty custom pieces
        customPieces.put("Custom 1", new boolean[4][4]);
        customPieces.put("Custom 2", new boolean[4][4]);
        customPieces.put("Custom 3", new boolean[4][4]);

        // Initialize default settings for existing custom pieces with unique IDs
        // starting from 9
        pieceSettings.put("Custom 1", new PieceSettings(defaultCustomPieceColor, defaultOutlineEnabled, true, 5, 9, 4));
        pieceSettings.put("Custom 2",
                new PieceSettings(defaultCustomPieceColor, defaultOutlineEnabled, true, 5, 10, 4));
        pieceSettings.put("Custom 3",
                new PieceSettings(defaultCustomPieceColor, defaultOutlineEnabled, true, 5, 11, 4));
    }

    private void initializeStandardPieces() {
        // I Piece (Cyan)
        boolean[][] iPiece = new boolean[4][4];
        iPiece[1][0] = iPiece[1][1] = iPiece[1][2] = iPiece[1][3] = true;
        customPieces.put("I Piece", iPiece);
        pieceSettings.put("I Piece", new PieceSettings(Color.CYAN, true, true, 5, 1, 4));

        // O Piece (Yellow)
        boolean[][] oPiece = new boolean[4][4];
        oPiece[1][1] = oPiece[1][2] = oPiece[2][1] = oPiece[2][2] = true;
        customPieces.put("O Piece", oPiece);
        pieceSettings.put("O Piece", new PieceSettings(Color.YELLOW, true, true, 5, 2, 4));

        // T Piece (Purple)
        boolean[][] tPiece = new boolean[4][4];
        tPiece[1][0] = tPiece[1][1] = tPiece[1][2] = tPiece[2][1] = true;
        customPieces.put("T Piece", tPiece);
        pieceSettings.put("T Piece", new PieceSettings(Color.PURPLE, true, true, 5, 3, 4));

        // S Piece (Green)
        boolean[][] sPiece = new boolean[4][4];
        sPiece[1][1] = sPiece[1][2] = sPiece[2][0] = sPiece[2][1] = true;
        customPieces.put("S Piece", sPiece);
        pieceSettings.put("S Piece", new PieceSettings(Color.GREEN, true, true, 5, 4, 4));

        // Z Piece (Red)
        boolean[][] zPiece = new boolean[4][4];
        zPiece[1][0] = zPiece[1][1] = zPiece[2][1] = zPiece[2][2] = true;
        customPieces.put("Z Piece", zPiece);
        pieceSettings.put("Z Piece", new PieceSettings(Color.RED, true, true, 5, 5, 4));

        // J Piece (Blue)
        boolean[][] jPiece = new boolean[4][4];
        jPiece[1][0] = jPiece[2][0] = jPiece[2][1] = jPiece[2][2] = true;
        customPieces.put("J Piece", jPiece);
        pieceSettings.put("J Piece", new PieceSettings(Color.BLUE, true, true, 5, 6, 4));

        // L Piece (Orange)
        boolean[][] lPiece = new boolean[4][4];
        lPiece[1][2] = lPiece[2][0] = lPiece[2][1] = lPiece[2][2] = true;
        customPieces.put("L Piece", lPiece);
        pieceSettings.put("L Piece", new PieceSettings(Color.ORANGE, true, true, 5, 7, 4));
    }

    /**
     * Returns the singleton instance of GameSettings.
     *
     * @return the global GameSettings instance
     */
    public static GameSettings getInstance() {
        if (instance == null) {
            instance = new GameSettings();
        }
        return instance;
    }

    /**
     * Saves a design grid for a custom piece interactively created by the user.
     * Creates a deep copy of the design array to ensure data integrity.
     *
     * @param name   the unique name of the custom piece
     * @param design the 2D boolean array representing the piece's shape
     */
    public void saveCustomPiece(String name, boolean[][] design) {
        // Deep copy to prevent reference issues
        boolean[][] copy = new boolean[design.length][design[0].length];
        for (int i = 0; i < design.length; i++) {
            System.arraycopy(design[i], 0, copy[i], 0, design[i].length);
        }
        customPieces.put(name, copy);
    }

    /**
     * Resets the design of a custom piece to an empty grid.
     * The piece entry remains in the system.
     *
     * @param name the name of the custom piece to clear
     */
    public void clearCustomPiece(String name) {
        if (name == null || !customPieces.containsKey(name)) {
            return;
        }
        customPieces.put(name, new boolean[4][4]);
    }

    /**
     * Removes a custom piece from the system.
     * Prevents removal of standard pieces or non-custom entries.
     *
     * @param name the name of the custom piece to remove
     */
    public void removeCustomPiece(String name) {
        if (name == null || !name.startsWith("Custom")) {
            return;
        }
        customPieces.remove(name);
    }

    /**
     * Creates a new custom piece entry with a unique name.
     * Automatically handles name generation (e.g., "Custom 1", "Custom 2")
     * and assigns a unique ID for the new piece.
     *
     * @return the name of the newly created custom piece
     */
    public String addNewCustomPiece() {
        int nextNum = 1;
        String newName;
        do {
            newName = "Custom " + nextNum;
            nextNum++;
        } while (customPieces.containsKey(newName) && nextNum < 1000);

        customPieces.put(newName, new boolean[4][4]);
        // Initialize with default settings and next available ID
        int nextId = getNextAvailableId();
        pieceSettings.put(newName,
                new PieceSettings(defaultCustomPieceColor, defaultOutlineEnabled, true, 5, nextId, 4));
        return newName;
    }

    /**
     * Calculates the next available unique ID for a custom piece.
     * Scans existing IDs and returns the highest ID + 1.
     *
     * @return the next available integer ID
     */
    private int getNextAvailableId() {
        int nextId = 9;
        for (PieceSettings ps : pieceSettings.values()) {
            if (ps.id >= nextId) {
                nextId = ps.id + 1;
            }
        }
        return nextId;
    }

    /**
     * Retrieves a sorted list of all custom piece names.
     * Sorts numerically based on the suffix number in the name.
     *
     * @return a List of strings containing custom piece names
     */
    public List<String> getCustomPieceNames() {
        return customPieces.keySet().stream()
                .filter(name -> name.startsWith("Custom"))
                .sorted((a, b) -> {
                    int numA = Integer.parseInt(a.replace("Custom ", ""));
                    int numB = Integer.parseInt(b.replace("Custom ", ""));
                    return Integer.compare(numA, numB);
                })
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a list of all piece names (standard and custom).
     * Standard pieces (I, O, T, S, Z, J, L) are listed first, followed by custom
     * pieces.
     *
     * @return a List of all piece names in the system
     */
    public List<String> getAllPieceNames() {
        List<String> allPieces = new java.util.ArrayList<>();
        // Add standard pieces first in order
        allPieces.add("I Piece");
        allPieces.add("O Piece");
        allPieces.add("T Piece");
        allPieces.add("S Piece");
        allPieces.add("Z Piece");
        allPieces.add("J Piece");
        allPieces.add("L Piece");
        // Add custom pieces
        allPieces.addAll(getCustomPieceNames());
        return allPieces;
    }

    /**
     * Checks if the given piece name corresponds to a standard Tetris piece.
     *
     * @param pieceName the name of the piece to check
     * @return true if it is a standard piece, false if custom or null
     */
    public boolean isStandardPiece(String pieceName) {
        return pieceName != null && pieceName.endsWith(" Piece") && !pieceName.startsWith("Custom");
    }

    /**
     * Retrieves the grid design associated with a piece name.
     *
     * @param name the name of the piece
     * @return the 2D boolean array representing the shape, or null if not found
     */
    public boolean[][] getCustomPiece(String name) {
        return customPieces.get(name);
    }

    /**
     * Returns the map of all custom piece designs.
     *
     * @return a Map where key is the piece name and value is the shape grid
     */
    public Map<String, boolean[][]> getAllCustomPieces() {
        return customPieces;
    }

    // Per-piece settings methods
    /**
     * Retrieves settings for a specific piece.
     * If settings for a custom piece do not exist, they are initialized with
     * defaults.
     *
     * @param pieceName the name of the piece to look up
     * @return the PieceSettings object containing configuration for the piece
     */
    public PieceSettings getPieceSettings(String pieceName) {
        if (pieceName != null) {
            PieceSettings settings = pieceSettings.get(pieceName);
            if (settings == null && pieceName.startsWith("Custom")) {
                // Initialize with defaults if not found (only for custom pieces)
                int id = getNextAvailableId();
                settings = new PieceSettings(defaultCustomPieceColor, defaultOutlineEnabled, true, 5, id, 4);
                pieceSettings.put(pieceName, settings);
            }
            return settings;
        }
        return null;
    }

    /**
     * Sets the color for a specific piece.
     * Updates the persistence model.
     *
     * @param pieceName the name of the piece
     * @param color     the new color
     */
    public void setPieceColor(String pieceName, Color color) {
        if (pieceName != null) {
            PieceSettings settings = pieceSettings.get(pieceName);
            if (settings == null && pieceName.startsWith("Custom")) {
                int id = getNextAvailableId();
                settings = new PieceSettings(color, defaultOutlineEnabled, true, 5, id, 4);
                pieceSettings.put(pieceName, settings);
            } else if (settings != null) {
                settings.color = color;
            }
        }
    }

    /**
     * Enables or disables the outline for a specific piece.
     *
     * @param pieceName the name of the piece
     * @param enabled   true to enable outline, false to disable
     */
    public void setPieceOutlineEnabled(String pieceName, boolean enabled) {
        if (pieceName != null) {
            PieceSettings settings = pieceSettings.get(pieceName);
            if (settings == null && pieceName.startsWith("Custom")) {
                int id = getNextAvailableId();
                settings = new PieceSettings(defaultCustomPieceColor, enabled, true, 5, id, 4);
                pieceSettings.put(pieceName, settings);
            } else if (settings != null) {
                settings.outlineEnabled = enabled;
            }
        }
    }

    /**
     * Enables or disables a piece from spawning in the game.
     *
     * @param pieceName the name of the piece
     * @param enabled   true to include in random generation, false to exclude
     */
    public void setPieceEnableInGame(String pieceName, boolean enabled) {
        if (pieceName != null) {
            PieceSettings settings = pieceSettings.get(pieceName);
            if (settings == null && pieceName.startsWith("Custom")) {
                int id = getNextAvailableId();
                settings = new PieceSettings(defaultCustomPieceColor, defaultOutlineEnabled, enabled, 5, id, 4);
                pieceSettings.put(pieceName, settings);
            } else if (settings != null) {
                settings.enableInGame = enabled;
            }
        }
    }

    /**
     * Sets the relative spawn rate (weight) for a piece.
     *
     * @param pieceName the name of the piece
     * @param rate      the spawn rate (typically 0-10)
     */
    public void setPieceSpawnRate(String pieceName, int rate) {
        if (pieceName != null) {
            PieceSettings settings = pieceSettings.get(pieceName);
            if (settings == null && pieceName.startsWith("Custom")) {
                int id = getNextAvailableId();
                settings = new PieceSettings(defaultCustomPieceColor, defaultOutlineEnabled, true, rate, id, 4);
                pieceSettings.put(pieceName, settings);
            } else if (settings != null) {
                settings.spawnRate = rate;
            }
        }
    }

    /**
     * Sets the grid size for a custom piece.
     *
     * @param pieceName the name of the custom piece
     * @param size      the dimension of the grid (e.g., 4 for 4x4)
     */
    public void setPieceGridSize(String pieceName, int size) {
        if (pieceName != null && pieceName.startsWith("Custom")) {
            PieceSettings settings = pieceSettings.get(pieceName);
            if (settings == null) {
                int id = getNextAvailableId();
                settings = new PieceSettings(defaultCustomPieceColor, defaultOutlineEnabled, true, 5, id, size);
                pieceSettings.put(pieceName, settings);
            } else {
                settings.gridSize = size;
            }
        }
    }

    // Legacy methods for backwards compatibility (now use default)
    /**
     * Retrieves the default color for new custom pieces.
     *
     * @return the default Color
     */
    public Color getCustomPieceColor() {
        return defaultCustomPieceColor;
    }

    /**
     * Sets the default color for new custom pieces.
     *
     * @param customPieceColor the new default Color
     */
    public void setCustomPieceColor(Color customPieceColor) {
        this.defaultCustomPieceColor = customPieceColor;
    }

    /**
     * Checks if outlines are enabled by default for new pieces.
     *
     * @return true if outlines are enabled, false otherwise
     */
    public boolean isOutlineEnabled() {
        return defaultOutlineEnabled;
    }

    /**
     * Sets the default outline setting for new pieces.
     *
     * @param outlineEnabled true to enable outlines, false to disable
     */
    public void setOutlineEnabled(boolean outlineEnabled) {
        this.defaultOutlineEnabled = outlineEnabled;
    }

    /**
     * Checks if new pieces are enabled in-game by default.
     *
     * @return true if enabled, false otherwise
     */
    public boolean isEnableInGame() {
        return enableInGame;
    }

    /**
     * Sets the default in-game enablement status for new pieces.
     *
     * @param enableInGame true to enable, false to disable
     */
    public void setEnableInGame(boolean enableInGame) {
        this.enableInGame = enableInGame;
    }

    /**
     * Retrieves the default spawn rate for new pieces.
     *
     * @return the spawn rate (0-10)
     */
    public int getSpawnRate() {
        return spawnRate;
    }

    /**
     * Sets the default spawn rate for new pieces.
     *
     * @param spawnRate the new spawn rate (0-10)
     */
    public void setSpawnRate(int spawnRate) {
        this.spawnRate = spawnRate;
    }
}
