package com.comp2042.ui;

import java.io.InputStream;

import com.comp2042.system.SettingsManager;
import com.comp2042.system.SoundManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * A JavaFX VBox that provides the user interface for modifying game settings.
 * Organizes settings into tabs: Audio, Visual, and Controls.
 * Handles user interactions for toggles and sliders, updating the underlying
 * {@link SettingsManager} and {@link SoundManager} immediately.
 */
public class SettingsPanel extends VBox {

    private Button doneButton;
    private Button mainResetButton;
    private SettingsManager settingsManager;
    private Font customFont;
    private Font customFontBold;

    // Tab buttons
    private Button audioTabButton;
    private Button visualTabButton;
    private Button controlsTabButton;

    // Content containers
    private VBox audioContent;
    private VBox visualContent;
    private ControlsPanel controlsPanel;
    private StackPane contentArea;

    // Toggle buttons (Audio/Visual)
    private Button musicToggle;
    private Button sfxToggle;
    private Button ghostToggle;
    private Button gridToggle;

    // Sliders
    private Slider musicVolumeSlider;
    private Slider sfxVolumeSlider;

    /**
     * Constructs a new SettingsPanel.
     * Initializes the layout, loads custom fonts, creates tabs, and sets up
     * event listeners for all settings controls.
     *
     * @param controlsPanel the ControlsPanel instance to embed within the
     *                      "Controls" tab
     */
    public SettingsPanel(ControlsPanel controlsPanel) {
        this.settingsManager = SettingsManager.getInstance();
        this.controlsPanel = controlsPanel;

        // Configure ControlsPanel for embedding
        if (controlsPanel != null) {
            controlsPanel.hideTitle();
            controlsPanel.hideBackButton();
            controlsPanel.hideTitle();
            controlsPanel.hideBackButton();
            controlsPanel.hideResetButton();
            // Remove border/background from embedded panel to blend in
            controlsPanel.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");
            // Reset size constraints to allow it to fit in the content area
            controlsPanel.setMinSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
            controlsPanel.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
            controlsPanel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        }

        // Load custom fonts
        try {
            InputStream fontStream1 = getClass().getResourceAsStream("/BoutiqueBitmap9x9_1.9.ttf");
            InputStream fontStream2 = getClass().getResourceAsStream("/BoutiqueBitmap9x9_Bold_1.9.ttf");
            if (fontStream1 != null) {
                customFont = Font.loadFont(fontStream1, 16);
            }
            if (fontStream2 != null) {
                customFontBold = Font.loadFont(fontStream2, 48);
            }
        } catch (Exception e) {
            System.err.println("Could not load custom fonts for SettingsPanel");
        }
        if (customFont == null)
            customFont = Font.font("Arial", 16);
        if (customFontBold == null)
            customFontBold = Font.font("Arial", 48);

        setAlignment(Pos.TOP_CENTER);
        setSpacing(15);
        setStyle("-fx-background-color: #2C3E50; " +
                "-fx-border-color: white; " +
                "-fx-border-width: 3; " +
                "-fx-border-radius: 10; " +
                "-fx-background-radius: 10; " +
                "-fx-padding: 25;");

        setPrefSize(800, 900);
        setMaxSize(800, 900);
        setMinSize(800, 900);

        // Title
        Label titleLabel = new Label("SETTINGS");
        titleLabel.setFont(customFontBold);
        titleLabel.setStyle("-fx-font-size: 48px; " +
                "-fx-text-fill: white; " +
                "-fx-effect: dropshadow(gaussian, #DD0584, 15, 0.8, 0, 0);");
        VBox.setMargin(titleLabel, new Insets(0, 0, 10, 0));

        // --- Framed Content Area (Contains Tabs + Content) ---
        VBox framedContent = new VBox(10);
        framedContent.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(framedContent, Priority.ALWAYS);
        framedContent.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3); " +
                "-fx-border-color: white; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-padding: 15;");

        // Tabs (Inside the frame)
        HBox tabsBox = new HBox(10);
        tabsBox.setAlignment(Pos.CENTER);
        tabsBox.setPadding(new Insets(0, 0, 10, 0));

        audioTabButton = createTabButton("AUDIO", true);
        visualTabButton = createTabButton("VISUAL", false);
        controlsTabButton = createTabButton("CONTROLS", false);

        tabsBox.getChildren().addAll(audioTabButton, visualTabButton, controlsTabButton);

        // Content Area (Inside the frame, below tabs)
        contentArea = new StackPane();
        contentArea.setAlignment(Pos.CENTER);
        VBox.setVgrow(contentArea, Priority.ALWAYS);
        // No border here, as the parent VBox has the border

        // Initialize content views
        createAudioContent();
        createVisualContent();

        // Add contents to stack pane
        contentArea.getChildren().addAll(audioContent, visualContent);
        if (controlsPanel != null) {
            contentArea.getChildren().add(controlsPanel);
        }

        framedContent.getChildren().addAll(tabsBox, contentArea);

        // Initialize buttons before showing tab (as showTab uses them)
        // Done button
        doneButton = new Button("Back");
        doneButton.setPrefSize(200, 50);
        doneButton.setMinSize(200, 50);
        doneButton.setMaxSize(200, 50);
        doneButton.setFont(customFont);
        doneButton.setStyle(getButtonStyle());
        doneButton.setOnMouseEntered(e -> doneButton.setStyle(getButtonHoverStyle()));
        doneButton.setOnMouseExited(e -> doneButton.setStyle(getButtonStyle()));

        // Main Reset Button (Context-sensitive)
        mainResetButton = new Button("Reset Defaults");
        mainResetButton.setPrefSize(240, 50);
        mainResetButton.setMinSize(240, 50);
        mainResetButton.setMaxSize(240, 50);
        mainResetButton.setFont(customFont);
        mainResetButton.setStyle(getButtonStyle());
        mainResetButton.setOnMouseEntered(e -> mainResetButton.setStyle(getButtonHoverStyle()));
        mainResetButton.setOnMouseExited(e -> mainResetButton.setStyle(getButtonStyle()));

        // Set initial visibility
        showTab("AUDIO");

        // Tab actions
        audioTabButton.setOnAction(e -> {
            showTab("AUDIO");
            SoundManager.getInstance().playSound(SoundManager.SFX_BTN_CLICK);
        });
        visualTabButton.setOnAction(e -> {
            showTab("VISUAL");
            SoundManager.getInstance().playSound(SoundManager.SFX_BTN_CLICK);
        });
        controlsTabButton.setOnAction(e -> {
            showTab("CONTROLS");
            SoundManager.getInstance().playSound(SoundManager.SFX_BTN_CLICK);
        });

        // Bottom Buttons container
        HBox buttonsBox = new HBox(15);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setPadding(new Insets(10, 0, 0, 0));

        buttonsBox.getChildren().addAll(mainResetButton, doneButton);

        getChildren().addAll(titleLabel, framedContent, buttonsBox);
    }

    /**
     * Creates and populates the content for the Audio settings tab.
     * Includes toggles and sliders for Music and Sound Effects (SFX).
     */
    private void createAudioContent() {
        audioContent = new VBox(20);
        audioContent.setAlignment(Pos.CENTER);
        audioContent.setPadding(new Insets(20));

        HBox musicRow = createToggleRow("Background Music", settingsManager.isMusicEnabled(),
                enabled -> {
                    settingsManager.setMusicEnabled(enabled);
                    musicVolumeSlider.setDisable(!enabled);
                    SoundManager.getInstance().onMusicEnabledChanged(enabled);
                    SoundManager.getInstance().playSound(SoundManager.SFX_BTN_CLICK);
                });
        musicToggle = (Button) ((HBox) musicRow.getChildren().get(1)).getChildren().get(0);

        HBox musicVolumeRow = createVolumeRow("Music Volume", settingsManager.getMusicVolume(),
                volume -> {
                    settingsManager.setMusicVolume(volume);
                    SoundManager.getInstance().updateMusicVolume(volume);
                });
        musicVolumeSlider = (Slider) ((HBox) musicVolumeRow.getChildren().get(1)).getChildren().get(0);
        musicVolumeSlider.setDisable(!settingsManager.isMusicEnabled());

        HBox sfxRow = createToggleRow("Sound Effects", settingsManager.isSfxEnabled(),
                enabled -> {
                    settingsManager.setSfxEnabled(enabled);
                    sfxVolumeSlider.setDisable(!enabled);
                    SoundManager.getInstance().playSound(SoundManager.SFX_BTN_CLICK);
                });
        sfxToggle = (Button) ((HBox) sfxRow.getChildren().get(1)).getChildren().get(0);

        HBox sfxVolumeRow = createVolumeRow("SFX Volume", settingsManager.getSfxVolume(),
                volume -> settingsManager.setSfxVolume(volume));
        sfxVolumeSlider = (Slider) ((HBox) sfxVolumeRow.getChildren().get(1)).getChildren().get(0);
        sfxVolumeSlider.setDisable(!settingsManager.isSfxEnabled());

        audioContent.getChildren().addAll(musicRow, musicVolumeRow, sfxRow, sfxVolumeRow);
    }

    /**
     * Creates and populates the content for the Visual settings tab.
     * Includes toggles for Ghost Piece and Grid Lines.
     */
    private void createVisualContent() {
        visualContent = new VBox(20);
        visualContent.setAlignment(Pos.CENTER);
        visualContent.setPadding(new Insets(20));

        HBox ghostRow = createToggleRow("Ghost Piece", settingsManager.isGhostPieceEnabled(),
                enabled -> {
                    settingsManager.setGhostPieceEnabled(enabled);
                    SoundManager.getInstance().playSound(SoundManager.SFX_BTN_CLICK);
                });
        ghostToggle = (Button) ((HBox) ghostRow.getChildren().get(1)).getChildren().get(0);

        HBox gridRow = createToggleRow("Grid Lines", settingsManager.isGridLinesEnabled(),
                enabled -> {
                    settingsManager.setGridLinesEnabled(enabled);
                    SoundManager.getInstance().playSound(SoundManager.SFX_BTN_CLICK);
                });
        gridToggle = (Button) ((HBox) gridRow.getChildren().get(1)).getChildren().get(0);

        visualContent.getChildren().addAll(ghostRow, gridRow);
    }

    /**
     * Programmatically selects and displays a specific tab.
     *
     * @param tabName the name of the tab to show ("AUDIO", "VISUAL", "CONTROLS")
     */
    public void selectTab(String tabName) {
        showTab(tabName);
    }

    /**
     * Switches the view to the specified tab.
     * Updates visibility of content panels and configures the main reset button
     * context.
     *
     * @param tabName the name of the tab to activate
     */
    private void showTab(String tabName) {
        // Reset tab styles
        audioTabButton.setStyle(getTabButtonStyle(false));
        visualTabButton.setStyle(getTabButtonStyle(false));
        controlsTabButton.setStyle(getTabButtonStyle(false));

        // Hide all content and remove from layout
        audioContent.setVisible(false);
        audioContent.setManaged(false);
        visualContent.setVisible(false);
        visualContent.setManaged(false);
        if (controlsPanel != null) {
            controlsPanel.setVisible(false);
            controlsPanel.setManaged(false);
        }

        // Activate selected tab
        // Activate selected tab and configure reset button
        switch (tabName) {
            case "AUDIO" -> {
                audioTabButton.setStyle(getTabButtonStyle(true));
                audioContent.setVisible(true);
                audioContent.setManaged(true);

                mainResetButton.setText("Reset Defaults");
                mainResetButton.setOnAction(e -> {
                    resetAudioSettings();
                    SoundManager.getInstance().playSound(SoundManager.SFX_BTN_CLICK);
                });
            }
            case "VISUAL" -> {
                visualTabButton.setStyle(getTabButtonStyle(true));
                visualContent.setVisible(true);
                visualContent.setManaged(true);

                mainResetButton.setText("Reset Defaults");
                mainResetButton.setOnAction(e -> {
                    resetVisualSettings();
                    SoundManager.getInstance().playSound(SoundManager.SFX_BTN_CLICK);
                });
            }
            case "CONTROLS" -> {
                controlsTabButton.setStyle(getTabButtonStyle(true));
                if (controlsPanel != null) {
                    controlsPanel.setVisible(true);
                    controlsPanel.setManaged(true);
                    controlsPanel.requestFocus(); // Ensure it captures keys

                    mainResetButton.setText("Reset Defaults");
                    mainResetButton.setOnAction(e -> {
                        controlsPanel.resetToDefaults();
                        SoundManager.getInstance().playSound(SoundManager.SFX_BTN_CLICK);
                    });
                }
            }
        }
    }

    /**
     * Creates a tab navigation button.
     *
     * @param text   the text to display on the button
     * @param active whether this tab is currently active (affects styling)
     * @return the constructed Button
     */
    private Button createTabButton(String text, boolean active) {
        Button button = new Button(text);
        button.setPrefSize(150, 40);
        button.setMinSize(150, 40);
        button.setMaxSize(150, 40);
        button.setFont(customFont);
        button.setStyle(getTabButtonStyle(active));
        return button;
    }

    /**
     * Returns the CSS style string for a tab button.
     *
     * @param active true if the button represents the active tab (pink highlight),
     *               false otherwise (transparent)
     * @return the CSS style string
     */
    private String getTabButtonStyle(boolean active) {
        if (active) {
            return "-fx-background-color: #DD0584; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-size: 16px; " +
                    "-fx-border-color: #DD0584; " +
                    "-fx-border-width: 2; " +
                    "-fx-border-radius: 8; " +
                    "-fx-background-radius: 8; " +
                    "-fx-cursor: hand;";
        } else {
            return "-fx-background-color: transparent; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-size: 16px; " +
                    "-fx-border-color: white; " +
                    "-fx-border-width: 2; " +
                    "-fx-border-radius: 8; " +
                    "-fx-background-radius: 8; " +
                    "-fx-cursor: hand;";
        }
    }

    /**
     * Creates a row containing a label and a toggle button (ON/OFF).
     *
     * @param label        the text label for the setting
     * @param initialState the initial state of the toggle
     * @param callback     the callback to execute when the toggle state changes
     * @return an HBox containing the label and toggle button
     */
    private HBox createToggleRow(String label, boolean initialState, ToggleCallback callback) {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER);
        row.setPrefWidth(550);
        row.setMaxWidth(550);

        Label nameLabel = new Label(label);
        nameLabel.setFont(customFont);
        nameLabel.setStyle("-fx-font-size: 18px; " +
                "-fx-text-fill: white; " +
                "-fx-min-width: 250; " +
                "-fx-alignment: center-left;");

        HBox toggleContainer = new HBox();
        toggleContainer.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(toggleContainer, Priority.ALWAYS);

        Button toggleButton = new Button(initialState ? "ON" : "OFF");
        toggleButton.setPrefSize(100, 35);
        toggleButton.setFont(customFont);
        toggleButton.setStyle(getToggleButtonStyle(initialState));

        toggleButton.setOnAction(e -> {
            boolean newState = toggleButton.getText().equals("OFF");
            toggleButton.setText(newState ? "ON" : "OFF");
            toggleButton.setStyle(getToggleButtonStyle(newState));
            callback.onToggle(newState);
        });

        toggleContainer.getChildren().add(toggleButton);
        row.getChildren().addAll(nameLabel, toggleContainer);
        return row;
    }

    /**
     * Creates a row containing a label and a volume slider.
     *
     * @param label        the text label for the setting
     * @param initialValue the initial volume value (0.0 to 1.0)
     * @param callback     the callback to execute when the slider value changes
     * @return an HBox containing the label and slider
     */
    private HBox createVolumeRow(String label, double initialValue, VolumeCallback callback) {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER);
        row.setPrefWidth(550);
        row.setMaxWidth(550);

        Label nameLabel = new Label(label);
        nameLabel.setFont(customFont);
        nameLabel.setStyle("-fx-font-size: 18px; " +
                "-fx-text-fill: white; " +
                "-fx-min-width: 250; " +
                "-fx-alignment: center-left;");

        HBox sliderContainer = new HBox(10);
        sliderContainer.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(sliderContainer, Priority.ALWAYS);

        Slider slider = new Slider(0, 100, initialValue * 100);
        slider.setPrefWidth(150);
        // Apply pink accent color
        slider.setStyle("-fx-accent: #DD0584; -fx-control-inner-background: #555555;");

        Label valueLabel = new Label(String.format("%d%%", (int) (initialValue * 100)));
        valueLabel.setFont(customFont);
        valueLabel.setStyle("-fx-text-fill: white; -fx-min-width: 50; -fx-alignment: center-right;");

        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int percent = newVal.intValue();
            valueLabel.setText(String.format("%d%%", percent));
            callback.onVolumeChange(percent / 100.0);
        });

        sliderContainer.getChildren().addAll(slider, valueLabel);
        row.getChildren().addAll(nameLabel, sliderContainer);
        return row;
    }

    /**
     * Returns the CSS style string for a toggle button based on its state.
     *
     * @param isOn true for ON (Green), false for OFF (Red)
     * @return the CSS style string
     */
    private String getToggleButtonStyle(boolean isOn) {
        if (isOn) {
            return "-fx-background-color: #4CAF50; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-size: 16px; " +
                    "-fx-border-color: #4CAF50; " +
                    "-fx-border-width: 2; " +
                    "-fx-border-radius: 5; " +
                    "-fx-background-radius: 5; " +
                    "-fx-cursor: hand;";
        } else {
            return "-fx-background-color: #f44336; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-size: 16px; " +
                    "-fx-border-color: #f44336; " +
                    "-fx-border-width: 2; " +
                    "-fx-border-radius: 5; " +
                    "-fx-background-radius: 5; " +
                    "-fx-cursor: hand;";
        }
    }

    /**
     * Returns the base CSS style for standard buttons.
     *
     * @return the CSS style string
     */
    private String getButtonStyle() {
        return "-fx-background-color: transparent; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 18px; " +
                "-fx-border-color: white; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand;";
    }

    /**
     * Returns the hover CSS style for standard buttons.
     *
     * @return the CSS style string for hover state
     */
    private String getButtonHoverStyle() {
        return "-fx-background-color: rgba(255, 255, 255, 0.1); " +
                "-fx-text-fill: #FFD75C; " +
                "-fx-font-size: 18px; " +
                "-fx-border-color: #FFD75C; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand;";
    }

    /**
     * Resets all audio settings to their factory defaults.
     * Updates both the internal state and the UI elements (toggles/sliders).
     */
    private void resetAudioSettings() {
        settingsManager.resetAudioToDefaults();

        // Update UI
        musicToggle.setText(settingsManager.isMusicEnabled() ? "ON" : "OFF");
        musicToggle.setStyle(getToggleButtonStyle(settingsManager.isMusicEnabled()));
        musicVolumeSlider.setValue(settingsManager.getMusicVolume() * 100);
        musicVolumeSlider.setDisable(!settingsManager.isMusicEnabled());

        // Notify SoundManager
        SoundManager.getInstance().onMusicEnabledChanged(settingsManager.isMusicEnabled());
        SoundManager.getInstance().updateMusicVolume(settingsManager.getMusicVolume());

        sfxToggle.setText(settingsManager.isSfxEnabled() ? "ON" : "OFF");
        sfxToggle.setStyle(getToggleButtonStyle(settingsManager.isSfxEnabled()));
        sfxVolumeSlider.setValue(settingsManager.getSfxVolume() * 100);
        sfxVolumeSlider.setDisable(!settingsManager.isSfxEnabled());
    }

    /**
     * Resets all visual settings to their factory defaults.
     * Updates both the internal state and the UI elements.
     */
    private void resetVisualSettings() {
        settingsManager.resetVisualToDefaults();

        // Update UI
        ghostToggle.setText(settingsManager.isGhostPieceEnabled() ? "ON" : "OFF");
        ghostToggle.setStyle(getToggleButtonStyle(settingsManager.isGhostPieceEnabled()));

        gridToggle.setText(settingsManager.isGridLinesEnabled() ? "ON" : "OFF");
        gridToggle.setStyle(getToggleButtonStyle(settingsManager.isGridLinesEnabled()));
    }

    /**
     * Retrieves the "Done" (or "Back") button used to exit the settings screen.
     *
     * @return the done button instance
     */
    public Button getDoneButton() {
        return doneButton;
    }

    @FunctionalInterface
    interface ToggleCallback {
        void onToggle(boolean enabled);
    }

    @FunctionalInterface
    interface VolumeCallback {
        void onVolumeChange(double volume);
    }
}
