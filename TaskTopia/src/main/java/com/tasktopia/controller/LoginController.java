package com.tasktopia.controller;

import com.tasktopia.MainApp;
import com.tasktopia.model.TaskStore;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.util.HashMap;
import java.util.Map;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    // defining sign up/create account for hover effect
    @FXML private Button signInBtn;

    @FXML
    public void initialize() {

        // Hide error label initially
        errorLabel.setVisible(false);

        // Pressing Enter triggers login
        usernameField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) handleLogin();
        });
        passwordField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) handleLogin();
        });

        applyHover(signInBtn);
    }

    @FXML
    private void handleLogin() {
        String user = usernameField.getText().trim();
        String pass = passwordField.getText().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            showError("Please enter both username and password.");
            return;
        }

        // Prototype: accept any non-empty credentials
        TaskStore.getInstance().setLoggedInUser(user);

        try {
            MainApp.showHome();   // This MUST load /fxml/home.fxml
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Failed to load home screen.");
        }
    }

    @FXML
    private void onGoToSignUp() {
        System.out.println("CLICKED");
        try {
            MainApp.showSignUp();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
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
