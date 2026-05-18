package com.tasktopia.model;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A thin wrapper around ITaskDAO that adds query/search helpers.
 */
public class TaskList {

    private final ITaskDAO taskDAO;

    public TaskList(ITaskDAO taskDAO) {
        this.taskDAO = taskDAO;
    }

    /** Delegates straight to the DAO. */
    public void addTask(Task task) {
        taskDAO.addTask(task);
    }

    /**
     * Returns tasks whose title contains the query (case-insensitive).
     * An empty or null query returns ALL tasks.
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