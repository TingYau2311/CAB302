package com.tasktopia.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskPriorityTest {

    @Test
    void priorityHighExists() {
        Task.Priority priority = Task.Priority.HIGH;
        assertNotNull(priority);
        assertEquals("HIGH", priority.name());
    }

    @Test
    void priorityMediumExists() {
        Task.Priority priority = Task.Priority.MEDIUM;
        assertNotNull(priority);
        assertEquals("MEDIUM", priority.name());
    }

    @Test
    void priorityLowExists() {
        Task.Priority priority = Task.Priority.LOW;
        assertNotNull(priority);
        assertEquals("LOW", priority.name());
    }

    @Test
    void allPrioritiesCanBeRetrieved() {
        Task.Priority[] priorities = Task.Priority.values();
        assertTrue(priorities.length > 0);
    }

    @Test
    void priorityValuesAreDifferent() {
        assertNotEquals(Task.Priority.HIGH, Task.Priority.LOW);
        assertNotEquals(Task.Priority.MEDIUM, Task.Priority.LOW);
        assertNotEquals(Task.Priority.HIGH, Task.Priority.MEDIUM);
    }
}