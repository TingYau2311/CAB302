package com.tasktopia.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * In-memory CategoryDAO for unit tests.
 * Categories are stored per-user and duplicates (same name, same user) are rejected.
 */
public class MockCategoryDAO {

    // userId → list of that user's categories
    private final Map<Integer, List<CustomCategory>> store = new LinkedHashMap<>();

    /**
     * Saves a category for the given user.
     * Silently ignores the save if a category with the same name already exists for that user.
     */
    public void saveCategory(int userId, CustomCategory category) {
        store.putIfAbsent(userId, new ArrayList<>());
        List<CustomCategory> userCats = store.get(userId);

        boolean duplicate = userCats.stream()
                .anyMatch(c -> c.getName().equalsIgnoreCase(category.getName()));

        if (!duplicate) {
            userCats.add(category);
        }
    }

    /** Returns all categories belonging to the given user (never null). */
    public List<CustomCategory> getCategoriesByUser(int userId) {
        return store.getOrDefault(userId, new ArrayList<>());
    }

    /** Removes a category by name for the given user. */
    public void deleteCategory(int userId, String name) {
        List<CustomCategory> userCats = store.get(userId);
        if (userCats != null) {
            userCats.removeIf(c -> c.getName().equalsIgnoreCase(name));
        }
    }
}