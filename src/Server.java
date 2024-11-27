import java.io.*;
import java.net.*;
import java.util.ArrayList;

/** 
 * This document was created for CS 180 PJ 5 Phase 2
 *
 * @version 1.0
 * @author Andrew tang, Aidan Lefort, Keya Jadhav, Rithvik Siddenki, Rui Meng
 */

public class Server implements ServerService, Runnable {
    private static Database database = new Database();
    private static final String DELIMITER = "<<END>>";
    private static final String SUCCESS = "SUCCESS";
    private static final String FAILURE = "FAILURE";


    // method to check if the login was successful or not
    public String login(String username, String password) {
        User user;
        user = database.findUserByName(username);
        if (user == null) {
            return null;
        }
        if (!(password.equals(user.getPassword()))) {
            return "";
        }
        if (!(username.equals(user.getName()))) {
            return "";
        }
        if (user.getPassword().equals(password) && user.getName().equals(username)) {
            return user.getName() + DELIMITER + user.getPassword() + DELIMITER + user.getEmail() +
                    DELIMITER + user.getPhoneNumber() + DELIMITER + user.getDescription() + DELIMITER + user.getUniversity();
        }
        return null;
    }

    // method used to add the new register user to the database and return true if successful
    public boolean register(User user) {
        if (database.addUser(user)) {
            return true;
        } else {
            return false;
        }
    }

    //method to send messages between two users
    public static boolean sendMessage(User sender, User reciever, String message) {
        database.sendMessage(sender, reciever, message);
        return true;
    }

    //method to load messages between two users
    public String loadMessages(User user, User reciever) {
        if (user == null || reciever == null) {
            return null;
        }
        ArrayList<String> messages = database.loadConversation(user.getName(), reciever.getName());
        String result = "";

        for (String s : messages) {
            result += s + DELIMITER;
        }
        return result;
    }

    //method to send friend request to another user
    public boolean sendFriendRequest(User user, User potentialFriend) {
        return database.sendFriendRequest(user, potentialFriend);
    }

    //method to view all friend requests of a user
    public ArrayList<String> viewFriendRequests(User user) {
        ArrayList<String> friendRequests = database.getFriendRequests(user);
        if (friendRequests == null) {
            return null;
        } else {
            return friendRequests;
        }
    }

    //method to decline friend requests
    public boolean declineFriendRequest(User receiver, User sender) {
        return database.rejectFriendRequest(receiver, sender);
    }
    
    //method to accept a friend request
    public boolean acceptFriendRequest(User receiver, User sender) {
        return database.acceptFriendRequest(receiver, sender friend);
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
        ArrayList<User> partialmatches = database.partialMatch(user);
        if (partialmatches.isEmpty()) {
            return null;
        }

        String result = "";
        for (User users : partialmatches) {
            String username = users.getName();
            result += username + DELIMITER;
        }
        return result;

    }

    //method to retrieve list of users who have the exact same preferences
    public String exactMatch(User user) {
        if (user == null) {
            return "";
        }

        ArrayList<User> exactmatches = database.exactMatch(user);

        if (exactmatches == null) {
            return "";
        }

        String result = "";
        for (User users : exactmatches) {
            String username = users.getName();
            result += username + DELIMITER;
        }
        return result;
    }

    //Method to search roommates by a specific preference
    public  String searchByParameter(String parameter, String value) throws UsernameTakenException, InvalidInput {
        if (parameter == null || parameter.isEmpty() || value == null || value.isEmpty()) {
            return "";
        }

        ArrayList<User> matches = database.searchByParameter(parameter, value);
        if (matches == null) {
            return "";
        }
        String result = "";
        for (User users : matches) {
            String username = users.getName();
            result += username + DELIMITER;
        }
        return result;
    }

    //method to set preferences of a user
    public synchronized void setPreferences(User user, String bedtime, boolean alcohol,
                                            boolean smoke, boolean guests, int tidy, int roomHours) {
        if (user == null) {
            return;
        }

        try {
            User existingUser = database.findUserByName(user.getName());

            if (existingUser != null) {
                existingUser.setPreferences(bedtime, alcohol, smoke, guests, tidy, roomHours);
                System.out.println("Preferences successfully updated for user: " + user.getName());
            } else {
                System.out.println("User not found in the database.");
            }
        } catch (Exception e) {
            System.out.println("Error updating preferences");
        }
    }

