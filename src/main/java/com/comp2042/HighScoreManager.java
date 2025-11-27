package com.comp2042;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HighScoreManager {
    private static final String HIGH_SCORE_FILE = "highscores.dat";
    private static final int MAX_HIGH_SCORES = 5;
    private List<HighScoreEntry> highScores;
    private final String currentFile;

    public HighScoreManager() {
        this(HIGH_SCORE_FILE);
    }

    public HighScoreManager(String fileName) {
        this.currentFile = fileName;
        highScores = new ArrayList<>();
        loadHighScores();
    }

    public static class HighScoreEntry implements Serializable, Comparable<HighScoreEntry> {
        private static final long serialVersionUID = 1L;
        private String name;
        private int score;

        public HighScoreEntry(String name, int score) {
            this.name = name;
            this.score = score;
        }

        public String getName() {
            return name;
        }

        public int getScore() {
            return score;
        }

        @Override
        public int compareTo(HighScoreEntry other) {
            return Integer.compare(other.score, this.score); // Descending order
        }
    }

    private void loadHighScores() {
        File file = new File(currentFile);
        if (!file.exists()) {
            // Initialize with default scores
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
            // If loading fails, use default scores
            highScores.clear();
            highScores.add(new HighScoreEntry("", 500));
            highScores.add(new HighScoreEntry("", 400));
            highScores.add(new HighScoreEntry("", 300));
            highScores.add(new HighScoreEntry("", 200));
            highScores.add(new HighScoreEntry("", 100));
        }
    }

    private void saveHighScores() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(currentFile))) {
            oos.writeObject(highScores);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isHighScore(int score) {
        if (highScores.size() < MAX_HIGH_SCORES) {
            return true;
        }
        return score > highScores.get(highScores.size() - 1).getScore();
    }

    public int addHighScore(String name, int score) {
        HighScoreEntry newEntry = new HighScoreEntry(name, score);
        highScores.add(newEntry);
        Collections.sort(highScores);

        // Keep only top MAX_HIGH_SCORES
        while (highScores.size() > MAX_HIGH_SCORES) {
            highScores.remove(highScores.size() - 1);
        }

        saveHighScores();

        // Return the rank (1-based index)
        return highScores.indexOf(newEntry) + 1;
    }

    public List<HighScoreEntry> getHighScores() {
        return new ArrayList<>(highScores);
    }

    public int getHighScore() {
        if (highScores.isEmpty()) {
            return 0;
        }
        return highScores.get(0).getScore();
    }
}