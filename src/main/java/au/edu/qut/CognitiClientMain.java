package au.edu.qut;

import au.edu.qut.cogniti.CognitiConversation;
import au.edu.qut.cogniti.Secrets;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class CognitiClientMain {

    //AI prompt
    private static final String AGENT_ID = "6a046b7d369faae92bfcd391";

    private static final String BEARER_TOKEN = Secrets.getBearerToken();

    public static void main(String[] args) throws Exception {
        CognitiConversation conversation = CognitiConversation.initialise(AGENT_ID, BEARER_TOKEN);

        while (true) {
            System.out.print("Welcome to TaskTopia! How can I help you? "); //AI Greeting
            System.out.flush();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(System.in, StandardCharsets.UTF_8)
            );

            String prompt = reader.readLine();
            String response = conversation.sendMessage(prompt);
            System.out.println("Response: " + response); //AI Response
        }
    }
}
