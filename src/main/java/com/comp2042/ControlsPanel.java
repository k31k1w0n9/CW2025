package com.comp2042;

import java.io.InputStream;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class ControlsPanel extends VBox {

    private Button doneButton;
    private KeyBindings keyBindings;
    private String currentlyRebinding = null;
    private Button currentRebindButton = null;
    private Font customFont;
    private Font customFontBold;

    public ControlsPanel(KeyBindings keyBindings) {
        this.keyBindings = keyBindings;

        // Load custom fonts
        try {
            InputStream fontStream1 = getClass().getResourceAsStream("/BoutiqueBitmap9x9_1.9.ttf");
            InputStream fontStream2 = getClass().getResourceAsStream("/BoutiqueBitmap9x9_Bold_1.9.ttf");
            if (fontStream1 != null) {
                customFont = Font.loadFont(fontStream1, 16);
            }
            if (fontStream2 != null) {
                customFontBold = Font.loadFont(fontStream2, 48);
            }
        } catch (Exception e) {
            System.err.println("Could not load custom fonts for ControlsPanel");
        }
        if (customFont == null) customFont = Font.font("Arial", 16);
        if (customFontBold == null) customFontBold = Font.font("Arial", 48);

        setAlignment(Pos.CENTER);
        setSpacing(15);
        setStyle("-fx-background-color: #3d4f6d; " +
                "-fx-border-color: white; " +
                "-fx-border-width: 3; " +
                "-fx-border-radius: 10; " +
                "-fx-background-radius: 10; " +
                "-fx-padding: 30;");

        setPrefSize(600, 660);
        setMaxSize(600, 660);
        setMinSize(600, 660);

        // Title with colorful letters
        HBox titleBox = createColorfulTitle();

        // Control mappings
        VBox controlsBox = new VBox(5);
        controlsBox.setAlignment(Pos.CENTER);

        controlsBox.getChildren().addAll(
                createControlRow("Shift Left", "MOVE_LEFT"),
                createControlRow("Shift Right", "MOVE_RIGHT"),
                createControlRow("Soft Drop", "SOFT_DROP"),
                createControlRow("Hard Drop", "HARD_DROP"),
                createControlRow("Rotate", "ROTATE"),
                createControlRow("Rotate Left", "ROTATE_LEFT"),
                createControlRow("Rotate Right", "ROTATE_RIGHT"),
                createControlRow("Hold", "HOLD"),
                createControlRow("Pause", "PAUSE")
        );

        // Spacer to push button down
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Back button container
        VBox buttonContainer = new VBox();
        buttonContainer.setAlignment(Pos.CENTER);

        doneButton = new Button("Back");
        doneButton.setPrefSize(200, 45);
        doneButton.setMinSize(200, 45);
        doneButton.setMaxSize(200, 45);
        // FIXED: Use custom pixel font
        doneButton.setFont(customFont);
        doneButton.setStyle("-fx-background-color: rgba(90, 95, 127, 0.8); " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 18px; " +
                "-fx-border-color: white; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand;");

        doneButton.setOnMouseEntered(e -> doneButton.setStyle(
                "-fx-background-color: rgba(120, 125, 167, 1.0); " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 18px; " +
                        "-fx-border-color: white; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 8; " +
                        "-fx-background-radius: 8; " +
                        "-fx-cursor: hand;"));
        doneButton.setOnMouseExited(e -> doneButton.setStyle(
                "-fx-background-color: rgba(90, 95, 127, 0.8); " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 18px; " +
                        "-fx-border-color: white; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 8; " +
                        "-fx-background-radius: 8; " +
                        "-fx-cursor: hand;"));

        buttonContainer.getChildren().add(doneButton);
        getChildren().addAll(titleBox, controlsBox, spacer, buttonContainer);

        // Set up key listener for rebinding
        setFocusTraversable(true);
        setOnKeyPressed(event -> {
            if (currentlyRebinding != null && currentRebindButton != null) {
                KeyCode newKey = event.getCode();

                // Don't allow Escape to be bound (reserved for cancel)
                if (newKey == KeyCode.ESCAPE) {
                    cancelRebind();
                    return;
                }

                // Update the binding
                keyBindings.rebindKey(currentlyRebinding, newKey);

                // Update button text
                currentRebindButton.setText(getKeyDisplayName(newKey));
                currentRebindButton.setStyle(getKeyButtonStyle());

                // Clear rebinding state
                currentlyRebinding = null;
                currentRebindButton = null;

                event.consume();
            }
        });
    }

    private HBox createColorfulTitle() {
        HBox titleBox = new HBox(2);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(0, 0, 15, 0));

        String[] letters = {"C", "O", "N", "T", "R", "O", "L", "S"};
        String[] colors = {"#FF5C7C", "#FFA05C", "#FFD75C", "#8FD75C", "#5CD7D7",
                "#5C8FFF", "#A05CFF", "#FF5CD7"};

        for (int i = 0; i < letters.length; i++) {
            Label letter = new Label(letters[i]);
            // FIXED: Use custom pixel font
            letter.setFont(customFontBold);
            letter.setStyle("-fx-font-size: 48px; " +
                    "-fx-text-fill: " + colors[i] + "; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 5, 0.5, 2, 2);");
            titleBox.getChildren().add(letter);
        }

        return titleBox;
    }

    private HBox createControlRow(String action, String bindingKey) {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER);
        row.setPrefWidth(550);
        row.setMaxWidth(550);

        Label actionLabel = new Label(action);
        // FIXED: Use custom pixel font
        actionLabel.setFont(customFont);
        actionLabel.setStyle("-fx-font-size: 16px; " +
                "-fx-text-fill: white; " +
                "-fx-min-width: 180; " +
                "-fx-alignment: center-left;");

        // Separator line
        Label separator = new Label("────────");
        separator.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.3); " +
                "-fx-font-size: 12px;");

        // Key button (clickable to rebind)
        Button keyButton = new Button(getCurrentKeysDisplay(bindingKey));
        keyButton.setPrefWidth(200);
        keyButton.setMinWidth(200);
        // FIXED: Use custom pixel font
        keyButton.setFont(customFont);
        keyButton.setStyle(getKeyButtonStyle());

        keyButton.setOnMouseEntered(e -> {
            if (currentlyRebinding == null) {
                keyButton.setStyle(getKeyButtonHoverStyle());
            }
        });

        keyButton.setOnMouseExited(e -> {
            if (currentlyRebinding == null || currentRebindButton != keyButton) {
                keyButton.setStyle(getKeyButtonStyle());
            }
        });

        keyButton.setOnAction(e -> {
            startRebinding(bindingKey, keyButton);
        });

        row.getChildren().addAll(actionLabel, separator, keyButton);
        return row;
    }

    private String getCurrentKeysDisplay(String bindingKey) {
        java.util.List<KeyCode> keys = keyBindings.getKeysForAction(bindingKey);
        if (keys.isEmpty()) {
            return "Not bound";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            if (i > 0) sb.append(" / ");
            sb.append(getKeyDisplayName(keys.get(i)));
        }
        return sb.toString();
    }

    private String getKeyDisplayName(KeyCode key) {
        return switch (key) {
            case UP -> "↑";
            case DOWN -> "↓";
            case LEFT -> "←";
            case RIGHT -> "→";
            case SPACE -> "Spacebar";
            default -> key.getName();
        };
    }

    private void startRebinding(String bindingKey, Button button) {
        // Cancel any previous rebinding
        if (currentRebindButton != null && currentRebindButton != button) {
            cancelRebind();
        }

        currentlyRebinding = bindingKey;
        currentRebindButton = button;

        button.setText("Press any key...");
        button.setStyle(getRebindingStyle());

        // Request focus to capture key presses
        requestFocus();
    }

    private void cancelRebind() {
        if (currentRebindButton != null) {
            currentRebindButton.setText(getCurrentKeysDisplay(currentlyRebinding));
            currentRebindButton.setStyle(getKeyButtonStyle());
        }
        currentlyRebinding = null;
        currentRebindButton = null;
    }

    private String getKeyButtonStyle() {
        return "-fx-font-size: 16px; " +
                "-fx-text-fill: white; " +
                "-fx-background-color: rgba(0, 0, 0, 0.3); " +
                "-fx-padding: 5 15; " +
                "-fx-border-color: rgba(255, 255, 255, 0.5); " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 5; " +
                "-fx-background-radius: 5; " +
                "-fx-cursor: hand; " +
                "-fx-alignment: center;";
    }

    private String getKeyButtonHoverStyle() {
        return "-fx-font-size: 16px; " +
                "-fx-text-fill: #FFD75C; " +
                "-fx-font-weight: bold; " +
                "-fx-background-color: rgba(0, 0, 0, 0.5); " +
                "-fx-padding: 5 15; " +
                "-fx-border-color: rgba(255, 215, 0, 0.8); " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 5; " +
                "-fx-background-radius: 5; " +
                "-fx-cursor: hand; " +
                "-fx-alignment: center;";
    }

    private String getRebindingStyle() {
        return "-fx-font-size: 16px; " +
                "-fx-text-fill: #FF5C7C; " +
                "-fx-font-weight: bold; " +
                "-fx-background-color: rgba(255, 92, 124, 0.2); " +
                "-fx-padding: 5 15; " +
                "-fx-border-color: #FF5C7C; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 5; " +
                "-fx-background-radius: 5; " +
                "-fx-cursor: wait; " +
                "-fx-alignment: center;";
    }

    public Button getDoneButton() {
        return doneButton;
    }
}