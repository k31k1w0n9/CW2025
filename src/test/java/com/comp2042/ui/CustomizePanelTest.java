package com.comp2042.ui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.comp2042.system.GameSettings;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;

class CustomizePanelTest {

    private static boolean toolkitInitialized = false;

    @BeforeAll
    static void initToolkit() throws Exception {
        if (!toolkitInitialized) {
            CompletableFuture<Void> initFuture = new CompletableFuture<>();
            Platform.startup(() -> initFuture.complete(null));
            initFuture.get(5, TimeUnit.SECONDS);
            toolkitInitialized = true;
        }
    }

    private CustomizePanel panel;

    @BeforeEach
    void setUp() throws Exception {
        runOnFxThread(() -> {
            GameSettings settings = GameSettings.getInstance();
            settings.clearCustomPiece("Custom 1");
            settings.clearCustomPiece("Custom 2");
            settings.clearCustomPiece("Custom 3");
        });

        panel = runOnFxThread(() -> new CustomizePanel());
        runOnFxThread(() -> panel.selectPiece("Custom 1"));
    }

    @Test
    void saveButtonPersistsCustomDesign() throws Exception {
        runOnFxThread(() -> {
            panel.setCellFilled(1, 1, true);
            panel.getSaveButton().fire();
        });

        boolean[][] saved = GameSettings.getInstance().getCustomPiece("Custom 1");
        assertTrue(saved[1][1], "Saved design should retain filled cells");
    }

    @Test
    void resetButtonClearsGrid() throws Exception {
        runOnFxThread(() -> {
            panel.setCellFilled(0, 0, true);
            panel.setCellFilled(0, 1, true);
            panel.getResetButton().fire();
        });

        assertFalse(runOnFxThread(() -> panel.isCellFilled(0, 0)), "Reset should clear filled cells");
        assertFalse(runOnFxThread(() -> panel.isCellFilled(0, 1)), "Reset should clear filled cells");
    }

    @Test
    void backButtonInvokesCallback() throws Exception {
        AtomicBoolean backCalled = new AtomicBoolean(false);

        runOnFxThread(() -> {
            panel.setOnBackAction(() -> backCalled.set(true));
            panel.getBackButton().fire();
        });

        assertTrue(backCalled.get(), "Back button should invoke registered callback");
    }

    @Test
    void deleteButtonClearsCustomPiece() throws Exception {
        runOnFxThread(() -> {
            panel.setCellFilled(2, 2, true);
            panel.getSaveButton().fire();
            panel.getDeleteButton().fire();
        });

        boolean[][] saved = GameSettings.getInstance().getCustomPiece("Custom 1");
        assertTrue(saved == null, "Delete should remove the custom piece");
    }

    private static <T> T runOnFxThread(FxCallable<T> callable) throws Exception {
        if (Platform.isFxApplicationThread()) {
            return callable.call();
        }

        CompletableFuture<T> future = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                future.complete(callable.call());
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        return future.get(5, TimeUnit.SECONDS);
    }

    private static void runOnFxThread(FxRunnable runnable) throws Exception {
        runOnFxThread(() -> {
            runnable.run();
            return null;
        });
    }

    @FunctionalInterface
    private interface FxCallable<T> {
        T call() throws Exception;
    }

    @FunctionalInterface
    private interface FxRunnable {
        void run() throws Exception;
    }
}
