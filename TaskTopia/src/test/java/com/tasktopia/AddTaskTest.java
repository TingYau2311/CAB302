package com.tasktopia;

import com.tasktopia.model.Task;
import com.tasktopia.model.TaskStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AddTaskTest {

    @BeforeEach
    void clearStore() {
        TaskStore.getInstance().getTasks().clear();
    }

    @Test
    @DisplayName("Adding a task stores it in the task list")
    void addTask_taskIsStoredInList() {
        Task t = new Task("Team Meeting", "Discuss sprint goals",
                LocalDate.now(), LocalTime.of(9, 0),
                Task.Category.WORK, Task.Priority.HIGH);

        TaskStore.getInstance().addTask(t);

        assertTrue(TaskStore.getInstance().getTasks().contains(t));
    }
}
