package com.tasktopia.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class SignupControllerTest {

    private SignupController controller;

    @BeforeEach
    void setUp() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SignUp.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
    }

    private void invokeHandleSignup() throws Exception {
        Method m = SignupController.class.getDeclaredMethod("handleSignup");
        m.setAccessible(true);
        m.invoke(controller);
    }

    @Test
    void invalidEmailIsRejected() throws Exception {
        ((TextField) controller.getClass().getDeclaredField("firstNameField")
                .get(controller)).setText("John");
        ((TextField) controller.getClass().getDeclaredField("lastNameField")
                .get(controller)).setText("Smith");
        ((TextField) controller.getClass().getDeclaredField("emailField")
                .get(controller)).setText("invalid-email");
        ((PasswordField) controller.getClass().getDeclaredField("passwordField")
                .get(controller)).setText("pass123");
        ((PasswordField) controller.getClass().getDeclaredField("confirmPasswordField")
                .get(controller)).setText("pass123");

        invokeHandleSignup();

        Label error = (Label) controller.getClass().getDeclaredField("errorLabel").get(controller);
        assertEquals("Please enter a valid email address.", error.getText());
    }
}
