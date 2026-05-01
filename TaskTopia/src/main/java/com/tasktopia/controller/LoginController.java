package com.tasktopia.controller;

import com.tasktopia.MainApp;
import com.tasktopia.model.Contact;
import com.tasktopia.model.IContactDAO;
import com.tasktopia.model.SqliteContactDAO;
import com.tasktopia.model.TaskStore;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import java.util.List;

public class LoginController {

    @FXML private TextField     emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label         errorLabel;

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);

        // Pressing Enter triggers login
        emailField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) handleLogin();
        });
        passwordField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) handleLogin();
        });
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String pass  = passwordField.getText().trim();

        if (email.isEmpty() || pass.isEmpty()) {
            showError("Please enter both email and password.");
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            showError("Please enter a valid email address.");
            return;
        }

        // Check credentials against database
        IContactDAO contactDAO = new SqliteContactDAO();
        List<Contact> contacts = contactDAO.getAllContacts();

        Contact matched = contacts.stream()
                .filter(c -> c.getEmail().equalsIgnoreCase(email)
                        && c.getPassword().equals(pass))
                .findFirst()
                .orElse(null);

        if (matched == null) {
            showError("Invalid email or password.");
            return;
        }


        // Store logged in user's full name and go to home
        TaskStore.getInstance().setLoggedInUser(matched.getFirstName()
                + " " + matched.getLastName());
        TaskStore.getInstance().setLoggedInUserId(matched.getId());

        try {
            MainApp.showHome();
        } catch (Exception ex) {
            showError("Failed to load home screen.");
        }
    }

    @FXML
    private void goToSignup() {
        try {
            MainApp.showSignup();
        } catch (Exception ex) {
            showError("Could not navigate to sign up. Please restart.");
        }
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
    }
}