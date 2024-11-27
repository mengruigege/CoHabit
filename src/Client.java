
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

    private String username;
    private String password;
    private String email;
    private String phoneNumber;
    private String userDescription;
    private String university;

    private String bedTime;
    private boolean alcohol;
    private boolean smoke;
    private boolean guests;
    private int tidy;
    private int roomHours;

    private boolean isConnected;
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private Scanner scanner;

    private final String serverAddress = "localhost";
    private final int serverPort = 1102;

    private static final String DELIMITER = "<<END>>";
    private static final String SUCCESS = "SUCCESS";
    private static final String FAILURE = "FAILURE";

    //constructor
    public Client(String serverAddress, int serverPort) {
        try {
            socket = new Socket(serverAddress, serverPort);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            scanner = new Scanner(System.in);
            System.out.println("Connected to " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
        } catch (IOException e) {
            System.out.println("Could not connect to " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
        }
    }

    public static void main(String[] args) {
        Client client = new Client("localhost", 1102);
        client.start();
    }

    public void start() {
        boolean exit = false;
        while (!exit) {
            System.out.println("Select an option:\n1. Login\n2. Register\n3. Exit");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    if (login()) {
                        mainScreen();
                    }
                    break;
                case "2":
                    if (register()) {
                        mainScreen();
                    }
                    break;
                case "3":
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid option, please try again");
            }
        }
        close();
    }

    public void close() {
        try {
            if (socket != null) socket.close();
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (scanner != null) scanner.close();
        } catch (IOException e) {
            System.out.println("Could not close client" + e.getMessage());
        }
    }

    public void mainScreen() {
        boolean exit = false;
        String choice;

        while (!exit) {
            System.out.println("\nSelect an option:");
            System.out.println("1. Send Message");
            System.out.println("2. View Message");
            System.out.println("3. View Friend Requests");
            System.out.println("4. Send Friend Request");
            System.out.println("5. Remove Friend");
            System.out.println("6. Block User");
            System.out.println("7. Unblock User");
            System.out.println("8. View Profile");
            System.out.println("9. Update Profile");
            System.out.println("10. Search roommates");
            System.out.println("11. Disconnect and Exit");
            choice = scanner.nextLine();
        }

        switch (choice) {
            case "1":
                sendMessage();
                break;
            case "2":
                viewMessage();
                break;
            case "3":
                viewFriendRequests();
                break;
            case "4":
                sendFriendRequest();
                break;
            case "5":
                removeFriend();
                break;
            case "6":
                blockUser();
                break;
            case "7":
                unblockUser();
                break;
            case "8":
                viewProfile();
                break;
            case "9":
                updateProfile();
                break;
            case "10":
                searchRoommates();
                break;
            case "11":
                disconnect();
                break;
            default:
                System.out.println("Invalid option, please try again");
        }
    }

    public boolean login() {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return false;
        }

        System.out.print("Enter your username: ");
        String usernameInput = scanner.nextLine();
        System.out.print("Enter your password: ");
        String passwordInput = scanner.nextLine();

        // Send login request
        writer.println("login" + DELIMITER + usernameInput + DELIMITER + passwordInput);

        try {
            String response = reader.readLine();
            if (response == null || response.equals(FAILURE)) {
                System.out.println("Login failed. Please check your username and password.");
                return false;
            }

            // Parse user information on success
            String[] userInfo = response.split(DELIMITER);
            if (userInfo.length != 6) {
                System.out.println("Error: Invalid response from server.");
                return false;
            }

            // Set user data
            username = userInfo[0];
            password = userInfo[1];
            email = userInfo[2];
            phoneNumber = userInfo[3];
            userDescription = userInfo[4];
            university = userInfo[5];

            System.out.println("Login successful. Welcome, " + username + "!");
            return true;
        } catch (IOException e) {
            System.out.println("Error during login: " + e.getMessage());
            return false;
        }
    }

    public boolean register() {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return false;
        }

        System.out.print("Create a username: ");
        username = scanner.nextLine();
        System.out.print("Create a password: ");
        password = scanner.nextLine();
        System.out.print("Enter your email: ");
        email = scanner.nextLine();
        System.out.print("Enter your phone number: ");
        phoneNumber = scanner.nextLine();
        System.out.print("Enter a brief description about yourself: ");
        userDescription = scanner.nextLine();
        System.out.print("Enter your university: ");
        university = scanner.nextLine();
        System.out.print("What is your average bedtime (e.g., 22:30)? ");
        bedTime = scanner.nextLine();
        System.out.print("Do you drink alcohol? (yes/no): ");
        alcohol = scanner.nextLine().equalsIgnoreCase("yes");
        System.out.print("Do you smoke? (yes/no): ");
        smoke = scanner.nextLine().equalsIgnoreCase("yes");
        System.out.print("Are you comfortable with guests? (yes/no): ");
        guests = scanner.nextLine().equalsIgnoreCase("yes");
        System.out.print("How tidy are you (1-10)? ");
        tidy = Integer.parseInt(scanner.nextLine());
        System.out.print("How many hours do you spend in your room daily? ");
        roomHours = Integer.parseInt(scanner.nextLine());

        // Send registration request
        writer.println("register" + DELIMITER + username + DELIMITER + password + DELIMITER + email + DELIMITER
                + phoneNumber + DELIMITER + userDescription + DELIMITER + university + DELIMITER +
                bedTime + DELIMITER + alcohol + DELIMITER + smoke + DELIMITER + guests + DELIMITER +
                tidy + DELIMITER + roomHours);

        try {
            String response = reader.readLine();
            if (response.equals(SUCCESS)) {
                System.out.println("Registration successful. Welcome, " + username + "!");
                return true;
            } else {
                System.out.println("Registration failed: " + response);
                return false;
            }
        } catch (IOException e) {
            System.out.println("Error during registration: " + e.getMessage());
            return false;
        }
    }

    public boolean sendMessage() {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return false;
        }

        System.out.print("Enter the username of the recipient: ");
        String recipient = scanner.nextLine();
        System.out.print("Enter your message: ");
        String message = scanner.nextLine();

        // Send message to server
        writer.println("sendMessage" + DELIMITER + username + DELIMITER + recipient + DELIMITER + message);

        try {
            String response = reader.readLine();
            if (response.equals(SUCCESS)) {
                System.out.println("Message sent to " + recipient);
                return true;
            } else {
                System.out.println("Failed to send message: " + response);
                return false;
            }
        } catch (IOException e) {
            System.out.println("Error sending message: " + e.getMessage());
            return false;
        }
    }

    public void viewMessage() {
        if (!isConnected) {
            System.out.println("Not connected to the server.");
            return;
        }

        System.out.print("Enter the username to view messages with: ");
        String recipient = scanner.nextLine();

        // Request message history from server
        writer.println("getMessageHistory" + DELIMITER + username + DELIMITER + recipient);

        try {
            String response = reader.readLine();
            if (response == null || response.equals(FAILURE)) {
                System.out.println("No messages found with " + recipient);
            } else {
                System.out.println("Messages with " + recipient + ":");
                for (String message : response.split(DELIMITER)) {
                    System.out.println(message);
                }
            }
        } catch (IOException e) {
            System.out.println("Error retrieving messages: " + e.getMessage());
        }
    }



    public void disconnect() {
        if (!isConnected) {
            System.out.println("Not connected to the server.");
            return;
        }

        try {
            socket.close();
            isConnected = false;
            System.out.println("Disconnected from the server.");
        } catch (IOException e) {
            System.out.println("Error disconnecting: " + e.getMessage());
        }
    }


    //main method
    public static void main(String[] args) throws IOException {
        Client client = new Client("localhost", 1102);

        if (!client.connect(client.serverAddress, client.serverPort)) {
            System.out.println("Failed to connect to the server. Exiting.");
            return;
        }

        boolean exit = false;
        boolean loggedIn = false;

        // To check if user wants to log in or register
        while (!loggedIn) {
            System.out.println("\nSelect an option:");
            System.out.println("1. Login");
            System.out.println("2. Register");

            String choice1 = scanner.nextLine();

            switch (choice1) {
                case "1":
                    //If user wants to log in: checks username and password
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
                        if (!loggedIn) {
                            System.out.println("Please try again");
                        }
                    }
                    break;
                case "2":
                    //If user wants to register: sets personal details and preferences
                    while (true) {
                        System.out.println("Create a username:");
                        client.setUsername(scanner.nextLine());

                        if (client.getUsername() == null) {
                            System.out.println("Username is invalid");
                        } else if (client.getUsername().contains(DELIMITER)) {
                            System.out.println(DELIMITER + " is not allowed");
                        } else {
                            break;
                        }
                    }
                    while (true) {
                        System.out.println("Create a password: ");
                        client.setPassword(scanner.nextLine());

                        if (client.getPassword() == null) {
                            System.out.println("Password is invalid");
                        } else if (client.getPassword().contains(DELIMITER)) {
                            System.out.println(DELIMITER + " is not allowed");
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
                        } else if (client.getEmail().contains(DELIMITER)) {
                            System.out.println(DELIMITER + " is not allowed");
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
                        } else if (client.getPhone().contains(DELIMITER)) {
                            System.out.println(DELIMITER + " is not allowed");
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
                        } else if (client.getUserDescription().contains(DELIMITER)) {
                            System.out.println(DELIMITER + " is not allowed");
                        } else {
                            break;
                        }
                    }
                    while (true) {
                        System.out.println("Enter your university: ");
                        client.setUniversity(scanner.nextLine());

                        if (client.getUniversity() == null) {
                            System.out.println("University is invalid");
                        } else if (client.getPassword().contains(DELIMITER)) {
                            System.out.println(DELIMITER + " is not allowed");
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

                        if (answer.equalsIgnoreCase("y")) {
                            smoking = true;
                            break;
                        } else if (answer.equalsIgnoreCase("n")) {
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

        //For user to choose what action they want to perform in the app
        while (!exit) {
            System.out.println("\nSelect an option:");
            System.out.println("1. Send Message");
            System.out.println("2. View Message");
            System.out.println("3. View Friend Requests");
            System.out.println("4. Send Friend Request");
            System.out.println("5. Remove Friend");
            System.out.println("6. Block User");
            System.out.println("7. Unblock User");
            System.out.println("8. View Profile");
            System.out.println("9. Update Profile");
            System.out.println("10. Search roommates");
            System.out.println("11. Disconnect and Exit");

            String choice2 = scanner.nextLine();

            switch (choice2) {
                case "1":
                    //To send messages
                    System.out.print("Enter receiver's username: ");
                    String receiver = scanner.nextLine();
                    System.out.print("Enter message: ");
                    String message = scanner.nextLine();
                    client.sendMessage(receiver, message);
                    break;
                case "2":
                    //To view incoming messages
                    System.out.print("Enter the username of the person whose messages you want to view: ");
                    String receiverUsername = scanner.nextLine();
                    client.viewMessage(receiverUsername);
                    break;
                case "3":
                    //To view friend requests
                    client.viewFriendRequests(client.getUsername());
                    break;
                case "4":
                    //To send friend requests
                    System.out.print("Enter username to send friend request: ");
                    String friendRequestUsername = scanner.nextLine();
                    client.sendFriendRequest(client.getUsername(), friendRequestUsername);
                    break;
                case "5":
                    //To remove friend
                    System.out.print("Enter username to remove as friend: ");
                    String removedFriend = scanner.nextLine();
                    client.removeFriend(client.getUsername(), removedFriend);
                    break;
                case "6":
                    //To block a user
                    System.out.print("Enter username to block: ");
                    String blockedUser = scanner.nextLine();
                    client.blockUser(client.getUsername(), blockedUser);
                    break;
                case "7":
                    //To unblock a user
                    System.out.print("Enter username to unblock: ");
                    String unblockUser = scanner.nextLine();
                    client.unblockUser(client.getUsername(), unblockUser);
                    break;
                case "8":
                    //To view a profile
                    System.out.print("Enter a profile to view: ");
                    String profile = scanner.nextLine();
                    client.viewProfile(profile);
                    break;
                case "9":
                    //To update user profile
                    System.out.println("\nChoose a parameter to update:");
                    System.out.println("1. Username");
                    System.out.println("2. Password");
                    System.out.println("3. Email");
                    System.out.println("4. Phone Number");
                    System.out.println("5. Description");
                    System.out.println("6. University");
                    System.out.println("7. Preferences");
                    System.out.println("8. Profile Picture");

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
                        case "8":
                            System.out.println("Enter the file path for your new profile picture (e.g., C:\\Users\\User\\picture.png):");
                            String filePath = scanner.nextLine();

                            if (client.setProfilePicture(filePath)) {
                                System.out.println("Profile picture uploaded successfully.");
                            } else {
                                System.out.println("Failed to upload profile picture.");
                            }
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
                case "10":
                    //To search for roommates based on preferences
                    System.out.println("\nHow would you like to search?");
                    System.out.println("1. By Parameter");
                    System.out.println("2. Exact Match");
                    System.out.println("3. Partial Match");

                    String option1 = scanner.nextLine();
                    String option2;
                    String parameter;
                    String value;

                    if (!option1.equals("1") && !option1.equals("2") && !option1.equals("3")) {
                        System.out.println("Invalid Input");
                    }

                    switch (option1) {
                        case "1":
                            //If you want to search for a roommate based on parameter
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
                            break;
                        case "2":
                            //To search for roommate based on exact match of preferences
                            client.exactMatch(user);
                            break;
                        case "3":
                            //To search for roommate based on partial match of preferences
                            client.partialMatch(user);
                            break;
                        default:
                            System.out.println("Invalid Input");
                            break;
                    }
                    break;
                case "11":
                    //To disconnect client from server
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

    //getters
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

    //setters
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

    //To connect client to server

    public boolean connect(String serverAddressInput, int port) {
        try {
            socket = new Socket(serverAddress, port);
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            isConnected = true;
            System.out.println("Connected to the server at " + serverAddress + ":" + port);
            return true;
        } catch (IOException e) {
            System.out.println("Error connecting to server: " + e.getMessage());
            return false;
        }
    }

    //To check if client is connected to server
    
    public boolean isConnected() {
        return isConnected;
    }

    //To disconnect client from server

    public void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) {
                writer.close();
                reader.close();
                socket.close();
                isConnected = false;
                System.out.println("Disconnected from the server.");
            }
        } catch (IOException e) {
            System.out.println("Error disconnecting from server: " + e.getMessage());
        }
    }

    //For client login
    public boolean login(String usernameInput, String passwordInput) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return false;
        }

        writer.println("login" + DELIMITER + usernameInput + DELIMITER + passwordInput);

        try {
            String response = reader.readLine(); //To read response from server
            if (response.equals(SUCCESS)) {
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

    //To set user information
    public void setUserInformation() throws IOException {
        String information = reader.readLine(); //To read response from server
        String[] tokens = information.split(DELIMITER);
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

    //To set user registration information
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

    //To register a new user to the app
    public boolean register() {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return false;
        }
        writer.println("register" + DELIMITER + username + DELIMITER + password + DELIMITER + email + DELIMITER
                + phoneNumber + DELIMITER + userDescription + DELIMITER + university + DELIMITER +
                bedTime + DELIMITER + alcohol + DELIMITER + smoke + DELIMITER + guests + DELIMITER +
                tidy + DELIMITER + roomHours);

        try {
            String response = reader.readLine(); //To read response from server
            if (response.equals(SUCCESS)) {
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
        return reader.readLine();
    }

    //To update a user's profile
    public boolean updateProfile(String oldUsername) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return false;
        }

        writer.println("updateProfile" + DELIMITER + oldUsername + DELIMITER + username + DELIMITER + password + DELIMITER + email + DELIMITER
                + phoneNumber + DELIMITER + userDescription + DELIMITER + university + DELIMITER +
                bedTime + DELIMITER + alcohol + DELIMITER + smoke + DELIMITER + guests + DELIMITER +
                tidy + DELIMITER + roomHours);

        try {
            String response = reader.readLine(); //To read response from server
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

    //To set a user's preferences
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

    //To send messages to another user
    public boolean sendMessage(String receiver, String message) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return false;
        }

        writer.println("sendMessage" + DELIMITER + username + DELIMITER + receiver + DELIMITER + message);

        try {
            String response = reader.readLine(); //To read response from server
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

    public void viewMessage(String receiverUsername) {
        if (!isConnected) {
            System.out.println("Not connected to the server.");
            return;
        }

        try {
            // Send request to the server
            writer.println("loadMessages" + DELIMITER + username + DELIMITER + receiverUsername);

            // Read the response
            String response = reader.readLine();
            if (response == null || response.equals("Message list is empty")) {
                System.out.println("No messages found between you and " + receiverUsername);
                return;
            }

            System.out.println("Messages with " + receiverUsername + ":");
            for (String message : response.split("###")) {
                System.out.println(message);
            }
        } catch (IOException e) {
            System.out.println("Error retrieving messages: " + e.getMessage());
        }
    }

    //To load chat history between two users
    public String fetchMessages(String user, String receiver) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return null;
        }

        writer.println("loadMessages" + DELIMITER + username + DELIMITER + receiver + DELIMITER + receiver);

        try {
            String response = reader.readLine(); //To read response from server
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

    //To send friend requests to other users

    public boolean sendFriendRequest(String user, String potentialFriend) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return false;
        }

        writer.println("sendFriendRequest" + DELIMITER + user + DELIMITER + potentialFriend);

        try {
            String response = reader.readLine(); //To read response from server
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

    //To view all friend requests of a user
    
    public void viewFriendRequests(String user) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return;
        }

        writer.println("viewFriendRequests" + DELIMITER + user);

        try {
            String response = reader.readLine(); //To read response from server
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
                    case "1": //If you accept friend request
                        if (acceptFriendRequest(requester)) {
                            System.out.println("You accepted the friend request from: " + requester);
                        } else {
                            System.out.println("Failed to accept friend request from: " + requester);
                        }
                        break;
                    case "2": //If you decline friend request
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

    //Helper method to accept friend request
    public boolean acceptFriendRequest(String friend) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return false;
        }

        writer.println("acceptFriendRequest" + DELIMITER + this.username + DELIMITER + friend);

        try {
            String response = reader.readLine(); //To read response from server
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

    //Helper method to decline friend request
    public boolean declineFriendRequest(String usernameInput) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return false;
        }

        writer.println("declineFriendRequest" + username + DELIMITER + usernameInput);

        try {
            String response = reader.readLine(); //To read response from server
            return "Successfully declined friend request".equals(response);
        } catch (IOException e) {
            System.out.println("Error declining friend request: " + e.getMessage());
            return false;
        }
    }

    //To add a user as a friend
    public boolean addFriend(String user, String friend) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return false;
        }

        writer.println("acceptFriendRequest" + DELIMITER + user + DELIMITER + friend);

        try {
            String response = reader.readLine(); //To read response from server
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

    //To remove user from friend list
    public boolean removeFriend(String user, String friend) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return false;
        }

        writer.println("removeFriend" + DELIMITER + user + DELIMITER + friend);

        try {
            String response = reader.readLine(); //To read response from server
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

    //To block user 
    public boolean blockUser(String user, String blockedUser) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return false;
        }

        writer.println("blockUser" + DELIMITER + user + DELIMITER + blockedUser);

        try {
            String response = reader.readLine(); //To read response from server
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

    //To view a user profile
    public void viewProfile(String usernameInput) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return;
        }

        writer.println("viewProfile" + DELIMITER + username);

        try {
            String response = reader.readLine(); //To read response from server
            System.out.println("Profile data: " + response);
        } catch (IOException e) {
            System.out.println("Error viewing profile: " + e.getMessage());
        }
    }

    //To view a user's profile
    public void viewFriendsList(String usernameInput) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return;
        }

        writer.println("viewFriendsList" + DELIMITER + usernameInput);

        try {
            String response = reader.readLine(); //To read response from server
            if (response.equals("Friend list is empty")) {
                System.out.println("You have no friends yet.");
            } else {
                System.out.println("Your Friends:\n" + response);
            }
        } catch (IOException e) {
            System.out.println("Error viewing friends list: " + e.getMessage());
        }
    }

    //To unblock a user
    public boolean unblockUser(String user, String blockedUser) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return false;
        }

        writer.println("removeBlockedUser" + DELIMITER + user + DELIMITER + blockedUser);

        try {
            String response = reader.readLine(); //To read response from server
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

    //To view and manage blocked users
    public void viewBlockedUsers(String usernameInput) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return;
        }

        writer.println("viewBlockedUsers" + DELIMITER + usernameInput);

        try {
            String response = reader.readLine(); //To read response from server
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
                    case "2": //In case user changes their mind
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

    //To search for a roommate based on a specific preferences
    public void searchByParameter(String parameter, String value) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return;
        }

        writer.println("searchByParameter" + DELIMITER + parameter + DELIMITER + value);

        try {
            String response = reader.readLine(); //To read response from server
            if (response.equals("No matches found")) {
                System.out.println("No users found with " + parameter + ": " + value);
            } else {
                System.out.println("Users matching " + parameter + " = " + value + ":\n");
                for (String token : response.split("###")) {
                    System.out.println(token);
                }
            }
        } catch (IOException e) {
            System.out.println("Error searching users: " + e.getMessage());
        }
    }

    //To search for roommate based on exact match of preferences
    
    public void exactMatch(User user) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return;
        }

        writer.println("exactMatch" + DELIMITER + username);

        try {
            String response = reader.readLine(); //To read response from server
            if (response.equals("No exact matches found")) {
                System.out.println("No exact matches found for your preferences.");
            } else {
                String[] tokens = response.split("###");
                System.out.println("Exact Matches:\n");
                for (String token : tokens) {
                    System.out.println(token);
                }
            }
        } catch (IOException e) {
            System.out.println("Error finding exact matches: " + e.getMessage());
        }
    }

    //To search for roommate based on exact match of preferences
    
    public void partialMatch(User user) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return;
        }

        writer.println("partialMatch" + DELIMITER + username);

        try {
            String response = reader.readLine(); //To read response from server
            if (response.equals("No partial matches found")) {
                System.out.println("No partial matches found for your preferences.");
            } else {
                System.out.println("Partial Matches:\n");
                for (String token : response.split("###")) {
                    System.out.println(token);
                }
            }
        } catch (IOException e) {
            System.out.println("Error finding partial matches: " + e.getMessage());
        }
    }

    // To upload Profile Picture for the user
    public boolean setProfilePicture(String filePath) {
        if (!isConnected) {
            System.out.println("Not connected to the server.");
            return false;
        }

        File file = new File(filePath);
        if (!file.exists() || file.isDirectory()) {
            System.out.println("Invalid file path. Please check the file and try again.");
            return false;
        }

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] fileBytes = fileInputStream.readAllBytes();

            // Inform the server about the request
            writer.println("uploadProfilePicture" + DELIMITER + username);
            writer.println(fileBytes.length); // Send the size of the file

            // Send the actual file bytes
            socket.getOutputStream().write(fileBytes);
            socket.getOutputStream().flush();

            // Wait for the server's response
            String response = reader.readLine();
            if ("Profile picture updated".equals(response)) {
                System.out.println("Profile picture updated successfully.");
                return true;
            } else {
                System.out.println("Failed to update profile picture. Server response: " + response);
                return false;
            }
        } catch (IOException e) {
            System.out.println("Error uploading profile picture: " + e.getMessage());
            return false;
        }
    }
}
