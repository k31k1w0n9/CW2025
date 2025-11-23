package com.comp2042;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
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
                System.out.println("✓✓✓ ALL FONTS LOADED SUCCESSFULLY ✓✓✓");
            } else {
                System.err.println("⚠ WARNING: Some fonts failed to load, using fallback");
                if (customFont == null)
                    customFont = Font.font("Arial", 28);
                if (customFontBold == null)
                    customFontBold = Font.font("Arial", 20);
            }
        } catch (Exception e) {
            System.err.println("❌ FONT LOADING ERROR:");
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
        setupGameOverPanel();
        setupNameInputDialog();
        setupOverlayLayer();
        setupBoardDimensions();
        setupBrickPanels();

        if (mainMenuPanel != null && mainMenuContainer != null && gameContainer != null) {
            mainMenuContainer.setVisible(true);
            gameContainer.setVisible(false);

            mainMenuPanel.setStartGameAction(this::startNewGame);

            mainMenuPanel.setControlsAction(this::showControlsFromMainMenu);
            System.out.println("✓ Controls action set");

            mainMenuPanel.setCustomizedAction(() -> System.out.println("Customized clicked"));

            mainMenuPanel.setQuitAction(() -> System.exit(0));
        }

        // Set game panel as focusable for keyboard input
        if (gamePanel != null) {
            gamePanel.setFocusTraversable(true);
            gamePanel.requestFocus();
        }

        System.out.println("✓ GuiController initialization complete");
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

            System.out.println("✓ Fonts applied to all UI elements");
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

        // FIXED: Ensure boardContainer aligns to top for proper alignment with side boxes
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
                    keyEvent.consume();
                    return;
                }
                if (keyBindings.isKeyBound("MOVE_RIGHT", code)) {
                    refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
                    keyEvent.consume();
                    return;
                }
                if (keyBindings.isKeyBound("ROTATE", code) || keyBindings.isKeyBound("ROTATE_LEFT", code)
                        || keyBindings.isKeyBound("ROTATE_RIGHT", code)) {
                    refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)));
                    keyEvent.consume();
                    return;
                }
                if (keyBindings.isKeyBound("SOFT_DROP", code)) {
                    moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
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
                    keyEvent.consume();
                    return;
                }
            }

            if (keyBindings.isKeyBound("PAUSE", code)) {
                togglePause();
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
        pauseMenuPanel.getMainMenuButton().setOnAction(e -> returnToMainMenu());
        pauseMenuPanel.getControlsButton().setOnAction(e -> showControls());
        pauseMenuPanel.getQuitButton().setOnAction(e -> System.exit(0));
    }

    private void setupControlsPanel() {
        System.out.println("Setting up controls panel...");

        if (controlsContainer != null) {
            System.out.println("controlsContainer exists");
            controlsContainer.getChildren().clear();

            ControlsPanel customControlsPanel = new ControlsPanel(keyBindings);
            System.out.println("ControlsPanel created");

            customControlsPanel.setMaxSize(600, 660);
            customControlsPanel.setMinSize(600, 660);
            customControlsPanel.setPrefSize(600, 660);
            System.out.println("ControlsPanel size set: 600x660");

            controlsContainer.getChildren().add(customControlsPanel);
            System.out.println("ControlsPanel added to container");
            System.out.println("Children count: " + controlsContainer.getChildren().size());

            controlsContainer.setVisible(false);
            controlsContainer.setPickOnBounds(false);
            controlsContainer.setMouseTransparent(true);
            System.out.println("ControlsContainer hidden initially");

            customControlsPanel.getDoneButton().setOnAction(e -> {
                System.out.println("Controls Done button clicked");
                hideControls();
            });
            System.out.println("Done button action set");
        } else {
            System.err.println("❌ controlsContainer is NULL!");
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
    }

    private void showControlsFromMainMenu() {
        System.out.println("\n=== SHOW CONTROLS FROM MAIN MENU ===");

        if (mainMenuContainer != null) {
            mainMenuContainer.setVisible(false);
            System.out.println("✓ Main menu hidden");
        }

        if (gameContainer != null) {
            gameContainer.setVisible(false);
            System.out.println("✓ Game container hidden");
        }

        // FIXED: controlsContainer is now at root level, so it can be shown
        // independently
        if (controlsContainer != null) {
            System.out.println("controlsContainer exists");
            System.out.println("Children count: " + controlsContainer.getChildren().size());

            controlsContainer.setVisible(true);
            controlsContainer.setPickOnBounds(true);
            controlsContainer.setMouseTransparent(false);
            controlsContainer.toFront(); // Ensure it's on top
            System.out.println("✓ controlsContainer made VISIBLE and INTERACTIVE");

            System.out.println("Container properties:");
            System.out.println("  Visible: " + controlsContainer.isVisible());
            System.out.println("  MouseTransparent: " + controlsContainer.isMouseTransparent());
            System.out.println("  PickOnBounds: " + controlsContainer.pickOnBoundsProperty().get());

            if (!controlsContainer.getChildren().isEmpty()) {
                ControlsPanel panel = (ControlsPanel) controlsContainer.getChildren().get(0);
                System.out.println("✓ Got ControlsPanel from children");

                panel.setVisible(true);
                System.out.println("✓ ControlsPanel set visible");

                panel.getDoneButton().setOnAction(e -> {
                    System.out.println("Done button clicked - returning to main menu");
                    controlsContainer.setVisible(false);
                    controlsContainer.setPickOnBounds(false);
                    controlsContainer.setMouseTransparent(true);

                    if (gameContainer != null)
                        gameContainer.setVisible(false);
                    if (mainMenuContainer != null)
                        mainMenuContainer.setVisible(true);

                    updateOverlayLayer();
                });
                System.out.println("✓ Done button action configured");

                panel.requestFocus();
                System.out.println("✓ Focus requested");
            } else {
                System.err.println("❌ controlsContainer has NO children!");
            }
        } else {
            System.err.println("❌ controlsContainer is NULL!");
        }

        updateOverlayLayer();
        System.out.println("=== SHOW CONTROLS COMPLETE ===\n");
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

        int pieceSize = (int) (BRICK_SIZE * 0.5);
        int verticalSpacing = 15; // Space between pieces
        int topPadding = 10;
        int bottomPadding = 20;

        for (int idx = 0; idx < nextShapes.size(); idx++) {
            int[][] shape = nextShapes.get(idx);

            StackPane pieceContainer = new StackPane();
            pieceContainer.setMinHeight(70);
            pieceContainer.setMaxHeight(70);
            pieceContainer.setAlignment(Pos.CENTER);

            // Add spacing except for last piece
            if (idx < nextShapes.size() - 1) {
                VBox.setMargin(pieceContainer, new Insets(0, 0, verticalSpacing, 0));
            }

            GridPane nextGrid = new GridPane();
            nextGrid.setHgap(1);
            nextGrid.setVgap(1);
            nextGrid.setAlignment(Pos.CENTER);

            int rows = shape.length;
            int cols = shape[0].length;

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (shape[i][j] != 0) {
                        Rectangle cell = new Rectangle(pieceSize, pieceSize);
                        cell.setFill(getColor(shape[i][j]));
                        cell.setStroke(Color.BLACK);
                        cell.setStrokeWidth(1);
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
        if (controlsContainer != null) {
            // Hide pause menu
            pauseMenuPanel.setVisible(false);
            if (pauseContainer != null) {
                pauseContainer.setVisible(false);
                pauseContainer.setPickOnBounds(false);
                pauseContainer.setMouseTransparent(true);
            }

            // Show controls - FIX visibility properly
            controlsContainer.setVisible(true);
            controlsContainer.setPickOnBounds(true);
            controlsContainer.setMouseTransparent(false);

            if (!controlsContainer.getChildren().isEmpty()) {
                ControlsPanel panel = (ControlsPanel) controlsContainer.getChildren().get(0);
                panel.setVisible(true);
                panel.getDoneButton().setOnAction(e -> hideControls());
                panel.requestFocus();
            }
        }
        updateOverlayLayer();
    }

    private void hideControls() {
        if (controlsContainer != null) {
            controlsContainer.setVisible(false);
            controlsContainer.setPickOnBounds(false);
            controlsContainer.setMouseTransparent(true);

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
        updateOverlayLayer();
        gamePanel.requestFocus();
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
        return switch (i) {
            case 0 -> Color.TRANSPARENT;
            case 1 -> Color.CYAN;
            case 2 -> Color.LIMEGREEN;
            case 3 -> Color.PURPLE;
            case 4 -> Color.YELLOW;
            case 5 -> Color.RED;
            case 6 -> Color.BLUE;
            case 7 -> Color.ORANGE;
            default -> Color.WHITE;
        };
    }

    private Color getColor(int value) {
        return switch (value) {
            case 0 -> Color.TRANSPARENT;
            case 1 -> Color.CYAN;
            case 2 -> Color.LIMEGREEN;
            case 3 -> Color.PURPLE;
            case 4 -> Color.YELLOW;
            case 5 -> Color.RED;
            case 6 -> Color.BLUE;
            case 7 -> Color.ORANGE;
            default -> Color.GRAY;
        };
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
                rectangle.setStroke(Color.rgb(50, 70, 100, 0.3));
                rectangle.setStrokeWidth(0.5);
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
                    // IF linesCleared > 0: Increment the combo
                    comboCount++;
                    // FIXED: Show COMBO for ANY back-to-back clear (comboCount >= 2 means at least
                    // 2 consecutive clears)
                    boolean showCombo = comboCount >= 2;
                    int scoreBonus = downData.getClearRow().getScoreBonus();

                    String clearText = switch (linesCleared) {
                        case 1 -> "SINGLE\n+" + scoreBonus;
                        case 2 -> "DOUBLE\n+" + scoreBonus;
                        case 3 -> "TRIPLE\n+" + scoreBonus;
                        case 4 -> "TETRIS\n+" + scoreBonus;
                        default -> "+" + scoreBonus;
                    };

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

            refreshBrick(downData.getViewData());
            updateNextPieces(downData.getViewData().getNextBrickData());
        }
        gamePanel.requestFocus();
    }

    private void hardDrop(MoveEvent event) {
        if (!isPause.get()) {
            DownData downData = eventListener.onHardDropEvent(event);

            int linesCleared = 0;
            // FIXED: Check lines cleared explicitly - only reset combo when linesCleared == 0
            // FIXED: Combo logic - ensure clearRow is present (hard drop always locks)
            if (downData.getClearRow() != null) {
                linesCleared = downData.getClearRow().getLinesRemoved();

                if (linesCleared > 0) {
                    // IF linesCleared > 0: Increment the combo
                    comboCount++;
                    // FIXED: Show COMBO for ANY back-to-back clear (comboCount >= 2 means at least
                    // 2 consecutive clears)
                    boolean showCombo = comboCount >= 2;
                    int scoreBonus = downData.getClearRow().getScoreBonus();

                    String clearText = switch (linesCleared) {
                        case 1 -> "SINGLE\n+" + scoreBonus;
                        case 2 -> "DOUBLE\n+" + scoreBonus;
                        case 3 -> "TRIPLE\n+" + scoreBonus;
                        case 4 -> "TETRIS\n+" + scoreBonus;
                        default -> "+" + scoreBonus;
                    };

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

            refreshBrick(downData.getViewData());
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
        if (levelLabel != null) {
            levelLabel.setText("1");
        }
        if (linesLabel != null) {
            linesLabel.setText("0");
        }

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
        if (timeLine != null) {
            timeLine.stop();
        }
        timeLine = new Timeline(new KeyFrame(
                Duration.millis(400),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))));
        timeLine.setCycleCount(Timeline.INDEFINITE);

        gamePanel.requestFocus();
        timeLine.play();
        isPause.setValue(false);
        isGameOver.setValue(false);
        currentScore = 0;
        updateOverlayLayer();
    }

    public void pauseGame(ActionEvent actionEvent) {
        gamePanel.requestFocus();
    }

}