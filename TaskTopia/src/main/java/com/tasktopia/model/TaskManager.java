package com.tasktopia.model;

import java.util.List;

public class TaskManager {
    private ITaskDAO taskDAO;
    public TaskManager(ITaskDAO taskDAO) {this.taskDAO = taskDAO;
    }

    public List<Task> searchTasks(String query) {
        return taskDAO.getAllTasks()
                .stream()
                .filter(task -> isTaskMatched(task, query))
                .toList();
    }

    private boolean isTaskMatched(Task task, String query) {
        if (query == null || query.isEmpty()) return true;
        query = query.toLowerCase();
        String searchString = task.getTitle()
                + " " + task.getDescription()
                + " " + task.getStartDate();
        return searchString.toLowerCase().contains(query);
    }

    public void addTask(Task task) {
        taskDAO.addTask(task);
    }
}