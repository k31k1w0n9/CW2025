package com.comp2042.ui;

import java.util.List;

import com.comp2042.system.GameSettings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * UI Panel for the Customisation Mode.
 * Allows users to create custom brick shapes, set their colors, and configure
 * spawn rates.
 * Provides a grid interface for designing pieces and integrates with
 * {@link GameSettings} for persistence.
 */
public class CustomizePanel extends StackPane {

    private VBox piecesListBox;
    private GridPane designGrid;

    private Font customFont;
    private Font customFontBold;

    private Button saveButton;
    private Button resetButton;
    private Button backButton;
    private Button deleteButton;
    private Button outlineToggleButton;
    private Button enableToggleButton;
    private Button size3x3Button;
    private Button size4x4Button;
    private ColorPicker colorPicker;

    private String selectedPiece = "I Piece";
    private Color selectedColor = Color.web("#4ade80");
    private boolean outlineEnabled = true;
    private boolean enableInGame = true;

    private Runnable onBackAction;

    private int gridSize = 4;
    private boolean[][] pieceDesign = new boolean[gridSize][gridSize];
    private GridPane previewGrid;
    private Label previewPlaceholder;
    private Slider spawnSlider;

    /**
     * Constructs a new CustomizePanel.
     * Initializes the complex UI layout including the pieces list, design grid,
     * and settings controls. Loads initial state from {@link GameSettings}.
     */
    public CustomizePanel() {
        // Load custom fonts
        try {
            customFont = Font.loadFont(
                    getClass().getResourceAsStream("/BoutiqueBitmap9x9_1.9.ttf"), 14);
            customFontBold = Font.loadFont(
                    getClass().getResourceAsStream("/BoutiqueBitmap9x9_Bold_1.9.ttf"), 16);
        } catch (Exception e) {
            System.err.println("Could not load custom font, using default");
            customFont = Font.font("Consolas", 14);
            customFontBold = Font.font("Consolas", 16);
        }

        // Load persisted appearance flags first so controls show correct defaults
        GameSettings persistedSettings = GameSettings.getInstance();
        selectedColor = persistedSettings.getCustomPieceColor();
        outlineEnabled = persistedSettings.isOutlineEnabled();
        enableInGame = persistedSettings.isEnableInGame();

        // Transparent background to show game background
        setStyle("-fx-background-color: transparent;");
        setAlignment(Pos.CENTER);

        // Main panel container - matching GameOverPanel style
        VBox panelContainer = new VBox(0);
        panelContainer.setAlignment(Pos.TOP_CENTER);
        panelContainer.setMaxWidth(1050);
        panelContainer.setMaxHeight(650);
        panelContainer.setStyle(
                "-fx-background-color: #2C3E50; " +
                        "-fx-border-color: white; " +
                        "-fx-border-width: 3; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10; " +
                        "-fx-padding: 30;");

        // Top bar with title
        HBox topBar = createTopBar();

        // Main content container
        HBox mainContainer = new HBox(12);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(15, 0, 0, 0));

        // === LEFT PANEL: Pieces List ===
        VBox leftPanel = createLeftPanel();

        // === CENTER PANEL: Design Grid ===
        VBox centerPanel = createCenterPanel();

        // === RIGHT PANEL: Appearance & Gameplay ===
        VBox rightPanel = createRightPanel();

        mainContainer.getChildren().addAll(leftPanel, centerPanel, rightPanel);

        panelContainer.getChildren().addAll(topBar, mainContainer);

        getChildren().add(panelContainer);

