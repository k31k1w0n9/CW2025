package com.comp2042.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * A modal dialog panel that prompts the player to enter their name after
 * achieving a high score.
 * Features a styled text field that limits input to 8 uppercase characters and
 * an OK button.
 * Designed to overlay the game screen with a translucent background.
 */
public class NameInputDialog extends VBox {

    private TextField nameTextField;
    private Button okButton;
    private Label scoreLabel;

    /**
     * Constructs a new NameInputDialog.
     * Sets up the visual style, including title, score display, and input field.
     * Adds listeners to the input field to enforce uppercase formatting and length
     * limits.
     */
    public NameInputDialog() {
        setAlignment(Pos.CENTER);
        setSpacing(12); // Reduced spacing
        setStyle("-fx-background-color: rgba(29, 39, 56, 0.98); " +
                "-fx-border-color: white; " +
                "-fx-border-width: 3; " +
                "-fx-border-radius: 10; " +
                "-fx-background-radius: 10; " +
                "-fx-padding: 40;");
        setPrefSize(450, 400);
        setMaxSize(450, 400);
        setMinSize(450, 400);

        // Top spacer
        VBox topSpacer = new VBox();
        topSpacer.setPrefHeight(15);

        // Title
        Label titleLabel = new Label("NEW HIGH SCORE!");
        titleLabel.setStyle("-fx-font-size: 32px; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: white; " +
                "-fx-effect: dropshadow(gaussian, rgba(255, 255, 255, 0.8), 15, 0.5, 0, 0);");

        // Score display
        scoreLabel = new Label("0");
        scoreLabel.setStyle("-fx-font-size: 64px; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: white; " +
                "-fx-effect: dropshadow(gaussian, rgba(255, 255, 255, 0.5), 10, 0.5, 0, 0); " +
                "-fx-padding: 5 0 5 0;");

        // Prompt
        Label promptLabel = new Label("ENTER YOUR INITIALS:");
        promptLabel.setStyle("-fx-font-size: 16px; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: white;");

        // Name input field
        nameTextField = new TextField();
        nameTextField.setPromptText("");
        nameTextField.setAlignment(Pos.CENTER);
        nameTextField.setPrefSize(300, 60);
        nameTextField.setMaxWidth(300);
        nameTextField.setStyle("-fx-font-size: 28px; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: white; " +
                "-fx-background-color: rgba(0, 0, 0, 0.6); " +
                "-fx-border-color: white; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 5; " +
                "-fx-background-radius: 5; " +
                "-fx-padding: 10;");

        // Limit to 8 characters and convert to uppercase
        nameTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() > 8) {
                nameTextField.setText(oldVal);
            }
            if (!newVal.equals(newVal.toUpperCase())) {
                nameTextField.setText(newVal.toUpperCase());
            }
        });

        // OK Button
        okButton = new Button("OK");
        okButton.setPrefSize(180, 50);
        VBox.setMargin(okButton, new javafx.geometry.Insets(15, 0, 0, 0)); // Add 15px top margin
        okButton.setStyle("-fx-background-color: rgba(90, 95, 127, 0.8); " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 18px; " +
                "-fx-border-color: white; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand;");
        okButton.setOnMouseEntered(e -> okButton.setStyle(
                "-fx-background-color: rgba(120, 125, 167, 1.0); " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-size: 18px; " +
                        "-fx-border-color: white; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 8; " +
                        "-fx-background-radius: 8; " +
                        "-fx-cursor: hand;"));
        okButton.setOnMouseExited(e -> okButton.setStyle(
                "-fx-background-color: rgba(90, 95, 127, 0.8); " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-size: 18px; " +
                        "-fx-border-color: white; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 8; " +
                        "-fx-background-radius: 8; " +
                        "-fx-cursor: hand;"));

        getChildren().addAll(topSpacer, titleLabel, scoreLabel, promptLabel, nameTextField, okButton);
    }

    /**
     * Updates the score displayed on the dialog.
     *
     * @param score the player's final score to display
     */
    public void setScore(int score) {
        scoreLabel.setText(String.format("%,d", score));
    }

    /**
     * Retrieves the name entered by the player.
     *
     * @return the entered name, or "AAA" if the field is empty or contains only
     *         whitespace
     */
    public String getPlayerName() {
        String name = nameTextField.getText().trim();
        return name.isEmpty() ? "AAA" : name;
    }

    /**
     * Returns the "OK" button control.
     *
     * @return the OK button
     */
    public Button getOkButton() {
        return okButton;
    }

    /**
     * Returns the text field for name input.
     *
     * @return the name TextField
     */
    public TextField getNameTextField() {
        return nameTextField;
    }

    /**
     * Resets the dialog to its initial state.
     * Clears the text field and requests focus for immediate typing.
     */
    public void reset() {
        nameTextField.clear();
        nameTextField.requestFocus();
    }
}