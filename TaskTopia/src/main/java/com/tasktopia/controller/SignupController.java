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
import java.util.List;

public class SignupController {

    @FXML private TextField     firstNameField;
    @FXML private TextField     lastNameField;
    @FXML private TextField     emailField;
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
        String firstName = firstNameField.getText().trim();
        String lastName  = lastNameField.getText().trim();
        String email     = emailField.getText().trim();
        String password  = passwordField.getText().trim();
        String confirm   = confirmPasswordField.getText().trim();


        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()
                || password.isEmpty() || confirm.isEmpty()) {
            showError("All fields are required.");
            return;
        }
        if (!email.contains("@") || !email.contains(".")) {
            showError("Please enter a valid email address.");
            return;
        }
        if (!password.equals(confirm)) {
            showError("Passwords do not match.");
            return;
        }

        List<Contact> existing = contactDAO.getAllContacts();
        boolean emailTaken = existing.stream()
                .anyMatch(c -> c.getEmail().equalsIgnoreCase(email));
        if (emailTaken) {
            showError("An account with that email already exists.");
            return;
        }

        Contact newContact = new Contact(firstName, lastName, email, password);
        contactDAO.addContact(newContact);

        try {
            MainApp.showLogin();
        } catch (Exception ex) {
            showError("Account created! Please log in.");
        }
    }

    @FXML
    private void goToLogin() {
        try {
            MainApp.showLogin();
        } catch (Exception ex) {
            showError("Could not navigate to login. Please restart.");
        }
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
    }
}