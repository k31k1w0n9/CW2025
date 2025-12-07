package com.comp2042.ui;

import java.io.InputStream;
import java.util.List;

import com.comp2042.system.KeyBindings;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * UI Panel for displaying and customizing game controls.
 * Allows users to view current key bindings and rebind keys to their
 * preference.
 * Integration with {@link KeyBindings} ensures changes are persisted.
 */
public class ControlsPanel extends VBox {

    private Button doneButton;
    private Button resetButton;
    private KeyBindings keyBindings;
    private String currentlyRebinding = null;
    private Button currentRebindButton = null;
    private Font customFont;
    private Font customFontBold;
    private Label titleLabel;
    private VBox controlsBox;

    /**
     * Constructs a new ControlsPanel.
     * Initializes the UI layout, loads custom fonts, and populates the control
     * list.
     *
     * @param keyBindings the data model for key bindings
     */
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
        if (customFont == null)
            customFont = Font.font("Arial", 16);
        if (customFontBold == null)
            customFontBold = Font.font("Arial", 48);

        setAlignment(Pos.CENTER);
        setSpacing(15);
        setStyle("-fx-background-color: #2C3E50; " +
                "-fx-border-color: white; " +
                "-fx-border-width: 3; " +
                "-fx-border-radius: 10; " +
                "-fx-background-radius: 10; " +
                "-fx-padding: 30;");

        setPrefSize(600, 660);
        setMaxSize(600, 660);
        setMinSize(600, 660);

        // Title
        this.titleLabel = new Label("CONTROLS");
        titleLabel.setFont(customFontBold);
        titleLabel.setStyle("-fx-font-size: 48px; " +
                "-fx-text-fill: white; " +
                "-fx-effect: dropshadow(gaussian, #DD0584, 15, 0.8, 0, 0);");
        titleLabel.setManaged(true);
        titleLabel.setVisible(true);

        // Control mappings
        controlsBox = new VBox(5);
        controlsBox.setAlignment(Pos.CENTER);
        refreshControls();

        // Back button container
        VBox buttonContainer = new VBox(10);
        buttonContainer.setAlignment(Pos.CENTER);

        resetButton = new Button("Reset Defaults");
        resetButton.setPrefSize(200, 45);
        resetButton.setMinSize(200, 45);
        resetButton.setMaxSize(200, 45);
        resetButton.setFont(customFont);
        resetButton.setStyle("-fx-background-color: transparent; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 18px; " +
                "-fx-border-color: white; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand;");

