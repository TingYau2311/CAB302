package com.tasktopia;

import com.tasktopia.model.MockTaskDAO;
import com.tasktopia.model.Task;
import com.tasktopia.model.TaskList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskListSearchEdgeCaseTest {

    private TaskList taskList;

    @BeforeEach
    void setup() {
        MockTaskDAO mockDAO = new MockTaskDAO();
        taskList = new TaskList(mockDAO);
        taskList.addTask(new Task("Buy Groceries", "Milk and eggs",
                LocalDate.now(), LocalTime.NOON, Task.Category.GROCERY, Task.Priority.LOW));
        taskList.addTask(new Task("Team Meeting", "Discuss project",
                LocalDate.now(), LocalTime.NOON, Task.Category.WORK, Task.Priority.HIGH));
    }

    @Test
    @DisplayName("Search with empty string returns no results")
    void search_emptyString_returnsNoResults() {
        List<Task> results = taskList.searchTasks("");
        assertEquals(0, results.size());
    }

    @Test
    @DisplayName("Search with whitespace only returns no match")
    void search_whitespaceOnly_returnsNoMatch() {
        List<Task> results = taskList.searchTasks(" ");
        assertEquals(0, results.size());
    }

    @Test
    @DisplayName("Search matching multiple tasks returns all matches")
    void search_matchesMultipleTasks_returnsAll() {
        taskList.addTask(new Task("Buy Milk", "From the store",
                LocalDate.now(), LocalTime.NOON, Task.Category.GROCERY, Task.Priority.MEDIUM));
        List<Task> results = taskList.searchTasks("buy");
        assertEquals(2, results.size());
    }

    @Test
    @DisplayName("Search is case insensitive for description")
    void search_caseInsensitive_description() {
        List<Task> results = taskList.searchTasks("MILK");
        assertEquals(1, results.size());
    }

    @Test
    @DisplayName("Search with special characters returns no match")
    void search_specialCharacters_returnsNoMatch() {
        List<Task> results = taskList.searchTasks("@#$%");
        assertEquals(0, results.size());
    }
}