package com.tasktopia.model;

import java.util.ArrayList;
import java.util.List;

public class MockTaskDAO implements ITaskDAO {

    public final ArrayList<Task> tasks = new ArrayList<>();

    @Override
    public void addTask(Task task) {
        tasks.add(task);
    }

    public void updateTask(Task task) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId() == task.getId()) {
                tasks.set(i, task);
                break;
            }
        }
    }

    public void removeTask(Task task) {
        tasks.remove(task);
    }

    public Task getTask(int id) {
        for (Task task : tasks) {
            if (task.getId() == id) {
                return task;
            }
        }
        return null;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks);
    }
}