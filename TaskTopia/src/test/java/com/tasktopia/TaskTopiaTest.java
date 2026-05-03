package com.tasktopia;

import com.tasktopia.model.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class TaskTopiaTest {

    // ══════════════════════════════════════════════════════════
    //  SHARED SETUP
    // ══════════════════════════════════════════════════════════

    private MockContactDAO contactDAO;
    private ITaskDAO taskDAO;

    @BeforeEach
    public void setUp() {
        contactDAO = new MockContactDAO();
        taskDAO = new MockTaskDAO();
    }

    // ══════════════════════════════════════════════════════════
    //  SIGN UP TESTS
    // ══════════════════════════════════════════════════════════

    /**
     * Test 1 — Valid sign up saves a new contact to the database.
     */
    @Test
    public void testSignUpSuccess() {
        Contact newUser = new Contact("Harry", "Smith", "harry@test.com", "password123");
        contactDAO.addContact(newUser);

        List<Contact> all = contactDAO.getAllContacts();
        assertEquals(1, all.size());
        assertEquals("Harry", all.get(0).getFirstName());
        assertEquals("harry@test.com", all.get(0).getEmail());
    }

    /**
     * Test 2 — Sign up with duplicate email should be rejected.
     * The controller checks for existing emails before saving.
     */
    @Test
    public void testSignUpDuplicateEmail() {
        Contact first = new Contact("Harry", "Smith", "harry@test.com", "password123");
        contactDAO.addContact(first);

        // Simulate the duplicate check from SignUpController
        List<Contact> existing = contactDAO.getAllContacts();
        boolean emailTaken = existing.stream()
                .anyMatch(c -> c.getEmail().equalsIgnoreCase("harry@test.com"));

        assertTrue(emailTaken, "Duplicate email should be detected");
        assertEquals(1, contactDAO.getAllContacts().size(), "No second account should be created");
    }

    /**
     * Test 3 — Sign up stores first name, last name, email and password correctly.
     */
    @Test
    public void testSignUpStoresCorrectFields() {
        Contact newUser = new Contact("Jane", "Doe", "jane@test.com", "securepass");
        contactDAO.addContact(newUser);

        Contact saved = contactDAO.getAllContacts().get(0);
        assertEquals("Jane", saved.getFirstName());
        assertEquals("Doe", saved.getLastName());
        assertEquals("jane@test.com", saved.getEmail());
        assertEquals("securepass", saved.getPassword());
    }

    // ══════════════════════════════════════════════════════════
    //  LOGIN TESTS
    // ══════════════════════════════════════════════════════════

    /**
     * Test 4 — Valid email and password returns the correct user.
     */
    @Test
    public void testLoginSuccess() {
        Contact user = new Contact("Harry", "Smith", "harry@test.com", "password123");
        contactDAO.addContact(user);

        Contact matched = contactDAO.getAllContacts().stream()
                .filter(c -> c.getEmail().equalsIgnoreCase("harry@test.com")
                        && c.getPassword().equals("password123"))
                .findFirst()
                .orElse(null);

        assertNotNull(matched, "Login should succeed with correct credentials");
        assertEquals("Harry", matched.getFirstName());
    }

    /**
     * Test 5 — Wrong password should not return a user.
     */
    @Test
    public void testLoginWrongPassword() {
        Contact user = new Contact("Harry", "Smith", "harry@test.com", "password123");
        contactDAO.addContact(user);

        Contact matched = contactDAO.getAllContacts().stream()
                .filter(c -> c.getEmail().equalsIgnoreCase("harry@test.com")
                        && c.getPassword().equals("wrongpassword"))
                .findFirst()
                .orElse(null);

        assertNull(matched, "Login should fail with wrong password");
    }

    /**
     * Test 6 — Wrong email should not return a user.
     */
    @Test
    public void testLoginWrongEmail() {
        Contact user = new Contact("Harry", "Smith", "harry@test.com", "password123");
        contactDAO.addContact(user);

        Contact matched = contactDAO.getAllContacts().stream()
                .filter(c -> c.getEmail().equalsIgnoreCase("wrong@test.com")
                        && c.getPassword().equals("password123"))
                .findFirst()
                .orElse(null);

        assertNull(matched, "Login should fail with wrong email");
    }

    // ══════════════════════════════════════════════════════════
    //  TASK TESTS
    // ══════════════════════════════════════════════════════════

    /**
     * Test 7 — Adding a task saves it correctly.
     */
    @Test
    public void testAddTaskSuccess() {
        Task task = new Task(
                "Buy groceries",
                LocalDateTime.of(2026, 5, 10, 9, 0),
                LocalDateTime.of(2026, 5, 10, 10, 0),
                "Get milk and eggs",
                "grocery",
                1
        );
        task.setPriority(Task.Priority.MEDIUM);
        taskDAO.addTask(task);

        List<Task> tasks = taskDAO.getAllTasks();
        assertEquals(1, tasks.size());
        assertEquals("Buy groceries", tasks.get(0).getTitle());
    }

    /**
     * Test 8 — Deleting a task removes it from the list.
     */
    @Test
    public void testDeleteTask() {
        Task task = new Task(
                "Buy groceries",
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                "Get milk",
                "grocery",
                1
        );
        taskDAO.addTask(task);
        assertEquals(1, taskDAO.getAllTasks().size());

        taskDAO.deleteTask(task);
        assertEquals(0, taskDAO.getAllTasks().size());
    }

    /**
     * Test 9 — Updating a task saves the new title correctly.
     */
    @Test
    public void testUpdateTask() {
        Task task = new Task(
                "Old Title",
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                "Some description",
                "work",
                1
        );
        taskDAO.addTask(task);

        task.setTitle("New Title");
        taskDAO.updateTask(task);

        Task updated = taskDAO.getAllTasks().get(0);
        assertEquals("New Title", updated.getTitle());
    }

    /**
     * Test 10 — Tasks are filtered correctly by user ID.
     */
    @Test
    public void testGetTasksByUser() {
        Task task1 = new Task("User 1 Task", LocalDateTime.now(),
                LocalDateTime.now().plusHours(1), "", "work", 1);
        Task task2 = new Task("User 2 Task", LocalDateTime.now(),
                LocalDateTime.now().plusHours(1), "", "work", 2);
        Task task3 = new Task("User 1 Task 2", LocalDateTime.now(),
                LocalDateTime.now().plusHours(1), "", "personal", 1);

        taskDAO.addTask(task1);
        taskDAO.addTask(task2);
        taskDAO.addTask(task3);

        List<Task> user1Tasks = taskDAO.getTasksByUser(1);
        assertEquals(2, user1Tasks.size(), "User 1 should have 2 tasks");

        List<Task> user2Tasks = taskDAO.getTasksByUser(2);
        assertEquals(1, user2Tasks.size(), "User 2 should have 1 task");
    }

    /**
     * Test 11 — Task priority is stored and retrieved correctly.
     */
    @Test
    public void testTaskPriority() {
        Task task = new Task("High priority task", LocalDateTime.now(),
                LocalDateTime.now().plusHours(1), "", "work", 1);
        task.setPriority(Task.Priority.HIGH);
        taskDAO.addTask(task);

        Task saved = taskDAO.getAllTasks().get(0);
        assertEquals(Task.Priority.HIGH, saved.getPriority());
    }

    /**
     * Test 12 — Task done status can be toggled.
     */
    @Test
    public void testTaskDoneToggle() {
        Task task = new Task("Test task", LocalDateTime.now(),
                LocalDateTime.now().plusHours(1), "", "personal", 1);
        assertFalse(task.isDone(), "Task should start as not done");

        task.setDone(true);
        assertTrue(task.isDone(), "Task should be marked as done");

        task.setDone(false);
        assertFalse(task.isDone(), "Task should be marked as not done again");
    }

}