package com.tasktopia.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskListTest {

    private TaskList taskList;
    private MockTaskDAO mockDAO;

    @BeforeEach
    void setUp() {
        mockDAO = new MockTaskDAO();
        taskList = new TaskList(mockDAO);
    }

    private Task sampleTask(String title) {
        return new Task(
                title,
                "Desc",
                LocalDate.now(),
                LocalTime.NOON,
                Task.Category.WORK,
                Task.Priority.MEDIUM
        );
    }

    @Test
    void addTaskDelegatesToDAO() {
        Task t = sampleTask("Test Task");
        taskList.addTask(t);

        assertEquals(1, mockDAO.getAllTasks().size());
    }

    @Test
    void searchTasksReturnsAllWhenQueryIsEmpty() {
        taskList.addTask(sampleTask("Task 1"));
        taskList.addTask(sampleTask("Task 2"));

        List<Task> results = taskList.searchTasks("");
        assertEquals(2, results.size());
    }

    @Test
    void searchTasksReturnsAllWhenQueryIsNull() {
        taskList.addTask(sampleTask("Task 1"));
        taskList.addTask(sampleTask("Task 2"));

        List<Task> results = taskList.searchTasks(null);
        assertEquals(2, results.size());
    }

    @Test
    void searchTasksFiltersByTitle() {
        taskList.addTask(sampleTask("Buy Milk"));
        taskList.addTask(sampleTask("Call Boss"));

        List<Task> results = taskList.searchTasks("milk");
        assertEquals(1, results.size());
        assertEquals("Buy Milk", results.get(0).getTitle());
    }

    @Test
    void searchTasksIsCaseInsensitive() {
        taskList.addTask(sampleTask("Important Meeting"));

        List<Task> results = taskList.searchTasks("MEETING");
        assertEquals(1, results.size());
    }

    @Test
    void searchTasksReturnsEmptyWhenNoMatch() {
        taskList.addTask(sampleTask("Task 1"));

        List<Task> results = taskList.searchTasks("nonexistent");
        assertEquals(0, results.size());
    }
}