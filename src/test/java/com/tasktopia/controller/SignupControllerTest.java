package com.tasktopia.controller;

import com.tasktopia.model.Contact;
import com.tasktopia.model.MockContactDAO;
import javafx.scene.control.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class SignupControllerTest {

    private SignupController controller;
    private MockContactDAO mockDAO;

    // -------- JavaFX Toolkit Init --------

    @BeforeAll
    static void initJavaFX() {
        JavaFXInitializer.init();
    }

    // -------- Reflection Helpers --------

    private void setField(Object target, String fieldName, Object value) {
        try {
            Class<?> cls = target.getClass();
            Field f = null;

            while (cls != null) {
                try {
                    f = cls.getDeclaredField(fieldName);
                    break;
                } catch (NoSuchFieldException ignored) {
                    cls = cls.getSuperclass();
                }
            }

            if (f == null) throw new RuntimeException("Field not found: " + fieldName);

            f.setAccessible(true);
            f.set(target, value);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object getField(Object target, String fieldName) {
        try {
            Class<?> cls = target.getClass();
            Field f = null;

            while (cls != null) {
                try {
                    f = cls.getDeclaredField(fieldName);
                    break;
                } catch (NoSuchFieldException ignored) {
                    cls = cls.getSuperclass();
                }
            }

            if (f == null) throw new RuntimeException("Field not found: " + fieldName);

            f.setAccessible(true);
            return f.get(target);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void callPrivate(Object target, String methodName) {
        try {
            Method m = target.getClass().getDeclaredMethod(methodName);
            m.setAccessible(true);
            m.invoke(target);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // -------- Test Setup --------

    @BeforeEach
    void setUp() {
        controller = new SignupController();

        setField(controller, "firstNameField", new TextField());
        setField(controller, "lastNameField", new TextField());
        setField(controller, "emailField", new TextField());
        setField(controller, "passwordField", new PasswordField());
        setField(controller, "confirmPasswordField", new PasswordField());
        setField(controller, "errorLabel", new Label());

        mockDAO = new MockContactDAO();
        setField(controller, "contactDAO", mockDAO);
    }

    // -------- Tests --------

    @Test
    void signupFailsWhenFieldsAreEmpty() {
        callPrivate(controller, "handleSignup");

        Label error = (Label) getField(controller, "errorLabel");
        assertTrue(error.isVisible());
        assertEquals("All fields are required.", error.getText());
    }

    @Test
    void signupFailsWithInvalidEmail() {
        ((TextField) getField(controller, "firstNameField")).setText("A");
        ((TextField) getField(controller, "lastNameField")).setText("B");
        ((TextField) getField(controller, "emailField")).setText("invalid");
        ((PasswordField) getField(controller, "passwordField")).setText("pass");
        ((PasswordField) getField(controller, "confirmPasswordField")).setText("pass");

        callPrivate(controller, "handleSignup");

        Label error = (Label) getField(controller, "errorLabel");
        assertEquals("Please enter a valid email address.", error.getText());
    }

    @Test
    void signupFailsWhenPasswordsDoNotMatch() {
        ((TextField) getField(controller, "firstNameField")).setText("A");
        ((TextField) getField(controller, "lastNameField")).setText("B");
        ((TextField) getField(controller, "emailField")).setText("a@test.com");
        ((PasswordField) getField(controller, "passwordField")).setText("pass1");
        ((PasswordField) getField(controller, "confirmPasswordField")).setText("pass2");

        callPrivate(controller, "handleSignup");

        Label error = (Label) getField(controller, "errorLabel");
        assertEquals("Passwords do not match.", error.getText());
    }

    @Test
    void signupFailsWhenEmailAlreadyExists() {
        mockDAO.addContact(new Contact("A", "B", "a@test.com", "pass"));

        ((TextField) getField(controller, "firstNameField")).setText("A");
        ((TextField) getField(controller, "lastNameField")).setText("B");
        ((TextField) getField(controller, "emailField")).setText("a@test.com");
        ((PasswordField) getField(controller, "passwordField")).setText("pass");
        ((PasswordField) getField(controller, "confirmPasswordField")).setText("pass");

        callPrivate(controller, "handleSignup");

        Label error = (Label) getField(controller, "errorLabel");
        assertEquals("An account with that email already exists.", error.getText());
    }

    @Test
    void signupSucceedsAndAddsContact() {
        ((TextField) getField(controller, "firstNameField")).setText("A");
        ((TextField) getField(controller, "lastNameField")).setText("B");
        ((TextField) getField(controller, "emailField")).setText("new@test.com");
        ((PasswordField) getField(controller, "passwordField")).setText("pass");
        ((PasswordField) getField(controller, "confirmPasswordField")).setText("pass");

        callPrivate(controller, "handleSignup");

        assertEquals(1, mockDAO.getAllContacts().size());
        Contact saved = mockDAO.getAllContacts().get(0);
        assertEquals("new@test.com", saved.getEmail());
    }
}

