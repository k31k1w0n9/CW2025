package com.comp2042.domain;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Random;
import com.comp2042.domain.bricks.*;
import com.comp2042.system.GameSettings;

/**
 * Core domain implementation of a 7-bag randomiser for generating Tetromino
 * pieces.
 * Located in the domain package as it encapsulates core game rules for piece
 * generation.
 * Uses the official Tetris Guideline algorithm where each of the 7 standard
 * pieces appears exactly once per bag before reshuffling.
 * Also supports custom pieces with configurable spawn rates.
 */
public class RandomBrickGenerator implements BrickGenerator {

    /** Queue of upcoming bricks for preview. */
    private final Deque<Brick> nextBricks = new ArrayDeque<>();

    /** Current bag of bricks to draw from. */
    private final List<Brick> currentBag = new ArrayList<>();

    /** Random number generator for shuffling. */
    private final Random random = new Random();

    /** Names of the 7 standard Tetromino pieces. */
    private final String[] standardNames = { "I Piece", "J Piece", "L Piece", "O Piece", "S Piece", "T Piece",
            "Z Piece" };

    /**
     * Constructs a new RandomBrickGenerator and initialises the first bag.
     */
    public RandomBrickGenerator() {
        refillBag();
        fillQueue();
    }

    /**
     * Resets the generator to its initial state for a new game.
     */
    @Override
    public void reset() {
        nextBricks.clear();
        currentBag.clear();
        refillBag();
        fillQueue();
    }

    /**
     * Refills the bag using a weighted randomiser.
     * Adds one of each enabled standard piece and custom pieces based on spawn
     * rates.
     */
    private void refillBag() {
        currentBag.clear();
        GameSettings settings = GameSettings.getInstance();

        for (String name : standardNames) {
            if (isPieceEnabled(settings, name)) {
                currentBag.add(createStandardBrick(name));
            }
        }

        Map<String, boolean[][]> customPieces = settings.getAllCustomPieces();

        for (Map.Entry<String, boolean[][]> entry : customPieces.entrySet()) {
            String name = entry.getKey();

            if (isStandardPiece(name)) {
                continue;
            }

            if (isPieceEnabled(settings, name)) {
                boolean[][] design = entry.getValue();

                if (isEmptyDesign(design)) {
                    continue;
                }

                GameSettings.PieceSettings pieceSettings = settings.getPieceSettings(name);

                int spawnRate = pieceSettings != null ? pieceSettings.spawnRate : 5;
                int id = pieceSettings != null ? pieceSettings.id : 8;

                int copiesToAdd = calculateCopiesToAdd(spawnRate);

                for (int k = 0; k < copiesToAdd; k++) {
                    currentBag.add(new CustomBrick(design, id));
                }
            }
        }

        if (currentBag.isEmpty()) {
            currentBag.add(new IBrick());
        }

        Collections.shuffle(currentBag, random);
    }

    /**
     * Calculates the number of copies to add based on spawn rate.
     *
     * @param spawnRate the spawn rate (0-10)
     * @return the number of copies to add to the bag
     */
    private int calculateCopiesToAdd(int spawnRate) {
        if (spawnRate == 0) {
            return 0;
        } else if (spawnRate <= 3) {
            return random.nextDouble() < 0.33 ? 1 : 0;
        } else if (spawnRate <= 6) {
            return 1;
        } else if (spawnRate <= 9) {
            return 2;
        } else {
            return 3;
        }
    }

    /**
     * Checks if a design grid is empty (all false values).
     *
     * @param design the boolean design grid
     * @return true if the design is empty, false otherwise
     */
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

    /**
     * Checks if a piece name is a standard Tetromino.
     *
     * @param name the piece name
     * @return true if it's a standard piece, false otherwise
     */
    private boolean isStandardPiece(String name) {
        for (String s : standardNames) {
            if (s.equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a piece is enabled in the game settings.
     *
     * @param settings the game settings
     * @param name     the piece name
     * @return true if the piece is enabled, false otherwise
     */
    private boolean isPieceEnabled(GameSettings settings, String name) {
        GameSettings.PieceSettings ps = settings.getPieceSettings(name);
        return ps != null && ps.enableInGame;
    }

    /**
     * Creates a standard brick instance by name.
     *
     * @param name the piece name
     * @return the corresponding Brick instance
     */
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

    /**
     * Ensures the queue always has at least 2 pieces for preview.
     */
    private void fillQueue() {
        while (nextBricks.size() < 2) {
            if (currentBag.isEmpty()) {
                refillBag();
            }
            if (!currentBag.isEmpty()) {
                nextBricks.add(currentBag.remove(0));
            } else {
                nextBricks.add(new IBrick());
            }
        }
    }

    /**
     * Returns the next brick and advances the queue.
     *
     * @return the next Brick to use
     */
    @Override
    public Brick getBrick() {
        fillQueue();
        Brick brick = nextBricks.poll();
        return brick != null ? brick : new IBrick();
    }

    /**
     * Returns the next brick without advancing the queue.
     *
     * @return the upcoming Brick (preview)
     */
    @Override
    public Brick getNextBrick() {
        fillQueue();
        Brick brick = nextBricks.peek();
        return brick != null ? brick : new IBrick();
    }
}