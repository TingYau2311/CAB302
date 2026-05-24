package com.tasktopia.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskCategoryTest {

    @Test
    void categoryWorkExists() {
        Task.Category cat = Task.Category.WORK;
        assertNotNull(cat);
        assertEquals("WORK", cat.name());
    }

    @Test
    void categorySchoolExists() {
        Task.Category cat = Task.Category.SCHOOL;
        assertNotNull(cat);
        assertEquals("SCHOOL", cat.name());
    }

    @Test
    void categoryGroceryExists() {
        Task.Category cat = Task.Category.GROCERY;
        assertNotNull(cat);
        assertEquals("GROCERY", cat.name());
    }

    @Test
    void categoryMedicalExists() {
        Task.Category cat = Task.Category.MEDICAL;
        assertNotNull(cat);
        assertEquals("MEDICAL", cat.name());
    }

    @Test
    void allCategoriesCanBeRetrieved() {
        Task.Category[] categories = Task.Category.values();
        assertTrue(categories.length > 0);
    }
}