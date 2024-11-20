import java.util.*;

/**
 * Team Project Phase 2 - CoHabit
 * <p>
 * This program works to implement a roommate search algorithm
 *
 * @author Aidan Lefort, Andrew Tang, Keya Jadhav, Rithvik Siddenki, Rui Meng
 * @version November 3rd, 2024
 */

public interface ClientService {
    boolean connect(String serverAddress, int port);          // Connect to server

    void disconnect();                                        // Disconnect

    boolean login(String username, String password);          // User login

    boolean register() throws UsernameTakenException;                              // Register a new user

    boolean sendMessage(String receiver, String message);     // Send a message

    String fetchMessages(String user, String receiver);             // Fetch message history

    boolean addFriend(String user, String friend);                       // Add friend directly
//    boolean sendFriendRequest(String username);               // Send friend request
//    boolean respondToFriendRequest(String sender, boolean accept); // Respond to friend request
//    ArrayList<String> getFriendRequests();                    // Retrieve pending friend requests

    void viewProfile(String username);                        // View user profile

    boolean updateProfile(String oldUsername);               // Update profile
}
