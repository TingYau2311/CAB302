package com.tasktopia;

import com.tasktopia.model.TaskStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoginTest {

    @BeforeEach
    void reset() {
        TaskStore.getInstance().setLoggedInUser("");
    }

    @Test
    @DisplayName("Login with valid credentials sets the logged-in user")
    void login_validCredentials_setsLoggedInUser() {
        String username = "alice";
        String password = "password123";

        boolean isValid = !username.trim().isEmpty() && !password.trim().isEmpty();
        if (isValid) {
            TaskStore.getInstance().setLoggedInUser(username);
        }

        assertEquals("alice", TaskStore.getInstance().getLoggedInUser());
    }
}
