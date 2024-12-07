import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.*;
import java.io.*;

/**
 * Team Project Phase 1 - CoHabit
 * <p>
 * This program works to implement a roommate search algorithm
 *
 * @author Aidan Lefort, Andrew Tang, Keya Jadhav, Rithvik Siddenki, Rui Meng
 * @version November 3rd, 2024
 */

public class Database implements DatabaseFramework, Searchable {
    private static ArrayList<User> allUsers = new ArrayList<>();
    private static HashMap<User, ArrayList<User>> allFriends = new HashMap<>();
    private static HashMap<User, ArrayList<String>> allMessages = new HashMap<>();
    private static HashMap<User, ArrayList<User>> allBlocked = new HashMap<>();
    private static HashMap<User, ArrayList<User>> allFriendRequests = new HashMap<>();

    private static final String USERS_FILE = "users.txt";
    private static final String FRIENDS_FILE = "friends.txt";
    private static final String MESSAGES_FILE = "messages.txt";
    private static final String BLOCKED_FILE = "blocked.txt";
    private static final String FRIEND_REQUESTS_FILE = "friend_requests.txt";
    private static final String PROFILE_PICTURE_FOLDER = "profile_pictures";

    private static final String DELIMITER = "<<END>>";

    private static final Object LOCK = new Object();

    // Constructor to initialize the Database object
    public Database() {
        synchronized (LOCK) {
            this.allUsers = new ArrayList<>();
        }
    }

