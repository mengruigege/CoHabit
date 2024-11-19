import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server {
    private static Database database = new Database();

    // method to check if the login was successful or not
    public String login(String username, String password) {
        //might have to make getAllUsers in database static
        database.loadUsersFromFile();
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
            return user.getName() + "###" + user.getPassword() + "###" + user.getEmail() +
                    "###" + user.getPhoneNumber() + "###" + user.getDescription() + "###" + user.getUniversity();
        }
        return null;
    }

    // method used to add the new register user to the database and return true if successful
    public boolean register(User user) {
        database.loadUsersFromFile();
        return database.addUser(user);
    }

    public static boolean sendMessage(User sender, User reciever, String message) {
        database.loadUsersFromFile();
        if (reciever == null) {
            return false;
        }
        ArrayList<String> messages = database.loadConversation(sender.getName(), reciever.getName());
        String senderName = sender.getName();
        String recieverName = reciever.getName();
        database.recordMessages(senderName, recieverName, message);
        System.out.println("RETURNING TRUE");
        return true;
    }

    public String loadMessages(User user, User reciever) {
        if (user == null || reciever == null) {
            return null;
        }
        ArrayList<String> messages = database.loadConversation(user.getName(), reciever.getName());
        String result = "";

        for (String s : messages) {
            result += s + "\n";
        }
        return result;
    }

    public boolean sendFriendRequest(User user, User potentialFriend) {
        database.loadUsersFromFile();
        if (user == null || potentialFriend == null) {
            return false;
        }
        // do I need to check if potentialFriend blocked the user
        database.addFriendRequest(user, potentialFriend); //do I need to switch the order
        // do I need to saveFriendRequests to File
        return true;
    }

    public ArrayList<String> viewFriendRequests(User user) {
        database.loadUsersFromFile();
        if (user == null || database.loadFriendRequestsFromFile() == null) {
            return null;
        }
        ArrayList<String> friendRequestNames = database.loadFriendRequestsFromFile();
        int i = 0;
        ArrayList<String> actualFriendRequestNames = new ArrayList<>();

        for (String friendRequestName : friendRequestNames) {
            if (friendRequestName.split(":")[1].equals(user.getName())) {
                actualFriendRequestNames.add(friendRequestName.split(":")[0]);
            }
        }

        return actualFriendRequestNames;
    }

    public  boolean declineFriendRequest(User user, User declinedUser) {
        if (user != null && declinedUser != null) {
            database.loadUsersFromFile();
            database.loadFriendRequestsFromFile();
            database.removeFriendRequest(user, declinedUser);
            //saveFriendRequestFile
            return true;
        } else {
            return false;
        }
    }
    //addFriend is what happens when you accept a friend request
    public  boolean addFriend(User user, User friend) {
        if (user != null && friend != null) {
            database.loadUsersFromFile();

            if (!(user.getFriendList().contains(friend))) {
//                database.loadFriendsFromFile();?
                database.addFriend(user, friend);
                database.addFriend(friend, user);
                database.saveFriendsToFile();
                database.removeFriendRequest(user, friend);
                //saveFriendRequestFile
                return true;
            }
        }
        return false;
    }
    public  boolean removeFriend(User user, User removedFriend) {
        if (user != null && removedFriend != null) {
            database.loadUsersFromFile();
            database.loadFriendsFromFile();
            if (user.getFriendList().contains(removedFriend)) {
                database.removeFriend(user, removedFriend);
                database.removeFriend(removedFriend, user); //not sure that we need this
                return true;
            }
        }
        return false;
    }
    public  boolean blockUser(User user, User blockedUser) {
        if (user != null || blockedUser != null) {
            database.loadUsersFromFile();
            database.loadBlockedFromFile();
            if (!(user.getBlockedUsers().contains(blockedUser))) {
                database.blockUser(user, blockedUser);
                return true;
            }
        }
        return false;
    }
    public  boolean removeBlockedUser(User user, User blockedUser) {
        if (user != null || blockedUser != null) {
            database.loadBlockedFromFile();
            database.loadBlockedFromFile();
            if (user.getBlockedUsers().contains(blockedUser)) {
                database.unblockUser(user, blockedUser);
                return true;
            }
        }
        return false;
    }
    public  ArrayList<User> viewBlockedUsers(User user) {
        if (user != null) {
            database.loadBlockedFromFile();
            database.loadBlockedFromFile();
            return user.getBlockedUsers();
        }
        return null;
    }
    public  ArrayList<User> viewFriendsList(User user) {
        if (user != null) {
            database.loadFriendsFromFile();
            database.loadFriendsFromFile();
            return user.getFriendList();
        } else {
            return null;
        }
    }

    public  String viewProfile(String username) {
        database.loadUsersFromFile();
        User user = database.findUserByName(username);
        if (user != null) {
            return user.toString();
        } else {
            return null;
        }
    }
    public  String partialMatch(User user) {
        if (user == null) {
            return "";
        }

        database.loadUsersFromFile();
        ArrayList<User> partialmatches = database.partialMatch(user);
        if (partialmatches.isEmpty()) {
            return "";
        }
        String result = "";
        for (User users : partialmatches) {
            String username = users.getName();
            result += username + "\n";
        }
        return result;

    }
    public  String exactMatch(User user) {
        if (user == null) {
            return "";
        }
        database.loadUsersFromFile();
        ArrayList<User> exactmatches = database.exactMatch(user);

        if (exactmatches == null) {
            return "";
        }

        String result = "";
        for (User users : exactmatches) {
            String username = users.getName();
            result += username + "\n";
        }
        return result;
    }
    public  String searchByParameter(String parameter, String value) {
        if (parameter == null || parameter.isEmpty() || value == null || value.isEmpty()) {
            return "";
        }
        database.loadUsersFromFile();
        ArrayList<User> matches = database.searchByParameter(parameter, value);
        if (matches == null) {
            return "";
        }
        String result = "";
        for (User users : matches) {
            String username = users.getName();
            result += username + "\n";
        }
        return result;
    }
    public synchronized void setPreferences(User user, String bedtime, boolean alcohol,
                                            boolean smoke, boolean guests, int tidy, int roomHours) {
        if (user == null) {
            return;
        }

        try {
            database.loadUsersFromFile();
            User existingUser = database.findUserByName(user.getName());

            if (existingUser != null) {
                existingUser.setPreferences(bedtime, alcohol, smoke, guests, tidy, roomHours);
                database.saveUsersToFile(); // Save updated user preferences back to the database
                System.out.println("Preferences successfully updated for user: " + user.getName());
            } else {
                System.out.println("User not found in the database.");
            }
        } catch (Exception e) {
            System.out.println("Error updating preferences");
        }
    }


    public static void main(String[] args) {
        database.loadUsersFromFile();
        Server server = new Server();

        try ( ServerSocket serverSocket = new ServerSocket(1102)) {
            while (true) {
                try (Socket socket = serverSocket.accept();
                      BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                      PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
                    while (true) {
                        String line = reader.readLine();
                        if (line == null) {
                            line = "";
                        }
                        System.out.println("line = " + line);
                        // this is the main part that help to decide what to do with information of line
                        //"login, username, password,
                        if (line.length() > 5 && line.substring(0, 5).contains("login")) {
                            String[] parts = line.split(",");

                            String username = parts[1];
                            String password = parts[2];

                            String userInformation = server.login(username, password);
                            if (userInformation != null) { // not sure about this
                                writer.println("Successful login");
                                writer.println(userInformation);
                                //break;
                            } else writer.println("Wrong username or password");

                        }

                        //format should be register#*username#*password#*email#*phoneNumber#*desciption#*university#*bedTime#*alcohol#*smoke#*guests#*tidy#*roomHours
                        if (line.length() > 8 && line.substring(0, 8).contains("register")) {
                            String[] parts = line.split("###");
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
                                    writer.println(e.getMessage()); //not sure what to do here
                                }
                                if (server.register(user)) {
                                    writer.println("successful registration");
                                } else {
                                    writer.println("registration failed");
                                }
                            } catch (UsernameTakenException e) {
                                writer.println("Enter a different username");
                            }

                        }

                        // should be in format sendMessage#*sender#*reciever#*message
                        if (line.length() > 11 && line.substring(0, 11).contains("sendMessage")) {
                            String[] parts = line.split("###");
                            database.loadUsersFromFile();
                            User sender = database.findUserByName(parts[1]);
                            User receiver = database.findUserByName(parts[2]);

                            String message = parts[3];
                            if (server.sendMessage(sender, receiver, message)) {
                                System.out.println("SUCCESS");
                                writer.println("Successfully sent message"); //not sure what the output is here
                            }
                            else {
                                System.out.println("FAILURE");
                                writer.println("Something went wrong");
                            }
                        }

                        // should be in format sendFriendRequest,user,potentialFriend
                        if (line.length() > 17 && line.substring(0, 17).contains("sendFriendRequest")) {
                            String[] parts = line.split(",");
                            database.loadUsersFromFile();
                            User user = database.findUserByName(parts[1]);
                            User potentialfriend = database.findUserByName(parts[2]);
                            if (server.sendFriendRequest(user, potentialfriend)) {
                                writer.println("Successfully sent friend request");
                            } else {
                                //System.out.println("failed");
                                writer.println("Friend request failed");
                            }

                        }

                        // should be in format viewFriendRequests,user
                        if (line.length() > 18 &&
                                line.substring(0, 18).contains("viewFriendRequests")) {
                            String[] parts = line.split(",");
                            database.loadUsersFromFile();
                            User user = database.findUserByName(parts[1]);
                            //System.out.println(server.viewFriendRequests(user));
                            if (server.viewFriendRequests(user) == null) {
                                writer.println("Friend requests are empty");
                            } else {
                                writer.println(String.join(",", server.viewFriendRequests(user))); // do not use toSting();
                            }
                        }

                        // format should be declineFriendRequest,user,declinedUser
                        if (line.length() > 20 && line.substring(0, 20).contains("declineFriendRequest")) {
                            String[] parts = line.split(",");
                            database.loadUsersFromFile();
                            User user = database.findUserByName(parts[1]);
                            User declinedUser = database.findUserByName(parts[2]);
                            if (server.declineFriendRequest(user, declinedUser)) {
                                writer.println("You declined friend request declined");
                            } else {
                                writer.println("Could not decline friend request sucessfully");
                            }
                        }

                        // should be in format addFriend,user,friend
                        if (line.length() > 9 && line.substring(0, 9).contains("addFriend")) {
                            String[] parts = line.split(",");
                            database.loadUsersFromFile();
                            User user = database.findUserByName(parts[1]);
                            User friend = database.findUserByName(parts[2]);
                            if (server.addFriend(user,friend)) {
                                System.out.println(user.getFriendList());
                                for (User friend1 : user.getFriendList()) {
                                    System.out.println(friend1.getName());
                                }

                                writer.println("Successfully added friend");
                            }
                            else {
                                writer.println("Entered a nonexistent Person");
                            }
                        }

                        // should be in format removeFriend,user,removedFriend
                        if (line.length() > 12 && line.substring(0, 12).contains("removeFriend")) {
                            String[] parts = line.split(",");
                            database.loadUsersFromFile();
                            User user = database.findUserByName(parts[1]);
                            User friend = database.findUserByName(parts[2]);
                            if (server.removeFriend(user,friend)) {
                                writer.println("Successfully removed friend");
                            }
                            else {
                                writer.println("Something went wrong");
                            }
                        }

                        // format should be viewFriendsList,user
                        if (line.length() > 15 && line.substring(0, 15).contains("viewFriendsList")) {
                            String[] parts = line.split(",");
                            database.loadUsersFromFile();
                            User user = database.findUserByName(parts[1]);
                            if (server.viewFriendsList(user) == null) {
                                writer.println("Friend list is empty");
                            } else {
                                writer.println(server.viewFriendsList(user));
                            }
                        }

                        //  should be in format loadMessages,user,reciever
                        if (line.length() > 12 && line.substring(0,12).contains("loadMessages")) {
                            String[] parts = line.split(",");
                            User user = database.findUserByName(parts[1]);
                            User reciever = database.findUserByName(parts[2]);
                            if (server.loadMessages(user, reciever) == null) {
                                writer.println("Message list is empty");
                            } else {
                                writer.println(server.loadMessages(user, reciever));
                            }

                        }

                        // format should be blockUser,user,blockedUser
                        if (line.length() > 9 && line.substring(0,9).contains("blockUser")) {
                            String[] parts = line.split(",");
                            database.loadUsersFromFile();
                            User user = database.findUserByName(parts[1]);
                            User blockedUser = database.findUserByName(parts[2]);
                            if (server.blockUser(user,blockedUser)) {
                                writer.println("Successfully blocked user");
                            }
                            else {
                                writer.println("Something went wrong");
                            }
                        }

                        // format should be removeBlockedUser,user,blockedUser
                        if (line.length() > 17 && line.substring(0,17).contains("removeBlockedUser")) {
                            String[] parts = line.split(",");
                            database.loadUsersFromFile();
                            User user = database.findUserByName(parts[1]);
                            User blockedUser = database.findUserByName(parts[2]);
                            if (server.removeBlockedUser(user,blockedUser)) {
                                writer.println("Successfully removed from blocked list");
                            } else {
                                writer.println("Something went wrong");
                            }

                        }

                        // format should be viewBlockedUsers,user
                        if (line.length() > 16 && line.substring(0, 16).contains("viewBlockedUsers")) {
                            String[] parts = line.split(",");
                            User user = database.findUserByName(parts[1]);
                            if (server.viewBlockedUsers(user) == null) {
                                writer.println("Blocked list is empty");
                            } else {
                                writer.println(server.viewBlockedUsers(user));
                            }
                        }

                        // format is viewProfile,username
                        if (line.length() > 11 && line.substring(0, 11).contains("viewProfile")) {
                            String[] parts = line.split(",");
                            String username = parts[1];
                            String viewProfile = server.viewProfile(username);
                            if (viewProfile != null) {
                                writer.println(viewProfile);
                            } else {
                                writer.println("Something went wrong");
                            }
                        }

                        // format should be partialMatch,user
                        if (line.length() > 12 && line.substring(0, 12).contains("partialMatch")) {
                            String[] parts = line.split(",");
                            User user = database.findUserByName(parts[1]);
                            if (server.partialMatch(user) == null) {
                                writer.println("No partial matches found");
                            } else {
                                writer.println(server.partialMatch(user));
                            }

                        }

                        // format should be exactMatch,user
                        if (line.length() > 10 && line.substring(0,10).contains("exactMatch")) {
                            String[] parts = line.split(",");
                            User user = database.findUserByName(parts[1]);
                            if (server.exactMatch(user) == null) {
                                writer.println("No exact matches found");
                            } else {
                                writer.println(server.exactMatch(user));
                            }
                        }
                        // format should be searchByParameter,parameter,value
                        if (line.length() > 17 && line.substring(0, 17).contains("searchByParameter")) {
                            String[] parts = line.split(",");
                            String parameter = parts[1];
                            String value = parts[2];
                            if (server.searchByParameter(parameter, value) == null) {
                                writer.println("No matches found");
                            } else {
                                writer.println(server.searchByParameter(parameter, value));
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }   catch (IOException e) {
            e.printStackTrace();
        }
    }
}