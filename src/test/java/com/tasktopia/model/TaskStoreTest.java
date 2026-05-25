package com.tasktopia.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskStoreTest {

    @BeforeEach
    void setUp() {
        TaskStore.getInstance().setLoggedInUser(null);
        TaskStore.getInstance().setLoggedInUserId(-1);
    }

    @Test
    void singletonReturnsSameInstance() {
        TaskStore instance1 = TaskStore.getInstance();
        TaskStore instance2 = TaskStore.getInstance();

        assertSame(instance1, instance2);
    }

    @Test
    void setLoggedInUserStoresValue() {
        TaskStore.getInstance().setLoggedInUser("Test User");
        assertEquals("Test User", TaskStore.getInstance().getLoggedInUser());
    }

    @Test
    void setLoggedInUserIdStoresValue() {
        TaskStore.getInstance().setLoggedInUserId(42);
        assertEquals(42, TaskStore.getInstance().getLoggedInUserId());
    }

    @Test
    void initialLoggedInUserIsNull() {
        assertNull(TaskStore.getInstance().getLoggedInUser());
    }

    @Test
    void initialLoggedInUserIdIsNegative() {
        assertTrue(TaskStore.getInstance().getLoggedInUserId() < 0);
    }

    @Test
    void setLoggedInUserToNullWorks() {
        TaskStore.getInstance().setLoggedInUser("Test");
        TaskStore.getInstance().setLoggedInUser(null);
        assertNull(TaskStore.getInstance().getLoggedInUser());
    }

    @Test
    void setLoggedInUserIdToZeroWorks() {
        TaskStore.getInstance().setLoggedInUserId(0);
        assertEquals(0, TaskStore.getInstance().getLoggedInUserId());
    }

    @Test
    void multipleSetLoggedInUserCallsOverwrite() {
        TaskStore.getInstance().setLoggedInUser("First");
        TaskStore.getInstance().setLoggedInUser("Second");
        assertEquals("Second", TaskStore.getInstance().getLoggedInUser());
    }

    @Test
    void multipleSetLoggedInUserIdCallsOverwrite() {
        TaskStore.getInstance().setLoggedInUserId(1);
        TaskStore.getInstance().setLoggedInUserId(2);
        assertEquals(2, TaskStore.getInstance().getLoggedInUserId());
    }

    @Test
    void setLoggedInUserWithEmptyStringWorks() {
        TaskStore.getInstance().setLoggedInUser("");
        assertEquals("", TaskStore.getInstance().getLoggedInUser());
    }
}