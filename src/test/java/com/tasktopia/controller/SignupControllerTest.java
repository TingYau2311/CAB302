//package com.tasktopia.controller;
//
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.scene.control.Label;
//import javafx.scene.control.PasswordField;
//import javafx.scene.control.TextField;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.lang.reflect.Field;
//import java.lang.reflect.Method;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class SignupControllerTest {
//
//    private SignupController controller;
//
//    @BeforeEach
//    void setUp() throws Exception {
//        ClassLoader cl = Thread.currentThread().getContextClassLoader();
//
//        System.out.println(cl.getResource("/fxml/SignUp.fxml")); // debug
//
//        FXMLLoader loader = new FXMLLoader(
//                cl.getResource("/fxml/SignUp.fxml")
//        );
//
//        Parent root = loader.load();
//        controller = loader.getController();
//    }
//
//
//
//    private <T> T getPrivateField(String name, Class<T> type) throws Exception {
//        Field f = SignupController.class.getDeclaredField(name);
//        f.setAccessible(true);
//        return type.cast(f.get(controller));
//    }
//
//    private void invokeHandleSignup() throws Exception {
//        Method m = SignupController.class.getDeclaredMethod("handleSignup");
//        m.setAccessible(true);
//        m.invoke(controller);
//    }
//
//    @Test
//    void invalidEmailIsRejected() throws Exception {
//        TextField firstName = getPrivateField("firstNameField", TextField.class);
//        TextField lastName = getPrivateField("lastNameField", TextField.class);
//        TextField email = getPrivateField("emailField", TextField.class);
//        PasswordField password = getPrivateField("passwordField", PasswordField.class);
//        PasswordField confirm = getPrivateField("confirmPasswordField", PasswordField.class);
//        Label error = getPrivateField("errorLabel", Label.class);
//
//        firstName.setText("Angela");
//        lastName.setText("Lin");
//        email.setText("not-an-email");
//        password.setText("password123");
//        confirm.setText("password123");
//
//        invokeHandleSignup();
//
//        assertEquals("Please enter a valid email address.", error.getText());
//        assertTrue(error.isVisible());
//    }
//}
