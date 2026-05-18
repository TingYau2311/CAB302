package com.tasktopia.model;

/**
 * Represents a user-defined task category with a display name and a hex colour.
 */
public class CustomCategory {

    private String name;
    private String colour;

    public CustomCategory(String name, String colour) {
        this.name   = name;
        this.colour = colour;
    }

    public String getName()              { return name; }
    public void setName(String name)     { this.name = name; }

    public String getColour()            { return colour; }
    public void setColour(String colour) { this.colour = colour; }

    /**
     * Returns a lowercase, trimmed key derived from the name.
     * Used by HomeController for category routing and duplicate detection.
     * e.g. "Travel" → "travel", "My Hobbies" → "my hobbies"
     */
    public String getKey() {
        return name == null ? "" : name.trim().toLowerCase();
    }

    @Override
    public String toString() {
        return name + " (" + colour + ")";
    }
}