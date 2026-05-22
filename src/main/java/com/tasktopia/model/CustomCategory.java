package com.tasktopia.model;

/**
 * Represents a user-defined task category with a display name and a hex colour.
 * <p>
 * Custom categories allow users to organise their tasks beyond the built-in
 * {@link Task.Category} enum values. Each instance stores a human-readable
 * name and an associated hex colour string (e.g. {@code "#FF5733"}).
 * </p>
 */
public class CustomCategory {

    private String name;
    private String colour;

    /**
     * Constructs a new {@code CustomCategory} with the given name and colour.
     *
     * @param name   the display name of the category (e.g. {@code "Travel"})
     * @param colour the hex colour string associated with this category (e.g. {@code "#FF5733"})
     */
    public CustomCategory(String name, String colour) {
        this.name   = name;
        this.colour = colour;
    }

    /**
     * Returns the display name of this category.
     *
     * @return the category name
     */
    public String getName() { return name; }

    /**
     * Sets the display name of this category.
     *
     * @param name the new category name
     */
    public void setName(String name) { this.name = name; }

    /**
     * Returns the hex colour string associated with this category.
     *
     * @return the colour string (e.g. {@code "#FF5733"})
     */
    public String getColour() { return colour; }

    /**
     * Sets the hex colour string for this category.
     *
     * @param colour the new colour string
     */
    public void setColour(String colour) { this.colour = colour; }

    /**
     * Returns a normalised key derived from the category name.
     * The key is the name trimmed of whitespace and converted to lowercase.
     * <p>
     * Used by {@code HomeController} for category routing and duplicate detection.
     * For example, {@code "Travel"} becomes {@code "travel"} and
     * {@code "My Hobbies"} becomes {@code "my hobbies"}.
     * </p>
     *
     * @return the lowercase, trimmed key, or an empty string if the name is {@code null}
     */
    public String getKey() {
        return name == null ? "" : name.trim().toLowerCase();
    }

    /**
     * Returns a string representation of this category in the format
     * {@code "name (colour)"}.
     *
     * @return a human-readable description of this category
     */
    @Override
    public String toString() {
        return name + " (" + colour + ")";
    }
}