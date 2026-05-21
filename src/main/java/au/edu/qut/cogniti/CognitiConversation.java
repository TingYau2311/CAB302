package au.edu.qut.cogniti;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class CognitiConversation {
    private static final String BASE_URL = "https://www.cogniti.qut.edu.au";

    private final String token;
    private final String agent;
    private String conversationID;
    private ArrayList<ChatTurn> chatHistory = new ArrayList<>();
    private String lastResponse = "";

    public CognitiConversation(String agent, String token) {
        this.agent = agent;
        this.token = token;
        conversationID = null;
    }

    public static CognitiConversation initialise(String agent, String token) throws IOException, InterruptedException {

        CognitiConversation conversation = new CognitiConversation(agent, token);
        conversation.startNew();
        return conversation;

    }

    private void startNew() throws IOException, InterruptedException {

        String jsonBody = new CognitiRequest(agent, "").getJsonBody();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/v1/conversations/new"
                ))
                .header("Authorization", "Bearer " + this.token)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());
        CognitiResponse decoded = CognitiResponse.fromJson(response.body());

        this.conversationID = decoded.getConversation_id();
    }

    LocalDate today = LocalDate.now();
    LocalTime now = LocalTime.now();



    public String sendMessage(String message) throws IOException, InterruptedException {

        String prompt = """
        You are JSON generator for task extraction.
        Convert the user input into a structured JSON task.
        
        RULES:
        - Return ONLY valid JSON
        - Never return null
        - Do NOT include explanations
        - Keep title short
        
        DESCRIPTION RULES:
        - The description MUST include extra context, requirements, and notes
        - Include anything that is NOT the main task action
        - Include items, ingredients, reminders, or context
        - Do NOT repeat the title
        - If there are no extra details, use empty string ""
        
        DATE AND TIME RULES:
        - If date is missing, use today's date in ISO format yyyy-MM-dd (always required) 
            CURRENT SYSTEM DATE: %s
        - If time is missing, use now in HH:mm (always required).
            CURRENT SYSTEM TIME: %s
        
        - DO NOT wrap output in markdown
        - DO NOT use ``` or ```json
        - Output raw JSON only
        
        CATEGORY RULES:
        - Allowed built-in categories: WORK, GROCERY, PERSONAL, SCHOOL, MEDICAL, SOCIAL, FITNESS
        
        If category is NOT mentioned:
        - infer sensible defaults
        - never return null
        - If user intent does NOT match built-in categories, use custom category
        
        CUSTOM CATEGORY FORMAT:
        {
            "type": "custom",
            "value": "gym"
        }
        
        BUILT-IN CATEGORY FORMAT:
        {
            "type": "builtin",
            "value": "WORK"
        }
        
        PRIORITY:
        - HIGH, MEDIUM, LOW
        - Infer from urgency words (urgent = HIGH, normal = MEDIUM, optional = LOW)
        
        JSON/OUTPUT FORMAT:
        {
          "title": "",
          "date": "",
          "time": "",
          "category": {
            "type": "builtin | custom",
            "value": ""
          },
          "priority": ""
        }
        
        """.formatted(today, now);

        String enhancedMessage = prompt + "\n\nUser input:\n" + message;

        CognitiRequest cognitiRequest =
                new CognitiRequest(enhancedMessage, agent, conversationID);

        cognitiRequest.addChatHistory(chatHistory);

        String jsonBody = cognitiRequest.getJsonBody();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/v1/chat/message"
                ))
                .header("Authorization", "Bearer " + this.token)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<InputStream> response =
                client.send(request, HttpResponse.BodyHandlers.ofInputStream());

        try (BufferedReader reader =
                     new BufferedReader(
                             new InputStreamReader(response.body(), StandardCharsets.UTF_8))) {

            lastResponse = "";
            String line;
            while ((line = reader.readLine()) != null) {

                if (line.isBlank()) {
                    continue; // skip keep-alives
                }
                CognitiResponse decoded = CognitiResponse.fromJson(line);
                lastResponse = lastResponse + decoded.getContent();
            }

            // only store raw input, not AI output
            chatHistory.add(new ChatTurn(message, ""));
        }

        return lastResponse;
    }
}