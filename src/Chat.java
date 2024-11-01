import java.util.ArrayList;

public class Chat implements Message {
    private User sender;
    private User reciever;
    private ArrayList<String> messages;

    public Chat(User sender, User reciever, ArrayList<String> messageLogs) {
        this.sender = sender;
        this.reciever = reciever;
        this.messages = new ArrayList<>();
    }

    public boolean sendMessage(User sender, User reciever, String message) {
        messages.add(message);
        return true;
    }

    public ArrayList<String> getMessages() {
        return messages;
    }

    public boolean deleteMessage(User sender, User reciever, String message) {
        messages.remove(message);
        return true;
    }

}
