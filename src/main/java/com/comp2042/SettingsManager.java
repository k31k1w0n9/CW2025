package com.comp2042;

import java.io.*;
import java.util.Properties;

/**
 * Manages game settings including audio, visual preferences, and gameplay
 * options.
 * Settings are persisted to a file for persistence across sessions.
 */
public class SettingsManager {
    private static SettingsManager instance;
    private Properties settings;
    private static final String SETTINGS_FILE = "settings.dat";

    // Setting keys
    public static final String MUSIC_ENABLED = "music.enabled";
    public static final String MUSIC_VOLUME = "music.volume";
    public static final String SFX_ENABLED = "sfx.enabled";
    public static final String SFX_VOLUME = "sfx.volume";
    public static final String GHOST_PIECE_ENABLED = "ghost.enabled";
    public static final String GRID_LINES_ENABLED = "grid.enabled";

    // Default values
    private static final boolean DEFAULT_MUSIC_ENABLED = true;
    private static final double DEFAULT_MUSIC_VOLUME = 0.5;
    private static final boolean DEFAULT_SFX_ENABLED = true;
    private static final double DEFAULT_SFX_VOLUME = 0.7;
    private static final boolean DEFAULT_GHOST_ENABLED = true;
    private static final boolean DEFAULT_GRID_ENABLED = true;

    private SettingsManager() {
        settings = new Properties();
        loadSettings();
    }

    public static SettingsManager getInstance() {
        if (instance == null) {
            instance = new SettingsManager();
        }
        return instance;
    }

    private void loadSettings() {
        File file = new File(SETTINGS_FILE);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                settings.load(fis);
                System.out.println("[INFO] Settings loaded successfully");
            } catch (IOException e) {
                System.err.println("[WARNING] Could not load settings, using defaults");
                setDefaults();
            }
        } else {
            System.out.println("[INFO] No settings file found, using defaults");
            setDefaults();
        }
    }

    private void setDefaults() {
        settings.setProperty(MUSIC_ENABLED, String.valueOf(DEFAULT_MUSIC_ENABLED));
        settings.setProperty(MUSIC_VOLUME, String.valueOf(DEFAULT_MUSIC_VOLUME));
        settings.setProperty(SFX_ENABLED, String.valueOf(DEFAULT_SFX_ENABLED));
        settings.setProperty(SFX_VOLUME, String.valueOf(DEFAULT_SFX_VOLUME));
        settings.setProperty(GHOST_PIECE_ENABLED, String.valueOf(DEFAULT_GHOST_ENABLED));
        settings.setProperty(GRID_LINES_ENABLED, String.valueOf(DEFAULT_GRID_ENABLED));
        saveSettings();
    }

    public void saveSettings() {
        try (FileOutputStream fos = new FileOutputStream(SETTINGS_FILE)) {
            settings.store(fos, "Tetris Game Settings");
            System.out.println("[INFO] Settings saved successfully");
        } catch (IOException e) {
            System.err.println("[ERROR] Could not save settings");
            e.printStackTrace();
        }
    }

    // Getters
    public boolean isMusicEnabled() {
        return Boolean.parseBoolean(settings.getProperty(MUSIC_ENABLED, String.valueOf(DEFAULT_MUSIC_ENABLED)));
    }

    public double getMusicVolume() {
        return Double.parseDouble(settings.getProperty(MUSIC_VOLUME, String.valueOf(DEFAULT_MUSIC_VOLUME)));
    }

    public boolean isSfxEnabled() {
        return Boolean.parseBoolean(settings.getProperty(SFX_ENABLED, String.valueOf(DEFAULT_SFX_ENABLED)));
    }

    public double getSfxVolume() {
        return Double.parseDouble(settings.getProperty(SFX_VOLUME, String.valueOf(DEFAULT_SFX_VOLUME)));
    }

    public boolean isGhostPieceEnabled() {
        return Boolean.parseBoolean(settings.getProperty(GHOST_PIECE_ENABLED, String.valueOf(DEFAULT_GHOST_ENABLED)));
    }

    public boolean isGridLinesEnabled() {
        return Boolean.parseBoolean(settings.getProperty(GRID_LINES_ENABLED, String.valueOf(DEFAULT_GRID_ENABLED)));
    }

    // Setters
    public void setMusicEnabled(boolean enabled) {
        settings.setProperty(MUSIC_ENABLED, String.valueOf(enabled));
        saveSettings();
    }

    public void setMusicVolume(double volume) {
        settings.setProperty(MUSIC_VOLUME, String.valueOf(Math.max(0.0, Math.min(1.0, volume))));
        saveSettings();
    }

    public void setSfxEnabled(boolean enabled) {
        settings.setProperty(SFX_ENABLED, String.valueOf(enabled));
        saveSettings();
    }

    public void setSfxVolume(double volume) {
        settings.setProperty(SFX_VOLUME, String.valueOf(Math.max(0.0, Math.min(1.0, volume))));
        saveSettings();
    }

    public void setGhostPieceEnabled(boolean enabled) {
        settings.setProperty(GHOST_PIECE_ENABLED, String.valueOf(enabled));
        saveSettings();
    }

    public void setGridLinesEnabled(boolean enabled) {
        settings.setProperty(GRID_LINES_ENABLED, String.valueOf(enabled));
        saveSettings();
    }

    public void resetToDefaults() {
        setDefaults();
    }

    public void resetAudioToDefaults() {
        settings.setProperty(MUSIC_ENABLED, String.valueOf(DEFAULT_MUSIC_ENABLED));
        settings.setProperty(MUSIC_VOLUME, String.valueOf(DEFAULT_MUSIC_VOLUME));
        settings.setProperty(SFX_ENABLED, String.valueOf(DEFAULT_SFX_ENABLED));
        settings.setProperty(SFX_VOLUME, String.valueOf(DEFAULT_SFX_VOLUME));
        saveSettings();
    }

    public void resetVisualToDefaults() {
        settings.setProperty(GHOST_PIECE_ENABLED, String.valueOf(DEFAULT_GHOST_ENABLED));
        settings.setProperty(GRID_LINES_ENABLED, String.valueOf(DEFAULT_GRID_ENABLED));
        saveSettings();
    }
}
