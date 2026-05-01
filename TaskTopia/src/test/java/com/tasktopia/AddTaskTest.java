package com.tasktopia;

import com.tasktopia.model.Task;
import com.tasktopia.model.TaskStore;

import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class AddTaskTest {

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



