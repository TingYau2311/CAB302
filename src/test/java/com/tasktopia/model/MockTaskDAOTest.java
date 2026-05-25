package com.tasktopia.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MockTaskDAOTest {

    private MockTaskDAO dao;

    @BeforeEach
    void setUp() {
        dao = new MockTaskDAO();
    }

    private Task sample(String name, int userId) {
        Task t = new Task(
                name,
                "Desc",
                LocalDate.now(),
                LocalTime.NOON,
                Task.Category.WORK,
                Task.Priority.MEDIUM
        );
        t.setUserId(userId);
        return t;
    }

    @Test
    void addTaskAssignsIdAndStores() {
        Task t = sample("Task 1", 1);
        dao.addTask(t);

        List<Task> all = dao.getAllTasks();
        assertEquals(1, all.size());
        assertEquals(1, all.get(0).getId());
    }

    @Test
    void updateTaskReplacesExisting() {
        Task t = sample("Original", 1);
        dao.addTask(t);

        t.setName("Updated");
        dao.updateTask(t);

        Task fetched = dao.getTask(t.getId());
        assertEquals("Updated", fetched.getName());
    }

    @Test
    void deleteTaskRemovesCorrectItem() {
        Task t1 = sample("A", 1);
        Task t2 = sample("B", 1);

        dao.addTask(t1);
        dao.addTask(t2);

        dao.deleteTask(t1);

        assertEquals(1, dao.getAllTasks().size());
        assertEquals("B", dao.getAllTasks().get(0).getName());
    }

    @Test
    void getTasksByUserFiltersCorrectly() {
        dao.addTask(sample("A", 1));
        dao.addTask(sample("B", 2));
        dao.addTask(sample("C", 1));

        List<Task> user1 = dao.getTasksByUser(1);

        assertEquals(2, user1.size());
        assertTrue(user1.stream().allMatch(t -> t.getUserId() == 1));
    }

    @Test
    void getTaskReturnsNullWhenNotFound() {
        Task found = dao.getTask(999);
        assertNull(found);
    }

    @Test
    void getTasksByUserReturnsEmptyWhenNoTasks() {
        List<Task> tasks = dao.getTasksByUser(1);
        assertEquals(0, tasks.size());
    }

    @Test
    void deleteNonExistentTaskDoesNothing() {
        dao.addTask(sample("Task", 1));
        Task fake = sample("Fake", 1);
        fake.setId(999);

        dao.deleteTask(fake);
        assertEquals(1, dao.getAllTasks().size());
    }

    @Test
    void getAllTasksReturnsEmptyInitially() {
        assertEquals(0, dao.getAllTasks().size());
    }

    @Test
    void addTaskWithSameNameCreatesMultipleTasks() {
        dao.addTask(sample("Same Name", 1));
        dao.addTask(sample("Same Name", 1));

        assertEquals(2, dao.getAllTasks().size());
    }

    @Test
    void updateTaskWithNonExistentIdDoesNothing() {
        Task t = sample("Original", 1);
        t.setId(999);

        dao.updateTask(t);
        assertEquals(0, dao.getAllTasks().size());
    }

    @Test
    void getTasksByUserWithMultipleUsersFiltersCorrectly() {
        dao.addTask(sample("User1-Task1", 1));
        dao.addTask(sample("User2-Task1", 2));
        dao.addTask(sample("User1-Task2", 1));
        dao.addTask(sample("User3-Task1", 3));

        List<Task> user1Tasks = dao.getTasksByUser(1);
        List<Task> user2Tasks = dao.getTasksByUser(2);

        assertEquals(2, user1Tasks.size());
        assertEquals(1, user2Tasks.size());
    }
}