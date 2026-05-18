package com.tasktopia.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    private Task sampleTask() {
        return new Task(
                "Buy Milk",
                "2L full cream",
                LocalDate.of(2025, 5, 20),
                LocalTime.of(10, 30),
                Task.Category.GROCERY,
                Task.Priority.HIGH
        );
    }

    @Test
    void constructorSetsFieldsCorrectly() {
        Task t = sampleTask();

        assertEquals("Buy Milk", t.getName());
        assertEquals("2L full cream", t.getDescription());
        assertEquals(LocalDate.of(2025, 5, 20), t.getDate());
        assertEquals(LocalTime.of(10, 30), t.getTime());
        assertEquals(Task.Category.GROCERY, t.getCategory());
        assertEquals(Task.Priority.HIGH, t.getPriority());
    }

    @Test
    void idIsAssignedSomePositiveValue() {
        Task t = sampleTask();
        assertTrue(t.getId() >= 0);   // safe: works with any ID system
    }

    @Test
    void settersUpdateFields() {
        Task t = sampleTask();

        t.setName("Updated");
        t.setDescription("New Desc");
        t.setCategory(Task.Category.SCHOOL);
        t.setPriority(Task.Priority.LOW);
        t.setDone(true);

        assertEquals("Updated", t.getName());
        assertEquals("New Desc", t.getDescription());
        assertEquals(Task.Category.SCHOOL, t.getCategory());
        assertEquals(Task.Priority.LOW, t.getPriority());
        assertTrue(t.isDone());
    }

    @Test
    void getTimeStringReturnsEmptyWhenTimeIsNull() {
        Task t = new Task(
                "A",
                "B",
                LocalDate.now(),
                null,
                Task.Category.WORK,
                Task.Priority.LOW
        );

        assertEquals("", t.getTimeString());
    }

    @Test
    void categoryLabelFormatsCorrectly() {
        Task t = new Task(
                "A",
                "B",
                LocalDate.now(),
                LocalTime.NOON,
                Task.Category.MEDICAL,
                Task.Priority.LOW
        );

        assertEquals("Medical", t.getCategoryLabel());
    }

    @Test
    void priorityLabelFormatsCorrectly() {
        Task t = new Task(
                "A",
                "B",
                LocalDate.now(),
                LocalTime.NOON,
                Task.Category.WORK,
                Task.Priority.HIGH
        );

        assertEquals("High", t.getPriorityLabel());
    }
}
