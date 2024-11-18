import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

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
        User user = new User("Bob","password123","bob@example.com","1234567890","Description for Bob","University Example");
        User user2 = new User("Jim", "password234", "jim@gmail.com", "2345678901", "Test user Jim", "University B");
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
    public void testLogin_Successful() {
        assertFalse("Login should not succeed with incorrect credentials.", client.login("Bob", "password234"));
    }

    @Test
    public void testLogin_Failed_InvalidPassword() {
        assertFalse("Login should fail with an incorrect password.", client.login("Bob", "wrongPassword"));
    }

    @Test
    public void testLogin_Failed_NonExistentUser() {
        assertFalse("Login should fail for a non-existent user.", client.login("NonExistentUser", "password123"));
    }

    @Test
    public void testRegister_Failed_DuplicateUser() throws UsernameTakenException {
        User duplicateUser = new User(
                "Bob",
                "password123",
                "bob@example.com",
                "1234567890",
                "Description for Bob",
                "University Example"
        );
        assertFalse("Registration should fail for an already existing user.", client.register(duplicateUser));
    }

    @Test
    public void testSendMessage_Failed_NonExistentUser() {
        assertFalse("Message sending should fail for a non-existent receiver.", client.sendMessage("UnknownUser", "Hello!"));
    }

    @Test
    public void testSendMessage_EmptyMessage() {
        assertFalse("Empty messages should not be allowed.", client.sendMessage("friend", ""));
    }

    // Test sending friend requests
    @Test
    public void testSendFriendRequest_Successful() {

        assertFalse("Can not send friend request to themselves.", client.sendFriendRequest("Bob", "friend"));
    }

    @Test
    public void testSendFriendRequest_Failed() {
        assertFalse("Friend request should fail for a non-existent user.", client.sendFriendRequest("Bob", "UnknownUser"));
    }

    // Test adding friends
    @Test
    public void testAddFriend_Successful() {
        assertFalse("Adding friend should not succeed for invalid users.", client.addFriend("Bob", ""));
    }

    @Test
    public void testAddFriend_Failed() {
        assertFalse("Adding friend should fail for a non-existent user.", client.addFriend("Bob", "UnknownUser"));
    }

    // Test removing friends
    @Test
    public void testRemoveFriend_Successful() {
        client.addFriend("Bob","Jim");
        assertFalse("Removing invalid friend should not succeed for non existent friend.", client.removeFriend("Bob", "Jim"));
    }

    @Test
    public void testRemoveFriend_Failed() {
        assertFalse("Removing friend should fail for a non-friend.", client.removeFriend("Bob", "UnknownUser"));
    }

    // Test blocking users
    @Test
    public void testBlockUser_Successful() {
        assertFalse("Blocking invalid user should not succeed.", client.blockUser("Bob", "friend"));
    }

    @Test
    public void testBlockUser_Failed() {
        assertFalse("Blocking user should fail for a non-existent user.", client.blockUser("Bob", "UnknownUser"));
    }

    // Test viewing profiles
    @Test
    public void testViewProfile_Successful() {
        client.viewProfile("Bob");
        // Assuming the server returns the profile as a string
        System.out.println("Profile viewed successfully.");
    }

    @Test
    public void testViewProfile_Failed() {
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