    // Centralized method to read data from a file
    private synchronized ArrayList<String> readFile(String file) {
        ArrayList<String> lines = new ArrayList<>();
        File targetFile = new File(file);

        // Check if the file exists; create it if not
        if (!targetFile.exists()) {
            try {
                if (targetFile.createNewFile()) {
                    System.out.println("File created: " + file);
                }
            } catch (IOException e) {
                System.out.println("Error creating file: " + file + " - " + e.getMessage());
            }
        }

        // Read the file contents
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + file + " - " + e.getMessage());
        }
        return lines;
    }


    // Centralized method to write data to a file
    private synchronized void writeFile(String file, ArrayList<String> data) {
        File targetFile = new File(file);

        // Ensure the file exists before writing
        try {
            if (!targetFile.exists() && targetFile.createNewFile()) {
                System.out.println("File created: " + file);
            }
        } catch (IOException e) {
            System.out.println("Error creating file: " + file + " - " + e.getMessage());
            return; // Exit the method if the file could not be created
        }

        // Write the data to the file
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(file))) {
            for (String line : data) {
                writer.println(line);
            }
        } catch (IOException e) {
            System.out.println("Error writing to file: " + file + " - " + e.getMessage());
        }
    }


    // Initialization
    public void initializeDatabase() throws UsernameTakenException, InvalidInput {
        loadUsers();
        loadFriends();
        loadBlocked();
        loadFriendRequests();
        loadMessages();
    }

    // load methods for internal class use
    private synchronized void loadUsers() throws InvalidInput, UsernameTakenException {
        allUsers.clear(); // Clear existing data
        ArrayList<String> lines = readFile(USERS_FILE);

        for (String line : lines) {
            String[] tokens = line.split(DELIMITER);
            if (tokens.length != 12) continue; // Ensure correct number of fields

            String name = tokens[0];
            String password = tokens[1];
            String email = tokens[2];
            String phoneNumber = tokens[3];
            String description = tokens[4];
            String university = tokens[5];

            String bedtime = tokens[6];
            boolean alcohol = Boolean.parseBoolean(tokens[7]);
            boolean smoke = Boolean.parseBoolean(tokens[8]);
            boolean guests = Boolean.parseBoolean(tokens[9]);
            int tidy = Integer.parseInt(tokens[10]);
            int roomHours = Integer.parseInt(tokens[11]);

            User user = new User(name, password, email, phoneNumber, description, university);
            user.setPreferences(bedtime, alcohol, smoke, guests, tidy, roomHours);
            allUsers.add(user);
        }
    }

    private synchronized void loadFriends() {
        allFriends.clear(); // Clear existing data
        ArrayList<String> lines = readFile(FRIENDS_FILE);

        for (String line : lines) {
            String[] tokens = line.split(":");
            if (tokens.length != 2) continue;

            User user = findUserByName(tokens[0]);
            if (user == null) continue;

            String[] friendNames = tokens[1].split(",");
            ArrayList<User> friends = new ArrayList<>();
            for (String friendName : friendNames) {
                User friend = findUserByName(friendName.trim());
                if (friend != null) {
                    friends.add(friend);
                }
            }
            allFriends.put(user, friends);
        }
    }

    private synchronized void loadBlocked() {
        allBlocked.clear(); // Clear existing data
        ArrayList<String> lines = readFile(BLOCKED_FILE);

        for (String line : lines) {
            String[] tokens = line.split(":");
            if (tokens.length != 2) continue;

            User user = findUserByName(tokens[0]);
            if (user == null) continue;

            String[] blockedNames = tokens[1].split(",");
            ArrayList<User> blocked = new ArrayList<>();
            for (String blockedName : blockedNames) {
                User blockedUser = findUserByName(blockedName.trim());
                if (blockedUser != null) {
                    blocked.add(blockedUser);
                }
            }
            allBlocked.put(user, blocked);
        }
    }

    private synchronized void loadFriendRequests() {
        allFriendRequests.clear(); // Clear existing data
        ArrayList<String> lines = readFile(FRIEND_REQUESTS_FILE);

        for (String line : lines) {
            String[] tokens = line.split(":");
            if (tokens.length != 2) continue;

            User receiver = findUserByName(tokens[0]);
            if (receiver == null) continue;

            String[] requesterNames = tokens[1].split(",");
            ArrayList<User> requesters = new ArrayList<>();
            for (String requesterName : requesterNames) {
                User requester = findUserByName(requesterName.trim());
                if (requester != null) {
                    requesters.add(requester);
                }
            }
            allFriendRequests.put(receiver, requesters);
        }
    }

    private synchronized void loadMessages() {
        allMessages.clear(); // Clear existing data
        ArrayList<String> lines = readFile(MESSAGES_FILE);

        for (String line : lines) {
            String[] tokens = line.split(":");
            if (tokens.length != 2) continue; // Ensure format is correct

            String[] participants = tokens[0].split(",");
            if (participants.length != 2) continue; // Ensure there are exactly two participants

            String senderName = participants[0].trim();
            String receiverName = participants[1].trim();

            User sender = findUserByName(senderName);
            User receiver = findUserByName(receiverName);

            if (sender == null || receiver == null) continue; // Skip if users are invalid

            // Messages are split by the pipe delimiter
            String[] messages = tokens[1].split("\\|");

            for (String message : messages) {
                String formattedMessage = sender.getName() + " -> " + receiver.getName() + ": " + message.trim();
                allMessages.computeIfAbsent(sender, k -> new ArrayList<>()).add(formattedMessage);
                allMessages.computeIfAbsent(receiver, k -> new ArrayList<>()).add(formattedMessage); // Store for both users
            }
        }
    }

    private synchronized void saveUsers() {
        ArrayList<String> lines = new ArrayList<>();
        for (User user : allUsers) {
            lines.add(user.toString());
        }
        writeFile(USERS_FILE, lines);
    }

    private synchronized void saveFriends() {
        ArrayList<String> lines = new ArrayList<>();
        for (Map.Entry<User, ArrayList<User>> entry : allFriends.entrySet()) {
            User user = entry.getKey();
            ArrayList<User> friends = entry.getValue();
            List<String> friendNames = new ArrayList<>();
            for (User friend : friends) {
                friendNames.add(friend.getName());
            }
            lines.add(user.getName() + ":" + String.join(",", friendNames));
        }
        writeFile(FRIENDS_FILE, lines);
    }

    private synchronized void saveBlocked() {
        ArrayList<String> lines = new ArrayList<>();
        for (Map.Entry<User, ArrayList<User>> entry : allBlocked.entrySet()) {
            User user = entry.getKey();
            ArrayList<User> blockedUsers = entry.getValue();
            List<String> blockedNames = new ArrayList<>();
            for (User blocked : blockedUsers) {
                blockedNames.add(blocked.getName());
            }
            lines.add(user.getName() + ":" + String.join(",", blockedNames));
        }
        writeFile(BLOCKED_FILE, lines);
    }

    private synchronized void saveFriendRequests() {
        ArrayList<String> lines = new ArrayList<>();
        for (Map.Entry<User, ArrayList<User>> entry : allFriendRequests.entrySet()) {
            User receiver = entry.getKey();
            ArrayList<User> requesters = entry.getValue();
            List<String> requesterNames = new ArrayList<>();
            for (User requester : requesters) {
                requesterNames.add(requester.getName());
            }
            lines.add(receiver.getName() + ":" + String.join(",", requesterNames));
        }
        writeFile(FRIEND_REQUESTS_FILE, lines);
    }

    public synchronized void saveMessages() {
        ArrayList<String> lines = new ArrayList<>();
        HashSet<String> processedPairs = new HashSet<>();

        for (Map.Entry<User, ArrayList<String>> entry : allMessages.entrySet()) {
            User sender = entry.getKey();
            ArrayList<String> messages = entry.getValue();

            for (String message : messages) {
                // Extract receiver and check pair uniqueness
                String[] parts = message.split(" -> ");
                if (parts.length < 2) continue;

                String receiverName = parts[1].split(":")[0].trim();
                User receiver = findUserByName(receiverName);

                if (receiver != null) {
                    String pairKey = sender.getName() + "," + receiver.getName();
                    if (!processedPairs.contains(pairKey)) {
                        processedPairs.add(pairKey);

                        // Collect all messages for this pair
                        ArrayList<String> pairMessages = new ArrayList<>();
                        for (String msg : messages) {
                            if (msg.contains(sender.getName() + " -> " + receiver.getName()) 
                                || msg.contains(receiver.getName() + " -> " + sender.getName())) {
                                pairMessages.add(msg.split(":")[1].trim());
                            }
                        }

                        // Write the pair and their messages
                        lines.add(sender.getName() + "," + receiver.getName() + ":" + String.join("|", pairMessages));
                    }
                }
            }
        }
        writeFile(MESSAGES_FILE, lines);
    }

    // Adds a user to the database if the username is not already taken
    // Returns true if successful, false otherwise
    public synchronized boolean addUser(User user) {
        if (user == null || user.getName() == null || user.getEmail() == null 
            || user.getPhoneNumber() == null || user.getPassword() == null 
            || user.getUniversity() == null || user.getBedTime() == null 
            || user.getDescription() == null || usernameExists(user.getName())) {
            return false;
        }

        allUsers.add(user);
        saveUsers(); // changes immediately
        return true;
    }

    public synchronized boolean addFriend(User user1, User user2) {
        if (user1 == null || user2 == null) {
            System.out.println("Invalid users provided.");
            return false;
        }

        // Check if they are already friends
        ArrayList<User> user1Friends = allFriends.getOrDefault(user1, new ArrayList<>());
        if (user1Friends.contains(user2)) {
            System.out.println("Users are already friends.");
            return false; // Return false because they are already friends
        }

        // Add each other as friends
        user1Friends.add(user2);
        allFriends.put(user1, user1Friends);

        ArrayList<User> user2Friends = allFriends.getOrDefault(user2, new ArrayList<>());
        user2Friends.add(user1);
        allFriends.put(user2, user2Friends);

        saveFriends(); // Save immediately to file
        System.out.println("Friendship added between: " + user1.getName() + " and " + user2.getName());
        return true;
    }


    public synchronized boolean blockUser(User blocker, User blocked) {
        if (blocker == null || blocked == null) {
            System.out.println("Invalid users provided.");
            return false;
        }

        // Check if the user is already blocked
        ArrayList<User> blockedUsers = allBlocked.getOrDefault(blocker, new ArrayList<>());
        if (blockedUsers.contains(blocked)) {
            System.out.println(blocker.getName() + " has already blocked " + blocked.getName());
            return false; // Return false because the user is already blocked
        }

        // Remove from friends list if they are friends
        removeFriend(blocker, blocked);

        // Add to the blocked list
        blockedUsers.add(blocked);
        allBlocked.put(blocker, blockedUsers);

        saveBlocked(); // Save immediately to file
        saveFriends(); // Save updated friends list
        System.out.println(blocker.getName() + " has blocked " + blocked.getName());
        return true;
    }

    public synchronized boolean sendFriendRequest(User sender, User receiver) {
        if (sender == null || receiver == null) {
            System.out.println("Invalid users provided.");
            return false;
        }

        // Check if either user has blocked the other
        ArrayList<User> senderBlockedUsers = allBlocked.getOrDefault(sender, new ArrayList<>());
        ArrayList<User> receiverBlockedUsers = allBlocked.getOrDefault(receiver, new ArrayList<>());

        if (senderBlockedUsers.contains(receiver) || receiverBlockedUsers.contains(sender)) {
            return false; // Cannot send a friend request if either user is blocked
        }

        // Check if the users are already friends
        ArrayList<User> senderFriends = allFriends.getOrDefault(sender, new ArrayList<>());
        if (senderFriends.contains(receiver)) {
            return false; // Return false because they are already friends
        }

        // Check if a friend request already exists
        ArrayList<User> requests = allFriendRequests.getOrDefault(receiver, new ArrayList<>());
        if (requests.contains(sender)) {
            return false; // Return false because the friend request already exists
        }

        // Add the friend request
        requests.add(sender);
        allFriendRequests.put(receiver, requests);

        saveFriendRequests(); // Save immediately to file
        return true;
    }

    public synchronized boolean sendMessage(User sender, User receiver, String message) {
        if (sender == null || receiver == null || message == null || message.isEmpty()) {
            System.out.println("Invalid input provided.");
            return false;
        }

        String formattedMessage = sender.getName() + " -> " + receiver.getName() + ": " + message;

        // Store the message only under the sender's entry
        allMessages.computeIfAbsent(sender, k -> new ArrayList<>()).add(formattedMessage);

        saveMessages(); // Save immediately to file
        System.out.println("Message sent: " + formattedMessage);
        return true;
    }

    public synchronized boolean removeUser(User user) {
        if (user == null || !allUsers.remove(user)) {
            System.out.println("User does not exist.");
            return false;
        }

        // Remove from friends, blocks, friend requests, and messages
        allFriends.remove(user);
        allBlocked.remove(user);
        allFriendRequests.remove(user);
        allMessages.remove(user);

        // Remove user from others' data
        for (ArrayList<User> friends : allFriends.values()) {
            friends.remove(user);
        }
        for (ArrayList<User> blocked : allBlocked.values()) {
            blocked.remove(user);
        }
        for (ArrayList<User> requests : allFriendRequests.values()) {
            requests.remove(user);
        }

        saveUsers();
        saveFriends();
        saveBlocked();
        saveFriendRequests();
        saveMessages();
        System.out.println("User removed: " + user.getName());
        return true;
    }

    public synchronized boolean removeFriend(User user1, User user2) {
        if (user1 == null || user2 == null) {
            System.out.println("Invalid users provided.");
            return false;
        }

        // Remove each other from friends list
        allFriends.getOrDefault(user1, new ArrayList<>()).remove(user2);
        allFriends.getOrDefault(user2, new ArrayList<>()).remove(user1);

        saveFriends(); // Save immediately to file
        System.out.println("Friendship removed between: " + user1.getName() + " and " + user2.getName());
        return true;
    }

    public synchronized boolean unblockUser(User blocker, User unblocked) {
        if (blocker == null || unblocked == null) {
            System.out.println("Invalid users provided.");
            return false;
        }

        // Get the list of blocked users for the blocker
        ArrayList<User> blockedUsers = allBlocked.getOrDefault(blocker, new ArrayList<>());

        // Check if the unblocked user exists in the blocked list
        if (!blockedUsers.contains(unblocked)) {
            System.out.println(unblocked.getName() + " is not in the blocked list of " 
                               + blocker.getName());
            return false; // Return false because the user is not blocked
        }

        // Remove the unblocked user from the blocker's blocked list
        blockedUsers.remove(unblocked);

        // Update the map with the modified blocked list
        allBlocked.put(blocker, blockedUsers);

        saveBlocked(); // Save immediately to file
        System.out.println(blocker.getName() + " has unblocked " + unblocked.getName());
        return true;
    }

    public synchronized boolean acceptFriendRequest(User receiver, User sender) {
        if (receiver == null || sender == null) {
            System.out.println("Invalid users provided.");
            return false;
        }

        // Check if the sender is in the receiver's friend request list
        ArrayList<User> requests = allFriendRequests.getOrDefault(receiver, new ArrayList<>());
        if (!requests.contains(sender)) {
            System.out.println("No friend request from " + sender.getName() + " to accept.");
            return false;
        }

        // Add each other as friends
        boolean addedAsFriend = addFriend(receiver, sender);
        if (!addedAsFriend) {
            System.out.println("Failed to add " + sender.getName() 
                               + " as a friend to " + receiver.getName());
            return false;
        }

        // Remove the sender from the receiver's friend requests
        requests.remove(sender);
        if (requests.isEmpty()) {
            allFriendRequests.remove(receiver);
        } else {
            allFriendRequests.put(receiver, requests);
        }

        saveFriendRequests(); // Persist updated friend requests
        saveFriends(); // Persist updated friends list
        return true;
    }

    public synchronized boolean rejectFriendRequest(User receiver, User sender) {
        if (receiver == null || sender == null) {
            System.out.println("Invalid users provided.");
            return false;
        }

        ArrayList<User> requests = allFriendRequests.getOrDefault(receiver, new ArrayList<>());
        if (requests.remove(sender)) {
            saveFriendRequests(); // Save updated friend requests
        }
        return true;
    }

    public synchronized ArrayList<String> getFriends(User user) {
        if (user == null) {
            System.out.println("Invalid user.");
            return new ArrayList<>(); // Return an empty list
        }

        ArrayList<User> friends = allFriends.getOrDefault(user, new ArrayList<>());
        ArrayList<String> friendNames = new ArrayList<>();

        for (User friend : friends) {
            friendNames.add(friend.getName());
        }

        return friendNames;
    }

    public synchronized ArrayList<String> getBlockedUsers(User user) {
        if (user == null) {
            return new ArrayList<>(); // Return an empty list
        }

        ArrayList<User> blockedUsers = allBlocked.getOrDefault(user, new ArrayList<>());
        ArrayList<String> blockedUserNames = new ArrayList<>();

        for (User blocked : blockedUsers) {
            blockedUserNames.add(blocked.getName());
        }

        return blockedUserNames;
    }

    public synchronized ArrayList<String> getMessage(User user1, User user2) {
        if (user1 == null || user2 == null) {
            return new ArrayList<>();
        }

        Set<String> uniqueMessages = new LinkedHashSet<>();

        // Collect messages involving both users, ensuring no duplicates
        ArrayList<String> user1Messages = allMessages.getOrDefault(user1, new ArrayList<>());
        ArrayList<String> user2Messages = allMessages.getOrDefault(user2, new ArrayList<>());

        for (String message : user1Messages) {
            if (message.contains(user1.getName() + " -> " + user2.getName()) 
                || message.contains(user2.getName() + " -> " + user1.getName())) {
                uniqueMessages.add(message);
            }
        }

        for (String message : user2Messages) {
            if (message.contains(user1.getName() + " -> " + user2.getName()) 
                || message.contains(user2.getName() + " -> " + user1.getName())) {
                uniqueMessages.add(message);
            }
        }

        return new ArrayList<>(uniqueMessages);
    }

    public synchronized ArrayList<String> getFriendRequests(User user) {
        if (user == null) {
            return new ArrayList<>(); // Return an empty list
        }

        ArrayList<User> friendRequests = allFriendRequests.getOrDefault(user, new ArrayList<>());
        ArrayList<String> friendRequestNames = new ArrayList<>();

        for (User requester : friendRequests) {
            friendRequestNames.add(requester.getName());
        }

        return friendRequestNames;
    }

    public synchronized boolean updateUserInFile(User updatedUser, String oldUsername) {
        if (updatedUser == null || oldUsername == null || oldUsername.isEmpty()) {
            return false;
        }

        // Replace the old user entry with the updated one
        for (int i = 0; i < allUsers.size(); i++) {
            if (allUsers.get(i).getName().equals(updatedUser.getName())) {
                allUsers.set(i, updatedUser); // Update the in-memory user
                saveUsers(); // Persist changes to the file
                return true;
            }
        }

        return false;
    }

    // Checks if a username already exists in the database
    public synchronized boolean usernameExists(String username) {
        if (username == null || username.isEmpty()) {
            return false;
        }

        for (User user : allUsers) {
            if (user.getName().equals(username)) {
                return true;
            }
        }
        return false;
    }

    // Finds and returns a user by their username
    // Returns null if no match is found
    public synchronized User findUserByName(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }

        for (User user : allUsers) {
            if (user.getName().equals(name)) {
                return user;
            }
        }
        return null; // User not found
    }

    public synchronized void saveProfilePicture(User user, byte[] profilePicture) {
        if (user == null || profilePicture == null) {
            return;
        }

        File profilePictureFile = new File(PROFILE_PICTURE_FOLDER, user.getName() + ".png");

        try {
            // Ensure the folder exists
            File folder = new File(PROFILE_PICTURE_FOLDER);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            try (FileOutputStream fos = new FileOutputStream(profilePictureFile)) {
                fos.write(profilePicture);
            }

        } catch (IOException e) {
            System.out.println("Error saving profile picture: " + e.getMessage());
        }
    }

    public synchronized byte[] loadProfilePicture(User user) {
        if (user == null) {
            return null;
        }

        File profilePictureFile = new File(PROFILE_PICTURE_FOLDER, user.getName() + ".png");

        if (!profilePictureFile.exists()) {
            return null;
        }

        try (FileInputStream fis = new FileInputStream(profilePictureFile)) {
            return fis.readAllBytes();
        } catch (IOException e) {
            return null;
        }
    }

    public synchronized void deleteProfilePicture(User user) {
        if (user == null) {
            return;
        }

        File profilePictureFile = new File(PROFILE_PICTURE_FOLDER, user.getName() + ".png");

        if (profilePictureFile.exists()) {
            if (profilePictureFile.delete()) {
                System.out.println("Profile picture deleted for user: " + user.getName());
            } else {
                System.out.println("Failed to delete profile picture for user: " + user.getName());
            }
        } else {
            System.out.println("No profile picture found for user: " + user.getName());
        }
    }


    // Searches users based on a specified parameter and value
    public synchronized String searchByParameter(String parameter, String value, String delimiter) {
        if (parameter == null || value == null || value.isEmpty()) {
            return null; // Return an empty string
        }

        String result = "";

        for (User user : allUsers) {
            switch (parameter.toLowerCase()) {
                case "name":
                    if (value.equals(user.getName())) {
                        result += user.getName() + delimiter;
                    }
                    break;
                case "email":
                    if (value.equals(user.getEmail())) {
                        result += user.getName() + delimiter;
                    }
                    break;
                case "phone":
                    if (value.equals(user.getPhoneNumber())) {
                        result += user.getName() + delimiter;
                    }
                    break;
                case "university":
                    if (value.equals(user.getUniversity())) {
                        result += user.getName() + delimiter;
                    }
                    break;
                default:
                    break;
            }
        }

        // Remove the last delimiter if matches were found
        if (!result.isEmpty()) {
            result = result.substring(0, result.length() - delimiter.length());
        }

        return result; // Return the list as a delimited string
    }


    public synchronized String exactMatch(User user, String delimiter) {
        if (user == null) {
            System.err.println("Main user cannot be null.");
            return null; // Return an empty string
        }

        String result = "";

        for (User user1 : allUsers) {
            if (!user.getName().equals(user1.getName()) && user.perfectMatch(user1)) {
                result += user1.getName() + delimiter;
            }
        }

        // Remove the last delimiter if matches were found
        if (!result.isEmpty()) {
            result = result.substring(0, result.length() - delimiter.length());
        }

        return result; // Return the list as a delimited string
    }

    public synchronized String partialMatch(User user, String delimiter) {
        if (user == null) {
            System.out.println("Main user cannot be null.");
            return null; // Return an empty string
        }

        ArrayList<User> matchedUsers = new ArrayList<>();
        ArrayList<Integer> matchScores = new ArrayList<>();

        // Calculate match scores for each user
        for (User user1 : allUsers) {
            if (!user.getName().equals(user1.getName())) {
                int score = user.partialMatch(user1); // Assuming this method exists in User
                if (score > 0) {
                    matchedUsers.add(user1);
                    matchScores.add(score);
                }
            }
        }

        // Sort the matched users by scores (descending order)
        for (int i = 0; i < matchScores.size() - 1; i++) {
            for (int j = i + 1; j < matchScores.size(); j++) {
                if (matchScores.get(i) < matchScores.get(j)) {
                    // Swap scores
                    int tempScore = matchScores.get(i);
                    matchScores.set(i, matchScores.get(j));
                    matchScores.set(j, tempScore);

                    // Swap corresponding users
                    User tempUser = matchedUsers.get(i);
                    matchedUsers.set(i, matchedUsers.get(j));
                    matchedUsers.set(j, tempUser);
                }
            }
        }

        // Build the result string
        String result = "";
        for (int i = 0; i < matchedUsers.size(); i++) {
            double percentage = (matchScores.get(i) * 100.0) / 6;
            result += matchedUsers.get(i).getName() + " (" 
                + String.format("%.2f", percentage) + "% match)" + delimiter;
        }

        // Remove trailing delimiter
        if (!result.isEmpty()) {
            result = result.substring(0, result.length() - delimiter.length());
        }

        return result; // Return the list as a delimited string
    }
}
