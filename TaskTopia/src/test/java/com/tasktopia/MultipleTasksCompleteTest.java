package com.tasktopia;

import com.tasktopia.model.Task;
import com.tasktopia.model.TaskStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MultipleTasksCompleteTest {

    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setup() {
        TaskStore.resetInstance();
        task1 = new Task("Task One", "desc", LocalDate.now(),
                LocalTime.NOON, Task.Category.WORK, Task.Priority.HIGH);
        task2 = new Task("Task Two", "desc", LocalDate.now(),
                LocalTime.NOON, Task.Category.PERSONAL, Task.Priority.MEDIUM);
        task3 = new Task("Task Three", "desc", LocalDate.now(),
                LocalTime.NOON, Task.Category.GROCERY, Task.Priority.LOW);
        TaskStore.getInstance().addTask(task1);
        TaskStore.getInstance().addTask(task2);
        TaskStore.getInstance().addTask(task3);
    }

    @Test
    @DisplayName("Marking multiple tasks complete sets all to done")
    void markMultipleTasks_allComplete() {
        task1.setDone(true);
        task2.setDone(true);
        task3.setDone(true);
        assertTrue(task1.isDone() && task2.isDone() && task3.isDone());
    }

    @Test
    @DisplayName("Marking some tasks complete does not affect others")
    void markSomeTasks_othersUnaffected() {
        task1.setDone(true);
        assertFalse(task2.isDone());
        assertFalse(task3.isDone());
    }

    @Test
    @DisplayName("Filter returns only completed tasks")
    void filterDone_returnsOnlyCompletedTasks() {
        task1.setDone(true);
        task2.setDone(true);
        List<Task> done = TaskStore.getInstance().filterByDone(true);
        assertEquals(2, done.size());
    }

    @Test
    @DisplayName("Unmarking a completed task sets it back to incomplete")
    void unmarkTask_setsBackToIncomplete() {
        task1.setDone(true);
        task1.setDone(false);
        assertFalse(task1.isDone());
    }
}