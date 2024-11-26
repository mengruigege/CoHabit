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
    private synchronized ArrayList<String> readFile(String filePath) {
        ArrayList<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + filePath + " - " + e.getMessage());
        }
        return lines;
    }

    // Centralized method to write data to a file
    private synchronized void writeFile(String filePath, ArrayList<String> data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String line : data) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing to file: " + filePath + " - " + e.getMessage());
        }
    }

    // Centralized method to append data to a file
    private synchronized void appendToFile(String filePath, String data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(data);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error appending to file: " + filePath + " - " + e.getMessage());
        }
    }

    // load methods for internal class use
    private synchronized void loadUsers() throws InvalidInput, UsernameTakenException {
        allUsers.clear(); // Clear existing data
        List<String> lines = readFile(USERS_FILE);

        for (String line : lines) {
            String[] data = line.split(",");
            if (data.length != 12) continue; // Ensure correct number of fields

            String name = data[0];
            String password = data[1];
            String email = data[2];
            String phoneNumber = data[3];
            String description = data[4];
            String university = data[5];

            String bedtime = data[6];
            boolean alcohol = Boolean.parseBoolean(data[7]);
            boolean smoke = Boolean.parseBoolean(data[8]);
            boolean guests = Boolean.parseBoolean(data[9]);
            int tidy = Integer.parseInt(data[10]);
            int roomHours = Integer.parseInt(data[11]);

            User user = new User(name, password, email, phoneNumber, description, university);
            user.setPreferences(bedtime, alcohol, smoke, guests, tidy, roomHours);
            allUsers.add(user);
        }
    }

    private synchronized void loadFriends() {
        allFriends.clear(); // Clear existing data
        List<String> lines = readFile(FRIENDS_FILE);

        for (String line : lines) {
            String[] data = line.split(":");
            if (data.length != 2) continue;

            User user = findUserByName(data[0]);
            if (user == null) continue;

            String[] friendNames = data[1].split(",");
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
            String[] data = line.split(":");
            if (data.length != 2) continue;

            User user = findUserByName(data[0]);
            if (user == null) continue;

            String[] blockedNames = data[1].split(",");
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
            String[] data = line.split(":");
            if (data.length != 2) continue;

            User receiver = findUserByName(data[0]);
            if (receiver == null) continue;

            String[] requesterNames = data[1].split(",");
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
            String[] data = line.split(":");
            if (data.length != 2) continue;

            User user = findUserByName(data[0]);
            if (user == null) continue;

            ArrayList<String> messages = new ArrayList<>(Arrays.asList(data[1].split("\\|")));
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
        if (user != null && !usernameExists(user.getName())) {
            allUsers.add(user);
            return true;
        }
        return false; // User is null or username is taken
    }

    // Removes a user from the database
    // Returns true if successful, false otherwise
    public boolean deleteUser(User user) {
        boolean removed;
        synchronized (LOCK) {
            removed = allUsers.remove(user);
        }
        if (removed) {
            saveUsersToFile();
            return true;
        }
        return removed;
    }

    // Checks if a username already exists in the database
    public synchronized boolean usernameExists(String username) {
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
        for (User user : allUsers) {
            if (user.getName().equals(name)) {
                return user;
            }
        }
        return null; // User not found
    }

    // Retrieves the list of all users
    public ArrayList<User> getAllUsers() {
        synchronized (LOCK) {
            return allUsers;
        }
    }

    // Adds two users as friends
    // Updates both users' friend lists and appends the friendship to a file
    public synchronized boolean addFriend(User user1, User user2) {
        if (user1 == null || user2 == null) {
            System.out.println("Either user1 or user2 is null. Cannot add friends.");
            return false;
        }

        // Check if they are already friends
        for (User friend : user1.getFriendList()) {
            if (friend.getName().equals(user2.getName())) {
                System.out.println(user2.getName() + " is already a friend of " + user1.getName());
                return false;
            }
        }

        // Add each other to their respective friend lists
        user1.addFriend(user2);
        user2.addFriend(user1);

        try (PrintWriter pw = new PrintWriter(new FileOutputStream(FRIENDS_FILE, true))) {
            pw.println(user1.getName() + ":" + user2.getName());
            pw.println(user2.getName() + ":" + user1.getName());
            System.out.println("Friends added successfully: " + user1.getName() + " and " + user2.getName());
        } catch (IOException e) {
            System.out.println("Error adding friends to file: " + e.getMessage());
            return false;
        }

        return true; // Friendship added successfully
    }

    // Removes a friendship between two users
    
    public synchronized boolean removeFriend(User user1, User user2) {
        if (user1 == null || user2 == null) {
            return false;
        }

        boolean removed = false;
        ArrayList<String> updatedFriendsFile = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FRIENDS_FILE))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String currentUser = parts[0].trim();
                    String[] friendList = parts[1].split(",");
                    ArrayList<String> updatedFriends = new ArrayList<>();

                    if (currentUser.equals(user1.getName())) {
                        // Remove user2 from user1's friend list
                        for (String friend : friendList) {
                            if (!friend.trim().equals(user2.getName())) {
                                updatedFriends.add(friend.trim());
                            } else {
                                removed = true;
                            }
                        }
                        if (!updatedFriends.isEmpty()) {
                            updatedFriendsFile.add(currentUser + ":" + String.join(",", updatedFriends));
                        }
                    } else if (currentUser.equals(user2.getName())) {
                        // Remove user1 from user2's friend list
                        for (String friend : friendList) {
                            if (!friend.trim().equals(user1.getName())) {
                                updatedFriends.add(friend.trim());
                            } else {
                                removed = true;
                            }
                        }
                        if (!updatedFriends.isEmpty()) {
                            updatedFriendsFile.add(currentUser + ":" + String.join(",", updatedFriends));
                        }
                    } else {
                        // Copy other users' data as is
                        updatedFriendsFile.add(line);
                    }
                } else if (parts.length == 1) {
                    // If there's no colon and it's just a user with no friends, skip this line
                    if (!parts[0].trim().equals(user1.getName()) && !parts[0].trim().equals(user2.getName())) {
                        updatedFriendsFile.add(line);
                    } else {
                        removed = true;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading friends file: " + e.getMessage());
            return false;
        }

        // Rewrite the file with updated data
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(FRIENDS_FILE))) {
            for (String updatedLine : updatedFriendsFile) {
                pw.println(updatedLine);
            }
        } catch (IOException e) {
            System.out.println("Error writing friends file: " + e.getMessage());
            return false;
        }

        return removed;
    }

    // Records a friend request from one user to another in the file
    public synchronized void addFriendRequest(User sender, User receiver) {
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(FRIEND_REQUESTS_FILE, true))) {
            pw.println(sender.getName() + ":" + receiver.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Removes a specific friend request from the file
    public synchronized void removeFriendRequest(User sender, User receiver) {
        ArrayList<String> friendRequests = new ArrayList<>();

        // Read the current friend requests and filter out the specified request
        try (BufferedReader br = new BufferedReader(new FileReader(FRIEND_REQUESTS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(":");
                if (tokens.length == 3) {
                    String currentSender = tokens[0];
                    String currentReceiver = tokens[1];

                    // Only keep the request if it doesn't match the sender and receiver
                    if (!(currentSender.equals(sender.getName()) && currentReceiver.equals(receiver.getName()))) {
                        friendRequests.add(line);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Rewrite the file with the updated list of friendRequests
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(FRIEND_REQUESTS_FILE))) {
            for (String request : friendRequests) {
                pw.println(request);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Retrieves a list of users blocked by the specified username
    public synchronized ArrayList<String> getBlockedUsers(String username) {
        if (username == null || username.isEmpty()) {
            return new ArrayList<>(); // Return an empty list if the username is invalid
        }

        ArrayList<String> blockedUsers = new ArrayList<>();

        // Read the blocked file and collect users blocked by the given username
        try (BufferedReader br = new BufferedReader(new FileReader(BLOCKED_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String currentUser = parts[0].trim();
                    if (currentUser.equals(username)) {
                        String blockedUser = parts[1];
                        blockedUsers.add(blockedUser);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading blocked file: " + e.getMessage());
        }
        return blockedUsers; // Return the list of blocked users
    }

    // Blocks a user by adding them to the blocker's block list and removing any existing friendship between them

    public synchronized boolean blockUser(String blockerName, String blockedName) {
        if (blockerName == null || blockedName == null || blockerName.isEmpty() || blockedName.isEmpty()) {
            return false;
        }

        boolean isBlockedAdded = false;
        ArrayList<String> updatedBlockedFile = new ArrayList<>();
        ArrayList<String> updatedFriendsFile = new ArrayList<>();

        try {
            // Update the blocked.txt file
            try (BufferedReader br = new BufferedReader(new FileReader(BLOCKED_FILE))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        String currentUser = parts[0].trim();
                        String[] blockedList = parts[1].split(",");
                        ArrayList<String> updatedBlocked = new ArrayList<>(Arrays.asList(blockedList));

                        if (currentUser.equals(blockerName)) {
                            if (!updatedBlocked.contains(blockedName)) {
                                updatedBlocked.add(blockedName);
                                isBlockedAdded = true;
                            }
                            updatedBlockedFile.add(currentUser + ":" + String.join(",", updatedBlocked));
                        } else {
                            updatedBlockedFile.add(line);
                        }
                    } else if (parts[0].trim().equals(blockerName)) {
                        updatedBlockedFile.add(blockerName + ":" + blockedName);
                        isBlockedAdded = true;
                    } else {
                        updatedBlockedFile.add(line);
                    }
                }

                // If the blocker is not found in the file, add them with the blocked user
                if (!isBlockedAdded) {
                    updatedBlockedFile.add(blockerName + ":" + blockedName);
                    isBlockedAdded = true;
                }
            }

            // Rewrite the blocked.txt file
            try (PrintWriter pw = new PrintWriter(new FileOutputStream(BLOCKED_FILE))) {
                for (String updatedLine : updatedBlockedFile) {
                    pw.println(updatedLine);
                }
            }

            // Update the friends.txt file
            try (BufferedReader br = new BufferedReader(new FileReader(FRIENDS_FILE))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        String currentUser = parts[0].trim();
                        String[] friendsList = parts[1].split(",");
                        ArrayList<String> updatedFriends = new ArrayList<>(Arrays.asList(friendsList));

                        if (currentUser.equals(blockerName)) {
                            updatedFriends.remove(blockedName);
                            if (updatedFriends.isEmpty()) {
                                continue; // Skip writing this line if no friends are left
                            }
                            updatedFriendsFile.add(currentUser + ":" + String.join(",", updatedFriends));
                        } else if (currentUser.equals(blockedName)) {
                            updatedFriends.remove(blockerName);
                            if (updatedFriends.isEmpty()) {
                                continue; // Skip writing this line if no friends are left
                            }
                            updatedFriendsFile.add(currentUser + ":" + String.join(",", updatedFriends));
                        } else {
                            updatedFriendsFile.add(line);
                        }
                    } else {
                        updatedFriendsFile.add(line);
                    }
                }
            }

            // Rewrite the friends.txt file
            try (PrintWriter pw = new PrintWriter(new FileOutputStream(FRIENDS_FILE))) {
                for (String updatedLine : updatedFriendsFile) {
                    pw.println(updatedLine);
                }
            }

        } catch (IOException e) {
            System.out.println("Error handling files: " + e.getMessage());
            return false;
        }

        return isBlockedAdded;
    }

    // Unblocks a user by removing them from the blocker's block list 
    public synchronized boolean unblockUser(String blockerName, String unblockedName) {
        if (blockerName == null || unblockedName == null || blockerName.isEmpty() || unblockedName.isEmpty()) {
            return false;
        }

        boolean isUnblocked = false;
        ArrayList<String> updatedBlockedFile = new ArrayList<>();

        try {
            // Update the blocked.txt file
            try (BufferedReader br = new BufferedReader(new FileReader(BLOCKED_FILE))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        String currentUser = parts[0].trim();
                        String[] blockedList = parts[1].split(",");
                        ArrayList<String> updatedBlocked = new ArrayList<>(Arrays.asList(blockedList));

                        if (currentUser.equals(blockerName)) {
                            // Remove the unblocked user from the blocker's list
                            if (updatedBlocked.contains(unblockedName)) {
                                updatedBlocked.remove(unblockedName);
                                isUnblocked = true;
                            }

                            // Only add the blocker to the updated file if they still have blocked users
                            if (!updatedBlocked.isEmpty()) {
                                updatedBlockedFile.add(currentUser + ":" + String.join(",", updatedBlocked));
                            }
                        } else {
                            updatedBlockedFile.add(line);
                        }
                    } else {
                        updatedBlockedFile.add(line); // Add lines without blocked users
                    }
                }
            }

            // Rewrite the blocked.txt file
            try (PrintWriter pw = new PrintWriter(new FileOutputStream(BLOCKED_FILE))) {
                for (String updatedLine : updatedBlockedFile) {
                    pw.println(updatedLine);
                }
            }

        } catch (IOException e) {
            System.out.println("Error handling files: " + e.getMessage());
            return false;
        }

        return isUnblocked;
    }

    // Saves all users to the USERS_FILE
    public synchronized void saveUsersToFile() {
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(USERS_FILE, true))) {
            for (User user : allUsers) {
                pw.println(user); // Write user information
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Retrieves a user's friends from the FRIENDS_FILE
    public synchronized ArrayList<String> getFriendsFromFile(String username) {
        ArrayList<String> friends = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FRIENDS_FILE))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String currentUser = parts[0].trim();
                    String[] friendList = parts[1].split(",");

                    // If this is the user we are looking for, populate their friends list
                    if (currentUser.equals(username)) {
                        for (String friend : friendList) {
                            if (!friend.trim().isEmpty()) {
                                friends.add(friend.trim());
                            }
                        }
                        break; // We found the user; no need to keep reading
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading friends from file: " + e.getMessage());
        }

        return friends;
    }


    // Loads blocked users from the BLOCKED_FILE
    public synchronized ArrayList<User> loadBlockedFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(BLOCKED_FILE))) {
            String line;
            ArrayList<User> blockedList = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(":");
                if (tokens.length == 2) {
                    String username = tokens[0];
                    String[] blocked = tokens[1].split(",");

                    User user = findUserByName(username);

                    if (user != null) {
                        for (String block : blocked) {
                            User blockedUser = findUserByName(block);
                            if (blockedUser != null) {
                                blockedList.add(blockedUser);
                            }
                        }
                    }
                }
            }
            return blockedList;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Loads all pending friend requests from the file
    public synchronized ArrayList<String> loadFriendRequestsFromFile() {
        ArrayList<String> friendRequests = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FRIEND_REQUESTS_FILE))) {
            String line ;
            while ((line = br.readLine()) != null) {
                friendRequests.add(line); // Load each request as a line in the list
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return friendRequests;
    }

    // Records a message exchanged between two users in the MESSAGES_FILE
    public synchronized void recordMessages(String sender, String receiver, String message) {
         // Format the message log: sender,receiver,message
        String log = String.format("%s,%s,%s", sender, receiver, message);
        try (PrintWriter pr = new PrintWriter(new FileOutputStream(MESSAGES_FILE, true))) {
            pr.println(log);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Loads the conversation history between two users
    public synchronized ArrayList<String> loadConversation(String user1, String user2) {
        ArrayList<String> messages = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(MESSAGES_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",", 3);
                if (tokens.length == 3) {
                    String sender = tokens[0];
                    String receiver = tokens[1];

                    if ((sender.equals(user1) && receiver.equals(user2))
                            || (sender.equals(user2) && receiver.equals(user1))) {
                        messages.add(tokens[2]);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return messages; // Return the list of conversation messages
    }

    public synchronized void saveProfilePicture(User user, byte[] profilePicture) {
        if (user == null || profilePicture == null) return;

        File profilePictureFile = new File(PROFILE_PICTURE_FOLDER, user.getName() + ".png");
        try (FileOutputStream fos = new FileOutputStream(profilePictureFile)) {
            fos.write(profilePicture);
            System.out.println("Profile picture saved for user: " + user.getName());
        } catch (IOException e) {
            System.out.println("Error saving profile picture: " + e.getMessage());
        }
    }


    // Loads the profile picture of a user from a file
    public synchronized byte[] loadProfilePicture(User user) {
        File profilePictureFile = new File(PROFILE_PICTURE_FOLDER, user.getName() + ".png");
        if (!profilePictureFile.exists()) return null;

        try (FileInputStream fis = new FileInputStream(profilePictureFile)) {
            return fis.readAllBytes();
        } catch (IOException e) {
            System.out.println("Error loading profile picture: " + e.getMessage());
            return null;
        }
    }


    // Deletes the profile picture of a user
    public void deleteProfilePicture(User user) {
        File pictureFile = new File(PROFILE_PICTURE_FOLDER, user.getName() + ".png");
        if (pictureFile.exists()) {
            pictureFile.delete(); // Delete the profile picture file if it exists
        }
    }

    // Searches users based on a specified parameter and value
    public synchronized ArrayList<User> searchByParameter(String parameter, String value) {
        loadUsersFromFile();
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
    public synchronized ArrayList<User> exactMatch(User mainUser) {
        if (mainUser == null) {
            System.err.println("Main user cannot be null.");
            return new ArrayList<>();
        }

        loadUsersFromFile();
        ArrayList<User> results = new ArrayList<>();

        for (User user : allUsers) {
            if (mainUser.perfectMatch(user) && !mainUser.getName().equals(user.getName())) {
                results.add(user);
            }
        }

        return results; // Return the list of exact matches
    }

    // Finds users who partially match with the main user
    public synchronized ArrayList<User> partialMatch(User mainUser) throws UsernameTakenException, InvalidInput {
        if (mainUser == null) {
            System.out.println("Main user cannot be null.");
            return new ArrayList<>();
        }

        loadUsersFromFile();
        ArrayList<User> results = new ArrayList<>();

        // Iterate by decreasing match scores for prioritized results
        for (int score = 5; score > 0; score--) {
            for (User user : allUsers) {
                if (!mainUser.getName().equals(user.getName()) && mainUser.partialMatch(user) == score) {
                    results.add(user);
                }
            }
        }
        return results;
    }
}
