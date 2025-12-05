package com.comp2042.system;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages high scores for the Tetris game.
 * Stores the top scores and persists them to a file.
 */
public class HighScoreManager {
    /** Default file name for storing high scores. */
    private static final String HIGH_SCORE_FILE = "highscores.dat";

    /** Maximum number of high scores to retain. */
    private static final int MAX_HIGH_SCORES = 5;

    /** List of high score entries. */
    private List<HighScoreEntry> highScores;

    /** The file currently being used for storage. */
    private final String currentFile;

    /**
     * Constructs a HighScoreManager using the default file location.
     */
    public HighScoreManager() {
        this(HIGH_SCORE_FILE);
    }

    /**
     * Constructs a HighScoreManager using a specified file.
     *
     * @param fileName the file path for storing high scores
     */
    public HighScoreManager(String fileName) {
        this.currentFile = fileName;
        highScores = new ArrayList<>();
        loadHighScores();
    }

    /**
     * Represents a single high score entry with player name and score.
     * Implements Serializable for file persistence and Comparable for sorting.
     */
    public static class HighScoreEntry implements Serializable, Comparable<HighScoreEntry> {
        private static final long serialVersionUID = 1L;
        private String name;
        private int score;

        /**
         * Constructs a new high score entry.
         *
         * @param name  the player's name
         * @param score the score achieved
         */
        public HighScoreEntry(String name, int score) {
            this.name = name;
            this.score = score;
        }

        /**
         * Returns the player's name.
         *
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * Returns the score.
         *
         * @return the score value
         */
        public int getScore() {
            return score;
        }

        /**
         * Compares this entry to another for sorting (descending by score).
         *
         * @param other the other entry to compare to
         * @return comparison result
         */
        @Override
        public int compareTo(HighScoreEntry other) {
            return Integer.compare(other.score, this.score);
        }
    }

    /**
     * Loads high scores from the storage file.
     * Uses default placeholder scores if the file doesn't exist.
     */
    @SuppressWarnings("unchecked")
    private void loadHighScores() {
        File file = new File(currentFile);
        if (!file.exists()) {
            highScores.add(new HighScoreEntry("", 500));
            highScores.add(new HighScoreEntry("", 400));
            highScores.add(new HighScoreEntry("", 300));
            highScores.add(new HighScoreEntry("", 200));
            highScores.add(new HighScoreEntry("", 100));
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            highScores = (List<HighScoreEntry>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            highScores.clear();
            highScores.add(new HighScoreEntry("", 500));
            highScores.add(new HighScoreEntry("", 400));
            highScores.add(new HighScoreEntry("", 300));
            highScores.add(new HighScoreEntry("", 200));
            highScores.add(new HighScoreEntry("", 100));
        }
    }

    /**
     * Saves high scores to the storage file.
     */
    private void saveHighScores() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(currentFile))) {
            oos.writeObject(highScores);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if a score qualifies for the high score list.
     *
     * @param score the score to check
     * @return true if the score is high enough to be added
     */
    public boolean isHighScore(int score) {
        if (highScores.size() < MAX_HIGH_SCORES) {
            return true;
        }
        return score > highScores.get(highScores.size() - 1).getScore();
    }

    /**
     * Adds a new high score to the list.
     *
     * @param name  the player's name
     * @param score the score achieved
     * @return the rank (1-based) of the new entry
     */
    public int addHighScore(String name, int score) {
        HighScoreEntry newEntry = new HighScoreEntry(name, score);
        highScores.add(newEntry);
        Collections.sort(highScores);

        while (highScores.size() > MAX_HIGH_SCORES) {
            highScores.remove(highScores.size() - 1);
        }

        saveHighScores();
        return highScores.indexOf(newEntry) + 1;
    }

    /**
     * Returns a copy of the high scores list.
     *
     * @return list of high score entries
     */
    public List<HighScoreEntry> getHighScores() {
        return new ArrayList<>(highScores);
    }

    /**
     * Returns the highest score achieved.
     *
     * @return the top score, or 0 if no scores exist
     */
    public int getHighScore() {
        if (highScores.isEmpty()) {
            return 0;
        }
        return highScores.get(0).getScore();
    }
}