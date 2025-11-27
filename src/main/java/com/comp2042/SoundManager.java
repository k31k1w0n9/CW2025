package com.comp2042;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    private static SoundManager instance;
    private MediaPlayer musicPlayer;
    private final Map<String, AudioClip> soundCache = new HashMap<>();
    private SettingsManager settingsManager;

    // Sound constants
    public static final String BGM = "/bgm.mp3";
    public static final String SFX_GAME_START = "/SFX/game-start.mp3";
    public static final String SFX_BTN_CLICK = "/SFX/btn-click.mp3";
    public static final String SFX_HARD_DROP = "/SFX/hard-drop.mp3";
    public static final String SFX_HOLD = "/SFX/hold.mp3";
    public static final String SFX_LANDING = "/SFX/landing.mp3";
    public static final String SFX_ROTATE = "/SFX/rotate.mp3";
    public static final String SFX_LINE_SINGLE = "/SFX/line-single.mp3";
    public static final String SFX_LINE_DOUBLE = "/SFX/line-double.mp3";
    public static final String SFX_LINE_TRIPLE = "/SFX/line-triple.mp3";
    public static final String SFX_LINE_TETRIS = "/SFX/line-tetris.mp3";
    public static final String SFX_MOVE = "/SFX/move.mp3";
    public static final String SFX_LEVEL_UP = "/SFX/level-up.mp3";
    public static final String SFX_GAME_OVER = "/SFX/game-over.mp3";

    private SoundManager() {
        settingsManager = SettingsManager.getInstance();
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

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

    public void stopMusic() {
        if (musicPlayer != null) {
            musicPlayer.stop();
        }
    }

    public void pauseMusic() {
        if (musicPlayer != null) {
            musicPlayer.pause();
        }
    }

    public void resumeMusic() {
        if (settingsManager.isMusicEnabled() && musicPlayer != null) {
            musicPlayer.play();
        }
    }

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

    public void updateMusicVolume(double volume) {
        if (musicPlayer != null) {
            musicPlayer.setVolume(volume);
        }
    }

    public void updateSfxVolume(double volume) {
        // AudioClips don't support global volume update easily without tracking all
        // instances
        // But we set volume on play(), so subsequent plays will respect the new volume.
        // For currently playing clips, we'd need to track them, but SFX are short
        // usually.
    }

    public void onMusicEnabledChanged(boolean enabled) {
        if (enabled) {
            playMusic();
        } else {
            stopMusic(); // or pause
        }
    }
}
