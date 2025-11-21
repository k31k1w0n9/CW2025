package com.comp2042;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class GameOverPanel extends VBox {

    private Button homeButton;
    private Button retryButton;
    private Button quitButton;
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
        homeButton = createIconButton("🏠");

        // Retry Button
        retryButton = createIconButton("↻");

        // Quit Button
        quitButton = createMenuButton("QUIT");
        quitButton.setStyle(getButtonStyle() +
                "-fx-font-size: 16px; " +
                "-fx-background-color: rgba(139, 195, 74, 0.8);");
        quitButton.setOnMouseEntered(e -> quitButton.setStyle(getButtonStyle() +
                "-fx-font-size: 16px; " +
                "-fx-background-color: rgba(139, 195, 74, 1.0);"));
        quitButton.setOnMouseExited(e -> quitButton.setStyle(getButtonStyle() +
                "-fx-font-size: 16px; " +
                "-fx-background-color: rgba(139, 195, 74, 0.8);"));

        buttonsBox.getChildren().addAll(homeButton, retryButton, quitButton);

        getChildren().addAll(titleLabel, highScoresTitle, highScoresBox, buttonsBox);
    }

    private Button createIconButton(String icon) {
        Button button = new Button(icon);
        button.setPrefSize(70, 70);
        button.setMinSize(70, 70);
        button.setMaxSize(70, 70);

        button.setStyle(getIconButtonStyle());
        button.setOnMouseEntered(e -> button.setStyle(getIconButtonHoverStyle()));
        button.setOnMouseExited(e -> button.setStyle(getIconButtonStyle()));

        return button;
    }

    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.setPrefSize(140, 50);
        button.setMinSize(140, 50);
        button.setMaxSize(140, 50);
        button.setStyle(getButtonStyle() + "-fx-font-size: 16px;");
        button.setOnMouseEntered(e -> button.setStyle(getButtonHoverStyle() + "-fx-font-size: 16px;"));
        button.setOnMouseExited(e -> button.setStyle(getButtonStyle() + "-fx-font-size: 16px;"));
        return button;
    }

    private String getIconButtonStyle() {
        return "-fx-background-color: rgba(90, 95, 127, 0.8); " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 28px; " +
                "-fx-border-color: white; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand; " +
                "-fx-padding: 0; " +
                "-fx-min-width: 70px; " +
                "-fx-max-width: 70px; " +
                "-fx-min-height: 70px; " +
                "-fx-max-height: 70px;";
    }

    private String getIconButtonHoverStyle() {
        return "-fx-background-color: rgba(120, 125, 167, 1.0); " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 28px; " +
                "-fx-border-color: white; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand; " +
                "-fx-padding: 0; " +
                "-fx-min-width: 70px; " +
                "-fx-max-width: 70px; " +
                "-fx-min-height: 70px; " +
                "-fx-max-height: 70px;";
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

    // Updated method to display high scores from HighScoreEntry list
    public void updateHighScoresFromList(List<HighScoreManager.HighScoreEntry> highScores, int currentScore) {
        highScoresBox.getChildren().clear();

        for (int i = 0; i < Math.min(5, highScores.size()); i++) {
            HighScoreManager.HighScoreEntry entry = highScores.get(i);

            HBox scoreRow = new HBox(20);
            scoreRow.setAlignment(Pos.CENTER);
            scoreRow.setPrefWidth(280);

            // Display name - fixed width for alignment
            Label nameLabel = new Label(entry.getName().isEmpty() ? "---" : entry.getName());
            nameLabel.setStyle("-fx-font-size: 16px; " +
                    "-fx-text-fill: " + (entry.getScore() == currentScore ? "#FFD700" : "white") + "; " +
                    "-fx-font-weight: bold; " +
                    "-fx-min-width: 120; " +
                    "-fx-max-width: 120; " +
                    "-fx-alignment: center-left;");

            // Display score - fixed width for alignment
            Label scoreLabel = new Label(String.valueOf(entry.getScore()));
            scoreLabel.setStyle("-fx-font-size: 18px; " +
                    "-fx-text-fill: " + (entry.getScore() == currentScore ? "#FFD700" : "white") + "; " +
                    "-fx-font-weight: bold; " +
                    "-fx-min-width: 100; " +
                    "-fx-max-width: 100; " +
                    "-fx-alignment: center-right;");

            // Highlight if it's the current score
            if (entry.getScore() == currentScore) {
                scoreRow.setStyle("-fx-background-color: rgba(255, 215, 0, 0.2); " +
                        "-fx-padding: 5 10; " +
                        "-fx-border-radius: 3; " +
                        "-fx-background-radius: 3;");
            }

            scoreRow.getChildren().addAll(nameLabel, scoreLabel);
            highScoresBox.getChildren().add(scoreRow);
        }
    }

    // Legacy method for compatibility
    public void updateHighScores(String playerName, int playerScore, int[] topScores) {
        highScoresBox.getChildren().clear();

        for (int i = 0; i < topScores.length; i++) {
            HBox scoreRow = new HBox(20);
            scoreRow.setAlignment(Pos.CENTER_LEFT);
            scoreRow.setPrefWidth(280);

            String name = (i == 0 && playerScore >= topScores[0]) ? playerName : "---";
            int score = topScores[i];

            Label nameLabel = new Label(name);
            nameLabel.setStyle("-fx-font-size: 16px; " +
                    "-fx-text-fill: " + (i == 0 ? "#FFD700" : "white") + "; " +
                    "-fx-font-weight: bold; " +
                    "-fx-min-width: 120; " +
                    "-fx-max-width: 120; " +
                    "-fx-alignment: center-left;");

            Label scoreLabel = new Label(String.valueOf(score));
            scoreLabel.setStyle("-fx-font-size: 18px; " +
                    "-fx-text-fill: " + (i == 0 ? "#FFD700" : "white") + "; " +
                    "-fx-font-weight: bold; " +
                    "-fx-min-width: 100; " +
                    "-fx-alignment: center-right;");

            if (i == 0 && playerScore >= topScores[0]) {
                scoreRow.setStyle("-fx-background-color: rgba(255, 215, 0, 0.2); " +
                        "-fx-padding: 5 10; " +
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