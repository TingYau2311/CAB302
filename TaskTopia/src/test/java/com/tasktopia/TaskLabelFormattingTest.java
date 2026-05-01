package com.tasktopia;

import com.tasktopia.model.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskLabelFormattingTest {

    private Task makeTask(Task.Category category, Task.Priority priority) {
        return new Task("Task", "desc", LocalDate.now(),
                LocalTime.NOON, category, priority);
    }

    @Test
    @DisplayName("getCategoryLabel returns title case for WORK")
    void getCategoryLabel_work_returnsTitleCase() {
        assertEquals("Work", makeTask(Task.Category.WORK, Task.Priority.MEDIUM).getCategoryLabel());
    }

    @Test
    @DisplayName("getCategoryLabel returns title case for GROCERY")
    void getCategoryLabel_grocery_returnsTitleCase() {
        assertEquals("Grocery", makeTask(Task.Category.GROCERY, Task.Priority.MEDIUM).getCategoryLabel());
    }

    @Test
    @DisplayName("getCategoryLabel returns title case for PERSONAL")
    void getCategoryLabel_personal_returnsTitleCase() {
        assertEquals("Personal", makeTask(Task.Category.PERSONAL, Task.Priority.MEDIUM).getCategoryLabel());
    }

    @Test
    @DisplayName("getPriorityLabel returns title case for HIGH")
    void getPriorityLabel_high_returnsTitleCase() {
        assertEquals("High", makeTask(Task.Category.WORK, Task.Priority.HIGH).getPriorityLabel());
    }

    @Test
    @DisplayName("getPriorityLabel returns title case for MEDIUM")
    void getPriorityLabel_medium_returnsTitleCase() {
        assertEquals("Medium", makeTask(Task.Category.WORK, Task.Priority.MEDIUM).getPriorityLabel());
    }

    @Test
    @DisplayName("getPriorityLabel returns title case for LOW")
    void getPriorityLabel_low_returnsTitleCase() {
        assertEquals("Low", makeTask(Task.Category.WORK, Task.Priority.LOW).getPriorityLabel());
    }
}