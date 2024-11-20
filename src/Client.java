import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * Team Project Phase 2 - CoHabit
 * <p>
 * This program works to implement a roommate search algorithm
 *
 * @author Aidan Lefort, Andrew Tang, Keya Jadhav, Rithvik Siddenki, Rui Meng
 * @version November 3rd, 2024
 */

public class Client implements ClientService {

    private String username = "";
    private String password = "";
    private String email = "";
    private String phoneNumber = "";
    private String userDescription = "";
    private String university = "";

    private String bedTime;
    private boolean alcohol;
    private boolean smoke;
    private boolean guests;
    private int tidy;
    private int roomHours;

    private boolean isConnected;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private final String serverAddress = "localhost";
    private final int serverPort = 1102;

    public Client(User user) {
        if (user != null && user.getName() != null && user.getPassword() != null
                && user.getEmail() != null && user.getPhoneNumber() != null &&
                user.getDescription() != null && user.getUniversity() != null) {
            this.username = user.getName();
            this.password = user.getPassword();
            this.email = user.getEmail();
            this.phoneNumber = user.getPhoneNumber();
            this.userDescription = user.getDescription();
            this.university = user.getUniversity();
        } else {
            this.username = null;
            this.password = null;
            this.email = null;
            this.phoneNumber = null;
            this.userDescription = null;
            this.university = null;
        }
    }

