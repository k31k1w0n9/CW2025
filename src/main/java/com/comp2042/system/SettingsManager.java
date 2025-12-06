package com.comp2042.system;

import java.io.*;
import java.util.Properties;

/**
 * Singleton manager for game settings including audio, visual preferences,
 * and gameplay options. Settings are persisted to a file for persistence
 * across game sessions.
 */
public class SettingsManager {
    /** Singleton instance. */
    private static SettingsManager instance;

    /** Properties object storing all settings. */
    private Properties settings;

    /** File path for persisting settings. */
    private static final String SETTINGS_FILE = "settings.dat";

    /** Setting key for music enabled state. */
    public static final String MUSIC_ENABLED = "music.enabled";

    /** Setting key for music volume level. */
    public static final String MUSIC_VOLUME = "music.volume";

    /** Setting key for SFX enabled state. */
    public static final String SFX_ENABLED = "sfx.enabled";

    /** Setting key for SFX volume level. */
    public static final String SFX_VOLUME = "sfx.volume";

    /** Setting key for ghost piece visibility. */
    public static final String GHOST_PIECE_ENABLED = "ghost.enabled";

    /** Setting key for grid lines visibility. */
    public static final String GRID_LINES_ENABLED = "grid.enabled";

    /** Default value for music enabled. */
    private static final boolean DEFAULT_MUSIC_ENABLED = true;

    /** Default value for music volume. */
    private static final double DEFAULT_MUSIC_VOLUME = 0.5;

    /** Default value for SFX enabled. */
    private static final boolean DEFAULT_SFX_ENABLED = true;

    /** Default value for SFX volume. */
    private static final double DEFAULT_SFX_VOLUME = 0.7;

    /** Default value for ghost piece visibility. */
    private static final boolean DEFAULT_GHOST_ENABLED = true;

    /** Default value for grid lines visibility. */
    private static final boolean DEFAULT_GRID_ENABLED = true;

    /**
     * Private constructor for singleton pattern.
     * Loads settings from file or uses defaults.
     */
    private SettingsManager() {
        settings = new Properties();
        loadSettings();
    }

    /**
     * Returns the singleton instance of SettingsManager.
     *
     * @return the SettingsManager instance
     */
    public static SettingsManager getInstance() {
        if (instance == null) {
            instance = new SettingsManager();
        }
        return instance;
    }

    /**
     * Loads settings from the settings file.
     * Uses defaults if the file doesn't exist or cannot be read.
     */
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

    /**
     * Sets all settings to their default values.
     */
    private void setDefaults() {
        settings.setProperty(MUSIC_ENABLED, String.valueOf(DEFAULT_MUSIC_ENABLED));
        settings.setProperty(MUSIC_VOLUME, String.valueOf(DEFAULT_MUSIC_VOLUME));
        settings.setProperty(SFX_ENABLED, String.valueOf(DEFAULT_SFX_ENABLED));
        settings.setProperty(SFX_VOLUME, String.valueOf(DEFAULT_SFX_VOLUME));
        settings.setProperty(GHOST_PIECE_ENABLED, String.valueOf(DEFAULT_GHOST_ENABLED));
        settings.setProperty(GRID_LINES_ENABLED, String.valueOf(DEFAULT_GRID_ENABLED));
        saveSettings();
    }

    /**
     * Saves current settings to the settings file.
     */
    public void saveSettings() {
        try (FileOutputStream fos = new FileOutputStream(SETTINGS_FILE)) {
            settings.store(fos, "Tetris Game Settings");
            System.out.println("[INFO] Settings saved successfully");
        } catch (IOException e) {
            System.err.println("[ERROR] Could not save settings");
            e.printStackTrace();
        }
    }

    /**
     * Returns whether background music is enabled.
     *
     * @return true if music is enabled, false otherwise
     */
    public boolean isMusicEnabled() {
        return Boolean.parseBoolean(settings.getProperty(MUSIC_ENABLED, String.valueOf(DEFAULT_MUSIC_ENABLED)));
    }

    /**
     * Returns the current music volume level.
     *
     * @return the music volume (0.0 to 1.0)
     */
    public double getMusicVolume() {
        return Double.parseDouble(settings.getProperty(MUSIC_VOLUME, String.valueOf(DEFAULT_MUSIC_VOLUME)));
    }

