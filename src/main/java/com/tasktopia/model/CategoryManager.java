package com.tasktopia.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryManager {

    private final List<CustomCategory> categories = new ArrayList<>();

    public void addCategory(CustomCategory category) {
        boolean duplicate = categories.stream()
                .anyMatch(c -> c.getName().equalsIgnoreCase(category.getName()));
        if (!duplicate) {
            categories.add(category);
        }
    }

    public void removeCategory(String name) {
        categories.removeIf(c -> c.getName().equalsIgnoreCase(name));
    }

    public List<CustomCategory> getCategories() {
        return categories;
    }

    public List<String> getCategoryNames() {
        return categories.stream()
                .map(CustomCategory::getName)
                .collect(Collectors.toList());
    }
}