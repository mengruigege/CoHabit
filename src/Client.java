import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Client implements ClientService {

    private User currentUser;
    private boolean isConnected;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private final String serverAddress = "localhost";
    private final int serverPort = 1102;
    private Chat chat;
    private FriendList friendList;

    // Constructor
    public Client(User user, FriendList friendList, Chat chat) {
        this.currentUser = user;
        this.friendList = friendList;
        this.chat = chat;
    }

    public static void main(String[] args) throws UsernameTakenException {
        String username = null;
        String password = null;
        User user = new User(username, password);
        FriendList friendList = new FriendList();
        Chat chat = new Chat();
        Client client = new Client(user, friendList, chat);

        if (!client.connect(client.serverAddress, client.serverPort)) {
            System.out.println("Failed to connect to the server. Exiting.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        String choice;
        boolean exit = false;

        while (!exit) {
            System.out.println("\nSelect an action:");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Send Message");
            System.out.println("4. Fetch Messages");
            System.out.println("5. Add Friend");
            System.out.println("6. View Profile");
            System.out.println("7. Update Profile");
            System.out.println("8. Disconnect and Exit");

            choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("Enter username: ");
                    username = scanner.nextLine();
                    System.out.print("Enter password: ");
                    password = scanner.nextLine();
                    client.login(username, password);
                    break;
                case "2":
                    System.out.print("Enter username: ");
                    username = scanner.nextLine();
                    System.out.print("Enter password: ");
                    password = scanner.nextLine();
                    User newUser = new User(username, password);
                    client.register(newUser);
                    break;
                case "3":
                    System.out.print("Enter receiver's username: ");
                    String receiver = scanner.nextLine();
                    System.out.print("Enter message: ");
                    String message = scanner.nextLine();
                    client.sendMessage(receiver, message);
                    break;
                case "4":
                    System.out.print("Enter username to fetch messages: ");
                    username = scanner.nextLine();
                    client.fetchMessages(username).forEach(System.out::println);
                    break;
                case "5":
                    System.out.print("Enter username to add as friend: ");
                    String friendUsername = scanner.nextLine();
                    client.addFriend(friendUsername);
                    break;
                case "6":
                    System.out.print("Enter username to view profile: ");
                    username = scanner.nextLine();
                    User profile = client.viewProfile(username);
                    if (profile != null) {
                        System.out.println("Profile: " + profile);
                    }
                    break;
                case "7":
                    System.out.print("Enter new username: ");
                    String newUsername = scanner.nextLine();
                    System.out.print("Enter new password: ");
                    String newPassword = scanner.nextLine();
                    User updatedProfile = new User(newUsername, newPassword);
                    client.updateProfile(updatedProfile);
                    break;
                case "8":
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

    // Establish connection to the server
    public boolean connect(String serverAddress, int port) {
        try {
            socket = new Socket(serverAddress, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            isConnected = true;
            System.out.println("Connected to the server at " + serverAddress + ":" + port);
            return true;
        } catch (IOException e) {
            System.out.println("Error connecting to server: " + e.getMessage());
            return false;
        }
    }

    // Disconnect from the server
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

    // Login method
    public boolean login(String username, String password) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return false;
        }

        try {
            // "LOGIN,username,password"
            String command = "LOGIN," + username + "," + password;
            out.writeObject(command);

            String response = (String) in.readObject();
            if ("OK".equals(response)) {
                currentUser.setName(username);
                System.out.println("Login successful.");
                return true;
            } else {
                System.out.println("Login failed.");
                return false;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error during login: " + e.getMessage());
            return false;
        }
    }

    // Register a new user
    public boolean register(User user) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return false;
        }

        try {
            // "REGISTER,username,password"
            String command = "REGISTER," + user.getName() + "," + user.getPassword();
            out.writeObject(command);

            String response = (String) in.readObject();
            if ("OK".equals(response)) {
                this.currentUser = user;
                System.out.println("User registered: " + user.getName());
                return true;
            } else {
                System.out.println("Registration failed.");
                return false;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error during registration: " + e.getMessage());
            return false;
        }
    }

    // Send a message to a specific user
    public boolean sendMessage(String receiver, String message) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return false;
        }

        try {
            // "SEND_MESSAGE,receiver,message"
            String command = "SEND_MESSAGE," + receiver + "," + message;
            out.writeObject(command);

            String response = (String) in.readObject();
            if ("OK".equals(response)) {
                System.out.println("Message sent to " + receiver);
                return true;
            } else {
                System.out.println("Failed to send message.");
                return false;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error sending message: " + e.getMessage());
            return false;
        }
    }

    // Fetch all messages
    public ArrayList<String> fetchMessages(String user) {
        ArrayList<String> messages = new ArrayList<>();
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return messages;
        }

        try {
            // "FETCH_MESSAGES,username"
            String command = "FETCH_MESSAGES," + user;
            out.writeObject(command);

            messages = (ArrayList<String>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error fetching messages: " + e.getMessage());
        }
        return messages;
    }

    // Add a friend
    public boolean addFriend(String username) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return false;
        }

        try {
            // "ADD_FRIEND,username"
            String command = "ADD_FRIEND," + username;
            out.writeObject(command);

            String response = (String) in.readObject();
            if ("OK".equals(response)) {
                System.out.println("Friend added: " + username);
                return true;
            } else {
                System.out.println("Failed to add friend.");
                return false;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error adding friend: " + e.getMessage());
            return false;
        }
    }

    // View a user's profile
    public User viewProfile(String username) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return null;
        }

        try {
            // "VIEW_PROFILE,username"
            String command = "VIEW_PROFILE," + username;
            out.writeObject(command);

            User user = (User) in.readObject();
            System.out.println("User profile: " + user.toString());
            return user;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error viewing profile: " + e.getMessage());
            return null;
        }
    }

    // Update user profile
    public boolean updateProfile(User updatedProfile) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return false;
        }

        try {
            // "UPDATE_PROFILE,username,password"
            String command = "UPDATE_PROFILE," + updatedProfile.getName() + "," + updatedProfile.getPassword();
            out.writeObject(command);

            String response = (String) in.readObject();
            if ("OK".equals(response)) {
                this.currentUser = updatedProfile;
                System.out.println("Profile updated: " + updatedProfile.getName());
                return true;
            } else {
                System.out.println("Profile update failed.");
                return false;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error updating profile: " + e.getMessage());
            return false;
        }
    }
}

