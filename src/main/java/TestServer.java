import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

public class TestServer {

    private Server server;
    private Database mockDatabase;
    private User bob, jim, alice;

    @BeforeEach
    public void setup() throws UsernameTakenException {
        // Mock database initialization
        mockDatabase = new Database();
        server = new Server(null);
        server.database = mockDatabase; // Injecting mock database

        // Adding test users to the database
        bob = new User("Bob", "password123", "bob@example.com", 
                       "123-456-7890", "Student", "SomeUniversity");
        jim = new User("Jim", "password234", "jim@example.com", 
                       "098-765-4321", "Worker", "AnotherUniversity");
        alice = new User("Alice", "password345", "alice@example.com", 
                         "555-555-5555", "Teacher", "YetAnotherUniversity");

        mockDatabase.addUser(bob);
        mockDatabase.addUser(jim);
    }

    // Test cases for login
    @Test
    public void testLoginValidCredentials() throws UsernameTakenException, InvalidInput {
        mockDatabase.initializeDatabase();
        String result = server.login("Bob", "password123");
        assertNotNull(result);
        assertTrue(result.contains("Bob"));
    }

    @Test
    public void testLoginInvalidPassword() throws UsernameTakenException, InvalidInput {
        mockDatabase.initializeDatabase();
        String result = server.login("Jim", "wrongpassword");
        assertNull(result);
    }

    @Test
    public void testLoginNonExistentUser() throws UsernameTakenException, InvalidInput {
        mockDatabase.initializeDatabase();
        String result = server.login("Alice", "password345");
        assertNull(result);
    }

    // Test cases for register
    @Test
    public void testRegisterExistingUser() {
        boolean result = server.register(bob);
        assertFalse(result);
    }

    @Test
    public void testRegisterInvalidUser() throws UsernameTakenException {
        User invalidUser = new User(null, "password", "invalid@example.com", 
                                    "000-000-0000", "None", "InvalidUniversity");
        assertFalse(server.register(invalidUser));
    }

    // Test cases for sendMessage
    @Test
    public void testSendMessageValid() {
        boolean result = server.sendMessage(bob, jim, "Hello Jim!");
        assertTrue(result);
    }

    @Test
    public void testSendMessageToNonExistentUser() {
        boolean result = server.sendMessage(bob, null, "Hello Alice!");
        assertFalse(result);
    }

    @Test
    public void testSendMessageEmptyMessage() {
        boolean result = server.sendMessage(jim, bob, "");
        assertFalse(result);
    }

    // Test cases for getMessageHistory
    @Test
    public void testGetMessageHistoryValidUsers() {
        mockDatabase.sendMessage(bob, jim, "Hello!");
        mockDatabase.sendMessage(jim, bob, "Hi!");
        ArrayList<String> messages = server.getMessageHistory(bob, jim);
        assertEquals(2, messages.size());
    }

    @Test
    public void testGetMessageHistoryNoMessages() {
        ArrayList<String> messages = server.getMessageHistory(bob, alice);
        assertTrue(messages.isEmpty());
    }

    @Test
    public void testGetMessageHistoryNonExistentUser() {
        ArrayList<String> messages = server.getMessageHistory(bob, null);
        assertEquals(new ArrayList<>(), messages);
    }

    // Test cases for sendFriendRequest
    @Test
    public void testSendFriendRequestValid() {
        boolean result = server.sendFriendRequest(bob, jim);
        assertTrue(result);
    }

    @Test
    public void testSendFriendRequestNonExistentUser() {
        boolean result = server.sendFriendRequest(bob, null);
        assertFalse(result);
    }

    @Test
    public void testSendFriendRequestDuplicate() {
        server.sendFriendRequest(bob, jim);
        boolean result = server.sendFriendRequest(bob, jim);
        assertFalse(result);
    }

    @Test
    public void testViewFriendRequestsWithPending() {
        server.sendFriendRequest(bob, jim);
        ArrayList<String> requests = server.viewFriendRequests(jim);
        assertEquals(1, requests.size());
        assertTrue(requests.contains("Bob"));
    }

    @Test
    public void testViewFriendRequestsNoRequests() {
        ArrayList<String> requests = server.viewFriendRequests(bob);
        assertTrue(requests.isEmpty());
    }

    @Test
    public void testViewFriendRequestsNonExistentUser() {
        ArrayList<String> requests = server.viewFriendRequests(alice);
        assertEquals(new ArrayList<>(), requests);
    }

    // Test cases for declineFriendRequest
    @Test
    public void testDeclineFriendRequestValid() {
        server.sendFriendRequest(bob, jim);
        boolean result = server.declineFriendRequest(jim, bob);
        assertTrue(result);
    }

