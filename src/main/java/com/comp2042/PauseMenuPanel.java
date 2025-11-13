package com.comp2042;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

public class PauseMenuPanel extends BorderPane {

    private final Button resumeButton;
    private final Button mainMenuButton;
    private final Button controlsButton;
    private final Button quitButton;

    public PauseMenuPanel() {
        this.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        this.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        this.setPrefSize(240, 360);

        // Dark background with white rounded border
        setStyle("-fx-background-color: rgba(13, 27, 42, 0.95); " +
                "-fx-border-color: white; " +
                "-fx-border-width: 4; " +
                "-fx-border-radius: 15; " +
                "-fx-background-radius: 15;");

        // Add drop shadow effect
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.5));
        shadow.setRadius(20);
        shadow.setOffsetX(0);
        shadow.setOffsetY(5);
        setEffect(shadow);

        VBox menuBox = new VBox(20);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setStyle("-fx-padding: 30 20 30 20;");

        Label pauseLabel = new Label("PAUSED");
        pauseLabel.setStyle("-fx-font-size: 42px; -fx-font-weight: bold; -fx-text-fill: white; " +
                "-fx-font-family: 'Arial', sans-serif;");

        resumeButton = new Button("RESUME");
        styleButton(resumeButton, "#4CAF50", "#45a049");

        mainMenuButton = new Button("OPTIONS");
        styleButton(mainMenuButton, "#607080", "#506070");

        controlsButton = new Button("CONTROLS");
        styleButton(controlsButton, "#607080", "#506070");

        quitButton = new Button("QUIT");
        styleButton(quitButton, "#607080", "#506070");

        menuBox.getChildren().addAll(pauseLabel, resumeButton, mainMenuButton, controlsButton, quitButton);
        setCenter(menuBox);
    }

    private void styleButton(Button b, String color, String hoverColor) {
        String baseStyle = "-fx-font-size: 16px; " +
                "-fx-padding: 12 40; " +
                "-fx-background-color: " + color + "; " +
                "-fx-text-fill: white; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand; " +
                "-fx-font-weight: bold; " +
                "-fx-font-family: 'Arial', sans-serif; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 2);";

        String hoverStyle = "-fx-font-size: 16px; " +
                "-fx-padding: 12 40; " +
                "-fx-background-color: " + hoverColor + "; " +
                "-fx-text-fill: white; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand; " +
                "-fx-font-weight: bold; " +
                "-fx-font-family: 'Arial', sans-serif; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 8, 0, 0, 3);";

        b.setStyle(baseStyle);
        b.setOnMouseEntered(e -> b.setStyle(hoverStyle));
        b.setOnMouseExited(e -> b.setStyle(baseStyle));
        b.setPrefWidth(200);
    }

    public Button getResumeButton() { return resumeButton; }
    public Button getMainMenuButton() { return mainMenuButton; }
    public Button getControlsButton() { return controlsButton; }
    public Button getQuitButton() { return quitButton; }
}