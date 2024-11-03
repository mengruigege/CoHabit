import java.util.*;

public interface Message {
    User getSender();
    User getReceiver();
    String getContent();
    String getTimestamp();
    public boolean sendMessage(User sender, User receiver); //method for sending messages
    public ArrayList<String> getMessageLogs(); //need to implement some way of storing messages between two users; Chat object?
    public void setMessageLogs(ArrayList<String> messages); //set method for list of messages
    public boolean deleteMessage(String message); //removes a certain message from list of messages
}
