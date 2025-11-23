package com.comp2042;

import java.io.InputStream;
import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class GameOverPanel extends VBox {

    private Button homeButton;
    private Button retryButton;
    private Button quitButton;
    private VBox highScoresBox;
    private Font customFont;
    private Font customFontBold;

    public GameOverPanel() {
        // Load custom fonts
        try {
            InputStream fontStream1 = getClass().getResourceAsStream("/BoutiqueBitmap9x9_1.9.ttf");
            InputStream fontStream2 = getClass().getResourceAsStream("/BoutiqueBitmap9x9_Bold_1.9.ttf");
            if (fontStream1 != null) {
                customFont = Font.loadFont(fontStream1, 28);
            }
            if (fontStream2 != null) {
                customFontBold = Font.loadFont(fontStream2, 28);
            }
        } catch (Exception e) {
            System.err.println("Could not load custom fonts for GameOverPanel");
        }
        if (customFont == null) customFont = Font.font("Arial", 28);
        if (customFontBold == null) customFontBold = Font.font("Arial", Font.getDefault().getSize());

        setAlignment(Pos.CENTER);
        setSpacing(20);
        // FIXED: Dark navy background (#2C3E50) instead of dark blue
        setStyle("-fx-background-color: #2C3E50; " +
                "-fx-border-color: white; " +
                "-fx-border-width: 3; " +
                "-fx-border-radius: 10; " +
                "-fx-background-radius: 10; " +
                "-fx-padding: 30;");
        setPrefSize(450, 520);
        setMaxSize(450, 520);

        // FIXED: Game Over Title - White pixel font with hot pink drop shadow
        Label titleLabel = new Label("GAME OVER");
        titleLabel.setFont(Font.font(customFontBold != null ? customFontBold.getFamily() : "Arial", 48));
        titleLabel.setStyle("-fx-text-fill: white; " +
                "-fx-effect: dropshadow(gaussian, #DD0584, 15, 0.8, 0, 0);");

        // High Scores Section
        Label highScoresTitle = new Label("HIGH SCORES");
        highScoresTitle.setFont(Font.font(customFont != null ? customFont.getFamily() : "Arial", 18));
        highScoresTitle.setStyle("-fx-text-fill: white; " +
                "-fx-padding: 10 0 5 0;");

        highScoresBox = new VBox(5);
        highScoresBox.setAlignment(Pos.CENTER);
        highScoresBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3); " +
                "-fx-border-color: white; " +
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
        quitButton.setFont(Font.font(customFont != null ? customFont.getFamily() : "Arial", 16));
        quitButton.setStyle(getButtonStyle() + "-fx-font-size: 16px; -fx-text-fill: white;");
        quitButton.setOnMouseEntered(e -> quitButton.setStyle(getButtonHoverStyle() + "-fx-font-size: 16px; -fx-text-fill: #FFD75C;"));
        quitButton.setOnMouseExited(e -> quitButton.setStyle(getButtonStyle() + "-fx-font-size: 16px; -fx-text-fill: white;"));

        buttonsBox.getChildren().addAll(homeButton, retryButton, quitButton);

        getChildren().addAll(titleLabel, highScoresTitle, highScoresBox, buttonsBox);
    }

    private Button createIconButton(String icon) {
        Button button = new Button(icon);
        button.setPrefSize(70, 70);
        button.setMinSize(70, 70);
        button.setMaxSize(70, 70);

        button.setStyle(getIconButtonStyle());
        // FIXED: Yellow hover effect
        button.setOnMouseEntered(e -> button.setStyle(getIconButtonHoverStyle()));
        button.setOnMouseExited(e -> button.setStyle(getIconButtonStyle()));

        return button;
    }

    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.setPrefSize(140, 50);
        button.setMinSize(140, 50);
        button.setMaxSize(140, 50);
        // FIXED: Use pixel font
        button.setFont(Font.font(customFont != null ? customFont.getFamily() : "Arial", 16));
        button.setStyle(getButtonStyle() + "-fx-font-size: 16px; -fx-text-fill: white;");
        // FIXED: Yellow hover effect
        button.setOnMouseEntered(e -> button.setStyle(getButtonHoverStyle() + "-fx-font-size: 16px; -fx-text-fill: #FFD75C;"));
        button.setOnMouseExited(e -> button.setStyle(getButtonStyle() + "-fx-font-size: 16px; -fx-text-fill: white;"));
        return button;
    }

    private String getIconButtonStyle() {
        return "-fx-background-color: transparent; " +
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
        // FIXED: Yellow hover effect
        return "-fx-background-color: transparent; " +
                "-fx-text-fill: #FFD75C; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 28px; " +
                "-fx-border-color: #FFD75C; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand; " +
                "-fx-padding: 0; " +
                "-fx-min-width: 70px; " +
                "-fx-max-width: 70px; " +
                "-fx-min-height: 70px; " +
                "-fx-max-height: 70px; " +
                "-fx-effect: dropshadow(gaussian, rgba(255, 215, 92, 0.8), 5, 0.5, 0, 0);";
    }

    private String getButtonStyle() {
        return "-fx-background-color: transparent; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-border-color: white; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand;";
    }

    private String getButtonHoverStyle() {
        // FIXED: Yellow hover effect
        return "-fx-background-color: transparent; " +
                "-fx-text-fill: #FFD75C; " +
                "-fx-font-weight: bold; " +
                "-fx-border-color: #FFD75C; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(255, 215, 92, 0.8), 5, 0.5, 0, 0);";
    }

    public void updateHighScoresFromList(List<HighScoreManager.HighScoreEntry> highScores, int currentScore) {
        highScoresBox.getChildren().clear();

        for (int i = 0; i < Math.min(5, highScores.size()); i++) {
            HighScoreManager.HighScoreEntry entry = highScores.get(i);

            HBox scoreRow = new HBox(20);
            scoreRow.setAlignment(Pos.CENTER);
            scoreRow.setPrefWidth(280);

            Label nameLabel = new Label(entry.getName().isEmpty() ? "---" : entry.getName());
            nameLabel.setFont(Font.font(customFont != null ? customFont.getFamily() : "Arial", 16));
            nameLabel.setStyle("-fx-text-fill: " + (entry.getScore() == currentScore ? "#FFD75C" : "white") + "; " +
                    "-fx-min-width: 120; " +
                    "-fx-max-width: 120; " +
                    "-fx-alignment: center-left;");

            Label scoreLabel = new Label(String.valueOf(entry.getScore()));
            scoreLabel.setFont(Font.font(customFont != null ? customFont.getFamily() : "Arial", 18));
            scoreLabel.setStyle("-fx-text-fill: " + (entry.getScore() == currentScore ? "#FFD75C" : "white") + "; " +
                    "-fx-min-width: 100; " +
                    "-fx-max-width: 100; " +
                    "-fx-alignment: center-right;");

            if (entry.getScore() == currentScore) {
                scoreRow.setStyle("-fx-background-color: rgba(255, 215, 92, 0.2); " +
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