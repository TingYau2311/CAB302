package com.tasktopia.model;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A thin wrapper around ITaskDAO that adds query/search helpers.
 */
public class TaskList {

    /** The underlying DAO used for all data access operations. */
    private final ITaskDAO taskDAO;

    /**
     * Constructs a new {@code TaskList} backed by the given DAO.
     *
     * @param taskDAO the {@link ITaskDAO} implementation to delegate to; must not be {@code null}
     */
    public TaskList(ITaskDAO taskDAO) {
        this.taskDAO = taskDAO;
    }

    /** Delegates straight to the DAO.
     *
     *  @param task the {@link Task} to add; must not be {@code null}
     */
    public void addTask(Task task) {
        taskDAO.addTask(task);
    }

    /**
     * Returns tasks whose title contains the query (case-insensitive).
     * An empty or null query returns ALL tasks.
     *
     * @param query the search string to match against task titles; may be {@code null} or emp
     *
     * @return a {@link List} of matching {@link Task} objects; never {@code null}, but may be empty if no tasks match
     */
    public List<Task> searchTasks(String query) {
        List<Task> all = taskDAO.getAllTasks();
        if (query == null || query.trim().isEmpty()) {
            return all;
        }
        String lower = query.trim().toLowerCase();
        return all.stream()
                .filter(t -> t.getTitle() != null
                        && t.getTitle().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }
}