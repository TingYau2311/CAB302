package au.edu.qut.cogniti;

public class ChatTurn {
    public final String userSays;
    public final String aiSays;

    public ChatTurn(String user, String ai) {
        this.userSays = user;
        this.aiSays = ai;
    }
}
