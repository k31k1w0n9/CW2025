package com.comp2042.system;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages all audio playback for the game.
 * This singleton class handles background music looping, sound effects (SFX)
 * triggering,
 * and volume control. It integrates with {@link SettingsManager} to respect
 * user
 * preferences for muting and volume levels.
 */
public class SoundManager {
    /** Singleton instance. */
    private static SoundManager instance;

    /** Media player for background music. */
    private MediaPlayer musicPlayer;

    /** Cache of loaded sound effects. */
    private final Map<String, AudioClip> soundCache = new HashMap<>();

    /** Reference to settings manager for volume preferences. */
    private SettingsManager settingsManager;

    /** Path to background music file. */
    public static final String BGM = "/bgm.mp3";

    /** Path to game start sound effect. */
    public static final String SFX_GAME_START = "/SFX/game-start.mp3";

    /** Path to button click sound effect. */
    public static final String SFX_BTN_CLICK = "/SFX/btn-click.mp3";

    /** Path to hard drop sound effect. */
    public static final String SFX_HARD_DROP = "/SFX/hard-drop.mp3";

    /** Path to hold piece sound effect. */
    public static final String SFX_HOLD = "/SFX/hold.mp3";

    /** Path to piece landing sound effect. */
    public static final String SFX_LANDING = "/SFX/landing.mp3";

    /** Path to rotation sound effect. */
    public static final String SFX_ROTATE = "/SFX/rotate.mp3";

    /** Path to single line clear sound effect. */
    public static final String SFX_LINE_SINGLE = "/SFX/line-single.mp3";

    /** Path to double line clear sound effect. */
    public static final String SFX_LINE_DOUBLE = "/SFX/line-double.mp3";

    /** Path to triple line clear sound effect. */
    public static final String SFX_LINE_TRIPLE = "/SFX/line-triple.mp3";

    /** Path to Tetris (4-line) clear sound effect. */
    public static final String SFX_LINE_TETRIS = "/SFX/line-tetris.mp3";

    /** Path to piece movement sound effect. */
    public static final String SFX_MOVE = "/SFX/move.mp3";

    /** Path to level up sound effect. */
    public static final String SFX_LEVEL_UP = "/SFX/level-up.mp3";

    /** Path to game over sound effect. */
    public static final String SFX_GAME_OVER = "/SFX/game-over.mp3";

    /**
     * Private constructor for singleton pattern.
     */
    private SoundManager() {
        settingsManager = SettingsManager.getInstance();
    }

    /**
     * Returns the singleton instance of SoundManager.
     *
     * @return the SoundManager instance
     */
    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    /**
     * Starts playing the background music loop.
     * Does nothing if music is disabled in settings. If the music player is not
     * yet initialized, it attempts to load the BGM file first.
     */
    public void playMusic() {
        if (!settingsManager.isMusicEnabled())
            return;

        try {
            if (musicPlayer == null) {
                URL resource = getClass().getResource(BGM);
                if (resource != null) {
                    Media media = new Media(resource.toExternalForm());
                    musicPlayer = new MediaPlayer(media);
                    musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                } else {
                    System.err.println("Could not find BGM file: " + BGM);
                    return;
                }
            }

            musicPlayer.setVolume(settingsManager.getMusicVolume());
            musicPlayer.play();
        } catch (Exception e) {
            System.err.println("Error playing music: " + e.getMessage());
        }
    }

    /**
     * Stops the background music.
     */
    public void stopMusic() {
        if (musicPlayer != null) {
            musicPlayer.stop();
        }
    }

    /**
     * Pauses the background music.
     */
    public void pauseMusic() {
        if (musicPlayer != null) {
            musicPlayer.pause();
        }
    }

    /**
     * Resumes the background music if music is enabled.
     */
    public void resumeMusic() {
        if (settingsManager.isMusicEnabled() && musicPlayer != null) {
            musicPlayer.play();
        }
    }

    /**
     * Plays a specific sound effect one time.
     * Checks if SFX is enabled before playing. Uses a caching mechanism to
     * avoid reloading the same sound file multiple times, ensuring low latency.
     *
     * @param soundPath the resource path to the sound file (e.g., "/SFX/click.mp3")
     */
    public void playSound(String soundPath) {
        if (!settingsManager.isSfxEnabled())
            return;

        try {
            AudioClip clip = soundCache.get(soundPath);
            if (clip == null) {
                URL resource = getClass().getResource(soundPath);
                if (resource != null) {
                    clip = new AudioClip(resource.toExternalForm());
                    soundCache.put(soundPath, clip);
                } else {
                    System.err.println("Could not find SFX file: " + soundPath);
                    return;
                }
            }

            clip.setVolume(settingsManager.getSfxVolume());
            clip.play();
        } catch (Exception e) {
            System.err.println("Error playing sound: " + soundPath + " - " + e.getMessage());
        }
    }

    /**
     * Updates the music volume.
     *
     * @param volume the new volume level (0.0 to 1.0)
     */
    public void updateMusicVolume(double volume) {
        if (musicPlayer != null) {
            musicPlayer.setVolume(volume);
        }
    }

    /**
     * Updates the SFX volume for future sound plays.
     *
     * @param volume the new volume level (0.0 to 1.0)
     */
    public void updateSfxVolume(double volume) {
        // Volume is applied on each playSound call
    }

    /**
     * Handles music enable/disable changes.
     *
     * @param enabled true to enable music, false to disable
     */
    public void onMusicEnabledChanged(boolean enabled) {
        if (enabled) {
            playMusic();
        } else {
            stopMusic();
        }
    }
}
