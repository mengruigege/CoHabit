import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server {
    private static Database database = new Database();

    // method to check if the login was successful or not
    public synchronized boolean login(String username, String password) {
        //might have to make getAllUsers in database static
        database.loadUsersFromFile();
        User user;
        user = database.findUserByName(username);
        if (user == null) {
            return false;
        }
        if (user.getPassword().equals(password)) {
            return true;
        }
        return false;
    }

    // method used to add the new register user to the database and return true if successful
    public synchronized boolean register(User user) {
        database.loadUsersFromFile();
        return database.addUser(user);
    }

    public synchronized boolean sendMessage(User sender, User reciever, String message) {
        database.loadUsersFromFile();
        if (reciever == null) {
            return false;
        }
        database.loadConversation(sender.getName(), reciever.getName());
        String senderName = sender.getName();
        String recieverName = reciever.getName();
        database.recordMessages(senderName, recieverName, message);
        return true;
    }

    public synchronized String loadMessages(User user, User reciever) {
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


    public synchronized boolean sendFriendRequest(User user, User potentialFriend) {
        database.loadUsersFromFile();
        if (user == null || potentialFriend == null) {
            return false;
        }
        database.loadFriendRequestsFromFile();
        // do I need to check if potentialFriend blocked the user
        database.addFriendRequest(potentialFriend, user); //do I need to switch the order
        // do I need to saveFriendRequests to File
        return true;
    }

    public synchronized ArrayList<User> viewFriendRequests(User user) {
        database.loadUsersFromFile();
        database.loadFriendRequestsFromFile();
        return user.getIncomingFriendRequest();
    }

    public synchronized boolean declineFriendRequest(User user, User declinedUser) {
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
    public synchronized boolean addFriend(User user, User friend) {
        if (user != null && friend != null) {
            database.loadUsersFromFile();

            if (!(user.getFriendList().contains(friend))) {
                database.loadFriendsFromFile();
                database.addFriend(user, friend);
                database.addFriend(friend, user);
                database.removeFriendRequest(user, friend);
                //saveFriendRequestFile
                return true;
            }
        }
        return false;
    }

    public synchronized boolean removeFriend(User user, User removedFriend) {
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

    public synchronized boolean blockUser(User user, User blockedUser) {
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

    public synchronized boolean removeBlockedUser(User user, User blockedUser) {
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

    public synchronized ArrayList<User> viewBlockedUsers(User user) {
        if (user != null) {
            database.loadBlockedFromFile();
            database.loadBlockedFromFile();
            return user.getBlockedUsers();
        }
        return null;
    }

    public synchronized ArrayList<User> viewFriendsList(User user) {
        if (user != null) {
            database.loadFriendsFromFile();
            database.loadFriendsFromFile();
            return user.getFriendList();
        } else {
            return null;
        }
    }

    public synchronized String viewProfile(String username) {
        database.loadUsersFromFile();
        User user = database.findUserByName(username);
        if (user != null) {
            return user.toString();
        } else {
            return null;
        }
    }

    public synchronized String partialMatch(User user) {
        if (user == null) {
            return "";
        }

        database.loadUsersFromFile();
        UserSearch userSearch = new UserSearch();
        ArrayList<User> partialmatches = userSearch.partialMatch(user);
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

    public synchronized String exactMatch(User user) {
        if (user == null) {
            return "";
        }
        database.loadUsersFromFile();
        UserSearch userSearch = new UserSearch();
        ArrayList<User> exactmatches = userSearch.exactMatch(user);

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

    public synchronized String searchByParameter(String parameter, String value) {
        if (parameter == null || parameter.isEmpty() || value == null || value.isEmpty()) {
            return "";
        }
        database.loadUsersFromFile();
        UserSearch userSearch = new UserSearch();
        ArrayList<User> matches = userSearch.searchByParameter(parameter, value);
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

    public synchronized void setPreferences(User user, String bedtime, boolean alcohol, boolean smoke, boolean guests, int tidy, int roomHours) {
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
        Server server = new Server();
        server.start();
        database.loadUsersFromFile();
        // some way to read all data that already exists in the database
        //open the ServerSocket and use the specific port
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(1102)) {
            while (true) {
                try (Socket socket = serverSocket.accept()) {
                    new Thread(new ClientHandler(socket)).start();

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class ClientHandler implements Runnable {
        private final Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
                while (true) {
                    String line = reader.readLine();
                    // this is the main part that help to decide what to do with information of line
                    //"login, username, password,
                    if (line.substring(0, 5).contains("login")) {
                        String[] parts = line.split(",");
                        database.loadUsersFromFile();
                        String username = parts[1];
                        String password = parts[2];
                        if (login(username, password)) { // not sure about this
                            writer.println("Sucessful login");
                            break;
                        } else writer.println("Wrong username or password");
                    }

                    //format should be register#*username#*password#*email#*phoneNumber#*desciption#*university#*bedTime#*alcohol#*smoke#*guests#*tidy#*roomHours
                    if (line.substring(0, 8).contains("register")) {
                        String[] parts = line.split("#*");
                        try {
                            User user = new User(parts[1], parts[2], parts[3], parts[4], parts[5], parts[6]);
                            boolean alcohol = Boolean.parseBoolean(parts[7]);
                            boolean smoke = Boolean.parseBoolean(parts[8]);
                            boolean guests = Boolean.parseBoolean(parts[9]);
                            int tidy = Integer.parseInt(parts[10]);
                            int roomHours = Integer.parseInt(parts[11]);
                            try {
                                user.setPreferences(parts[7], alcohol, smoke, guests, tidy, roomHours);
                            } catch (Exception e) {
                                writer.println(e.getMessage()); //not sure what to do here
                            }
                            if (register(user)) {
                                writer.println("successful registration");
                            } else {
                                writer.println("registration failed");
                            }
                        } catch (UsernameTakenException e) {
                            writer.println("Enter a different username");
                        }
                    }

                    // should be in format sendMessage#*sender#*reciever#*message
                    if (line.substring(0, 11).contains("sendMessage")) {
                        String[] parts = line.split("#*");
                        database.loadUsersFromFile();
                        User sender = database.findUserByName(parts[1]);
                        User receiver = database.findUserByName(parts[2]);

                        String message = parts[3];
                        if (sendMessage(sender, receiver, message)) {
                            writer.println("Successfully sent message"); //not sure what the output is here
                        } else {
                            writer.println("Something went wrong");
                        }
                    }

                    // should be in format sendFriendRequest,user,potentialFriend
                    if (line.substring(0, 17).contains("sendFriendRequest")) {
                        String[] parts = line.split(",");
                        database.loadUsersFromFile();
                        User user = database.findUserByName(parts[1]);
                        User potentialfriend = database.findUserByName(parts[2]);
                        if (sendFriendRequest(user, potentialfriend)) {
                            writer.println("Successfully sent friend request");
                        } else {
                            writer.println("Friend request failed");
                        }

                    }
                    // should be in format viewFriendRequests,user
                    if (line.substring(0, 18).contains("viewFriendRequests")) {
                        String[] parts = line.split(",");
                        database.loadUsersFromFile();
                        User user = database.findUserByName(parts[1]);
                        if (viewFriendRequests(user) == null) {
                            writer.println("Friend requests are empty");
                        } else {
                            writer.println(viewFriendRequests(user).toString()); // do not use toSting();
                        }
                    }
                    // format should be declineFriendRequest,user,declinedUser
                    if (line.substring(0, 20).contains("declineFriendRequest")) {
                        String[] parts = line.split(",");
                        database.loadUsersFromFile();
                        User user = database.findUserByName(parts[1]);
                        User declinedUser = database.findUserByName(parts[2]);
                        if (declineFriendRequest(user, declinedUser)) {
                            writer.println("You declined friend request declined");
                        } else {
                            writer.println("Could not decline friend request sucessfully");
                        }
                    }

                    // should be in format addFriend,user,friend
                    if (line.substring(0, 9).contains("addFriend")) {
                        String[] parts = line.split(",");
                        database.loadUsersFromFile();
                        User user = database.findUserByName(parts[1]);
                        User friend = database.findUserByName(parts[2]);
                        if (addFriend(user, friend)) {
                            writer.println("Successfully added friend");
                        } else {
                            writer.println("Entered a nonexistent Person");
                        }
                    }
                    // should be in format removeFriend,user,removedFriend
                    if (line.substring(0, 12).contains("removeFriend")) {
                        String[] parts = line.split(",");
                        database.loadUsersFromFile();
                        User user = database.findUserByName(parts[1]);
                        User friend = database.findUserByName(parts[2]);
                        if (removeFriend(user, friend)) {
                            writer.println("Successfully removed friend");
                        } else {
                            writer.println("Something went wrong");
                        }
                    }
                    // format should be viewFriendsList,user
                    if (line.substring(0, 15).contains("viewFriendsList")) {
                        String[] parts = line.split(",");
                        database.loadUsersFromFile();
                        User user = database.findUserByName(parts[1]);
                        if (viewFriendsList(user) == null) {
                            writer.println("Friend list is empty");
                        } else {
                            writer.println(viewFriendsList(user));
                        }
                    }
                    //  should be in format loadMessages,user,reciever
                    if (line.substring(0, 12).contains("loadMessages")) {
                        String[] parts = line.split(",");
                        User user = database.findUserByName(parts[1]);
                        User reciever = database.findUserByName(parts[2]);
                        if (loadMessages(user, reciever) == null) {
                            writer.println("Message list is empty");
                        } else {
                            writer.println(loadMessages(user, reciever));
                        }

                    }
                    // format should be blockUser,user,blockedUser
                    if (line.substring(0, 9).contains("blockUser")) {
                        String[] parts = line.split(",");
                        database.loadUsersFromFile();
                        User user = database.findUserByName(parts[1]);
                        User blockedUser = database.findUserByName(parts[2]);
                        if (blockUser(user, blockedUser)) {
                            writer.println("Successfully blocked user");
                        } else {
                            writer.println("Something went wrong");
                        }
                    }
                    // format should be removeBlockedUser,user,blockedUser
                    if (line.substring(0, 17).contains("removeBlockedUser")) {
                        String[] parts = line.split(",");
                        database.loadUsersFromFile();
                        User user = database.findUserByName(parts[1]);
                        User blockedUser = database.findUserByName(parts[2]);
                        if (removeBlockedUser(user, blockedUser)) {
                            writer.println("Successfully removed from blocked list");
                        } else {
                            writer.println("Something went wrong");
                        }

                    }
                    // format should be viewBlockedUsers,user
                    if (line.substring(0, 16).contains("viewBlockedUsers")) {
                        String[] parts = line.split(",");
                        User user = database.findUserByName(parts[1]);
                        if (viewBlockedUsers(user) == null) {
                            writer.println("Blocked list is empty");
                        } else {
                            writer.println(viewBlockedUsers(user));
                        }
                    }


                    // format is viewProfile,username
                    if (line.substring(0, 11).contains("viewProfile")) {
                        String[] parts = line.split(",");
                        String username = parts[1];
                        String viewProfile = viewProfile(username);
                        if (viewProfile != null) {
                            writer.println(viewProfile);
                        } else {
                            writer.println("Something went wrong");
                        }
                    }
                    // format should be partialMatch,user
                    if (line.substring(0, 12).contains("partialMatch")) {
                        String[] parts = line.split(",");
                        User user = database.findUserByName(parts[1]);
                        if (partialMatch(user) == null || partialMatch(user).isEmpty()) {
                            writer.println("No partial matches found");
                        } else {
                            writer.println(partialMatch(user));
                        }

                    }
                    // format should be exactMatch,user
                    if (line.substring(0, 10).contains("exactMatch")) {
                        String[] parts = line.split(",");
                        User user = database.findUserByName(parts[1]);
                        if (exactMatch(user) == null || exactMatch(user).isEmpty()) {
                            writer.println("No exact matches found");
                        } else {
                            writer.println(exactMatch(user));
                        }
                    }
                    // format should be searchByParameter,parameter,value
                    if (line.substring(0, 17).contains("searchByParameter")) {
                        String[] parts = line.split(",");
                        String parameter = parts[1];
                        String value = parts[2];
                        if (searchByParameter(parameter, value) == null || searchByParameter(parameter, value).isEmpty()) {
                            writer.println("No matches found");
                        } else {
                            writer.println(searchByParameter(parameter, value));
                        }
                    }

                    if (line.substring(0, 17).contains("updatePreferences")) {
                        String[] parts = line.split(",", 3);
                        User user = database.findUserByName(parts[1]);
                        if (user == null) {
                            writer.println("User not found");
                        }
                        String[] tokens = parts[2].split("#*");
                        user.setPreferences(tokens[0], Boolean.parseBoolean(tokens[1]), Boolean.parseBoolean(tokens[2]),
                                Boolean.parseBoolean(tokens[3]), Integer.parseInt(tokens[4]), Integer.parseInt(tokens[5]));
                        database.saveUsersToFile();
                        writer.println("Preferences updated");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InvalidInput e) {
                throw new RuntimeException(e);
            }
        }
    }
}
