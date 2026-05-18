package au.edu.qut.cogniti;

import com.google.gson.Gson;

public class CognitiResponse {
    private int id;
    private String agent_id;
    private String chat_box_autofocus;
    private String conversation_id;
    private String prompt_length_limit;
    private String content;


    public int getId() {
        return id;
    }

    public String getAgent_id() {
        return agent_id;
    }

    public String getChat_box_autofocus() {
        return chat_box_autofocus;
    }

    public String getConversation_id() {
        return conversation_id;
    }

    public String getPrompt_length_limit() {
        return prompt_length_limit;
    }

    public static CognitiResponse fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, CognitiResponse.class);

    }


    public String getContent() {
        return content;
    }
}
