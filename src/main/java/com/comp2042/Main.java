package com.comp2042;

import java.net.URL;

import com.comp2042.core.GameController;
import com.comp2042.core.GuiController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main entry point for the TetrisJFX application.
 * Extends {@link Application} to initialise the JavaFX runtime and display the
 * game window.
 */
public class Main extends Application {

    /**
     * Initialises and displays the primary stage of the application.
     * Loads the FXML layout, sets up the scene, and creates the game controller.
     *
     * @param primaryStage the primary stage for this application
     * @throws Exception if the FXML file cannot be loaded
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        URL location = getClass().getClassLoader().getResource("gameLayout.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(location);
        Parent root = fxmlLoader.load();
        GuiController c = fxmlLoader.getController();

        primaryStage.setTitle("TetrisJFX");

        Scene scene = new Scene(root, 1300, 850);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1200);
        primaryStage.setMinHeight(800);
        primaryStage.setResizable(true);
        primaryStage.show();

        new GameController(c);
    }

    /**
     * The main method which launches the JavaFX application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
