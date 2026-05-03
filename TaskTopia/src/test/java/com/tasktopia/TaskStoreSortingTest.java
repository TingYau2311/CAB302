package com.tasktopia;

import com.tasktopia.model.Task;
import com.tasktopia.model.TaskStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskStoreSortingTest {

    private TaskStore store;

    @BeforeEach
    void setup() {
        store = TaskStore.getInstance();
        store.getTasks().clear();
    }

    private Task makeTask(String name, LocalDate date, Task.Priority priority) {
        return new Task(
                name,
                "desc",
                date,
                LocalTime.NOON,
                Task.Category.WORK,
                priority
        );
    }

    @Test
    void testSortByDate() {
        Task t1 = makeTask("Task1", LocalDate.of(2025, 5, 10), Task.Priority.MEDIUM);
        Task t2 = makeTask("Task2", LocalDate.of(2025, 5, 5), Task.Priority.HIGH);
        Task t3 = makeTask("Task3", LocalDate.of(2025, 5, 20), Task.Priority.LOW);

        store.addTask(t1);
        store.addTask(t2);
        store.addTask(t3);

        List<Task> sorted = store.sortByDate();

        assertEquals(t2, sorted.get(0)); // earliest
        assertEquals(t1, sorted.get(1));
        assertEquals(t3, sorted.get(2)); // latest
    }

    @Test
    void testSortByPriority() {
        Task low = makeTask("Low", LocalDate.now(), Task.Priority.LOW);
        Task high = makeTask("High", LocalDate.now(), Task.Priority.HIGH);
        Task med = makeTask("Medium", LocalDate.now(), Task.Priority.MEDIUM);

        store.addTask(low);
        store.addTask(high);
        store.addTask(med);

        List<Task> sorted = store.sortByPriority();

        assertEquals(high, sorted.get(0)); // HIGH first
        assertEquals(med, sorted.get(1));
        assertEquals(low, sorted.get(2)); // LOW last
    }

    @Test
    void testSortAlphabetically() {
        Task b = makeTask("Bravo", LocalDate.now(), Task.Priority.MEDIUM);
        Task a = makeTask("Alpha", LocalDate.now(), Task.Priority.MEDIUM);
        Task c = makeTask("Charlie", LocalDate.now(), Task.Priority.MEDIUM);

        store.addTask(b);
        store.addTask(a);
        store.addTask(c);

        List<Task> sorted = store.sortByName();

        assertEquals(a, sorted.get(0));
        assertEquals(b, sorted.get(1));
        assertEquals(c, sorted.get(2));
    }
}
