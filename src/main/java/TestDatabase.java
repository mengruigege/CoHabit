import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

public class TestDatabase {

    private Database database;
    private User bob, jim, alice;

    @BeforeEach
    public void setup() throws UsernameTakenException, InvalidInput {
        database = new Database();

        // Initialize test users
        bob = new User("Bob", "password123", "bob@example.com", 
                       "123-456-7890", "Description", "UniversityA");
        jim = new User("Jim", "password234", "jim@example.com", 
                       "234-567-8901", "Description", "UniversityB");
        alice = new User("Alice", "password345", "alice@example.com", 
                         "345-678-9012", "Description", "UniversityC");

        // Set preferences (optional, assuming User class has the method)
        bob.setPreferences("10:00 PM", true, false, true, 5, 10);
        jim.setPreferences("11:00 PM", false, true, false, 4, 15);
        alice.setPreferences("10:00 PM", true, false, true, 5, 10);

        // Add users to database
        database.addUser(bob);
        database.addUser(jim);
    }

    // Test cases for `addUser`
    @Test
    public void testAddUserValid() {
        assertTrue(database.addUser(alice)); // Should add successfully
    }

    @Test
    public void testAddUserDuplicate() {
        assertFalse(database.addUser(bob)); // Duplicate user
    }

    @Test
    public void testAddUserNull() {
        assertFalse(database.addUser(null)); // Null user
    }

    // Test cases for `findUserByName`
    @Test
    public void testFindUserByNameValid() {
        assertEquals(bob, database.findUserByName("Bob"));
    }

    @Test
    public void testFindUserByNameInvalid() {
        assertNull(database.findUserByName("NonExistentUser"));
    }

    @Test
    public void testFindUserByNameNull() {
        assertNull(database.findUserByName(null));
    }

    // Test cases for `sendFriendRequest`
    @Test
    public void testSendFriendRequestValid() {
        assertTrue(database.sendFriendRequest(bob, jim));
    }

    @Test
    public void testSendFriendRequestDuplicate() {
        database.sendFriendRequest(bob, jim);
        assertFalse(database.sendFriendRequest(bob, jim)); // Duplicate request
    }

    @Test
    public void testSendFriendRequestBlocked() {
        database.blockUser(jim, bob);
        assertFalse(database.sendFriendRequest(bob, jim)); // Blocked user
    }

    // Test cases for `acceptFriendRequest`
    @Test
    public void testAcceptFriendRequestValid() {
        database.sendFriendRequest(bob, jim);
        assertTrue(database.acceptFriendRequest(jim, bob));
    }

    @Test
    public void testAcceptFriendRequestNoRequest() {
        assertFalse(database.acceptFriendRequest(jim, bob)); // No request exists
    }

    @Test
    public void testAcceptFriendRequestInvalidUsers() {
        assertFalse(database.acceptFriendRequest(null, bob)); // Invalid user
    }

    // Test cases for `rejectFriendRequest`
    @Test
    public void testRejectFriendRequestValid() {
        database.sendFriendRequest(bob, jim);
        assertTrue(database.rejectFriendRequest(jim, bob));
    }

    @Test
    public void testRejectFriendRequestInvalidUsers() {
        assertFalse(database.rejectFriendRequest(null, bob)); // Invalid user
    }

    // Test cases for `removeUser`
    @Test
    public void testRemoveUserValid() {
        assertTrue(database.removeUser(bob));
    }

    @Test
    public void testRemoveUserNonExistent() {
        assertFalse(database.removeUser(alice)); // User not in database
    }

    @Test
    public void testRemoveUserNull() {
        assertFalse(database.removeUser(null)); // Null user
    }

    // Test cases for `blockUser`
    @Test
    public void testBlockUserValid() {
        assertTrue(database.blockUser(bob, jim));
    }

    @Test
    public void testBlockUserAlreadyBlocked() {
        database.blockUser(bob, jim);
        assertFalse(database.blockUser(bob, jim)); // Already blocked
    }

    @Test
    public void testBlockUserInvalid() {
        assertFalse(database.blockUser(bob, null)); // Null blocked user
    }

    // Test cases for `unblockUser`
    @Test
    public void testUnblockUserValid() {
        database.blockUser(bob, jim);
        assertTrue(database.unblockUser(bob, jim));
    }

    @Test
    public void testUnblockUserNotBlocked() {
        assertFalse(database.unblockUser(bob, jim)); // Not blocked
    }

    @Test
    public void testUnblockUserInvalid() {
        assertFalse(database.unblockUser(bob, null)); // Null unblocked user
    }

    // Test cases for `sendMessage`
    @Test
    public void testSendMessageValid() {
        assertTrue(database.sendMessage(bob, jim, "Hello Jim!"));
    }

    @Test
    public void testSendMessageEmpty() {
        assertFalse(database.sendMessage(bob, jim, "")); // Empty message
    }

    @Test
    public void testSendMessageInvalidUser() {
        assertFalse(database.sendMessage(null, jim, "Hello!")); // Null sender
    }

    // Test cases for `getMessage`
    @Test
    public void testGetMessageWithMessages() {
        database.sendMessage(bob, jim, "Hello Jim!");
        database.sendMessage(jim, bob, "Hi Bob!");
        ArrayList<String> messages = database.getMessage(bob, jim);
        assertEquals(2, messages.size());
    }

    @Test
    public void testGetMessageNoMessages() {
        ArrayList<String> messages = database.getMessage(bob, alice);
        assertTrue(messages.isEmpty());
    }

    @Test
    public void testGetMessageInvalidUsers() {
        ArrayList<String> messages = database.getMessage(null, jim);
        assertTrue(messages.isEmpty());
    }

    // Test cases for `partialMatch`
    @Test
    public void testPartialMatchWithResults() {
        database.addUser(alice); // Alice has similar preferences to Bob
        String result = database.partialMatch(bob, "<<END>>");
        assertNotNull(result);
        assertTrue(result.contains("Alice"));
    }

    @Test
    public void testPartialMatchNoResults() {
        String result = database.partialMatch(jim, "<<END>>");
        assertEquals("", result);
    }

    @Test
    public void testPartialMatchNullUser() {
        String result = database.partialMatch(null, "<<END>>");
        assertNull(result);
    }

    // Test cases for `exactMatch`
    @Test
    public void testExactMatchWithResults() {
        database.addUser(alice); // Alice has the same preferences as Bob
        String result = database.exactMatch(bob, "<<END>>");
        assertNotNull(result);
        assertTrue(result.contains("Alice"));
    }

    @Test
    public void testExactMatchNoResults() {
        String result = database.exactMatch(jim, "<<END>>");
        assertEquals("", result);
    }

    @Test
    public void testExactMatchNullUser() {
        String result = database.exactMatch(null, "<<END>>");
        assertNull(result);
    }
}
