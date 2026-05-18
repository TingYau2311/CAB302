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
}