    //main method for computation
    public static void main(String[] args) throws UsernameTakenException, InvalidInput {
        database.initializeDatabase();
        Server server = new Server();

        //connect server to client via socket
        
        try (ServerSocket serverSocket = new ServerSocket(1102)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> {
                    try (Socket socket = clientSocket;
                         BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                         PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
                        while (true) {
                            String line = reader.readLine();
                            if (line == null) 
                                break;
                            
                            //receives message from client in the format "login, username, password"
                            if (line.startsWith("login")) {
                                String[] parts = line.split(DELIMITER);

                                String username = parts[1];
                                String password = parts[2];

                                String userInformation = server.login(username, password);
                                if (userInformation != null && !userInformation.isEmpty()) { // not sure about this
                                    writer.println("Successful login");
                                    writer.println(userInformation);
                                } else writer.println("Wrong username or password");

                            }

                            /** receives message from client in the format  
                             * register###username###password###email###phoneNumber###desciption
                             * ###university###bedTime###alcohol###smoke###guests###tidy###roomHours */

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
                                        writer.println(e.getMessage()); 
                                    }
                                    if (server.register(user)) {
                                        writer.println("successful registration");
                                    } else {
                                        writer.println("registration failed (possibly username taken)");
                                    }
                                } catch (UsernameTakenException e) {
                                    writer.println("Enter a different username");
                                }

                            }

                            // receives message from client in format sendMessage###sender###reciever###message
                            if (line.startsWith("sendMessage")) {
                                String[] parts = line.split(DELIMITER);
                                User sender = database.findUserByName(parts[1]);
                                User receiver = database.findUserByName(parts[2]);

                                String message = parts[3];
                                if (server.sendMessage(sender, receiver, message)) {
                                    System.out.println("SUCCESS");
                                    writer.println("Successfully sent message");
                                } else {
                                    System.out.println("FAILURE");
                                    writer.println("Something went wrong");
                                }
                            }

                            // Receives message from client in the format loadMessages###user###receiver
                            if (line.startsWith("loadMessages")) {
                                String[] parts = line.split(DELIMITER);
                                String senderUsername = parts[1];
                                String receiverUsername = parts[2];

                                User sender = database.findUserByName(senderUsername);
                                User receiver = database.findUserByName(receiverUsername);

                                if (sender == null || receiver == null) {
                                    writer.println("Error: One or both users not found.");
                                    continue;
                                }

                                String messages = server.loadMessages(sender, receiver);
                                if (messages == null || messages.isEmpty()) {
                                    writer.println("Message list is empty");
                                } else {
                                    writer.println(messages);
                                }
                            }

                            // receives message from client in the format sendFriendRequest,user,potentialFriend
                            if (line.startsWith("sendFriendRequest")) {
                                String[] parts = line.split(DELIMITER);
                                User user = database.findUserByName(parts[1]);
                                User potentialfriend = database.findUserByName(parts[2]);
                                if (server.sendFriendRequest(user, potentialfriend)) {
                                    writer.println("Successfully sent friend request");
                                } else {
                                    writer.println("Friend request failed (possibly already sent a friend request)");
                                }

                            }

                            // receives message from client in the format viewFriendRequests,user
                            if (line.startsWith("viewFriendRequests")) {
                                String[] parts = line.split(DELIMITER);
                                User user = database.findUserByName(parts[1]);
                                if (server.viewFriendRequests(user) == null) {
                                    writer.println("Friend requests are empty");
                                } else {
                                    writer.println(String.join(DELIMITER, server.viewFriendRequests(user)));
                                }
                            }

                            // receives message from client in the format declineFriendRequest,user,declinedUser
                            if (line.startsWith("declineFriendRequest")) {
                                String[] parts = line.split(DELIMITER);
                                User receiver = database.findUserByName(parts[1]);
                                User sender = database.findUserByName(parts[2]);
                                if (server.declineFriendRequest(receiver, sender)) {
                                    writer.println("You declined friend request declined");
                                } else {
                                    writer.println("Could not decline friend request successfully");
                                }
                            }

                            // receives message from client in the format acceptFriendRequest,user,friend
                            if (line.startsWith("acceptFriendRequest")) {
                                String[] parts = line.split(DELIMITER);
                                User user = database.findUserByName(parts[1]);
                                User friend = database.findUserByName(parts[2]);
                                if (server.acceptFriendRequest(user, friend)) {
                                    System.out.println(user.getFriendList());
                                    for (User friend1 : user.getFriendList()) {
                                        System.out.println(friend1.getName());
                                    }
                                    writer.println("Successfully added friend");
                                } else {
                                    writer.println("Entered a nonexistent Person");
                                }
                            }

                            // receives message from client in the format removeFriend,user,removedFriend
                            if (line.startsWith("removeFriend")) {
                                String[] parts = line.split(DELIMITER);
                                User user = database.findUserByName(parts[1]);
                                User friend = database.findUserByName(parts[2]);
                                if (server.removeFriend(user, friend)) {
                                    writer.println("Successfully removed friend");
                                } else {
                                    writer.println("Something went wrong");
                                }
                            }

                            // receives message from client in the format viewFriendsList,user
                            if (line.startsWith("viewFriendsList")) {
                                String[] parts = line.split(DELIMITER);
                                User user = database.findUserByName(parts[1]);
                                if (server.viewFriendsList(user) == null) {
                                    writer.println("Friend list is empty");
                                } else {
                                    writer.println(server.viewFriendsList(user));
                                }
                            }

                            //  receives message from client in the format loadMessages,user,reciever
                            if (line.startsWith("loadMessages")) {
                                String[] parts = line.split(DELIMITER);
                                User user = database.findUserByName(parts[1]);
                                User reciever = database.findUserByName(parts[2]);
                                if (server.loadMessages(user, reciever) == null) {
                                    writer.println("Message list is empty");
                                } else {
                                    writer.println(server.loadMessages(user, reciever));
                                }

                            }

                            // receives message from client in the format blockUser,user,blockedUser
                            if (line.startsWith("blockUser")) {
                                String[] parts = line.split(DELIMITER);
                                User user = database.findUserByName(parts[1]);
                                User blockedUser = database.findUserByName(parts[2]);
                                if (server.blockUser(user, blockedUser)) {
                                    writer.println("Successfully blocked user");
                                } else {
                                    writer.println("Something went wrong");
                                }
                            }

                            // receives message from client in the format removeBlockedUser,user,blockedUser
                            if (line.startsWith("removeBlockedUser")) {
                                String[] parts = line.split(DELIMITER);
                                User user = database.findUserByName(parts[1]);
                                User blockedUser = database.findUserByName(parts[2]);
                                if (server.removeBlockedUser(user, blockedUser)) {
                                    writer.println("Successfully removed from blocked list");
                                } else {
                                    writer.println("Something went wrong");
                                }

                            }

                            // receives message from client in the format viewBlockedUsers,user
                            if (line.startsWith("viewBlockedUsers")) {
                                String[] parts = line.split(DELIMITER);
                                User user = database.findUserByName(parts[1]);
                                if (server.viewBlockedUsers(user) == null) {
                                    writer.println("Blocked list is empty");
                                } else {
                                    writer.println(server.viewBlockedUsers(user));
                                }
                            }

                            // receives message from client in the format viewProfile,username
                            if (line.startsWith("viewProfile")) {
                                String[] parts = line.split(DELIMITER);
                                String username = parts[1];
                                String viewProfile = server.viewProfile(username);
                                if (viewProfile != null) {
                                    writer.println(viewProfile);
                                } else {
                                    writer.println("Something went wrong");
                                }
                            }


                            if (line.startsWith("updateProfile")) {
                                String[] tokens = line.split(DELIMITER);
                                if (tokens.length < 14) {
                                    writer.println("Error: Missing fields for updating profile.");
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
                                    writer.println("Error: Invalid numeric values for tidy or roomHours.");
                                    continue;
                                }

                                User user = database.findUserByName(oldUsername); // Lookup using the old username

                                if (user == null) {
                                    writer.println("Error: User not found.");
                                    continue;
                                }

                                if (!password.equals(user.getPassword())) {
                                    writer.println("Error: Incorrect password.");
                                    continue;
                                }

                                // Update the user's profile
                                try {
                                    user.setName(newUsername); // Update the username
                                    user.setEmail(email);
                                    user.setPhoneNumber(phoneNumber);
                                    user.setDescription(description);
                                    user.setUniversity(university);
                                    user.setPreferences(bedTime, alcohol, smoke, guests, tidy, roomHours);
                                    writer.println("Profile Updated");
                                } catch (Exception e) {
                                    writer.println("Error updating profile: " + e.getMessage());
                                }
                            }

                            // receives message from client in the format partialMatch,user
                            if (line.startsWith("partialMatch")) {
                                String[] parts = line.split(DELIMITER);
                                User user = database.findUserByName(parts[1]);
                                if (server.partialMatch(user) == null) {
                                    writer.println("No partial matches found");
                                } else {
                                    writer.println(server.partialMatch(user));
                                }

                            }

                            // receives message from client in the format exactMatch,user
                            if (line.startsWith("exactMatch")) {
                                String[] parts = line.split(DELIMITER);
                                User user = database.findUserByName(parts[1]);
                                if (server.exactMatch(user) == null) {
                                    writer.println("No exact matches found");
                                } else {
                                    writer.println(server.exactMatch(user));
                                }
                            }
                            // receives message from client in the format searchByParameter,parameter,value
                            if (line.startsWith("searchByParameter")) {
                                String[] parts = line.split(DELIMITER);
                                String parameter = parts[1];
                                String value = parts[2];
                                if (server.searchByParameter(parameter, value) == null) {
                                    writer.println("No matches found");
                                } else {
                                    writer.println(server.searchByParameter(parameter, value));
                                }
                            }

                            // Receives message from client in the format uploadProfilePicture###username
                            if (line.startsWith("uploadProfilePicture")) {
                                String[] parts = line.split(DELIMITER);
                                String username = parts[1];

                                User user = database.findUserByName(username);

                                if (user == null) {
                                    writer.println("Error: User not found.");
                                    continue;
                                }

                                try {
                                    int fileSize = Integer.parseInt(reader.readLine()); // Read the size of the file
                                    byte[] fileBytes = new byte[fileSize];
                                    socket.getInputStream().read(fileBytes); // Read the file data

                                    database.saveProfilePicture(user, fileBytes); // Save the profile picture in the database
                                    writer.println("Profile picture updated");
                                } catch (IOException | NumberFormatException e) {
                                    writer.println("Error updating profile picture: " + e.getMessage());
                                }
                            }

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        }   catch (IOException e) {
            e.printStackTrace();
        }
    }
}
