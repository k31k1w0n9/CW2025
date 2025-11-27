package com.comp2042;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.util.Duration;

public class GuiController implements Initializable {

    // Dynamic cell size - calculated based on screen height
    private int BRICK_SIZE = 20;
    private static final int BOARD_COLS = 10;
    private static final int BOARD_ROWS = 20;
    private static final int VISIBLE_ROWS = 20;
    private static final int BUFFER_ROWS = 2;

    @FXML
    private Pane gamePanel;
    @FXML
    private GridPane holdPieceGrid;
    @FXML
    private VBox nextPiecesContainer;
    @FXML
    private HBox gameHBox;
    @FXML
    private VBox leftColumn;
    @FXML
    private VBox rightColumn;

    @FXML
    private Label scoreLabel;
    @FXML
    private Label currentScoreLabel;
    @FXML
    private Label levelLabel;
    @FXML
    private Label linesLabel;
    @FXML
    private Label highScoreLabel;
    @FXML
    private Label levelUpLabel;

    @FXML
    private Group groupNotification;
    @FXML
    private Pane brickPanel;
    @FXML
    private Pane ghostPanel;
    @FXML
    private Pane rootPane;
    @FXML
    private StackPane boardContainer;
    @FXML
    private Pane notificationLayer;

    @FXML
    private Rectangle boardBorder;
    @FXML
    private Rectangle boardBackground;

    @FXML
    private StackPane rootStackPane;
    @FXML
    private StackPane mainMenuContainer;
    @FXML
    private MainMenuPanel mainMenuPanel;
    @FXML
    private StackPane gameContainer;

    @FXML
    private StackPane pauseContainer;
    @FXML
    private PauseMenuPanel pauseMenuPanel;
    @FXML
    private StackPane controlsContainer;
    @FXML
    private StackPane settingsContainer;
    @FXML
    private StackPane customizeContainer;
    @FXML
    private StackPane overlayLayer;
    @FXML
    private StackPane gameOverContainer;
    @FXML
    private GameOverPanel gameOverPanel;

    @FXML
    private StackPane nameInputContainer;
    @FXML
    private NameInputDialog nameInputDialog;
    @FXML
    private Button pauseButton;

    private CustomizePanel customizePanel;
    private SettingsPanel settingsPanel;
    private ControlsPanel controlsPanel;
    private Rectangle[][] displayMatrix;
    private InputEventListener eventListener;
    private Rectangle[][] rectangles;
    private Rectangle[][] ghostRectangles;
    private Timeline timeLine;

    private final BooleanProperty isPause = new SimpleBooleanProperty();
    private final BooleanProperty isGameOver = new SimpleBooleanProperty();
    private int currentScore = 0;
    private HighScoreManager highScoreManager;
    private KeyBindings keyBindings;

    private Font customFont;
    private Font customFontBold;

    // FIXED: Track combo count for continuous clears
    private int comboCount = 0;

    // Level and gravity system
    private LevelSystem levelSystem;

    public LevelSystem getLevelSystem() {
        return levelSystem;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Calculate dynamic cell size based on screen height
        calculateCellSize();

        // =========================================================================
        // DEBUG: Font Loading with detailed error tracking
        // =========================================================================
        System.out.println("\n--- FONT LOADING ---");
        try {
            System.out.println("Attempting to load fonts...");

            InputStream fontStream1 = getClass().getResourceAsStream("/BoutiqueBitmap9x9_1.9.ttf");
            InputStream fontStream2 = getClass().getResourceAsStream("/BoutiqueBitmap9x9_Bold_1.9.ttf");

            System.out.println("Font stream 1 (regular): " + (fontStream1 != null ? "FOUND" : "NOT FOUND"));
            System.out.println("Font stream 2 (bold): " + (fontStream2 != null ? "FOUND" : "NOT FOUND"));

            if (fontStream1 != null) {
                customFont = Font.loadFont(fontStream1, 28);
                System.out.println("Regular font loaded: " + (customFont != null));
                if (customFont != null) {
                    System.out.println("Font family: " + customFont.getFamily());
                    System.out.println("Font size: " + customFont.getSize());
                }
            }

            if (fontStream2 != null) {
                customFontBold = Font.loadFont(fontStream2, 20);
                System.out.println("Bold font loaded: " + (customFontBold != null));
                if (customFontBold != null) {
                    System.out.println("Bold font family: " + customFontBold.getFamily());
                }
            }

            if (customFont != null && customFontBold != null) {
                System.out.println("[OK] ALL FONTS LOADED SUCCESSFULLY");
            } else {
                System.err.println("[WARNING] Some fonts failed to load, using fallback");
                if (customFont == null)
                    customFont = Font.font("Arial", 28);
                if (customFontBold == null)
                    customFontBold = Font.font("Arial", 20);
            }
        } catch (Exception e) {
            System.err.println("[ERROR] FONT LOADING ERROR:");
            e.printStackTrace();
            customFont = Font.font("Arial", 28);
            customFontBold = Font.font("Arial", 20);
        }

        // =========================================================================
        // FIXED: Background is now loaded via CSS (see window_style.css .root)
        // and via FXML (see gameLayout.fxml #rootPane)
        // This ensures it loads properly and applies to both root and game area
        // =========================================================================

        // Initialize managers
        highScoreManager = new HighScoreManager();
        keyBindings = new KeyBindings();
        levelSystem = new LevelSystem();

        // Start background music
        SoundManager.getInstance().playMusic();

        // Setup board dimensions
        setupBoardDimensions();

        // Apply fonts to labels - FIXED: Apply immediately, then again after FXML loads
        System.out.println("\n--- APPLYING FONTS TO UI ---");
        applyCustomFontsToAllLabels();
        // Also apply after a short delay to ensure FXML is fully loaded
        javafx.application.Platform.runLater(() -> {
            applyCustomFontsToAllLabels();
        });

        // This ensures fonts work even if individual label font setting fails
        if (rootStackPane != null && customFont != null) {
            String currentStyle = rootStackPane.getStyle();
            if (currentStyle == null)
                currentStyle = "";
            rootStackPane.setStyle(currentStyle +
                    " -fx-font-family: \"" + customFont.getFamily() + "\", serif;");
        }

        // Update high score display
        updateHighScoreDisplay();

        // Bind level and lines to UI
        if (levelLabel != null) {
            levelLabel.textProperty().bind(levelSystem.levelProperty().asString());
        }
        if (linesLabel != null) {
            linesLabel.textProperty().bind(levelSystem.totalLinesClearedProperty().asString());
        }

        if (pauseButton != null) {
            pauseButton.setMinSize(55, 55);
            pauseButton.setPrefSize(55, 55);
            pauseButton.setMaxSize(55, 55);
            pauseButton.setFocusTraversable(false);
            pauseButton.setMouseTransparent(false);
            pauseButton.setPickOnBounds(true);

            // Set initial style explicitly
            pauseButton.setStyle(
                    "-fx-font-size: 32px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-font-family: Arial;" +
                            "-fx-text-fill: white; " +
                            "-fx-background-color: rgba(90, 95, 127, 0.85); " +
                            "-fx-border-color: white; " +
                            "-fx-border-width: 3; " +
                            "-fx-padding: 0; " +
                            "-fx-cursor: hand; " +
                            "-fx-background-radius: 5; " +
                            "-fx-border-radius: 5;");
        }

        // Setup all handlers and panels
        setupKeyHandlers();
        setupPauseMenu();
        setupControlsPanel();
        setupSettingsPanel();
        setupCustomizePanel();
        setupGameOverPanel();
        setupNameInputDialog();
        setupOverlayLayer();
        setupBoardDimensions();
        setupBrickPanels();

        if (mainMenuPanel != null && mainMenuContainer != null && gameContainer != null) {
            mainMenuContainer.setVisible(true);
            gameContainer.setVisible(false);

            mainMenuPanel.setStartGameAction(this::startNewGame);

            mainMenuPanel.setSettingsAction(this::showSettings);
            System.out.println("Settings action set");

            mainMenuPanel.setCustomizedAction(this::showCustomizeFromMainMenu);
            System.out.println("Customize action set");

            mainMenuPanel.setQuitAction(() -> System.exit(0));
        }

        // Set game panel as focusable for keyboard input
        if (gamePanel != null) {
            gamePanel.setFocusTraversable(true);
            gamePanel.requestFocus();
        }

        // Initialize Level Up Notification Label
        if (levelUpLabel == null) {
            levelUpLabel = new Label("LEVEL UP!");
        }
        levelUpLabel.setVisible(false);
        // Style it
        levelUpLabel.setStyle(
                "-fx-font-family: '" + (customFontBold != null ? customFontBold.getFamily() : "Arial") + "';" +
                        "-fx-font-size: 48px;" +
                        "-fx-text-fill: white;" +
                        "-fx-effect: dropshadow(gaussian, #DD0584, 0, 0, 0, 0);" // Start with no glow
        );

        if (overlayLayer != null && !overlayLayer.getChildren().contains(levelUpLabel)) {
            overlayLayer.getChildren().add(levelUpLabel);
        }

        System.out.println("GuiController initialization complete");
    }

