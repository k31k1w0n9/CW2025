package com.comp2042.ui;

import java.io.InputStream;
import java.util.List;

import com.comp2042.system.HighScoreManager;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * A modal screen displayed when the game ends.
 * Shows the "GAME OVER" status, a list of high scores, and options to
 * return home, retry the game, or quit.
 */
public class GameOverPanel extends VBox {

    private Button homeButton;
    private Button retryButton;
    private Button quitButton;
    private VBox highScoresBox;
    private Font customFont;
    private Font customFontBold;
    private Font customFontBoldButton;

    /**
     * Constructs a new GameOverPanel.
     * Initializes the layout, loads custom fonts, and sets up high score display
     * and control buttons.
     */
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
                // Reload for button size
                InputStream fontStream3 = getClass().getResourceAsStream("/BoutiqueBitmap9x9_Bold_1.9.ttf");
                if (fontStream3 != null) {
                    customFontBoldButton = Font.loadFont(fontStream3, 16);
                }
            }
        } catch (Exception e) {
            System.err.println("Could not load custom fonts for GameOverPanel");
        }
        if (customFont == null)
            customFont = Font.font("Arial", 28);
        if (customFontBold == null)
            customFontBold = Font.font("Arial", Font.getDefault().getSize());
        if (customFontBoldButton == null)
            customFontBoldButton = Font.font("Arial", 16);

        setAlignment(Pos.CENTER);
        setSpacing(20);
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
        // Use bold font for High Scores title
        highScoresTitle.setFont(Font.font(customFontBold != null ? customFontBold.getFamily() : "Arial", 18));
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
        quitButton.setFont(Font.font(customFontBold != null ? customFontBold.getFamily() : "Arial", 18));
        quitButton.setStyle(getQuitButtonStyle() + "-fx-font-size: 16px; -fx-text-fill: white; -fx-font-weight: bold;");
        quitButton.setOnMouseEntered(
                e -> quitButton.setStyle(getQuitButtonHoverStyle() + "-fx-font-size: 16px; -fx-text-fill: white;"));
        quitButton.setOnMouseExited(
                e -> quitButton.setStyle(getQuitButtonStyle() + "-fx-font-size: 16px; -fx-text-fill: white;"));

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
        button.setFont(Font.font(customFont != null ? customFont.getFamily() : "Arial", 16));
        button.setStyle(getButtonStyle() + "-fx-font-size: 16px; -fx-text-fill: white;");
        button.setOnMouseEntered(
                e -> button.setStyle(getButtonHoverStyle() + "-fx-font-size: 16px; -fx-text-fill: white;"));
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
        return "-fx-background-color: rgba(255, 255, 255, 0.1); " +
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
        return "-fx-background-color: rgba(255, 255, 255, 0.1); " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-border-color: white; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand;";
    }

    private String getQuitButtonStyle() {
        return "-fx-background-color: #DD0584; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-border-color: white; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand;";
    }

    private String getQuitButtonHoverStyle() {
        return "-fx-background-color: derive(#DD0584, 20%); " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-border-color: white; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand;";
    }

    /**
     * Updates the high score list displayed on the panel.
     * Highlights the player's current score if it appears in the top list.
     *
     * @param highScores   the list of high score entries to display
     * @param currentScore the score achieved in the just-concluded game
     */
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

    /**
     * Retrieves the "Home" icon button.
     *
     * @return the Button object for returning to the main menu
     */
    public Button getHomeButton() {
        return homeButton;
    }

    /**
     * Retrieves the "Retry" icon button.
     *
     * @return the Button object for restarting the game
     */
    public Button getRetryButton() {
        return retryButton;
    }

    /**
     * Retrieves the "Quit" button.
     *
     * @return the Button object for quitting the application
     */
    public Button getQuitButton() {
        return quitButton;
    }
}