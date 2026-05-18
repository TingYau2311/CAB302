package com.tasktopia.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages user-defined custom categories (name + colour).
 * Built-in categories (Work, Grocery, …) are handled separately via Task.Category enum.
 */
public class CategoryManager {

    private final List<CustomCategory> categories = new ArrayList<>();

    /** Add a new custom category. Ignores duplicates (same key). */
    public void addCategory(CustomCategory category) {
        boolean exists = categories.stream()
                .anyMatch(c -> c.getKey().equals(category.getKey()));
        if (!exists) {
            categories.add(category);
        }
    }

    /** Legacy string-only overload kept for backwards compatibility. */
    public void addCategory(String categoryName) {
        addCategory(new CustomCategory(categoryName, "#A3CFF5"));
    }

    public List<CustomCategory> getCategories() {
        return Collections.unmodifiableList(categories);
    }

    /** Returns the names of all custom categories (for combo-boxes, etc.). */
    public List<String> getCategoryNames() {
        List<String> names = new ArrayList<>();
        for (CustomCategory c : categories) names.add(c.getName());
        return names;
    }
}