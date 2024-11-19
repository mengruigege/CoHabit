import java.io.IOException;
import java.util.ArrayList;

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
    void saveProfilePicture(User user);
    void loadProfilePicture(User user);
    void deleteProfilePicture(User user);

    // Message management
    void recordMessages(String sender, String receiver, String message);
    ArrayList<String> loadConversation(String user1, String user2);

    // File handling for users
    void loadUsersFromFile();
    void saveUsersToFile();
}
