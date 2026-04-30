package com.tasktopia.model;

import java.util.ArrayList;
import java.util.List;

public class MockTaskDAO implements ITaskDAO {

    public final ArrayList<Task> tasks =  new ArrayList<>();
    private int autoIncrementedId = 0;

    @Override
    public void addTask(Task task) {
        task.setId(autoIncrementedId);
        autoIncrementedId++;
        tasks.add(task);
    }


    public void updateTask(Task task) {
        for (int i = 0; i < task.size(); i++) {
            if (task.get(i).getId() == task.getId()) {
                task.set(i, task);
                break;
            }
        }
    }


    public void removeTask(Task task){
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
