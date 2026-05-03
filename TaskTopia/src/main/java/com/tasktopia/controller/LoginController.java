package com.tasktopia.controller;

import com.tasktopia.MainApp;
import com.tasktopia.model.Contact;
import com.tasktopia.model.IContactDAO;
import com.tasktopia.model.SqliteContactDAO;
import com.tasktopia.model.TaskStore;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import java.util.List;

import java.util.HashMap;
import java.util.Map;

public class LoginController {

    @FXML private TextField     emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label         errorLabel;

    // defining sign up/create account for hover effect
    @FXML private Button signInBtn;

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

        applyHover(signInBtn);
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
            ex.printStackTrace();
            showError("Failed to load home screen: " + ex.getMessage());
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
