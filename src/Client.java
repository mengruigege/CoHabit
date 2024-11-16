import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

    private User currentUser;
    private boolean isConnected;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private final String serverAddress = "localhost";
    private final int serverPort = 1102;

    public Client(User user) {
        this.currentUser = user;
    }

    public static void main(String[] args) throws UsernameTakenException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        User user = new User(username, password);
        Client client = new Client(user);

        if (!client.connect(client.serverAddress, client.serverPort)) {
            System.out.println("Failed to connect to the server. Exiting.");
            return;
        }

        boolean exit = false;

        while (!exit) {
            System.out.println("\nSelect an option:");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Send Message");
            System.out.println("4. View Friend Requests");
            System.out.println("5. Send Friend Request");
            System.out.println("6. Add Friend");
            System.out.println("7. Remove Friend");
            System.out.println("8. Block User");
            System.out.println("9. View Profile");
            System.out.println("10. Disconnect and Exit");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    client.login(username, password);
                    break;
                case "2":
                    client.register(new User(username, password));
                    break;
                case "3":
                    System.out.print("Enter receiver's username: ");
                    String receiver = scanner.nextLine();
                    System.out.print("Enter message: ");
                    String message = scanner.nextLine();
                    client.sendMessage(receiver, message);
                    break;
                case "4":
                    client.viewFriendRequests(username);
                    break;
                case "5":
                    System.out.print("Enter username to send friend request: ");
                    String friendRequestUsername = scanner.nextLine();
                    client.sendFriendRequest(username, friendRequestUsername);
                    break;
                case "6":
                    System.out.print("Enter username to add as friend: ");
                    String friendUsername = scanner.nextLine();
                    client.addFriend(username, friendUsername);
                    break;
                case "7":
                    System.out.print("Enter username to remove as friend: ");
                    String removedFriend = scanner.nextLine();
                    client.removeFriend(username, removedFriend);
                    break;
                case "8":
                    System.out.print("Enter username to block: ");
                    String blockedUser = scanner.nextLine();
                    client.blockUser(username, blockedUser);
                    break;
                case "9":
                    client.viewProfile(username);
                    break;
                case "10":
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

    public boolean register(User user) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return false;
        }

        out.println("register," + user.getName() + "," + user.getPassword());

        try {
            String response = in.readLine();
            if ("User registered".equals(response)) {
                System.out.println("User registered: " + user.getName());
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

    public boolean sendMessage(String receiver, String message) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return false;
        }

        out.println("sendMessage," + currentUser.getName() + "," + receiver + "," + message);

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

}
