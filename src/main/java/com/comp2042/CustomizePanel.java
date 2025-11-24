package com.comp2042;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class CustomizePanel extends StackPane {

    private VBox piecesListBox;
    private GridPane designGrid;

    private Font customFont;
    private Font customFontBold;

    private Button saveButton;
    private Button resetButton;
    private Button backButton;

    private String selectedPiece = "I Piece";
    private Color selectedColor = Color.web("#4ade80");
    private boolean outlineEnabled = true;
    private boolean enableInGame = true;

    private int gridSize = 4;
    private boolean[][] pieceDesign = new boolean[gridSize][gridSize];

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

        headerBox.getChildren().addAll(piecesLabel, addButton);

        // Scrollable pieces list
        piecesListBox = new VBox(6);
        piecesListBox.setAlignment(Pos.TOP_CENTER);

        String[] pieces = { "I Piece", "O Piece", "T Piece", "S Piece", "Z Piece", "J Piece", "L Piece", "Custom 1",
                "Custom 2", "Custom 3" };
        String[] blockCounts = { "4 blocks", "4 blocks", "4 blocks", "4 blocks", "4 blocks", "4 blocks", "4 blocks",
                "5 blocks", "8 blocks", "6 blocks" };

        for (int i = 0; i < pieces.length; i++) {
            piecesListBox.getChildren().add(createPieceListItem(pieces[i], blockCounts[i], i < 7));
        }

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
        Button deleteButton = new Button("Delete Piece");
        deleteButton.setFont(customFont);
        deleteButton.setMaxWidth(Double.MAX_VALUE);
        deleteButton.setStyle(
                "-fx-background-color: #DD0584; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 10 16 10 16; " +
                        "-fx-background-radius: 5; " +
                        "-fx-cursor: hand;");

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

        item.setOnMouseClicked(e -> {
            selectedPiece = name;
            refreshPiecesList();
        });

        return item;
    }

    private void refreshPiecesList() {
        piecesListBox.getChildren().clear();
        String[] pieces = { "I Piece", "O Piece", "T Piece", "S Piece", "Z Piece", "J Piece", "L Piece", "Custom 1",
                "Custom 2", "Custom 3" };
        String[] blockCounts = { "4 blocks", "4 blocks", "4 blocks", "4 blocks", "4 blocks", "4 blocks", "4 blocks",
                "5 blocks", "8 blocks", "6 blocks" };

        for (int i = 0; i < pieces.length; i++) {
            piecesListBox.getChildren().add(createPieceListItem(pieces[i], blockCounts[i], i < 7));
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

        Button size3x3 = createSizeButton("3x3");
        Button size4x4 = createSizeButton("4x4");
        size4x4.setStyle(size4x4.getStyle() + "-fx-background-color: #DD0584;");

        gridHeader.getChildren().addAll(gridLabel, sizeLabel, size3x3, size4x4);

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
        resetButton = createTopButton("Reset", "#DD0584");
        backButton = createTopButton("Back", "#5a5f7f");

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

                // Left click to add
                cell.setOnMouseClicked(e -> {
                    if (e.isPrimaryButtonDown()) {
                        pieceDesign[r][c] = true;
                        updateCell(cell, true);
                    } else if (e.isSecondaryButtonDown()) {
                        pieceDesign[r][c] = false;
                        updateCell(cell, false);
                    }
                });

                // Hover effect
                cell.setOnMouseEntered(e -> {
                    if (!pieceDesign[r][c]) {
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

        ColorPicker colorPicker = new ColorPicker(selectedColor);
        colorPicker.setPrefWidth(90);
        colorPicker.setStyle("-fx-cursor: hand;");
        colorPicker.setOnAction(e -> {
            selectedColor = colorPicker.getValue();
            refreshDesignGrid();
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

        Button outlineButton = new Button(outlineEnabled ? "ON" : "OFF");
        outlineButton.setFont(customFont);
        outlineButton.setPrefSize(60, 25);
        outlineButton.setStyle(
                "-fx-background-color: " + (outlineEnabled ? "#4ade80" : "#5a5f7f") + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 5; " +
                        "-fx-cursor: hand;");

        outlineButton.setOnAction(e -> {
            outlineEnabled = !outlineEnabled;
            outlineButton.setText(outlineEnabled ? "ON" : "OFF");
            outlineButton.setStyle(
                    "-fx-background-color: " + (outlineEnabled ? "#4ade80" : "#5a5f7f") + "; " +
                            "-fx-text-fill: white; " +
                            "-fx-background-radius: 5; " +
                            "-fx-cursor: hand;");
        });

        outlineBox.getChildren().addAll(outlineLabel, spacer, outlineButton);

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

        Button enableButton = new Button(enableInGame ? "ON" : "OFF");
        enableButton.setFont(customFont);
        enableButton.setPrefSize(60, 25);
        enableButton.setStyle(
                "-fx-background-color: " + (enableInGame ? "#4ade80" : "#5a5f7f") + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 5; " +
                        "-fx-cursor: hand;");

        enableButton.setOnAction(e -> {
            enableInGame = !enableInGame;
            enableButton.setText(enableInGame ? "ON" : "OFF");
            enableButton.setStyle(
                    "-fx-background-color: " + (enableInGame ? "#4ade80" : "#5a5f7f") + "; " +
                            "-fx-text-fill: white; " +
                            "-fx-background-radius: 5; " +
                            "-fx-cursor: hand;");
        });

        enableBox.getChildren().addAll(enableLabel, spacer1, enableButton);

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

        Slider spawnSlider = new Slider(0, 10, 5);
        spawnSlider.setShowTickMarks(false);
        spawnSlider.setShowTickLabels(false);
        spawnSlider.setStyle("-fx-control-inner-background: rgba(60, 65, 90, 0.8);");

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

        Label previewText = new Label("Piece preview");
        previewText.setFont(Font.font(customFont.getFamily(), 12));
        previewText.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.4);");

        previewArea.getChildren().add(previewText);

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
    }

    public Button getSaveButton() {
        return saveButton;
    }

    public Button getResetButton() {
        return resetButton;
    }

    public Button getBackButton() {
        return backButton;
    }
}
