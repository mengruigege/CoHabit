
/**
 * Team Project Phase 2 - CoHabit
 * <p>
 * This program works to implement a roommate search algorithm
 *
 * @author Aidan Lefort, Andrew Tang, Keya Jadhav, Rithvik Siddenki, Rui Meng
 * @version November 3rd, 2024
 */

public interface ClientService {
    // Initiates the client application and connects to the server
    void start();

    // Closes the connection to the server and releases resources
    void close();

    // Displays the main dashboard for the client
    boolean mainScreen();

    // Attempts to log in the user with the given credentials
    boolean login();

    // Registers a new user with the provided details
    boolean register();

    // Sends a message to another user
    boolean sendMessage();

    // Retrieves pending friend requests for the logged-in user
    void viewFriendRequests();

    // Sends a friend request to another user
    void sendFriendRequest();

    // Removes a user from the friend list
    void removeFriend();

    // Blocks a specific user
    void blockUser();

    // Unblocks a previously blocked user
    void unblockUser();

    // Displays the profile of the logged-in user or another user
    void viewProfile();

    // Updates the profile details of the logged-in user
    void updateProfile();

    // Searches for roommates based on specific criteria
    void searchRoommates();

    // Disconnects the client from the server
    void disconnect();

    // Accepts a pending friend request
    boolean acceptFriendRequest(String friend);

    // Declines a pending friend request
    boolean declineFriendRequest(String sender);

    // Searches users based on a specific parameter and value
    void searchByParameter(String parameter, String value);

    // Retrieves users with exact preference matches
    void exactMatch();

    // Retrieves users with partial preference matches
    void partialMatch();

    // Uploads a profile picture for the logged-in user
    boolean setProfilePicture(String filePath);
}
