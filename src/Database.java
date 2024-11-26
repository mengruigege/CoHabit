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

public class Database implements DatabaseFramework {
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
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(file))) {
            for (String line : data) {
                writer.println(line);
            }
        } catch (IOException e) {
            System.out.println("Error writing to file: " + file + " - " + e.getMessage());
        }
    }

    // Centralized method to append data to a file
    private synchronized void appendToFile(String file, String data) {
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(file, true))) {
            writer.println(data);
        } catch (IOException e) {
            System.out.println("Error appending to file: " + file + " - " + e.getMessage());
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
            String[] tokens = line.split(",");
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
        List<String> lines = readFile(BLOCKED_FILE);

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
        List<String> lines = readFile(FRIEND_REQUESTS_FILE);

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
        List<String> lines = readFile(MESSAGES_FILE);

        for (String line : lines) {
            String[] tokens = line.split(":");
            if (tokens.length != 2) continue;

            User user = findUserByName(tokens[0]);
            if (user == null) continue;

            ArrayList<String> messages = new ArrayList<>(Arrays.asList(tokens[1].split("\\|")));
            allMessages.put(user, messages);
        }
    }

    private synchronized void saveUsers() {
        ArrayList<String> lines = new ArrayList<>();
        for (User user : allUsers) {
            lines.add(user.toString()); // Assuming User has a proper toString method
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
        for (Map.Entry<User, ArrayList<String>> entry : allMessages.entrySet()) {
            User user = entry.getKey();
            ArrayList<String> messages = entry.getValue();
            lines.add(user.getName() + ":" + String.join("|", messages));
        }
        writeFile(MESSAGES_FILE, lines);
    }

    // Adds a user to the database if the username is not already taken
    // Returns true if successful, false otherwise
    public synchronized boolean addUser(User user) {
        if (user == null || usernameExists(user.getName())) {
            System.out.println("User is null or username already exists.");
            return false;
        }

        allUsers.add(user);
        saveUsers(); // changes immediately
        return true;
    }

    public synchronized void addFriend(User user1, User user2) {
        if (user1 == null || user2 == null) {
            System.out.println("Invalid users provided.");
            return;
        }

        // Add each other as friends
        allFriends.computeIfAbsent(user1, k -> new ArrayList<>()).add(user2);
        allFriends.computeIfAbsent(user2, k -> new ArrayList<>()).add(user1);

        saveFriends(); // Save immediately to file
        System.out.println("Friendship added between: " + user1.getName() + " and " + user2.getName());
    }

    public synchronized void blockUser(User blocker, User blocked) {
        if (blocker == null || blocked == null) {
            System.out.println("Invalid users provided.");
            return;
        }

        allBlocked.computeIfAbsent(blocker, k -> new ArrayList<>()).add(blocked);

        saveBlocked(); // Save immediately to file
        System.out.println(blocker.getName() + " has blocked " + blocked.getName());
    }


    public synchronized void sendFriendRequest(User sender, User receiver) {
        if (sender == null || receiver == null) {
            System.out.println("Invalid users provided.");
            return;
        }

        allFriendRequests.computeIfAbsent(receiver, k -> new ArrayList<>()).add(sender);

        saveFriendRequests(); // Save immediately to file
        System.out.println(sender.getName() + " sent a friend request to " + receiver.getName());
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

    public synchronized void removeFriend(User user1, User user2) {
        if (user1 == null || user2 == null) {
            System.out.println("Invalid users provided.");
            return;
        }

        // Remove each other from friends list
        allFriends.getOrDefault(user1, new ArrayList<>()).remove(user2);
        allFriends.getOrDefault(user2, new ArrayList<>()).remove(user1);

        saveFriends(); // Save immediately to file
        System.out.println("Friendship removed between: " + user1.getName() + " and " + user2.getName());
    }

    public synchronized void unblockUser(User blocker, User unblocked) {
        if (blocker == null || unblocked == null) {
            System.out.println("Invalid users provided.");
            return;
        }

        allBlocked.getOrDefault(blocker, new ArrayList<>()).remove(unblocked);

        saveBlocked(); // Save immediately to file
        System.out.println(blocker.getName() + " has unblocked " + unblocked.getName());
    }

    public synchronized void rejectFriendRequest(User receiver, User sender) {
        if (receiver == null || sender == null) {
            System.out.println("Invalid users provided.");
            return;
        }

        ArrayList<User> requests = allFriendRequests.getOrDefault(receiver, new ArrayList<>());
        if (requests.remove(sender)) {
            saveFriendRequests(); // Save updated friend requests
            System.out.println(receiver.getName() + " rejected a friend request from " + sender.getName());
        } else {
            System.out.println("No friend request from " + sender.getName() + " to reject.");
        }
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
            System.out.println("Invalid user or profile picture data.");
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
                System.out.println("Profile picture saved for user: " + user.getName());
            }
        } catch (IOException e) {
            System.out.println("Error saving profile picture: " + e.getMessage());
        }
    }

    public synchronized byte[] loadProfilePicture(User user) {
        if (user == null) {
            System.out.println("Invalid user.");
            return null;
        }

        File profilePictureFile = new File(PROFILE_PICTURE_FOLDER, user.getName() + ".png");

        if (!profilePictureFile.exists()) {
            System.out.println("Profile picture not found for user: " + user.getName());
            return null;
        }

        try (FileInputStream fis = new FileInputStream(profilePictureFile)) {
            return fis.readAllBytes();
        } catch (IOException e) {
            System.out.println("Error loading profile picture: " + e.getMessage());
            return null;
        }
    }

    public synchronized void deleteProfilePicture(User user) {
        if (user == null) {
            System.out.println("Invalid user.");
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
    public synchronized ArrayList<User> searchByParameter(String parameter, String value) throws UsernameTakenException, InvalidInput {
        ArrayList<User> matchingUsers = new ArrayList<>();

        // Null and edge-case handling
        if (parameter == null || value == null || value.isEmpty()) {
            System.out.println("Invalid parameter or value for search.");
            return matchingUsers; // Return empty list
        }

        for (User user : allUsers) {
            switch (parameter.toLowerCase()) {
                case "name":
                    if (value.equals(user.getName())) {
                        matchingUsers.add(user);
                    }
                    break;
                case "email":
                    if (value.equals(user.getEmail())) {
                        matchingUsers.add(user);
                    }
                    break;
                case "phone":
                    if (value.equals(user.getPhoneNumber())) {
                        matchingUsers.add(user);
                    }
                    break;
                case "university":
                    if (value.equals(user.getUniversity())) {
                        matchingUsers.add(user);
                    }
                    break;
                default:
                    System.out.println("Invalid parameter type for search: " + parameter);
                    break;
            }
        }

        return matchingUsers; // Return the list of matching users
    }

    // Finds users who are an exact match with the main user
    public synchronized ArrayList<User> exactMatch(User user) {
        ArrayList<User> results = new ArrayList<>();

        if (user == null) {
            System.err.println("Main user cannot be null.");
            return results; // Return empty list
        }

        for (User user1 : allUsers) {
            if (!user1.getName().equals(user1.getName()) && user1.perfectMatch(user1)) {
                results.add(user1);
            }
        }

        return results; // Return the list of exact matches
    }


    public synchronized ArrayList<User> partialMatch(User user) {
        ArrayList<User> results = new ArrayList<>();

        if (user == null) {
            System.out.println("Main user cannot be null.");
            return results; // Return empty list
        }

        TreeMap<Integer, ArrayList<User>> rankedMatches = new TreeMap<>(Collections.reverseOrder());

        for (User user1 : allUsers) {
            if (!user1.getName().equals(user1.getName())) {
                int score = user1.partialMatch(user1); // Assuming this method exists in User
                if (score > 0) {
                    rankedMatches.putIfAbsent(score, new ArrayList<>());
                    rankedMatches.get(score).add(user1);
                }
            }
        }

        for (ArrayList<User> matchGroup : rankedMatches.values()) {
            results.addAll(matchGroup); // Add users sorted by score
        }

        return results;
    }
}
