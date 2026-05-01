package com.tasktopia;

import com.tasktopia.model.TaskStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LogoutTest {

    @BeforeEach
    void loginUser() {
        TaskStore.getInstance().setLoggedInUser("alice");
    }

    @Test
    @DisplayName("Logout clears the logged-in user")
    void logout_clearsLoggedInUser() {
        TaskStore.getInstance().setLoggedInUser("");

        assertEquals("", TaskStore.getInstance().getLoggedInUser());
    }
}
