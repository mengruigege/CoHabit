import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Team Project Phase 1 - CoHabit
 *
 * This program works to implement a roommate search algorithm
 *
 * @author Aidan Lefort, Andrew Tang, Keya Jadhav, Rithvik Siddenki, Rui Meng
 * @version November 3rd, 2024
 */

public class TestClient {
    private Thread serverThread;
    private Client client;

    @Before
    public void setUp() throws Exception {
        // Start the server in a separate thread
        serverThread = new Thread(() -> {
            try {
                Server.main(new String[0]); // Start the real server
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        serverThread.start();

        // Wait briefly to ensure the server starts
        Thread.sleep(1000);

        // Initialize the client with a properly created User
        User user = new User("Bob", "password123", "bob@example.com", "1234567890",
                             "Description for Bob", "University Example");
        User user2 = new User("Jim", "password234", "jim@gmail.com",
                              "2345678901", "Test user Jim", "University B");
        client = new Client(user);

        // Connect the client to the server
        assertTrue("Client should successfully connect to the server.", client.connect("localhost", 1102));
    }

    @After
    public void tearDown() throws Exception {
        client.disconnect();
        serverThread.interrupt(); // Stop the server thread
    }

    // Test connection
    @Test
    public void testClientConnection() {
        assertTrue("Client should be connected to the server.", client.isConnected());
        client.disconnect();
        assertFalse("Client should disconnect successfully.", client.isConnected());
    }

    // Test login
    @Test
    public void testLoginSuccessful() {
        assertTrue("Login should succeed with correct credentials.", client.login("Bob", "password123"));
    }

    @Test
    public void testLoginFailedInvalidPassword() {
        assertFalse("Login should fail with an incorrect password.", client.login("Bob", "wrongPassword"));
    }

    @Test
    public void testLoginFailedNonExistentUser() {
        assertFalse("Login should fail for a non-existent user.", client.login("NonExistentUser", "password123"));
    }

    // Test sending messages
    @Test
    public void testSendMessageSuccessful() {
        assertTrue("Message should be sent successfully.", client.sendMessage("Jim", "Hello there!"));
    }

    @Test
    public void testSendMessageFailedNonExistentUser() {
        assertFalse("Message sending should fail for a non-existent receiver.",
                    client.sendMessage("UnknownUser", "Hello!"));
    }

    @Test
    public void testSendMessageEmptyMessage() {
        assertFalse("Empty messages should not be allowed.", client.sendMessage("Jim", ""));
    }

    // Test sending friend requests
    @Test
    public void testSendFriendRequestSuccessful() {

        assertTrue("Friend request should be sent successfully.", client.sendFriendRequest("Bob", "Jim"));
    }

    @Test
    public void testSendFriendRequestFailed() {
        assertFalse("Friend request should fail for a non-existent user.",
                    client.sendFriendRequest("Bob", "UnknownUser"));
    }

    // Test removing friends
    @Test
    public void testRemoveFriendSuccessful() {
        client.sendFriendRequest("Bob", "Jim");
        client.acceptFriendRequest("Jim");
        assertTrue("Removing friend should succeed for existing friends.", client.removeFriend("Bob", "Jim"));
    }

    @Test
    public void testRemoveFriendFailed() {
        assertFalse("Removing friend should fail for a non-friend.", client.removeFriend("Bob", "UnknownUser"));
    }

    // Test blocking users
    @Test
    public void testBlockUserSuccessful() {
        assertTrue("Blocking user should succeed.", client.blockUser("Bob", "Jim"));
    }

    @Test
    public void testBlockUserFailed() {
        assertFalse("Blocking user should fail for a non-existent user.", client.blockUser("Bob", "UnknownUser"));
    }

    // Test viewing profiles
    @Test
    public void testViewProfileSuccessful() {
        client.viewProfile("Bob");
        // Assuming the server returns the profile as a string
        System.out.println("Profile viewed successfully.");
    }

    @Test
    public void testViewProfileFailed() {
        client.viewProfile("UnknownUser");
        System.out.println("Profile viewing should fail for a non-existent user.");
    }

    // Test disconnect
    @Test
    public void testDisconnect() {
        client.disconnect();
        assertFalse("Client should not be connected after disconnecting.", client.isConnected());
    }
}
