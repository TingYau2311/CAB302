package com.tasktopia.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TaskStore {

    private static TaskStore instance;
    private final ObservableList<Task> tasks = FXCollections.observableArrayList();
    private String loggedInUser = "";
    private int loggedInUserId = -1;

    private TaskStore() {
    }

    public static TaskStore getInstance() {
        if (instance == null) instance = new TaskStore();
        return instance;
    }

    public ObservableList<Task> getTasks() {
        return tasks;
    }

    public void addTask(Task t) {
        tasks.add(t);
    }

    public void removeTask(Task t) {
        tasks.remove(t);
    }

    public String getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(String user) {
        loggedInUser = user;
    }

    public int getLoggedInUserId() {
        return loggedInUserId;
    }

    public void setLoggedInUserId(int id) {
        loggedInUserId = id;
    }
}
