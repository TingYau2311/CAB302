package au.edu.qut.cogniti;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
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
//        String jsonBody = """
//            {
//                "agent_id": "%s",
//                "lti_course_id": null
//            }
//            """.formatted(agent);
//
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

    public String sendMessage(String message) throws IOException, InterruptedException {

        CognitiRequest cognitiRequest = new CognitiRequest(message, agent, conversationID);
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

            chatHistory.add(new ChatTurn(message, lastResponse));
        }

        return lastResponse;
    }


//    public void uploadFile(String filename)
//            throws IOException, InterruptedException {
//
//        if (conversationID == null) {
//            throw new IllegalStateException("Conversation not initialised");
//        }
//
//        File file = new File(filename);
//        if (!file.exists() || !file.isFile()) {
//            throw new IllegalArgumentException("File does not exist: " + filename);
//        }
//
//        String boundary = "----JavaBoundary" + UUID.randomUUID();
//        String LINE_FEED = "\r\n";
//
//        String url = BASE_URL
//                + "/api/v1/chat/agents/"
//                + agent
//                + "/conversations/"
//                + conversationID
//                + "/attachments/";
//
//        ByteArrayOutputStream body = new ByteArrayOutputStream();
//
//        // ---- File part
//        body.write(("--" + boundary + LINE_FEED).getBytes(StandardCharsets.UTF_8));
//        body.write((
//                "Content-Disposition: form-data; name=\"file\"; filename=\""
//                        + file.getName() + "\"" + LINE_FEED
//        ).getBytes(StandardCharsets.UTF_8));
//
//        String mimeType = Files.probeContentType(file.toPath());
//        if (mimeType == null) {
//            mimeType = "application/octet-stream";
//        }
//
//        body.write(("Content-Type: " + mimeType + LINE_FEED + LINE_FEED)
//                .getBytes(StandardCharsets.UTF_8));
//
//        try (FileInputStream fis = new FileInputStream(file)) {
//            fis.transferTo(body);
//        }
//
//        body.write(LINE_FEED.getBytes(StandardCharsets.UTF_8));
//
//        // ---- End boundary
//        body.write(("--" + boundary + "--" + LINE_FEED)
//                .getBytes(StandardCharsets.UTF_8));
//
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create(url))
//                .header("Authorization", "Bearer " + token)
//                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
//                .POST(HttpRequest.BodyPublishers.ofByteArray(body.toByteArray()))
//                .build();
//
//        HttpClient client = HttpClient.newHttpClient();
//        HttpResponse<String> response =
//                client.send(request, HttpResponse.BodyHandlers.ofString());
//
//        if (response.statusCode() != 201) {
//            throw new IOException(
//                    "Attachment upload failed (HTTP "
//                            + response.statusCode() + "): "
//                            + response.body()
//            );
//        }
//    }

}

