package com.tasktopia.model;

import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskStoreTest {

    private TaskStore store;

    @BeforeEach
    void setUp() {
        store = TaskStore.getInstance();
        store.getTasks().clear();
        store.setLoggedInUser("");
        store.setLoggedInUserId(-1);
    }

    @Test
    void addTaskWorks() {
        Task t = new Task(
                "Test Task",
                "Description",
                LocalDate.now(),
                LocalTime.NOON,
                Task.Category.WORK,
                Task.Priority.MEDIUM
        );

        store.addTask(t);

        ObservableList<Task> tasks = store.getTasks();
        assertEquals(1, tasks.size());
        assertEquals("Test Task", tasks.get(0).getTitle());
    }

    @Test
    void removeTaskWorks() {
        Task t = new Task(
                "Task A",
                "Desc",
                LocalDate.now(),
                LocalTime.NOON,
                Task.Category.PERSONAL,
                Task.Priority.LOW
        );

        store.addTask(t);
        store.removeTask(t);

        assertTrue(store.getTasks().isEmpty());
    }

    @Test
    void loggedInUserSetAndGet() {
        store.setLoggedInUser("angela@example.com");
        assertEquals("angela@example.com", store.getLoggedInUser());
    }

    @Test
    void loggedInUserIdSetAndGet() {
        store.setLoggedInUserId(42);
        assertEquals(42, store.getLoggedInUserId());
    }
}
