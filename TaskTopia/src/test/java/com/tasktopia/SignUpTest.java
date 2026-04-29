package com.tasktopia;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

 public class SignUpTest {

    private String validateSignUp(String username, String password, String confirm) {
        if (username.isEmpty() || password.isEmpty()) return "Please fill all fields";
        if (!password.equals(confirm)) return "Passwords do not match";
        return null;
    }

    @Test
    @DisplayName("Sign up with valid details succeeds")
    void signUp_validDetails_succeeds() {
        String error = validateSignUp("alice", "secret123", "secret123");
        assertNull(error);
    }
}