package com.tasktopia.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void taskCreationStoresAllFields() {
        LocalDate date = LocalDate.of(2026, 5, 20);
        LocalTime time = LocalTime.of(14, 30);

        Task task = new Task(
                "Study",
                "CAB302 work",
                date,
                time,
                Task.Category.SCHOOL,
                Task.Priority.HIGH
        );

        assertEquals("Study", task.getName());
        assertEquals("CAB302 work", task.getDescription());
        assertEquals(date, task.getDate());
        assertEquals(time, task.getTime());
        assertEquals(Task.Category.SCHOOL, task.getCategory());
        assertEquals(Task.Priority.HIGH, task.getPriority());
        assertFalse(task.isDone());
    }

    @Test
    void settersUpdateFieldsCorrectly() {
        Task task = new Task(
                "Old",
                "Old desc",
                LocalDate.now(),
                LocalTime.NOON,
                Task.Category.WORK,
                Task.Priority.MEDIUM
        );

        task.setName("New");
        task.setDescription("New desc");
        task.setCategory(Task.Category.PERSONAL);
        task.setPriority(Task.Priority.LOW);
        task.setDone(true);

        assertEquals("New", task.getName());
        assertEquals("New desc", task.getDescription());
        assertEquals(Task.Category.PERSONAL, task.getCategory());
        assertEquals(Task.Priority.LOW, task.getPriority());
        assertTrue(task.isDone());
    }

    @Test
    void categoryLabelFormatsCorrectly() {
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
    void priorityLabelFormatsCorrectly() {
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

    @Test
    void timeStringHandlesNull() {
        Task task = new Task(
                "No time",
                "None",
                LocalDate.now(),
                null,
                Task.Category.PERSONAL,
                Task.Priority.MEDIUM
        );

        assertEquals("", task.getTimeString());
    }
}
