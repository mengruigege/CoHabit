import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;

public class TestDatabase {
    private Database database;
    private User user1;
    private User user2;
    private User user3;

    @Before
    public void setUp() throws UsernameTakenException {
        // Initialize a fresh database instance for each test
        database = new Database();
        user1 = new User("Bob", "password123", "bob@gmail.com", "1234567890", "person", "purdue");
        user2 = new User("Jim", "password234", "jim@gmail.com", "2345678901", "person2", "purdue2");
        user3 = new User("Alice", "password345", "alice@gmail.com", "3456789012", "person3", "purdue3");
    }

    // Tests for addUser method
    @Test
    public void testAddUserSuccessfully() {
        boolean result = database.addUser(user1);
        assertTrue(result);
        ArrayList<User> users = database.getAllUsers();
        assertEquals(1, users.size());
        assertEquals("Bob", users.get(0).getName());
    }

    @Test
    public void testAddUserDuplicate() {
        database.addUser(user1);
        boolean result = database.addUser(user1);  // Attempt to add the same user again
        assertFalse(result);  // Duplicate addition should fail
        ArrayList<User> users = database.getAllUsers();
        assertEquals(1, users.size());  // Only one instance of user1 should be present
    }

    @Test
    public void testAddUserNull() {
        boolean result = database.addUser(null);  // Null input should not be added
        assertFalse(result);
        assertTrue(database.getAllUsers().isEmpty());  // Database should remain empty
    }

    // Tests for deleteUser method
    @Test
    public void testDeleteUserSuccessfully() {
        database.addUser(user1);
        boolean result = database.deleteUser(user1);
        assertTrue(result);
        ArrayList<User> users = database.getAllUsers();
        assertFalse(users.contains(user1));
    }

    @Test
    public void testDeleteUserNotInDatabase() {
        boolean result = database.deleteUser(user2);  // Attempt to delete a user not in the database
        assertFalse(result);  // Should return false since user2 is not in the database
    }

    @Test
    public void testDeleteUserNull() {
        database.addUser(user1);
        boolean result = database.deleteUser(null);  // Null input should not affect the database
        assertFalse(result);
        ArrayList<User> users = database.getAllUsers();
        assertTrue(users.contains(user1));  // user1 should still be present
    }

    // Tests for addFriend method
    @Test
    public void testAddFriendSuccessfully() {
        database.addUser(user1);
        database.addUser(user2);
        boolean result = database.addFriend(user1, user2);
        assertTrue(result);
        assertTrue(user1.getFriendList().contains(user2));
        assertTrue(user2.getFriendList().contains(user1));
    }

    @Test
    public void testAddFriendAlreadyFriends() {
        database.addUser(user1);
        database.addUser(user2);
        database.addFriend(user1, user2);  // Initial friendship
        boolean result = database.addFriend(user1, user2);  // Attempt to re-add the same friendship
        assertTrue(result);  // Method still considers them friends
    }

    @Test
    public void testAddFriendNonexistentUser() {
        database.addUser(user1);
        boolean result = database.addFriend(user1, user3);  // user3 is not added to the database
        assertFalse(result);  // Should return false as user3 is not in the database
    }

    // Tests for usernameExists method
    @Test
    public void testUsernameExists() {
        database.addUser(user1);
        assertTrue(database.usernameExists("Bob"));  // Username "Bob" should exist
    }

    @Test
    public void testUsernameExistsNonexistent() {
        assertFalse(database.usernameExists("Nonexistent"));  // Username that hasn't been added should return false
    }

    @Test
    public void testUsernameExistsCaseInsensitive() {
        database.addUser(user1);
        assertTrue(database.usernameExists("bob"));  // Test case-insensitivity
    }

    // Tests for findUserByName method
    @Test
    public void testFindUserByNameSuccessfully() {
        database.addUser(user1);
        database.addUser(user2);
        User foundUser = database.findUserByName("Bob");
        assertNotNull(foundUser);
        assertEquals("Bob", foundUser.getName());
    }

    @Test
    public void testFindUserByNameNonexistent() {
        User foundUser = database.findUserByName("Nonexistent");
        assertNull(foundUser);  // Should return null for a name not in the database
    }

    // Tests for file-based methods (for completeness, but ideally mock these in real-world testing)

    @Test
    public void testLoadUsersFromFile() {
        database.addUser(user1);
        database.addUser(user2);
        database.saveUsersToFile();  // Save to file first

        Database newDatabase = new Database();
        newDatabase.loadUsersFromFile();
        ArrayList<User> users = newDatabase.getAllUsers();

        assertEquals(2, users.size());
        assertEquals("Bob", users.get(0).getName());
        assertEquals("Jim", users.get(1).getName());
    }

    @Test
    public void testSaveAndLoadFriendsFromFile() {
        database.addUser(user1);
        database.addUser(user2);
        database.addFriend(user1, user2);
        database.saveFriendsToFile();  // Save friendships to file

        ArrayList<User> loadedFriends = database.loadFriendsFromFile();
        assertTrue(loadedFriends.contains(user2));
        assertTrue(user1.getFriendList().contains(user2));
    }

    @Test
    public void testRecordAndLoadConversation() {
        database.recordMessages("Bob", "Jim", "Hello, Jim!", "2024-10-01 10:00");
        database.recordMessages("Jim", "Bob", "Hi, Bob!", "2024-10-01 10:05");

        ArrayList<String> conversation = database.loadConversation("Bob", "Jim");
        assertEquals(2, conversation.size());
        assertTrue(conversation.get(0).contains("Hello, Jim!"));
        assertTrue(conversation.get(1).contains("Hi, Bob!"));
    }
}
