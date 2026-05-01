package com.tasktopia.util;

import com.tasktopia.model.Task;

public final class Styles {

    // Colours
    public static final String BG          = "#f4f6fb";
    public static final String SIDEBAR_BG  = "#1e2a4a";
    public static final String SIDEBAR_ACC = "#2e3f6e";
    public static final String PRIMARY     = "#4a6cf7";
    public static final String ACCENT_RED  = "#ff6b6b";
    public static final String ACCENT_YEL  = "#ffd93d";
    public static final String TEXT        = "#1e2a4a";
    public static final String TEXT_MUTED  = "#8898aa";
    public static final String BORDER      = "#e4e9f4";
    public static final String HIGH        = "#ff6b6b";
    public static final String MEDIUM_CLR  = "#e6ac00";
    public static final String LOW         = "#6bcb77";

    private Styles() {}

    public static String navItem(boolean active) {
        String bg = active ? PRIMARY : "transparent";
        String fg = active ? "white"  : "#a8b8d8";
        return "-fx-background-color: " + bg + "; "
                + "-fx-text-fill: " + fg + "; "
                + "-fx-font-family: 'Segoe UI'; -fx-font-size: 13px; "
                + "-fx-padding: 10 14; -fx-background-radius: 10; "
                + "-fx-cursor: hand; -fx-alignment: CENTER_LEFT;";
    }

    public static String navItemHover() {
        return "-fx-background-color: " + SIDEBAR_ACC + "; "
                + "-fx-text-fill: white; "
                + "-fx-font-family: 'Segoe UI'; -fx-font-size: 13px; "
                + "-fx-padding: 10 14; -fx-background-radius: 10; "
                + "-fx-cursor: hand; -fx-alignment: CENTER_LEFT;";
    }

    public static String card(Task.Priority priority) {
        String accent = priority == Task.Priority.HIGH   ? HIGH
                : priority == Task.Priority.MEDIUM ? MEDIUM_CLR
                : LOW;
        return "-fx-background-color: white; -fx-background-radius: 16; "
                + "-fx-border-color: transparent transparent transparent " + accent + "; "
                + "-fx-border-width: 0 0 0 4; -fx-border-radius: 16; "
                + "-fx-padding: 18 22; "
                + "-fx-effect: dropshadow(gaussian, rgba(74,108,247,0.10), 16, 0, 0, 4); "
                + "-fx-cursor: hand;";
    }

    public static String taskName(boolean done) {
        return "-fx-font-size: 15px; -fx-font-weight: bold; "
                + "-fx-font-family: 'Segoe UI'; "
                + "-fx-text-fill: " + (done ? TEXT_MUTED : TEXT) + "; "
                + (done ? "-fx-strikethrough: true;" : "");
    }

    public static String taskMeta() {
        return "-fx-font-size: 11px; -fx-font-family: 'Segoe UI'; "
                + "-fx-text-fill: " + TEXT_MUTED + ";";
    }

    public static String badge(String type) {
        return switch (type) {
            case "high"     -> "-fx-background-color: rgba(255,107,107,0.12); "
                    + "-fx-text-fill: #ff6b6b; -fx-background-radius: 20; "
                    + "-fx-padding: 3 10; -fx-font-size: 11px; -fx-font-weight: bold; "
                    + "-fx-font-family: 'Segoe UI';";
            case "medium"   -> "-fx-background-color: rgba(255,217,61,0.2); "
                    + "-fx-text-fill: #b8960c; -fx-background-radius: 20; "
                    + "-fx-padding: 3 10; -fx-font-size: 11px; -fx-font-weight: bold; "
                    + "-fx-font-family: 'Segoe UI';";
            case "low"      -> "-fx-background-color: rgba(107,203,119,0.15); "
                    + "-fx-text-fill: #2e7d32; -fx-background-radius: 20; "
                    + "-fx-padding: 3 10; -fx-font-size: 11px; -fx-font-weight: bold; "
                    + "-fx-font-family: 'Segoe UI';";
            case "category" -> "-fx-background-color: rgba(74,108,247,0.10); "
                    + "-fx-text-fill: #4a6cf7; -fx-background-radius: 20; "
                    + "-fx-padding: 3 10; -fx-font-size: 11px; -fx-font-weight: bold; "
                    + "-fx-font-family: 'Segoe UI';";
            default         -> "-fx-background-color: " + BORDER + "; "
                    + "-fx-background-radius: 20; -fx-padding: 3 10;";
        };
    }

    public static String checkCircle(boolean done) {
        return done
                ? "-fx-background-color: " + PRIMARY + "; -fx-background-radius: 50; "
                + "-fx-min-width: 24; -fx-min-height: 24; -fx-max-width: 24; -fx-max-height: 24; "
                + "-fx-text-fill: white; -fx-font-size: 11px; -fx-cursor: hand;"
                : "-fx-background-color: transparent; "
                + "-fx-border-color: " + BORDER + "; -fx-border-radius: 50; "
                + "-fx-border-width: 2.5; "
                + "-fx-min-width: 24; -fx-min-height: 24; -fx-max-width: 24; -fx-max-height: 24; "
                + "-fx-cursor: hand;";
    }

    public static String primaryButton() {
        return "-fx-background-color: linear-gradient(to right, #4a6cf7, #6b8fff); "
                + "-fx-text-fill: white; -fx-font-family: 'Segoe UI'; -fx-font-weight: bold; "
                + "-fx-font-size: 13px; -fx-padding: 13 22; -fx-background-radius: 12; "
                + "-fx-cursor: hand; "
                + "-fx-effect: dropshadow(gaussian, rgba(74,108,247,0.35), 14, 0, 0, 5);";
    }

