package com.tasktopia.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SqliteTaskDAO implements ITaskDAO {
    private Connection connection;

    public SqliteTaskDAO() {
        connection = SqliteConnection.getInstance();
    }


    @Override
    public void addTask(Task task) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO tasks (title, startDate, endDate, description, tags, priority, userId) VALUES (?, ?, ?, ?, ?, ?, ?)");
            statement.setString(1, task.getTitle());
            statement.setString(2, task.getStartDate().toString());
            statement.setString(3, task.getEndDate().toString());
            statement.setString(4, task.getDescription());
            statement.setString(5, task.getTags());
            statement.setString(6, task.getPriority() != null ? task.getPriority().name() : "MEDIUM");
            statement.setInt(7, task.getUserId());
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                task.setId(generatedKeys.getInt(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateTask(Task task) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE tasks SET title = ?, startDate = ?, endDate = ?, description = ?, tags = ?, priority = ?, userId = ? WHERE id = ?");
            statement.setString(1, task.getTitle());
            statement.setString(2, task.getStartDate().toString());
            statement.setString(3, task.getEndDate().toString());
            statement.setString(4, task.getDescription());
            statement.setString(5, task.getTags());
            statement.setString(6, task.getPriority() != null ? task.getPriority().name() : "MEDIUM");
            statement.setInt(7, task.getUserId());
            statement.setInt(8, task.getId());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteTask(Task task) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM tasks WHERE id = ?");
            statement.setInt(1, task.getId());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Task getTask(int id) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM tasks WHERE id = ?");
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String Title = resultSet.getString("Title");
                LocalDateTime startDate = parseDateTime(resultSet.getString("startDate"));
                LocalDateTime endDate = parseDateTime(resultSet.getString("endDate"));
                String Description = resultSet.getString("Description");
                String Tags = resultSet.getString("Tags");
                Task.Priority priority = parsePriority(resultSet.getString("priority"));
                int UserID = resultSet.getInt("UserId");

                Task task = new Task(Title, startDate, endDate, Description, Tags, UserID);
                task.setId(id);
                task.setPriority(priority);
                return task;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    // Add this helper method to SqliteTaskDAO
    private LocalDateTime parseDateTime(String value) {
        if (value == null) return LocalDateTime.now();
        // Handle old date-only format
        if (value.length() == 10) {
            return LocalDate.parse(value).atStartOfDay();
        }
        // Handle new datetime format
        return LocalDateTime.parse(value);
    }

    @Override
    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM tasks";
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String Title = resultSet.getString("Title");
                LocalDateTime startDate = parseDateTime(resultSet.getString("startDate"));
                LocalDateTime endDate = parseDateTime(resultSet.getString("endDate"));
                String Description = resultSet.getString("Description");
                String Tags = resultSet.getString("Tags");
                Task.Priority priority = parsePriority(resultSet.getString("priority"));
                int UserID = resultSet.getInt("UserId");
                Task task = new Task(Title, startDate, endDate, Description, Tags, UserID);
                task.setId(id);
                task.setPriority(priority);
                tasks.add(task);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tasks;
    }
    @Override
    public List<Task> getTasksByUser(int userId) {
        List<Task> tasks = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM tasks WHERE userId = ?");
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                LocalDateTime startDate = parseDateTime(resultSet.getString("startDate"));
                LocalDateTime endDate = parseDateTime(resultSet.getString("endDate"));
                String description = resultSet.getString("description");
                String tags = resultSet.getString("tags");
                Task.Priority priority = parsePriority(resultSet.getString("priority"));
                int taskUserId = resultSet.getInt("userId");
                Task task = new Task(title, startDate, endDate, description, tags, taskUserId);
                task.setId(id);
                task.setPriority(priority);
                tasks.add(task);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tasks;
    }
    private Task.Priority parsePriority(String value) {
        if (value == null) return Task.Priority.MEDIUM;
        try {
            return Task.Priority.valueOf(value.toUpperCase());
        } catch (Exception e) {
            return Task.Priority.MEDIUM;
        }
    }
}