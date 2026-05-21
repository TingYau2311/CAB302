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
}