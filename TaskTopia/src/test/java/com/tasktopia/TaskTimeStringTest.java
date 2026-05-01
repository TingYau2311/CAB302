package com.tasktopia;

import com.tasktopia.model.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTimeStringTest {

    @Test
    @DisplayName("getTimeString returns correct format")
    void getTimeString_returnsCorrectFormat() {
        Task task = new Task("Task", "desc", LocalDate.now(),
                LocalTime.of(9, 30), Task.Category.WORK, Task.Priority.MEDIUM);
        assertEquals("09:30", task.getTimeString());
    }

    @Test
    @DisplayName("getTimeString returns midnight correctly")
    void getTimeString_midnight_returnsCorrectly() {
        Task task = new Task("Task", "desc", LocalDate.now(),
                LocalTime.MIDNIGHT, Task.Category.WORK, Task.Priority.MEDIUM);
        assertEquals("00:00", task.getTimeString());
    }

    @Test
    @DisplayName("getTimeString returns noon correctly")
    void getTimeString_noon_returnsCorrectly() {
        Task task = new Task("Task", "desc", LocalDate.now(),
                LocalTime.NOON, Task.Category.WORK, Task.Priority.MEDIUM);
        assertEquals("12:00", task.getTimeString());
    }

    @Test
    @DisplayName("getTimeString returns correct format for end of day")
    void getTimeString_endOfDay_returnsCorrectly() {
        Task task = new Task("Task", "desc", LocalDate.now(),
                LocalTime.of(23, 59), Task.Category.WORK, Task.Priority.MEDIUM);
        assertEquals("23:59", task.getTimeString());
    }
}