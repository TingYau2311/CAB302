package com.tasktopia.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles persistence of user-defined categories in the SQLite database.
 */
public class SqliteCategoryDAO {

    private final Connection connection;

    public SqliteCategoryDAO() {
        this.connection = SqliteConnection.getInstance();
    }

    /**
     * Persists a new custom category for the given user.
     * Silently skips if an identical name already exists for that user.
     */
    public void saveCategory(int userId, CustomCategory category) {
        try {
            // Guard against duplicates at the DB level
            PreparedStatement check = connection.prepareStatement(
                    "SELECT COUNT(*) FROM user_categories WHERE userId = ? AND LOWER(name) = LOWER(?)");
            check.setInt(1, userId);
            check.setString(2, category.getName());
            ResultSet rs = check.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) return; // already saved

            PreparedStatement insert = connection.prepareStatement(
                    "INSERT INTO user_categories (userId, name, colour) VALUES (?, ?, ?)");
            insert.setInt(1, userId);
            insert.setString(2, category.getName());
            insert.setString(3, category.getColour());
            insert.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads all custom categories that belong to the given user, in creation order.
     */
    public List<CustomCategory> getCategoriesByUser(int userId) {
        List<CustomCategory> result = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT name, colour FROM user_categories WHERE userId = ? ORDER BY id ASC");
            statement.setInt(1, userId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                result.add(new CustomCategory(rs.getString("name"), rs.getString("colour")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Deletes a specific category for a user (reserved for future use).
     */
    public void deleteCategory(int userId, String categoryName) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM user_categories WHERE userId = ? AND LOWER(name) = LOWER(?)");
            statement.setInt(1, userId);
            statement.setString(2, categoryName);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}