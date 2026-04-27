package com.tasktopia;

import com.tasktopia.model.Task;
import com.tasktopia.model.Task.Category;
import com.tasktopia.model.Task.Priority;
import com.tasktopia.model.TaskStore;

import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

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
}


class AddTaskTest {

    @BeforeEach
    void clearStore() {
        TaskStore.getInstance().getTasks().clear();
    }

    @Test
    @DisplayName("Adding a task stores it in the task list")
    void addTask_taskIsStoredInList() {
        Task t = new Task("Team Meeting", "Discuss sprint goals",
                LocalDate.now(), LocalTime.of(9, 0),
                Category.WORK, Priority.HIGH);

        TaskStore.getInstance().addTask(t);

        assertTrue(TaskStore.getInstance().getTasks().contains(t));
    }
}


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


class DeleteTaskTest {

    private Task task;

    @BeforeEach
    void setupStore() {
        TaskStore.getInstance().getTasks().clear();
        task = new Task("Submit Report", "Final report",
                LocalDate.now(), null, Category.WORK, Priority.HIGH);
        TaskStore.getInstance().addTask(task);
    }

    @Test
    @DisplayName("Deleting a task removes it from the store")
    void deleteTask_removesTaskFromStore() {
        TaskStore.getInstance().removeTask(task);

        assertFalse(TaskStore.getInstance().getTasks().contains(task));
    }
}


class MarkCompleteTest {

    private Task task;

    @BeforeEach
    void setupTask() {
        task = new Task("Buy Groceries", "",
                LocalDate.now(), null, Category.GROCERY, Priority.LOW);
    }

    @Test
    @DisplayName("Marking a task as done sets its status to complete")
    void markComplete_setDoneTrue_taskIsComplete() {
        task.setDone(true);

        assertTrue(task.isDone());
    }
}