    public static String secondaryButton() {
        return "-fx-background-color: white; "
                + "-fx-text-fill: " + TEXT + "; -fx-font-family: 'Segoe UI'; -fx-font-weight: bold; "
                + "-fx-font-size: 13px; -fx-padding: 13 22; -fx-background-radius: 12; "
                + "-fx-cursor: hand; "
                + "-fx-effect: dropshadow(gaussian, rgba(74,108,247,0.10), 10, 0, 0, 3);";
    }

    public static String dangerButton() {
        return "-fx-background-color: rgba(255,107,107,0.10); "
                + "-fx-text-fill: " + ACCENT_RED + "; -fx-font-family: 'Segoe UI'; "
                + "-fx-font-weight: bold; -fx-font-size: 13px; "
                + "-fx-padding: 13 22; -fx-background-radius: 12; -fx-cursor: hand;";
    }

    /**
     * Style for the Edit button used in the detail modal and task rows.
     * Matches the primary look but slightly more compact for inline placement.
     */
    public static String editButton() {
        return "-fx-background-color: " + PRIMARY + "; "
                + "-fx-text-fill: white; -fx-font-family: 'Segoe UI'; -fx-font-weight: bold; "
                + "-fx-font-size: 13px; -fx-padding: 10 16; -fx-background-radius: 10; "
                + "-fx-cursor: hand; "
                + "-fx-effect: dropshadow(gaussian, rgba(74,108,247,0.22), 10, 0, 0, 3);";
    }

    public static String modalCard() {
        return "-fx-background-color: white; -fx-background-radius: 24; "
                + "-fx-padding: 36 36 32 36; "
                + "-fx-effect: dropshadow(gaussian, rgba(30,42,74,0.25), 40, 0, 0, 10);";
    }

    public static String modalTitle() {
        return "-fx-font-size: 20px; -fx-font-weight: bold; "
                + "-fx-font-family: 'Segoe UI'; -fx-text-fill: " + TEXT + ";";
    }

    public static String formLabel() {
        return "-fx-font-size: 10px; -fx-font-weight: bold; "
                + "-fx-font-family: 'Segoe UI'; -fx-text-fill: " + TEXT_MUTED + ";";
    }

    public static String formInput() {
        return "-fx-background-color: " + BG + "; "
                + "-fx-border-color: " + BORDER + "; -fx-border-radius: 10; "
                + "-fx-background-radius: 10; -fx-border-width: 1.5; "
                + "-fx-padding: 10 14; -fx-font-family: 'Segoe UI'; -fx-font-size: 13px; "
                + "-fx-text-fill: " + TEXT + ";";
    }

    public static String formInputFocused() {
        return "-fx-background-color: " + BG + "; "
                + "-fx-border-color: " + PRIMARY + "; -fx-border-radius: 10; "
                + "-fx-background-radius: 10; -fx-border-width: 1.5; "
                + "-fx-padding: 10 14; -fx-font-family: 'Segoe UI'; -fx-font-size: 13px; "
                + "-fx-text-fill: " + TEXT + ";";
    }

    public static String overlayBg() {
        return "-fx-background-color: rgba(30,42,74,0.55);";
    }

    public static String closeButton() {
        return "-fx-background-color: " + BG + "; -fx-background-radius: 50; "
                + "-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 13px; "
                + "-fx-min-width: 30; -fx-min-height: 30; -fx-max-width: 30; -fx-max-height: 30; "
                + "-fx-cursor: hand;";
    }

    public static String emptyState() {
        return "-fx-font-family: 'Segoe UI'; -fx-font-size: 14px; "
                + "-fx-text-fill: " + TEXT_MUTED + ";";
    }

    public static String toastStyle() {
        return "-fx-background-color: " + TEXT + "; -fx-background-radius: 12; "
                + "-fx-padding: 13 20; -fx-font-family: 'Segoe UI'; -fx-font-size: 13px; "
                + "-fx-text-fill: white; "
                + "-fx-effect: dropshadow(gaussian, rgba(30,42,74,0.25), 20, 0, 0, 6);";
    }

    public static String aiHint() {
        return "-fx-background-color: rgba(74,108,247,0.06); -fx-background-radius: 8; "
                + "-fx-padding: 10 12; -fx-font-family: 'Segoe UI'; -fx-font-size: 11px; "
                + "-fx-text-fill: " + TEXT_MUTED + "; -fx-wrap-text: true;";
    }

    public static String mainSubtitle() {
        return "-fx-font-size: 12px; -fx-font-family: 'Segoe UI'; "
                + "-fx-text-fill: " + TEXT_MUTED + ";";
    }

    public static String dateChip() {
        return "-fx-background-color: white; -fx-background-radius: 20; "
                + "-fx-padding: 7 16; -fx-font-family: 'Segoe UI'; "
                + "-fx-font-size: 12px; -fx-text-fill: " + TEXT_MUTED + "; "
                + "-fx-effect: dropshadow(gaussian, rgba(74,108,247,0.10), 10, 0, 0, 3);";
    }

    // box around tasks
    public static String taskListCard() {
        return "-fx-background-color: white; "
                + "-fx-background-radius: 18; "
                + "-fx-padding: 20; "
                + "-fx-effect: dropshadow(gaussian, rgba(30,42,74,0.12), 20, 0, 0, 6);";
    }
}
