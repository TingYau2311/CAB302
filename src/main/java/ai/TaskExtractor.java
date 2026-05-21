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

        // if category is not null, and the type is built in or custom
        if (dto.category != null && dto.category.value != null) {

            String raw = dto.category.value.trim().toUpperCase();

            try {
                Task.Category cat = Task.Category.valueOf(raw);
                task.setCategory(cat);
                task.setTags(raw.toLowerCase()); // in case it is not parsed correctly

            } catch (IllegalArgumentException e) {
                System.out.println("Unknown category from AI: " + raw);
                task.setCategory(Task.Category.PERSONAL); //  fallback
                task.setTags(raw.toLowerCase());
            }
        }

        return task;
    }
}