package com.tasktopia;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Entry point for the Tasktopia JavaFX application.
 *
 * <p>This class bootstraps the JavaFX lifecycle by loading the main FXML view
 * and displaying it in a fixed-size primary window.</p>
 */
public class HelloApplication extends Application {

    /** The title displayed in the application window's title bar. */
    public static final String TITLE = "Tasktopia";

    /** The default width of the application window in pixels. */
    public static final int WIDTH = 900;

    /** The default height of the application window in pixels. */
    public static final int HEIGHT = 600;

    /**
     * Initialises and displays the primary stage.
     *
     * <p>Loads {@code /fxml/main-view.fxml}, wraps it in a {@link Scene} with
     * the configured dimensions, and shows the window.</p>
     *
     * @param stage the primary stage provided by the JavaFX runtime
     * @throws IOException if the FXML resource cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/fxml/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), WIDTH, HEIGHT);
        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Application entry point.
     *
     * @param args command-line arguments passed to the JavaFX launcher
     */
    public static void main(String[] args) {
        launch();
    }
}