    public static void main(String[] args) throws InvalidInput, UsernameTakenException, IOException {
        Scanner scanner = new Scanner(System.in);
        User user = null;
        Client client = new Client(user);

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
                    while (!loggedIn) {
                        while (true) {
                            System.out.println("Enter your username:");
                            client.setUsername(scanner.nextLine());

                            if (client.getUsername() == null) {
                                System.out.println("Username is invalid");
                            } else {
                                break;
                            }
                        }
                        while (true) {
                            System.out.println("Enter your password:");
                            client.setPassword(scanner.nextLine());

                            if (client.getPassword() == null) {
                                System.out.println("Password is invalid");
                            } else {
                                break;
                            }
                        }
                        if (client.login(client.getUsername(), client.getPassword())) {
                            loggedIn = true;
                            client.setUserInformation();
                            break;
                        } else {
                            System.out.println("Invalid username or password");
                        }
                    }
                    break;
                case "2":
                    while (true) {
                        System.out.println("Create a username:");
                        client.setUsername(scanner.nextLine());

                        if (client.getUsername() == null) {
                            System.out.println("Username is invalid");
                        } else if (client.getUsername().contains("###")) {
                            System.out.println("'###' is not allowed");
                        } else {
                            break;
                        }
                    }
                    while (true) {
                        System.out.println("Create a password: ");
                        client.setPassword(scanner.nextLine());

                        if (client.getPassword() == null) {
                            System.out.println("Password is invalid");
                        } else if (client.getPassword().contains("###")) {
                            System.out.println("'###' is not allowed");
                        } else {
                            break;
                        }
                    }
                    while (true) {
                        System.out.println("Enter your email:");
                        client.setEmail(scanner.nextLine());

                        if (client.getEmail() == null || !client.getEmail().contains("@") 
                            || !client.getEmail().contains(".")) {
                            System.out.println("client.getEmail() is invalid");
                        } else if (client.getEmail().contains("###")) {
                            System.out.println("'###' is not allowed");
                        } else {
                            break;
                        }
                    }
                    while (true) {
                        System.out.println("Enter your phone number: ");
                        client.setPhone(scanner.nextLine());
                        boolean notInt = false;
                        try {
                            long number = Long.parseLong(client.getPhone());
                        } catch (Exception e) {
                            notInt = true;
                        }

                        if (client.getPhone() == null) {
                            System.out.println("Phone number is invalid");
                        } else if (client.getPhone().contains("###")) {
                            System.out.println("'###' is not allowed");
                        } else if (client.getPhone().length() != 10) {
                            System.out.println("Phone number is invalid");
                        } else if (notInt) {
                            System.out.println("Not a number");
                        } else {
                            break;
                        }
                    }
                    while (true) {
                        System.out.println("Create a description:");
                        client.setUserDescription(scanner.nextLine());

                        if (client.getUserDescription() == null) {
                            System.out.println("Description is invalid");
                        } else if (client.getUserDescription().contains("###")) {
                            System.out.println("'###' is not allowed");
                        } else {
                            break;
                        }
                    }
                    while (true) {
                        System.out.println("Enter your university: ");
                        client.setUniversity(scanner.nextLine());

                        if (client.getUniversity() == null) {
                            System.out.println("University is invalid");
                        } else if (client.getPassword().contains("###")) {
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
                    client.setUserRegisterInformation(client.getUsername(), client.getPassword(),
                                                      client.getEmail(), client.getPhone(), 
                                                      client.getUserDescription(), client.getUniversity());
                    client.setPreferences(bedTime, alcohol, smoking, guests, tidy, roomHours);
                    if (client.register()) {
                        loggedIn = true;
                    }
                    break;
            }
        }
        while (!exit) {
            System.out.println("\nSelect an option:");
            System.out.println("1. Send Message");
            System.out.println("2. View Friend Requests");
            System.out.println("3. Send Friend Request");
            System.out.println("4. Remove Friend");
            System.out.println("5. Block User");
            System.out.println("6. Unblock User");
            System.out.println("7. View Profile");
            System.out.println("8. Update Profile");
            System.out.println("9. Search roommates");
            System.out.println("10. Disconnect and Exit");

            String choice2 = scanner.nextLine();

            switch (choice2) {
                case "1":
                    System.out.print("Enter receiver's username: ");
                    String receiver = scanner.nextLine();
                    System.out.print("Enter message: ");
                    String message = scanner.nextLine();
                    //client.fetchMessages(client.getUsername(), receiver);
                    client.sendMessage(receiver, message);
                    break;
                case "2":
                    client.viewFriendRequests(client.getUsername());
                    break;
                case "3":
                    System.out.print("Enter username to send friend request: ");
                    String friendRequestUsername = scanner.nextLine();
                    client.sendFriendRequest(client.getUsername(), friendRequestUsername);
                    break;
                case "4":
                    System.out.print("Enter username to remove as friend: ");
                    String removedFriend = scanner.nextLine();
                    client.removeFriend(client.getUsername(), removedFriend);
                    break;
                case "5":
                    System.out.print("Enter username to block: ");
                    String blockedUser = scanner.nextLine();
                    client.blockUser(client.getUsername(), blockedUser);
                    break;
                case "6":
                    System.out.print("Enter username to unblock: ");
                    String unblockUser = scanner.nextLine();
                    client.unblockUser(client.getUsername(), unblockUser);
                    break;
                case "7":
                    System.out.print("Enter a profile to view: ");
                    String profile = scanner.nextLine();
                    client.viewProfile(profile);
                    break;
                case "8":
                    System.out.println("\nChoose a parameter to update:");
                    System.out.println("1. Username");
                    System.out.println("2. Password");
                    System.out.println("3. Email");
                    System.out.println("4. Phone Number");
                    System.out.println("5. Description");
                    System.out.println("6. University");
                    System.out.println("7. Preferences");

                    String selection = scanner.nextLine();
                    String oldUsername = client.getUsername();
                    switch (selection) {
                        case "1":
                            System.out.print("Enter new username: ");
                            client.setUsername(scanner.nextLine());
                            break;
                        case "2":
                            System.out.print("Enter new password: ");
                            client.setPassword(scanner.nextLine());
                            break;
                        case "3":
                            System.out.print("Enter new email: ");
                            client.setEmail(scanner.nextLine());
                            break;
                        case "4":
                            System.out.print("Enter new phone number: ");
                            client.setPhone(scanner.nextLine());
                            break;
                        case "5":
                            System.out.print("Enter new description: ");
                            client.setUserDescription(scanner.nextLine());
                            break;
                        case "6":
                            System.out.print("Enter new university: ");
                            client.setUniversity(scanner.nextLine());
                            break;
                        case "7":
                            System.out.println("Updating preferences...");
                            // Call a method to collect preferences and update
                            System.out.print("Enter your average bed time (e.g., 22:30): ");
                            client.bedTime = scanner.nextLine();

                            System.out.print("Do you drink alcohol? (y/n): ");
                            client.alcohol = scanner.nextLine().equalsIgnoreCase("y");

                            System.out.print("Do you smoke? (y/n): ");
                            client.smoke = scanner.nextLine().equalsIgnoreCase("y");

                            System.out.print("Are you comfortable with guests? (y/n): ");
                            client.guests = scanner.nextLine().equalsIgnoreCase("y");

                            System.out.print("How tidy are you? (1-10): ");
                            client.tidy = Integer.parseInt(scanner.nextLine());

                            System.out.print("How many hours per day do you spend in your room? ");
                            client.roomHours = Integer.parseInt(scanner.nextLine());
                            break;
                        default:
                            System.out.println("Invalid selection.");
                    }

                    if (client.updateProfile(oldUsername)) {
                        System.out.println("Profile updated successfully.");
                    } else {
                        System.out.println("Profile update failed.");
                    }
                    break;
                case "9":
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
                                    break;
                                case "2":
                                    System.out.println("\nEnter the desired value:");
                                    parameter = scanner.nextLine();
                                    client.searchByParameter("email", parameter);
                                    break;
                                case "3":
                                    System.out.println("\nEnter the desired value:");
                                    parameter = scanner.nextLine();
                                    client.searchByParameter("phone", parameter);
                                    break;
                                case "4":
                                    System.out.println("\nEnter the desired value:");
                                    parameter = scanner.nextLine();
                                    client.searchByParameter("university", parameter);
                                    break;
                            }
                        case "2":
                            client.exactMatch(user);
                            break;
                        case "3":
                            client.partialMatch(user);
                            break;
                        default:
                            System.out.println("Invalid Input");
                            break;
                    }
                    break;
                case "10":
                    client.disconnect();
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
        scanner.close();
        System.out.println("Client exited.");
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phoneNumber;
    }

