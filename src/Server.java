import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private static Database database = new Database();

// method to check if the login was successful or not
    public static boolean login(String username, String password) {
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
    public static boolean register(User user) {
        database.loadUsersFromFile();
        database.addUser(user);
        return true;
    }
    public static boolean sendMessage(User sender, User reciever, String message) {
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
    public static String loadMessages(User user, User reciever) {
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


    public static boolean sendFriendRequest(User user, User potentialFriend) {
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
    public static ArrayList<User> viewFriendRequests(User user) {
        database.loadUsersFromFile();
        database.loadFriendRequestsFromFile();
        return user.getIncomingFriendRequest();
    }
    public static boolean declineFriendRequest(User user, User declinedUser) {
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
    public static boolean addFriend(User user, User friend) {
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
    public static boolean removeFriend(User user, User removedFriend) {
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
    public static boolean blockUser(User user, User blockedUser) {
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
    public static boolean removeBlockedUser(User user, User blockedUser) {
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
    public static ArrayList<User> viewBlockedUsers(User user) {
        if (user != null) {
            database.loadBlockedFromFile();
            database.loadBlockedFromFile();
            return user.getBlockedUsers();
        }
        return null;
    }
    public static ArrayList<User> viewFriendsList(User user) {
        if (user != null) {
            database.loadFriendsFromFile();
            database.loadFriendsFromFile();
            return user.getFriendList();
        } else {
            return null;
        }
    }



    public static String viewProfile(String username) {
        database.loadUsersFromFile();
        User user = database.findUserByName(username);
        if (user != null) {
            return user.toString();
        } else {
            return null;
        }
    }

    public static void main(String[] args) {

        database.loadUsersFromFile();
        User currentUser;
        // some way to read all data that already exists in the database
        //open the ServerSocket and use the specific port
        try ( ServerSocket serverSocket = new ServerSocket(1102)) {
            while (true) {
                try ( Socket socket = serverSocket.accept();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
                    while (true) {
                        String line = reader.readLine();
                        // this is the main part that help to decide what to do with information of line
                        //"login, username, password,
                        if (line.contains("login")) {
                            String[] parts = line.split(",");
                            database.loadUsersFromFile();
                            String username = parts[1];
                            String password = parts[2];
                            if (Server.login(username, password)) { // not sure about this
                                writer.println("Sucessful login");
                                break;
                            } else writer.println("Wrong username or password");

                        }
                        //format should be register#*username#*password#*email#*phoneNumber#*desciption#*university
                        if (line.contains("register")) {
                            String[] parts = line.split("#*");
                            try {
                                User user = new User(parts[1],parts[2],parts[3],parts[4],parts[5],parts[6]);
                                if (Server.register(user)) {
                                    writer.println("successful registration");
                                } else {
                                    writer.println("registration failed");
                                }
                            } catch (UsernameTakenException e) {
                                writer.println("Enter a different username");
                            }

                        }

                        // should be in format sendMessage,sender,reciever,message
                        if (line.contains("sendMessage")) {
                            String[] parts = line.split(",");
                            database.loadUsersFromFile();
                            User sender = database.findUserByName(parts[1]);
                            User receiver = database.findUserByName(parts[2]);

                            String message = parts[3];
                            if (Server.sendMessage(sender, receiver, message)) {
                                writer.println("Successfully sent message"); //not sure what the output is here
                            }
                            else {
                                writer.println("Something went wrong");
                            }
                        }
                        // should be in format sendFriendRequest,user,potentialFriend
                        if (line.contains("sendFriendRequest")) {
                            String[] parts = line.split(",");
                            database.loadUsersFromFile();
                            User user = database.findUserByName(parts[1]);
                            User potentialfriend = database.findUserByName(parts[2]);
                            if (Server.sendFriendRequest(user,potentialfriend)) {
                                writer.println("Successfully sent friend request");
                            } else {
                                writer.println("Friend request failed");
                            }

                        }
                        // should be in format viewFriendRequests,user
                        if (line.contains("viewFriendRequests")) {
                            String[] parts = line.split(",");
                            database.loadUsersFromFile();
                            User user = database.findUserByName(parts[1]);
                            if (Server.viewFriendRequests(user) == null) {
                                writer.println("Friend requests are empty");
                            } else {
                                writer.println(viewFriendRequests(user).toString()); // do not use toSting();
                                }
                        }
                        // format should be declineFriendRequest,user,declinedUser
                        if (line.contains("declineFriendRequest")) {
                            String[] parts = line.split(",");
                            database.loadUsersFromFile();
                            User user = database.findUserByName(parts[1]);
                            User declinedUser = database.findUserByName(parts[2]);
                            if (Server.declineFriendRequest(user, declinedUser)) {
                                writer.println("You declined friend request declined");
                            } else {
                                writer.println("Could not decline friend request sucessfully");
                            }

                        }

                        // should be in format addFriend,user,friend
                        if (line.contains("addFriend")) {
                            String[] parts = line.split(",");
                            database.loadUsersFromFile();
                            User user = database.findUserByName(parts[1]);
                            User friend = database.findUserByName(parts[2]);
                            if (Server.addFriend(user,friend)) {
                                writer.println("Successfully added friend");
                            }
                            else {
                                writer.println("Entered a nonexistent Person");
                            }
                        }
                        // should be in format removeFriend,user,removedFriend
                        if (line.contains("removeFriend")) {
                            String[] parts = line.split(",");
                            database.loadUsersFromFile();
                            User user = database.findUserByName(parts[1]);
                            User friend = database.findUserByName(parts[2]);
                            if (Server.removeFriend(user,friend)) {
                                writer.println("Successfully removed friend");
                            }
                            else {
                                writer.println("Something went wrong");
                            }
                        }
                        // format should be viewFriendsList,user
                        if (line.contains("viewFriendsList")) {
                            String[] parts = line.split(",");
                            database.loadUsersFromFile();
                            User user = database.findUserByName(parts[1]);
                            if (Server.viewFriendsList(user) == null) {
                                writer.println("Friend list is empty");
                            } else {
                                writer.println(viewFriendsList(user).toString()); //do not use toSting() here
                            }
                        }
                        //  should be in format loadMessages,user,reciever
                        if (line.contains("loadMessages")) {
                            String[] parts = line.split(",");
                            User user = database.findUserByName(parts[1]);
                            User reciever = database.findUserByName(parts[2]);
                            if (Server.loadMessages(user, reciever) == null) {
                                writer.println("Message list is empty");
                            } else {
                                writer.println(loadMessages(user, reciever));
                            }

                        }
                        // format should be blockUser,user,blockedUser
                        if (line.contains("blockUser")) {
                            String[] parts = line.split(",");
                            database.loadUsersFromFile();
                            User user = database.findUserByName(parts[1]);
                            User blockedUser = database.findUserByName(parts[2]);
                            if (Server.blockUser(user,blockedUser)) {
                                writer.println("Successfully blocked user");
                            }
                            else {
                                writer.println("Something went wrong");
                            }
                        }
                        // format should be removeBlockedUser,user,blockedUser
                        if (line.contains("removeBlockedUser")) {
                            String[] parts = line.split(",");
                            database.loadUsersFromFile();
                            User user = database.findUserByName(parts[1]);
                            User blockedUser = database.findUserByName(parts[2]);
                            if (Server.removeBlockedUser(user,blockedUser)) {
                                writer.println("Successfully removed from blocked list");
                            } else {
                                writer.println("Something went wrong");
                            }

                        }
                        // format should be viewBlockedUsers,user
                        if (line.contains("viewBlockedUsers")) {
                            String[] parts = line.split(",");
                            User user = database.findUserByName(parts[1]);
                            if (Server.viewBlockedUsers(user) == null) {
                                writer.println("Blocked list is empty");
                            } else {
                                writer.println(viewBlockedUsers(user).toString()); //do not use toString
                            }
                        }


                        
                        // format is viewProfile,username
                        if (line.contains("viewProfile")) {
                            String[] parts = line.split(",");
                            String username = parts[1];
                            String viewProfile = Server.viewProfile(username);
                            if (viewProfile != null) {
                                writer.println(viewProfile);
                            } else {
                                writer.println("Something went wrong");
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



