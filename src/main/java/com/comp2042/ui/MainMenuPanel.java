package com.comp2042.ui;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

/**
 * Represents the main menu screen of the application.
 * Displays the game logo and provides navigation buttons for starting the game,
 * customizing settings, accessing the settings menu, and quitting the
 * application.
 * Manages hover animations and click interactions for menu buttons.
 */
public class MainMenuPanel extends StackPane {

    private MenuButton startButton;
    private MenuButton customizedButton;
    private MenuButton settingsButton;
    private MenuButton quitButton;
    private VBox menuContainer;
    private Font buttonFont;
    private Font buttonFontBold;

    /**
     * Constructs a new MainMenuPanel.
     * Initializes the background, logo, and menu buttons.
     * Sets up custom fonts and layout containers.
     */
    public MainMenuPanel() {
        // Load custom font
        try {
            buttonFont = Font.loadFont(
                    getClass().getResourceAsStream("/BoutiqueBitmap9x9_1.9.ttf"), 24);
            buttonFontBold = Font.loadFont(
                    getClass().getResourceAsStream("/BoutiqueBitmap9x9_Bold_1.9.ttf"), 24);
        } catch (Exception e) {
            System.err.println("Could not load custom font, using default");
            e.printStackTrace();
            buttonFont = Font.font("Consolas", 24);
            buttonFontBold = Font.font("Consolas", 24);
        }

        // Set background image
        try {
            Image bgImage = new Image(getClass().getResourceAsStream("/background.png"));
            BackgroundImage background = new BackgroundImage(
                    bgImage,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(100, 100, true, true, false, true));
            setBackground(new Background(background));
        } catch (Exception e) {
            System.err.println("Could not load background image");
            setStyle("-fx-background-color: #3d4f6d;");
        }

        setAlignment(Pos.CENTER);

        // Main container
        menuContainer = new VBox(20);
        menuContainer.setAlignment(Pos.CENTER);
        menuContainer.setPrefWidth(700);

        // Logo
        VBox logoBox = new VBox(50);
        logoBox.setAlignment(Pos.CENTER);
        logoBox.setPrefHeight(300);

        try {
            Image logoImage = new Image(getClass().getResourceAsStream("/logo.png"));
            ImageView logoView = new ImageView(logoImage);
            logoView.setFitWidth(500);
            logoView.setFitHeight(374.64);
            logoView.setPreserveRatio(true);
            logoBox.getChildren().add(logoView);
        } catch (Exception e) {
            System.err.println("Could not load logo image");
            Label fallbackLabel = new Label("TETRIS");
            fallbackLabel.setStyle("-fx-font-size: 80px; -fx-text-fill: white;");
            logoBox.getChildren().add(fallbackLabel);
        }

        // Menu buttons with hover effect
        startButton = new MenuButton("Start Game");
        customizedButton = new MenuButton("Customized");
        settingsButton = new MenuButton("Settings");
        quitButton = new MenuButton("Quit Game");

        menuContainer.getChildren().addAll(
                logoBox,
                startButton,
                customizedButton,
                settingsButton,
                quitButton);

        getChildren().add(menuContainer);
    }

    /**
     * Internal class representing a stylized menu button.
     * Handles its own hover animations (arrow reveal, text scale) and click
     * effects.
     */
    private class MenuButton extends StackPane {
        private Label leftArrow;
        private Label buttonText;
        private Label rightArrow;
        private HBox contentBox;
        private boolean isHovered = false;
        private Runnable onClickAction;
        private boolean isAnimating = false;

