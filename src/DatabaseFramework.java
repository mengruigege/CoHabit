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
   // User Management
    boolean addUser(User user);  // Adds a new user to the database
    boolean removeUser(User user);  // Removes a user from the database
    boolean usernameExists(String username);  // Checks if a username already exists
    User findUserByName(String name);  // Finds a user by their name

    // Friendship Management
    boolean addFriend(User user1, User user2);  // Adds a friend relationship between two users
    boolean removeFriend(User user1, User user2);  // Removes the friendship between two users

    // Block Management
    boolean blockUser(User blocker, User blocked);  // Blocks a user
    boolean unblockUser(User blocker, User unblocked);  // Unblocks a user

    // Friend Request Management
    boolean sendFriendRequest(User sender, User receiver);  // Sends a friend request from one user to another
    boolean acceptFriendRequest(User receiver, User sender);  // Rejects a pending friend request
    boolean rejectFriendRequest(User receiver, User sender);  // Rejects a pending friend request

    // Messaging Management
    boolean sendMessage(User sender, User receiver, String message);

    // Profile Picture Management
    void saveProfilePicture(User user, byte[] profilePicture);  // Saves a user's profile picture
    byte[] loadProfilePicture(User user);  // Loads a user's profile picture
    void deleteProfilePicture(User user);  // Deletes a user's profile picture

    // Search and Matching
    ArrayList<User> searchByParameter(String parameter, String value) throws UsernameTakenException, InvalidInput;  // Searches for users based on a parameter and value
    ArrayList<User> exactMatch(User user);  // Finds users who exactly match a given user's attributes
    ArrayList<User> partialMatch(User user);  // Finds users who partially match a given user's attributes
}
