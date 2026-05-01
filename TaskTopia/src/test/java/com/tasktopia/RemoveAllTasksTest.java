package com.tasktopia;

import com.tasktopia.model.Task;
import com.tasktopia.model.TaskStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class RemoveAllTasksTest {

    @BeforeEach
    void setup() {
        TaskStore.resetInstance();
        TaskStore.getInstance().addTask(new Task("Task One", "desc",
                LocalDate.now(), LocalTime.NOON, Task.Category.WORK, Task.Priority.HIGH));
        TaskStore.getInstance().addTask(new Task("Task Two", "desc",
                LocalDate.now(), LocalTime.NOON, Task.Category.PERSONAL, Task.Priority.MEDIUM));
        TaskStore.getInstance().addTask(new Task("Task Three", "desc",
                LocalDate.now(), LocalTime.NOON, Task.Category.GROCERY, Task.Priority.LOW));
    }

    @Test
    @DisplayName("Removing all tasks one by one leaves store empty")
    void removeAllTasks_oneByOne_storeIsEmpty() {
        TaskStore store = TaskStore.getInstance();
        while (!store.getTasks().isEmpty()) {
            store.removeTask(store.getTasks().get(0));
        }
        assertEquals(0, store.getTasks().size());
    }

    @Test
    @DisplayName("Clearing all tasks leaves store empty")
    void clearAllTasks_storeIsEmpty() {
        TaskStore.getInstance().getTasks().clear();
        assertEquals(0, TaskStore.getInstance().getTasks().size());
    }

    @Test
    @DisplayName("Adding a task after clearing works correctly")
    void addTaskAfterClear_storeHasOneTask() {
        TaskStore.getInstance().getTasks().clear();
        TaskStore.getInstance().addTask(new Task("New Task", "desc",
                LocalDate.now(), LocalTime.NOON, Task.Category.WORK, Task.Priority.LOW));
        assertEquals(1, TaskStore.getInstance().getTasks().size());
    }
}