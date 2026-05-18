package com.tasktopia.model;

/**
 * Parses a natural-language task string that uses hashtag conventions:
 *   "Call boss next tuesday #work #high"
 *
 * Supported priority tags : #high, #medium, #low
 * Supported category tags : #work, #grocery, #personal, #school, #medical, #social, #fitness
 */
public class AiTaskParser {

    /**
     * Returns the task name with all hashtag tokens stripped and whitespace trimmed.
     * e.g. "Call boss next tuesday #work #high" → "Call boss next tuesday"
     */
    public String extractName(String input) {
        if (input == null) return null;
        return input.replaceAll("#\\S+", "").trim().replaceAll("\\s{2,}", " ");
    }

    /**
     * Scans for #high / #medium / #low and returns the matching Priority,
     * or null if no recognised priority tag is found.
     */
    public Task.Priority extractPriority(String input) {
        if (input == null) return null;
        String lower = input.toLowerCase();
        if (lower.contains("#high"))   return Task.Priority.HIGH;
        if (lower.contains("#medium")) return Task.Priority.MEDIUM;
        if (lower.contains("#low"))    return Task.Priority.LOW;
        return null;
    }

    /**
     * Scans for a category hashtag and returns the matching Category,
     * or null if no recognised category tag is found.
     */
    public Task.Category extractCategory(String input) {
        if (input == null) return null;
        String lower = input.toLowerCase();
        for (Task.Category cat : Task.Category.values()) {
            if (lower.contains("#" + cat.name().toLowerCase())) {
                return cat;
            }
        }
        return null;
    }
}