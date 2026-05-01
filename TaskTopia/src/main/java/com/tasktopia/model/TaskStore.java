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

    private TaskStore() {
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

    public static void resetInstance() {
        instance = null;
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

    // ---------------------------------------------------
    // Filtering Methods
    // ---------------------------------------------------

    public List<Task> filterByCategory(Task.Category category) {
        return tasks.stream()
                .filter(t -> t != null && t.getCategory() == category)
                .collect(Collectors.toList());
    }

    public List<Task> filterByPriority(Task.Priority priority) {
        return tasks.stream()
                .filter(t -> t != null && t.getPriority() == priority)
                .collect(Collectors.toList());
    }

    public List<Task> filterByDone(boolean done) {
        return tasks.stream()
                .filter(t -> t != null && t.isDone() == done)
                .collect(Collectors.toList());
    }

    public List<Task> filterByDate(LocalDate date) {
        return tasks.stream()
                .filter(t -> t != null && date.equals(t.getDate()))
                .collect(Collectors.toList());
    }

    // ---------------------------------------------------
    // Sorting Methods
    // ---------------------------------------------------

    public List<Task> sortByDate() {
        return tasks.stream()
                .sorted(Comparator.comparing(Task::getDate))
                .collect(Collectors.toList());
    }

    public List<Task> sortByPriority() {
        return tasks.stream()
                .sorted(Comparator.comparing(Task::getPriority))
                .collect(Collectors.toList());
    }

    public List<Task> sortByName() {
        return tasks.stream()
                .sorted(Comparator.comparing(Task::getName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }
}
