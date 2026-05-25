package ai;

/**
 * Data-transfer object (DTO) that represents the category portion of a
 * task parsed from an AI response.
 *
 * <p>Instances of this class are populated by Gson when deserialising the
 * JSON returned by the AI API, so all fields are intentionally {@code public}
 * and non-final to allow direct field injection.</p>
 *
 * <p>Example JSON fragment mapped to this class:</p>
 * <pre>{@code
 * "category": {
 *   "type":  "built_in",
 *   "value": "WORK"
 * }
 * }</pre>
 *
 * @see ParsedTask
 */
public class ParsedCategory {

    /**
     * Indicates whether the category is a built-in enum value or a
     * user-defined custom category (e.g. {@code "built_in"} or {@code "custom"}).
     * May be {@code null} if the AI did not return a type.
     */
    public String type;

    /**
     * The category name as returned by the AI (e.g. {@code "WORK"},
     * {@code "PERSONAL"}). This value is mapped to {@link com.tasktopia.model.Task.Category}
     * by {@code TaskExtractor}. May be {@code null} if the AI did not identify
     * a category.
     */
    public String value;
}