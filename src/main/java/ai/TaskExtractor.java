package ai;

import ai.ParsedTask;
import au.edu.qut.cogniti.CognitiConversation;
import com.google.gson.Gson;
import com.tasktopia.model.Task;
import com.tasktopia.model.TaskStore;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TaskExtractor {

    private final CognitiConversation conversation;
    private final Gson gson = new Gson();

    public TaskExtractor(CognitiConversation conversation) {
        this.conversation = conversation;
    }

    public Task extract(String input) throws Exception {

        String raw = conversation.sendMessage(input);

        String cleaned = cleanJson(raw);

        ParsedTask dto = gson.fromJson(cleaned, ParsedTask.class);

        return map(dto);
    }

    // for cleaning prompt response for correct format
    private String cleanJson(String response) {
        return response
                .replace("```json", "")
                .replace("```", "")
                .trim();
    }

    private Task map(ParsedTask dto) {

        LocalDateTime start = null;

        if (dto.date != null && dto.time != null) {
            start = LocalDateTime.of(
                    LocalDate.parse(dto.date),
                    LocalTime.parse(dto.time)
            );
        } else {
            start = LocalDateTime.now(); // fallback
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

        // making sure parsed category matches mapping
        if (dto.category != null) {
            if ("builtin".equalsIgnoreCase(dto.category.type)) {
                task.setCategory(Task.Category.valueOf(dto.category.value.toUpperCase()));
            } else {
                task.setTags(dto.category.value); // custom category fallback
            }
        }

        return task;
    }
}