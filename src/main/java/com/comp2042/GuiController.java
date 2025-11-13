package com.comp2042;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.effect.Reflection;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class GuiController implements Initializable {

    private static final int BRICK_SIZE = 20;

    @FXML
    private GridPane gamePanel;

    @FXML private GridPane holdPieceGrid;
    @FXML private VBox nextPiecesContainer;

    @FXML private Label scoreLabel;
    @FXML private Label currentScoreLabel;
    @FXML private Label levelLabel;
    @FXML private Label linesLabel;

    @FXML
    private Group groupNotification;

    @FXML
    private Pane brickPanel;

    @FXML
    private Pane ghostPanel;

    @FXML
    private Pane rootPane;

    @FXML
    private BorderPane gameBoard;

    @FXML
    private StackPane pauseContainer;

    @FXML
    private PauseMenuPanel pauseMenuPanel;

    @FXML
    private StackPane controlsContainer;

    @FXML
    private StackPane overlayLayer;

    @FXML
    private ControlsPanel controlsPanel;

    @FXML
    private GameOverPanel gameOverPanel;

    @FXML
    private javafx.scene.control.Button pauseButton;

    @FXML
    public void onPauseButtonHover() {
        pauseButton.setStyle(
                "-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white; " +
                        "-fx-background-color: rgba(120, 125, 167, 0.9); -fx-border-color: white; " +
                        "-fx-border-width: 2; -fx-padding: 10 15; -fx-cursor: hand; " +
                        "-fx-background-radius: 5; -fx-border-radius: 5;"
        );
    }

    @FXML
    public void onPauseButtonExit() {
        pauseButton.setStyle(
                "-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white; " +
                        "-fx-background-color: rgba(90, 95, 127, 0.7); -fx-border-color: white; " +
                        "-fx-border-width: 2; -fx-padding: 10 15; -fx-cursor: hand; " +
                        "-fx-background-radius: 5; -fx-border-radius: 5;"
        );
    }

    @FXML
    public void toggleDebugGrids() {
        showDebugGrids = !showDebugGrids;
        if (showDebugGrids) {
            createDebugGrids();
        } else {
            removeDebugGrids();
        }
    }

    private Rectangle[][] displayMatrix;

    private InputEventListener eventListener;

    private Rectangle[][] rectangles;

    private Rectangle[][] ghostRectangles;

    private double cellWidth;
    private double cellHeight;

    private Timeline timeLine;

    private final BooleanProperty isPause = new SimpleBooleanProperty();

    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    private GridPane brickMatrixGrid;

    private GridPane displayMatrixGrid;

    private boolean showDebugGrids = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Ensure cell dimensions match brick size exactly (no gaps)
        cellWidth = BRICK_SIZE;
        cellHeight = BRICK_SIZE;

        // Configure GridPane to have no padding and align to top-left
        gamePanel.setHgap(0);
        gamePanel.setVgap(0);
        gamePanel.setAlignment(javafx.geometry.Pos.TOP_LEFT);
        // Ensure GridPane has no padding that could offset the cells
        gamePanel.setPadding(new Insets(0));

        Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 38);
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        gamePanel.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (!isPause.get() && !isGameOver.get()) {
                    if (keyEvent.getCode() == KeyCode.LEFT || keyEvent.getCode() == KeyCode.A) {
                        refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.D) {
                        refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.UP || keyEvent.getCode() == KeyCode.W) {
                        refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.S) {
                        moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
                        keyEvent.consume();
                    }
                }
                if (keyEvent.getCode() == KeyCode.P || keyEvent.getCode() == KeyCode.ESCAPE) {
                    togglePause();
                    keyEvent.consume();
                }

                if (keyEvent.getCode() == KeyCode.N) {
                    newGame(null);
                }
                // Add this inside the gamePanel.setOnKeyPressed handler
                if (keyEvent.getCode() == KeyCode.G) {
                    toggleDebugGrids();
                    keyEvent.consume();
                }
            }
        });
        gameOverPanel.setVisible(false);
        pauseMenuPanel.setVisible(false);
        if (pauseContainer != null) {
            pauseContainer.setVisible(false);
            pauseContainer.setMouseTransparent(true);
        }

        pauseMenuPanel.getResumeButton().setOnAction(e -> togglePause());
        pauseMenuPanel.getMainMenuButton().setOnAction(e -> {
            System.out.println("Main menu clicked");
        });
        pauseMenuPanel.getControlsButton().setOnAction(e -> showControls());
        pauseMenuPanel.getQuitButton().setOnAction(e -> System.exit(0));

        if (controlsPanel != null && controlsContainer != null) {
            controlsContainer.setVisible(false);
            controlsContainer.setMouseTransparent(true);
            controlsPanel.getDoneButton().setOnAction(e -> hideControls());
            controlsPanel.getPrevButton().setOnAction(e -> {
                System.out.println("Previous clicked");
            });
            controlsPanel.getNextButton().setOnAction(e -> {
                System.out.println("Next clicked");
            });
        }
        if (overlayLayer != null) {
            overlayLayer.setMouseTransparent(true);
        }

        final Reflection reflection = new Reflection();
        reflection.setFraction(0.8);
        reflection.setTopOpacity(0.9);
        reflection.setTopOffset(-12);

        brickPanel.setManaged(false);
        ghostPanel.setManaged(false);
        Rectangle brickClip = new Rectangle(rootPane.getPrefWidth(), rootPane.getPrefHeight());
        Rectangle ghostClip = new Rectangle(rootPane.getPrefWidth(), rootPane.getPrefHeight());
        brickPanel.setClip(brickClip);
        ghostPanel.setClip(ghostClip);
        rootPane.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
            brickClip.setWidth(newBounds.getWidth());
            brickClip.setHeight(newBounds.getHeight());
            ghostClip.setWidth(newBounds.getWidth());
            ghostClip.setHeight(newBounds.getHeight());
        });
    }

    private void createDebugGrids() {
        // Remove existing grids if any
        removeDebugGrids();

        // Create brick matrix grid overlay (RED)
        brickMatrixGrid = new GridPane();
        brickMatrixGrid.setMouseTransparent(true);
        brickMatrixGrid.setStyle("-fx-background-color: transparent;");

        // Create display matrix grid overlay (BLUE)
        displayMatrixGrid = new GridPane();
        displayMatrixGrid.setMouseTransparent(true);
        displayMatrixGrid.setStyle("-fx-background-color: transparent;");

        // Add to root pane
        if (!rootPane.getChildren().contains(brickMatrixGrid)) {
            rootPane.getChildren().add(brickMatrixGrid);
        }
        if (!rootPane.getChildren().contains(displayMatrixGrid)) {
            rootPane.getChildren().add(displayMatrixGrid);
        }
    }

    private void removeDebugGrids() {
        if (brickMatrixGrid != null) {
            rootPane.getChildren().remove(brickMatrixGrid);
            brickMatrixGrid = null;
        }
        if (displayMatrixGrid != null) {
            rootPane.getChildren().remove(displayMatrixGrid);
            displayMatrixGrid = null;
        }
    }

    // Call this method in refreshBrick to update the brick matrix visualization
    private void updateBrickMatrixDebug(ViewData brick) {
        if (!showDebugGrids || brickMatrixGrid == null) return;

        brickMatrixGrid.getChildren().clear();
        Point2D origin = getBoardOrigin();
        brickMatrixGrid.setLayoutX(origin.getX() + brick.getxPosition() * cellWidth);
        brickMatrixGrid.setLayoutY(origin.getY() + brick.getyPosition() * cellHeight);

        int[][] brickData = brick.getBrickData();
        for (int i = 0; i < brickData.length; i++) {
            for (int j = 0; j < brickData[i].length; j++) {
                Rectangle cell = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                cell.setFill(Color.TRANSPARENT);
                cell.setStroke(Color.RED);
                cell.setStrokeWidth(2);
                cell.setOpacity(0.6);
                brickMatrixGrid.add(cell, j, i);
            }
        }
    }

    // Call this method in refreshGameBackground to update the display matrix visualization
    private void updateDisplayMatrixDebug() {
        if (!showDebugGrids || displayMatrixGrid == null) return;

        displayMatrixGrid.getChildren().clear();
        Point2D origin = getBoardOrigin();
        displayMatrixGrid.setLayoutX(origin.getX());
        displayMatrixGrid.setLayoutY(origin.getY());

        if (displayMatrix != null) {
            for (int i = 2; i < displayMatrix.length; i++) {
                for (int j = 0; j < displayMatrix[i].length; j++) {
                    Rectangle cell = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                    cell.setFill(Color.TRANSPARENT);
                    cell.setStroke(Color.BLUE);
                    cell.setStrokeWidth(1);
                    cell.setOpacity(0.4);
                    displayMatrixGrid.add(cell, j, i);
                }
            }
        }
    }

    public void togglePause() {
        if (isGameOver.get()) {
            return;
        }

        isPause.set(!isPause.get());
        pauseMenuPanel.setVisible(isPause.get());
        if (pauseContainer != null) {
            pauseContainer.setVisible(isPause.get());
            pauseContainer.setMouseTransparent(!isPause.get());
        }
        if (!isPause.get()) {
            hideControls();
        }
        updateOverlayLayer();

        if (isPause.get()) {
            timeLine.pause();
        } else {
            timeLine.play();
            gamePanel.requestFocus();
        }
    }

    public void updateNextPieces(List<int[][]> nextShapes) {
        if (nextPiecesContainer == null) {
            return;
        }
        nextPiecesContainer.getChildren().clear();

        for (int[][] shape : nextShapes) {
            GridPane nextGrid = new GridPane();
            nextGrid.setHgap(0);
            nextGrid.setVgap(0);
            nextGrid.setPrefSize(100, 80);
            nextGrid.setMinSize(100, 80);
            nextGrid.setMaxSize(100, 80);
            nextGrid.setAlignment(javafx.geometry.Pos.CENTER);
            nextGrid.setStyle("-fx-background-color: #1a2332; -fx-border-color: #4a5f7f; -fx-border-width: 2;");

            int rows = shape.length;
            int cols = shape[0].length;

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (shape[i][j] != 0) {
                        Rectangle cell = new Rectangle(18, 18);
                        cell.setFill(getColor(shape[i][j]));
                        cell.setStroke(Color.BLACK);
                        cell.setStrokeWidth(1);
                        nextGrid.add(cell, j, i);
                    }
                }
            }

            nextPiecesContainer.getChildren().add(nextGrid);
        }
    }

    public void updateHoldPiece(int[][] holdShape) {
        if (holdPieceGrid == null) return;
        holdPieceGrid.getChildren().clear();

        if (holdShape == null) {
            return;
        }

        int rows = holdShape.length;
        int cols = holdShape[0].length;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (holdShape[i][j] != 0) {
                    Rectangle cell = new Rectangle(20, 20);
                    cell.setFill(getColor(holdShape[i][j]));
                    cell.setStroke(Color.BLACK);
                    cell.setStrokeWidth(1);
                    holdPieceGrid.add(cell, j, i);
                }
            }
        }
    }

    private void showControls() {
        if (controlsContainer != null && controlsPanel != null) {
            controlsContainer.setVisible(true);
            controlsContainer.setMouseTransparent(false);
            pauseMenuPanel.setVisible(false);
            if (pauseContainer != null) {
                pauseContainer.setVisible(false);
                pauseContainer.setMouseTransparent(true);
            }
        }
        updateOverlayLayer();
    }

    private void hideControls() {
        if (controlsContainer != null) {
            controlsContainer.setVisible(false);
            controlsContainer.setMouseTransparent(true);
            if (isPause.get()) {
                pauseMenuPanel.setVisible(true);
                if (pauseContainer != null) {
                    pauseContainer.setVisible(true);
                    pauseContainer.setMouseTransparent(false);
                }
            }
        }
        updateOverlayLayer();
    }

    private void updateOverlayLayer() {
        if (overlayLayer != null) {
            boolean overlaysVisible =
                    (pauseContainer != null && pauseContainer.isVisible()) ||
                            (controlsContainer != null && controlsContainer.isVisible());
            overlayLayer.setMouseTransparent(!overlaysVisible);
        }
    }

    private Paint getFillColor(int i) {
        return switch (i) {
            case 0 -> Color.TRANSPARENT;
            case 1 -> Color.CYAN;
            case 2 -> Color.GREEN;
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
            case 2 -> Color.GREEN;
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
                rectangles[i][j] = rectangle;
                // Position each rectangle within the 4x4 grid
                // i = row in brick matrix (0-3), j = column in brick matrix (0-3)
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
                ghostRectangles[i][j] = rectangle;
                // Position each rectangle within the 4x4 grid, aligned with brick rectangles
                rectangle.setLayoutX(j * BRICK_SIZE);
                rectangle.setLayoutY(i * BRICK_SIZE);
                ghostPanel.getChildren().add(rectangle);
            }
        }
    }

    public void initGameView(int[][] boardMatrix, ViewData viewData) {

        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int i = 0; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i);
            }
        }

        rebuildBrickRectangles(viewData.getBrickData());
        rebuildGhostRectangles(viewData.getBrickData());

        updateBrickPanelPosition(viewData);
        updateGhostPanelPosition(viewData);

        timeLine = new Timeline(new KeyFrame(
                Duration.millis(400),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();

        updateNextPieces(viewData.getNextBrickData());
    }

    private Point2D getBoardOrigin() {
        // Since GridPane, brickPanel, and ghostPanel are all positioned at (0,0) in rootPane,
        // and GridPane has no padding, the first grid cell starts at (0,0)
        // The GridPane's top-left corner is the origin
        Bounds gridBounds = gamePanel.getBoundsInLocal();
        Bounds gridInRootPane = gamePanel.localToParent(gridBounds);
        // Account for any potential offset - should be (0,0) but calculate for safety
        return new Point2D(Math.max(0, gridInRootPane.getMinX()), Math.max(0, gridInRootPane.getMinY()));
    }

    private void updateBrickPanelPosition(ViewData brick) {
        // Since all panels start at (0,0) in rootPane, we just need to offset by grid position
        // Each grid cell is exactly BRICK_SIZE pixels
        // The brick position is in matrix coordinates: x=column, y=row (all rows 0-19 visible)
        Point2D origin = getBoardOrigin();
        double x = origin.getX() + brick.getxPosition() * BRICK_SIZE;
        double y = origin.getY() + brick.getyPosition() * BRICK_SIZE;
        brickPanel.setLayoutX(x);
        brickPanel.setLayoutY(y);
    }

    private void updateGhostPanelPosition(ViewData brick) {
        // Same calculation as brick panel, but using ghost position
        Point2D origin = getBoardOrigin();
        double x = origin.getX() + brick.getGhostXPosition() * BRICK_SIZE;
        double y = origin.getY() + brick.getGhostYPosition() * BRICK_SIZE;
        ghostPanel.setLayoutX(x);
        ghostPanel.setLayoutY(y);
    }


    private void refreshBrick(ViewData brick) {

        if (rectangles == null ||
                rectangles.length != brick.getBrickData().length ||
                rectangles[0].length != brick.getBrickData()[0].length) {
            rebuildBrickRectangles(brick.getBrickData());
            rebuildGhostRectangles(brick.getBrickData());
        }

        if (!isPause.get()) {
            updateBrickPanelPosition(brick);

            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    setRectangleData(brick.getBrickData()[i][j], rectangles[i][j]);
                }
            }
        }

        updateGhostPanelPosition(brick);

        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                int value = brick.getBrickData()[i][j];
                Rectangle r = ghostRectangles[i][j];

                if (value != 0) {
                    r.setFill(Color.TRANSPARENT);
                    r.setStroke(getFillColor(value));
                    r.setStrokeWidth(2);
                    r.setOpacity(0.4);
                } else {
                    r.setFill(Color.TRANSPARENT);
                    r.setStroke(Color.TRANSPARENT);
                }

                r.setArcHeight(9);
                r.setArcWidth(9);
            }
        }
        // Add debug visualization
        updateBrickMatrixDebug(brick);
    }

    public void refreshGameBackground(int[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
        // Add debug visualization
        updateDisplayMatrixDebug();
    }

    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(getFillColor(color));
        rectangle.setArcHeight(9);
        rectangle.setArcWidth(9);
    }

    private void moveDown(MoveEvent event) {
        if (!isPause.get()) {
            DownData downData = eventListener.onDownEvent(event);
            if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
                NotificationPanel notificationPanel = new NotificationPanel("+" + downData.getClearRow().getScoreBonus());
                groupNotification.getChildren().add(notificationPanel);
                notificationPanel.showScore(groupNotification.getChildren());
            }
            refreshBrick(downData.getViewData());
            updateNextPieces(downData.getViewData().getNextBrickData());
        }
        gamePanel.requestFocus();
    }

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void updateNextPiece(int[][] nextShape) {
    }

    public void bindScore(IntegerProperty integerProperty) {
        if (scoreLabel != null) {
            scoreLabel.textProperty().bind(integerProperty.asString("%d"));
        }
        if (currentScoreLabel != null) {
            currentScoreLabel.textProperty().bind(integerProperty.asString("%d"));
        }
        if (levelLabel != null) {
            levelLabel.setText("1");
        }
        if (linesLabel != null) {
            linesLabel.setText("0");
        }
    }

    public void gameOver() {
        timeLine.stop();
        gameOverPanel.setVisible(true);
        isGameOver.setValue(true);
    }

    public void newGame(ActionEvent actionEvent) {
        timeLine.stop();
        gameOverPanel.setVisible(false);
        pauseMenuPanel.setVisible(false);
        eventListener.createNewGame();
        gamePanel.requestFocus();
        timeLine.play();
        isPause.setValue(false);
        isGameOver.setValue(false);
    }

    public void pauseGame(ActionEvent actionEvent) {
        gamePanel.requestFocus();
    }
}