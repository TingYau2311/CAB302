package com.tasktopia.model;

import java.util.List;

public interface ITaskDAO {
    void addTask(Task task);
    void updateTask(Task task);
    void deleteTask(Task task);
    Task getTask(int id);
    List<Task> getAllTasks();
    List<Task> getTasksByUser(int userId);
}