    /**
     * Returns whether sound effects are enabled.
     *
     * @return true if SFX are enabled, false otherwise
     */
    public boolean isSfxEnabled() {
        return Boolean.parseBoolean(settings.getProperty(SFX_ENABLED, String.valueOf(DEFAULT_SFX_ENABLED)));
    }

    /**
     * Returns the current SFX volume level.
     *
     * @return the SFX volume (0.0 to 1.0)
     */
    public double getSfxVolume() {
        return Double.parseDouble(settings.getProperty(SFX_VOLUME, String.valueOf(DEFAULT_SFX_VOLUME)));
    }

    /**
     * Returns whether the ghost piece is visible.
     *
     * @return true if ghost piece is enabled, false otherwise
     */
    public boolean isGhostPieceEnabled() {
        return Boolean.parseBoolean(settings.getProperty(GHOST_PIECE_ENABLED, String.valueOf(DEFAULT_GHOST_ENABLED)));
    }

    /**
     * Returns whether grid lines are visible.
     *
     * @return true if grid lines are enabled, false otherwise
     */
    public boolean isGridLinesEnabled() {
        return Boolean.parseBoolean(settings.getProperty(GRID_LINES_ENABLED, String.valueOf(DEFAULT_GRID_ENABLED)));
    }

    /**
     * Sets whether background music is enabled.
     *
     * @param enabled true to enable music, false to disable
     */
    public void setMusicEnabled(boolean enabled) {
        settings.setProperty(MUSIC_ENABLED, String.valueOf(enabled));
        saveSettings();
    }

    /**
     * Sets the music volume level.
     *
     * @param volume the volume level (0.0 to 1.0)
     */
    public void setMusicVolume(double volume) {
        settings.setProperty(MUSIC_VOLUME, String.valueOf(Math.max(0.0, Math.min(1.0, volume))));
        saveSettings();
    }

    /**
     * Sets whether sound effects are enabled.
     *
     * @param enabled true to enable SFX, false to disable
     */
    public void setSfxEnabled(boolean enabled) {
        settings.setProperty(SFX_ENABLED, String.valueOf(enabled));
        saveSettings();
    }

    /**
     * Sets the SFX volume level.
     *
     * @param volume the volume level (0.0 to 1.0)
     */
    public void setSfxVolume(double volume) {
        settings.setProperty(SFX_VOLUME, String.valueOf(Math.max(0.0, Math.min(1.0, volume))));
        saveSettings();
    }

    /**
     * Sets whether the ghost piece is visible.
     *
     * @param enabled true to show ghost piece, false to hide
     */
    public void setGhostPieceEnabled(boolean enabled) {
        settings.setProperty(GHOST_PIECE_ENABLED, String.valueOf(enabled));
        saveSettings();
    }

    /**
     * Sets whether grid lines are visible.
     *
     * @param enabled true to show grid lines, false to hide
     */
    public void setGridLinesEnabled(boolean enabled) {
        settings.setProperty(GRID_LINES_ENABLED, String.valueOf(enabled));
        saveSettings();
    }

    /**
     * Resets all settings to their default values.
     * <p>
     * Immediately persists these changes to the settings file to ensure consistency
     * across application restarts.
     */
    public void resetToDefaults() {
        setDefaults();
    }

    /**
     * Resets only audio settings to their default values.
     */
    public void resetAudioToDefaults() {
        settings.setProperty(MUSIC_ENABLED, String.valueOf(DEFAULT_MUSIC_ENABLED));
        settings.setProperty(MUSIC_VOLUME, String.valueOf(DEFAULT_MUSIC_VOLUME));
        settings.setProperty(SFX_ENABLED, String.valueOf(DEFAULT_SFX_ENABLED));
        settings.setProperty(SFX_VOLUME, String.valueOf(DEFAULT_SFX_VOLUME));
        saveSettings();
    }

    /**
     * Resets only visual settings to their default values.
     */
    public void resetVisualToDefaults() {
        settings.setProperty(GHOST_PIECE_ENABLED, String.valueOf(DEFAULT_GHOST_ENABLED));
        settings.setProperty(GRID_LINES_ENABLED, String.valueOf(DEFAULT_GRID_ENABLED));
        saveSettings();
    }
}
