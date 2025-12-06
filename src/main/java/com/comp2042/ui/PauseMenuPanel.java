package com.comp2042.ui;

import java.io.InputStream;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * A modal menu displayed when the game is paused.
 * Provides options to resume the game, access settings, view controls,
 * return to the main menu, or quit the application.
 */
public class PauseMenuPanel extends VBox {

    private Button resumeButton;
    private Button settingsButton;
    private Button controlsButton;
    private Button mainMenuButton;
    private Button quitButton;
    private Font customFont;
    private Font customFontBold;
    private Font customFontBoldButton;

    /**
     * Constructs a new PauseMenuPanel.
     * Sets up the layout, custom fonts, and creates all navigation buttons.
     */
    public PauseMenuPanel() {
        // Load custom fonts
        try {
            InputStream fontStream1 = getClass().getResourceAsStream("/BoutiqueBitmap9x9_1.9.ttf");
            InputStream fontStream2 = getClass().getResourceAsStream("/BoutiqueBitmap9x9_Bold_1.9.ttf");
            if (fontStream1 != null) {
                customFont = Font.loadFont(fontStream1, 16);
            }
            if (fontStream2 != null) {
                customFontBold = Font.loadFont(fontStream2, 48);
                // Reload for button size since we can't easily resize loaded fonts
                InputStream fontStream3 = getClass().getResourceAsStream("/BoutiqueBitmap9x9_Bold_1.9.ttf");
                if (fontStream3 != null) {
                    customFontBoldButton = Font.loadFont(fontStream3, 18);
                }
            }
        } catch (Exception e) {
            System.err.println("Could not load custom fonts for PauseMenuPanel");
        }
        if (customFont == null)
            customFont = Font.font("Arial", 16);
        if (customFontBold == null)
            customFontBold = Font.font("Arial", 48);
        if (customFontBoldButton == null)
            customFontBoldButton = Font.font("Arial", 18);

        setAlignment(Pos.CENTER);
        setSpacing(20);
        setStyle("-fx-background-color: #2C3E50; " +
                "-fx-border-color: white; " +
                "-fx-border-width: 3; " +
                "-fx-border-radius: 10; " +
                "-fx-background-radius: 10; " +
                "-fx-padding: 40;");
        setPrefSize(350, 500);
        setMaxSize(350, 500);
        setMinSize(350, 500);

        // Paused Title
        Label titleLabel = new Label("PAUSED");
        titleLabel.setFont(customFontBold);
        titleLabel.setStyle("-fx-font-size: 48px; " +
                "-fx-text-fill: white; " +
                "-fx-effect: dropshadow(gaussian, #DD0584, 15, 0.8, 0, 0);");

        // Resume Button
        resumeButton = createMenuButton("RESUME");

        // Settings Button
        settingsButton = createMenuButton("SETTINGS");

        // Controls Button
        controlsButton = createMenuButton("CONTROLS");

        // Main Menu Button
        mainMenuButton = createMenuButton("MAIN MENU");

        // Quit Button
        quitButton = createMenuButton("QUIT");
        // Use bold font for QUIT button
        quitButton.setFont(customFontBoldButton);
        quitButton.setStyle(getQuitButtonStyle());
        quitButton.setOnMouseEntered(e -> quitButton.setStyle(getQuitButtonHoverStyle()));
        quitButton.setOnMouseExited(e -> quitButton.setStyle(getQuitButtonStyle()));

        getChildren().addAll(titleLabel, resumeButton, settingsButton, controlsButton, mainMenuButton, quitButton);
    }

    /**
     * Creates a standard menu button with correct styling and hover effects.
     *
     * @param text the text to display on the button
     * @return the constructed Button
     */
    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.setPrefSize(250, 50);
        button.setMinSize(250, 50);
        button.setMaxSize(250, 50);
        button.setFont(customFont);
        button.setStyle(getButtonStyle());
        button.setOnMouseEntered(e -> button.setStyle(getButtonHoverStyle()));
        button.setOnMouseExited(e -> button.setStyle(getButtonStyle()));
        return button;
    }

    /**
     * Returns the base CSS style for standard menu buttons.
     *
     * @return the CSS style string
     */
    private String getButtonStyle() {
        return "-fx-background-color: transparent; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 18px; " +
                "-fx-border-color: white; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand;";
    }

    /**
     * Returns the hover CSS style for standard menu buttons.
     *
     * @return the CSS style string for hover state
     */
    private String getButtonHoverStyle() {
        return "-fx-background-color: rgba(255, 255, 255, 0.1); " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 18px; " +
                "-fx-border-color: white; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand;";
    }

    /**
     * Returns the base CSS style for the Quit button (pink).
     *
     * @return the CSS style string
     */
    private String getQuitButtonStyle() {
        return "-fx-background-color: #DD0584; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 18px; " +
                "-fx-border-color: white; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand;";
    }

    /**
     * Returns the hover CSS style for the Quit button.
     *
     * @return the CSS style string for hover state
     */
    private String getQuitButtonHoverStyle() {
        return "-fx-background-color: derive(#DD0584, 20%); " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 18px; " +
                "-fx-border-color: white; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand;";
    }

    /**
     * Retrieves the "Resume" button reference.
     *
     * @return the Button object for resuming the game
     */
    public Button getResumeButton() {
        return resumeButton;
    }

    /**
     * Retrieves the "Settings" button reference.
     *
     * @return the Button object for accessing settings
     */
    public Button getSettingsButton() {
        return settingsButton;
    }

    /**
     * Retrieves the "Controls" button reference.
     *
     * @return the Button object for viewing controls
     */
    public Button getControlsButton() {
        return controlsButton;
    }

    /**
     * Retrieves the "Main Menu" button reference.
     *
     * @return the Button object for returning to the main menu
     */
    public Button getMainMenuButton() {
        return mainMenuButton;
    }

    /**
     * Retrieves the "Quit" button reference.
     *
     * @return the Button object for quitting the application
     */
    public Button getQuitButton() {
        return quitButton;
    }
}