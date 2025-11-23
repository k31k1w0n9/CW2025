package com.comp2042;

import javafx.animation.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.io.InputStream;

public class NotificationPanel extends BorderPane {

    private Label scoreLabel;

    public NotificationPanel(String text) {
        setMinHeight(200);
        setMinWidth(220);

        // Load custom font
        Font customFont = null;
        try {
            InputStream fontStream = getClass().getResourceAsStream("/BoutiqueBitmap9x9_Bold_1.9.ttf");
            if (fontStream != null) {
                customFont = Font.loadFont(fontStream, 48);
            }
        } catch (Exception e) {
            System.err.println("Could not load custom font for NotificationPanel");
        }
        if (customFont == null) {
            customFont = Font.font("Arial", FontWeight.BOLD, 48);
        }

        // Create score label
        scoreLabel = new Label(text);
        scoreLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);  // Center multi-line text

        // FIXED: Use custom pixel font
        scoreLabel.setFont(customFont);
        scoreLabel.setTextFill(Color.WHITE);


        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.YELLOW);
        dropShadow.setRadius(25);
        dropShadow.setSpread(0.8);

        Glow glow = new Glow(0.8);
        dropShadow.setInput(glow);  // Combine both effects

        scoreLabel.setEffect(dropShadow);

        setCenter(scoreLabel);

        // Make the panel itself transparent so only text is visible
        setStyle("-fx-background-color: transparent;");
        setPickOnBounds(false);
        setMouseTransparent(true);
    }

    public void showScore(ObservableList<Node> list) {

        // Phase 1: Pop in with scale effect
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(150), this);
        scaleIn.setFromX(0.3);
        scaleIn.setFromY(0.3);
        scaleIn.setToX(1.2);
        scaleIn.setToY(1.2);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(150), this);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        ParallelTransition popIn = new ParallelTransition(scaleIn, fadeIn);

        // Phase 2: Scale back to normal
        ScaleTransition scaleNormal = new ScaleTransition(Duration.millis(100), this);
        scaleNormal.setToX(1.0);
        scaleNormal.setToY(1.0);

        // Phase 3: Hold briefly
        PauseTransition hold = new PauseTransition(Duration.millis(600));

        // Phase 4: Float up and fade out
        TranslateTransition floatUp = new TranslateTransition(Duration.millis(800), this);
        floatUp.setToY(this.getLayoutY() - 60);  // Float up more

        FadeTransition fadeOut = new FadeTransition(Duration.millis(800), this);
        fadeOut.setToValue(0);

        ParallelTransition floatAndFade = new ParallelTransition(floatUp, fadeOut);

        // Combine all phases
        SequentialTransition fullSequence = new SequentialTransition(
                popIn,
                scaleNormal,
                hold,
                floatAndFade
        );

        fullSequence.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                list.remove(NotificationPanel.this);
            }
        });

        fullSequence.play();
    }
}