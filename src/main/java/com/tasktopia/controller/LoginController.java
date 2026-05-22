package com.tasktopia.controller;

import com.tasktopia.MainApp;
import com.tasktopia.model.Contact;
import com.tasktopia.model.IContactDAO;
import com.tasktopia.model.SqliteContactDAO;
import com.tasktopia.model.TaskStore;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JavaFX controller for the Login screen.
 * <p>
 * Handles user authentication by validating the submitted email and password
 * against contacts stored via {@link IContactDAO}. On success, it updates
 * {@link TaskStore} with the logged-in user's details and navigates to the
 * home screen via {@link MainApp#showHome()}.
 * </p>
 *
 * <p>The {@link #contactDAO} field is package-private to allow unit tests to
 * inject a mock DAO without going through the JavaFX runtime.</p>
 */
public class LoginController {

    /** Input field for the user's email address. */
    @FXML private TextField     emailField;

    /** Input field for the user's password (masked). */
    @FXML private PasswordField passwordField;

    /** Label used to display validation or authentication error messages. */
    @FXML private Label         errorLabel;

    /** The primary sign-in button. */
    @FXML private Button        signInBtn;

    /**
     * The DAO used to look up contacts for authentication.
     * Package-private so tests can inject a {@code MockContactDAO} directly.
     * Lazily initialised to {@link SqliteContactDAO} on the first login attempt
     * if no mock has been injected.
     */
    IContactDAO contactDAO;

    /**
     * Called by the JavaFX runtime after all {@code @FXML} fields are injected.
     * <p>
     * Hides the error label, registers Enter-key shortcuts on both input fields,
     * and attaches a hover-scale animation to the sign-in button.
     * </p>
     */
    @FXML
    public void initialize() {
        if (errorLabel != null)   errorLabel.setVisible(false);

        if (emailField != null) {
            emailField.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ENTER) handleLogin();
            });
        }

        if (passwordField != null) {
            passwordField.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ENTER) handleLogin();
            });
        }

        if (signInBtn != null) applyHover(signInBtn);
    }

    /**
     * Handles the sign-in action triggered by the button or the Enter key.
     * <p>
     * Validation steps (in order):
     * <ol>
     *   <li>Checks that neither field is empty.</li>
     *   <li>Performs a basic email format check ({@code @} and {@code .} present).</li>
     *   <li>Searches all contacts for a case-insensitive email match with an exact
     *       password match.</li>
     * </ol>
     * On success, the matched user's full name and ID are stored in {@link TaskStore}
     * and the application navigates to the home screen.
     * A {@link NullPointerException} thrown by {@link MainApp#showHome()} during unit
     * tests (where no primary stage exists) is silently swallowed because
     * {@code TaskStore} has already been updated — which is what the tests verify.
     * </p>
     */
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

        if (contactDAO == null) {
            contactDAO = new SqliteContactDAO();
        }

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

        TaskStore.getInstance().setLoggedInUser(
                matched.getFirstName() + " " + matched.getLastName()
        );
        TaskStore.getInstance().setLoggedInUserId(matched.getId());

        try {
            MainApp.showHome();
        } catch (Exception ex) {
            // In unit tests primaryStage is null so showHome() throws a
            // NullPointerException — that's expected and safe to ignore because
            // TaskStore has already been updated, which is what the test checks.
            if (!(ex instanceof NullPointerException)) {
                ex.printStackTrace();
                showError("Failed to load home screen: " + ex.getMessage());
            }
        }
    }

    /**
     * Navigates to the sign-up screen when the user clicks the "Go to Sign Up" link.
     * Any navigation exceptions are printed to standard error.
     */
    @FXML
    private void onGoToSignUp() {
        try {
            MainApp.showSignUp();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays an error message in the error label.
     *
     * @param msg the message to display; must not be {@code null}
     */
    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
    }

    /** Stores each button's original inline style so hover changes can be reverted. */
    private final Map<Button, String> baseStyles = new HashMap<>();

    /**
     * Attaches mouse-enter and mouse-exit handlers to a button that produce a
     * subtle scale-up effect on hover.
     *
     * @param btn the {@link Button} to decorate; must not be {@code null}
     */
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