        public MenuButton(String text) {
            setPrefSize(500, 60);
            setMaxSize(500, 60);

            // Content container
            contentBox = new HBox(15);
            contentBox.setAlignment(Pos.CENTER);

            // Left arrow (hidden by default)
            leftArrow = new Label(">");
            leftArrow.setFont(buttonFontBold);
            leftArrow.setStyle("-fx-text-fill: white; " +
                    "-fx-effect: dropshadow(gaussian, #DD0584, 4, 0, 0, 4);");
            leftArrow.setOpacity(0);

            // Button text
            String cleanText = text.replace("▸", "").replace("◂", "").trim();
            buttonText = new Label(cleanText);
            buttonText.setFont(buttonFont);
            buttonText.setStyle("-fx-text-fill: white; " +
                    "-fx-effect: dropshadow(gaussian, #DD0584, 4, 0, 0, 4); " +
                    "-fx-min-width: 300; " +
                    "-fx-alignment: center;");

            // Right arrow (hidden by default)
            rightArrow = new Label("<");
            rightArrow.setFont(buttonFontBold);
            rightArrow.setStyle("-fx-text-fill: white; " +
                    "-fx-effect: dropshadow(gaussian, #DD0584, 4, 0, 0, 4);");
            rightArrow.setOpacity(0);

            contentBox.getChildren().addAll(leftArrow, buttonText, rightArrow);
            getChildren().add(contentBox);

            // Hover effects
            setOnMouseEntered(e -> {
                if (!isHovered && !isAnimating) {
                    isHovered = true;
                    playHoverAnimation(true);
                }
            });

            setOnMouseExited(e -> {
                if (isHovered && !isAnimating) {
                    isHovered = false;
                    playHoverAnimation(false);
                }
            });

            // Click handler
            setOnMouseClicked(e -> {
                if (!isAnimating) {
                    playLineClearAnimation();
                }
            });

            setStyle("-fx-cursor: hand;");
        }

        private void playHoverAnimation(boolean show) {
            Duration duration = Duration.millis(200);

            if (show) {
                FadeTransition leftFade = new FadeTransition(duration, leftArrow);
                leftFade.setToValue(1);

                FadeTransition rightFade = new FadeTransition(duration, rightArrow);
                rightFade.setToValue(1);

                ScaleTransition scale = new ScaleTransition(duration, buttonText);
                scale.setToX(1.1);
                scale.setToY(1.1);

                buttonText.setFont(buttonFontBold);
                buttonText.setStyle("-fx-text-fill: #FFD75C; " +
                        "-fx-effect: dropshadow(gaussian, #DD0584, 4, 0, 0, 4); " +
                        "-fx-min-width: 300; " +
                        "-fx-alignment: center;");
                leftArrow.setStyle("-fx-text-fill: #FFD75C; " +
                        "-fx-effect: dropshadow(gaussian, #DD0584, 4, 0, 0, 4); " +
                        "-fx-alignment: center;");
                rightArrow.setStyle("-fx-text-fill: #FFD75C; " +
                        "-fx-effect: dropshadow(gaussian, #DD0584, 4, 0, 0, 4); " +
                        "-fx-alignment: center;");

                ParallelTransition parallel = new ParallelTransition(leftFade, rightFade, scale);
                parallel.play();
            } else {
                FadeTransition leftFade = new FadeTransition(duration, leftArrow);
                leftFade.setToValue(0);

                FadeTransition rightFade = new FadeTransition(duration, rightArrow);
                rightFade.setToValue(0);

                ScaleTransition scale = new ScaleTransition(duration, buttonText);
                scale.setToX(1.0);
                scale.setToY(1.0);

                buttonText.setFont(buttonFont);
                buttonText.setStyle("-fx-text-fill: white; " +
                        "-fx-effect: dropshadow(gaussian, #DD0584, 4, 0, 0, 4); " +
                        "-fx-min-width: 300; " +
                        "-fx-alignment: center;");
                leftArrow.setStyle("-fx-text-fill: white; " +
                        "-fx-effect: dropshadow(gaussian, #DD0584, 4, 0, 0, 4); " +
                        "-fx-alignment: center;");
                rightArrow.setStyle("-fx-text-fill: white; " +
                        "-fx-effect: dropshadow(gaussian, #DD0584, 4, 0, 0, 4); " +
                        "-fx-alignment: center;");

                ParallelTransition parallel = new ParallelTransition(leftFade, rightFade, scale);
                parallel.play();
            }
        }

