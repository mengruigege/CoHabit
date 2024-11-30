import java.util.*;

/**
 * Team Project Phase 2 - CoHabit
 * <p>
 * This program works to implement a roommate search algorithm
 *
 * @author Aidan Lefort, Andrew Tang, Keya Jadhav, Rithvik Siddenki, Rui Meng
 * @version November 3rd, 2024
 */

import java.io.IOException;

public interface ClientService {
    // Connection management
    void start();
    void close();
    void disconnect();
    boolean isConnected();

    // User authentication and profile management
    boolean login();
    boolean register();
    void updateProfile();
    void viewProfile();
    void viewProfile(String usernameInput);

    // Messaging
    boolean sendMessage(String receiver, String message);
    void viewMessage();
    void viewMessage(String receiverUsername);

    // Friend management
    void viewFriendRequests();
    void sendFriendRequest();
    boolean sendFriendRequest(String user, String potentialFriend);
    boolean acceptFriendRequest(String friend);
    boolean declineFriendRequest(String usernameInput);
    boolean addFriend(String user, String friend);
    void removeFriend();
    boolean removeFriend(String user, String friend);

    // Blocking management
    void blockUser();
    boolean blockUser(String user, String blockedUser);
    void unblockUser();
    boolean unblockUser(String user, String blockedUser);
    void viewBlockedUsers(String usernameInput);

    // Search functionality
    void searchRoommates();
    void searchByParameter(String parameter, String value);
    void exactMatch();
    void partialMatch();

    // Preferences and settings
    void setPreferences(String bedTimeInput, boolean alcoholInput, boolean smokeInput,
                        boolean guestsInput, int tidyInput, int roomHoursInput);

    // File and profile picture management
    boolean setProfilePicture(String filePath);

    // User-specific operations
    String getUsername();
    void setUsername(String usernameInput);
    String getPassword();
    void setPassword(String passwordInput);
    String getEmail();
    void setEmail(String emailInput);
    String getPhone();
    void setPhone(String phoneNumberInput);
    String getUniversity();
    void setUniversity(String universityInput);
    String getUserDescription();
    void setUserDescription(String userDescriptionInput);
    void setUserInformation() throws IOException;
    void setUserRegisterInformation(String usernameInput, String passwordInput,
                                    String emailInput, String phoneNumberInput,
                                    String userDescriptionInput, String universityInput);
}

