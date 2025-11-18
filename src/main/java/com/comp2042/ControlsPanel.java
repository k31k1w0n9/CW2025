package com.comp2042;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ControlsPanel extends VBox {

    private Button doneButton;

    public ControlsPanel() {
        setAlignment(Pos.CENTER);
        setSpacing(15);
        setStyle("-fx-background-color: #3d4f6d; " +
                "-fx-border-color: white; " +
                "-fx-border-width: 3; " +
                "-fx-border-radius: 10; " +
                "-fx-background-radius: 10; " +
                "-fx-padding: 30;");
        setPrefSize(600, 550);
        setMaxSize(600, 550);
        setMinSize(600, 550);

        // Title with colorful letters
        HBox titleBox = createColorfulTitle();

        // Control mappings
        VBox controlsBox = new VBox(8);
        controlsBox.setAlignment(Pos.CENTER);

        controlsBox.getChildren().addAll(
                createControlRow("Shift Left", "← / A"),
                createControlRow("Shift Right", "→ / D"),
                createControlRow("Soft Drop", "↓ / S"),
                createControlRow("Hard Drop", "↑ / W / Spacebar"),
                createControlRow("Rotate Left", "Z / J"),
                createControlRow("Rotate Right", "X / K"),
                createControlRow("Hold", "C / L"),
                createControlRow("Pause", "Esc"),
                createControlRow("Fullscreen Toggle", "F")
        );

        // Back button
        doneButton = new Button("◄ Back ►");
        doneButton.setPrefSize(200, 45);
        doneButton.setStyle("-fx-background-color: rgba(90, 95, 127, 0.8); " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 18px; " +
                "-fx-border-color: white; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand;");
        doneButton.setOnMouseEntered(e -> doneButton.setStyle(
                "-fx-background-color: rgba(120, 125, 167, 1.0); " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-size: 18px; " +
                        "-fx-border-color: white; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 8; " +
                        "-fx-background-radius: 8; " +
                        "-fx-cursor: hand;"));
        doneButton.setOnMouseExited(e -> doneButton.setStyle(
                "-fx-background-color: rgba(90, 95, 127, 0.8); " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-size: 18px; " +
                        "-fx-border-color: white; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 8; " +
                        "-fx-background-radius: 8; " +
                        "-fx-cursor: hand;"));

        getChildren().addAll(titleBox, controlsBox, doneButton);
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
            letter.setStyle("-fx-font-size: 48px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-text-fill: " + colors[i] + "; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 5, 0.5, 2, 2);");
            titleBox.getChildren().add(letter);
        }

        return titleBox;
    }

    private HBox createControlRow(String action, String keys) {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER);
        row.setPrefWidth(550);
        row.setMaxWidth(550);

        Label actionLabel = new Label(action);
        actionLabel.setStyle("-fx-font-size: 16px; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: normal; " +
                "-fx-min-width: 180; " +
                "-fx-alignment: center-left;");

        // Separator line
        Label separator = new Label("─".repeat(15));
        separator.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.3); " +
                "-fx-font-size: 12px;");

        Label keysLabel = new Label(keys);
        keysLabel.setStyle("-fx-font-size: 16px; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-background-color: rgba(0, 0, 0, 0.3); " +
                "-fx-padding: 5 15; " +
                "-fx-border-color: rgba(255, 255, 255, 0.5); " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 5; " +
                "-fx-background-radius: 5; " +
                "-fx-min-width: 200; " +
                "-fx-alignment: center;");

        row.getChildren().addAll(actionLabel, separator, keysLabel);
        return row;
    }

    public Button getDoneButton() {
        return doneButton;
    }

    // Remove unused methods
    public Button getPrevButton() {
        return null; // Not used anymore
    }

    public Button getNextButton() {
        return null; // Not used anymore
    }
}