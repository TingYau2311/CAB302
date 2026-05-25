package com.tasktopia.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {

    private TaskManager manager;
    private MockTaskDAO mockDAO;

    @BeforeEach
    void setUp() {
        mockDAO = new MockTaskDAO();
        manager = new TaskManager(mockDAO);
    }

    private Task sampleTask(String title, String description) {
        return new Task(
                title,
                description,
                LocalDate.now(),
                LocalTime.NOON,
                Task.Category.WORK,
                Task.Priority.MEDIUM
        );
    }

    @Test
    void addTaskDelegatesToDAO() {
        Task t = sampleTask("Test Task", "Description");
        manager.addTask(t);

        assertEquals(1, mockDAO.getAllTasks().size());
    }

    @Test
    void searchTasksReturnsAllWhenQueryIsEmpty() {
        manager.addTask(sampleTask("Task 1", "Desc 1"));
        manager.addTask(sampleTask("Task 2", "Desc 2"));

        List<Task> results = manager.searchTasks("");
        assertEquals(2, results.size());
    }

    @Test
    void searchTasksReturnsAllWhenQueryIsNull() {
        manager.addTask(sampleTask("Task 1", "Desc 1"));
        manager.addTask(sampleTask("Task 2", "Desc 2"));

        List<Task> results = manager.searchTasks(null);
        assertEquals(2, results.size());
    }

    @Test
    void searchTasksFiltersByTitle() {
        manager.addTask(sampleTask("Buy Milk", "From store"));
        manager.addTask(sampleTask("Call Boss", "About meeting"));

        List<Task> results = manager.searchTasks("milk");
        assertEquals(1, results.size());
        assertEquals("Buy Milk", results.get(0).getTitle());
    }

    @Test
    void searchTasksFiltersByDescription() {
        manager.addTask(sampleTask("Task 1", "Important meeting"));
        manager.addTask(sampleTask("Task 2", "Send email"));

        List<Task> results = manager.searchTasks("meeting");
        assertEquals(1, results.size());
        assertEquals("Task 1", results.get(0).getTitle());
    }

    @Test
    void searchTasksIsCaseInsensitive() {
        manager.addTask(sampleTask("Buy Groceries", "Eggs and bread"));

        List<Task> results = manager.searchTasks("GROCERIES");
        assertEquals(1, results.size());
    }
}