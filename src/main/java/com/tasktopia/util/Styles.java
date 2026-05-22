package com.tasktopia.util;

import com.tasktopia.model.Task;
import javafx.scene.control.Button;

/**
 * Centralised repository of inline JavaFX CSS style strings for the Tasktopia UI.
 *
 * <p>All methods return a ready-to-use value for {@link javafx.scene.Node#setStyle(String)}.
 * Colour constants are exposed as {@code public static final} fields so that
 * other classes can reference the palette directly.</p>
 *
 * <p>This class is a utility class and cannot be instantiated.</p>
 */
public final class Styles {

    // -------------------------------------------------------------------------
    // Colour palette
    // -------------------------------------------------------------------------

    /** Main background colour of the application. */
    public static final String BG          = "#f4f6fb";

    /** Top-bar / sidebar accent colour. */
    public static final String TOPBAR_ACC  = "#2e3f6e";

    /** Primary brand / action colour (blue). */
    public static final String PRIMARY     = "#4a6cf7";

    /** Accent red, used for destructive actions and high-priority indicators. */
    public static final String ACCENT_RED  = "#ff6b6b";

    /** Default body text colour. */
    public static final String TEXT        = "#1e2a4a";

    /** Muted / secondary text colour. */
    public static final String TEXT_MUTED  = "#8898aa";

    /** General border / divider colour. */
    public static final String BORDER      = "#e4e9f4";

    /** Colour used for HIGH-priority task accents. */
    public static final String HIGH        = "#ff6b6b";

    /** Colour used for MEDIUM-priority task accents. */
    public static final String MEDIUM_CLR  = "#e6ac00";

    /** Colour used for LOW-priority task accents. */
    public static final String LOW         = "#6bcb77";

    /**
     * Private constructor — prevents instantiation of this utility class.
     */
    private Styles() {}

    // -------------------------------------------------------------------------
    // Navigation
    // -------------------------------------------------------------------------

    /**
     * Returns the inline style for a sidebar navigation button.
     *
     * @param active {@code true} if this item represents the currently active view
     * @return a JavaFX CSS style string
     */
    public static String navItem(boolean active) {
        String bg = active ? PRIMARY : "transparent";
        String fg = active ? "white"  : "#a8b8d8";
        return "-fx-background-color: " + bg + "; "
                + "-fx-text-fill: " + fg + "; "
                + "-fx-font-family: 'Segoe UI'; -fx-font-size: 13px; "
                + "-fx-padding: 10 14; -fx-background-radius: 10; "
                + "-fx-cursor: hand; -fx-alignment: CENTER_LEFT;";
    }

    /**
     * Returns the hover style for a sidebar navigation button.
     *
     * <p>Applies a subtle scale transform and drop-shadow to provide visual
     * feedback on mouse-over.</p>
     *
     * @return a JavaFX CSS style string
     */
    public static String navItemHover() {
        return "-fx-background-color: " + TOPBAR_ACC + "; "
                + "-fx-text-fill: white; "
                + "-fx-font-family: 'Segoe UI'; -fx-font-size: 13px; "
                + "-fx-padding: 10 14; -fx-background-radius: 10; "
                + "-fx-cursor: hand; -fx-alignment: CENTER_LEFT;"
                + "-fx-scale-x: 1.05; -fx-scale-y: 1.05;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 12, 0.2, 0, 4);";
    }

    /**
     * Attaches mouse-enter and mouse-exit handlers to a {@link Button} so that
     * it transitions between a base style and a hover style automatically.
     *
     * @param btn        the button to which hover behaviour is applied
     * @param baseStyle  the style applied when the pointer is not over the button
     * @param hoverStyle the style applied while the pointer is over the button
     */
    public static void applyHoverEffect(Button btn, String baseStyle, String hoverStyle) {
        btn.setStyle(baseStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(baseStyle));
    }

    // -------------------------------------------------------------------------
    // Task cards
    // -------------------------------------------------------------------------

    /**
     * Returns the inline style for a task card, with a coloured left border
     * that reflects the task's priority level.
     *
     * @param priority the {@link Task.Priority} of the task ({@code HIGH}, {@code MEDIUM}, or {@code LOW})
     * @return a JavaFX CSS style string
     */
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

    /**
     * Returns the inline style for a task name label.
     *
     * <p>When {@code done} is {@code true} the text is rendered in the muted
     * colour with a strikethrough to indicate completion.</p>
     *
     * @param done {@code true} if the task has been marked as complete
     * @return a JavaFX CSS style string
     */
    public static String taskName(boolean done) {
        return "-fx-font-size: 15px; -fx-font-weight: bold; "
                + "-fx-font-family: 'Segoe UI'; "
                + "-fx-text-fill: " + (done ? TEXT_MUTED : TEXT) + "; "
                + (done ? "-fx-strikethrough: true;" : "");
    }

