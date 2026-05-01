package com.tasktopia;

import com.tasktopia.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskFieldValidationAfterEditTest {

    private Task task;

    @BeforeEach
    void setup() {
        task = new Task("Original", "desc", LocalDate.now(),
                LocalTime.NOON, Task.Category.WORK, Task.Priority.MEDIUM);
    }

    @Test
    @DisplayName("Setting name to empty string is rejected")
    void setName_emptyString_isRejected() {
        assertThrows(IllegalArgumentException.class, () -> task.setName(""));
    }

    @Test
    @DisplayName("Setting name to whitespace only is rejected")
    void setName_whitespaceOnly_isRejected() {
        assertThrows(IllegalArgumentException.class, () -> task.setName("   "));
    }

    @Test
    @DisplayName("Setting description to null is rejected")
    void setDescription_null_isRejected() {
        assertThrows(IllegalArgumentException.class, () -> task.setDescription(null));
    }

    @Test
    @DisplayName("Setting description to empty string is allowed")
    void setDescription_emptyString_isAllowed() {
        assertDoesNotThrow(() -> task.setDescription(""));
    }

    @Test
    @DisplayName("Setting date to null is rejected")
    void setDate_null_isRejected() {
        assertThrows(IllegalArgumentException.class, () -> task.setDate(null));
    }

    @Test
    @DisplayName("Setting time to null is rejected")
    void setTime_null_isRejected() {
        assertThrows(IllegalArgumentException.class, () -> task.setTime(null));
    }
}