    public String getUniversity() {
        return university;
    }

    public String getUserDescription() {
        return userDescription;
    }

    public void setUsername(String usernameInput) {
        this.username = usernameInput;
    }

    public void setPassword(String passwordInput) {
        this.password = passwordInput;
    }

    public void setEmail(String emailInput) {
        this.email = emailInput;
    }

    public void setPhone(String phoneNumberInput) {
        this.phoneNumber = phoneNumberInput;
    }

    public void setUniversity(String universityInput) {
        this.university = universityInput;
    }

    public void setUserDescription(String userDescriptionInput) {
        this.userDescription = userDescriptionInput;
    }

    public boolean connect(String serverAddressInput, int port) {
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

    public boolean login(String usernameInput, String passwordInput) {
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

    public void setUserInformation() throws IOException {
        String information = in.readLine();
        String[] tokens = information.split("###");
        if (tokens.length != 6) {
            System.out.println("Error! Invalid User Information");
            return;
        }
        username = tokens[0];
        password = tokens[1];
        email = tokens[2];
        phoneNumber = tokens[3];
        userDescription = tokens[4];
        university = tokens[5];
    }

    public void setUserRegisterInformation(String usernameInput, String passwordInput, 
                                           String emailInput,
                                           String phoneNumberInput, 
                                           String userDescriptionInput, 
                                           String universityInput) {
        this.username = usernameInput;
        this.password = passwordInput;
        this.email = emailInput;
        this.phoneNumber = phoneNumberInput;
        this.userDescription = userDescriptionInput;
        this.university = universityInput;
    }

    public boolean register() {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return false;
        }
        out.println("register###" + username + "###" + password + "###" + email + "###"
                + phoneNumber + "###" + userDescription + "###" + university + "###" +
                bedTime + "###" + alcohol + "###" + smoke + "###" + guests + "###" +
                tidy + "###" + roomHours);

        try {
            String response = in.readLine();
            if ("successful registration".equals(response)) {
                System.out.println("User registered: " + username);
                return true;
            } else {
                System.out.println("Registration: " + response);
                return false;
            }
        } catch (IOException e) {
            System.out.println("Error during registration: " + e.getMessage());
            return false;
        }
    }

    public String getMessage() throws IOException {
        return in.readLine();
    }

    public boolean updateProfile(String oldUsername) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return false;
        }

        out.println("updateProfile###" + oldUsername + "###" + username + "###" + password + "###" + email + "###"
                + phoneNumber + "###" + userDescription + "###" + university + "###" +
                bedTime + "###" + alcohol + "###" + smoke + "###" + guests + "###" +
                tidy + "###" + roomHours);

        try {
            String response = in.readLine();
            if ("Profile Updated".equals(response)) {
                System.out.println("Profile Updated: " + username);
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

    public void setPreferences(String bedTimeInput, boolean alcoholInput, boolean smokeInput,
                               boolean guestsInput, int tidyInput, int roomHoursInput) {
        this.bedTime = bedTimeInput;
        this.alcohol = alcoholInput;
        this.smoke = smokeInput;
        this.guests = guestsInput;
        this.tidy = tidyInput;
        this.roomHours = roomHoursInput;
        if (bedTimeInput == null || tidyInput <= 0 || tidyInput > 10 || roomHoursInput < 0) {
            System.out.println("Invalid Input");
        }
    }

    public boolean sendMessage(String receiver, String message) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return false;
        }

        out.println("sendMessage###" + username + "###" + receiver + "###" + message);

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

        out.println("loadMessages," + username + "," + receiver + "," + receiver);

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
                String[] tokens = requester.split(":");

                System.out.println("Friend request from: " + tokens[0]);
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

    public boolean acceptFriendRequest(String friend) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return false;
        }

        out.println("acceptFriendRequest," + this.username + "," + friend);

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

    public boolean declineFriendRequest(String usernameInput) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return false;
        }

        out.println("declineFriendRequest," + usernameInput);

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

    public void viewProfile(String usernameInput) {
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

    public void viewFriendsList(String usernameInput) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return;
        }

        out.println("viewFriendsList," + usernameInput);

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

    public boolean unblockUser(String user, String blockedUser) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return false;
        }

        out.println("removeBlockedUser," + user + "," + blockedUser);

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


    public void viewBlockedUsers(String usernameInput) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return;
        }

        out.println("viewBlockedUsers," + usernameInput);

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
                        if (unblockUser(usernameInput, blockedUser)) {
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

        out.println("exactMatch," + username);

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

        out.println("partialMatch," + username);

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
        out.println("updatePreferences," + username + "," + preferences.replace(",", "###"));

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
