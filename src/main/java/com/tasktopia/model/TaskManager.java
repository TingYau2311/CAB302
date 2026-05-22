package com.tasktopia.model;

import java.util.List;

/**
 * Service class that provides higher-level task management operations on top of
 * an {@link ITaskDAO}.
 * <p>
 * Unlike {@link TaskList}, which searches only task titles, {@code TaskManager}
 * performs a broader search across the title, description, and start date fields.
 * It also delegates task creation to the underlying DAO.
 * </p>
 */
public class TaskManager {

    /** The underlying DAO used for all data access operations. */
    private ITaskDAO taskDAO;

    /**
     * Constructs a new {@code TaskManager} backed by the given DAO.
     *
     * @param taskDAO the {@link ITaskDAO} implementation to delegate to; must not be {@code null}
     */
    public TaskManager(ITaskDAO taskDAO) {this.taskDAO = taskDAO;
    }

    /**
     * Returns all tasks whose title, description, or start date contains the given
     * query string (case-insensitive).
     * <p>
     * If the query is {@code null} or empty, all tasks are returned.
     * </p>
     *
     * @param query the search string; may be {@code null} or empty
     * @return a {@link List} of matching {@link Task} objects; never {@code null},
     *         but may be empty if no tasks match
     */
    public List<Task> searchTasks(String query) {
        return taskDAO.getAllTasks()
                .stream()
                .filter(task -> isTaskMatched(task, query))
                .toList();
    }

    /**
     * Determines whether a task matches the given query by checking whether the
     * concatenated title, description, and start date contain the query string
     * (case-insensitive).
     * <p>
     * A {@code null} or empty query always returns {@code true} (matches all tasks).
     * </p>
     *
     * @param task  the {@link Task} to evaluate; must not be {@code null}
     * @param query the search string; may be {@code null} or empty
     * @return {@code true} if the task matches the query, {@code false} otherwise
     */
    private boolean isTaskMatched(Task task, String query) {
        if (query == null || query.isEmpty()) return true;
        query = query.toLowerCase();
        String searchString = task.getTitle()
                + " " + task.getDescription()
                + " " + task.getStartDate();
        return searchString.toLowerCase().contains(query);
    }

    /**
     * Persists a new task by delegating to the underlying DAO.
     *
     * @param task the {@link Task} to add; must not be {@code null}
     */
    public void addTask(Task task) {
        taskDAO.addTask(task);
    }
}