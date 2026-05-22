package ai;

import ai.ParsedTask;
import au.edu.qut.cogniti.CognitiConversation;
import com.google.gson.Gson;
import com.tasktopia.model.Task;
import com.tasktopia.model.TaskStore;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Extracts a structured {@link Task} from a natural-language string by sending
 * the input to an AI conversation, parsing the JSON response, and mapping the
 * result to the application's domain model.
 *
 * <h2>Processing pipeline</h2>
 * <ol>
 *   <li>Send the raw user input to the AI via {@link CognitiConversation}.</li>
 *   <li>Strip any Markdown code fences from the response ({@link #cleanJson}).</li>
 *   <li>Deserialise the cleaned JSON into a {@link ParsedTask} DTO using Gson.</li>
 *   <li>Map the DTO to a fully initialised {@link Task} ({@link #map}).</li>
 * </ol>
 *
 * <h2>Fallback behaviour</h2>
 * <ul>
 *   <li>If {@code date} or {@code time} is absent, the task start time defaults
 *       to {@link LocalDateTime#now()}.</li>
 *   <li>If the AI returns an unrecognised category, the task falls back to
 *       {@link Task.Category#PERSONAL}.</li>
 * </ul>
 */
public class TaskExtractor {

    /** AI conversation session used to send prompts and receive JSON responses. */
    private final CognitiConversation conversation;

    /** Gson instance used to deserialise AI JSON responses into {@link ParsedTask} DTOs. */
    private final Gson gson = new Gson();

    /**
     * Constructs a new {@code TaskExtractor} backed by the given AI conversation.
     *
     * @param conversation the active {@link CognitiConversation} session;
     *                     must not be {@code null}
     */
    public TaskExtractor(CognitiConversation conversation) {
        this.conversation = conversation;
    }

    /**
     * Sends {@code input} to the AI, parses the JSON response, and returns a
     * populated {@link Task}.
     *
     * @param input the natural-language task description provided by the user
     * @return a {@link Task} populated with data extracted from the AI response
     * @throws Exception if the AI call fails, the response cannot be parsed,
     *                   or date/time strings are in an unexpected format
     */
    public Task extract(String input) throws Exception {
        String raw = conversation.sendMessage(input);
        String cleaned = cleanJson(raw);
        ParsedTask dto = gson.fromJson(cleaned, ParsedTask.class);
        return map(dto);
    }

    /**
     * Removes Markdown code-fence markers ({@code ```json} and {@code ```})
     * that the AI may wrap around its JSON response, then trims whitespace.
     *
     * @param response the raw string returned by the AI
     * @return a clean JSON string ready for Gson deserialisation
     */
    private String cleanJson(String response) {
        return response
                .replace("```json", "")
                .replace("```", "")
                .trim();
    }

    /**
     * Maps a {@link ParsedTask} DTO to a fully initialised {@link Task} domain object.
     *
     * <p>The task's start time is derived from {@link ParsedTask#date} and
     * {@link ParsedTask#time}; if either field is {@code null}, the current
     * date/time is used as a fallback. The end time is always set to one hour
     * after the start time.</p>
     *
     * <p>Priority and category are applied when present. An unrecognised
     * category string is caught and silently replaced with
     * {@link Task.Category#PERSONAL}.</p>
     *
     * @param dto the parsed task DTO produced by Gson; must not be {@code null}
     * @return a {@link Task} ready to be persisted via {@link TaskStore}
     */
    private Task map(ParsedTask dto) {

        LocalDateTime start;

        if (dto.date != null && dto.time != null) {
            start = LocalDateTime.of(
                    LocalDate.parse(dto.date),
                    LocalTime.parse(dto.time)
            );
        } else {
            start = LocalDateTime.now(); // fallback when AI omits date/time
        }

        Task task = new Task(
                dto.title,
                start,
                start.plusHours(1),
                dto.description,
                null,
                TaskStore.getInstance().getLoggedInUserId()
        );

        if (dto.priority != null) {
            task.setPriority(Task.Priority.valueOf(dto.priority));
        }

        // Apply category if present; fall back to PERSONAL for unknown values
        if (dto.category != null && dto.category.value != null) {
            String raw = dto.category.value.trim().toUpperCase();
            try {
                Task.Category cat = Task.Category.valueOf(raw);
                task.setCategory(cat);
                task.setTags(raw.toLowerCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Unknown category from AI: " + raw);
                task.setCategory(Task.Category.PERSONAL);
                task.setTags(raw.toLowerCase());
            }
        }

        return task;
    }
}