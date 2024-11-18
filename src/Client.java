import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client implements ClientService {

    private static User currentUser;
    private boolean isConnected;
    private Socket socket;
    private static PrintWriter out;
    private static BufferedReader in;
    private final String serverAddress = "localhost";
    private final int serverPort = 1102;

    public Client(User user) {
        this.currentUser = user;
    }

    public static void main(String[] args) throws InvalidInput, UsernameTakenException, IOException {
        Scanner scanner = new Scanner(System.in);
        Client client = new Client(currentUser);
        String username = "";
        String password = "";
        String email = "";
        String phoneNumber = "";
        String userDescription = "";
        String university = "";

        if (!client.connect(client.serverAddress, client.serverPort)) {
            System.out.println("Failed to connect to the server. Exiting.");
            return;
        }

        boolean exit = false;

        boolean loggedIn = false;

        while (!loggedIn) {
            System.out.println("\nSelect an option:");
            System.out.println("1. Login");
            System.out.println("2. Register");

            String choice1 = scanner.nextLine();

            switch (choice1) {
                case "1":
                    while (true) {
                        while (true) {
                            System.out.println("Enter your username:");
                            username = scanner.nextLine();

                            if (username == null) {
                                System.out.println("Username is invalid");
                            } else {
                                break;
                            }
                        }
                        while (true) {
                            System.out.println("Enter your password:");
                            password = scanner.nextLine();

                            if (password == null) {
                                System.out.println("Password is invalid");
                            } else {
                                break;
                            }
                        }
                        client.login(username, password);
                        String message = in.readLine();
                        if (message.equals("Successful Login")) {
                            loggedIn = true;
                            break;
                        } else {
                            System.out.println("Invalid username or password");
                        }
                    }
                case "2":
                    while (true) {
                        System.out.println("Create a username:");
                        username = scanner.nextLine();

                        if (username == null) {
                            System.out.println("Username is invalid");
                        } else if (username.contains("###")) {
                            System.out.println("'###' is not allowed");
                        } else {
                            break;
                        }
                    }
                    while (true) {
                        System.out.println("Create a password: ");
                        password = scanner.nextLine();

                        if (password == null) {
                            System.out.println("Password is invalid");
                        } else if (password.contains("###")) {
                            System.out.println("'###' is not allowed");
                        } else {
                            break;
                        }
                    }
                    while (true) {
                        System.out.println("Enter your email:");
                        email = scanner.nextLine();

                        if (email == null || !email.contains("@") || !email.contains(".")) {
                            System.out.println("email is invalid");
                        } else if (email.contains("###")) {
                            System.out.println("'###' is not allowed");
                        } else {
                            break;
                        }
                    }
                    while (true) {
                        System.out.println("Enter your phone number: ");
                        phoneNumber = scanner.nextLine();
                        boolean notInt = false;
                        try {
                            long number = Long.parseLong(phoneNumber);
                        } catch (Exception e) {
                            notInt = true;
                        }

                        if (phoneNumber == null) {
                            System.out.println("Phone number is invalid");
                        } else if (phoneNumber.contains("###")) {
                            System.out.println("'###' is not allowed");
                        } else if (phoneNumber.length() != 10) {
                            System.out.println("Phone number is invalid");
                        } else if (notInt) {
                            System.out.println("Not a number");
                        } else {
                            break;
                        }
                    }
                    while (true) {
                        System.out.println("Create a description:");
                        userDescription = scanner.nextLine();

                        if (userDescription == null) {
                            System.out.println("Description is invalid");
                        } else if (userDescription.contains("###")) {
                            System.out.println("'###' is not allowed");
                        } else {
                            break;
                        }
                    }
                    while (true) {
                        System.out.println("Create a university: ");
                        university = scanner.nextLine();

                        if (university == null) {
                            System.out.println("University is invalid");
                        } else if (password.contains("###")) {
                            System.out.println("'###' is not allowed");
                        } else {
                            break;
                        }
                    }
                    String bedTime;
                    while (true) {
                        System.out.println("What is your average bed time?");
                        bedTime = scanner.nextLine();
                        boolean nan = false;

                        try {
                            int time = Integer.parseInt(bedTime.replace(":", ""));
                        } catch (Exception e) {
                            nan = true;
                        }

                        if (bedTime == null) {
                            System.out.println("Bed time is invalid");
                        } else if (!bedTime.contains(":")) {
                            System.out.println("Bed time is invalid");
                        } else if (nan) {
                            System.out.println("Not a number");
                        } else {
                            break;
                        }
                    }
                    boolean alcohol;
                    String answer;
                    while (true) {
                        System.out.println("Do you drink alcohol? (y/n)");
                        answer = scanner.nextLine();

                        if (answer.equals("y")) {
                            alcohol = true;
                            break;
                        } else if (answer.equals("n")) {
                            alcohol = false;
                            break;
                        } else {
                            System.out.println("Invalid Input");
                        }
                    }
                    boolean smoking;
                    while (true) {
                        System.out.println("Do you smoke? (y/n)");
                        answer = scanner.nextLine();

                        if (answer.equals("y")) {
                            smoking = true;
                            break;
                        } else if (answer.equals("n")) {
                            smoking = false;
                            break;
                        } else {
                            System.out.println("Invalid Input");
                        }
                    }
                    boolean guests;
                    while (true) {
                        System.out.println("Are you comfortable with guests? (y/n)");
                        answer = scanner.nextLine();

                        if (answer.equals("y")) {
                            guests = true;
                            break;
                        } else if (answer.equals("n")) {
                            guests = false;
                            break;
                        } else {
                            System.out.println("Invalid Input");
                        }
                    }
                    int tidy;
                    while (true) {
                        System.out.println("How tidy are you? (1-10)");
                        try {
                            tidy = scanner.nextInt();
                            scanner.nextLine();
                            if (tidy <= 10 && tidy >= 1) {
                                break;
                            } else {
                                System.out.println("Outside of range");
                            }
                        } catch (Exception e) {
                            System.out.println("Invalid Input");
                        }
                    }
                    int roomHours;
                    while (true) {
                        System.out.println("How many hours per day on average do you spend in your room?");
                        try {
                            roomHours = scanner.nextInt();
                            scanner.nextLine();
                            if (roomHours >= 1 && roomHours <= 24) {
                                break;
                            } else {
                                System.out.println("Outside of Range");
                            }
                        } catch (Exception e) {
                            System.out.println("Invalid Input");
                        }
                    }
                    currentUser = new User(username, password, email, phoneNumber, userDescription, university);
                    currentUser.setPreferences(bedTime, alcohol, smoking, guests, tidy, roomHours);
                    client.register(currentUser);
                    loggedIn = true;
                    break;
            }
        }

        while (!exit) {
            System.out.println("\nSelect an option:");
            System.out.println("1. Send Message");
            System.out.println("2. View Friend Requests");
            System.out.println("3. Send Friend Request");
            System.out.println("4. Add Friend");
            System.out.println("5. Remove Friend");
            System.out.println("6. Block User");
            System.out.println("7. View and Manage Blocked Users");
            System.out.println("8. View Profile");
            System.out.println("9. Update Profile");
            System.out.println("10. Search roommates");
            System.out.println("11. Disconnect and Exit");

            String choice2 = scanner.nextLine();

            switch (choice2) {
                case "1":
                    System.out.print("Enter receiver's username: ");
                    String receiver = scanner.nextLine();
                    System.out.print("Enter message: ");
                    String message = scanner.nextLine();
                    client.fetchMessages(username, receiver);
                    client.sendMessage(receiver, message);
                    break;
                case "2":
                    client.viewFriendRequests(username);
                    break;
                case "3":
                    System.out.print("Enter username to send friend request: ");
                    String friendRequestUsername = scanner.nextLine();
                    client.sendFriendRequest(username, friendRequestUsername);
                    break;
                case "4":
                    System.out.print("Enter username to add as friend: ");
                    String friendUsername = scanner.nextLine();
                    client.addFriend(username, friendUsername);
                    break;
                case "5":
                    System.out.print("Enter username to remove as friend: ");
                    String removedFriend = scanner.nextLine();
                    client.removeFriend(username, removedFriend);
                    break;
                case "6":
                    System.out.print("Enter username to block: ");
                    String blockedUser = scanner.nextLine();
                    client.blockUser(username, blockedUser);
                    break;
                case "7":
                    client.viewBlockedUsers(username);
                    break;
                case "8":
                    System.out.print("Enter a profile to view: ");
                    String profile = scanner.nextLine();
                    client.viewProfile(profile);
                    break;
                case "9":
                    System.out.println("\nChoose a parameter to update:");
                    System.out.println("1. Username");
                    System.out.println("2. Password");
                    System.out.println("3. Email");
                    System.out.println("4. Phone Number");
                    System.out.println("5. Description");
                    System.out.println("6. University");
                    System.out.println("7. Preferences");

                    String selection = scanner.nextLine();


                    client.updateProfile(currentUser);
                case "10":
                    System.out.println("\nHow would you like to search?");
                    System.out.println("1. By Parameter");
                    System.out.println("2. Exact Match");
                    System.out.println("3. Partial Match");

                    String option1 = scanner.nextLine();
                    String option2;
                    String parameter;
                    String value;

                    if (!option1.equals("1") || !option1.equals("2") || !option1.equals("3")) {
                        System.out.println("Invalid Input");
                    }

                    switch (option1) {
                        case "1":
                            System.out.println("\nSelect a parameter:");
                            System.out.println("1. Username");
                            System.out.println("2. Email");
                            System.out.println("3. Phone");
                            System.out.println("4. University");

                            option2 = scanner.nextLine();

                            switch (option2) {
                                case "1":
                                    System.out.println("\nEnter the desired value:");
                                    parameter = scanner.nextLine();
                                    client.searchByParameter("name", parameter);
                                case "2":
                                    System.out.println("\nEnter the desired value:");
                                    parameter = scanner.nextLine();
                                    client.searchByParameter("email", parameter);
                                case "3":
                                    System.out.println("\nEnter the desired value:");
                                    parameter = scanner.nextLine();
                                    client.searchByParameter("phone", parameter);
                                case "4":
                                    System.out.println("\nEnter the desired value:");
                                    parameter = scanner.nextLine();
                                    client.searchByParameter("university", parameter);
                            }

                        case "2":
                            client.exactMatch(currentUser);
                        case "3":
                            client.partialMatch(currentUser);
                    }

                case "11":
                    client.disconnect();
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
        System.out.println("Client exited.");
    }

    public boolean connect(String serverAddress, int port) {
        try {
            socket = new Socket(serverAddress, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            isConnected = true;
            System.out.println("Connected to the server at " + serverAddress + ":" + port);
            return true;
        } catch (IOException e) {
            System.out.println("Error connecting to server: " + e.getMessage());
            return false;
        }
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) {
                out.close();
                in.close();
                socket.close();
                isConnected = false;
                System.out.println("Disconnected from the server.");
            }
        } catch (IOException e) {
            System.out.println("Error disconnecting from server: " + e.getMessage());
        }
    }

    public boolean login(String username, String password) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return false;
        }

        out.println("login," + username + "," + password);

        try {
            String response = in.readLine();
            if ("Successful login".equals(response)) {
                System.out.println("Login successful.");
                return true;
            } else {
                System.out.println("Login failed: " + response);
                return false;
            }
        } catch (IOException e) {
            System.out.println("Error during login: " + e.getMessage());
            return false;
        }
    }

    public boolean register(User user) throws UsernameTakenException {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return false;
        }
        this.currentUser = new User(user.getName(), user.getPassword(), user.getEmail(), user.getPhoneNumber(), user.getDescription(), user.getUniversity());

        out.println("register###" + user.getName() + "###" + user.getPassword() + "###" + user.getEmail() + "###"
                + user.getPhoneNumber() + "###" + user.getDescription() + "###" + user.getUniversity() + "###" +
                user.getPreferences().replace(", ", "###"));


        try {
            String response = in.readLine();
            if ("successful registration".equals(response)) {
                System.out.println("User registered: " + user.getName());
                return true;
            } else {
                System.out.println("Registration: " + response);
                return false;
            }
        } catch (IOException e) {
            System.out.println();
            return false;
        }
    }

    public boolean updateProfile(User user) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return false;
        }

        out.println("updateProfile###" + user.getName() + "###" + user.getPassword() + "###" + user.getEmail() + "###"
                + user.getPhoneNumber() + "###" + user.getDescription() + "###" + user.getUniversity() + "###" + user.getPreferences().replace(",", "###"));

        try {
            String response = in.readLine();
            if ("Profile Updated".equals(response)) {
                System.out.println("Profile Updated: " + user.getName());
                return true;
            } else {
                System.out.println("Updating failed: " + response);
                return false;
            }
        } catch (IOException e) {
            System.out.println("Error during updating: " + e.getMessage());
            return false;
        }
    }

    public boolean sendMessage(String receiver, String message) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return false;
        }

        out.println("sendMessage###" + currentUser.getName() + "###" + receiver + "###" + message);

        try {
            String response = in.readLine();
            if ("Successfully sent message".equals(response)) {
                System.out.println("Message sent to " + receiver);
                return true;
            } else {
                System.out.println("Failed to send message.");
                return false;
            }
        } catch (IOException e) {
            System.out.println("Error sending message: " + e.getMessage());
            return false;
        }
    }

    public String fetchMessages(String user, String receiver) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return null;
        }

        out.println("loadMessages," + currentUser.getName() + "," + receiver + "," + receiver);

        try {
            String response = in.readLine();
            if ("Message List is Empty".equals(response)) {
                return null;
            } else {
                return response;
            }
        } catch (IOException e) {
            System.out.println("Error loading message history");
            return null;
        }
    }

    public boolean sendFriendRequest(String user, String potentialFriend) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return false;
        }

        out.println("sendFriendRequest," + user + "," + potentialFriend);

        try {
            String response = in.readLine();
            if ("Successfully sent friend request".equals(response)) {
                System.out.println("Friend request sent to " + potentialFriend);
                return true;
            } else {
                System.out.println("Friend request failed.");
                return false;
            }
        } catch (IOException e) {
            System.out.println("Error sending friend request: " + e.getMessage());
            return false;
        }
    }

    public void viewFriendRequests(String user) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return;
        }

        out.println("viewFriendRequests," + user);

        try {
            String response = in.readLine();
            if (response == null || response.isEmpty() || "No friend requests".equals(response)) {
                System.out.println("You have no pending friend requests.");
                return;
            }

            String[] friendRequests = response.split(",");
            Scanner scanner = new Scanner(System.in);

            for (String requester : friendRequests) {
                System.out.println("Friend request from: " + requester);
                System.out.println("Do you want to (1) Accept or (2) Decline?");
                String choice = scanner.nextLine();

                switch (choice) {
                    case "1":
                        if (acceptFriendRequest(requester)) {
                            System.out.println("You accepted the friend request from: " + requester);
                        } else {
                            System.out.println("Failed to accept friend request from: " + requester);
                        }
                        break;
                    case "2":
                        if (declineFriendRequest(requester)) {
                            System.out.println("You declined the friend request from: " + requester);
                        } else {
                            System.out.println("Failed to decline friend request from: " + requester);
                        }
                        break;
                    default:
                        System.out.println("Invalid option. Skipping request from: " + requester);
                }
            }
        } catch (IOException e) {
            System.out.println("Error viewing friend requests: " + e.getMessage());
        }
    }

    public boolean acceptFriendRequest(String username) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return false;
        }

        out.println("acceptFriendRequest," + username);

        try {
            String response = in.readLine();
            return "Successfully accepted friend request".equals(response);
        } catch (IOException e) {
            System.out.println("Error accepting friend request: " + e.getMessage());
            return false;
        }
    }

    public boolean declineFriendRequest(String username) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return false;
        }

        out.println("declineFriendRequest," + username);

        try {
            String response = in.readLine();
            return "Successfully declined friend request".equals(response);
        } catch (IOException e) {
            System.out.println("Error declining friend request: " + e.getMessage());
            return false;
        }
    }

    public boolean addFriend(String user, String friend) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return false;
        }

        out.println("addFriend," + user + "," + friend);

        try {
            String response = in.readLine();
            if ("Successfully added friend".equals(response)) {
                System.out.println(friend + " is now your friend.");
                return true;
            } else {
                System.out.println("Failed to add friend.");
                return false;
            }
        } catch (IOException e) {
            System.out.println("Error adding friend: " + e.getMessage());
            return false;
        }
    }

    public boolean removeFriend(String user, String friend) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return false;
        }

        out.println("removeFriend," + user + "," + friend);

        try {
            String response = in.readLine();
            if ("Successfully removed friend".equals(response)) {
                System.out.println(friend + " has been removed from your friend list.");
                return true;
            } else {
                System.out.println("Failed to remove friend.");
                return false;
            }
        } catch (IOException e) {
            System.out.println("Error removing friend: " + e.getMessage());
            return false;
        }
    }

    public boolean blockUser(String user, String blockedUser) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return false;
        }

        out.println("blockUser," + user + "," + blockedUser);

        try {
            String response = in.readLine();
            if ("Successfully blocked user".equals(response)) {
                System.out.println(blockedUser + " has been blocked.");
                return true;
            } else {
                System.out.println("Failed to block user.");
                return false;
            }
        } catch (IOException e) {
            System.out.println("Error blocking user: " + e.getMessage());
            return false;
        }
    }

    public void viewProfile(String username) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return;
        }

        out.println("viewProfile," + username);

        try {
            String response = in.readLine();
            System.out.println("Profile data: " + response);
        } catch (IOException e) {
            System.out.println("Error viewing profile: " + e.getMessage());
        }
    }

    public void viewFriendsList(String username) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return;
        }

        out.println("viewFriendsList," + username);

        try {
            String response = in.readLine();
            if (response.equals("Friend list is empty")) {
                System.out.println("You have no friends yet.");
            } else {
                System.out.println("Your Friends:\n" + response);
            }
        } catch (IOException e) {
            System.out.println("Error viewing friends list: " + e.getMessage());
        }
    }

    public boolean unblockUser(String username, String blockedUser) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return false;
        }

        out.println("removeBlockedUser," + username + "," + blockedUser);

        try {
            String response = in.readLine();
            if ("Successfully removed from blocked list".equals(response)) {
                System.out.println(blockedUser + " has been unblocked.");
                return true;
            } else {
                System.out.println("Failed to unblock user.");
                return false;
            }
        } catch (IOException e) {
            System.out.println("Error unblocking user: " + e.getMessage());
            return false;
        }
    }


    public void viewBlockedUsers(String username) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return;
        }
        out.println("viewBlockedUsers," + username);

        try {
            String response = in.readLine();
            if (response == null) {
                System.out.println("You have not blocked anyone.");
                return;
            }

            String[] blockedUsers = response.split(",");
            Scanner scanner = new Scanner(System.in);

            for (String blockedUser : blockedUsers) {
                System.out.println("Blocked User: " + blockedUser);
                System.out.println("Do you want to unblock this user? (1) Yes or (2) No");
                String choice = scanner.nextLine();

                switch (choice) {
                    case "1":
                        if (unblockUser(username, blockedUser)) {
                            System.out.println("You have unblocked " + blockedUser);
                        } else {
                            System.out.println("Failed to unblock " + blockedUser);
                        }
                        break;
                    case "2":
                        System.out.println("You chose not to unblock " + blockedUser);
                        break;
                    default:
                        System.out.println("Invalid option. Skipping " + blockedUser);
                }
            }
        } catch (IOException e) {
            System.out.println("Error viewing blocked users: " + e.getMessage());
        }
    }

    public void searchByParameter(String parameter, String value) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return;
        }

        out.println("searchByParameter," + parameter + "," + value);

        try {
            String response = in.readLine();
            if (response.equals("No matches found")) {
                System.out.println("No users found with " + parameter + ": " + value);
            } else {
                System.out.println("Users matching " + parameter + " = " + value + ":\n" + response);
            }
        } catch (IOException e) {
            System.out.println("Error searching users: " + e.getMessage());
        }
    }

    public void exactMatch(User user) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return;
        }

        out.println("exactMatch," + user.getName());

        try {
            String response = in.readLine();
            if (response.equals("No exact matches found")) {
                System.out.println("No exact matches found for your preferences.");
            } else {
                System.out.println("Exact Matches:\n" + response);
            }
        } catch (IOException e) {
            System.out.println("Error finding exact matches: " + e.getMessage());
        }
    }

    public void partialMatch(User user) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return;
        }

        out.println("partialMatch," + user.getName());

        try {
            String response = in.readLine();
            if (response.equals("No partial matches found")) {
                System.out.println("No partial matches found for your preferences.");
            } else {
                System.out.println("Partial Matches:\n" + response);
            }
        } catch (IOException e) {
            System.out.println("Error finding partial matches: " + e.getMessage());
        }
    }

    public void updatePreferences(User user) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return;
        }

        String preferences = user.getPreferences();
        out.println("updatePreferences," + user.getName() + "," + preferences.replace(",", "###"));

        try {
            String response = in.readLine();
            if ("Preferences Updated".equals(response)) {
                System.out.println("Your preferences have been updated.");
            } else {
                System.out.println("Failed to update preferences.");
            }
        } catch (IOException e) {
            System.out.println("Error updating preferences: " + e.getMessage());
        }
    }
}
