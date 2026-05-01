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
        store.setLoggedInUser(null); // Reset user state
    }

    @Test
    void testSingletonInstance() {
        TaskStore s1 = TaskStore.getInstance();
        TaskStore s2 = TaskStore.getInstance();

        assertSame(s1, s2, "TaskStore should always return the same instance");
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

    @Test
    void testClearAllTasks() {
        Task t1 = new Task("A", "d", LocalDate.now(), LocalTime.NOON, Task.Category.WORK, Task.Priority.LOW);
        Task t2 = new Task("B", "d", LocalDate.now(), LocalTime.NOON, Task.Category.WORK, Task.Priority.LOW);

        store.addTask(t1);
        store.addTask(t2);

        store.getTasks().clear(); // Clear list

        assertEquals(0, store.getTasks().size());
    }

    @Test
    void testAddingDuplicateTasksAllowed() {
        Task task = new Task(
                "Repeat",
                "desc",
                LocalDate.now(),
                LocalTime.NOON,
                Task.Category.PERSONAL,
                Task.Priority.MEDIUM
        );

        store.addTask(task);
        store.addTask(task); // same instance added twice

        assertEquals(2, store.getTasks().size());
    }

    @Test
    void testAddNullTaskDoesNotCrash() {
        assertDoesNotThrow(() -> store.addTask(null));
    }

    @Test
    void testStoreStartsEmptyAfterSetup() {
        assertEquals(0, store.getTasks().size(), "Store should be empty after setup()");
    }

    @Test
    void testRemoveTaskThatDoesNotExistDoesNotCrash() {
        Task t1 = new Task("A", "d", LocalDate.now(), LocalTime.NOON, Task.Category.WORK, Task.Priority.LOW);
        Task t2 = new Task("B", "d", LocalDate.now(), LocalTime.NOON, Task.Category.WORK, Task.Priority.LOW);

        store.addTask(t1);

        assertDoesNotThrow(() -> store.removeTask(t2));
        assertEquals(1, store.getTasks().size());
    }
}
