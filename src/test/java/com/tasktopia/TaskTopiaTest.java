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
        taskDAO    = new MockTaskDAO();
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
        assertEquals("Jane",          saved.getFirstName());
        assertEquals("Doe",           saved.getLastName());
        assertEquals("jane@test.com", saved.getEmail());
        assertEquals("securepass",    saved.getPassword());
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

    // ══════════════════════════════════════════════════════════
    //  FAILING TESTS — UNIMPLEMENTED FEATURES
    //  These tests will fail because the features they test
    //  do not exist yet in the codebase.
    // ══════════════════════════════════════════════════════════

    /**
     * Test 13 — AI parser should extract task name by stripping hashtags.
     * Fails because AiTaskParser class does not exist yet.
     */
//    @Test
//    public void testAiParserExtractsTaskName() {
//        String input = "Call boss next tuesday #work #high";
//        String expected = "Call boss next tuesday";
//
//        // Fails — AiTaskParser does not exist
//        AiTaskParser parser = new AiTaskParser();
//        String result = parser.extractName(input);
//        assertEquals(expected, result);
//    }

    /**
     * Test 14 — AI parser should detect #high priority tag.
     * Fails because AiTaskParser class does not exist yet.
     */
//    @Test
//    public void testAiParserDetectsPriority() {
//        String input = "Buy milk tomorrow #grocery #high";
//
//        // Fails — AiTaskParser does not exist
//        AiTaskParser parser = new AiTaskParser();
//        Task.Priority priority = parser.extractPriority(input);
//        assertEquals(Task.Priority.HIGH, priority);
//    }

    /**
     * Test 15 — AI parser should detect #work category tag.
     * Fails because AiTaskParser class does not exist yet.
     */
    @Test
    public void testAiParserDetectsCategory() {
        String input = "Team meeting at 3pm #work #medium";

        // Fails — AiTaskParser does not exist
        AiTaskParser parser = new AiTaskParser();
        Task.Category category = parser.extractCategory(input);
        assertEquals(Task.Category.WORK, category);
    }

//    /**
//     * Test 16 — A new category is saved with the correct name and colour,
//     * and can be retrieved for the same user after a simulated re-login.
//     */
//    @Test
//    public void testAddCategoryPersistsForUser() {
//        MockCategoryDAO categoryDAO = new MockCategoryDAO();
//        CategoryManager categoryManager = new CategoryManager();
//
//        // User creates a category and it is saved to the DAO
//        CustomCategory travel = new CustomCategory("Travel", "#4D9DE0");
//        categoryManager.addCategory(travel);
//        categoryDAO.saveCategory(1, travel);
//
//        // Simulate logout + login: fresh CategoryManager, reload from DAO
//        CategoryManager reloaded = new CategoryManager();
//        List<CustomCategory> saved = categoryDAO.getCategoriesByUser(1);
//        saved.forEach(reloaded::addCategory);
//
//        assertEquals(1, reloaded.getCategories().size(),
//                "One category should be restored after re-login");
//        assertEquals("Travel", reloaded.getCategories().get(0).getName(),
//                "Category name should be restored correctly");
//        assertEquals("#4D9DE0", reloaded.getCategories().get(0).getColour(),
//                "Category colour should be restored correctly");
//    }

    /**
     * Test 17 — Adding the same category name twice should only store one entry
     */
    @Test
    public void testDuplicateCategoryIsRejected() {
        MockCategoryDAO categoryDAO = new MockCategoryDAO();
        CategoryManager categoryManager = new CategoryManager();

        CustomCategory first  = new CustomCategory("Hobbies", "#FF6B6B");
        CustomCategory second = new CustomCategory("Hobbies", "#6BCB77"); // same name, different colour

        categoryManager.addCategory(first);
        categoryDAO.saveCategory(1, first);

        categoryManager.addCategory(second);
        categoryDAO.saveCategory(1, second);

        // CategoryManager should still have only one entry
        assertEquals(1, categoryManager.getCategories().size(),
                "CategoryManager should reject the duplicate name");

        // DAO should also have only one stored row for this user
        assertEquals(1, categoryDAO.getCategoriesByUser(1).size(),
                "DAO should not persist a duplicate category");
    }

    /**
     * Test 18 — Empty search query should return all tasks.
     * Fails because TaskList.searchTasks("") currently returns
     * an empty list instead of all tasks.
     */
//    @Test
//    public void testSearchEmptyQueryReturnsAllTasks() {
//        TaskList taskList = new TaskList(taskDAO);
//        taskList.addTask(new Task("Buy milk", LocalDateTime.now(),
//                LocalDateTime.now().plusHours(1), "", "grocery", 1));
//        taskList.addTask(new Task("Team meeting", LocalDateTime.now(),
//                LocalDateTime.now().plusHours(1), "", "work", 1));
//
//        // Fails — currently returns empty list for blank query
//        List<Task> results = taskList.searchTasks("");
//        assertEquals(2, results.size(),
//                "Empty search should return all tasks");
//    }

    /**
     * Test 19 — Categories are isolated per user: one user's categories
     * must not appear when loading another user's categories.
     */
//    @Test
//    public void testCategoriesAreIsolatedPerUser() {
//        MockCategoryDAO categoryDAO = new MockCategoryDAO();
//
//        // User 1 creates two categories
//        categoryDAO.saveCategory(1, new CustomCategory("Travel",  "#4D9DE0"));
//        categoryDAO.saveCategory(1, new CustomCategory("Fitness", "#6BCB77"));
//
//        // User 2 creates one category with the same name as one of User 1's
//        categoryDAO.saveCategory(2, new CustomCategory("Travel",  "#FF6B6B"));
//
//        List<CustomCategory> user1Cats = categoryDAO.getCategoriesByUser(1);
//        List<CustomCategory> user2Cats = categoryDAO.getCategoriesByUser(2);
//
//        assertEquals(2, user1Cats.size(),
//                "User 1 should have exactly 2 categories");
//        assertEquals(1, user2Cats.size(),
//                "User 2 should have exactly 1 category");
//        assertEquals("#FF6B6B", user2Cats.get(0).getColour(),
//                "User 2's Travel category should keep its own colour, not User 1's");
//    }

    // ══════════════════════════════════════════════════════════
    //  MOCK TASK DAO (in-memory, no DB needed)
    // ══════════════════════════════════════════════════════════

    static class MockTaskDAO implements ITaskDAO {
        private final java.util.ArrayList<Task> tasks = new java.util.ArrayList<>();
        private int nextId = 1;

        @Override
        public void addTask(Task task) {
            task.setId(nextId++);
            tasks.add(task);
        }

        @Override
        public void updateTask(Task task) {
            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i).getId() == task.getId()) {
                    tasks.set(i, task);
                    return;
                }
            }
        }

        @Override
        public void deleteTask(Task task) {
            tasks.removeIf(t -> t.getId() == task.getId());
        }

        @Override
        public Task getTask(int id) {
            return tasks.stream().filter(t -> t.getId() == id).findFirst().orElse(null);
        }

        @Override
        public List<Task> getAllTasks() {
            return new java.util.ArrayList<>(tasks);
        }

        @Override
        public List<Task> getTasksByUser(int userId) {
            return tasks.stream()
                    .filter(t -> t.getUserId() == userId)
                    .collect(java.util.stream.Collectors.toList());
        }
    }
}