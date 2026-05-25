package com.tasktopia.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CategoryManagerTest {

    private CategoryManager manager;

    @BeforeEach
    void setUp() {
        manager = new CategoryManager();
    }

    @Test
    void addCategoryStoresNewCategory() {
        CustomCategory cat = new CustomCategory("TestCat", "#FF0000");
        manager.addCategory(cat);

        List<CustomCategory> all = manager.getCategories();
        assertEquals(1, all.size());
        assertEquals("TestCat", all.get(0).getName());
    }

    @Test
    void addCategoryIgnoresDuplicateNames() {
        CustomCategory cat1 = new CustomCategory("Work", "#FF0000");
        CustomCategory cat2 = new CustomCategory("work", "#00FF00"); // Same name, different case

        manager.addCategory(cat1);
        manager.addCategory(cat2);

        assertEquals(1, manager.getCategories().size());
    }

    @Test
    void removeCategoryDeletesCorrectItem() {
        CustomCategory cat1 = new CustomCategory("Cat1", "#FF0000");
        CustomCategory cat2 = new CustomCategory("Cat2", "#00FF00");

        manager.addCategory(cat1);
        manager.addCategory(cat2);
        manager.removeCategory("Cat1");

        assertEquals(1, manager.getCategories().size());
        assertEquals("Cat2", manager.getCategories().get(0).getName());
    }

    @Test
    void removeCategoryIsCaseInsensitive() {
        CustomCategory cat = new CustomCategory("Work", "#FF0000");
        manager.addCategory(cat);
        manager.removeCategory("work"); // lowercase

        assertEquals(0, manager.getCategories().size());
    }

    @Test
    void getCategoryNamesReturnsAllNames() {
        manager.addCategory(new CustomCategory("Work", "#FF0000"));
        manager.addCategory(new CustomCategory("Home", "#00FF00"));

        List<String> names = manager.getCategoryNames();
        assertEquals(2, names.size());
        assertTrue(names.contains("Work"));
        assertTrue(names.contains("Home"));
    }

    @Test
    void getCategoriesReturnsEmptyListInitially() {
        assertEquals(0, manager.getCategories().size());
    }
}