import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final Database database = new Database();
    private static final int PORT = 1102;
    private static final int THREAD_POOL_SIZE = 10;

    public static void main(String[] args) {
        System.out.println("Server is starting...");
        database.loadUsersFromFile(); // Load data at server startup

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);
            ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected");
                threadPool.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    /**
     * Inner class to handle individual client connections.
     */
    private static class ClientHandler implements Runnable {
        private final Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)
            ) {
                String line;
                while ((line = reader.readLine()) != null) {
                    handleRequest(line, writer);
                }
            } catch (IOException e) {
                System.err.println("Client error: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.err.println("Error closing client socket: " + e.getMessage());
                }
            }
        }

        private void handleRequest(String line, PrintWriter writer) {
            if (line.startsWith("login")) {
                handleLogin(line, writer);
            } else if (line.startsWith("register")) {
                handleRegister(line, writer);
            } else if (line.startsWith("sendMessage")) {
                handleSendMessage(line, writer);
            } else if (line.startsWith("addFriend")) {
                handleAddFriend(line, writer);
            } else if (line.startsWith("removeFriend")) {
                handleRemoveFriend(line, writer);
            } else if (line.startsWith("blockUser")) {
                handleBlockUser(line, writer);
            } else if (line.startsWith("removeBlockedUser")) {
                handleUnblockUser(line, writer);
            } else if (line.startsWith("viewProfile")) {
                handleViewProfile(line, writer);
            } else {
                writer.println("Unknown command");
            }
        }

        private void handleLogin(String line, PrintWriter writer) {
            String[] parts = line.split(",");
            if (parts.length != 3) {
                writer.println("Error: Invalid login format");
                return;
            }
            String username = parts[1];
            String password = parts[2];

            synchronized (database) {
                User user = database.findUserByName(username);
                if (user != null && user.getPassword().equals(password)) {
                    writer.println("Successful login");
                    writer.println(
                            user.getName() + "###" + user.getPassword() + "###" + user.getEmail() + "###"
                                    + user.getPhoneNumber() + "###" + user.getDescription() + "###" + user.getUniversity()
                    );
                } else {
                    writer.println("Invalid username or password");
                }
            }
        }

        private void handleRegister(String line, PrintWriter writer) {
            String[] parts = line.split("###");
            if (parts.length < 7) {
                writer.println("Error: Missing registration fields");
                return;
            }

            try {
                User user = new User(parts[1], parts[2], parts[3], parts[4], parts[5], parts[6]);
                synchronized (database) {
                    if (database.addUser(user)) {
                        writer.println("Registration successful");
                    } else {
                        writer.println("Registration failed: Username already taken");
                    }
                }
            } catch (Exception e) {
                writer.println("Error: " + e.getMessage());
            }
        }

        private void handleSendMessage(String line, PrintWriter writer) {
            String[] parts = line.split("###");
            if (parts.length != 4) {
                writer.println("Error: Invalid message format");
                return;
            }
            String senderName = parts[1];
            String receiverName = parts[2];
            String message = parts[3];

            synchronized (database) {
                User sender = database.findUserByName(senderName);
                User receiver = database.findUserByName(receiverName);

                if (sender == null || receiver == null) {
                    writer.println("Error: User not found");
                    return;
                }

                database.recordMessages(senderName, receiverName, message);
                writer.println("Message sent successfully");
            }
        }

        private void handleAddFriend(String line, PrintWriter writer) {
            String[] parts = line.split(",");
            if (parts.length != 3) {
                writer.println("Error: Invalid addFriend format");
                return;
            }

            synchronized (database) {
                User user = database.findUserByName(parts[1]);
                User friend = database.findUserByName(parts[2]);

                if (user == null || friend == null) {
                    writer.println("Error: User not found");
                    return;
                }

                if (database.addFriend(user, friend)) {
                    writer.println("Friend added successfully");
                } else {
                    writer.println("Error adding friend");
                }
            }
        }

        private void handleRemoveFriend(String line, PrintWriter writer) {
            String[] parts = line.split(",");
            if (parts.length != 3) {
                writer.println("Error: Invalid removeFriend format");
                return;
            }

            synchronized (database) {
                User user = database.findUserByName(parts[1]);
                User removedFriend = database.findUserByName(parts[2]);

                if (user == null || removedFriend == null) {
                    writer.println("Error: User not found");
                    return;
                }

                if (database.removeFriend(user, removedFriend)) {
                    writer.println("Friend removed successfully");
                } else {
                    writer.println("Error removing friend");
                }
            }
        }

        private void handleBlockUser(String line, PrintWriter writer) {
            String[] parts = line.split(",");
            if (parts.length != 3) {
                writer.println("Error: Invalid blockUser format");
                return;
            }

            synchronized (database) {
                if (database.blockUser(parts[1], parts[2])) {
                    writer.println("User blocked successfully");
                } else {
                    writer.println("Error blocking user");
                }
            }
        }

        private void handleUnblockUser(String line, PrintWriter writer) {
            String[] parts = line.split(",");
            if (parts.length != 3) {
                writer.println("Error: Invalid unblockUser format");
                return;
            }

            synchronized (database) {
                if (database.unblockUser(parts[1], parts[2])) {
                    writer.println("User unblocked successfully");
                } else {
                    writer.println("Error unblocking user");
                }
            }
        }

        private void handleViewProfile(String line, PrintWriter writer) {
            String[] parts = line.split(",");
            if (parts.length != 2) {
                writer.println("Error: Invalid viewProfile format");
                return;
            }

            synchronized (database) {
                User user = database.findUserByName(parts[1]);
                if (user != null) {
                    writer.println(user.toString());
                } else {
                    writer.println("Error: User not found");
                }
            }
        }
    }
}
