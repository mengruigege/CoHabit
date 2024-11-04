import java.util.*;

/**
 * Team Project Phase 1 - CoHabit
 * <p>
 * This program works to implement a roommate search algorithm
 *
 * @author Aidan Lefort, Andrew Tang, Keya Jadhav, Rithvik Siddenki, Rui Meng
 * @version November 3rd, 2024
 */

public interface Sendable {
    boolean sendMessage(User sender, User receiver, String message); //to send messages

    boolean deleteMessage(User sender, User receiver, String message); //to delete messages

    ArrayList<String> getMessages(); //to receive messages
}
