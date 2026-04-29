package com.tasktopia.controller;

import com.tasktopia.MainApp;
import com.tasktopia.model.Contact;
import com.tasktopia.model.IContactDAO;
import com.tasktopia.model.SqliteContactDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

public class SignupController {

    @FXML private TextField     usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label         errorLabel;

    private IContactDAO contactDAO;

    @FXML
    public void initialize() {
        contactDAO = new SqliteContactDAO();
        errorLabel.setVisible(false);

        confirmPasswordField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) handleSignup();
        });
    }

    @FXML
    private void handleSignup() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String confirm  = confirmPasswordField.getText().trim();

        // Only check that fields are not empty and passwords match
        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            showError("All fields are required.");
            return;
        }
        if (!password.equals(confirm)) {
            showError("Passwords do not match.");
            return;
        }

        // Save new user
        Contact newUser = new Contact(username, "", username + "@tasktopia.local", password);
        contactDAO.addContact(newUser);

        try {
            MainApp.showLogin();
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Account created! Please return to login.");
        }
    }

    @FXML
    private void goToLogin() {
        try {
            MainApp.showLogin();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
    }
}