import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Base64;

/** 
 * This document was created for CS 180 PJ 5 Phase 2
 *
 * @version 1.0
 * @author Andrew tang, Aidan Lefort, Keya Jadhav, Rithvik Siddenki, Rui Meng
 */

public class Server implements ServerService, Runnable {
    static Database database = new Database();
    private static final String DELIMITER = "<<END>>";
    private static final String SUCCESS = "SUCCESS";
    private static final String FAILURE = "FAILURE";
    private final Socket clientSocket;

    public Server(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {
            while (true) {
                String line;
                try {
                    line = reader.readLine();
                    if (line == null) break;

                    // Receives message from client in the format "login<<END>>username<<END>>password"
                    if (line.startsWith("login")) {
                        String[] parts = line.split(DELIMITER);
                        String username = parts[1];
                        String password = parts[2];

                        String userInformation = login(username, password);
                        if (userInformation != null && !userInformation.isEmpty()) {
                            writer.println(userInformation); // Send user info

                            // Load and send profile picture
                            User user = database.findUserByName(username);
                            byte[] profilePicture = database.loadProfilePicture(user);

                            if (profilePicture != null) {
                                String encodedPicture = Base64.getEncoder().encodeToString(profilePicture);
                                writer.println(encodedPicture); // Send Base64-encoded picture
                            } else {
                                writer.println("NO_PICTURE"); // No picture found
                            }
                        } else {
                            writer.println(FAILURE); // Login failed
                        }
                    }


                    // receives message from client in the format
                    if (line.startsWith("register")) {
                        String[] parts = line.split(DELIMITER);
                        try {
                            User user = new User(parts[1], parts[2], parts[3], parts[4], parts[5], parts[6]);
                            boolean alcohol = Boolean.parseBoolean(parts[8]);
                            boolean smoke = Boolean.parseBoolean(parts[9]);
                            boolean guests = Boolean.parseBoolean(parts[10]);
                            int tidy = Integer.parseInt(parts[11]);
                            int roomHours = Integer.parseInt(parts[12]);
                            try {
                                user.setPreferences(parts[7], alcohol, smoke, guests, tidy, roomHours);
                            } catch (Exception e) {
                                writer.println("Invalid registration data");
                            }

                            if (register(user)) {
                                writer.println(SUCCESS);
                            } else {
                                writer.println("Username is taken or credentials are null");
                            }
                        } catch (Exception e) {
                            writer.println("Invalid registration data");
                        }

                    }

                    if (line.startsWith("sendMessage")) {
                        String[] parts = line.split(DELIMITER);

                        User sender = database.findUserByName(parts[1]);
                        User receiver = database.findUserByName(parts[2]);
                        String message = parts[3];

                        if (sendMessage(sender, receiver, message)) {
                            writer.println(SUCCESS);
                        } else {
                            writer.println(FAILURE);
                        }
                    }

                    if (line.startsWith("getMessageHistory")) {
                        String[] parts = line.split(DELIMITER);

                        String senderUsername = parts[1];
                        String receiverUsername = parts[2];

                        User sender = database.findUserByName(senderUsername);
                        User receiver = database.findUserByName(receiverUsername);
                        ArrayList<String> messages = getMessageHistory(sender, receiver);

                        if (messages == null || messages.isEmpty()) {
                            writer.println(FAILURE);
                        } else {
                            writer.println(String.join(DELIMITER, messages));
                        }
                    }

                    // receives message from client in the format sendFriendRequest,user,potentialFriend
                    if (line.startsWith("sendFriendRequest")) {
                        String[] parts = line.split(DELIMITER);

                        User sender = database.findUserByName(parts[1]);
                        User receiver = database.findUserByName(parts[2]);

                        if (sender == null || receiver == null) {
                            writer.println("One or both users do not exist");
                            continue;
                        }

                        if (database.getBlockedUsers(receiver).contains(sender.getName()) || database.getBlockedUsers(sender).contains(receiver.getName())) {
                            writer.println("Friend request cannot be sent due to a blocking relationship");
                            continue;
                        }

                        if (sendFriendRequest(sender, receiver)) {
                            writer.println(SUCCESS);
                        } else {
                            writer.println("Friend request cannot be sent. You might have already sent a friend request to this person");
                        }
                    }

                    if (line.startsWith("viewFriendRequests")) {
                        String[] parts = line.split(DELIMITER);

                        User user = database.findUserByName(parts[1]);

                        ArrayList<String> friendRequests = viewFriendRequests(user);
                        if (friendRequests == null || friendRequests.isEmpty()) {
                            writer.println(FAILURE);
                        } else {
                            writer.println(String.join(DELIMITER, viewFriendRequests(user)));
                        }
                    }

                    if (line.startsWith("declineFriendRequest")) {
                        String[] parts = line.split(DELIMITER);

                        User receiver = database.findUserByName(parts[1]);
                        User sender = database.findUserByName(parts[2]);

                        if (receiver == null || sender == null) {
                            writer.println("One or both users do not exist");
                        }

                        if (declineFriendRequest(receiver, sender)) {
                            writer.println(SUCCESS);
                        } else {
                            writer.println(FAILURE);
                        }
                    }

                    if (line.startsWith("acceptFriendRequest")) {
                        String[] parts = line.split(DELIMITER);

                        User user = database.findUserByName(parts[1]);
                        User friend = database.findUserByName(parts[2]);

                        if (user == null || friend == null) {
                            writer.println("One or both users do not exist");
                        }

                        if (acceptFriendRequest(user, friend)) {
                            writer.println(SUCCESS);
                        } else {
                            writer.println(FAILURE);
                        }
                    }

                    if (line.startsWith("removeFriend")) {
                        String[] parts = line.split(DELIMITER);

                        User user = database.findUserByName(parts[1]);
                        User friend = database.findUserByName(parts[2]);

                        if (user == null || friend == null) {
                            writer.println("One or both users do not exist");
                        }

                        if (removeFriend(user, friend)) {
                            writer.println(SUCCESS);
                        } else {
                            writer.println(FAILURE);
                        }
                    }

                    if (line.startsWith("getFriendList")) {
                        String[] parts = line.split(DELIMITER);

                        User user = database.findUserByName(parts[1]);

                        if (user == null) {
                            writer.println(FAILURE);
                            continue;
                        }

                        ArrayList<String> friendList = viewFriendsList(user);
                        if (friendList == null) {
                            writer.println(FAILURE);
                        } else {
                            writer.println(String.join(DELIMITER, friendList));
                        }
                    }

                    if (line.startsWith("getBlockList")) {
                        String[] parts = line.split(DELIMITER);

                        User user = database.findUserByName(parts[1]);

                        if (user == null) {
                            writer.println(FAILURE);
                            continue;
                        }

                        ArrayList<String> blockedUserList = viewBlockedUsers(user);
                        if (blockedUserList == null) {
                            writer.println(FAILURE);
                        } else {
                            writer.println(String.join(DELIMITER, blockedUserList));
                        }
                    }

                    if (line.startsWith("blockUser")) {
                        String[] parts = line.split(DELIMITER);

                        User user = database.findUserByName(parts[1]);
                        User blockedUser = database.findUserByName(parts[2]);

                        if (user == null || blockedUser == null) {
                            writer.println("One or both users do not exist");
                            continue;
                        }

                        if (blockUser(user, blockedUser)) {
                            writer.println(SUCCESS);
                        } else {
                            writer.println(blockedUser.getName() + "could not be blocked. They might be already blocked");
                        }
                    }

                    if (line.startsWith("removeBlockedUser")) {
                        String[] parts = line.split(DELIMITER);

                        User user = database.findUserByName(parts[1]);
                        User blockedUser = database.findUserByName(parts[2]);

                        if (user == null || blockedUser == null) {
                            writer.println("One or both users do not exist");
                            continue;
                        }

                        if (unblockUser(user, blockedUser)) {
                            writer.println(SUCCESS);
                        } else {
                            writer.println(FAILURE);
                        }

                    }

                    // receives message from client in the format viewProfile,username
                    if (line.startsWith("viewProfile")) {
                        String[] parts = line.split(DELIMITER);

                        String username = parts[1];
                        User user = database.findUserByName(username);

                        if (user != null) {
                            // Send user profile details
                            writer.println(user.toString());

                            // Load and send the profile picture
                            byte[] profilePicture = database.loadProfilePicture(user);
                            if (profilePicture != null) {
                                String encodedPicture = Base64.getEncoder().encodeToString(profilePicture);
                                writer.println(encodedPicture); // Send Base64-encoded picture
                            } else {
                                writer.println("NO_PICTURE"); // Indicate no picture available
                            }
                        } else {
                            writer.println(FAILURE);
                        }
                    }

                    if (line.startsWith("updateProfile")) {
                        String[] tokens = line.split(DELIMITER);
                        if (tokens.length != 14) {
                            writer.println(FAILURE);
                            continue;
                        }

                        String oldUsername = tokens[1]; // Old username for lookup
                        String newUsername = tokens[2]; // New username (if updated)
                        String password = tokens[3];
                        String email = tokens[4];
                        String phoneNumber = tokens[5];
                        String description = tokens[6];
                        String university = tokens[7];
                        String bedTime = tokens[8];
                        boolean alcohol = Boolean.parseBoolean(tokens[9]);
                        boolean smoke = Boolean.parseBoolean(tokens[10]);
                        boolean guests = Boolean.parseBoolean(tokens[11]);
                        int tidy;
                        int roomHours;

                        try {
                            tidy = Integer.parseInt(tokens[12]);
                            roomHours = Integer.parseInt(tokens[13]);
                        } catch (NumberFormatException e) {
                            writer.println(FAILURE);
                            continue;
                        }

                        // Lookup the user by their old username
                        User user = database.findUserByName(oldUsername);

                        if (user == null) {
                            writer.println(FAILURE);
                            continue;
                        }

                        // Update the user's profile
                        try {
                            user.setName(newUsername); // Update the username
                            user.setPassword(password);
                            user.setEmail(email);
                            user.setPhoneNumber(phoneNumber);
                            user.setDescription(description);
                            user.setUniversity(university);
                            user.setPreferences(bedTime, alcohol, smoke, guests, tidy, roomHours);

                            // Save changes to the database
                            if (database.updateUserInFile(user, oldUsername)) {
                                writer.println(SUCCESS); // Successfully updated
                            } else {
                                writer.println(FAILURE); // Failed to save changes
                            }
                        } catch (Exception e) {
                            writer.println(FAILURE);
                        }
                    }


                    // receives message from client in the format partialMatch,user
                    if (line.startsWith("partialMatch")) {
                        String[] parts = line.split(DELIMITER);

                        User user = database.findUserByName(parts[1]);
                        String result = partialMatch(user);

                        if (result == null) {
                            writer.println(FAILURE);
                        } else {
                            writer.println(result);
                        }

                    }

                    // receives message from client in the format exactMatch,user
                    if (line.startsWith("exactMatch")) {
                        String[] parts = line.split(DELIMITER);

                        User user = database.findUserByName(parts[1]);
                        String result = exactMatch(user);

                        if (result == null) {
                            writer.println(FAILURE);
                        } else {
                            writer.println(result);
                        }
                    }

                    // receives message from client in the format searchByParameter,parameter,value
                    if (line.startsWith("searchByParameter")) {
                        String[] parts = line.split(DELIMITER);

                        String parameter = parts[1];
                        String value = parts[2];
                        String result = searchByParameter(parameter, value);

                        if (result == null) {
                            writer.println(FAILURE);
                        } else {
                            writer.println(result);
                        }
                    }

                    // Receives message from client in the format uploadProfilePicture###username
                    if (line.startsWith("uploadProfilePicture")) {
                        String[] parts = line.split(DELIMITER);

                        String username = parts[1];
                        User user = database.findUserByName(username);

                        if (user == null) {
                            writer.println(FAILURE);
                            continue;
                        }

                        try {
                            int fileSize = Integer.parseInt(reader.readLine()); // Read the size of the file
                            byte[] fileBytes = new byte[fileSize];
                            clientSocket.getInputStream().read(fileBytes); // Read the file data

                            // Validate the PNG file directly
                            if (fileBytes.length < 8) {
                                writer.println("INVALID_FILE"); // File too small to be a valid PNG
                                continue;
                            }

                            // Check the PNG signature (first 8 bytes)
                            byte[] pngSignature = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
                            boolean isValid = true;
                            for (int i = 0; i < pngSignature.length; i++) {
                                if (fileBytes[i] != pngSignature[i]) {
                                    isValid = false;
                                    break;
                                }
                            }

                            if (!isValid) {
                                writer.println("INVALID_FILE"); // Not a valid PNG
                                continue;
                            }

                            database.saveProfilePicture(user, fileBytes); // Save the profile picture in the database
                            writer.println(SUCCESS);
                        } catch (IOException | NumberFormatException e) {
                            writer.println(FAILURE);
                        }
                    }
                } catch (IOException e) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws UsernameTakenException, InvalidInput {
        database.initializeDatabase();

        try (ServerSocket serverSocket = new ServerSocket(1102)) {
            System.out.println("Server started. Waiting for connection...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getRemoteSocketAddress());
                new Thread(new Server(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // method to check if the login was successful or not
    public String login(String username, String password) {
        User user = database.findUserByName(username);
        if (user == null) {
            return null;
        }

        if (user.getPassword().equals(password) && user.getName().equals(username)) {
            return user.toString();
        }
        return null;
    }

    // method used to add the new register user to the database and return true if successful
    public boolean register(User user) {
        return database.addUser(user);
    }

    //method to send messages between two users
    public boolean sendMessage(User sender, User reciever, String message) {
        return database.sendMessage(sender, reciever, message);
    }

    //method to load messages between two users
    public ArrayList<String> getMessageHistory(User user1, User user2) {
        return database.getMessage(user1, user2);
    }

    //method to send friend request to another user
    public boolean sendFriendRequest(User sender, User receiver) {
        return database.sendFriendRequest(sender, receiver);
    }

    //method to view all friend requests of a user
    public ArrayList<String> viewFriendRequests(User user) {
        return database.getFriendRequests(user);
    }

    //method to decline friend requests
    public boolean declineFriendRequest(User receiver, User sender) {
        return database.rejectFriendRequest(receiver, sender);
    }
    
    //method to accept a friend request
    public boolean acceptFriendRequest(User receiver, User sender) {
        return database.acceptFriendRequest(receiver, sender);
    }

    //method to remove a friend from friend list
    public  boolean removeFriend(User remover, User removed) {
        return database.removeFriend(remover, removed);
    }

    //method to block a user
    public  boolean blockUser(User blocker, User blocked) {
        return database.blockUser(blocker, blocked);
    }

    //method to unblock a user
    public boolean unblockUser(User unblocker, User unblocked) {
        return database.unblockUser(unblocker, unblocked);
    }

    //method to view all blocked users
    public ArrayList<String> viewBlockedUsers(User user) {
        return database.getBlockedUsers(user);
    }

    //method to view all friends of a user
    public ArrayList<String> viewFriendsList(User user) {
        return database.getFriends(user);
    }

    //method to view a user's profile
    public  String viewProfile(String username) {
        User user = database.findUserByName(username);
        if (user != null) {
            return user.toString();
        } else {
            return null;
        }
    }

    //method to check how many preferences of two users match, and return users in a descending order of matches
    public  String partialMatch(User user) {
        return database.partialMatch(user, DELIMITER);
    }

    //method to retrieve list of users who have the exact same preferences
    public String exactMatch(User user) {
        return database.exactMatch(user, DELIMITER);
    }

    //Method to search roommates by a specific preference
    public  String searchByParameter(String parameter, String value) {
        return database.searchByParameter(parameter, value, DELIMITER);
    }
}
