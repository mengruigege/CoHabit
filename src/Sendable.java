import java.util.*;

public interface Sendable {
    boolean sendMessage(User sender, User receiver, String message);
    boolean deleteMessage(String content);
    ArrayList<String> getMessages();
}
