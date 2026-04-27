package com.tasktopia;

import com.tasktopia.model.Task;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {

    @Test
    void testTaskCreationStoresAllFields() {
        LocalDate date = LocalDate.of(2025, 5, 10);
        LocalTime time = LocalTime.of(14, 30);

        Task task = new Task(
                "Study",
                "Work on assignment",
                date,
                time,
                Task.Category.SCHOOL,
                Task.Priority.HIGH
        );

        assertEquals("Study", task.getName());
        assertEquals("Work on assignment", task.getDescription());
        assertEquals(date, task.getDate());
        assertEquals(time, task.getTime());
        assertEquals(Task.Category.SCHOOL, task.getCategory());
        assertEquals(Task.Priority.HIGH, task.getPriority());
        assertFalse(task.isDone());
    }

    @Test
    void testSettersUpdateFields() {
        Task task = new Task(
                "Old Name",
                "Old Desc",
                LocalDate.now(),
                LocalTime.NOON,
                Task.Category.WORK,
                Task.Priority.MEDIUM
        );

        task.setName("New Name");
        task.setDescription("New Desc");
        task.setCategory(Task.Category.PERSONAL);
        task.setPriority(Task.Priority.LOW);
        task.setDone(true);

        assertEquals("New Name", task.getName());
        assertEquals("New Desc", task.getDescription());
        assertEquals(Task.Category.PERSONAL, task.getCategory());
        assertEquals(Task.Priority.LOW, task.getPriority());
        assertTrue(task.isDone());
    }

    @Test
    void testCategoryLabelFormatting() {
        Task task = new Task(
                "Test",
                "Test",
                LocalDate.now(),
                LocalTime.now(),
                Task.Category.MEDICAL,
                Task.Priority.LOW
        );

        assertEquals("Medical", task.getCategoryLabel());
    }

    @Test
    void testPriorityLabelFormatting() {
        Task task = new Task(
                "Test",
                "Test",
                LocalDate.now(),
                LocalTime.now(),
                Task.Category.WORK,
                Task.Priority.HIGH
        );

        assertEquals("High", task.getPriorityLabel());
    }
}
