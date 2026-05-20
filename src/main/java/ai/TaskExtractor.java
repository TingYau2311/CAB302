package ai;

import ai.ParsedTask;
import au.edu.qut.cogniti.CognitiConversation;
import com.google.gson.Gson;
import com.tasktopia.model.Task;

import java.time.LocalDateTime;

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
        Task task = new Task(
                dto.title,
                LocalDateTime.parse(dto.startDate),
                LocalDateTime.parse(dto.endDate),
                dto.description,
                null,
                0
        );

        if (dto.priority != null) {
            task.setPriority(Task.Priority.valueOf(dto.priority));
        }

        if (dto.category != null) {
            task.setCategory(Task.Category.valueOf(dto.category));
        }

        return task;
    }
}