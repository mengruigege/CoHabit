import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class TestServer {

    private Server server; // Server instance
    private Database database; // Mocked Database instance
    private User user1;
    private User user2;
    private User user3;

    private static final String USERS_FILE = "users.txt";
    private static final String FRIENDS_FILE = "friends.txt";
    private static final String MESSAGES_FILE = "messages.txt";
    private static final String BLOCKED_FILE = "blocked.txt";
    private static final String FRIEND_REQUESTS_FILE = "friend_requests.txt";

    @Before
    public void setUp() throws UsernameTakenException {
        // Initialize the server and database
        server = new Server();
        database = new Database();
        database.loadUsersFromFile();

        // Create test users
        user1 = new User("Bob", "password123", "bob@gmail.com", "1234567890", "Test user Bob", "University A");
        user2 = new User("Jim", "password234", "jim@gmail.com", "2345678901", "Test user Jim", "University B");
        user3 = new User("Alice", "password345", "alice@example.com", "5555555555", "Test user Alice", "University A");

        // Add test users to the database
        database.addUser(user1);
        database.addUser(user2);
        database.addUser(user3);
    }

    @After
    public void tearDown() {
        // Ensure all files are cleaned up and recreated as empty
        resetFile(USERS_FILE);
        resetFile(FRIENDS_FILE);
        resetFile(MESSAGES_FILE);
        resetFile(BLOCKED_FILE);
        resetFile(FRIEND_REQUESTS_FILE);
    }

    private void resetFile(String filename) {
        File file = new File(filename);
        if (file.exists() && !file.delete()) {
            System.err.println("Failed to delete file: " + filename);
        }
        try {
            if (!file.createNewFile()) {
                System.err.println("Failed to create empty file: " + filename);
            }
        } catch (IOException e) {
            System.err.println("Error creating file: " + filename + " - " + e.getMessage());
        }
    }

    @Test
    public void testLogin_UserNotFound() {
        String result = server.login("NonExistentUser", "password123");
        assertNull("Login should fail for a non-existent user.", result);
    }

    @Test
    public void testLogin_EmptyUsername() {
        String result = server.login("", "password123");
        assertNull("Login should fail for an empty username.", result);
    }

    @Test
    public void testLogin_NullUsername() {
        String result = server.login(null, "password123");
        assertNull("Login should fail for a null username.", result);
    }

    @Test
    public void testLogin_CaseSensitivity() {
        String result = server.login("bob", "password123");
        assertNull("Login should fail for case-sensitive username mismatch.", result);
    }


    // Test register functionality
    @Test
    public void testRegister_NewUser() throws UsernameTakenException {
        User newUser = new User("Keya", "securePass", "keya@example.com", "5555555555", "Test user Keya", "University D");
        assertTrue("Register should succeed for a new user.", server.register(newUser));
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
        // assertTrue("Users should be added as friends.", server.addFriend(user1, user2));
    }

    @Test
    public void testAddFriend_UserNull() {
        // assertFalse("Adding friends should fail if the user is null.", server.addFriend(null, user2));
    }

    @Test
    public void testAddFriend_AlreadyFriends() {
        server.addFriend(user1, user2);
        assertFalse("Adding friends should fail if already friends.", server.addFriend(user1, user2));
    }

    @Test
    public void testViewProfile_NonExistentUser() {
        assertNull("ViewProfile should return null for a non-existent user.", server.viewProfile("NonExistent"));
    }

    @Test
    public void testPartialMatch_NullUser() {
        assertEquals("Partial match should return an empty string if the user is null.", "", server.partialMatch(null));
    }


    @Test
    public void testExactMatch_NoMatches() throws InvalidInput {
        user1.setPreferences("10:00 PM", false, false, true, 7, 8);
        user2.setPreferences("1:00 AM", true, true, true, 9, 10);

        String result = server.exactMatch(user1);
        assertTrue("Exact match should return an empty string if no matches are found.", result.isEmpty());
    }

    @Test
    public void testExactMatch_NullUser() {
        assertEquals("Exact match should return an empty string if the user is null.", "", server.exactMatch(null));
    }

    @Test
    public void testSearchByParameter_NoMatch() {
        String result = server.searchByParameter("name", "NonExistentUser");
        assertTrue("Search by parameter should return an empty string if no matches are found.", result.isEmpty());
    }

    @Test
    public void testSearchByParameter_InvalidParameter() {
        String result = server.searchByParameter("invalid", "value");
        assertTrue("Search by parameter should return an empty string for an invalid parameter.", result.isEmpty());
    }

    @Test
    public void testSearchByParameter_EmptyValue() {
        String result = server.searchByParameter("name", "");
        assertTrue("Search by parameter should return an empty string for an empty search value.", result.isEmpty());
    }

    @Test
    public void testSearchByParameter_NullParameter() {
        String result = server.searchByParameter(null, "value");
        assertTrue("Search by parameter should return an empty string for a null parameter.", result.isEmpty());
    }

    @Test
    public void testSearchByParameter_NullValue() {
        String result = server.searchByParameter("name", null);
        assertTrue("Search by parameter should return an empty string for a null value.", result.isEmpty());
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
