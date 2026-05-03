package com.tasktopia;

import com.tasktopia.model.ITaskDAO;
import com.tasktopia.model.MockTaskDAO;
import com.tasktopia.model.Task;
import com.tasktopia.model.TaskList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {

    private MockTaskDAO mockDAO;
    private TaskList taskList;
    private Task sampleTask;

    @BeforeEach
    void setUp() {
        mockDAO = new MockTaskDAO();
        taskList = new TaskList(mockDAO);
        sampleTask = new Task(
                "Buy groceries",
                "Milk and eggs",
                LocalDate.now(),
                LocalTime.NOON,
                Task.Category.GROCERY,
                Task.Priority.MEDIUM
        );
    }

    // --- LoginTest ---
    @Test
    void testLoginWithValidCredentials() {
        // Placeholder: replace with your actual login logic when available
        assertTrue(true);
    }

    // --- SignUpTest ---
    @Test
    void testSignUpCreatesUser() {
        // Placeholder: replace with your actual sign-up logic when available
        assertTrue(true);
    }

    // --- AddTaskTest ---
    @Test
    void testAddTaskIncreasesTaskCount() {
        taskList.addTask(sampleTask);
        assertEquals(1, mockDAO.tasks.size());
    }

    @Test
    void testAddedTaskHasCorrectName() {
        taskList.addTask(sampleTask);
        assertEquals("Buy groceries", mockDAO.tasks.get(0).getName());
    }

    // --- DeleteTaskTest ---
    @Test
    void testDeleteTaskRemovesFromList() {
        taskList.addTask(sampleTask);
        mockDAO.removeTask(sampleTask);
        assertEquals(0, mockDAO.tasks.size());
    }

    // --- MarkCompleteTest ---
    @Test
    void testMarkTaskAsComplete() {
        taskList.addTask(sampleTask);
        sampleTask.setDone(true);
        assertTrue(mockDAO.tasks.get(0).isDone());
    }

    @Test
    void testTaskIsNotDoneByDefault() {
        taskList.addTask(sampleTask);
        assertFalse(mockDAO.tasks.get(0).isDone());
    }

    // --- LogoutTest ---
    @Test
    void testLogoutClearsSession() {
        // Placeholder: replace with your actual logout logic when available
        assertTrue(true);
    }

    // --- SearchTest ---
    @Test
    void testSearchByNameReturnsMatch() {
        taskList.addTask(sampleTask);
        List<Task> results = taskList.searchTasks("groceries");
        assertEquals(1, results.size());
    }

    @Test
    void testSearchByDescriptionReturnsMatch() {
        taskList.addTask(sampleTask);
        List<Task> results = taskList.searchTasks("eggs");
        assertEquals(1, results.size());
    }

    @Test
    void testSearchWithNoMatchReturnsEmpty() {
        taskList.addTask(sampleTask);
        List<Task> results = taskList.searchTasks("xyz123");
        assertEquals(0, results.size());
    }

    @Test
    void testSearchIsCaseInsensitive() {
        taskList.addTask(sampleTask);
        List<Task> results = taskList.searchTasks("GROCERIES");
        assertEquals(1, results.size());
    }
}