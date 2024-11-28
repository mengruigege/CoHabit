
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
            isConnected = false;
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
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid option, please try again");
                    break;
            }
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
                System.out.println("Login failed. Incorrect username or password.");
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

        // Username
        while (true) {
            System.out.print("Create a username: ");
            username = scanner.nextLine();
            if (username != null && !username.contains(DELIMITER) && !username.trim().isEmpty()) {
                break;
            }
            System.out.println("Invalid username. Please try again.");
        }

        // Password
        while (true) {
            System.out.print("Create a password: ");
            password = scanner.nextLine();
            if (password != null && !password.contains(DELIMITER) && password.length() >= 6) {
                break;
            }
            System.out.println("Invalid password. Password must be at least 6 characters. Please try again.");
        }

        // Email
        while (true) {
            System.out.print("Enter your email: ");
            email = scanner.nextLine();
            if (email != null && email.matches("[^@]+@[^@]+\\.[^@]+")) {
                break;
            }
            System.out.println("Invalid email format. Please try again.");
        }

        // Phone Number
        while (true) {
            System.out.print("Enter your phone number (10 digits): ");
            phoneNumber = scanner.nextLine();
            if (phoneNumber != null && phoneNumber.matches("\\d{10}")) {
                break;
            }
            System.out.println("Invalid phone number. Please enter exactly 10 digits.");
        }

        // Description
        while (true) {
            System.out.print("Enter a brief description about yourself: ");
            userDescription = scanner.nextLine();
            if (userDescription != null && !userDescription.contains(DELIMITER) && !userDescription.trim().isEmpty()) {
                break;
            }
            System.out.println("Invalid description. Please try again.");
        }

        // University
        while (true) {
            System.out.print("Enter your university: ");
            university = scanner.nextLine();
            if (university != null && !university.contains(DELIMITER) && !university.trim().isEmpty()) {
                break;
            }
            System.out.println("Invalid university. Please try again.");
        }

        // Bedtime
        while (true) {
            System.out.print("What is your average bedtime (e.g., 22:30)? ");
            bedTime = scanner.nextLine();
            if (bedTime != null && bedTime.matches("\\d{2}:\\d{2}")) {
                break;
            }
            System.out.println("Invalid bedtime. Please enter in HH:MM format.");
        }

        // Alcohol
        while (true) {
            System.out.print("Do you drink alcohol? (yes/no): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("yes") || input.equals("no")) {
                alcohol = input.equals("yes");
                break;
            }
            System.out.println("Invalid input. Please enter 'yes' or 'no'.");
        }

        // Smoke
        while (true) {
            System.out.print("Do you smoke? (yes/no): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("yes") || input.equals("no")) {
                smoke = input.equals("yes");
                break;
            }
            System.out.println("Invalid input. Please enter 'yes' or 'no'.");
        }

        // Guests
        while (true) {
            System.out.print("Are you comfortable with guests? (yes/no): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("yes") || input.equals("no")) {
                guests = input.equals("yes");
                break;
            }
            System.out.println("Invalid input. Please enter 'yes' or 'no'.");
        }

        // Tidy Level
        while (true) {
            System.out.print("How tidy are you? (1-10): ");
            try {
                tidy = Integer.parseInt(scanner.nextLine());
                if (tidy >= 1 && tidy <= 10) {
                    break;
                }
                System.out.println("Invalid input. Please enter a number between 1 and 10.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }

        // Room Hours
        while (true) {
            System.out.print("How many hours do you spend in your room daily? (1-24): ");
            try {
                roomHours = Integer.parseInt(scanner.nextLine());
                if (roomHours >= 1 && roomHours <= 24) {
                    break;
                }
                System.out.println("Invalid input. Please enter a number between 1 and 24.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }

        // Send registration request
        writer.println("register" + DELIMITER + username + DELIMITER + password + DELIMITER + email + DELIMITER
                + phoneNumber + DELIMITER + userDescription + DELIMITER + university + DELIMITER +
                bedTime + DELIMITER + alcohol + DELIMITER + smoke + DELIMITER + guests + DELIMITER +
                tidy + DELIMITER + roomHours);

        try {
            String response = reader.readLine();
            if (SUCCESS.equals(response)) {
                System.out.println("Registration successful. Welcome, " + username + "!");
            } else {
                System.out.println("Registration failed: " + response);
                return false;
            }
        } catch (IOException e) {
            System.out.println("Error during registration: " + e.getMessage());
            return false;
        }

        // Profile Picture
        while (true) {
            System.out.print("Enter the file path for your profile picture (e.g., C:\\Users\\User\\picture.png): ");
            String filePath = scanner.nextLine();

            File file = new File(filePath);
            if (!file.exists() || file.isDirectory()) {
                System.out.println("Invalid file path. Please try again.");
                continue;
            }

            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                byte[] fileBytes = fileInputStream.readAllBytes();

                // Inform the server about the profile picture
                writer.println("uploadProfilePicture" + DELIMITER + username);
                writer.println(fileBytes.length); // Send the file size

                // Send the file data
                socket.getOutputStream().write(fileBytes);
                socket.getOutputStream().flush();

                // Wait for server confirmation
                String pictureResponse = reader.readLine();
                if (SUCCESS.equals(pictureResponse)) {
                    System.out.println("Profile picture uploaded successfully.");
                    break;
                } else {
                    System.out.println("Failed to upload profile picture. Server response: " + pictureResponse);
                }
            } catch (IOException e) {
                System.out.println("Error uploading profile picture: " + e.getMessage());
            }
        }

        return true;
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

    public void viewFriendRequests() {
        if (!isConnected) {
            System.out.println("Not connected to the server.");
            return;
        }

        writer.println("viewFriendRequests" + DELIMITER + username);

        try {
            String response = reader.readLine();
            if (response == null || response.equals(FAILURE)) {
                System.out.println("No pending friend requests.");
                return;
            }

            String[] requests = response.split(DELIMITER);
            System.out.println("You have the following friend requests:");
            for (String requester : requests) {
                System.out.println("Friend request from: " + requester);
                System.out.println("Do you want to (1) Accept or (2) Decline?");
                String choice = scanner.nextLine();

                if ("1".equals(choice)) {
                    if (acceptFriendRequest(requester)) {
                        System.out.println("Accepted friend request from " + requester);
                    } else {
                        System.out.println("Failed to accept friend request from " + requester);
                    }
                } else if ("2".equals(choice)) {
                    if (declineFriendRequest(requester)) {
                        System.out.println("Declined friend request from " + requester);
                    } else {
                        System.out.println("Failed to decline friend request from " + requester);
                    }
                } else {
                    System.out.println("Invalid option. Skipping request from " + requester);
                }
            }
        } catch (IOException e) {
            System.out.println("Error retrieving friend requests: " + e.getMessage());
        }
    }

    public void sendFriendRequest() {
        if (!isConnected) {
            System.out.println("Not connected to the server.");
            return;
        }

        System.out.print("Enter the username to send a friend request to: ");
        String friendUsername = scanner.nextLine();

        writer.println("sendFriendRequest" + DELIMITER + username + DELIMITER + friendUsername);

        try {
            String response = reader.readLine();
            if (response.equals(SUCCESS)) {
                System.out.println("Friend request sent to " + friendUsername);
            } else {
                System.out.println("Failed to send friend request: " + response);
            }
        } catch (IOException e) {
            System.out.println("Error sending friend request: " + e.getMessage());
        }
    }

    public void removeFriend() {
        if (!isConnected) {
            System.out.println("Not connected to the server.");
            return;
        }

        System.out.print("Enter the username to remove from your friend list: ");
        String friendUsername = scanner.nextLine();

        writer.println("removeFriend" + DELIMITER + username + DELIMITER + friendUsername);

        try {
            String response = reader.readLine();
            if (response.equals(SUCCESS)) {
                System.out.println(friendUsername + " has been removed from your friend list.");
            } else {
                System.out.println("Failed to remove friend: " + response);
            }
        } catch (IOException e) {
            System.out.println("Error removing friend: " + e.getMessage());
        }
    }

    public void blockUser() {
        if (!isConnected) {
            System.out.println("Not connected to the server.");
            return;
        }

        System.out.print("Enter the username to block: ");
        String blockedUsername = scanner.nextLine();

        writer.println("blockUser" + DELIMITER + username + DELIMITER + blockedUsername);

        try {
            String response = reader.readLine();
            if (response.equals(SUCCESS)) {
                System.out.println(blockedUsername + " has been blocked.");
            } else {
                System.out.println("Failed to block user: " + response);
            }
        } catch (IOException e) {
            System.out.println("Error blocking user: " + e.getMessage());
        }
    }

    public void unblockUser() {
        if (!isConnected) {
            System.out.println("Not connected to the server.");
            return;
        }

        System.out.print("Enter the username to unblock: ");
        String unblockedUsername = scanner.nextLine();

        writer.println("removeBlockedUser" + DELIMITER + username + DELIMITER + unblockedUsername);

        try {
            String response = reader.readLine();
            if (response.equals(SUCCESS)) {
                System.out.println(unblockedUsername + " has been unblocked.");
            } else {
                System.out.println("Failed to unblock user: " + response);
            }
        } catch (IOException e) {
            System.out.println("Error unblocking user: " + e.getMessage());
        }
    }

    public void viewProfile() {
        if (!isConnected) {
            System.out.println("Not connected to the server.");
            return;
        }

        System.out.print("Enter the username to view their profile: ");
        String targetUsername = scanner.nextLine();

        writer.println("viewProfile" + DELIMITER + targetUsername);

        try {
            String response = reader.readLine();
            if (response == null || response.equals(FAILURE)) {
                System.out.println("Profile not found for " + targetUsername);
            } else {
                System.out.println("Profile of " + targetUsername + ":\n" + response);
            }
        } catch (IOException e) {
            System.out.println("Error viewing profile: " + e.getMessage());
        }
    }

    public void updateProfile() {
        if (!isConnected) {
            System.out.println("Not connected to the server.");
            return;
        }

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
        String oldUsername = username;

        try {
            switch (selection) {
                case "1":
                    while (true) {
                        System.out.print("Enter new username: ");
                        username = scanner.nextLine();
                        if (username == null || username.trim().isEmpty() || username.contains(DELIMITER)) {
                            System.out.println("Invalid username. Please try again.");
                        } else {
                            break;
                        }
                    }
                    break;
                case "2":
                    while (true) {
                        System.out.print("Enter new password: ");
                        password = scanner.nextLine();
                        if (password == null || password.trim().isEmpty() || password.length() < 6 || password.contains(DELIMITER)) {
                            System.out.println("Invalid password. Must be at least 6 characters and not contain " + DELIMITER);
                        } else {
                            break;
                        }
                    }
                    break;
                case "3":
                    while (true) {
                        System.out.print("Enter new email: ");
                        email = scanner.nextLine();
                        if (email == null || !email.matches("[^@]+@[^@]+\\.[^@]+")) {
                            System.out.println("Invalid email format. Please try again.");
                        } else {
                            break;
                        }
                    }
                    break;
                case "4":
                    while (true) {
                        System.out.print("Enter new phone number: ");
                        phoneNumber = scanner.nextLine();
                        if (phoneNumber == null || !phoneNumber.matches("\\d{10}")) {
                            System.out.println("Invalid phone number. Must be exactly 10 digits.");
                        } else {
                            break;
                        }
                    }
                    break;
                case "5":
                    while (true) {
                        System.out.print("Enter new description: ");
                        userDescription = scanner.nextLine();
                        if (userDescription == null || userDescription.trim().isEmpty() || userDescription.contains(DELIMITER)) {
                            System.out.println("Invalid description. Please try again.");
                        } else {
                            break;
                        }
                    }
                    break;
                case "6":
                    while (true) {
                        System.out.print("Enter new university: ");
                        university = scanner.nextLine();
                        if (university == null || university.trim().isEmpty() || university.contains(DELIMITER)) {
                            System.out.println("Invalid university. Please try again.");
                        } else {
                            break;
                        }
                    }
                    break;
                case "7":
                    while (true) {
                        System.out.print("Enter your average bed time (e.g., 22:30): ");
                        bedTime = scanner.nextLine();
                        if (bedTime == null || !bedTime.matches("\\d{2}:\\d{2}")) {
                            System.out.println("Invalid bed time format. Please try again.");
                        } else {
                            break;
                        }
                    }
                    while (true) {
                        System.out.print("Do you drink alcohol? (yes/no): ");
                        String input = scanner.nextLine().trim().toLowerCase();
                        if (input.equals("yes")) {
                            alcohol = true;
                            break;
                        } else if (input.equals("no")) {
                            alcohol = false;
                            break;
                        } else {
                            System.out.println("Invalid input. Please enter 'yes' or 'no'.");
                        }
                    }
                    while (true) {
                        System.out.print("Do you smoke? (yes/no): ");
                        String input = scanner.nextLine().trim().toLowerCase();
                        if (input.equals("yes")) {
                            smoke = true;
                            break;
                        } else if (input.equals("no")) {
                            smoke = false;
                            break;
                        } else {
                            System.out.println("Invalid input. Please enter 'yes' or 'no'.");
                        }
                    }
                    while (true) {
                        System.out.print("Are you comfortable with guests? (yes/no): ");
                        String input = scanner.nextLine().trim().toLowerCase();
                        if (input.equals("yes")) {
                            guests = true;
                            break;
                        } else if (input.equals("no")) {
                            guests = false;
                            break;
                        } else {
                            System.out.println("Invalid input. Please enter 'yes' or 'no'.");
                        }
                    }
                    while (true) {
                        System.out.print("How tidy are you? (1-10): ");
                        try {
                            tidy = Integer.parseInt(scanner.nextLine());
                            if (tidy >= 1 && tidy <= 10) {
                                break;
                            } else {
                                System.out.println("Value must be between 1 and 10.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input. Please enter a number between 1 and 10.");
                        }
                    }
                    while (true) {
                        System.out.print("How many hours per day do you spend in your room? (1-24): ");
                        try {
                            roomHours = Integer.parseInt(scanner.nextLine());
                            if (roomHours >= 1 && roomHours <= 24) {
                                break;
                            } else {
                                System.out.println("Value must be between 1 and 24.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input. Please enter a number between 1 and 24.");
                        }
                    }
                    break;
                case "8":
                    System.out.print("Enter the file path for your new profile picture (e.g., C:\\Users\\User\\picture.png): ");
                    String filePath = scanner.nextLine();
                    if (setProfilePicture(filePath)) {
                        System.out.println("Profile picture uploaded successfully.");
                    } else {
                        System.out.println("Failed to upload profile picture.");
                    }
                    return; // Exit early, as profile picture doesn't require server update
                default:
                    System.out.println("Invalid option. Returning to the main menu.");
                    return;
            }

            // Send update request to the server
            writer.println("updateProfile" + DELIMITER + oldUsername + DELIMITER + username + DELIMITER +
                    password + DELIMITER + email + DELIMITER + phoneNumber + DELIMITER + userDescription +
                    DELIMITER + university + DELIMITER + bedTime + DELIMITER + alcohol + DELIMITER +
                    smoke + DELIMITER + guests + DELIMITER + tidy + DELIMITER + roomHours);

            // Read response
            String response = reader.readLine();
            if (response.equals(SUCCESS)) {
                System.out.println("Profile updated successfully.");
            } else {
                System.out.println("Profile update failed: " + response);
            }
        } catch (IOException e) {
            System.out.println("Error updating profile: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input for numeric fields. Please try again.");
        }
    }

    public void searchRoommates() {
        if (!isConnected) {
            System.out.println("Not connected to the server.");
            return;
        }

        System.out.println("Search Roommates:");
        System.out.println("1. By Parameter");
        System.out.println("2. Exact Match");
        System.out.println("3. Partial Match");
        String option = "";

        // Validate user input for search type
        while (true) {
            System.out.print("Enter your choice (1/2/3): ");
            option = scanner.nextLine().trim();
            if (option.equals("1") || option.equals("2") || option.equals("3")) {
                break;
            }
            System.out.println("Invalid input. Please enter 1, 2, or 3.");
        }

        try {
            switch (option) {
                case "1": // Search by Parameter
                    String parameter = "";
                    String value = "";

                    while (true) {
                        System.out.print("Enter search parameter (e.g., university, email, phone, username): ");
                        parameter = scanner.nextLine().trim().toLowerCase();
                        if (parameter.matches("username|email|phone|university")) {
                            break;
                        }
                        System.out.println("Invalid parameter. Please choose from: username, email, phone, university.");
                    }

                    while (true) {
                        System.out.print("Enter value for parameter: ");
                        value = scanner.nextLine().trim();
                        if (!value.isEmpty()) {
                            break;
                        }
                        System.out.println("Value cannot be empty. Please try again.");
                    }

                    writer.println("searchByParameter" + DELIMITER + parameter + DELIMITER + value);
                    break;

                case "2": // Exact Match
                    writer.println("exactMatch" + DELIMITER + username);
                    break;

                case "3": // Partial Match
                    writer.println("partialMatch" + DELIMITER + username);
                    break;
            }

            // Handle server response
            String response = reader.readLine();
            if (response == null || response.equals(FAILURE)) {
                System.out.println("No matches found.");
            } else {
                System.out.println("Matches found:");
                for (String match : response.split(DELIMITER)) {
                    System.out.println(match);
                }
            }
        } catch (IOException e) {
            System.out.println("Error searching roommates: " + e.getMessage());
        }
    }


    public void disconnect() {
        if (!isConnected) {
            System.out.println("Not connected to the server.");
            return;
        }

        try {
            if (socket != null) {
                socket.close();
            }
            isConnected = false;
            System.out.println("Disconnected from the server.");
        } catch (IOException e) {
            System.out.println("Error disconnecting from server: " + e.getMessage());
        } finally {
            isConnected = false;
        }
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

    //To check if client is connected to server
    
    public boolean isConnected() {
        return isConnected;
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
        File file = new File(filePath);
        if (!file.exists() || file.isDirectory()) {
            System.out.println("Invalid file path. Please try again.");
            return false;
        }

        long maxFileSize = 5 * 1024 * 1024; // 5 MB limit
        if (file.length() > maxFileSize) {
            System.out.println("File size exceeds the maximum limit of 5 MB.");
            return false;
        }

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] fileBytes = fileInputStream.readAllBytes();
            writer.println("uploadProfilePicture" + DELIMITER + username);
            writer.println(fileBytes.length);
            socket.getOutputStream().write(fileBytes);
            socket.getOutputStream().flush();

            String response = reader.readLine();
            if ("SUCCESS".equals(response)) {
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
