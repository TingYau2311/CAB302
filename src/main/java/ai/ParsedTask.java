package ai;

/**
 * Data-transfer object (DTO) that represents a task parsed from a raw AI
 * response.
 *
 * <p>Instances are populated by Gson when deserialising the JSON returned by
 * the AI API inside {@link TaskExtractor}, so all fields are intentionally
 * {@code public} and non-final to allow direct field injection.</p>
 *
 * <p>Example JSON structure mapped to this class:</p>
 * <pre>{@code
 * {
 *   "title":       "Doctor appointment",
 *   "description": "Annual check-up at the GP",
 *   "date":        "2025-06-15",
 *   "time":        "09:30",
 *   "category": {
 *     "type":  "built_in",
 *     "value": "HEALTH"
 *   },
 *   "priority": "HIGH"
 * }
 * }</pre>
 *
 * @see ParsedCategory
 * @see TaskExtractor
 */
public class ParsedTask {

    /**
     * The short title of the task as identified by the AI.
     * May be {@code null} if the AI could not determine a title.
     */
    public String title;

    /**
     * An optional longer description of the task.
     * May be {@code null}.
     */
    public String description;

    /**
     * The due date in ISO-8601 format ({@code yyyy-MM-dd}), e.g. {@code "2025-06-15"}.
     * May be {@code null}; {@link TaskExtractor} falls back to the current date/time
     * when this field is absent.
     */
    public String date;

    /**
     * The due time in ISO-8601 format ({@code HH:mm} or {@code HH:mm:ss}),
     * May be {@code null}; used together with {@link #date} to form a
     * {@link java.time.LocalDateTime}.
     */
    public String time;

    /**
     * The category associated with the task, encapsulating both the type
     * ({@code built_in} / {@code custom}) and the category value.
     * May be {@code null} if the AI did not identify a category.
     */
    public ParsedCategory category;

    /**
     * The priority level of the task as a string (e.g. {@code "HIGH"},
     * {@code "MEDIUM"}, {@code "LOW"}). This value is mapped to
     * {@link com.tasktopia.model.Task.Priority} by {@link TaskExtractor}.
     * May be {@code null}.
     */
    public String priority;
}