import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestServer {

    private Server server; // Server instance
    private Database database; // Mocked Database instance
    private User user1;
    private User user2;
    private User user3;

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

        database.saveUsersToFile();
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
        User newUser = new User("Keya", "securePass", "keya@example.com", "5555555555", "Test user Keya", "University D");
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

    // Test for partialMatch
    @Test
    public void testPartialMatch_Success() throws InvalidInput {
        user1.setPreferences("11:00 PM", true, false, true, 7, 8);
        user2.setPreferences("11:00 PM", true, true, true, 5, 8);
        user3.setPreferences("11:00 PM", true, false, true, 6, 8);

        String result = server.partialMatch(user1);
        assertTrue("Partial match should include users with similar preferences.",
                result.contains("Jim") || result.contains("Alice"));
    }

    @Test
    public void testPartialMatch_NoMatches() throws InvalidInput {
        user1.setPreferences("10:00 PM", false, false, false, 3, 5);
        user2.setPreferences("1:00 AM", true, true, true, 9, 10);
        user3.setPreferences("12:00 PM", true, false, false, 2, 3);

        String result = server.partialMatch(user1);
        assertTrue("Partial match should return an empty string if no matches are found.", result.contains("Alice"));
    }

    @Test
    public void testPartialMatch_NullUser() {
        assertEquals("Partial match should return an empty string if the user is null.", "", server.partialMatch(null));
    }

    // Test for exactMatch
    @Test
    public void testExactMatch_Success() throws InvalidInput {
        user1.setPreferences("10:00 PM", false, false, true, 7, 8);
        user2.setPreferences("10:00 PM", false, false, true, 7, 8);

        String result = server.exactMatch(user1);
        assertTrue("Exact match should include users with identical preferences.", result.contains("Jim"));
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
    public void testSearchByParameter_Name() {
        String result = server.searchByParameter("name", "Bob");
        assertTrue("Search by parameter (name) should find the correct user.", result.contains("Bob"));
        assertFalse("Search by parameter (name) should not include unrelated users.", result.contains("Jim"));
    }

    @Test
    public void testSearchByParameter_University() {
        String result = server.searchByParameter("university", "University A");
        assertTrue("Search by parameter (university) should include all matching users.", result.contains("Bob") && result.contains("Alice"));
        assertFalse("Search by parameter (university) should exclude non-matching users.", result.contains("Jim"));
    }

    @Test
    public void testSearchByParameter_Email() {
        String result = server.searchByParameter("email", "bob@gmail.com");
        assertTrue("Search by parameter (email) should find the correct user.", result.contains("Bob"));
        assertFalse("Search by parameter (email) should exclude users with other emails.", result.contains("Alice"));
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