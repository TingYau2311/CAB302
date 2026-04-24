package com.tasktopia.model;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;

public class Task {
    private int id;
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String description;
    private String tags;
    private int userId;

    public Task(String title, LocalDateTime startDate, LocalDateTime endDate, String description, String tags, int userId) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.tags = tags;
        this.userId = userId;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }



    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }



    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }



    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    //--------------------------------------------------------------


    public enum Priority { HIGH, MEDIUM, LOW }
    public enum Category { WORK, GROCERY, PERSONAL, SCHOOL, MEDICAL, SOCIAL, FITNESS }

    private static int nextId = 1;

    private String name;
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

    public String    getName()        { return name; }
    public LocalDate getDate()        { return date; }
    public LocalTime getTime()        { return time; }
    public Category  getCategory()    { return category; }
    public Priority  getPriority()    { return priority; }
    public boolean   isDone()         { return done; }

    public void setName(String name)               { this.name = name; }
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
