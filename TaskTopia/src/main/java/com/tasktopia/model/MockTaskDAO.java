package com.tasktopia.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MockTaskDAO implements ITaskDAO {

    private final ArrayList<Task> tasks = new ArrayList<>();
    private int autoIncrementedId = 1;

    @Override
    public void addTask(Task task) {
        task.setId(autoIncrementedId++);
        tasks.add(task);
    }

    @Override
    public void updateTask(Task task) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId() == task.getId()) {
                tasks.set(i, task);
                return;
            }
        }
    }

    @Override
    public void deleteTask(Task task) {
        tasks.removeIf(t -> t.getId() == task.getId());
    }

    @Override
    public Task getTask(int id) {
        return tasks.stream()
                .filter(t -> t.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks);
    }

    @Override
    public List<Task> getTasksByUser(int userId) {
        return tasks.stream()
                .filter(t -> t.getUserId() == userId)
                .collect(Collectors.toList());
    }
}