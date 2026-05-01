package com.tasktopia;

import com.tasktopia.model.Task;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class TaskCompletionStateTest {

    private Task makeTask() {
        return new Task(
                "Test Task",
                "desc",
                LocalDate.now(),
                LocalTime.NOON,
                Task.Category.WORK,
                Task.Priority.MEDIUM
        );
    }

    @Test
    void testTaskStartsNotDone() {
        Task task = makeTask();
        assertFalse(task.isDone(), "New tasks should start as not done");
    }

    @Test
    void testMarkTaskAsDone() {
        Task task = makeTask();
        task.setDone(true);
        assertTrue(task.isDone(), "Task should be marked as done");
    }

    @Test
    void testMarkTaskAsNotDone() {
        Task task = makeTask();
        task.setDone(true);   // mark done first
        task.setDone(false);  // then undo
        assertFalse(task.isDone(), "Task should be marked as not done");
    }
}
