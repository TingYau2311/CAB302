package com.tasktopia.model;

import java.util.List;


public interface ITaskDAO {
    public void addTask(Task task);

    public void updateTask(Task task);

    public void deleteTask(Task task);

    public Task getTask(int id);

    public List<Task> getAllTasks();

    public List<Task> getTasksByUser(int userId);
}