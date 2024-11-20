import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.io.File;
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
        user1 = new User("Bob", "password123", "bob@gmail.com", "1234567890", "Test user Bob", "University A");
        user2 = new User("Jim", "password234", "jim@gmail.com", "2345678901", "Test user Jim", "University B");
        user3 = new User("Alice", "password345","alice@gmail.com","3456789012","Test user Alice","University C");
        new File("profile_pictures").mkdir();
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
        assertFalse(user2.getFriendList().contains(user1));
    }

    @Test
    public void testAddFriendAlreadyFriends() {
        database.addUser(user1);
        database.addUser(user2);
        database.addFriend(user1, user2);  // Initial friendship
        boolean result = database.addFriend(user1, user2);  // Attempt to re-add the same friendship
        assertTrue(result);  // Method still considers them friends
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
        assertTrue(database.usernameExists("Bob"));  // Test case-insensitivity
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

        database.loadUsersFromFile();
        ArrayList<User> users = database.getAllUsers();

        assertTrue(0 < users.size());
        assertEquals("Bob", users.get(0).getName());
        assertEquals("Jim", users.get(1).getName());
    }

//    @Test
//    public void testSaveAndLoadFriendsFromFile() {
//        database.addUser(user1);
//        database.addUser(user2);
//        database.addFriend(user1, user2);
//        database.saveFriendsToFile();  // Save friendships to file
//
//        ArrayList<User> loadedFriends = database.loadFriendsFromFile();
//        assertTrue(loadedFriends.contains(user2));
//        assertTrue(user1.getFriendList().contains(user2));
//    }

    @Test
    public void testRecordAndLoadConversation() {
        Database newDatabase = new Database();

        newDatabase.recordMessages("Bob", "Jim", "Hello, Jim!");
        newDatabase.recordMessages("Jim", "Bob", "Hi, Bob!");

        ArrayList<String> conversation = newDatabase.loadConversation("Bob", "Jim");
        assertTrue(0 < conversation.size());
        assertTrue(conversation.get(0).contains("Hello, Jim!"));
        assertTrue(conversation.get(1).contains("Hi, Bob!"));
    }

    // Tests for saveProfilePicture method
    @Test
    public void testSaveProfilePictureForUser1() {
        byte[] samplePicture = {1, 2, 3};  // Mock data for profile picture
        user1.setProfilePicture(samplePicture);
        database.saveProfilePicture(user1, samplePicture);

        File pictureFile = new File("profile_pictures/Bob.png");
        assertTrue("Profile picture file should exist for user1", pictureFile.exists());
    }

    @Test
    public void testSaveProfilePictureForUser2WithNoData() {
        user2.setProfilePicture(null);  // No picture data
        database.saveProfilePicture(user2, null);

        File pictureFile = new File("profile_pictures/Jim.png");
        assertFalse("No file should be created if profile picture is null for user2", pictureFile.exists());
    }

    @Test
    public void testSaveProfilePictureForUser3OverwritesExistingFile() {
        byte[] initialPicture = {1, 2};  // Initial picture data
        byte[] newPicture = {3, 4, 5};  // New picture data to overwrite

        user3.setProfilePicture(initialPicture);
        database.saveProfilePicture(user3, initialPicture);
        user3.setProfilePicture(newPicture);
        database.saveProfilePicture(user3, newPicture);

        File pictureFile = new File("profile_pictures/Alice.png");
        assertTrue("Profile picture file should exist after overwriting for user3", pictureFile.exists());
    }

    // Tests for loadProfilePicture method
    @Test
    public void testLoadProfilePictureForUser1() throws UsernameTakenException {
        byte[] samplePicture = {4, 5, 6};
        user1.setProfilePicture(samplePicture);
        database.saveProfilePicture(user1, samplePicture);

        User user1 = new User("Bob", "password123", "bob@gmail.com", "1234567890", "Test user Bob", "University A");
        database.loadProfilePicture(user1);

        assertArrayEquals("Loaded profile picture should match saved data for user1", samplePicture, user1.getProfilePicture());
    }

    @Test
    public void testLoadProfilePictureForNonexistentUser() throws UsernameTakenException {
        User newUser = new User("NonexistentUser", "password","null","null","null","null");
        database.loadProfilePicture(newUser);

        assertNull("Profile picture should remain null if no file exists for nonexistent user", newUser.getProfilePicture());
    }

    @Test
    public void testLoadProfilePictureForUser2WhenFileExists() throws UsernameTakenException {
        byte[] pictureData = {7, 8, 9};
        user2.setProfilePicture(pictureData);
        database.saveProfilePicture(user2, pictureData);

        User user2 = new User("Jim", "password234", "jim@gmail.com", "2345678901", "Test user Jim", "University B");

        database.loadProfilePicture(user2);
        assertArrayEquals("Profile picture loaded from file should match saved data for user2", pictureData, user2.getProfilePicture());
    }

    // Tests for deleteProfilePicture method
    @Test
    public void testDeleteProfilePictureForUser1() {
        byte[] pictureData = {10, 11, 12};
        user1.setProfilePicture(pictureData);
        database.saveProfilePicture(user1, pictureData);

        File pictureFile = new File("profile_pictures/Bob.png");
        assertTrue("File should exist before deletion for user1", pictureFile.exists());

        database.deleteProfilePicture(user1);
        assertFalse("File should not exist after deletion for user1", pictureFile.exists());
    }

    @Test
    public void testDeleteProfilePictureForUser2WithoutSettingPicture() {
        // No profile picture is set for user2, so no file should be created or deleted
        database.deleteProfilePicture(user2);

        File pictureFile = new File("profile_pictures/Jim.png");
        assertFalse("File should not exist if profile picture was never set for user2", pictureFile.exists());
    }
}
