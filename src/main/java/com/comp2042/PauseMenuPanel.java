package com.comp2042;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class PauseMenuPanel extends VBox {

    private Button resumeButton;
    private Button controlsButton;
    private Button mainMenuButton;
    private Button quitButton;

    public PauseMenuPanel() {
        setAlignment(Pos.CENTER);
        setSpacing(20);
        setStyle("-fx-background-color: rgba(29, 39, 56, 0.98); " +
                "-fx-border-color: white; " +
                "-fx-border-width: 3; " +
                "-fx-border-radius: 10; " +
                "-fx-background-radius: 10; " +
                "-fx-padding: 40;");
        setPrefSize(350, 450);
        setMaxSize(350, 450);
        setMinSize(350, 450);

        // Paused Title
        Label titleLabel = new Label("PAUSED");
        titleLabel.setStyle("-fx-font-size: 48px; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: white; " +
                "-fx-effect: dropshadow(gaussian, rgba(255, 255, 255, 0.6), 10, 0.5, 0, 0);");

        // Resume Button
        resumeButton = createMenuButton("RESUME");

        // Controls Button
        controlsButton = createMenuButton("CONTROLS");

        // Main Menu Button
        mainMenuButton = createMenuButton("MAIN MENU");

        // Quit Button
        quitButton = createMenuButton("QUIT");
        quitButton.setStyle(getButtonStyle() +
                "-fx-background-color: rgba(139, 195, 74, 0.8);");
        quitButton.setOnMouseEntered(e -> quitButton.setStyle(getButtonStyle() +
                "-fx-background-color: rgba(139, 195, 74, 1.0);"));
        quitButton.setOnMouseExited(e -> quitButton.setStyle(getButtonStyle() +
                "-fx-background-color: rgba(139, 195, 74, 0.8);"));

        getChildren().addAll(titleLabel, resumeButton, controlsButton, mainMenuButton, quitButton);
    }

    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.setPrefSize(250, 50);
        button.setMinSize(250, 50);
        button.setMaxSize(250, 50);
        button.setStyle(getButtonStyle());
        button.setOnMouseEntered(e -> button.setStyle(getButtonHoverStyle()));
        button.setOnMouseExited(e -> button.setStyle(getButtonStyle()));
        return button;
    }

    private String getButtonStyle() {
        return "-fx-background-color: rgba(90, 95, 127, 0.8); " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 18px; " +
                "-fx-border-color: white; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand;";
    }

    private String getButtonHoverStyle() {
        return "-fx-background-color: rgba(120, 125, 167, 1.0); " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 18px; " +
                "-fx-border-color: white; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand;";
    }

    public Button getResumeButton() {
        return resumeButton;
    }

    public Button getControlsButton() {
        return controlsButton;
    }

    public Button getMainMenuButton() {
        return mainMenuButton;
    }

    public Button getQuitButton() {
        return quitButton;
    }
}