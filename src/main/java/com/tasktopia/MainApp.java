package com.tasktopia;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    public static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        stage.setTitle("TaskTopia");
        stage.setMinWidth(899);
        stage.setMinHeight(599);
        showLogin();
        stage.show();
    }

    public static void showLogin() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("/fxml/Login.fxml")
        );
        Scene scene = new Scene(loader.load(), 1099, 700);
        primaryStage.setScene(scene);
    }

    public static void showHome() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("/fxml/Home.fxml")
        );
        Scene scene = new Scene(loader.load(), 1099, 700);
        primaryStage.setScene(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
