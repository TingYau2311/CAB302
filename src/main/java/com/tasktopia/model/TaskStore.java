package com.tasktopia.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Singleton in-memory store for the currently logged-in user's tasks and session state.
 * <p>
 * {@code TaskStore} acts as the application's runtime cache of tasks, backed by a
 * JavaFX {@link ObservableList} so that UI components can react to changes automatically.
 * It also holds the currently authenticated user's display name and database ID.
 * </p>
 *
 * <p>Use {@link #getInstance()} to obtain the shared instance. Do not instantiate
 * this class directly.</p>
 *
 * <p><strong>Note:</strong> This store is independent of the database DAOs.
 * Changes made here (via {@link #addTask} / {@link #removeTask}) are not automatically
 * persisted; callers are responsible for also invoking the appropriate DAO methods.</p>
 */
public class TaskStore {

    /** The single shared instance. */
    private static TaskStore instance;

    /** The observable list of tasks for the currently logged-in user. */
    private final ObservableList<Task> tasks = FXCollections.observableArrayList();

    /** The display name of the currently logged-in user. */
    private String loggedInUser = "";

    /** The database ID of the currently logged-in user, or {@code -1} if not set. */
    private int loggedInUserId = -1;

    /**
     * Private constructor — use {@link #getInstance()} instead.
     */
    private TaskStore() {
    }

    /**
     * Returns the shared {@code TaskStore} instance, creating it on first call.
     *
     * @return the singleton {@code TaskStore}; never {@code null}
     */
    public static TaskStore getInstance() {
        if (instance == null) instance = new TaskStore();
        return instance;
    }

    /**
     * Returns the observable list of tasks for the current user.
     * UI components can bind to this list to receive automatic update notifications.
     *
     * @return the {@link ObservableList} of {@link Task} objects; never {@code null}
     */
    public ObservableList<Task> getTasks() {
        return tasks;
    }

    /**
     * Adds a task to the in-memory store.
     * This does <em>not</em> persist the task to the database.
     *
     * @param t the {@link Task} to add; must not be {@code null}
     */
    public void addTask(Task t) {
        tasks.add(t);
    }

    /**
     * Removes a task from the in-memory store.
     * This does <em>not</em> delete the task from the database.
     *
     * @param t the {@link Task} to remove; must not be {@code null}
     */

    public void removeTask(Task t) {
        tasks.remove(t);
    }

    /**
     * Returns the display name of the currently logged-in user.
     *
     * @return the user's display name, or an empty string if not set
     */
    public String getLoggedInUser() {
        return loggedInUser;
    }

    /**
     * Sets the display name of the currently logged-in user.
     * Typically called after a successful authentication.
     *
     * @param user the user's display name
     */
    public void setLoggedInUser(String user) {
        loggedInUser = user;
    }

    /**
     * Returns the database ID of the currently logged-in user.
     *
     * @return the user's ID, or {@code -1} if no user is logged in
     */
    public int getLoggedInUserId() {
        return loggedInUserId;
    }

    /**
     * Sets the database ID of the currently logged-in user.
     * Typically called after a successful authentication.
     *
     * @param id the user's database ID
     */
    public void setLoggedInUserId(int id) {
        loggedInUserId = id;
    }
}
