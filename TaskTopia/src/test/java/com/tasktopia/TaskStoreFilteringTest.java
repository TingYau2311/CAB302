package com.tasktopia;

import com.tasktopia.model.Task;
import com.tasktopia.model.TaskStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskStoreFilteringTest {

    private TaskStore store;

    @BeforeEach
    void setup() {
        store = TaskStore.getInstance();
        store.getTasks().clear();
    }

    private Task makeTask(String name, Task.Category category, Task.Priority priority, boolean done) {
        Task t = new Task(
                name,
                "desc",
                LocalDate.now(),
                LocalTime.NOON,
                category,
                priority
        );
        t.setDone(done);
        return t;
    }

    @Test
    void testFilterByCategory() {
        Task t1 = makeTask("Buy Milk", Task.Category.GROCERY, Task.Priority.MEDIUM, false);
        Task t2 = makeTask("Finish Report", Task.Category.WORK, Task.Priority.HIGH, false);
        Task t3 = makeTask("Apples", Task.Category.GROCERY, Task.Priority.LOW, false);

        store.addTask(t1);
        store.addTask(t2);
        store.addTask(t3);

        List<Task> groceries = store.filterByCategory(Task.Category.GROCERY);

        assertEquals(2, groceries.size());
        assertTrue(groceries.contains(t1));
        assertTrue(groceries.contains(t3));
    }

    @Test
    void testFilterByPriority() {
        Task t1 = makeTask("Low Task", Task.Category.PERSONAL, Task.Priority.LOW, false);
        Task t2 = makeTask("High Task", Task.Category.WORK, Task.Priority.HIGH, false);
        Task t3 = makeTask("Another High", Task.Category.SCHOOL, Task.Priority.HIGH, false);

        store.addTask(t1);
        store.addTask(t2);
        store.addTask(t3);

        List<Task> highPriority = store.filterByPriority(Task.Priority.HIGH);

        assertEquals(2, highPriority.size());
        assertTrue(highPriority.contains(t2));
        assertTrue(highPriority.contains(t3));
    }

    @Test
    void testFilterByCompletionStatus() {
        Task t1 = makeTask("Done Task", Task.Category.WORK, Task.Priority.MEDIUM, true);
        Task t2 = makeTask("Not Done", Task.Category.WORK, Task.Priority.MEDIUM, false);

        store.addTask(t1);
        store.addTask(t2);

        List<Task> doneTasks = store.filterByDone(true);
        List<Task> notDoneTasks = store.filterByDone(false);

        assertEquals(1, doneTasks.size());
        assertEquals(1, notDoneTasks.size());
        assertTrue(doneTasks.contains(t1));
        assertTrue(notDoneTasks.contains(t2));
    }

    @Test
    void testFilterByDate() {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        Task t1 = new Task("Today Task", "desc", today, LocalTime.NOON, Task.Category.WORK, Task.Priority.MEDIUM);
        Task t2 = new Task("Tomorrow Task", "desc", tomorrow, LocalTime.NOON, Task.Category.WORK, Task.Priority.MEDIUM);

        store.addTask(t1);
        store.addTask(t2);

        List<Task> todayTasks = store.filterByDate(today);

        assertEquals(1, todayTasks.size());
        assertTrue(todayTasks.contains(t1));
    }
}
