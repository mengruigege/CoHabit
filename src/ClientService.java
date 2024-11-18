import java.util.*;

public interface ClientService {
    boolean connect(String serverAddress, int port);          // Connect to server

    void disconnect();                                        // Disconnect

    boolean login(String username, String password);          // User login

    boolean register(User user) throws UsernameTakenException;                              // Register a new user

    boolean sendMessage(String receiver, String message);     // Send a message

    String fetchMessages(String user, String receiver);             // Fetch message history

    boolean addFriend(String user, String friend);                       // Add friend directly
//    boolean sendFriendRequest(String username);               // Send friend request
//    boolean respondToFriendRequest(String sender, boolean accept); // Respond to friend request
//    ArrayList<String> getFriendRequests();                    // Retrieve pending friend requests

    void viewProfile(String username);                        // View user profile

    boolean updateProfile(User updatedProfile);               // Update profile
}
