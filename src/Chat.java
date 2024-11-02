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

    public synchronized boolean sendMessage(User sender, User reciever, String message) { 
        //to make sure only one thread can send a message at a time
        messages.add(message);
        return true;
    }

    public synchronized ArrayList<String> getMessages() {
        //to make sure only one thread can get a message at a time
        return messages; //return new ArrayList<>(messages); should we put in this instead? it will make a copy of the text to be returned.
    }

    public synchronized boolean deleteMessage(User sender, User reciever, String message) {
        messages.remove(message);
        return true;
    }

}