        resetButton.setOnMouseEntered(e -> resetButton.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.1); " +
                        "-fx-text-fill: #FFD75C; " +
                        "-fx-font-size: 18px; " +
                        "-fx-border-color: #FFD75C; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 8; " +
                        "-fx-background-radius: 8; " +
                        "-fx-cursor: hand;"));
        resetButton.setOnMouseExited(e -> resetButton.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 18px; " +
                        "-fx-border-color: white; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 8; " +
                        "-fx-background-radius: 8; " +
                        "-fx-cursor: hand;"));

        resetButton.setOnAction(e -> {
            resetToDefaults();
        });

        doneButton = new Button("Back");
        doneButton.setPrefSize(200, 45);
        doneButton.setMinSize(200, 45);
        doneButton.setMaxSize(200, 45);
        doneButton.setFont(customFont);
        doneButton.setStyle("-fx-background-color: transparent; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 18px; " +
                "-fx-border-color: white; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand;");

        doneButton.setOnMouseEntered(e -> doneButton.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.1); " +
                        "-fx-text-fill: #FFD75C; " +
                        "-fx-font-size: 18px; " +
                        "-fx-border-color: #FFD75C; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 8; " +
                        "-fx-background-radius: 8; " +
                        "-fx-cursor: hand;"));
        doneButton.setOnMouseExited(e -> doneButton.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 18px; " +
                        "-fx-border-color: white; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 8; " +
                        "-fx-background-radius: 8; " +
                        "-fx-cursor: hand;"));

        buttonContainer.getChildren().addAll(resetButton, doneButton);
        getChildren().addAll(titleLabel, controlsBox, buttonContainer);

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

    /**
     * Resets all key bindings to their default values and refreshes the UI.
     */
    public void resetToDefaults() {
        keyBindings.resetToDefaults();
        refreshControls();
    }

    /**
     * Hides the "Reset Defaults" button from the view.
     * Used when the panel is embedded in contexts where reset is not applicable.
     */
    public void hideResetButton() {
        if (resetButton != null) {
            resetButton.setVisible(false);
            resetButton.setManaged(false);
        }
    }

    /**
     * Re-populates the list of controls based on current key bindings.
     * Cleears existing rows and creates new ones for each action.
     */
    private void refreshControls() {
        controlsBox.getChildren().clear();
        controlsBox.getChildren().addAll(
                createControlRow("Shift Left", "MOVE_LEFT"),
                createControlRow("Shift Right", "MOVE_RIGHT"),
                createControlRow("Soft Drop", "SOFT_DROP"),
                createControlRow("Hard Drop", "HARD_DROP"),
                createControlRow("Rotate", "ROTATE"),
                createControlRow("Rotate Left", "ROTATE_LEFT"),
                createControlRow("Rotate Right", "ROTATE_RIGHT"),
                createControlRow("Hold", "HOLD"),
                createControlRow("Pause", "PAUSE"));
    }

    /**
     * Creates a single row in the controls list for a specific action.
     *
     * @param action     the display name of the action
     * @param bindingKey the key identifier for looking up bindings
     * @return an HBox containing the label and rebind button
     */
    private HBox createControlRow(String action, String bindingKey) {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER);
        row.setPrefWidth(550);
        row.setMaxWidth(550);

        Label actionLabel = new Label(action);
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

    /**
     * Formats the current key bindings for display on the button.
     * Joins multiple keys with " / ".
     *
     * @param bindingKey the action key to look up
     * @return a formatted string of bound keys
     */
    private String getCurrentKeysDisplay(String bindingKey) {
        List<KeyCode> keys = keyBindings.getKeysForAction(bindingKey);
        if (keys.isEmpty()) {
            return "Not bound";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            if (i > 0)
                sb.append(" / ");
            sb.append(getKeyDisplayName(keys.get(i)));
        }
        return sb.toString();
    }

    /**
     * Returns a user-friendly display name for key codes.
     * Converts arrow keys to symbols and handles special keys.
     *
     * @param key the KeyCode to format
     * @return the display string
     */
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

    /**
     * Initiates the key rebinding process for a specific action.
     * Visual feedback indicates the system is waiting for input.
     *
     * @param bindingKey the action to rebind
     * @param button     the button that triggered the rebind
     */
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

    /**
     * Cancels the current rebinding operation, reverting UI state.
     */
    private void cancelRebind() {
        if (currentRebindButton != null) {
            currentRebindButton.setText(getCurrentKeysDisplay(currentlyRebinding));
            currentRebindButton.setStyle(getKeyButtonStyle());
        }
        currentlyRebinding = null;
        currentRebindButton = null;
    }

    /**
     * @return CSS style string for standard key buttons
     */
    private String getKeyButtonStyle() {
        return "-fx-font-size: 16px; " +
                "-fx-text-fill: white; " +
                "-fx-background-color: transparent; " +
                "-fx-padding: 5 15; " +
                "-fx-border-color: white; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 5; " +
                "-fx-background-radius: 5; " +
                "-fx-cursor: hand; " +
                "-fx-alignment: center;";
    }

    /**
     * @return CSS style string for hovered key buttons
     */
    private String getKeyButtonHoverStyle() {
        return "-fx-font-size: 16px; " +
                "-fx-text-fill: #FFD75C; " +
                "-fx-font-weight: bold; " +
                "-fx-background-color: rgba(255, 255, 255, 0.1); " +
                "-fx-padding: 5 15; " +
                "-fx-border-color: #FFD75C; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 5; " +
                "-fx-background-radius: 5; " +
                "-fx-cursor: hand; " +
                "-fx-alignment: center;";
    }

    /**
     * @return CSS style string for active rebinding state
     */
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

    /**
     * Returns the "Done" / "Back" button instance.
     *
     * @return the Button control
     */
    public Button getDoneButton() {
        return doneButton;
    }

    /**
     * Hides the "Back" button from the view.
     */
    public void hideBackButton() {
        if (doneButton != null) {
            doneButton.setVisible(false);
            doneButton.setManaged(false);
        }
    }

    /**
     * Hides the title label from the view.
     */
    public void hideTitle() {
        if (titleLabel != null) {
            titleLabel.setVisible(false);
            titleLabel.setManaged(false);
        }
    }
}