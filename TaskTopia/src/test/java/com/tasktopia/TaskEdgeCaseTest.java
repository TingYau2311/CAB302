package com.tasktopia;

import com.tasktopia.model.Task;
import com.tasktopia.model.TaskStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskEdgeCaseTest {

    @BeforeEach
    void setup() {
        TaskStore.resetInstance();
    }

    @Test
    @DisplayName("Task name with only spaces is rejected")
    void taskName_onlySpaces_isRejected() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new Task("   ", "desc", LocalDate.now(), LocalTime.NOON,
                        Task.Category.WORK, Task.Priority.MEDIUM));
        assertEquals("Task name cannot be empty", ex.getMessage());
    }

    @Test
    @DisplayName("Task with very long name is accepted")
    void taskName_veryLongName_isAccepted() {
        String longName = "A".repeat(1000);
        assertDoesNotThrow(() ->
                new Task(longName, "desc", LocalDate.now(), LocalTime.NOON,
                        Task.Category.WORK, Task.Priority.MEDIUM));
    }

    @Test
    @DisplayName("Task with special characters in name is accepted")
    void taskName_specialCharacters_isAccepted() {
        assertDoesNotThrow(() ->
                new Task("Task @#$%!", "desc", LocalDate.now(), LocalTime.NOON,
                        Task.Category.WORK, Task.Priority.MEDIUM));
    }

    @Test
    @DisplayName("Adding duplicate tasks both appear in store")
    void addDuplicateTasks_bothAppearInStore() {
        int sizeBefore = TaskStore.getInstance().getTasks().size();
        Task t1 = new Task("Same Name", "desc", LocalDate.now(), LocalTime.NOON,
                Task.Category.WORK, Task.Priority.MEDIUM);
        Task t2 = new Task("Same Name", "desc", LocalDate.now(), LocalTime.NOON,
                Task.Category.WORK, Task.Priority.MEDIUM);
        TaskStore.getInstance().addTask(t1);
        TaskStore.getInstance().addTask(t2);
        assertEquals(sizeBefore + 2, TaskStore.getInstance().getTasks().size());
    }

    @Test
    @DisplayName("Task with past date is accepted")
    void task_pastDate_isAccepted() {
        assertDoesNotThrow(() ->
                new Task("Old Task", "desc", LocalDate.of(2000, 1, 1),
                        LocalTime.NOON, Task.Category.WORK, Task.Priority.LOW));
    }

    @Test
    @DisplayName("Task with future date is accepted")
    void task_futureDate_isAccepted() {
        assertDoesNotThrow(() ->
                new Task("Future Task", "desc", LocalDate.now().plusYears(10),
                        LocalTime.NOON, Task.Category.WORK, Task.Priority.LOW));
    }
}