package com.tasktopia;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main JavaFX application class for TaskTopia.
 *
 * <p>Manages the primary {@link Stage} and acts as a central navigation hub,
 * swapping scenes between the Login, Sign-Up, and Home views without creating
 * new windows.</p>
 */
public class MainApp extends Application {

    /**
     * The application's primary stage, held statically so that any class can
     * trigger a scene transition via the {@code show*()} navigation methods.
     */
    public static Stage primaryStage;

    /**
     * Initialises the primary stage and navigates to the Login screen.
     *
     * <p>Sets the window title and minimum dimensions before displaying the
     * initial scene.</p>
     *
     * @param stage the primary stage provided by the JavaFX runtime
     * @throws Exception if the Login FXML resource cannot be loaded
     */
    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        stage.setTitle("TaskTopia");
        stage.setMinWidth(899);
        stage.setMinHeight(599);
        showLogin();
        stage.show();
    }

    /**
     * Navigates to the Login screen.
     *
     * <p>Loads {@code /fxml/Login.fxml} and replaces the current scene on the
     * primary stage.</p>
     *
     * @throws Exception if the Login FXML resource cannot be loaded
     */
    public static void showLogin() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("/fxml/Login.fxml")
        );
        Scene scene = new Scene(loader.load(), 1099, 700);
        primaryStage.setScene(scene);
    }

    /**
     * Navigates to the Sign-Up screen.
     *
     * <p>Loads {@code /fxml/SignUp.fxml} and replaces the current scene on the
     * primary stage.</p>
     *
     * @throws Exception if the SignUp FXML resource cannot be loaded
     */
    public static void showSignUp() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("/fxml/SignUp.fxml")
        );
        Scene scene = new Scene(loader.load(), 1099, 700);
        primaryStage.setScene(scene);
    }

    /**
     * Navigates to the Home screen.
     *
     * <p>Loads {@code /fxml/Home.fxml} and replaces the current scene on the
     * primary stage.</p>
     *
     * @throws Exception if the Home FXML resource cannot be loaded
     */
    public static void showHome() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("/fxml/Home.fxml")
        );
        Scene scene = new Scene(loader.load(), 1099, 700);
        primaryStage.setScene(scene);
    }

    /**
     * Application entry point.
     *
     * @param args command-line arguments passed to the JavaFX launcher
     */
    public static void main(String[] args) {
        launch(args);
    }
}
