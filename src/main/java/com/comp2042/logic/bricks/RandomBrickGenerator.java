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

    private final String[] standardNames = { "I Piece", "J Piece", "L Piece", "O Piece", "S Piece", "T Piece",
            "Z Piece" };

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
     * 1. Always add one of each enabled Standard Piece.
     * 2. Add Custom Pieces based on their spawn rate settings.
     * 3. Shuffle the combined bag.
     */
    private void refillBag() {
        currentBag.clear();
        GameSettings settings = GameSettings.getInstance();

        // 1. Add ONE of each Enabled Standard Piece (Standard 7-Bag Rule)
        for (String name : standardNames) {
            // Only add if the user hasn't disabled this standard piece
            if (isPieceEnabled(settings, name)) {
                currentBag.add(createStandardBrick(name));
            }
        }

        // 2. Add Custom Pieces based on Spawn Rate
        Map<String, boolean[][]> customPieces = settings.getAllCustomPieces();

        for (Map.Entry<String, boolean[][]> entry : customPieces.entrySet()) {
            String name = entry.getKey();

            // Skip standard pieces in this loop (they are handled in Step 1)
            if (isStandardPiece(name)) {
                continue;
            }

            // Only proceed if this custom piece is enabled
            if (isPieceEnabled(settings, name)) {
                boolean[][] design = entry.getValue();

                // Skip empty designs (all false) to prevent "phantom" bricks
                if (isEmptyDesign(design)) {
                    continue;
                }

                GameSettings.PieceSettings pieceSettings = settings.getPieceSettings(name);

                int spawnRate = pieceSettings != null ? pieceSettings.spawnRate : 5;
                int id = pieceSettings != null ? pieceSettings.id : 8;

                // LOGIC: How many times should a custom piece appear per "Bag"?
                // Rate 0: Never (0 copies)
                // Rate 1-3: 33% chance (handled by adding 1 copy with probability)
                // Rate 4-6: 1 copy (Standard frequency)
                // Rate 7-9: 2 copies (Double frequency)
                // Rate 10: 3 copies (High frequency)

                int copiesToAdd = 0;

                if (spawnRate == 0) {
                    copiesToAdd = 0;
                } else if (spawnRate <= 3) {
                    // Low spawn rate: Only add it to THIS bag 33% of the time
                    if (random.nextDouble() < 0.33)
                        copiesToAdd = 1;
                } else if (spawnRate <= 6) {
                    copiesToAdd = 1; // Same frequency as a standard piece
                } else if (spawnRate <= 9) {
                    copiesToAdd = 2;
                } else {
                    copiesToAdd = 3;
                }

                for (int k = 0; k < copiesToAdd; k++) {
                    currentBag.add(new CustomBrick(design, id));
                }
            }
        }

        // Safety check: If user disabled EVERYTHING or bag is empty, add a default
        // I-Piece
        if (currentBag.isEmpty()) {
            currentBag.add(new IBrick());
        }

        // 3. Shuffle the final bag
        Collections.shuffle(currentBag, random);
    }

    private boolean isEmptyDesign(boolean[][] design) {
        if (design == null)
            return true;
        for (boolean[] row : design) {
            for (boolean cell : row) {
                if (cell)
                    return false;
            }
        }
        return true;
    }

    private boolean isStandardPiece(String name) {
        for (String s : standardNames) {
            if (s.equals(name)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPieceEnabled(GameSettings settings, String name) {
        GameSettings.PieceSettings ps = settings.getPieceSettings(name);
        return ps != null && ps.enableInGame;
    }

    private Brick createStandardBrick(String name) {
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
                return new IBrick();
        }
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