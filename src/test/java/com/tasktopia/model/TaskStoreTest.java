package com.tasktopia.model;

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
        store.getTasks().clear(); // reset list
        store.setLoggedInUser("");
    }

    private Task sample(String name) {
        return new Task(
                name,
                "Desc",
                LocalDate.now(),
                LocalTime.NOON,
                Task.Category.WORK,
                Task.Priority.MEDIUM
        );
    }

    @Test
    void addTaskStoresTask() {
        Task t = sample("A");
        store.addTask(t);

        assertEquals(1, store.getTasks().size());
        assertEquals("A", store.getTasks().get(0).getName());
    }

    @Test
    void removeTaskRemovesCorrectItem() {
        Task a = sample("A");
        Task b = sample("B");

        store.addTask(a);
        store.addTask(b);

        store.removeTask(a);

        assertEquals(1, store.getTasks().size());
        assertEquals("B", store.getTasks().get(0).getName());
    }

    @Test
    void removeTaskOnMissingDoesNothing() {
        Task a = sample("A");

        store.removeTask(a);

        assertEquals(0, store.getTasks().size());
    }

    @Test
    void loggedInUserStoresCorrectly() {
        store.setLoggedInUser("angela@example.com");
        assertEquals("angela@example.com", store.getLoggedInUser());
    }

    @Test
    void getInstanceReturnsSameSingleton() {
        TaskStore s1 = TaskStore.getInstance();
        TaskStore s2 = TaskStore.getInstance();

        assertSame(s1, s2);
    }
}

