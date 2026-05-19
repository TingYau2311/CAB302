/*package com.tasktopia.controller;

import com.tasktopia.model.Contact;
import com.tasktopia.model.MockContactDAO;
import com.tasktopia.model.TaskStore;
import javafx.scene.control.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class LoginControllerTest {

    private LoginController controller;
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
        controller = new LoginController();

        setField(controller, "emailField", new TextField());
        setField(controller, "passwordField", new PasswordField());
        setField(controller, "errorLabel", new Label());
        setField(controller, "signInBtn", new Button());

        mockDAO = new MockContactDAO();
        setField(controller, "contactDAO", mockDAO);

        TaskStore.getInstance().setLoggedInUser(null);
        TaskStore.getInstance().setLoggedInUserId(-1);
    }

    // -------- Tests --------

    @Test
    void loginFailsWhenFieldsEmpty() {
        callPrivate(controller, "handleLogin");

        Label error = (Label) getField(controller, "errorLabel");
        assertEquals("Please enter both email and password.", error.getText());
    }

    @Test
    void loginFailsWithInvalidEmail() {
        ((TextField) getField(controller, "emailField")).setText("invalid");
        ((PasswordField) getField(controller, "passwordField")).setText("pass");

        callPrivate(controller, "handleLogin");

        Label error = (Label) getField(controller, "errorLabel");
        assertEquals("Please enter a valid email address.", error.getText());
    }

    @Test
    void loginFailsWithWrongCredentials() {
        mockDAO.addContact(new Contact("A", "B", "a@test.com", "pass"));

        ((TextField) getField(controller, "emailField")).setText("a@test.com");
        ((PasswordField) getField(controller, "passwordField")).setText("wrong");

        callPrivate(controller, "handleLogin");

        Label error = (Label) getField(controller, "errorLabel");
        assertEquals("Invalid email or password.", error.getText());
    }

    @Test
    void loginSucceedsAndStoresUser() {
        Contact c = new Contact("A", "B", "a@test.com", "pass");
        mockDAO.addContact(c);

        ((TextField) getField(controller, "emailField")).setText("a@test.com");
        ((PasswordField) getField(controller, "passwordField")).setText("pass");

        callPrivate(controller, "handleLogin");

        assertEquals("A B", TaskStore.getInstance().getLoggedInUser());
    }
}

 */