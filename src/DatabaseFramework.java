import java.io.IOException;
import java.util.ArrayList;

/**
 * Team Project Phase 2 - CoHabit
 * <p>
 * This program works to implement a roommate search algorithm
 *
 * @author Aidan Lefort, Andrew Tang, Keya Jadhav, Rithvik Siddenki, Rui Meng
 * @version November 3rd, 2024
 */

public interface DatabaseFramework {

    // User management
    boolean addUser(User user);
    boolean deleteUser(User user);
    boolean usernameExists(String username);
    User findUserByName(String name);
    ArrayList<User> getAllUsers();

    // Friend management
    boolean addFriend(User user1, User user2);
    boolean removeFriend(User user1, User user2);
    ArrayList<User> loadFriendsFromFile();

    // Block management
    ArrayList<User> loadBlockedFromFile();
    void saveBlockedToFile();

    // Friend requests
    void addFriendRequest(User sender, User receiver);
    ArrayList<String> loadFriendRequestsFromFile();

    // Profile picture management
    void saveProfilePicture(User user, byte[] profilePicture);
    byte[] loadProfilePicture(User user);
    void deleteProfilePicture(User user);

    // Message management
    void recordMessages(String sender, String receiver, String message);
    ArrayList<String> loadConversation(String user1, String user2);

    // File handling for users
    void loadUsersFromFile();
    void saveUsersToFile();
}