    /**
     * Returns the inline style for secondary task metadata labels (e.g. due
     * date, category).
     *
     * @return a JavaFX CSS style string
     */
    public static String taskMeta() {
        return "-fx-font-size: 11px; -fx-font-family: 'Segoe UI'; "
                + "-fx-text-fill: " + TEXT_MUTED + ";";
    }

    // -------------------------------------------------------------------------
    // Badges
    // -------------------------------------------------------------------------

    /**
     * Returns the inline style for a small pill-shaped badge.
     *
     * <p>Supported type values:</p>
     * <ul>
     *   <li>{@code "high"}     — red badge for high-priority tasks</li>
     *   <li>{@code "medium"}   — amber badge for medium-priority tasks</li>
     *   <li>{@code "low"}      — green badge for low-priority tasks</li>
     *   <li>{@code "category"} — blue badge for category labels</li>
     *   <li>anything else      — neutral grey badge</li>
     * </ul>
     *
     * @param type a string key identifying the badge variant
     * @return a JavaFX CSS style string
     */
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

    // -------------------------------------------------------------------------
    // Interactive controls
    // -------------------------------------------------------------------------

    /**
     * Returns the inline style for a circular task-completion toggle button.
     *
     * <p>A filled circle (primary colour with a checkmark) is shown when the
     * task is done; an empty bordered circle is shown otherwise.</p>
     *
     * @param done {@code true} if the task has been marked as complete
     * @return a JavaFX CSS style string
     */
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

    /**
     * Returns the inline style for a primary action button (gradient blue fill).
     *
     * @return a JavaFX CSS style string
     */
    public static String primaryButton() {
        return "-fx-background-color: linear-gradient(to right, #4a6cf7, #6b8fff); "
                + "-fx-text-fill: white; -fx-font-family: 'Segoe UI'; -fx-font-weight: bold; "
                + "-fx-font-size: 13px; -fx-padding: 13 22; -fx-background-radius: 12; "
                + "-fx-cursor: hand; "
                + "-fx-effect: dropshadow(gaussian, rgba(74,108,247,0.35), 14, 0, 0, 5);";
    }

    /**
     * Returns the inline style for a secondary / neutral action button (white fill).
     *
     * @return a JavaFX CSS style string
     */
    public static String secondaryButton() {
        return "-fx-background-color: white; "
                + "-fx-text-fill: " + TEXT + "; -fx-font-family: 'Segoe UI'; -fx-font-weight: bold; "
                + "-fx-font-size: 13px; -fx-padding: 13 22; -fx-background-radius: 12; "
                + "-fx-cursor: hand; "
                + "-fx-effect: dropshadow(gaussian, rgba(74,108,247,0.10), 10, 0, 0, 3);";
    }

    /**
     * Returns the inline style for a danger / destructive action button
     * (translucent red fill with red text).
     *
     * @return a JavaFX CSS style string
     */
    public static String dangerButton() {
        return "-fx-background-color: rgba(255,107,107,0.10); "
                + "-fx-text-fill: " + ACCENT_RED + "; -fx-font-family: 'Segoe UI'; "
                + "-fx-font-weight: bold; -fx-font-size: 13px; "
                + "-fx-padding: 13 22; -fx-background-radius: 12; -fx-cursor: hand;";
    }

    // -------------------------------------------------------------------------
    // Modals & overlays
    // -------------------------------------------------------------------------

    /**
     * Returns the inline style for the white rounded card that forms the body
     * of a modal dialog.
     *
     * @return a JavaFX CSS style string
     */
    public static String modalCard() {
        return "-fx-background-color: white; -fx-background-radius: 24; "
                + "-fx-padding: 36 36 32 36; "
                + "-fx-effect: dropshadow(gaussian, rgba(30,42,74,0.25), 40, 0, 0, 10);";
    }

    /**
     * Returns the inline style for the title label inside a modal dialog.
     *
     * @return a JavaFX CSS style string
     */
    public static String modalTitle() {
        return "-fx-font-size: 20px; -fx-font-weight: bold; "
                + "-fx-font-family: 'Segoe UI'; -fx-text-fill: " + TEXT + ";";
    }

    /**
     * Returns the inline style for a semi-transparent dark overlay that sits
     * behind a modal dialog to dim the rest of the UI.
     *
     * @return a JavaFX CSS style string
     */
    public static String overlayBg() {
        return "-fx-background-color: rgba(30,42,74,0.55);";
    }

