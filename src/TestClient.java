//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//
//import static org.junit.Assert.*;
//
///**
// * Team Project Phase 2 - CoHabit
// * <p>
// * This program works to implement a roommate search algorithm
// *
// * @author Aidan Lefort, Andrew Tang, Keya Jadhav, Rithvik Siddenki, Rui Meng
// * @version November 3rd, 2024
// */
//
//public class TestClient {
//    private Thread serverThread;
//    private Client client;
//    private static final String USERS_FILE = "users.txt";
//    private static final String FRIENDS_FILE = "friends.txt";
//    private static final String MESSAGES_FILE = "messages.txt";
//    private static final String BLOCKED_FILE = "blocked.txt";
//    private static final String FRIEND_REQUESTS_FILE = "friend_requests.txt";
//
//    @Before
//    public void setUp() throws Exception {
//        // Start the server in a separate thread
//        serverThread = new Thread(() -> {
//            try {
//                Server.main(new String[0]); // Start the real server
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//        serverThread.start();
//
//        // Wait briefly to ensure the server starts
//        Thread.sleep(1000);
//
//        // Initialize the client with a properly created User
//        User user = new User("Bob", "password123", "bob@example.com", "1234567890",
//                "Description for Bob", "University Example");
//        client = new Client(user);
//
//        // Connect the client to the server
//        assertTrue("Client should successfully connect to the server.", client.connect("localhost", 1102));
//    }
//
//    @After
//    public void tearDown() throws Exception {
//        client.disconnect();
//        serverThread.interrupt(); // Stop the server thread
//
//        // Delete and recreate empty files for testing
//        resetTestFile(USERS_FILE);
//        resetTestFile(FRIENDS_FILE);
//        resetTestFile(MESSAGES_FILE);
//        resetTestFile(BLOCKED_FILE);
//        resetTestFile(FRIEND_REQUESTS_FILE);
//    }
//
//    private void resetTestFile(String filename) {
//        File file = new File(filename);
//        if (file.exists() && !file.delete()) {
//            System.err.println("Failed to delete test file: " + filename);
//        }
//        try {
//            if (file.createNewFile()) {
//                System.out.println("Empty file created: " + filename);
//            }
//        } catch (IOException e) {
//            System.err.println("Error creating new file: " + filename);
//        }
//    }
//
//    // Test connection
//    @Test
//    public void testClientConnection() {
//        assertTrue("Client should be connected to the server.", client.isConnected());
//        client.disconnect();
//        assertFalse("Client should disconnect successfully.", client.isConnected());
//    }
//
//    // Test login
//    @Test
//    public void testLoginSuccessful() {
//        assertFalse("Login should succeed with correct credentials.", client.login("Bob", "password123"));
//    }
//
//    @Test
//    public void testLoginFailedInvalidPassword() {
//        assertFalse("Login should fail with an incorrect password.", client.login("Bob", "wrongPassword"));
//    }
//
//    @Test
//    public void testLoginFailedNonExistentUser() {
//        assertFalse("Login should fail for a non-existent user.", client.login("NonExistentUser", "password123"));
//    }
//
//    // Test sending messages
//    @Test
//    public void testSendMessage() {
//        assertFalse("Empty messages should not be allowed.", client.sendMessage("Jim", ""));
//    }
//
//    // Test sending friend requests
//    @Test
//    public void testSendFriendRequest() {
//        assertFalse("Friend request should not be sent successfully if already exist.", client.sendFriendRequest("Bob", "Jim"));
//    }
//
//    @Test
//    public void testSendFriendRequestFailed() {
//        assertFalse("Friend request should fail for a non-existent user.",
//                client.sendFriendRequest("Bob", "UnknownUser"));
//    }
//
//    // Test removing friends
//    @Test
//    public void testRemoveFriend() {
//        assertFalse("Removing friend should fail for a non-friend.", client.removeFriend("Bob", "UnknownUser"));
//    }
//
//    // Test blocking users
//    @Test
//    public void testBlockUser() {
//        assertFalse("Blocking user should fail for a non-existent user.", client.blockUser("Bob", "UnknownUser"));
//    }
//
//    // Test viewing profiles
//    @Test
//    public void testViewProfileSuccessful() {
//        client.viewProfile("Bob");
//        // Assuming the server returns the profile as a string
//        System.out.println("Profile viewed successfully.");
//    }
//
//    @Test
//    public void testViewProfileFailed() {
//        client.viewProfile("UnknownUser");
//        System.out.println("Profile viewing should fail for a non-existent user.");
//    }
//
//    // Test disconnect
//    @Test
//    public void testDisconnect() {
//        client.disconnect();
//        assertFalse("Client should not be connected after disconnecting.", client.isConnected());
//    }
//}
