package com.tasktopia.controller;

import com.tasktopia.MainApp;
import com.tasktopia.model.TaskStore;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

public class LoginController {

    @FXML private TextField     usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label         errorLabel;

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);

        usernameField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) handleLogin();
        });
        passwordField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) handleLogin();
        });
    }

    @FXML
    private void handleLogin() {
        String user = usernameField.getText().trim();
        String pass = passwordField.getText().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            showError("Please enter both username and password.");
            return;
        }

        // Accept any non-empty credentials
        TaskStore.getInstance().setLoggedInUser(user);

        try {
            MainApp.showHome();
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Failed to load home screen.");
        }
    }

    @FXML
    private void goToSignup() {
        try {
            MainApp.showSignup();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
    }
}