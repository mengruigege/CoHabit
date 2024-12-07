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
    // Verifies login credentials and returns user data on success
    String login(String username, String password);

    // Registers a new user and adds them to the database
    boolean register(User user);

    // Sends a message from one user to another
    boolean sendMessage(User sender, User receiver, String message);

    // Retrieves message history between two users
    ArrayList<String> getMessageHistory(User user1, User user2);

    // Sends a friend request from one user to another
    boolean sendFriendRequest(User sender, User receiver);

    // Retrieves pending friend requests for a user
    ArrayList<String> viewFriendRequests(User user);

    // Declines a friend request from a specific user
    boolean declineFriendRequest(User receiver, User sender);

    // Accepts a friend request and adds the sender to the friend list
    boolean acceptFriendRequest(User receiver, User sender);

    // Removes a friend from the user's friend list
    boolean removeFriend(User remover, User removed);

    // Blocks a specific user
    boolean blockUser(User blocker, User blocked);

    // Unblocks a previously blocked user
    boolean unblockUser(User unblocker, User unblocked);

    // Retrieves the list of blocked users for a user
    ArrayList<String> viewBlockedUsers(User user);

    // Retrieves the list of friends for a user
    ArrayList<String> viewFriendsList(User user);

    // Returns the profile information of a specific user
    String viewProfile(String username);

    // Retrieves users with partial preference matches
    String partialMatch(User user);

    // Retrieves users with exact preference matches
    String exactMatch(User user);

    // Searches users based on a specific parameter and value
    String searchByParameter(String parameter, String value);
}
