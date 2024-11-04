import java.util.*;

public interface Sendable {
    boolean sendMessage(User sender, User receiver, String message); //to send messages
    boolean deleteMessage(User sender, User receiver, String message); //to delete messages
    ArrayList<String> getMessages(); //to receive messages
}
