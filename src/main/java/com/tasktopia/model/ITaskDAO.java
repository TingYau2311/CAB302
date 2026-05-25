package com.tasktopia.model;

import java.util.List;

/**
 * Data Access Object interface for {@link Task} entities.
 * <p>
 * Defines the standard CRUD (Create, Read, Update, Delete) operations
 * that any concrete task DAO implementation must provide, whether
 * backed by a real database or an in-memory mock.
 * </p>
 */
public interface ITaskDAO {

    /**
     * Persists a new task to the data store.
     * Implementations should assign a generated ID back to the provided
     * {@code task} object after a successful insert.
     *
     * @param task the {@link Task} to add; must not be {@code null}
     */
    void addTask(Task task);

    /**
     * Updates an existing task record in the data store.
     * The task is identified by its {@link Task#getId() id}.
     *
     * @param task the {@link Task} containing updated values; must not be {@code null}
     */
    void updateTask(Task task);

    /**
     * Removes a task from the data store.
     * The task is identified by its {@link Task#getId() id}.
     *
     * @param task the {@link Task} to delete; must not be {@code null}
     */
    void deleteTask(Task task);

    /**
     * Retrieves a single task by its unique ID.
     *
     * @param id the ID of the task to retrieve
     * @return the matching {@link Task}, or {@code null} if no task with that ID exists
     */
    Task getTask(int id);

    /**
     * Retrieves all tasks stored in the data store.
     *
     * @return a {@link List} of all {@link Task} objects; never {@code null},
     *         but may be empty if no tasks exist
     */
    List<Task> getAllTasks();

    /**
     * Retrieves all tasks belonging to a specific user.
     *
     * @param userId the ID of the user whose tasks should be retrieved
     * @return a {@link List} of {@link Task} objects owned by the given user;
     *         never {@code null}, but may be empty
     */
    List<Task> getTasksByUser(int userId);
}