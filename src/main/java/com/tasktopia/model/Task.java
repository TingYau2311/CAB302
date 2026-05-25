package com.tasktopia.model;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Represents a task in the Tasktopia application.
 * <p>
 * A {@code Task} can be created in two ways, reflected by two constructors:
 * <ul>
 *   <li>The <em>database constructor</em> (used by {@code SqliteTaskDAO}) accepts
 *       a full date-time range, tags, and a user ID.</li>
 *   <li>The <em>GUI constructor</em> (used by {@code TaskStore}) accepts a date,
 *       time, {@link Category}, and {@link Priority} for the UI-driven workflow.</li>
 * </ul>
 * Both paths share the same underlying fields; unused fields remain {@code null}
 * or default-valued.
 */
public class Task {

    /**
     * Represents the urgency level of a task.
     */
    public enum Priority { HIGH, MEDIUM, LOW }

    /**
     * Represents the built-in category a task can belong to.
     * User-defined categories are managed separately via {@link CustomCategory}.
     */
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

    /**
     * Constructs a {@code Task} for database persistence.
     * Used primarily by {@code SqliteTaskDAO} when reading from or writing to the database.
     *
     * @param title       the title of the task; must not be {@code null}
     * @param startDate   the start date and time of the task
     * @param endDate     the end (due) date and time of the task
     * @param description a longer description of the task
     * @param tags        a comma-separated string of tags associated with the task
     * @param userId      the ID of the user who owns this task
     */
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

    /**
     * Constructs a {@code Task} for the GUI workflow.
     * Used by {@code TaskStore} and UI controllers when creating tasks via the interface.
     *
     * @param name        the display name (title) of the task
     * @param description a longer description of the task
     * @param date        the date on which the task is scheduled
     * @param time        the time at which the task is scheduled
     * @param category    the built-in {@link Category} of the task
     * @param priority    the {@link Priority} level of the task
     */
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

    /**
     * Returns the unique database ID of this task.
     *
     * @return the task's ID
     */
    public int getId()                  { return id; }

    /**
     * Sets the unique database ID of this task.
     * Typically called by the DAO after a successful insert.
     *
     * @param id the ID to assign
     */
    public void setId(int id)           { this.id = id; }

    /**
     * Returns the title of this task.
     *
     * @return the task title
     */
    public String getTitle()            { return title; }

    /**
     * Sets the title of this task.
     *
     * @param title the new title
     */
    public void setTitle(String title)  { this.title = title; }

    /**
     * Returns the title of this task.
     * Alias for {@link #getTitle()} provided for compatibility with
     * {@code HomeController} and {@code TaskStore}.
     *
     * @return the task title
     */
    public String getName()             { return title; }

    /**
     * Sets the title of this task.
     * Alias for {@link #setTitle(String)} provided for compatibility with
     * {@code HomeController} and {@code TaskStore}.
     *
     * @param name the new title
     */
    public void setName(String name)    { this.title = name; }

    /**
     * Returns the start date and time of this task.
     *
     * @return the start {@link LocalDateTime}
     */
    public LocalDateTime getStartDate()              { return startDate; }

    /**
     * Sets the start date and time of this task.
     *
     * @param startDate the new start date-time
     */
    public void setStartDate(LocalDateTime startDate){ this.startDate = startDate; }

    /**
     * Returns the end (due) date and time of this task.
     *
     * @return the end {@link LocalDateTime}
     */
    public LocalDateTime getEndDate()                { return endDate; }

    /**
     * Sets the end (due) date and time of this task.
     *
     * @param endDate the new end date-time
     */
    public void setEndDate(LocalDateTime endDate)    { this.endDate = endDate; }

    /**
     * Returns the description of this task.
     *
     * @return the task description
     */
    public String getDescription()                   { return description; }

    /**
     * Sets the description of this task.
     *
     * @param description the new description
     */
    public void setDescription(String description)   { this.description = description; }

    /**
     * Returns the tags string associated with this task.
     *
     * @return a comma-separated string of tags, or {@code null} if not set
     */
    public String getTags()             { return tags; }

    /**
     * Sets the tags string for this task.
     *
     * @param tags a comma-separated string of tags
     */
    public void setTags(String tags)    { this.tags = tags; }

    /**
     * Returns the ID of the user who owns this task.
     *
     * @return the owner's user ID
     */
    public int getUserId()              { return userId; }

    /**
     * Sets the ID of the user who owns this task.
     *
     * @param userId the new user ID
     */
    public void setUserId(int userId)   { this.userId = userId; }

    /**
     * Returns the scheduled date of this task (GUI constructor path).
     *
     * @return the {@link LocalDate}, or {@code null} if not set
     */
    public LocalDate getDate()          { return date; }

    /**
     * Sets the scheduled date of this task.
     *
     * @param date the new scheduled date
     */
    public void setDate(LocalDate date) { this.date = date; }

    /**
     * Returns the scheduled time of this task (GUI constructor path).
     *
     * @return the {@link LocalTime}, or {@code null} if not set
     */
    public LocalTime getTime()          { return time; }

    /**
     * Sets the scheduled time of this task.
     *
     * @param time the new scheduled time
     */
    public void setTime(LocalTime time) { this.time = time; }

    /**
     * Returns the built-in category of this task.
     *
     * @return the {@link Category}, or {@code null} if not set
     */
    public Category getCategory() { return category; }

    /**
     * Sets the built-in category of this task.
     *
     * @param category the new {@link Category}
     */
    public void setCategory(Category category)  { this.category = category; }


    /**
     * Returns the priority of this task.
     *
     * @return the {@link Priority}, or {@code null} if not set
     */
    public Priority getPriority() { return priority; }

    /**
     * Sets the priority of this task.
     *
     * @param priority the new {@link Priority}
     */
    public void setPriority(Priority priority) { this.priority = priority; }

    /**
     * Returns whether this task has been marked as done.
     *
     * @return {@code true} if the task is complete; {@code false} otherwise
     */
    public boolean isDone() { return done; }

    /**
     * Marks this task as done or not done.
     *
     * @param done {@code true} to mark complete; {@code false} to mark incomplete
     */
    public void setDone(boolean done) { this.done = done; }

    // ── Helper methods ──────────────────────────────────────────────────────

    /**
     * Returns the scheduled time as a string, or an empty string if no time is set.
     *
     * @return the time string (e.g. {@code "14:30"}) or {@code ""}
     */
    public String getTimeString() {
        return time != null ? time.toString() : "";
    }

    /**
     * Returns a human-readable label for the task's category.
     * The first letter is capitalised and the remainder are lowercase
     * (e.g. {@link Category#WORK} → {@code "Work"}).
     *
     * @return the category label, or {@code ""} if the category is {@code null}
     */
    public String getCategoryLabel() {
        if (category == null) return "";
        String s = category.name();
        return s.charAt(0) + s.substring(1).toLowerCase();
    }

    /**
     * Returns a human-readable label for the task's priority.
     * The first letter is capitalised and the remainder are lowercase
     * (e.g. {@link Priority#HIGH} → {@code "High"}).
     *
     * @return the priority label, or {@code ""} if the priority is {@code null}
     */
    public String getPriorityLabel() {
        if (priority == null) return "";
        String s = priority.name();
        return s.charAt(0) + s.substring(1).toLowerCase();
    }
}