package au.edu.qut.cogniti;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public class CognitiRequest {

    JsonObject root;

    public CognitiRequest(String agentId, String ltiCourseId) {
        root = new JsonObject();
        root.addProperty("agent_id", agentId);
        root.addProperty("lti_course_id", ltiCourseId);
    }

    public CognitiRequest(String userMessage, String agentId, String conversationId) {
        root = new JsonObject();

        JsonArray messages = new JsonArray();
        JsonObject message = new JsonObject();
        message.addProperty("content", userMessage);
        messages.add(message);

        root.add("messages", messages);
        root.addProperty("agent_id", agentId);
        root.addProperty("conversation_id", conversationId);
        root.add("file_attachments", new JsonArray());
    }

    public void addChatHistory(ArrayList<ChatTurn> history) {
        JsonArray chatHistory = new JsonArray();

        for (ChatTurn turn : history) {

            // user turn
            JsonObject userEntry = new JsonObject();
            userEntry.addProperty("role", "user");
            userEntry.addProperty("content", turn.userSays);
            chatHistory.add(userEntry);

            // assistant turn
            JsonObject aiEntry = new JsonObject();
            aiEntry.addProperty("role", "ai");
            aiEntry.addProperty("content", turn.aiSays);
            chatHistory.add(aiEntry);
        }

        root.add("chat_history", chatHistory);

    }

    String getJsonBody() {
        Gson gson = new Gson();
        return gson.toJson(root);
    }

}
