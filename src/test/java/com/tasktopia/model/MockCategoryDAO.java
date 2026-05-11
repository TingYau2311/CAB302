package com.tasktopia.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class MockCategoryDAO {

    private final List<int[]> rows = new ArrayList<>();
    private final List<CustomCategory> cats = new ArrayList<>();

    public void saveCategory(int userId, CustomCategory category) {
        // Duplicate guard — same logic as SqliteCategoryDAO
        boolean exists = cats.stream()
                .anyMatch(c -> c.getKey().equals(category.getKey()));
        if (exists) return;
        cats.add(category);
        rows.add(new int[]{userId, cats.size() - 1});
    }

    public List<CustomCategory> getCategoriesByUser(int userId) {
        return rows.stream()
                .filter(r -> r[0] == userId)
                .map(r -> cats.get(r[1]))
                .collect(Collectors.toList());
    }
}