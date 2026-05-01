package com.tasktopia;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class SignUpTest {

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

    @Test
    @DisplayName("Sign up with empty username fails")
    void signUp_emptyUsername_fails() {
        String error = validateSignUp("", "secret123", "secret123");
        assertEquals("Please fill all fields", error);
    }

    @Test
    @DisplayName("Sign up with empty password fails")
    void signUp_emptyPassword_fails() {
        String error = validateSignUp("alice", "", "");
        assertEquals("Please fill all fields", error);
    }

    @Test
    @DisplayName("Sign up with mismatched passwords fails")
    void signUp_passwordMismatch_fails() {
        String error = validateSignUp("alice", "secret123", "different");
        assertEquals("Passwords do not match", error);
    }
}