    private void applyCustomFontsToAllLabels() {
        if (customFont == null) {
            System.err.println("Cannot apply fonts - customFont is null");
            return;
        }

        try {
            // Create font sizes
            Font scoreFontLarge = Font.font(customFont.getFamily(), 36);
            Font scoreFontMedium = Font.font(customFont.getFamily(), 28);

            // Apply to score labels
            if (scoreLabel != null) {
                scoreLabel.setFont(scoreFontLarge);
                scoreLabel.setStyle("-fx-text-fill: white;");
            }
            if (highScoreLabel != null) {
                highScoreLabel.setFont(scoreFontLarge);
                highScoreLabel.setStyle("-fx-text-fill: white;");
            }
            if (levelLabel != null) {
                levelLabel.setFont(scoreFontMedium);
                levelLabel.setStyle("-fx-text-fill: white;");
            }
            if (linesLabel != null) {
                linesLabel.setFont(scoreFontMedium);
                linesLabel.setStyle("-fx-text-fill: white;");
            }

            // FIXED: Apply fonts to header labels in info boxes
            applyFontsToInfoBoxHeaders();

            System.out.println("Fonts applied to all UI elements");
        } catch (Exception e) {
            System.err.println("Error applying fonts: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void applyFontsToInfoBoxHeaders() {
        if (customFontBold == null)
            return;

        Font headerFont = Font.font(customFontBold.getFamily(), 14);

        // Find and apply fonts to all header labels
        if (leftColumn != null) {
            applyFontToChildren(leftColumn, headerFont);
        }
        if (rightColumn != null) {
            applyFontToChildren(rightColumn, headerFont);
        }
    }

    private void applyFontToChildren(javafx.scene.Parent parent, Font font) {
        for (javafx.scene.Node node : parent.getChildrenUnmodifiable()) {
            if (node instanceof Label) {
                Label label = (Label) node;
                if (label.getStyleClass().contains("info-header-text")) {
                    label.setFont(font);
                }
            } else if (node instanceof javafx.scene.Parent) {
                applyFontToChildren((javafx.scene.Parent) node, font);
            }
        }
    }

    private void calculateCellSize() {
        double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
        // Target 85% of screen height for the board, with some padding
        double targetBoardHeight = screenHeight * 0.85 - 60;
        BRICK_SIZE = (int) (targetBoardHeight / BOARD_ROWS);
        // Clamp between reasonable values
        BRICK_SIZE = Math.max(25, Math.min(40, BRICK_SIZE));
    }

    private int gameRowToDisplayRow(int gameRow) {
        return gameRow - 2;
    }

    private int displayRowToGameRow(int displayRow) {
        return displayRow + 2;
    }

    private boolean isGameRowVisible(int gameRow) {
        return gameRow >= 2 && gameRow < 22;
    }

    private void setupBoardDimensions() {
        int boardWidth = BOARD_COLS * BRICK_SIZE; // 10 columns
        int boardHeight = BOARD_ROWS * BRICK_SIZE; // 20 VISIBLE rows

        // FIXED: Ensure boardContainer aligns to top for proper alignment with side
        // boxes
        if (boardContainer != null) {
            boardContainer.setAlignment(Pos.CENTER);
        }

        if (rootPane != null) {
            rootPane.setPrefSize(boardWidth, boardHeight);
            rootPane.setMinSize(boardWidth, boardHeight);
            rootPane.setMaxSize(boardWidth, boardHeight);

        }

        if (boardBorder != null) {
            boardBorder.setWidth(boardWidth);
            boardBorder.setHeight(boardHeight);
        }

        if (boardBackground != null) {
            boardBackground.setWidth(boardWidth);
            boardBackground.setHeight(boardHeight);
        }

        if (gamePanel != null) {
            gamePanel.setPrefSize(boardWidth, boardHeight);
            gamePanel.setMinSize(boardWidth, boardHeight);
            gamePanel.setMaxSize(boardWidth, boardHeight);
            gamePanel.setPadding(Insets.EMPTY);
        }

        if (brickPanel != null) {
            brickPanel.setPrefSize(boardWidth, boardHeight);
            brickPanel.setMinSize(boardWidth, boardHeight);
            brickPanel.setMaxSize(boardWidth, boardHeight);
        }

        if (ghostPanel != null) {
            ghostPanel.setPrefSize(boardWidth, boardHeight);
            ghostPanel.setMinSize(boardWidth, boardHeight);
            ghostPanel.setMaxSize(boardWidth, boardHeight);
        }

        if (nextPiecesContainer != null) {
            double nextHeight = boardHeight * 0.6;
            nextPiecesContainer.setMinHeight(nextHeight);
            nextPiecesContainer.setPrefHeight(nextHeight);
            nextPiecesContainer.setMaxHeight(nextHeight);
            nextPiecesContainer.setAlignment(Pos.TOP_CENTER);
            if (rightColumn != null) {
                rightColumn.setAlignment(Pos.TOP_LEFT);
            }
        }
    }

    private void setupKeyHandlers() {
        gamePanel.setOnKeyPressed(keyEvent -> {
            KeyCode code = keyEvent.getCode();

            if (!isPause.get() && !isGameOver.get()) {
                if (keyBindings.isKeyBound("MOVE_LEFT", code)) {
                    refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));
                    SoundManager.getInstance().playSound(SoundManager.SFX_MOVE);
                    keyEvent.consume();
                    return;
                }
                if (keyBindings.isKeyBound("MOVE_RIGHT", code)) {
                    refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
                    SoundManager.getInstance().playSound(SoundManager.SFX_MOVE);
                    keyEvent.consume();
                    return;
                }
                if (keyBindings.isKeyBound("ROTATE", code) || keyBindings.isKeyBound("ROTATE_LEFT", code)
                        || keyBindings.isKeyBound("ROTATE_RIGHT", code)) {
                    refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)));
                    SoundManager.getInstance().playSound(SoundManager.SFX_ROTATE);
                    keyEvent.consume();
                    return;
                }
                if (keyBindings.isKeyBound("SOFT_DROP", code)) {
                    moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
                    SoundManager.getInstance().playSound(SoundManager.SFX_MOVE);
                    keyEvent.consume();
                    return;
                }
                if (keyBindings.isKeyBound("HARD_DROP", code)) {
                    hardDrop(new MoveEvent(EventType.HARD_DROP, EventSource.USER));
                    keyEvent.consume();
                    return;
                }
                if (keyBindings.isKeyBound("HOLD", code)) {
                    refreshBrick(eventListener.onHoldEvent(new MoveEvent(EventType.HOLD, EventSource.USER)));
                    SoundManager.getInstance().playSound(SoundManager.SFX_HOLD);
                    keyEvent.consume();
                    return;
                }
            }

