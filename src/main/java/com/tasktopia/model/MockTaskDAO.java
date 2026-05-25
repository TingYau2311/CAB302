package com.tasktopia.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * In-memory implementation of {@link ITaskDAO} for use in unit tests
 * and development scenarios where a real database is not available.
 * <p>
 * Tasks are stored in a plain {@link ArrayList} and IDs are assigned
 * via a simple auto-incrementing counter, mirroring the behaviour of a
 * database {@code AUTOINCREMENT} primary key.
 * </p>
 */
public class MockTaskDAO implements ITaskDAO {

    /** Internal list holding all tasks for the lifetime of this instance. */
    private final ArrayList<Task> tasks = new ArrayList<>();

    /** Counter used to simulate auto-incremented primary keys. */
    private int autoIncrementedId = 1;

    /**
     * Adds the given task to the in-memory store and assigns it the
     * next available auto-incremented ID.
     *
     * @param task the {@link Task} to add; must not be {@code null}
     */
    @Override
    public void addTask(Task task) {
        task.setId(autoIncrementedId++);
        tasks.add(task);
    }

    /**
     * Replaces the existing task entry whose ID matches the given task.
     * If no matching task is found, the method returns without making changes.
     *
     * @param task the {@link Task} with updated values; must not be {@code null}
     */
    @Override
    public void updateTask(Task task) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId() == task.getId()) {
                tasks.set(i, task);
                return;
            }
        }
    }

    /**
     * Removes the task with the same ID as the given task from the store.
     * If no matching task is found, the method returns without making changes.
     *
     * @param task the {@link Task} to delete; must not be {@code null}
     */
    @Override
    public void deleteTask(Task task) {
        tasks.removeIf(t -> t.getId() == task.getId());
    }

    /**
     * Finds and returns the task with the specified ID.
     *
     * @param id the ID of the task to retrieve
     * @return the matching {@link Task}, or {@code null} if none is found
     */
    @Override
    public Task getTask(int id) {
        return tasks.stream()
                .filter(t -> t.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Returns a copy of all tasks currently held in the in-memory store.
     *
     * @return a new {@link List} containing all stored {@link Task} objects;
     *         never {@code null}, but may be empty
     */
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks);
    }

    /**
     * Returns all tasks that belong to the specified user.
     *
     * @param userId the ID of the user whose tasks should be retrieved
     * @return a {@link List} of {@link Task} objects with a matching {@code userId};
     *         never {@code null}, but may be empty
     */
    @Override
    public List<Task> getTasksByUser(int userId) {
        return tasks.stream()
                .filter(t -> t.getUserId() == userId)
                .collect(Collectors.toList());
    }
}