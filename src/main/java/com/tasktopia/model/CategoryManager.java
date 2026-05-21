package com.tasktopia.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages a collection of {@link CustomCategory} objects for the Tasktopia application.
 */

public class CategoryManager {
    /**
     * The internal list storing all registered {@link CustomCategory} instances.
     */
    private final List<CustomCategory> categories = new ArrayList<>();

    /**
     * Adds a new category to the manager if no existing category shares the same name.
     *
     * <p>The duplicate check is case-insensitive, so {@code "Work"} and {@code "work"}
     * are considered identical and the new entry will be silently ignored.</p>
     *
     * @param category the {@link CustomCategory} to add; must not be {@code null}
     */
    public void addCategory(CustomCategory category) {
        boolean duplicate = categories.stream()
                .anyMatch(c -> c.getName().equalsIgnoreCase(category.getName()));
        if (!duplicate) {
            categories.add(category);
        }
    }

    /**
     * Removes the category with the specified name from the manager.
     *
     * <p>The name comparison is case-insensitive. If no matching category is found,
     * the method completes without making any changes.</p>
     *
     * @param name the name of the category to remove; must not be {@code null}
     */
    public void removeCategory(String name) {
        categories.removeIf(c -> c.getName().equalsIgnoreCase(name));
    }

    /**
     * Returns the list of all currently registered categories.
     *
     * <p><strong>Note:</strong> This returns a direct reference to the internal list.
     * Callers should avoid modifying it directly; use {@link #addCategory(CustomCategory)}
     * and {@link #removeCategory(String)} instead.</p>
     *
     * @return a {@link List} of {@link CustomCategory} objects; never {@code null}
     */
    public List<CustomCategory> getCategories() {
        return categories;
    }

    /**
     * Returns a list of the names of all currently registered categories.
     *
     * <p>The order of names corresponds to the order in which categories were added.</p>
     *
     * @return a {@link List} of category name strings; never {@code null}
     */
    public List<String> getCategoryNames() {
        return categories.stream()
                .map(CustomCategory::getName)
                .collect(Collectors.toList());
    }
}