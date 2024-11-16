import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestServer {

    private Server server; // Server instance
    private Database database; // Mocked Database instance
    private User user1;
    private User user2;

    @Before
    public void setUp() throws UsernameTakenException {
        // Initialize the server and database
        server = new Server();
        database = new Database();
        database.loadUsersFromFile();

        // Create test users
        user1 = new User("Bob", "password123", "bob@example.com", "1234567890", "Test user Bob", "University A");
        user2 = new User("Jim", "password234", "jim@example.com", "9876543210", "Test user Jim", "University B");

        // Add test users to the database
        database.addUser(user1);
        database.addUser(user2);
    }

    // Test login functionality
    @Test
    public void testLogin_Successful() {
        assertTrue("Login should succeed with correct credentials.", server.login("Bob", "password123"));
    }

    @Test
    public void testLogin_InvalidPassword() {
        assertFalse("Login should fail with an incorrect password.", server.login("Bob", "wrongPassword"));
    }

    @Test
    public void testLogin_UserNotFound() {
        assertFalse("Login should fail for a non-existent user.", server.login("UnknownUser", "password123"));
    }

    // Test register functionality
    @Test
    public void testRegister_NewUser() throws UsernameTakenException {
        User newUser = new User("Alice", "securePass", "alice@example.com", "5555555555", "Test user Alice", "University C");
        assertTrue("Register should succeed for a new user.", server.register(newUser));
    }

    @Test
    public void testRegister_ExistingUser() {
        assertFalse("Register should fail for an already registered user.", server.register(user1));
    }

    // Test sendMessage functionality
    @Test
    public void testSendMessage_Successful() {
        assertTrue("Message should be sent successfully.", server.sendMessage(user1, user2, "Hello"));
    }

    @Test
    public void testSendMessage_ReceiverNull() {
        assertFalse("Message should fail if the receiver is null.", server.sendMessage(user1, null, "Hello"));
    }

    @Test
    public void testSendMessage_EmptyMessage() {
        assertTrue("Empty messages should still be allowed.", server.sendMessage(user1, user2, ""));
    }

    // Test addFriend functionality
    @Test
    public void testAddFriend_Successful() {
        assertTrue("Users should be added as friends.", server.addFriend(user1, user2));
    }

    @Test
    public void testAddFriend_UserNull() {
        assertFalse("Adding friends should fail if the user is null.", server.addFriend(null, user2));
    }

    @Test
    public void testAddFriend_AlreadyFriends() {
        server.addFriend(user1, user2);
        assertFalse("Adding friends should fail if already friends.", server.addFriend(user1, user2));
    }

    // Test viewProfile functionality
    @Test
    public void testViewProfile_ExistingUser() {
        String expectedProfile = user1.toString();
        assertEquals("ViewProfile should return the correct user details.", expectedProfile, server.viewProfile("Bob"));
    }

    @Test
    public void testViewProfile_NonExistentUser() {
        assertNull("ViewProfile should return null for a non-existent user.", server.viewProfile("NonExistent"));
    }

    // Test database-related methods
    @Test
    public void testDatabase_UserPersistence() {
        database.saveUsersToFile();
        database.loadUsersFromFile();
        assertNotNull("User Bob should still exist after saving and loading the database.", database.findUserByName("Bob"));
    }

    @Test
    public void testDatabase_MessagePersistence() {
        server.sendMessage(user1, user2, "Test Message");

        ArrayList<String> messages = database.loadConversation("Bob", "Jim");
        assertTrue("The message should persist in the database after saving and loading.", messages.contains("Test Message"));
    }
}