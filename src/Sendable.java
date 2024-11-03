import java.util.*;

public interface Sendable {
    boolean sendMessage(User sender, User receiver, String message);
    boolean deleteMessage(User sender, User receiver, String message);
    ArrayList<String> getMessages();
}
