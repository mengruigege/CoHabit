import java.util.*;

/**
 * Team Project Phase 2 - CoHabit
 * <p>
 * This program works to implement a roommate search algorithm
 *
 * @author Aidan Lefort, Andrew Tang, Keya Jadhav, Rithvik Siddenki, Rui Meng
 * @version November 3rd, 2024
 */

public interface ServerService {
    void start(int port);                                  // Start the server

    void stop();                                           // Stop the server

    boolean authenticateUser(String username, String password); // Authenticate user credentials

    boolean registerUser(User user);                            // Register a new user

    boolean sendMessage(String sender, String receiver, String message);  // Send a message

    ArrayList<String> getMessages(String user1, String user2);            // Retrieve message history

    boolean addFriend(String user1, String user2);                // Add a friend

    boolean sendFriendRequest(String sender, String receiver);    // Send a friend request

    boolean respondToFriendRequest(String sender, String receiver, boolean accept); // Respond to request

    ArrayList<String> getFriendRequests(String username);         // Retrieve pending friend requests

    boolean blockUser(String requester, String target);           // Block a user

    User getUserProfile(String username);                         // Get user profile

    boolean updateUserProfile(String username, User updatedProfile); // Update profile
}
