package com.tasktopia;

import com.tasktopia.model.Task;
import com.tasktopia.model.TaskStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertFalse;

class DeleteTaskTest {

    private Task task;

    @BeforeEach
    void setupStore() {
        TaskStore.resetInstance();
        task = new Task("Submit Report", "Final report",
                LocalDate.now(), LocalTime.of(9, 0), Task.Category.WORK, Task.Priority.HIGH);
        TaskStore.getInstance().addTask(task);
    }

    @Test
    @DisplayName("Deleting a task removes it from the store")
    void deleteTask_removesTaskFromStore() {
        TaskStore.getInstance().removeTask(task);

        assertFalse(TaskStore.getInstance().getTasks().contains(task));
    }
}