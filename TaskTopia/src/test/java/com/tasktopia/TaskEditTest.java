package com.tasktopia;

import com.tasktopia.model.Task;
import com.tasktopia.model.TaskStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskEditTest {

    private Task task;

    @BeforeEach
    void setup() {
        TaskStore.resetInstance();
        task = new Task("Original Name", "Original Description",
                LocalDate.now(), LocalTime.NOON, Task.Category.WORK, Task.Priority.MEDIUM);
        TaskStore.getInstance().addTask(task);
    }

    @Test
    @DisplayName("Editing task name updates correctly")
    void editTask_name_updatesCorrectly() {
        task.setName("Updated Name");
        assertEquals("Updated Name", task.getName());
    }

    @Test
    @DisplayName("Editing task description updates correctly")
    void editTask_description_updatesCorrectly() {
        task.setDescription("Updated Description");
        assertEquals("Updated Description", task.getDescription());
    }

    @Test
    @DisplayName("Editing task priority updates correctly")
    void editTask_priority_updatesCorrectly() {
        task.setPriority(Task.Priority.HIGH);
        assertEquals(Task.Priority.HIGH, task.getPriority());
    }

    @Test
    @DisplayName("Editing task category updates correctly")
    void editTask_category_updatesCorrectly() {
        task.setCategory(Task.Category.PERSONAL);
        assertEquals(Task.Category.PERSONAL, task.getCategory());
    }

    @Test
    @DisplayName("Editing task date updates correctly")
    void editTask_date_updatesCorrectly() {
        LocalDate newDate = LocalDate.now().plusDays(3);
        task.setDate(newDate);
        assertEquals(newDate, task.getDate());
    }

    @Test
    @DisplayName("Editing task time updates correctly")
    void editTask_time_updatesCorrectly() {
        task.setTime(LocalTime.of(9, 30));
        assertEquals(LocalTime.of(9, 30), task.getTime());
    }
}