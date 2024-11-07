import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Client implements ClientService {

    private User currentUser;
    private boolean isConnected;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private final String serverAddress = "localhost";
    private final int serverPort = 1102;
    private Chat chat;
    private FriendList friendList;

    // Constructor
    public Client(User user, FriendList friendList, Chat chat) {
        this.currentUser = user;
        this.FriendList = friendList;
        this.chat = chat;
    }

    // Establish connection to the server
  
    public boolean connect(String serverAddress, int port) {
        try {
            // Create socket and connect to server
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

        out.println("LOGIN " + username + " " + password);  // Send login request to the server
        try {
            String response = in.readLine();
            if ("OK".equals(response)) {
                currentUser.setName(username);
                System.out.println("Login successful.");
                return true;
            } else {
                System.out.println("Login failed.");
                return false;
            }
        } catch (IOException e) {
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

        out.println("REGISTER " + user.getName() + " " + user.getPassword() + " " + user.getEmail());  // Send register request
        try {
            String response = in.readLine();
            if ("OK".equals(response)) {
                this.currentUser = user;
                System.out.println("User registered: " + user.getName());
                return true;
            } else {
                System.out.println("Registration failed.");
                return false;
            }
        } catch (IOException e) {
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

        out.println("SEND_MESSAGE " + receiver + " " + message);  // Send message to server
        try {
            String response = in.readLine();
            if ("OK".equals(response)) {
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

    // Fetch all messages 
    
    public ArrayList<String> fetchMessages(String user) {
        ArrayList<String> messages = new ArrayList<>();
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return messages;
        }

        out.println("FETCH_MESSAGES " + user);  // Request message history from server
        try {
            String response = in.readLine();
            if (response != null && !response.isEmpty()) {
                String[] messageArray = response.split(";");
                for (String message : messageArray) {
                    messages.add(message);
                }
            }
        } catch (IOException e) {
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

        out.println("ADD_FRIEND " + username);  // Send add friend request to server
        try {
            String response = in.readLine();
            if ("OK".equals(response)) {
                System.out.println("Friend added: " + username);
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

    // View a user's profile
    
    public User viewProfile(String username) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return null;
        }

        out.println("VIEW_PROFILE " + username);  // Request user profile from server
        try {
            String response = in.readLine();
            if (response != null) {
                
                User user = new User(username, "password", "email", "123-456", "description", "university");
                System.out.println("User profile: " + user.toString());
                return user;
            } else {
                System.out.println("Profile not found.");
                return null;
            }
        } catch (IOException e) {
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

        out.println("UPDATE_PROFILE " + updatedProfile.getName() + " " + updatedProfile.getPassword());  // Send profile update request to server
        try {
            String response = in.readLine();
            if ("OK".equals(response)) {
                this.currentUser = updatedProfile;
                System.out.println("Profile updated: " + updatedProfile.getName());
                return true;
            } else {
                System.out.println("Profile update failed.");
                return false;
            }
        } catch (IOException e) {
            System.out.println("Error updating profile: " + e.getMessage());
            return false;
        }
    }

    
    public void start() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the Chat Application!");

        while (isConnected) {
            System.out.println("Choose an action: 1) Login 2) Register 3) Send Message 4) View Profile 5) Exit");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> loginPrompt(scanner);
                case 2 -> registerPrompt(scanner);
                case 3 -> messagePrompt(scanner);
                case 4 -> profilePrompt(scanner);
                case 5 -> {
                    disconnect();
                    scanner.close();
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    
    private void login(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        login(username, password);
    }

    private void register(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter phone number: ");
        String phone = scanner.nextLine();
        User newUser = new User(username, password, email, phone, "description", "university");
        register(newUser);
    }

    private void msg(Scanner scanner) {
        System.out.print("Enter receiver's username: ");
        String receiver = scanner.nextLine();
        System.out.print("Enter message: ");
        String message = scanner.nextLine();
        sendMessage(receiver, message);
    }

    private void profile(Scanner scanner) {
        System.out.print("Enter username to view profile: ");
        String username = scanner.nextLine();
        viewProfile(username);
    }
}