            if (keyBindings.isKeyBound("PAUSE", code)) {
                togglePause();
                SoundManager.getInstance().playSound(SoundManager.SFX_BTN_CLICK);
                keyEvent.consume();
                return;
            }

            if (code == KeyCode.N) {
                newGame(null);
            }
        });
    }

    private void setupPauseMenu() {
        pauseMenuPanel.setVisible(false);
        if (pauseContainer != null) {
            pauseContainer.setVisible(false);
            pauseContainer.setMouseTransparent(true);
        }

        pauseMenuPanel.getResumeButton().setOnAction(e -> togglePause());
        pauseMenuPanel.getSettingsButton().setOnAction(e -> showSettings());
        pauseMenuPanel.getMainMenuButton().setOnAction(e -> returnToMainMenu());
        pauseMenuPanel.getControlsButton().setOnAction(e -> showControls());
        pauseMenuPanel.getQuitButton().setOnAction(e -> System.exit(0));
    }

    private void setupControlsPanel() {
        System.out.println("Setting up controls panel...");

        // Initialize the controls panel field
        controlsPanel = new ControlsPanel(keyBindings);
        controlsPanel.setMaxSize(600, 660);
        controlsPanel.setMinSize(600, 660);
        controlsPanel.setPrefSize(600, 660);

        // We don't add it to controlsContainer anymore as it's embedded in Settings
        if (controlsContainer != null) {
            controlsContainer.setVisible(false);
            controlsContainer.setPickOnBounds(false);
            controlsContainer.setMouseTransparent(true);
        }

        System.out.println("Controls panel setup complete");
    }

    private void setupSettingsPanel() {
        System.out.println("Setting up settings panel...");

        if (settingsContainer != null) {
            System.out.println("settingsContainer exists");
            settingsContainer.getChildren().clear();

            settingsPanel = new SettingsPanel(controlsPanel);
            System.out.println("SettingsPanel created");

            settingsPanel.setMaxSize(700, 700);
            settingsPanel.setMinSize(700, 700);
            settingsPanel.setPrefSize(700, 700);
            System.out.println("SettingsPanel size set: 700x700");

            settingsContainer.getChildren().add(settingsPanel);
            System.out.println("SettingsPanel added to container");

            settingsContainer.setVisible(false);
            settingsContainer.setPickOnBounds(false);
            settingsContainer.setMouseTransparent(true);
            System.out.println("SettingsContainer hidden initially");

            settingsPanel.getDoneButton().setOnAction(e -> {
                System.out.println("Settings Done button clicked");
                hideSettings();
            });

            System.out.println("Settings panel setup complete");
        } else {
            System.err.println("[ERROR] settingsContainer is NULL!");
        }
    }

    private void setupCustomizePanel() {
        System.out.println("Setting up customize panel...");

        if (customizeContainer != null) {
            System.out.println("customizeContainer exists");
            customizeContainer.getChildren().clear();

            customizePanel = new CustomizePanel();
            System.out.println("CustomizePanel created");

            customizeContainer.getChildren().add(customizePanel);
            System.out.println("CustomizePanel added to container");

            customizeContainer.setVisible(false);
            customizeContainer.setPickOnBounds(false);
            customizeContainer.setMouseTransparent(true);
            System.out.println("CustomizeContainer hidden initially");

            // Wire up buttons
            // Save and Reset are handled internally by CustomizePanel

            customizePanel.setOnBackAction(() -> {
                System.out.println("Back button clicked - returning to main menu");

                customizeContainer.setVisible(false);
                customizeContainer.setPickOnBounds(false);
                customizeContainer.setMouseTransparent(true);

                if (gameContainer != null)
                    gameContainer.setVisible(false);
                if (mainMenuContainer != null)
                    mainMenuContainer.setVisible(true);

                updateOverlayLayer();
            });

            System.out.println("Customize panel setup complete");
        } else {
            System.err.println("[ERROR] customizeContainer is NULL!");
        }
    }

    private void setupGameOverPanel() {
        gameOverPanel = new GameOverPanel();
        gameOverPanel.setVisible(false);
        if (gameOverContainer != null) {
            gameOverContainer.getChildren().add(gameOverPanel);
            gameOverContainer.setVisible(false);
            gameOverContainer.setMouseTransparent(true);
        }

        gameOverPanel.getRetryButton().setOnAction(e -> newGame(null));
        gameOverPanel.getQuitButton().setOnAction(e -> System.exit(0));
        gameOverPanel.getHomeButton().setOnAction(e -> returnToMainMenu());
    }

    private void setupNameInputDialog() {
        if (nameInputDialog != null && nameInputContainer != null) {
            nameInputContainer.setVisible(false);
            nameInputContainer.setMouseTransparent(true);

            nameInputDialog.getOkButton().setOnAction(e -> {
                String playerName = nameInputDialog.getPlayerName();
                highScoreManager.addHighScore(playerName, currentScore);
                nameInputContainer.setVisible(false);
                nameInputContainer.setMouseTransparent(true);
                updateHighScoreDisplay();

                if (gameOverPanel != null) {
                    List<HighScoreManager.HighScoreEntry> highScores = highScoreManager.getHighScores();
                    gameOverPanel.updateHighScoresFromList(highScores, currentScore);
                    gameOverPanel.setVisible(true);
                    gameOverContainer.setVisible(true);
                    gameOverContainer.setMouseTransparent(false);
                }
                updateOverlayLayer();
            });

            nameInputDialog.getNameTextField().setOnAction(e -> nameInputDialog.getOkButton().fire());
        }
    }

    private void setupOverlayLayer() {
        if (overlayLayer != null) {
            overlayLayer.setMouseTransparent(true);
        }
    }

    private void setupBrickPanels() {
        brickPanel.setManaged(false);
        ghostPanel.setManaged(false);

        int boardWidth = BOARD_COLS * BRICK_SIZE;
        int boardHeight = BOARD_ROWS * BRICK_SIZE;

        // Clip brickPanel - hides pieces above/below visible area
        Rectangle brickClip = new Rectangle(boardWidth, boardHeight);
        brickPanel.setClip(brickClip);

        // Clip ghostPanel
        Rectangle ghostClip = new Rectangle(boardWidth, boardHeight);
        ghostPanel.setClip(ghostClip);

        // Clip gamePanel (the grid background)
        Rectangle gameClip = new Rectangle(boardWidth, boardHeight);
        gamePanel.setClip(gameClip);
    }

    @FXML
    public void onPauseButtonHover() {
        if (pauseButton != null) {
            pauseButton.setStyle(
                    "-fx-font-size: 32px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-text-fill: white; " +
                            "-fx-background-color: rgba(120, 125, 167, 1.0); " +
                            "-fx-border-color: white; " +
                            "-fx-border-width: 3; " +
                            "-fx-padding: 0; " +
                            "-fx-cursor: hand; " +
                            "-fx-background-radius: 5; " +
                            "-fx-border-radius: 5;");
        }
    }

    @FXML
    public void onPauseButtonExit() {
        if (pauseButton != null) {
            pauseButton.setStyle(
                    "-fx-font-size: 32px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-text-fill: white; " +
                            "-fx-background-color: rgba(90, 95, 127, 0.85); " +
                            "-fx-border-color: white; " +
                            "-fx-border-width: 3; " +
                            "-fx-padding: 0; " +
                            "-fx-cursor: hand; " +
                            "-fx-background-radius: 5; " +
                            "-fx-border-radius: 5;");
        }
    }

    private void startNewGame() {
        if (mainMenuContainer != null)
            mainMenuContainer.setVisible(false);
        if (gameContainer != null)
            gameContainer.setVisible(true);

        // Delegate to the central newGame method to ensure consistent initialization
        // This ensures timeline is started, rectangles are reset, and UI is cleared
        newGame(null);
        SoundManager.getInstance().playSound(SoundManager.SFX_GAME_START);
    }

    private void showCustomizeFromMainMenu() {
        System.out.println("\n=== SHOW CUSTOMIZE FROM MAIN MENU ===");

        if (mainMenuContainer != null) {
            mainMenuContainer.setVisible(false);
            System.out.println("Main menu hidden");
        }

        if (gameContainer != null) {
            gameContainer.setVisible(false);
            System.out.println("Game container hidden");
        }

        if (customizeContainer != null) {
            System.out.println("customizeContainer exists");
            System.out.println("Children count: " + customizeContainer.getChildren().size());

            customizeContainer.setVisible(true);
            customizeContainer.setPickOnBounds(true);
            customizeContainer.setMouseTransparent(false);
            customizeContainer.toFront();
            System.out.println("customizeContainer made VISIBLE and INTERACTIVE");

            if (customizePanel != null) {
                customizePanel.setVisible(true);
                customizePanel.requestFocus();
                System.out.println("CustomizePanel ready");
            } else if (!customizeContainer.getChildren().isEmpty()) {
                System.out.println("CustomizePanel retrieved from container");
                customizeContainer.getChildren().get(0).requestFocus();
            } else {
                System.err.println("[ERROR] customizeContainer has NO children!");
            }
        } else {
            System.err.println("[ERROR] customizeContainer is NULL!");
        }

        updateOverlayLayer();
        System.out.println("=== SHOW CUSTOMIZE COMPLETE ===\n");
    }

    public void returnToMainMenu() {
        if (timeLine != null)
            timeLine.stop();
        if (gameOverPanel != null)
            gameOverPanel.setVisible(false);
        if (gameOverContainer != null) {
            gameOverContainer.setVisible(false);
            gameOverContainer.setMouseTransparent(true);
        }
        if (pauseContainer != null)
            pauseContainer.setVisible(false);
        if (nameInputContainer != null)
            nameInputContainer.setVisible(false);
        if (gameContainer != null)
            gameContainer.setVisible(false);
        if (mainMenuContainer != null)
            mainMenuContainer.setVisible(true);
        updateOverlayLayer();
    }

    public void togglePause() {
        if (isGameOver.get())
            return;

        isPause.set(!isPause.get());
        pauseMenuPanel.setVisible(isPause.get());
        if (pauseContainer != null) {
            pauseContainer.setVisible(isPause.get());
            pauseContainer.setMouseTransparent(!isPause.get());
        }
        if (!isPause.get())
            hideControls();
        updateOverlayLayer();

        if (isPause.get())
            timeLine.pause();
        else {
            timeLine.play();
            gamePanel.requestFocus();
        }
    }

    public void updateNextPieces(List<int[][]> nextShapes) {
        if (nextPiecesContainer == null)
            return;
        nextPiecesContainer.getChildren().clear();

        int verticalSpacing = 15; // Space between pieces

        for (int idx = 0; idx < nextShapes.size(); idx++) {
            int[][] shape = nextShapes.get(idx);

            StackPane pieceContainer = new StackPane();
            pieceContainer.setMinHeight(70);
            pieceContainer.setMaxHeight(70);
            pieceContainer.setAlignment(Pos.CENTER);
            pieceContainer.setPadding(new Insets(5)); // Add padding for large pieces

            // Add spacing except for last piece
            if (idx < nextShapes.size() - 1) {
                VBox.setMargin(pieceContainer, new Insets(0, 0, verticalSpacing, 0));
            }

            GridPane nextGrid = new GridPane();
            nextGrid.setHgap(2);
            nextGrid.setVgap(2);
            nextGrid.setAlignment(Pos.CENTER);

            int rows = shape.length;
            int cols = shape[0].length;

            // Calculate dynamic piece size based on piece dimensions to fit in container
            // Account for padding and gaps
            double availableWidth = 120; // Approximate width available
            double availableHeight = 60; // Approximate height available per piece
            double cellSizeWidth = (availableWidth - (cols - 1) * 2) / cols;
            double cellSizeHeight = (availableHeight - (rows - 1) * 2) / rows;
            int pieceSize = (int) Math.min(Math.min(cellSizeWidth, cellSizeHeight), BRICK_SIZE * 0.5);
            pieceSize = Math.max(pieceSize, 8); // Minimum size

            // Check if this is a custom piece to get per-piece color
            boolean isCustomPiece = false;
            Color customColor = null;
            boolean useOutline = false;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    int val = shape[i][j];
                    if (val >= 9) {
                        isCustomPiece = true;
                        customColor = getCustomPieceColorById(val);
                        // Get outline setting from piece settings
                        GameSettings.PieceSettings settings = getPieceSettingsById(val);
                        useOutline = settings != null ? settings.outlineEnabled
                                : GameSettings.getInstance().isOutlineEnabled();
                        break;
                    } else if (val == 8) {
                        // Legacy support
                        isCustomPiece = true;
                        String pieceName = findCustomPieceName(shape);
                        if (pieceName != null) {
                            GameSettings.PieceSettings settings = GameSettings.getInstance()
                                    .getPieceSettings(pieceName);
                            if (settings != null) {
                                customColor = settings.color;
                                useOutline = settings.outlineEnabled;
                            }
                        }
                        break;
                    }
                }
                if (isCustomPiece)
                    break;
            }

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (shape[i][j] != 0) {
                        Rectangle cell = new Rectangle(pieceSize, pieceSize);

                        // Use per-piece color for custom pieces
                        if (isCustomPiece && customColor != null) {
                            cell.setFill(customColor);
                            if (useOutline) {
                                cell.setStroke(Color.WHITE);
                                cell.setStrokeWidth(1);
                            } else {
                                cell.setStroke(Color.TRANSPARENT);
                            }
                        } else {
                            cell.setFill(getColor(shape[i][j]));
                            cell.setStroke(Color.BLACK);
                            cell.setStrokeWidth(1);
                        }

                        cell.setArcWidth(3);
                        cell.setArcHeight(3);
                        nextGrid.add(cell, j, i);
                    }
                }
            }

            pieceContainer.getChildren().add(nextGrid);
            nextPiecesContainer.getChildren().add(pieceContainer);
        }
    }

    public void updateHoldPiece(int[][] holdShape) {
        if (holdPieceGrid == null)
            return;
        holdPieceGrid.getChildren().clear();
        if (holdShape == null)
            return;

        int pieceSize = (int) (BRICK_SIZE * 0.5);
        int rows = holdShape.length;
        int cols = holdShape[0].length;

        holdPieceGrid.setAlignment(Pos.CENTER);
        holdPieceGrid.setHgap(1);
        holdPieceGrid.setVgap(1);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (holdShape[i][j] != 0) {
                    Rectangle cell = new Rectangle(pieceSize, pieceSize);
                    cell.setFill(getColor(holdShape[i][j]));
                    cell.setStroke(Color.BLACK);
                    cell.setStrokeWidth(1);
                    cell.setArcWidth(5);
                    cell.setArcHeight(5);
                    holdPieceGrid.add(cell, j, i);
                }
            }
        }
    }

    private void showControls() {
        showSettings();
        if (settingsPanel != null) {
            settingsPanel.selectTab("CONTROLS");
        }
    }

    private void hideControls() {
        hideSettings();
    }

    private void showSettings() {
        if (settingsContainer != null) {
            // Hide pause menu
            pauseMenuPanel.setVisible(false);
            if (pauseContainer != null) {
                pauseContainer.setVisible(false);
                pauseContainer.setPickOnBounds(false);
                pauseContainer.setMouseTransparent(true);
            }

            // Show settings
            settingsContainer.setVisible(true);
            settingsContainer.setPickOnBounds(true);
            settingsContainer.setMouseTransparent(false);

            if (settingsPanel != null) {
                settingsPanel.setVisible(true);
                settingsPanel.requestFocus();
            }
        }
        updateOverlayLayer();
    }

    private void hideSettings() {
        if (settingsContainer != null) {
            settingsContainer.setVisible(false);
            settingsContainer.setPickOnBounds(false);
            settingsContainer.setMouseTransparent(true);

            // Show pause menu again if game is paused
            if (isPause.get()) {
                pauseMenuPanel.setVisible(true);
                if (pauseContainer != null) {
                    pauseContainer.setVisible(true);
                    pauseContainer.setPickOnBounds(true);
                    pauseContainer.setMouseTransparent(false);
                }
            }
        }
        refreshGridLines();
        updateOverlayLayer();
        gamePanel.requestFocus();
    }

    private void refreshGridLines() {
        if (displayMatrix == null)
            return;
        boolean gridEnabled = SettingsManager.getInstance().isGridLinesEnabled();
        for (int i = 0; i < displayMatrix.length; i++) {
            for (int j = 0; j < displayMatrix[i].length; j++) {
                if (displayMatrix[i][j] != null) {
                    if (gridEnabled) {
                        displayMatrix[i][j].setStroke(Color.rgb(50, 70, 100, 0.5));
                    } else {
                        displayMatrix[i][j].setStroke(Color.TRANSPARENT);
                    }
                }
            }
        }
    }

    private void updateOverlayLayer() {
        if (overlayLayer != null) {
            boolean overlaysVisible = (pauseContainer != null && pauseContainer.isVisible()) ||
                    (controlsContainer != null && controlsContainer.isVisible()) ||
                    (gameOverContainer != null && gameOverContainer.isVisible()) ||
                    (nameInputContainer != null && nameInputContainer.isVisible());

            overlayLayer.setPickOnBounds(overlaysVisible);
            overlayLayer.setMouseTransparent(!overlaysVisible);
        }
    }

    private Paint getFillColor(int i) {
        // Handle transparent/empty cells
        if (i == 0) {
            return Color.TRANSPARENT;
        }

        // Handle custom pieces (ID >= 9)
        if (i >= 9) {
            return getCustomPieceColorById(i);
        }

        // Handle standard pieces (ID 1-7) - always use GameSettings
        if (i >= 1 && i <= 7) {
            String name = getStandardPieceName(i);
            if (name != null) {
                GameSettings.PieceSettings ps = GameSettings.getInstance().getPieceSettings(name);
                if (ps != null && ps.color != null) {
                    return ps.color;
                }
            }
        }

        // Fallback colors (should rarely be used if GameSettings is properly
        // initialized)
        return switch (i) {
            case 1 -> Color.CYAN; // I Piece
            case 2 -> Color.YELLOW; // O Piece
            case 3 -> Color.PURPLE; // T Piece
            case 4 -> Color.GREEN; // S Piece
            case 5 -> Color.RED; // Z Piece
            case 6 -> Color.BLUE; // J Piece
            case 7 -> Color.ORANGE; // L Piece
            case 8 -> GameSettings.getInstance().getCustomPieceColor(); // Legacy custom piece
            default -> Color.WHITE;
        };
    }

    private Color getColor(int value) {
        if (value >= 9) {
            return getCustomPieceColorById(value);
        }
        if (value >= 1 && value <= 7) {
            String name = getStandardPieceName(value);
            if (name != null) {
                GameSettings.PieceSettings ps = GameSettings.getInstance().getPieceSettings(name);
                if (ps != null)
                    return ps.color;
            }
        }

        return switch (value) {
            case 0 -> Color.TRANSPARENT;
            case 1 -> Color.CYAN; // I Piece
            case 2 -> Color.YELLOW; // O Piece
            case 3 -> Color.PURPLE; // T Piece
            case 4 -> Color.GREEN; // S Piece
            case 5 -> Color.RED; // Z Piece
            case 6 -> Color.BLUE; // J Piece
            case 7 -> Color.ORANGE; // L Piece
            case 8 -> GameSettings.getInstance().getCustomPieceColor(); // Fallback if piece not found
            default -> Color.GRAY;
        };
    }

    private String getStandardPieceName(int id) {
        return switch (id) {
            case 1 -> "I Piece";
            case 2 -> "O Piece";
            case 3 -> "T Piece";
            case 4 -> "S Piece";
            case 5 -> "Z Piece";
            case 6 -> "J Piece";
            case 7 -> "L Piece";
            default -> null;
        };
    }

    // Find custom piece name by matching shape to stored designs
    private String findCustomPieceName(int[][] shape) {
        if (shape == null)
            return null;

        GameSettings settings = GameSettings.getInstance();
        Map<String, boolean[][]> allPieces = settings.getAllCustomPieces();

        for (Map.Entry<String, boolean[][]> entry : allPieces.entrySet()) {
            boolean[][] design = entry.getValue();
            if (design == null)
                continue;

            // Check if shape matches this design (accounting for possible rotations)
            if (shapeMatches(shape, design)) {
                return entry.getKey();
            }
        }
        return null;
    }

    // Check if shape matches design (simple comparison - assumes base rotation)
    private boolean shapeMatches(int[][] shape, boolean[][] design) {
        if (shape == null || design == null)
            return false;
        if (shape.length != design.length)
            return false;
        if (shape[0].length != design[0].length)
            return false;

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                boolean shapeHasBlock = shape[i][j] == 8;
                boolean designHasBlock = design[i][j];
                if (shapeHasBlock != designHasBlock) {
                    return false;
                }
            }
        }
        return true;
    }

    // Get settings for a custom piece by ID
    private GameSettings.PieceSettings getPieceSettingsById(int id) {
        GameSettings settings = GameSettings.getInstance();
        for (GameSettings.PieceSettings ps : settings.getAllCustomPieces().keySet().stream()
                .map(settings::getPieceSettings)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList())) {
            if (ps.id == id) {
                return ps;
            }
        }
        return null;
    }

    // Get color for a custom piece by ID
    private Color getCustomPieceColorById(int id) {
        GameSettings.PieceSettings ps = getPieceSettingsById(id);
        if (ps != null) {
            return ps.color;
        }
        return GameSettings.getInstance().getCustomPieceColor(); // Fallback
    }

    // Get color for a custom piece shape
    private Color getColorForCustomPiece(int[][] shape) {
        String pieceName = findCustomPieceName(shape);
        if (pieceName != null) {
            GameSettings.PieceSettings settings = GameSettings.getInstance().getPieceSettings(pieceName);
            if (settings != null) {
                return settings.color;
            }
        }
        // Fallback to default custom piece color
        return GameSettings.getInstance().getCustomPieceColor();
    }

    private void rebuildBrickRectangles(int[][] brickShape) {
        brickPanel.getChildren().clear();
        rectangles = new Rectangle[brickShape.length][brickShape[0].length];

        for (int i = 0; i < brickShape.length; i++) {
            for (int j = 0; j < brickShape[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(getFillColor(brickShape[i][j]));
                rectangle.setArcWidth(8);
                rectangle.setArcHeight(8);
                rectangles[i][j] = rectangle;
                rectangle.setLayoutX(j * BRICK_SIZE);
                rectangle.setLayoutY(i * BRICK_SIZE);
                brickPanel.getChildren().add(rectangle);
            }
        }
    }

    private void rebuildGhostRectangles(int[][] brickShape) {
        ghostPanel.getChildren().clear();
        ghostRectangles = new Rectangle[brickShape.length][brickShape[0].length];

        for (int i = 0; i < brickShape.length; i++) {
            for (int j = 0; j < brickShape[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                rectangle.setStroke(Color.TRANSPARENT);
                rectangle.setOpacity(0.4);
                rectangle.setArcWidth(8);
                rectangle.setArcHeight(8);
                ghostRectangles[i][j] = rectangle;
                rectangle.setLayoutX(j * BRICK_SIZE);
                rectangle.setLayoutY(i * BRICK_SIZE);
                ghostPanel.getChildren().add(rectangle);
            }
        }
    }

    public void initGameView(int[][] boardMatrix, ViewData viewData) {
        if (timeLine != null) {
            timeLine.stop();
        }

        int cols = boardMatrix[0].length;
        displayMatrix = new Rectangle[BOARD_ROWS][cols];
        gamePanel.getChildren().clear();

        for (int i = 0; i < BOARD_ROWS; i++) {
            for (int j = 0; j < cols; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                if (SettingsManager.getInstance().isGridLinesEnabled()) {
                    rectangle.setStroke(Color.rgb(50, 70, 100, 0.5));
                } else {
                    rectangle.setStroke(Color.TRANSPARENT);
                }
                rectangle.setStrokeWidth(0.8);
                displayMatrix[i][j] = rectangle;
                rectangle.setLayoutX(j * BRICK_SIZE);
                rectangle.setLayoutY(i * BRICK_SIZE);
                gamePanel.getChildren().add(rectangle);
            }
        }

        gamePanel.layout();

        rebuildBrickRectangles(viewData.getBrickData());
        rebuildGhostRectangles(viewData.getBrickData());
        updateBrickPanelPosition(viewData);
        updateGhostPanelPosition(viewData);

        // CRITICAL: Ensure border is always on top of everything
        if (boardBorder != null) {
            boardBorder.toFront();
        }

        timeLine = new Timeline(new KeyFrame(
                Duration.millis(400),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();

        updateNextPieces(viewData.getNextBrickData());
    }

    private void updateBrickPanelPosition(ViewData brick) {
        int gameX = brick.getxPosition();
        int gameY = brick.getyPosition();
        int[][] brickShape = brick.getBrickData();

        // CRITICAL FIX: Find the topmost block to determine when piece becomes visible
        // Pieces spawn at game row 0 (buffer), but should only appear when topmost
        // block reaches row 2
        int shapeTopRow = -1;
        for (int i = 0; i < brickShape.length; i++) {
            for (int j = 0; j < brickShape[i].length; j++) {
                if (brickShape[i][j] != 0) {
                    if (shapeTopRow == -1)
                        shapeTopRow = i;
                    break;
                }
            }
            if (shapeTopRow != -1)
                break;
        }
        if (shapeTopRow == -1)
            shapeTopRow = 0;

        // Calculate where the topmost block is in game coordinates
        int topmostBlockGameRow = gameY + shapeTopRow;

        // Convert game coords to display pixels
        // gameY=0 -> pixelY=-2*BRICK_SIZE (hidden above)
        // gameY=2 -> pixelY=0 (top of visible)
        double pixelX = gameX * BRICK_SIZE;
        double pixelY = (gameY - BUFFER_ROWS) * BRICK_SIZE;

        // FIXED: Hide piece if topmost block is still in buffer zone (rows 0-1)
        // Piece should only appear when topmost block reaches row 2 (display row 0)
        if (topmostBlockGameRow < BUFFER_ROWS) {
            brickPanel.setVisible(false);
        } else {
            brickPanel.setVisible(true);
            brickPanel.setLayoutX(pixelX);
            brickPanel.setLayoutY(pixelY);
        }
    }

    private void updateGhostPanelPosition(ViewData brick) {
        if (!SettingsManager.getInstance().isGhostPieceEnabled()) {
            ghostPanel.setVisible(false);
            return;
        }
        ghostPanel.setVisible(true);

        int gameX = brick.getGhostXPosition();
        int gameY = brick.getGhostYPosition();
        int[][] brickShape = brick.getBrickData();

        // FIXED: Use EXACT same calculation as brick panel for perfect alignment
        // Find the topmost block (same as brick panel)
        int shapeTopRow = -1;
        for (int i = 0; i < brickShape.length; i++) {
            for (int j = 0; j < brickShape[i].length; j++) {
                if (brickShape[i][j] != 0) {
                    if (shapeTopRow == -1)
                        shapeTopRow = i;
                    break;
                }
            }
            if (shapeTopRow != -1)
                break;
        }
        if (shapeTopRow == -1)
            shapeTopRow = 0;

        // Calculate where the topmost block is in game coordinates (same as brick
        // panel)
        int topmostBlockGameRow = gameY + shapeTopRow;

        // EXACT same formula as brick panel
        double pixelX = gameX * BRICK_SIZE;
        double pixelY = (gameY - BUFFER_ROWS) * BRICK_SIZE;

        // FIXED: Hide ghost if topmost block is still in buffer zone (same logic as
        // brick)
        if (topmostBlockGameRow < BUFFER_ROWS) {
            ghostPanel.setVisible(false);
        } else {
            ghostPanel.setVisible(true);
            ghostPanel.setLayoutX(pixelX);
            ghostPanel.setLayoutY(pixelY);
        }
    }

    public void refreshBrick(ViewData brick) {
        int[][] brickShape = brick.getBrickData();

        if (rectangles == null ||
                rectangles.length != brickShape.length ||
                rectangles[0].length != brickShape[0].length) {
            rebuildBrickRectangles(brickShape);
            rebuildGhostRectangles(brickShape);
        }

        // FIXED: Always update position and appearance, even when paused (for initial
        // display)
        updateBrickPanelPosition(brick);

        for (int i = 0; i < brickShape.length; i++) {
            for (int j = 0; j < brickShape[i].length; j++) {
                setRectangleData(brickShape[i][j], rectangles[i][j]);
            }
        }

        // Ghost position and appearance
        updateGhostPanelPosition(brick);

        for (int i = 0; i < brickShape.length; i++) {
            for (int j = 0; j < brickShape[i].length; j++) {
                int value = brickShape[i][j];
                Rectangle r = ghostRectangles[i][j];

                if (value != 0) {
                    r.setFill(Color.TRANSPARENT);
                    r.setStroke(getFillColor(value));
                    r.setStrokeWidth(2);
                    r.setOpacity(0.5);
                } else {
                    r.setFill(Color.TRANSPARENT);
                    r.setStroke(Color.TRANSPARENT);
                }
            }
        }

        // Keep border on top after refresh
        if (boardBorder != null) {
            boardBorder.toFront();
        }
    }

    public void refreshGameBackground(int[][] board) {
        // FIXED: Only update visible rows (game rows 2-21 map to display rows 0-19)
        // Prevent "chopped" bottom bricks by ensuring we only display rows 0-19
        // Game matrix has 22 rows (0-21), but we only display rows 2-21 (display rows
        // 0-19)
        for (int gameRow = BUFFER_ROWS; gameRow < board.length; gameRow++) {
            int displayRow = gameRow - BUFFER_ROWS;

            // CRITICAL: Only update if within visible display range (0-19)
            // This prevents pieces from being displayed beyond the visible area
            if (displayRow >= 0 && displayRow < BOARD_ROWS) {
                for (int col = 0; col < board[gameRow].length; col++) {
                    setRectangleData(board[gameRow][col], displayMatrix[displayRow][col]);
                }
            }
        }
    }

    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(getFillColor(color));
        rectangle.setArcHeight(8);
        rectangle.setArcWidth(8);

        // Apply outline if enabled and it's a block (not empty space)
        if (color != 0) {
            // For filled cells, check if outline is enabled
            // For custom pieces, check per-piece settings; for others, use global setting
            boolean useOutline = false;
            if (color == 8) {
                // Custom piece - we'll need to get piece name from context
                // For now, use global setting as fallback
                useOutline = GameSettings.getInstance().isOutlineEnabled();
            } else {
                useOutline = GameSettings.getInstance().isOutlineEnabled();
            }

            if (useOutline) {
                rectangle.setStroke(Color.WHITE);
                rectangle.setStrokeWidth(1);
            } else {
                rectangle.setStroke(Color.TRANSPARENT);
            }
        } else {
            // Keep grid lines for empty cells - make them more visible
            if (SettingsManager.getInstance().isGridLinesEnabled()) {
                rectangle.setStroke(Color.rgb(50, 70, 100, 0.5));
            } else {
                rectangle.setStroke(Color.TRANSPARENT);
            }
            rectangle.setStrokeWidth(0.8);
        }
    }

    private void moveDown(MoveEvent event) {
        if (!isPause.get()) {
            DownData downData = eventListener.onDownEvent(event);

            // FIXED: Check lines cleared explicitly - only reset combo when linesCleared ==
            // 0
            int linesCleared = 0;
            // Only update combo if the piece locked (clearRow is not null)
            if (downData.getClearRow() != null) {
                linesCleared = downData.getClearRow().getLinesRemoved();

                if (linesCleared > 0) {
                    // Play line clear sound
                    switch (linesCleared) {
                        case 1 -> SoundManager.getInstance().playSound(SoundManager.SFX_LINE_SINGLE);
                        case 2 -> SoundManager.getInstance().playSound(SoundManager.SFX_LINE_DOUBLE);
                        case 3 -> SoundManager.getInstance().playSound(SoundManager.SFX_LINE_TRIPLE);
                        case 4 -> SoundManager.getInstance().playSound(SoundManager.SFX_LINE_TETRIS);
                    }

                    // Add lines to level system
                    int oldLevel = levelSystem.getLevel();
                    levelSystem.addLinesCleared(linesCleared);
                    if (levelSystem.getLevel() > oldLevel) {
                        SoundManager.getInstance().playSound(SoundManager.SFX_LEVEL_UP);
                        showLevelUpNotification(levelSystem.getLevel());
                    }

                    // Update drop speed based on new gravity
                    updateDropSpeed();

                    // IF linesCleared > 0: Increment the combo
                    comboCount++;
                    // FIXED: Show COMBO for ANY back-to-back clear (comboCount >= 2 means at least
                    // 2 consecutive clears)
                    boolean showCombo = comboCount >= 2;
                    int scoreBonus = downData.getClearRow().getScoreBonus();

                    // Build clear text with T-Spin and back-to-back support
                    String clearText;
                    if (downData.getClearRow().isTSpin()) {
                        // T-Spin clears
                        switch (linesCleared) {
                            case 0:
                                clearText = "T-SPIN\n+" + scoreBonus;
                                break;
                            case 1:
                                clearText = "T-SPIN\nSINGLE\n+" + scoreBonus;
                                break;
                            case 2:
                                clearText = "T-SPIN\nDOUBLE\n+" + scoreBonus;
                                break;
                            case 3:
                                clearText = "T-SPIN\nTRIPLE\n+" + scoreBonus;
                                break;
                            default:
                                clearText = "T-SPIN\n+" + scoreBonus;
                        }
                    } else {
                        // Regular clears
                        clearText = switch (linesCleared) {
                            case 1 -> "SINGLE\n+" + scoreBonus;
                            case 2 -> "DOUBLE\n+" + scoreBonus;
                            case 3 -> "TRIPLE\n+" + scoreBonus;
                            case 4 -> "TETRIS\n+" + scoreBonus;
                            default -> "+" + scoreBonus;
                        };
                    }

                    // Back-to-Back indicator
                    if (downData.getClearRow().isBackToBack()) {
                        clearText += "\nBACK-TO-BACK";
                    }

                    // FIXED: Show COMBO for any consecutive clear (Tetris then Single, or any
                    // combination)
                    if (showCombo) {
                        clearText += "\nCOMBO x" + (comboCount - 1);
                    }

                    System.out.println("Line clear: " + clearText + " (combo: " + comboCount + ")");

                    NotificationPanel notificationPanel = new NotificationPanel(clearText);
                    groupNotification.getChildren().add(notificationPanel);
                    // FIXED: Center notification in the game board area using actual container
                    // bounds
                    // Use rootPane bounds if available, otherwise fall back to calculated size
                    double boardWidth = (rootPane != null && rootPane.getWidth() > 0)
                            ? rootPane.getWidth()
                            : (BOARD_COLS * BRICK_SIZE);
                    double boardHeight = (rootPane != null && rootPane.getHeight() > 0)
                            ? rootPane.getHeight()
                            : (BOARD_ROWS * BRICK_SIZE);
                    double centerX = boardWidth / 2.0;
                    double centerY = boardHeight / 2.0;
                    notificationPanel.setLayoutX(centerX - notificationPanel.getMinWidth() / 2.0);
                    notificationPanel.setLayoutY(centerY - notificationPanel.getMinHeight() / 2.0);
                    notificationPanel.showScore(groupNotification.getChildren());
                } else {
                    // ELSE (if and only if linesCleared == 0): Reset the combo
                    comboCount = 0;
                }
            }

            if (!isGameOver.get()) {
                refreshBrick(downData.getViewData());
            }
            updateNextPieces(downData.getViewData().getNextBrickData());
        }
        gamePanel.requestFocus();
    }

    /**
     * Update the drop speed based on current gravity level
     * Called after lines are cleared and level changes
     */
    private void updateDropSpeed() {
        if (timeLine == null)
            return;

        // Stop current timeline
        timeLine.stop();

        // Get new drop interval from level system
        int dropIntervalMs = levelSystem.getDropIntervalMs();

        // At 20G, pieces should instantly drop to lock position
        // This is handled in the game logic, but we still need a minimal interval
        if (levelSystem.is20G()) {
            System.out.println("⚡ 20G MODE ACTIVATED - Instant drop!");
        }

        System.out.println("Drop speed updated: Level " + levelSystem.getLevel() +
                ", Gravity " + levelSystem.getGravity() + "G" +
                ", Interval " + dropIntervalMs + "ms");

        // Create new timeline with updated speed
        timeLine = new Timeline(new KeyFrame(
                Duration.millis(dropIntervalMs),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))));
        timeLine.setCycleCount(Timeline.INDEFINITE);

        // Restart if not paused
        if (!isPause.get()) {
            timeLine.play();
        }
    }

    private void hardDrop(MoveEvent event) {
        if (!isPause.get()) {
            DownData downData = eventListener.onHardDropEvent(event);

            int linesCleared = 0;
            // FIXED: Check lines cleared explicitly - only reset combo when linesCleared ==
            // 0
            // FIXED: Combo logic - ensure clearRow is present (hard drop always locks)
            if (downData.getClearRow() != null) {
                SoundManager.getInstance().playSound(SoundManager.SFX_HARD_DROP);
                linesCleared = downData.getClearRow().getLinesRemoved();

                if (linesCleared > 0) {
                    // Play line clear sound
                    switch (linesCleared) {
                        case 1 -> SoundManager.getInstance().playSound(SoundManager.SFX_LINE_SINGLE);
                        case 2 -> SoundManager.getInstance().playSound(SoundManager.SFX_LINE_DOUBLE);
                        case 3 -> SoundManager.getInstance().playSound(SoundManager.SFX_LINE_TRIPLE);
                        case 4 -> SoundManager.getInstance().playSound(SoundManager.SFX_LINE_TETRIS);
                    }

                    // Add lines to level system
                    int oldLevel = levelSystem.getLevel();
                    levelSystem.addLinesCleared(linesCleared);
                    if (levelSystem.getLevel() > oldLevel) {
                        SoundManager.getInstance().playSound(SoundManager.SFX_LEVEL_UP);
                        showLevelUpNotification(levelSystem.getLevel());
                    }

                    // Update drop speed based on new gravity
                    updateDropSpeed();

                    // IF linesCleared > 0: Increment the combo
                    comboCount++;
                    // FIXED: Show COMBO for ANY back-to-back clear (comboCount >= 2 means at least
                    // 2 consecutive clears)
                    boolean showCombo = comboCount >= 2;
                    int scoreBonus = downData.getClearRow().getScoreBonus();

                    // Build clear text with T-Spin and back-to-back support
                    String clearText;
                    if (downData.getClearRow().isTSpin()) {
                        // T-Spin clears
                        switch (linesCleared) {
                            case 0:
                                clearText = "T-SPIN\n+" + scoreBonus;
                                break;
                            case 1:
                                clearText = "T-SPIN\nSINGLE\n+" + scoreBonus;
                                break;
                            case 2:
                                clearText = "T-SPIN\nDOUBLE\n+" + scoreBonus;
                                break;
                            case 3:
                                clearText = "T-SPIN\nTRIPLE\n+" + scoreBonus;
                                break;
                            default:
                                clearText = "T-SPIN\n+" + scoreBonus;
                        }
                    } else {
                        // Regular clears
                        clearText = switch (linesCleared) {
                            case 1 -> "SINGLE\n+" + scoreBonus;
                            case 2 -> "DOUBLE\n+" + scoreBonus;
                            case 3 -> "TRIPLE\n+" + scoreBonus;
                            case 4 -> "TETRIS\n+" + scoreBonus;
                            default -> "+" + scoreBonus;
                        };
                    }

                    // Back-to-Back indicator
                    if (downData.getClearRow().isBackToBack()) {
                        clearText += "\nBACK-TO-BACK";
                    }

                    // FIXED: Show COMBO for any consecutive clear (Tetris then Single, or any
                    // combination)
                    if (showCombo) {
                        clearText += "\nCOMBO x" + (comboCount - 1);
                    }

                    System.out.println("Line clear: " + clearText + " (combo: " + comboCount + ")");

                    NotificationPanel notificationPanel = new NotificationPanel(clearText);
                    groupNotification.getChildren().add(notificationPanel);
                    // FIXED: Center notification in the game board area using actual container
                    // bounds
                    // Use rootPane bounds if available, otherwise fall back to calculated size
                    double boardWidth = (rootPane != null && rootPane.getWidth() > 0)
                            ? rootPane.getWidth()
                            : (BOARD_COLS * BRICK_SIZE);
                    double boardHeight = (rootPane != null && rootPane.getHeight() > 0)
                            ? rootPane.getHeight()
                            : (BOARD_ROWS * BRICK_SIZE);
                    double centerX = boardWidth / 2.0;
                    double centerY = boardHeight / 2.0;
                    notificationPanel.setLayoutX(centerX - notificationPanel.getMinWidth() / 2.0);
                    notificationPanel.setLayoutY(centerY - notificationPanel.getMinHeight() / 2.0);
                    notificationPanel.showScore(groupNotification.getChildren());
                } else {
                    // ELSE (if and only if linesCleared == 0): Reset the combo
                    comboCount = 0;
                }
            }

            if (!isGameOver.get()) {
                refreshBrick(downData.getViewData());
            }
            updateNextPieces(downData.getViewData().getNextBrickData());
        }
        gamePanel.requestFocus();
    }

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void bindScore(IntegerProperty integerProperty) {
        if (scoreLabel != null) {
            scoreLabel.textProperty().bind(integerProperty.asString("%d"));
        }
        // levelLabel and linesLabel are now bound to LevelSystem properties in
        // initialize()
        // Don't set text here to avoid "bound value cannot be set" error

        integerProperty.addListener((obs, oldVal, newVal) -> {
            currentScore = newVal.intValue();
        });
    }

    private void updateHighScoreDisplay() {
        if (highScoreLabel != null) {
            highScoreLabel.setText(String.valueOf(highScoreManager.getHighScore()));
        }
    }

    public void gameOver() {
        SoundManager.getInstance().playSound(SoundManager.SFX_GAME_OVER);
        timeLine.stop();
        isGameOver.setValue(true);
        boolean isHighScore = highScoreManager.isHighScore(currentScore);

        if (isHighScore && nameInputDialog != null && nameInputContainer != null) {
            if (gameOverContainer != null) {
                gameOverContainer.setVisible(false);
                gameOverContainer.setMouseTransparent(true);
            }

            nameInputDialog.setScore(currentScore);
            nameInputDialog.reset();
            nameInputContainer.setVisible(true);
            nameInputContainer.setMouseTransparent(false);
            nameInputDialog.getNameTextField().requestFocus();
        } else {
            if (nameInputContainer != null) {
                nameInputContainer.setVisible(false);
                nameInputContainer.setMouseTransparent(true);
            }

            if (gameOverPanel != null && gameOverContainer != null) {
                displayHighScores("", currentScore);
                gameOverPanel.setVisible(true);
                gameOverContainer.setVisible(true);
                gameOverContainer.setMouseTransparent(false);
            }
        }
        updateOverlayLayer();
    }

    private void displayHighScores(String playerName, int playerScore) {
        if (gameOverPanel == null)
            return;
        List<HighScoreManager.HighScoreEntry> highScores = highScoreManager.getHighScores();
        gameOverPanel.updateHighScoresFromList(highScores, playerScore);
    }

    public void newGame(ActionEvent actionEvent) {
        // FIXED: Stop and clear timeline before resetting
        if (timeLine != null) {
            timeLine.stop();
        }

        if (gameOverPanel != null)
            gameOverPanel.setVisible(false);
        if (gameOverContainer != null) {
            gameOverContainer.setVisible(false);
            gameOverContainer.setMouseTransparent(true);
        }
        if (nameInputContainer != null) {
            nameInputContainer.setVisible(false);
            nameInputContainer.setMouseTransparent(true);
        }
        pauseMenuPanel.setVisible(false);
        // Reset combo tracking on new game
        comboCount = 0;

        // Reset level system
        levelSystem.reset();

        // Reset rectangles to force rebuild in refreshBrick
        rectangles = null;
        ghostRectangles = null;
        if (groupNotification != null) {
            groupNotification.getChildren().clear();
        }

        // FIXED: Ensure brick and ghost panels are visible for new game
        if (brickPanel != null) {
            brickPanel.setVisible(true);
            brickPanel.getChildren().clear(); // Clear any old rectangles
        }
        if (ghostPanel != null) {
            ghostPanel.setVisible(true);
            ghostPanel.getChildren().clear(); // Clear any old rectangles
        }

        // FIXED: Create new game and refresh display
        eventListener.createNewGame();

        // FIXED: Recreate timeline to ensure it works properly for new game
        // Use level system to get initial drop speed
        if (timeLine != null) {
            timeLine.stop();
        }
        int initialDropInterval = levelSystem.getDropIntervalMs();
        timeLine = new Timeline(new KeyFrame(
                Duration.millis(initialDropInterval),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))));
        timeLine.setCycleCount(Timeline.INDEFINITE);

        gamePanel.requestFocus();
        isPause.setValue(false);
        isGameOver.setValue(false);
        currentScore = 0;
        updateOverlayLayer();

        // Start timeline after a short delay to ensure game is fully initialized
        javafx.application.Platform.runLater(() -> {
            if (timeLine != null && !isPause.get()) {
                timeLine.play();
            }
        });
    }

    public void pauseGame(ActionEvent actionEvent) {
        gamePanel.requestFocus();
    }

    private void showLevelUpNotification(int newLevel) {
        if (levelUpLabel == null)
            return;

        levelUpLabel.setText("LEVEL " + newLevel + "!");
        levelUpLabel.setVisible(true);
        // Move label up to avoid blocking line clear notifications
        levelUpLabel.setTranslateY(-150);
        levelUpLabel.setOpacity(0);
        levelUpLabel.setScaleX(0.5);
        levelUpLabel.setScaleY(0.5);

        // Create animation sequence
        // 1. Fade in and scale up
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), levelUpLabel);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(300), levelUpLabel);
        scaleUp.setFromX(0.5);
        scaleUp.setFromY(0.5);
        scaleUp.setToX(1.2);
        scaleUp.setToY(1.2);

        ParallelTransition appear = new ParallelTransition(fadeIn, scaleUp);

        // 2. Pulse/Glow (using Scale)
        ScaleTransition pulse = new ScaleTransition(Duration.millis(200), levelUpLabel);
        pulse.setFromX(1.2);
        pulse.setFromY(1.2);
        pulse.setToX(1.0);
        pulse.setToY(1.0);

        // 3. Hold
        PauseTransition hold = new PauseTransition(Duration.millis(800));

        // 4. Fade out
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), levelUpLabel);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        SequentialTransition sequence = new SequentialTransition(appear, pulse, hold, fadeOut);
        sequence.setOnFinished(e -> levelUpLabel.setVisible(false));
        sequence.play();
    }

}