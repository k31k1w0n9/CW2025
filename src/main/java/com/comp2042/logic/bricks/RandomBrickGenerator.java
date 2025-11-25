package com.comp2042.logic.bricks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Random;
import com.comp2042.GameSettings;

public class RandomBrickGenerator implements BrickGenerator {

    private final Deque<Brick> nextBricks = new ArrayDeque<>();
    private final List<Brick> currentBag = new ArrayList<>();
    private final Random random = new Random();

    public RandomBrickGenerator() {
        // Fill initial bag and queue
        refillBag();
        fillQueue();
    }

    @Override
    public void reset() {
        nextBricks.clear();
        currentBag.clear();
        refillBag();
        fillQueue();
    }

    /**
     * Weighted Bag Randomizer.
     * Each enabled piece is added to the bag 'spawnRate' times.
     * This ensures that pieces with higher spawn rates appear more frequently
     * in a predictable manner.
     */
    private void refillBag() {
        currentBag.clear();
        GameSettings settings = GameSettings.getInstance();

        List<BrickEntry> enabledPieces = new ArrayList<>();

        // Collect all enabled standard pieces
        addPieceEntry(settings, "I Piece", enabledPieces);
        addPieceEntry(settings, "J Piece", enabledPieces);
        addPieceEntry(settings, "L Piece", enabledPieces);
        addPieceEntry(settings, "O Piece", enabledPieces);
        addPieceEntry(settings, "S Piece", enabledPieces);
        addPieceEntry(settings, "T Piece", enabledPieces);
        addPieceEntry(settings, "Z Piece", enabledPieces);

        // Collect all enabled custom pieces
        Map<String, boolean[][]> customPieces = settings.getAllCustomPieces();
        for (Map.Entry<String, boolean[][]> entry : customPieces.entrySet()) {
            String name = entry.getKey();

            // Skip standard pieces as they are added explicitly above
            if (settings.isStandardPiece(name)) {
                continue;
            }

            boolean[][] design = entry.getValue();

            // Only add non-empty designs
            if (!isEmptyDesign(design)) {
                addPieceEntry(settings, name, enabledPieces);
            }
        }

        // If no pieces are enabled, add at least one standard piece to prevent crash
        if (enabledPieces.isEmpty()) {
            currentBag.add(createBrick("I Piece", settings));
        } else {
            // Add each piece to the bag 'spawnRate' times
            for (BrickEntry entry : enabledPieces) {
                // Ensure at least 1 copy if rate > 0 (though addPieceEntry checks > 0)
                int count = Math.max(1, entry.spawnRate);
                for (int i = 0; i < count; i++) {
                    currentBag.add(createBrick(entry.name, settings));
                }
            }
        }

        Collections.shuffle(currentBag, random);
    }

    /**
     * Helper class to store piece information before creating brick instances
     */
    private static class BrickEntry {
        String name;
        int spawnRate;

        BrickEntry(String name, int spawnRate) {
            this.name = name;
            this.spawnRate = spawnRate;
        }
    }

    /**
     * Adds piece to the list if enabled in game
     */
    private void addPieceEntry(GameSettings settings, String name, List<BrickEntry> list) {
        GameSettings.PieceSettings ps = settings.getPieceSettings(name);
        if (ps != null && ps.enableInGame && ps.spawnRate > 0) {
            list.add(new BrickEntry(name, ps.spawnRate));
        }
    }

    /**
     * Creates a new brick instance based on piece name
     * Always creates NEW instances to avoid reference issues
     */
    private Brick createBrick(String name, GameSettings settings) {
        switch (name) {
            case "I Piece":
                return new IBrick();
            case "J Piece":
                return new JBrick();
            case "L Piece":
                return new LBrick();
            case "O Piece":
                return new OBrick();
            case "S Piece":
                return new SBrick();
            case "T Piece":
                return new TBrick();
            case "Z Piece":
                return new ZBrick();
            default:
                // Custom piece
                boolean[][] design = settings.getCustomPiece(name);
                GameSettings.PieceSettings ps = settings.getPieceSettings(name);
                if (design != null && ps != null) {
                    return new CustomBrick(design, ps.id);
                }
                // Fallback to I piece if something goes wrong
                return new IBrick();
        }
    }

    /**
     * Checks if a design is empty (no filled cells)
     */
    private boolean isEmptyDesign(boolean[][] design) {
        if (design == null)
            return true;
        for (boolean[] row : design) {
            if (row != null) {
                for (boolean cell : row) {
                    if (cell)
                        return false;
                }
            }
        }
        return true;
    }

    // Ensures we always have at least 2 pieces in the queue
    private void fillQueue() {
        while (nextBricks.size() < 2) {
            if (currentBag.isEmpty()) {
                refillBag(); // Regenerate bag with current settings
            }
            if (!currentBag.isEmpty()) {
                nextBricks.add(currentBag.remove(0));
            } else {
                // Emergency fallback - should never happen
                nextBricks.add(new IBrick());
            }
        }
    }

    @Override
    public Brick getBrick() {
        fillQueue();
        Brick brick = nextBricks.poll();
        return brick != null ? brick : new IBrick();
    }

    @Override
    public Brick getNextBrick() {
        fillQueue();
        Brick brick = nextBricks.peek();
        return brick != null ? brick : new IBrick();
    }
}