package com.tasktopia.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * SQLite-backed implementation of {@link ITaskDAO}.
 * <p>
 * Performs CRUD operations on the {@code tasks} table using the shared
 * connection provided by {@link SqliteConnection}. Generated primary keys are
 * written back to the {@link Task} object after a successful insert.
 * </p>
 *
 * <p>Date values are stored as ISO-8601 strings and parsed back via
 * {@link #parseDateTime(String)}, which handles both legacy date-only strings
 * ({@code yyyy-MM-dd}) and full date-time strings ({@code yyyy-MM-ddTHH:mm:ss}).</p>
 *
 * <p>All SQL exceptions are caught and printed to standard error rather than
 * propagated, so callers should verify return values where correctness matters.</p>
 */
public class SqliteTaskDAO implements ITaskDAO {

    /** Shared database connection obtained from the singleton {@link SqliteConnection}. */
    private Connection connection;

    /**
     * Constructs a new {@code SqliteTaskDAO} using the application's
     * shared SQLite connection.
     */
    public SqliteTaskDAO() {
        connection = SqliteConnection.getInstance();
    }

    /**
     * Inserts a new task into the {@code tasks} table.
     * The generated database ID is assigned back to the {@code task} object.
     * If {@link Task#getPriority()} is {@code null}, {@code "MEDIUM"} is stored.
     *
     * @param task the {@link Task} to insert; must not be {@code null}
     */
    @Override
    public void addTask(Task task) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO tasks (title, startDate, endDate, description, tags, priority, userId) "
                            + "VALUES (?, ?, ?, ?, ?, ?, ?)");
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

    /**
     * Updates an existing task record in the {@code tasks} table.
     * The task is identified by its {@link Task#getId() id}.
     * If {@link Task#getPriority()} is {@code null}, {@code "MEDIUM"} is stored.
     *
     * @param task the {@link Task} containing updated field values; must not be {@code null}
     */
    @Override
    public void updateTask(Task task) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE tasks SET title = ?, startDate = ?, endDate = ?, description = ?, "
                            + "tags = ?, priority = ?, userId = ? WHERE id = ?");
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

    /**
     * Deletes a task from the {@code tasks} table by its ID.
     *
     * @param task the {@link Task} to delete; must not be {@code null}
     */
    @Override
    public void deleteTask(Task task) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM tasks WHERE id = ?");
            statement.setInt(1, task.getId());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a single task from the {@code tasks} table by its ID.
     *
     * @param id the primary key of the task to retrieve
     * @return the matching {@link Task}, or {@code null} if no record exists with that ID
     */
    @Override
    public Task getTask(int id) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM tasks WHERE id = ?");
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String title            = resultSet.getString("Title");
                LocalDateTime startDate = parseDateTime(resultSet.getString("startDate"));
                LocalDateTime endDate   = parseDateTime(resultSet.getString("endDate"));
                String description      = resultSet.getString("Description");
                String tags             = resultSet.getString("Tags");
                Task.Priority priority  = parsePriority(resultSet.getString("priority"));
                int userId              = resultSet.getInt("UserId");

                Task task = new Task(title, startDate, endDate, description, tags, userId);
                task.setId(id);
                task.setPriority(priority);
                return task;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves all tasks stored in the {@code tasks} table.
     *
     * @return a {@link List} of all {@link Task} objects; never {@code null},
     *         but may be empty if the table contains no rows
     */
    @Override
    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM tasks");
            while (resultSet.next()) {
                int id                  = resultSet.getInt("id");
                String title            = resultSet.getString("Title");
                LocalDateTime startDate = parseDateTime(resultSet.getString("startDate"));
                LocalDateTime endDate   = parseDateTime(resultSet.getString("endDate"));
                String description      = resultSet.getString("Description");
                String tags             = resultSet.getString("Tags");
                Task.Priority priority  = parsePriority(resultSet.getString("priority"));
                int userId              = resultSet.getInt("UserId");
                Task task = new Task(title, startDate, endDate, description, tags, userId);
                task.setId(id);
                task.setPriority(priority);
                tasks.add(task);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tasks;
    }

    /**
     * Retrieves all tasks belonging to the specified user from the {@code tasks} table.
     *
     * @param userId the ID of the user whose tasks should be retrieved
     * @return a {@link List} of {@link Task} objects owned by the given user;
     *         never {@code null}, but may be empty
     */
    @Override
    public List<Task> getTasksByUser(int userId) {
        List<Task> tasks = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM tasks WHERE userId = ?");
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id                  = resultSet.getInt("id");
                String title            = resultSet.getString("title");
                LocalDateTime startDate = parseDateTime(resultSet.getString("startDate"));
                LocalDateTime endDate   = parseDateTime(resultSet.getString("endDate"));
                String description      = resultSet.getString("description");
                String tags             = resultSet.getString("tags");
                Task.Priority priority  = parsePriority(resultSet.getString("priority"));
                int taskUserId          = resultSet.getInt("userId");
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

    /**
     * Parses an ISO-8601 date or date-time string into a {@link LocalDateTime}.
     * <p>
     * Handles two formats:
     * <ul>
     *   <li>Date-only ({@code yyyy-MM-dd}, length 10) — converted via
     *       {@link LocalDate#atStartOfDay()}</li>
     *   <li>Full date-time ({@code yyyy-MM-ddTHH:mm:ss}) — parsed directly</li>
     * </ul>
     * Returns {@link LocalDateTime#now()} if the value is {@code null}.
     * </p>
     *
     * @param value the string to parse; may be {@code null}
     * @return the parsed {@link LocalDateTime}, or the current date-time if {@code value} is null
     */
    private LocalDateTime parseDateTime(String value) {
        if (value == null) return LocalDateTime.now();
        // Handle old date-only format (yyyy-MM-dd)
        if (value.length() == 10) {
            return LocalDate.parse(value).atStartOfDay();
        }
        // Handle full date-time format
        return LocalDateTime.parse(value);
    }

    /**
     * Converts a priority string to the corresponding {@link Task.Priority} enum constant.
     * The comparison is case-insensitive. Returns {@link Task.Priority#MEDIUM} if the
     * value is {@code null} or does not match any known constant.
     *
     * @param value the priority string to parse (e.g. {@code "HIGH"}, {@code "low"})
     * @return the matching {@link Task.Priority}, or {@link Task.Priority#MEDIUM} as a default
     */
    private Task.Priority parsePriority(String value) {
        if (value == null) return Task.Priority.MEDIUM;
        try {
            return Task.Priority.valueOf(value.toUpperCase());
        } catch (Exception e) {
            return Task.Priority.MEDIUM;
        }
    }
}