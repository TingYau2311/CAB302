package com.tasktopia;

import com.tasktopia.model.Task;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class TaskValidationTest {

    @Test
    void testNameCannotBeEmpty() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new Task(
                        "",
                        "desc",
                        LocalDate.now(),
                        LocalTime.NOON,
                        Task.Category.WORK,
                        Task.Priority.MEDIUM
                )
        );
        assertEquals("Task name cannot be empty", ex.getMessage());
    }

    @Test
    void testDescriptionCanBeEmptyButNotNull() {
        assertDoesNotThrow(() ->
                new Task(
                        "Valid",
                        "",
                        LocalDate.now(),
                        LocalTime.NOON,
                        Task.Category.WORK,
                        Task.Priority.MEDIUM
                )
        );

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new Task(
                        "Valid",
                        null,
                        LocalDate.now(),
                        LocalTime.NOON,
                        Task.Category.WORK,
                        Task.Priority.MEDIUM
                )
        );
        assertEquals("Description cannot be null", ex.getMessage());
    }

    @Test
    void testDateCannotBeNull() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new Task(
                        "Valid",
                        "desc",
                        null,
                        LocalTime.NOON,
                        Task.Category.WORK,
                        Task.Priority.MEDIUM
                )
        );
        assertEquals("Date cannot be null", ex.getMessage());
    }

    @Test
    void testTimeCannotBeNull() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new Task(
                        "Valid",
                        "desc",
                        LocalDate.now(),
                        null,
                        Task.Category.WORK,
                        Task.Priority.MEDIUM
                )
        );
        assertEquals("Time cannot be null", ex.getMessage());
    }

    @Test
    void testCategoryCannotBeNull() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new Task(
                        "Valid",
                        "desc",
                        LocalDate.now(),
                        LocalTime.NOON,
                        null,
                        Task.Priority.MEDIUM
                )
        );
        assertEquals("Category cannot be null", ex.getMessage());
    }

    @Test
    void testPriorityCannotBeNull() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new Task(
                        "Valid",
                        "desc",
                        LocalDate.now(),
                        LocalTime.NOON,
                        Task.Category.WORK,
                        null
                )
        );
        assertEquals("Priority cannot be null", ex.getMessage());
    }
}