    /**
     * Returns the inline style for a modal close (✕) button.
     *
     * @return a JavaFX CSS style string
     */
    public static String closeButton() {
        return "-fx-background-color: " + BG + "; -fx-background-radius: 50; "
                + "-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 13px; "
                + "-fx-min-width: 30; -fx-min-height: 30; -fx-max-width: 30; -fx-max-height: 30; "
                + "-fx-cursor: hand;";
    }

    // -------------------------------------------------------------------------
    // Form controls
    // -------------------------------------------------------------------------

    /**
     * Returns the inline style for a form field label (small, uppercased appearance).
     *
     * @return a JavaFX CSS style string
     */
    public static String formLabel() {
        return "-fx-font-size: 10px; -fx-font-weight: bold; "
                + "-fx-font-family: 'Segoe UI'; -fx-text-fill: " + TEXT_MUTED + ";";
    }

    /**
     * Returns the inline style for an unfocused form input field.
     *
     * @return a JavaFX CSS style string
     */
    public static String formInput() {
        return "-fx-background-color: " + BG + "; "
                + "-fx-border-color: " + BORDER + "; -fx-border-radius: 10; "
                + "-fx-background-radius: 10; -fx-border-width: 1.5; "
                + "-fx-padding: 10 14; -fx-font-family: 'Segoe UI'; -fx-font-size: 13px; "
                + "-fx-text-fill: " + TEXT + ";";
    }

    /**
     * Returns the inline style for a focused form input field.
     *
     * <p>The border colour changes to {@link #PRIMARY} to provide a clear
     * keyboard-focus indicator.</p>
     *
     * @return a JavaFX CSS style string
     */
    public static String formInputFocused() {
        return "-fx-background-color: " + BG + "; "
                + "-fx-border-color: " + PRIMARY + "; -fx-border-radius: 10; "
                + "-fx-background-radius: 10; -fx-border-width: 1.5; "
                + "-fx-padding: 10 14; -fx-font-family: 'Segoe UI'; -fx-font-size: 13px; "
                + "-fx-text-fill: " + TEXT + ";";
    }

    // -------------------------------------------------------------------------
    // Miscellaneous
    // -------------------------------------------------------------------------

    /**
     * Returns the inline style for an empty-state label (shown when a list has
     * no items to display).
     *
     * @return a JavaFX CSS style string
     */
    public static String emptyState() {
        return "-fx-font-family: 'Segoe UI'; -fx-font-size: 14px; "
                + "-fx-text-fill: " + TEXT_MUTED + ";";
    }

    /**
     * Returns the inline style for a transient toast / snackbar notification.
     *
     * @return a JavaFX CSS style string
     */
    public static String toastStyle() {
        return "-fx-background-color: " + TEXT + "; -fx-background-radius: 12; "
                + "-fx-padding: 13 20; -fx-font-family: 'Segoe UI'; -fx-font-size: 13px; "
                + "-fx-text-fill: white; "
                + "-fx-effect: dropshadow(gaussian, rgba(30,42,74,0.25), 20, 0, 0, 6);";
    }

    /**
     * Returns the inline style for an AI-generated hint / suggestion label
     * rendered beneath a task or input field.
     *
     * @return a JavaFX CSS style string
     */
    public static String aiHint() {
        return "-fx-background-color: rgba(74,108,247,0.06); -fx-background-radius: 8; "
                + "-fx-padding: 10 12; -fx-font-family: 'Segoe UI'; -fx-font-size: 11px; "
                + "-fx-text-fill: " + TEXT_MUTED + "; -fx-wrap-text: true;";
    }

    /**
     * Returns the inline style for a secondary subtitle label on the main view.
     *
     * @return a JavaFX CSS style string
     */
    public static String mainSubtitle() {
        return "-fx-font-size: 12px; -fx-font-family: 'Segoe UI'; "
                + "-fx-text-fill: " + TEXT_MUTED + ";";
    }

    /**
     * Returns the inline style for a date chip — a small pill that displays
     * a formatted date string (e.g. "Today", "Mon 23 Jun").
     *
     * @return a JavaFX CSS style string
     */
    public static String dateChip() {
        return "-fx-background-color: white; -fx-background-radius: 20; "
                + "-fx-padding: 7 16; -fx-font-family: 'Segoe UI'; "
                + "-fx-font-size: 12px; -fx-text-fill: " + TEXT_MUTED + "; "
                + "-fx-effect: dropshadow(gaussian, rgba(74,108,247,0.10), 10, 0, 0, 3);";
    }

    /**
     * Returns the inline style for the rounded white card container that wraps
     * the task list on the Home screen.
     *
     * @return a JavaFX CSS style string
     */
    public static String taskListCard() {
        return "-fx-background-color: white; "
                + "-fx-background-radius: 18; "
                + "-fx-padding: 20; "
                + "-fx-effect: dropshadow(gaussian, rgba(30,42,74,0.12), 20, 0, 0, 6);";
    }
}