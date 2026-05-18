package com.tasktopia.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void priorityLabelFormatsCorrectly() {
        Task t = new Task(
                "Task",
                "Desc",
                LocalDate.now(),
                LocalTime.NOON,
                Task.Category.WORK,
                Task.Priority.HIGH
        );

        assertEquals("High", t.getPriorityLabel());
    }

    @Test
    void categoryLabelFormatsCorrectly() {
        Task t = new Task(
                "Task",
                "Desc",
                LocalDate.now(),
                LocalTime.NOON,
                Task.Category.MEDICAL,
                Task.Priority.LOW
        );

        assertEquals("Medical", t.getCategoryLabel());
    }

    @Test
    void doneToggleWorks() {
        Task t = new Task(
                "Task",
                "Desc",
                LocalDate.now(),
                LocalTime.NOON,
                Task.Category.PERSONAL,
                Task.Priority.MEDIUM
        );

        assertFalse(t.isDone());
        t.setDone(true);
        assertTrue(t.isDone());
    }
}