        // Load initial state
        loadSettings();
    }

    private void loadSettings() {
        GameSettings settings = GameSettings.getInstance();

        // Load appearance
        selectedColor = settings.getCustomPieceColor();
        outlineEnabled = settings.isOutlineEnabled();
        enableInGame = settings.isEnableInGame();

        // Default selection focuses on first custom slot so saving works immediately
        selectPiece("Custom 1");
        applyAppearanceSettingsToUI();
    }

    private HBox createTopBar() {
        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER);
        topBar.setPadding(new Insets(0, 0, 20, 0));
        topBar.setStyle("-fx-background-color: transparent;");

        // Title - centered
        Label titleLabel = new Label("Tetris Piece Editor");
        titleLabel.setFont(Font.font(customFontBold.getFamily(), 32));
        titleLabel.setStyle("-fx-text-fill: white; " +
                "-fx-effect: dropshadow(gaussian, #DD0584, 15, 0.8, 0, 0);");

        topBar.getChildren().add(titleLabel);

        return topBar;
    }

    private Button createTopButton(String text, String color) {
        Button btn = new Button(text);
        btn.setFont(customFont);
        btn.setMinWidth(80);
        btn.setStyle(
                "-fx-background-color: " + color + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 8 16 8 16; " +
                        "-fx-background-radius: 5; " +
                        "-fx-cursor: hand; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 2);");

        btn.setOnMouseEntered(e -> {
            btn.setStyle(
                    "-fx-background-color: derive(" + color + ", 20%); " +
                            "-fx-text-fill: white; " +
                            "-fx-padding: 8 16 8 16; " +
                            "-fx-background-radius: 5; " +
                            "-fx-cursor: hand; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 8, 0, 0, 3);");
        });

        btn.setOnMouseExited(e -> {
            btn.setStyle(
                    "-fx-background-color: " + color + "; " +
                            "-fx-text-fill: white; " +
                            "-fx-padding: 8 16 8 16; " +
                            "-fx-background-radius: 5; " +
                            "-fx-cursor: hand; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 2);");
        });

        return btn;
    }

    private VBox createLeftPanel() {
        VBox leftPanel = new VBox(12);
        leftPanel.setPrefWidth(220);
        leftPanel.setMaxWidth(220);
        leftPanel.setAlignment(Pos.TOP_CENTER);
        leftPanel.setPadding(new Insets(15));
        leftPanel.setStyle(
                "-fx-background-color: rgba(40, 45, 70, 0.8); " +
                        "-fx-background-radius: 10; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 10, 0, 0, 3);");

        // Header
        HBox headerBox = new HBox(8);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label piecesLabel = new Label("Pieces");
        piecesLabel.setFont(Font.font(customFontBold.getFamily(), 18));
        piecesLabel.setStyle("-fx-text-fill: white;");

        Button addButton = new Button("+");
        addButton.setFont(customFontBold);
        addButton.setStyle(
                "-fx-background-color: #4ade80; " +
                        "-fx-text-fill: white; " +
                        "-fx-min-width: 28; " +
                        "-fx-min-height: 28; " +
                        "-fx-background-radius: 14; " +
                        "-fx-cursor: hand;");
        addButton.setOnAction(e -> addNewCustomPiece());

        headerBox.getChildren().addAll(piecesLabel, addButton);

        // Scrollable pieces list
        piecesListBox = new VBox(6);
        piecesListBox.setAlignment(Pos.TOP_CENTER);

        // Load all pieces from GameSettings
        refreshPiecesList();

        // Wrap in ScrollPane
        ScrollPane scrollPane = new ScrollPane(piecesListBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(280);
        scrollPane.setStyle(
                "-fx-background: transparent; " +
                        "-fx-background-color: transparent; " +
                        "-fx-border-color: transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // Delete button
        deleteButton = new Button("Delete Piece");
        deleteButton.setFont(customFont);
        deleteButton.setMaxWidth(Double.MAX_VALUE);
        deleteButton.setStyle(
                "-fx-background-color: #DD0584; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 10 16 10 16; " +
                        "-fx-background-radius: 5; " +
                        "-fx-cursor: hand;");
        deleteButton.setOnAction(e -> deleteSelectedPiece());
        updateDeleteButtonState();

        leftPanel.getChildren().addAll(headerBox, scrollPane, deleteButton);

        return leftPanel;
    }

    private HBox createPieceListItem(String name, String blockCount, boolean isDefault) {
        HBox item = new HBox(8);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(10, 12, 10, 12));
        item.setMaxWidth(Double.MAX_VALUE);

        Color indicatorColor = isDefault ? Color.web("#4ade80") : Color.web("#DD0584");

        if (name.equals(selectedPiece)) {
            item.setStyle(
                    "-fx-background-color: rgba(100, 80, 140, 0.6); " +
                            "-fx-background-radius: 5; " +
                            "-fx-cursor: hand;");
        } else {
            item.setStyle(
                    "-fx-background-color: rgba(50, 55, 80, 0.5); " +
                            "-fx-background-radius: 5; " +
                            "-fx-cursor: hand;");
        }

        // Indicator circle
        Circle indicator = new Circle(4);
        indicator.setFill(indicatorColor);

        // Text
        VBox textBox = new VBox(2);
        textBox.setAlignment(Pos.CENTER_LEFT);
        textBox.setMaxWidth(150);

        Label nameLabel = new Label(name);
        nameLabel.setFont(customFont);
        nameLabel.setStyle("-fx-text-fill: white;");
        nameLabel.setMaxWidth(150);
        nameLabel.setWrapText(false);
        nameLabel.setTextAlignment(TextAlignment.LEFT);

        Label countLabel = new Label(blockCount);
        countLabel.setFont(Font.font(customFont.getFamily(), 11));
        countLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6);");

        textBox.getChildren().addAll(nameLabel, countLabel);

        item.getChildren().addAll(indicator, textBox);

        // Hover effect
        item.setOnMouseEntered(e -> {
            if (!name.equals(selectedPiece)) {
                item.setStyle(
                        "-fx-background-color: rgba(70, 75, 100, 0.7); " +
                                "-fx-background-radius: 5; " +
                                "-fx-cursor: hand;");
            }
        });

        item.setOnMouseExited(e -> {
            if (!name.equals(selectedPiece)) {
                item.setStyle(
                        "-fx-background-color: rgba(50, 55, 80, 0.5); " +
                                "-fx-background-radius: 5; " +
                                "-fx-cursor: hand;");
            }
        });

        item.setOnMouseClicked(e -> selectPiece(name));

        return item;
    }

    private void refreshPiecesList() {
        piecesListBox.getChildren().clear();

        // Add standard pieces
        String[] standardPieces = { "I Piece", "O Piece", "T Piece", "S Piece", "Z Piece", "J Piece", "L Piece" };
        String[] standardBlockCounts = { "4 blocks", "4 blocks", "4 blocks", "4 blocks", "4 blocks", "4 blocks",
                "4 blocks" };

        for (int i = 0; i < standardPieces.length; i++) {
            piecesListBox.getChildren().add(createPieceListItem(standardPieces[i], standardBlockCounts[i], true));
        }

        // Add custom pieces dynamically from GameSettings
        List<String> customPieceNames = GameSettings.getInstance().getCustomPieceNames();
        for (String customName : customPieceNames) {
            boolean[][] design = GameSettings.getInstance().getCustomPiece(customName);
            int blockCount = countBlocks(design);
            piecesListBox.getChildren().add(createPieceListItem(customName, blockCount + " blocks", false));
        }
    }

    private int countBlocks(boolean[][] design) {
        if (design == null)
            return 0;
        int count = 0;
        for (boolean[] row : design) {
            if (row != null) {
                for (boolean cell : row) {
                    if (cell)
                        count++;
                }
            }
        }
        return count;
    }

    private void loadPiece(String name) {
        // Load piece settings first to get the correct grid size
        GameSettings.PieceSettings settings = GameSettings.getInstance().getPieceSettings(name);
        if (settings != null) {
            // Set color and outline FIRST (before creating grid)
            selectedColor = settings.color;
            outlineEnabled = settings.outlineEnabled;
            enableInGame = settings.enableInGame;

            // Update UI to reflect loaded settings
            if (colorPicker != null) {
                colorPicker.setValue(selectedColor);
            }
            updateOutlineToggle();

            // Update spawn rate slider
            if (spawnSlider != null) {
                spawnSlider.setValue(settings.spawnRate);
            }

            updateEnableToggle();

            // Set grid size and recreate grid if needed
            if (gridSize != settings.gridSize) {
                gridSize = settings.gridSize;
                pieceDesign = new boolean[gridSize][gridSize];
                createDesignGrid();
                updateSizeButtonStyles();
            }
        } else {
            // Fallback if no settings found
            gridSize = 4;
            pieceDesign = new boolean[gridSize][gridSize];
        }

        // Clear grid
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                pieceDesign[i][j] = false;
            }
        }

        // Load design from GameSettings (works for both standard and custom pieces)
        boolean[][] savedDesign = GameSettings.getInstance().getCustomPiece(name);
        if (savedDesign != null) {
            int rows = Math.min(gridSize, savedDesign.length);
            int cols = Math.min(gridSize, savedDesign[0].length);
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (i < savedDesign.length && j < savedDesign[i].length) {
                        pieceDesign[i][j] = savedDesign[i][j];
                    }
                }
            }
        }

        if (designGrid != null) {
            refreshDesignGrid();
        } else {
            updatePreview();
        }
    }

    private VBox createCenterPanel() {
        VBox centerPanel = new VBox(15);
        centerPanel.setPrefWidth(400);
        centerPanel.setMaxWidth(400);
        centerPanel.setAlignment(Pos.TOP_CENTER);

        // Design Grid Section
        VBox gridSection = new VBox(12);
        gridSection.setAlignment(Pos.TOP_CENTER);
        gridSection.setPadding(new Insets(15));
        gridSection.setStyle(
                "-fx-background-color: rgba(40, 45, 70, 0.8); " +
                        "-fx-background-radius: 10; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 10, 0, 0, 3);");

        // Header with size buttons
        HBox gridHeader = new HBox(12);
        gridHeader.setAlignment(Pos.CENTER);

        Label gridLabel = new Label("Design Grid");
        gridLabel.setFont(Font.font(customFontBold.getFamily(), 18));
        gridLabel.setStyle("-fx-text-fill: white;");

        Label sizeLabel = new Label("Size:");
        sizeLabel.setFont(customFont);
        sizeLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.8);");

        size3x3Button = createSizeButton("3x3");
        size4x4Button = createSizeButton("4x4");
        size4x4Button.setStyle(size4x4Button.getStyle() + "-fx-background-color: #DD0584;");

        size3x3Button.setOnAction(e -> setGridSize(3));
        size4x4Button.setOnAction(e -> setGridSize(4));
        updateSizeButtonStyles();

        gridHeader.getChildren().addAll(gridLabel, sizeLabel, size3x3Button, size4x4Button);

        // Design grid
        designGrid = new GridPane();
        designGrid.setAlignment(Pos.CENTER);
        designGrid.setHgap(2);
        designGrid.setVgap(2);
        designGrid.setPadding(new Insets(15));
        designGrid.setStyle(
                "-fx-background-color: rgba(30, 35, 55, 0.9); " +
                        "-fx-background-radius: 5;");

        createDesignGrid();

        // Instructions
        Label instructionLabel = new Label("Left Click: Add \u2022 Right Click: Remove");
        instructionLabel.setFont(Font.font(customFont.getFamily(), 12));
        instructionLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6);");

        // Buttons at bottom of grid panel
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        saveButton = createTopButton("Save", "#4ade80");
        saveButton.setOnAction(e -> saveSettings());

        resetButton = createTopButton("Reset", "#DD0584");
        resetButton.setOnAction(e -> resetGrid());

        backButton = createTopButton("Back", "#5a5f7f");
        backButton.setOnAction(e -> {
            saveSettings();
            if (onBackAction != null) {
                onBackAction.run();
            }
        });

        buttonBox.getChildren().addAll(saveButton, resetButton, backButton);

        gridSection.getChildren().addAll(gridHeader, designGrid, instructionLabel, buttonBox);

        centerPanel.getChildren().add(gridSection);

        return centerPanel;
    }

    private Button createSizeButton(String text) {
        Button btn = new Button(text);
        btn.setFont(customFont);
        btn.setMinWidth(50);
        btn.setStyle(
                "-fx-background-color: #4a4f6f; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 6 12 6 12; " +
                        "-fx-background-radius: 5; " +
                        "-fx-cursor: hand;");
        return btn;
    }

    private void createDesignGrid() {
        designGrid.getChildren().clear();

        int cellSize = 70;

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                StackPane cell = new StackPane();
                cell.setPrefSize(cellSize, cellSize);
                cell.setMaxSize(cellSize, cellSize);
                cell.setStyle(
                        "-fx-background-color: rgba(50, 55, 75, 0.8); " +
                                "-fx-border-color: rgba(100, 105, 125, 0.5); " +
                                "-fx-border-width: 1; " +
                                "-fx-cursor: hand;");

                final int r = row;
                final int c = col;

                // Always add click handlers, but check if editable at click time
                cell.setOnMouseClicked(e -> {
                    // Check if this piece is editable
                    if (!GameSettings.getInstance().isStandardPiece(selectedPiece)) {
                        if (e.getButton() == MouseButton.PRIMARY) {
                            pieceDesign[r][c] = true;
                            updateCell(cell, true);
                            updatePreview();
                        } else if (e.getButton() == MouseButton.SECONDARY) {
                            pieceDesign[r][c] = false;
                            updateCell(cell, false);
                            updatePreview();
                        }
                    }
                });

                // Hover effect (only for custom pieces)
                cell.setOnMouseEntered(e -> {
                    if (!GameSettings.getInstance().isStandardPiece(selectedPiece) && !pieceDesign[r][c]) {
                        cell.setStyle(
                                "-fx-background-color: rgba(70, 75, 95, 0.9); " +
                                        "-fx-border-color: rgba(120, 125, 145, 0.7); " +
                                        "-fx-border-width: 2; " +
                                        "-fx-cursor: hand;");
                    }
                });

                cell.setOnMouseExited(e -> {
                    updateCell(cell, pieceDesign[r][c]);
                });

                designGrid.add(cell, col, row);
            }
        }
    }

    private void updateCell(StackPane cell, boolean filled) {
        if (filled) {
            cell.setStyle(
                    "-fx-background-color: " + toHex(selectedColor) + "; " +
                            "-fx-border-color: " + (outlineEnabled ? "white" : "transparent") + "; " +
                            "-fx-border-width: " + (outlineEnabled ? "2" : "0") + "; " +
                            "-fx-cursor: hand;");
        } else {
            cell.setStyle(
                    "-fx-background-color: rgba(50, 55, 75, 0.8); " +
                            "-fx-border-color: rgba(100, 105, 125, 0.5); " +
                            "-fx-border-width: 1; " +
                            "-fx-cursor: hand;");
        }
    }

    private String toHex(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    private VBox createRightPanel() {
        VBox rightPanel = new VBox(15);
        rightPanel.setPrefWidth(260);
        rightPanel.setMaxWidth(260);
        rightPanel.setAlignment(Pos.TOP_CENTER);

        // Appearance section
        VBox appearanceBox = createAppearanceSection();

        // Gameplay section
        VBox gameplayBox = createGameplaySection();

        // Preview section
        VBox previewBox = createPreviewSection();

        rightPanel.getChildren().addAll(appearanceBox, gameplayBox, previewBox);

        return rightPanel;
    }

    private VBox createAppearanceSection() {
        VBox section = new VBox(12);
        section.setPadding(new Insets(15));
        section.setStyle(
                "-fx-background-color: rgba(40, 45, 70, 0.8); " +
                        "-fx-background-radius: 10; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 10, 0, 0, 3);");

        // Header
        Label titleLabel = new Label("Appearance");
        titleLabel.setFont(Font.font(customFontBold.getFamily(), 16));
        titleLabel.setStyle("-fx-text-fill: white;");

        // Color picker
        HBox colorBox = new HBox(8);
        colorBox.setAlignment(Pos.CENTER_LEFT);

        Label colorLabel = new Label("Color");
        colorLabel.setFont(customFont);
        colorLabel.setStyle("-fx-text-fill: white;");

        colorPicker = new ColorPicker(selectedColor);
        colorPicker.setPrefWidth(90);
        colorPicker.setStyle("-fx-cursor: hand;");
        colorPicker.setOnAction(e -> {
            selectedColor = colorPicker.getValue();
            refreshDesignGrid();
            updatePreview();
        });

        colorBox.getChildren().addAll(colorLabel, colorPicker);

        // Outline button (instead of toggle)
        HBox outlineBox = new HBox(8);
        outlineBox.setAlignment(Pos.CENTER_LEFT);

        Label outlineLabel = new Label("Outline");
        outlineLabel.setFont(customFont);
        outlineLabel.setStyle("-fx-text-fill: white;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        outlineToggleButton = createToggleButton(outlineEnabled);
        outlineToggleButton.setOnAction(e -> {
            outlineEnabled = !outlineEnabled;
            updateOutlineToggle();
            refreshDesignGrid();
        });

        outlineBox.getChildren().addAll(outlineLabel, spacer, outlineToggleButton);

        section.getChildren().addAll(titleLabel, colorBox, outlineBox);

        return section;
    }

    private VBox createGameplaySection() {
        VBox section = new VBox(12);
        section.setPadding(new Insets(15));
        section.setStyle(
                "-fx-background-color: rgba(40, 45, 70, 0.8); " +
                        "-fx-background-radius: 10; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 10, 0, 0, 3);");

        // Header
        Label titleLabel = new Label("Gameplay");
        titleLabel.setFont(Font.font(customFontBold.getFamily(), 16));
        titleLabel.setStyle("-fx-text-fill: white;");

        // Enable in game button (instead of toggle)
        HBox enableBox = new HBox(8);
        enableBox.setAlignment(Pos.CENTER_LEFT);

        Label enableLabel = new Label("Enable in Game");
        enableLabel.setFont(customFont);
        enableLabel.setStyle("-fx-text-fill: white;");
        enableLabel.setMaxWidth(140);
        enableLabel.setWrapText(true);

        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);

        enableToggleButton = createToggleButton(enableInGame);
        enableToggleButton.setOnAction(e -> {
            enableInGame = !enableInGame;
            updateEnableToggle();
            GameSettings.getInstance().setPieceEnableInGame(selectedPiece, enableInGame);
        });

        enableBox.getChildren().addAll(enableLabel, spacer1, enableToggleButton);

        // Spawn rate slider
        VBox spawnBox = new VBox(6);

        HBox spawnHeader = new HBox(8);
        spawnHeader.setAlignment(Pos.CENTER_LEFT);

        Label spawnLabel = new Label("Spawn Rate");
        spawnLabel.setFont(customFont);
        spawnLabel.setStyle("-fx-text-fill: white;");

        Label spawnValue = new Label("5/10");
        spawnValue.setFont(customFont);
        spawnValue.setStyle("-fx-text-fill: #FFD75C;");

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        spawnHeader.getChildren().addAll(spawnLabel, spacer2, spawnValue);

        spawnSlider = new Slider(0, 10, 5);
        spawnSlider.setShowTickMarks(false);
        spawnSlider.setShowTickLabels(false);
        spawnSlider.setStyle("-fx-control-inner-background: rgba(60, 65, 90, 0.8);");

        spawnSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int val = newVal.intValue();
            spawnValue.setText(val + "/10");
            // Update individual setting immediately
            GameSettings.getInstance().setPieceSpawnRate(selectedPiece, val);
        });

        Label helpText = new Label("How often piece appears");
        helpText.setFont(Font.font(customFont.getFamily(), 11));
        helpText.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.5);");
        helpText.setWrapText(true);

        spawnBox.getChildren().addAll(spawnHeader, spawnSlider, helpText);

        section.getChildren().addAll(titleLabel, enableBox, spawnBox);

        return section;
    }

    private VBox createPreviewSection() {
        VBox section = new VBox(12);
        section.setPadding(new Insets(15));
        section.setAlignment(Pos.TOP_CENTER);
        section.setStyle(
                "-fx-background-color: rgba(40, 45, 70, 0.8); " +
                        "-fx-background-radius: 10; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 10, 0, 0, 3);");

        // Header
        Label titleLabel = new Label("Preview");
        titleLabel.setFont(Font.font(customFontBold.getFamily(), 16));
        titleLabel.setStyle("-fx-text-fill: white;");

        // Preview area
        StackPane previewArea = new StackPane();
        previewArea.setPrefSize(180, 120);
        previewArea.setStyle(
                "-fx-background-color: rgba(30, 35, 55, 0.9); " +
                        "-fx-background-radius: 5;");

        previewGrid = new GridPane();
        previewGrid.setAlignment(Pos.CENTER);
        previewGrid.setHgap(4);
        previewGrid.setVgap(4);

        previewPlaceholder = new Label("Piece preview");
        previewPlaceholder.setFont(Font.font(customFont.getFamily(), 12));
        previewPlaceholder.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.4);");

        previewArea.getChildren().addAll(previewGrid, previewPlaceholder);
        updatePreview();

        section.getChildren().addAll(titleLabel, previewArea);

        return section;
    }

    private void refreshDesignGrid() {
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                int index = row * gridSize + col;
                if (index < designGrid.getChildren().size()) {
                    StackPane cell = (StackPane) designGrid.getChildren().get(index);
                    updateCell(cell, pieceDesign[row][col]);
                }
            }
        }
        updatePreview();
    }

    /**
     * Retrieves the "Save" button instance.
     *
     * @return the save button
     */
    public Button getSaveButton() {
        return saveButton;
    }

    /**
     * Retrieves the "Reset" button instance.
     *
     * @return the reset button
     */
    public Button getResetButton() {
        return resetButton;
    }

    /**
     * Retrieves the "Back" button instance.
     *
     * @return the back button
     */
    public Button getBackButton() {
        return backButton;
    }

    /**
     * Retrieves the "Delete" button instance.
     *
     * @return the delete button
     */
    public Button getDeleteButton() {
        return deleteButton;
    }

    /**
     * Saves the current design and settings for the selected piece.
     * Persists updates to the global GameSettings for immediate use in gameplay.
     * Also refreshes the UI to reflect changes.
     */
    public void saveSettings() {
        GameSettings settings = GameSettings.getInstance();
        boolean isStandard = GameSettings.getInstance().isStandardPiece(selectedPiece);

        // Save current piece design if it's a custom piece (not standard)
        if (selectedPiece.startsWith("Custom")) {
            settings.saveCustomPiece(selectedPiece, pieceDesign);
            settings.setPieceGridSize(selectedPiece, gridSize);
        }

        // Save color and outline for ALL pieces (standard and custom)
        settings.setPieceColor(selectedPiece, selectedColor);
        settings.setPieceOutlineEnabled(selectedPiece, outlineEnabled);

        // Save spawn rate for all pieces
        if (spawnSlider != null) {
            settings.setPieceSpawnRate(selectedPiece, (int) spawnSlider.getValue());
        }

        // Update list to reflect new block count
        refreshPiecesList();

        // Save enable status for the specific piece
        settings.setPieceEnableInGame(selectedPiece, enableInGame);
        // Spawn rate is handled in the slider listener

        // Ensure preview is up to date
        updatePreview();

        System.out.println("Settings saved for: " + selectedPiece);
    }

    /**
     * Clears the current design grid, setting all cells to empty.
     */
    public void resetGrid() {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                pieceDesign[i][j] = false;
            }
        }
        refreshDesignGrid();
    }

    /**
     * Sets a callback to be executed when the back button is clicked.
     *
     * @param action the Runnable to execute
     */
    public void setOnBackAction(Runnable action) {
        this.onBackAction = action;
    }

    /**
     * Allows programmatic selection of a piece, mirroring the list click behaviour.
     * Useful for tests and for potential future integrations.
     */
    public void selectPiece(String name) {
        if (name == null) {
            return;
        }
        selectedPiece = name;
        loadPiece(name);
        refreshPiecesList();
        updateDeleteButtonState();
        updateGridSizeButtonsState();
    }

    private void updateDeleteButtonState() {
        // Disable delete button for standard pieces
        boolean isStandard = GameSettings.getInstance().isStandardPiece(selectedPiece);
        if (deleteButton != null) {
            deleteButton.setDisable(isStandard);
            if (isStandard) {
                deleteButton.setStyle(
                        "-fx-background-color: #666; " +
                                "-fx-text-fill: #999; " +
                                "-fx-padding: 10 16 10 16; " +
                                "-fx-background-radius: 5; " +
                                "-fx-cursor: not-allowed;");
            } else {
                deleteButton.setStyle(
                        "-fx-background-color: #DD0584; " +
                                "-fx-text-fill: white; " +
                                "-fx-padding: 10 16 10 16; " +
                                "-fx-background-radius: 5; " +
                                "-fx-cursor: hand;");
            }
        }
    }

    private void updateGridSizeButtonsState() {
        // Disable grid size buttons for standard pieces
        boolean isStandard = GameSettings.getInstance().isStandardPiece(selectedPiece);
        if (size3x3Button != null) {
            size3x3Button.setDisable(isStandard);
        }
        if (size4x4Button != null) {
            size4x4Button.setDisable(isStandard);
        }

        // Enable color picker and outline toggle for ALL pieces
        if (colorPicker != null) {
            colorPicker.setDisable(false);
        }
        if (outlineToggleButton != null) {
            outlineToggleButton.setDisable(false);
        }
    }

    /**
     * Programmatically set a cell's filled state. Keeps the grid in sync when used
     * outside direct mouse interaction (e.g. tests or future presets).
     */
    public void setCellFilled(int row, int col, boolean filled) {
        if (row < 0 || row >= gridSize || col < 0 || col >= gridSize) {
            throw new IllegalArgumentException("Cell coordinates out of bounds");
        }
        pieceDesign[row][col] = filled;

        if (designGrid != null) {
            int index = row * gridSize + col;
            if (index < designGrid.getChildren().size()) {
                StackPane cell = (StackPane) designGrid.getChildren().get(index);
                updateCell(cell, filled);
            }
        }
        updatePreview();
    }

    /**
     * Checks if a specific cell in the current design grid is filled.
     *
     * @param row the row index (0-based)
     * @param col the column index (0-based)
     * @return true if the cell is filled (part of the piece), false otherwise
     */
    public boolean isCellFilled(int row, int col) {
        if (row < 0 || row >= gridSize || col < 0 || col >= gridSize) {
            throw new IllegalArgumentException("Cell coordinates out of bounds");
        }
        return pieceDesign[row][col];
    }

    private void setGridSize(int newSize) {
        if (gridSize == newSize) {
            return;
        }

        // Save current design to GameSettings before changing size
        if (selectedPiece.startsWith("Custom")) {
            GameSettings.getInstance().saveCustomPiece(selectedPiece, pieceDesign);
            GameSettings.getInstance().setPieceGridSize(selectedPiece, newSize);
        }

        // Update grid size
        gridSize = newSize;

        // Load the design from GameSettings (which stores the full design)
        boolean[][] savedDesign = GameSettings.getInstance().getCustomPiece(selectedPiece);
        if (savedDesign != null) {
            // Create new design array with new size
            pieceDesign = new boolean[newSize][newSize];
            // Copy from saved design (which has the original size)
            int rows = Math.min(newSize, savedDesign.length);
            int cols = Math.min(newSize, savedDesign[0].length);
            for (int i = 0; i < rows; i++) {
                System.arraycopy(savedDesign[i], 0, pieceDesign[i], 0, cols);
            }
        } else {
            pieceDesign = new boolean[newSize][newSize];
        }

        createDesignGrid();
        refreshDesignGrid();
        updateSizeButtonStyles();
    }

    private void updateSizeButtonStyles() {
        if (size3x3Button != null) {
            size3x3Button.setStyle(
                    "-fx-background-color: " + (gridSize == 3 ? "#DD0584" : "#4a4f6f") + "; " +
                            "-fx-text-fill: white; " +
                            "-fx-padding: 6 12 6 12; " +
                            "-fx-background-radius: 5; " +
                            "-fx-cursor: hand;");
        }
        if (size4x4Button != null) {
            size4x4Button.setStyle(
                    "-fx-background-color: " + (gridSize == 4 ? "#DD0584" : "#4a4f6f") + "; " +
                            "-fx-text-fill: white; " +
                            "-fx-padding: 6 12 6 12; " +
                            "-fx-background-radius: 5; " +
                            "-fx-cursor: hand;");
        }
    }

    private Button createToggleButton(boolean active) {
        Button button = new Button(active ? "ON" : "OFF");
        button.setFont(customFont);
        button.setPrefSize(60, 25);
        button.setStyle(
                "-fx-background-color: " + (active ? "#4ade80" : "#5a5f7f") + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 5; " +
                        "-fx-cursor: hand;");
        return button;
    }

    private void updateOutlineToggle() {
        if (outlineToggleButton != null) {
            outlineToggleButton.setText(outlineEnabled ? "ON" : "OFF");
            outlineToggleButton.setStyle(
                    "-fx-background-color: " + (outlineEnabled ? "#4ade80" : "#5a5f7f") + "; " +
                            "-fx-text-fill: white; " +
                            "-fx-background-radius: 5; " +
                            "-fx-cursor: hand;");
        }
    }

    private void updateEnableToggle() {
        if (enableToggleButton != null) {
            enableToggleButton.setText(enableInGame ? "ON" : "OFF");
            enableToggleButton.setStyle(
                    "-fx-background-color: " + (enableInGame ? "#4ade80" : "#5a5f7f") + "; " +
                            "-fx-text-fill: white; " +
                            "-fx-background-radius: 5; " +
                            "-fx-cursor: hand;");
        }
    }

    private void applyAppearanceSettingsToUI() {
        if (colorPicker != null) {
            colorPicker.setValue(selectedColor);
        }
        updateOutlineToggle();
        updateEnableToggle();
        refreshDesignGrid();
    }

    private void deleteSelectedPiece() {
        if (selectedPiece == null || !selectedPiece.startsWith("Custom")) {
            return;
        }

        // Remove the piece from GameSettings
        GameSettings.getInstance().removeCustomPiece(selectedPiece);

        // Remove from list and select the first available piece
        refreshPiecesList();

        // Select first custom piece if available, otherwise first standard piece
        List<String> customNames = GameSettings.getInstance().getCustomPieceNames();
        if (!customNames.isEmpty()) {
            selectPiece(customNames.get(0));
        } else {
            selectPiece("I Piece");
        }
    }

    private void addNewCustomPiece() {
        String newName = GameSettings.getInstance().addNewCustomPiece();
        refreshPiecesList();
        selectPiece(newName);
    }

    private void updatePreview() {
        if (previewGrid == null) {
            return;
        }

        previewGrid.getChildren().clear();
        int size = gridSize;
        double available = 110;
        double spacing = 4;
        double cellSize = Math.max(10, Math.min(24, (available - ((size - 1) * spacing)) / size));
        boolean hasBlocks = false;

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (row < pieceDesign.length && col < pieceDesign[row].length && pieceDesign[row][col]) {
                    hasBlocks = true;
                    Rectangle rect = new Rectangle(cellSize, cellSize);
                    rect.setArcWidth(6);
                    rect.setArcHeight(6);
                    rect.setFill(selectedColor);
                    if (outlineEnabled) {
                        rect.setStroke(Color.WHITE);
                        rect.setStrokeWidth(1.5);
                    } else {
                        rect.setStroke(Color.TRANSPARENT);
                    }
                    previewGrid.add(rect, col, row);
                }
            }
        }

        previewPlaceholder.setVisible(!hasBlocks);
        previewGrid.setVisible(hasBlocks);
    }
}
