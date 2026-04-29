package com.tasktopia.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.tasktopia.MainApp;

import java.util.HashMap;
import java.util.Map;


public class SignUpController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    // defining sign up/create account for hover effect
    @FXML private Button signUpBtn;

    // initializing for hover effect
    @FXML
    public void initialize() {
        applyHover(signUpBtn);
    }

    @FXML
    private void onSignUp() {
        String user = usernameField.getText().trim();
        String pass = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            showAlert("Please fill all fields");
            return;
        }

        if (!pass.equals(confirm)) {
            showAlert("Passwords do not match");
            return;
        }

        // TEMPORARY STORAGE replace later with DB !!!
        System.out.println("User registered: " + user);

        showAlert("Account created! Please login.");

        try {
            MainApp.showLogin();
        } catch (Exception e) {
            showAlert("Could not return to login screen.");
        }
    }

    @FXML
    private void onBackToLogin() {
        try {
            MainApp.showLogin();
        } catch (Exception e) {
            showError("Failed to load login screen.");
        }
    }

    // displays error pop up for user
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    //
    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private final Map<Button, String> baseStyles = new HashMap<>();

    private void applyHover(Button btn) {
        String base = btn.getStyle();
        baseStyles.put(btn, base);

        btn.setOnMouseEntered(e ->
                btn.setStyle(baseStyles.get(btn) + "-fx-scale-x: 1.05; -fx-scale-y: 1.05;")
        );

        btn.setOnMouseExited(e ->
                btn.setStyle(baseStyles.get(btn))
        );
    }
}