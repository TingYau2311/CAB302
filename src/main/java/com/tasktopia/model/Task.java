package com.tasktopia.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Task {

    public enum Priority { HIGH, MEDIUM, LOW }
    public enum Category { WORK, GROCERY, PERSONAL, SCHOOL, MEDICAL, SOCIAL, FITNESS }

    private static int nextId = 1;

    private final int id;
    private String name;
    private String description;
    private LocalDate date;
    private LocalTime time;
    private Category category;
    private Priority priority;
    private boolean done;

    public Task(String name, String description, LocalDate date, LocalTime time,
                Category category, Priority priority) {
        this.id          = nextId++;
        this.name        = name;
        this.description = description;
        this.date        = date;
        this.time        = time;
        this.category    = category;
        this.priority    = priority;
        this.done        = false;
    }

    public int       getId()          { return id; }
    public String    getName()        { return name; }
    public String    getDescription() { return description; }
    public LocalDate getDate()        { return date; }
    public LocalTime getTime()        { return time; }
    public Category  getCategory()    { return category; }
    public Priority  getPriority()    { return priority; }
    public boolean   isDone()         { return done; }

    public void setName(String name)               { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setDate(LocalDate date)            { this.date = date; }
    public void setTime(LocalTime time)            { this.time = time; }
    public void setCategory(Category category)     { this.category = category; }
    public void setPriority(Priority priority)     { this.priority = priority; }
    public void setDone(boolean done)              { this.done = done; }

    public String getTimeString() {
        return time != null ? time.toString() : "";
    }

    public String getCategoryLabel() {
        if (category == null) return "";
        String s = category.name();
        return s.charAt(0) + s.substring(1).toLowerCase();
    }

    public String getPriorityLabel() {
        if (priority == null) return "";
        String s = priority.name();
        return s.charAt(0) + s.substring(1).toLowerCase();
    }
}
