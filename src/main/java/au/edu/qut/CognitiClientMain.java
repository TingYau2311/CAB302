package au.edu.qut;

import au.edu.qut.cogniti.CognitiConversation;
import au.edu.qut.cogniti.Secrets;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class CognitiClientMain {

    // TRY ANY OF THESE AGENTS
    //    private static final String AGENT_ID = "69f2ab2bac7d1d1e0d0b608b"; //CAB302 Question Generator Production
    private static final String AGENT_ID = "6a046b7d369faae92bfcd391"; //Brisbane Urban Explorer

    private static final String BEARER_TOKEN = Secrets.getBearerToken();

    public static void main(String[] args) throws Exception {
        CognitiConversation conversation = CognitiConversation.initialise(AGENT_ID, BEARER_TOKEN);

        while (true) {
            System.out.print("Enter prompt: ");
            System.out.flush();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(System.in, StandardCharsets.UTF_8)
            );

            String prompt = reader.readLine();
            String response = conversation.sendMessage(prompt);
            System.out.println("Response: " + response);
        }
    }
}
