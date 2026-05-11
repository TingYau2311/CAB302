package com.tasktopia.model;

/**
 * Represents a user-defined category with a display name and a colour (hex string).
 */
public class CustomCategory {

    private final String name;
    private final String colour; // e.g. "#FF6B6B"

    public CustomCategory(String name, String colour) {
        this.name   = name;
        this.colour = colour;
    }

    public String getName()   { return name; }
    public String getColour() { return colour; }

    /** Key used for category filtering (lower-case, no spaces). */
    public String getKey() {
        return name.toLowerCase().replaceAll("\\s+", "_");
    }
}

