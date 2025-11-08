package com.comp2042;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;

public class PauseMenuPanel extends BorderPane {

    private final Button resumeButton;
    private final Button newGameButton;
    private final Button quitButton;

    public PauseMenuPanel() {
        setStyle("-fx-background-color: rgba(0, 0, 0, 0.8);");

        VBox menuBox = new VBox(20);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setStyle("-fx-padding: 40;");

        final Label pauseLabel = new Label("PAUSED");
        pauseLabel.setStyle("-fx-font-size: 48px; -fx-font-weight: bold; -fx-text-fill: white;");

        resumeButton = new Button("Resume (P)");
        styleButton(resumeButton);

        newGameButton = new Button("New Game (N)");
        styleButton(newGameButton);

        quitButton = new Button("Quit");
        styleButton(quitButton);

        menuBox.getChildren().addAll(pauseLabel, resumeButton, newGameButton, quitButton);
        setCenter(menuBox);
    }

    private void styleButton(Button button) {
        button.setStyle(
                "-fx-font-size: 18px; " +
                        "-fx-padding: 10 30; " +
                        "-fx-background-color: #4a4a4a; " +
                        "-fx-text-fill: white; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5; " +
                        "-fx-cursor: hand;"
        );

        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-font-size: 18px; " +
                        "-fx-padding: 10 30; " +
                        "-fx-background-color: #6a6a6a; " +
                        "-fx-text-fill: white; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5; " +
                        "-fx-cursor: hand;"
        ));

        button.setOnMouseExited(e -> button.setStyle(
                "-fx-font-size: 18px; " +
                        "-fx-padding: 10 30; " +
                        "-fx-background-color: #4a4a4a; " +
                        "-fx-text-fill: white; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5; " +
                        "-fx-cursor: hand;"
        ));

        button.setPrefWidth(200);
    }

    public Button getResumeButton() {
        return resumeButton;
    }

    public Button getNewGameButton() {
        return newGameButton;
    }

    public Button getQuitButton() {
        return quitButton;
    }
}