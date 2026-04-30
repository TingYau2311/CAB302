package com.tasktopia.model;

import java.util.ArrayList;
import java.util.List;

public class TaskList {
    private ITaskDAO taskDAO;
    public TaskList(ITaskDAO taskDAO){
        this.taskDAO = taskDAO;
    }


    public List<Task> searchTasks(String query) {
        ArrayList<Task> results = new ArrayList<>();
        for (Task task : taskDAO.getAllTasks()) {
            if (task.getString().contains(query)) {
                results.add(task);
            }
        }
        return results;
    }

    public void addTask(Task task) {
        taskDAO.addTask(task);
    }
}
