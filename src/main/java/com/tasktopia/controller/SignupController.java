package com.tasktopia.controller;

import com.tasktopia.MainApp;
import com.tasktopia.model.Contact;
import com.tasktopia.model.IContactDAO;
import com.tasktopia.model.SqliteContactDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.List;

/**
 * JavaFX controller for the Sign-Up screen.
 * <p>
 * Collects a new user's personal details and credentials, validates the input,
 * and persists the new {@link Contact} via {@link IContactDAO}. On success,
 * the user is redirected to the login screen via {@link MainApp#showLogin()}.
 * </p>
 *
 * <p>The {@link #contactDAO} field is package-private to allow unit tests to
 * inject a mock DAO. Note that {@link #initialize()} deliberately does
 * <em>not</em> create the DAO — only {@link #handleSignup()} does, via a
 * null-check guard — so tests can inject the mock after construction without
 * the FX runtime overwriting it.</p>
 */
public class SignupController {

    /** Input field for the new user's first name. */
    @FXML private TextField     firstNameField;

    /** Input field for the new user's last name. */
    @FXML private TextField     lastNameField;

    /** Input field for the new user's email address. */
    @FXML private TextField     emailField;

    /** Input field for the chosen password (masked). */
    @FXML private PasswordField passwordField;

    /** Input field to confirm the chosen password (masked). */
    @FXML private PasswordField confirmPasswordField;

    /** Label used to display validation or error messages to the user. */
    @FXML private Label         errorLabel;

    /**
     * The DAO used to check for duplicate emails and persist the new contact.
     * Package-private so tests can inject a {@code MockContactDAO} directly.
     * Lazily initialised to {@link SqliteContactDAO} in {@link #handleSignup()}
     * if no mock has been injected.
     */
    IContactDAO contactDAO;

    /**
     * Called by the JavaFX runtime after all {@code @FXML} fields are injected.
     * <p>
     * Hides the error label and registers an Enter-key shortcut on the confirm
     * password field. The DAO is intentionally <em>not</em> created here so that
     * unit tests can inject a mock after construction.
     * </p>
     */
    @FXML
    public void initialize() {
        // NOTE: DAO is intentionally NOT initialised here.
        // initialize() is only called by the FX runtime; tests inject the mock
        // via reflection after construction and never call initialize().
        // The null-check guard in handleSignup() handles both cases.

        if (errorLabel != null) errorLabel.setVisible(false);

        if (confirmPasswordField != null) {
            confirmPasswordField.setOnKeyPressed(e -> {
                if (e.getCode() == javafx.scene.input.KeyCode.ENTER) handleSignup();
            });
        }
    }

    /**
     * Handles the sign-up form submission.
     * <p>
     * Validation steps (in order):
     * <ol>
     *   <li>Ensures no field is left blank.</li>
     *   <li>Performs a basic email format check ({@code @} and {@code .} present).</li>
     *   <li>Confirms that the password and confirmation password match.</li>
     *   <li>Checks whether the email address is already registered (case-insensitive).</li>
     * </ol>
     * On successful validation, a new {@link Contact} is created and persisted.
     * The application then attempts to navigate to the login screen; if navigation
     * fails (e.g. in a test environment), the error label is used to inform the
     * user that their account was created and they should log in.
     * </p>
     */
    @FXML
    private void handleSignup() {
        // Only create real DAO if tests haven't injected one
        if (contactDAO == null) {
            contactDAO = new SqliteContactDAO();
        }

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

    /**
     * Navigates back to the login screen when the user clicks the "Go to Login" link.
     * If navigation fails, an error message is shown in the error label.
     */
    @FXML
    private void goToLogin() {
        try {
            MainApp.showLogin();
        } catch (Exception ex) {
            showError("Could not navigate to login. Please restart.");
        }
    }

    /**
     * Displays an error or informational message in the error label.
     *
     * @param msg the message to display; must not be {@code null}
     */
    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
    }
}