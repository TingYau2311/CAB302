package com.tasktopia.model;

import java.util.List;

public interface ITaskDAO {
    List<Task> getAllTasks();

    void addTask(Task task);
}
