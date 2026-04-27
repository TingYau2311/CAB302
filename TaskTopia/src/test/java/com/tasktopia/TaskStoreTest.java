package com.tasktopia;

import com.tasktopia.model.Task;
import com.tasktopia.model.TaskStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class TaskStoreTest {

    private TaskStore store;

    @BeforeEach
    void setup() {
        store = TaskStore.getInstance();
        store.getTasks().clear(); // Reset list for clean tests
    }

    @Test
    void testAddTask() {
        Task task = new Task(
                "Buy Milk",
                "2L full cream",
                LocalDate.now(),
                LocalTime.NOON,
                Task.Category.GROCERY,
                Task.Priority.MEDIUM
        );

        store.addTask(task);

        assertEquals(1, store.getTasks().size());
        assertEquals("Buy Milk", store.getTasks().get(0).getName());
    }

    @Test
    void testRemoveTask() {
        Task task = new Task(
                "Meeting",
                "Discuss project",
                LocalDate.now(),
                LocalTime.now(),
                Task.Category.WORK,
                Task.Priority.HIGH
        );

        store.addTask(task);
        store.removeTask(task);

        assertTrue(store.getTasks().isEmpty());
    }

    @Test
    void testLoggedInUserSetAndGet() {
        store.setLoggedInUser("angela");
        assertEquals("angela", store.getLoggedInUser());
    }
}
