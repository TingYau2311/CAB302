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

class TaskStoreFilterAfterEditTest {

    private Task task;

    @BeforeEach
    void setup() {
        TaskStore.resetInstance();
        task = new Task("Work Task", "desc", LocalDate.now(),
                LocalTime.NOON, Task.Category.WORK, Task.Priority.HIGH);
        TaskStore.getInstance().addTask(task);
    }

    @Test
    @DisplayName("Filter reflects updated category after edit")
    void filterByCategory_afterEdit_reflectsChange() {
        task.setCategory(Task.Category.PERSONAL);
        List<Task> workTasks = TaskStore.getInstance().filterByCategory(Task.Category.WORK);
        assertEquals(0, workTasks.size());
        List<Task> personalTasks = TaskStore.getInstance().filterByCategory(Task.Category.PERSONAL);
        assertEquals(1, personalTasks.size());
    }

    @Test
    @DisplayName("Filter reflects updated priority after edit")
    void filterByPriority_afterEdit_reflectsChange() {
        task.setPriority(Task.Priority.LOW);
        List<Task> highTasks = TaskStore.getInstance().filterByPriority(Task.Priority.HIGH);
        assertEquals(0, highTasks.size());
        List<Task> lowTasks = TaskStore.getInstance().filterByPriority(Task.Priority.LOW);
        assertEquals(1, lowTasks.size());
    }

    @Test
    @DisplayName("Filter reflects updated date after edit")
    void filterByDate_afterEdit_reflectsChange() {
        LocalDate newDate = LocalDate.now().plusDays(5);
        task.setDate(newDate);
        List<Task> todayTasks = TaskStore.getInstance().filterByDate(LocalDate.now());
        assertEquals(0, todayTasks.size());
        List<Task> futureTasks = TaskStore.getInstance().filterByDate(newDate);
        assertEquals(1, futureTasks.size());
    }
}