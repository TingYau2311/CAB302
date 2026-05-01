package com.tasktopia;

import com.tasktopia.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MarkCompleteTest {

    private Task task;

    @BeforeEach
    void setupTask() {
        task = new Task("Buy Groceries", "",
                LocalDate.now(), null, Task.Category.GROCERY, Task.Priority.LOW);
    }

    @Test
    @DisplayName("Marking a task as done sets its status to complete")
    void markComplete_setDoneTrue_taskIsComplete() {
        task.setDone(true);

        assertTrue(task.isDone());
    }
}
