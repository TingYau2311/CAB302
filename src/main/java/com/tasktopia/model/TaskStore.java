package com.tasktopia.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.LocalTime;

public class TaskStore {

    private static TaskStore instance;
    private final ObservableList<Task> tasks = FXCollections.observableArrayList();
    private String loggedInUser = "";

    private TaskStore() {
        // Demo tasks so the app loads with data
        tasks.add(new Task(
                "Buy Milk",
                "Full cream, 2L from Woolworths",
                LocalDate.now(),
                LocalTime.of(15, 4),
                Task.Category.GROCERY,
                Task.Priority.MEDIUM));

        tasks.add(new Task(
                "Team Meeting",
                "Discuss final design with team.",
                LocalDate.now(),
                LocalTime.of(7, 35),
                Task.Category.WORK,
                Task.Priority.HIGH));
    }

    public static TaskStore getInstance() {
        if (instance == null) instance = new TaskStore();
        return instance;
    }

    public ObservableList<Task> getTasks() { return tasks; }
    public void addTask(Task t)            { tasks.add(t); }
    public void removeTask(Task t)         { tasks.remove(t); }

    public String getLoggedInUser()           { return loggedInUser; }
    public void   setLoggedInUser(String user){ loggedInUser = user; }
}