        private void playLineClearAnimation() {
            isAnimating = true;

            // Execute the click action immediately (including sound effect)
            if (onClickAction != null) {
                onClickAction.run();
            }

            // Create overlay that stays in button bounds
            StackPane animationOverlay = new StackPane();
            animationOverlay.setPrefSize(500, 60);
            animationOverlay.setMaxSize(500, 60);
            animationOverlay.setMouseTransparent(true);

            // Create white flash
            Rectangle flashOverlay = new Rectangle(500, 60);
            flashOverlay.setFill(Color.WHITE);
            flashOverlay.setOpacity(0);
            animationOverlay.getChildren().add(flashOverlay);

            // Add overlay to button
            getChildren().add(animationOverlay);

            // Phase 1: Flash white
            FadeTransition flash1 = new FadeTransition(Duration.millis(80), flashOverlay);
            flash1.setToValue(0.9);

            FadeTransition flash2 = new FadeTransition(Duration.millis(80), flashOverlay);
            flash2.setToValue(0.2);

            FadeTransition flash3 = new FadeTransition(Duration.millis(80), flashOverlay);
            flash3.setToValue(0.9);

            SequentialTransition flashSequence = new SequentialTransition(flash1, flash2, flash3);

            // Phase 2: Line collapse
            flashSequence.setOnFinished(e -> {
                VBox linesContainer = new VBox(0);
                linesContainer.setPrefSize(500, 60);
                linesContainer.setMaxSize(500, 60);

                int numLines = 8;
                for (int i = 0; i < numLines; i++) {
                    Rectangle line = new Rectangle(500, 60.0 / numLines);
                    line.setFill(Color.web("#FFD75C"));
                    line.setOpacity(0.95);
                    linesContainer.getChildren().add(line);

                    ScaleTransition scaleY = new ScaleTransition(Duration.millis(200), line);
                    scaleY.setFromY(1.0);
                    scaleY.setToY(0.0);
                    scaleY.setDelay(Duration.millis(i * 25));

                    FadeTransition fade = new FadeTransition(Duration.millis(200), line);
                    fade.setFromValue(0.95);
                    fade.setToValue(0);
                    fade.setDelay(Duration.millis(i * 25));

                    ParallelTransition lineAnim = new ParallelTransition(scaleY, fade);
                    lineAnim.play();

                    if (i == numLines - 1) {
                        lineAnim.setOnFinished(ev -> {
                            getChildren().remove(animationOverlay);
                            isAnimating = false;
                        });
                    }
                }

                animationOverlay.getChildren().add(linesContainer);
            });

            flashSequence.play();
        }

        public void setOnClickAction(Runnable action) {
            this.onClickAction = action;
        }
    }

    // Public methods to set button actions
    /**
     * Sets the action to be executed when the "Start Game" button is clicked.
     *
     * @param action the Runnable to execute
     */
    public void setStartGameAction(Runnable action) {
        startButton.setOnClickAction(action);
    }

    /**
     * Sets the action to be executed when the "Customized" button is clicked.
     *
     * @param action the Runnable to execute
     */
    public void setCustomizedAction(Runnable action) {
        customizedButton.setOnClickAction(action);
    }

    /**
     * Sets the action to be executed when the "Settings" button is clicked.
     *
     * @param action the Runnable to execute
     */
    public void setSettingsAction(Runnable action) {
        settingsButton.setOnClickAction(action);
    }

    /**
     * Sets the action to be executed when the "Quit Game" button is clicked.
     *
     * @param action the Runnable to execute
     */
    public void setQuitAction(Runnable action) {
        quitButton.setOnClickAction(action);
    }

    public MenuButton getStartButton() {
        return startButton;
    }

    public MenuButton getCustomizedButton() {
        return customizedButton;
    }

    public MenuButton getSettingsButton() {
        return settingsButton;
    }

    public MenuButton getQuitButton() {
        return quitButton;
    }
}