    @Test
    public void testDeclineFriendRequestNonExistentUser() {
        boolean result = server.declineFriendRequest(null, bob);
        assertFalse(result);
    }

    // Test cases for acceptFriendRequest
    @Test
    public void testAcceptFriendRequestValid() {
        server.sendFriendRequest(bob, jim);
        boolean result = server.acceptFriendRequest(jim, bob);
        assertTrue(result);
    }

    @Test
    public void testAcceptFriendRequestNoRequest() {
        boolean result = server.acceptFriendRequest(jim, alice);
        assertFalse(result);
    }

    @Test
    public void testAcceptFriendRequestNonExistentUser() {
        boolean result = server.acceptFriendRequest(null, bob);
        assertFalse(result);
    }

    // Test cases for removeFriend
    @Test
    public void testRemoveFriendValid() {
        server.sendFriendRequest(bob, jim);
        server.acceptFriendRequest(jim, bob);
        boolean result = server.removeFriend(jim, bob);
        assertTrue(result);
    }

    @Test
    public void testRemoveFriendNotFriends() {
        boolean result = server.removeFriend(jim, null);
        assertFalse(result);
    }

    @Test
    public void testRemoveFriendNonExistentUser() {
        boolean result = server.removeFriend(null, bob);
        assertFalse(result);
    }

    // Test cases for blockUser
    @Test
    public void testBlockUserValid() {
        boolean result = server.blockUser(jim, bob);
        assertTrue(result);
    }

    @Test
    public void testBlockUserAlreadyBlocked() {
        server.blockUser(jim, bob);
        boolean result = server.blockUser(jim, bob);
        assertFalse(result);
    }

    @Test
    public void testBlockUserNonExistent() {
        boolean result = server.blockUser(jim, null);
        assertFalse(result);
    }

    // Test cases for unblockUser
    @Test
    public void testUnblockUserValid() {
        server.blockUser(jim, bob);
        boolean result = server.unblockUser(jim, bob);
        assertTrue(result);
    }

    @Test
    public void testUnblockUserNotBlocked() {
        boolean result = server.unblockUser(jim, bob);
        assertFalse(result);
    }

    @Test
    public void testUnblockUserNonExistent() {
        boolean result = server.unblockUser(jim, alice);
        assertFalse(result);
    }

    // Test cases for viewBlockedUsers
    @Test
    public void testViewBlockedUsersWithBlocked() {
        server.blockUser(jim, bob);
        ArrayList<String> blockedUsers = server.viewBlockedUsers(jim);
        assertEquals(1, blockedUsers.size());
        assertTrue(blockedUsers.contains("Bob"));
    }

    @Test
    public void testViewBlockedUsersNoBlocked() {
        ArrayList<String> blockedUsers = server.viewBlockedUsers(jim);
        assertTrue(blockedUsers.isEmpty());
    }

    @Test
    public void testViewBlockedUsersNonExistentUser() {
        ArrayList<String> blockedUsers = server.viewBlockedUsers(alice);
        assertEquals(new ArrayList<>(), blockedUsers);
    }

    // Test cases for viewFriendsList
    @Test
    public void testViewFriendsListWithFriends() {
        server.sendFriendRequest(bob, jim);
        server.acceptFriendRequest(jim, bob);
        ArrayList<String> friends = server.viewFriendsList(jim);
        assertEquals(1, friends.size());
        assertTrue(friends.contains("Bob"));
    }

    @Test
    public void testViewFriendsListNoFriends() {
        ArrayList<String> friends = server.viewFriendsList(jim);
        assertTrue(friends.isEmpty());
    }

    @Test
    public void testViewFriendsListNonExistentUser() {
        ArrayList<String> friends = server.viewFriendsList(null);
        assertEquals(new ArrayList<>(), friends);
    }

    // Test cases for partialMatch
    @Test
    public void testPartialMatchWithMatches() {
        String result = server.partialMatch(bob);
        assertNotNull(result);
    }

    @Test
    public void testPartialMatchNonExistentUser() {
        String result = server.partialMatch(null);
        assertNull(result);
    }

    // Test cases for exactMatch
    @Test
    public void testExactMatchWithMatches() {
        String result = server.exactMatch(bob);
        assertNotNull(result);
    }

    @Test
    public void testExactMatchNoMatches() throws UsernameTakenException {
        User noMatchUser = new User("NoMatch", "password", 
                                    "no@match.com", "111-111-1111", "None", "None");
        mockDatabase.addUser(noMatchUser);
        String result = server.exactMatch(noMatchUser);
        assertEquals("", result);
    }

    @Test
    public void testExactMatchNonExistentUser() {
        String result = server.exactMatch(null);
        assertNull(result);
    }
}
