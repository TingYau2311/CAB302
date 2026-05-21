package com.tasktopia.model;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;

public class Task {

    public enum Priority { HIGH, MEDIUM, LOW }
    public enum Category { WORK, GROCERY, PERSONAL, SCHOOL, MEDICAL, SOCIAL, FITNESS }


    private int id;
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String description;
    private String tags;
    private int userId;


    private LocalDate date;
    private LocalTime time;
    private Category category;
    private Priority priority;
    private boolean done;

    //  constructor used by SqliteTaskDAO
    public Task(String title, LocalDateTime startDate, LocalDateTime endDate,
                String description, String tags, int userId) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.tags = tags;
        this.userId = userId;
        this.done = false;
    }

    // ── GUI constructor (used by TaskStore) ─────────────────────
    public Task(String name, String description, LocalDate date, LocalTime time,
                Category category, Priority priority) {
        this.title = name;
        this.description = description;
        this.date = date;
        this.time = time;
        this.category = category;
        this.priority = priority;
        this.done = false;
    }


    public int getId()                  { return id; }
    public void setId(int id)           { this.id = id; }

    public String getTitle()            { return title; }
    public void setTitle(String title)  { this.title = title; }

    // getName() alias so HomeController and TaskStore still work
    public String getName()             { return title; }
    public void setName(String name)    { this.title = name; }

    public LocalDateTime getStartDate()              { return startDate; }
    public void setStartDate(LocalDateTime startDate){ this.startDate = startDate; }

    public LocalDateTime getEndDate()                { return endDate; }
    public void setEndDate(LocalDateTime endDate)    { this.endDate = endDate; }

    public String getDescription()                   { return description; }
    public void setDescription(String description)   { this.description = description; }

    public String getTags()             { return tags; }
    public void setTags(String tags)    { this.tags = tags; }

    public int getUserId()              { return userId; }
    public void setUserId(int userId)   { this.userId = userId; }

    public LocalDate getDate()          { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getTime()          { return time; }
    public void setTime(LocalTime time) { this.time = time; }

    public Category getCategory() { return category; }
    public void setCategory(Category category)  { this.category = category; }

    public Priority getPriority()               { return priority; }
    public void setPriority(Priority priority)  { this.priority = priority; }

    public boolean isDone()             { return done; }
    public void setDone(boolean done)   { this.done = done; }

    // ── Helper methods

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
