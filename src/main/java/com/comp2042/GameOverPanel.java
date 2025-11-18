package com.comp2042;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class GameOverPanel extends VBox {

    private Button homeButton;
    private Button retryButton;
    private Button quitButton;
    private Label finalScoreLabel;
    private VBox highScoresBox;

    public GameOverPanel() {
        setAlignment(Pos.CENTER);
        setSpacing(20);
        setStyle("-fx-background-color: rgba(29, 39, 56, 0.98); " +
                "-fx-border-color: white; " +
                "-fx-border-width: 3; " +
                "-fx-border-radius: 10; " +
                "-fx-background-radius: 10; " +
                "-fx-padding: 30;");
        setPrefSize(400, 500);
        setMaxSize(400, 500);

        // Game Over Title
        Label titleLabel = new Label("GAME OVER");
        titleLabel.setStyle("-fx-font-size: 42px; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: white; " +
                "-fx-effect: dropshadow(gaussian, rgba(255, 255, 255, 0.6), 10, 0.5, 0, 0);");

        // High Scores Section
        Label highScoresTitle = new Label("HIGH SCORES");
        highScoresTitle.setStyle("-fx-font-size: 18px; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: white; " +
                "-fx-padding: 10 0 5 0;");

        highScoresBox = new VBox(5);
        highScoresBox.setAlignment(Pos.CENTER);
        highScoresBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4); " +
                "-fx-border-color: #4a5f7f; " +
                "-fx-border-width: 2; " +
                "-fx-padding: 15; " +
                "-fx-border-radius: 5; " +
                "-fx-background-radius: 5;");
        highScoresBox.setPrefWidth(300);

        // Buttons Container
        HBox buttonsBox = new HBox(15);
        buttonsBox.setAlignment(Pos.CENTER);

        // Home Button
        homeButton = createButton("🏠");
        homeButton.setStyle(getButtonStyle() + "-fx-font-size: 24px;");
        homeButton.setPrefSize(70, 70);

        // Retry Button
        retryButton = createButton("↻");
        retryButton.setStyle(getButtonStyle() + "-fx-font-size: 32px;");
        retryButton.setPrefSize(70, 70);

        // Quit Button
        quitButton = createButton("QUIT");
        quitButton.setStyle(getButtonStyle() +
                "-fx-font-size: 16px; " +
                "-fx-background-color: rgba(139, 195, 74, 0.8);");
        quitButton.setPrefSize(140, 50);
        quitButton.setOnMouseEntered(e -> quitButton.setStyle(getButtonStyle() +
                "-fx-font-size: 16px; " +
                "-fx-background-color: rgba(139, 195, 74, 1.0);"));
        quitButton.setOnMouseExited(e -> quitButton.setStyle(getButtonStyle() +
                "-fx-font-size: 16px; " +
                "-fx-background-color: rgba(139, 195, 74, 0.8);"));

        buttonsBox.getChildren().addAll(homeButton, retryButton, quitButton);

        getChildren().addAll(titleLabel, highScoresTitle, highScoresBox, buttonsBox);
    }

    private Button createButton(String text) {
        Button button = new Button(text);
        button.setStyle(getButtonStyle());
        button.setOnMouseEntered(e -> button.setStyle(getButtonHoverStyle()));
        button.setOnMouseExited(e -> button.setStyle(getButtonStyle()));
        return button;
    }

    private String getButtonStyle() {
        return "-fx-background-color: rgba(90, 95, 127, 0.8); " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
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
                "-fx-border-color: white; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand;";
    }

    public void updateHighScores(String playerName, int playerScore, int[] topScores) {
        highScoresBox.getChildren().clear();

        for (int i = 0; i < topScores.length; i++) {
            HBox scoreRow = new HBox(10);
            scoreRow.setAlignment(Pos.CENTER);

            String name = (i == 0 && playerScore >= topScores[0]) ? playerName : "";
            int score = topScores[i];

            Label nameLabel = new Label(name.isEmpty() ? "" : name);
            nameLabel.setStyle("-fx-font-size: 16px; " +
                    "-fx-text-fill: " + (i == 0 ? "#FFD700" : "white") + "; " +
                    "-fx-font-weight: bold; " +
                    "-fx-min-width: 100;");

            Label scoreLabel = new Label(String.valueOf(score));
            scoreLabel.setStyle("-fx-font-size: 18px; " +
                    "-fx-text-fill: " + (i == 0 ? "#FFD700" : "white") + "; " +
                    "-fx-font-weight: bold;");

            if (i == 0 && playerScore >= topScores[0]) {
                scoreRow.setStyle("-fx-background-color: rgba(255, 215, 0, 0.2); " +
                        "-fx-padding: 5; " +
                        "-fx-border-radius: 3; " +
                        "-fx-background-radius: 3;");
            }

            scoreRow.getChildren().addAll(nameLabel, scoreLabel);
            highScoresBox.getChildren().add(scoreRow);
        }
    }

    public Button getHomeButton() {
        return homeButton;
    }

    public Button getRetryButton() {
        return retryButton;
    }

    public Button getQuitButton() {
        return quitButton;
    }
}