import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;

public class TestFriendList {
    private User user1;
    private User user2;
    private User user3;
    private User user4;
    private Relationships relationships;
    private Database database;

    @Before
    public void setUp() throws UsernameTakenException {
        database = new Database(); // Initialize with mock or empty database
        user1 = new User("Bob", "password123");
        user2 = new User("Jim", "password234");
        user3 = new User("Alice", "password345");
        user4 = new User("David", "password456");
        relationships = new Relationships(user1, database);
    }

    // Tests for addFriend method
    @Test
    public void testAddFriendSuccessfully() {
        assertTrue(relationships.addFriend(user2));
        assertTrue(relationships.getFriends().contains(user2));
    }

    @Test
    public void testAddFriendAlreadyInList() {
        relationships.addFriend(user2);
        assertFalse(relationships.addFriend(user2));  // Duplicate addition should fail
    }

    @Test
    public void testAddFriendNullUser() {
        assertFalse(relationships.addFriend(null));  // Null input should return false
    }

    // Tests for removeFriend method
    @Test
    public void testRemoveFriendSuccessfully() {
        relationships.addFriend(user2);
        assertTrue(relationships.removeFriend(user2));
        assertFalse(relationships.getFriends().contains(user2));
    }

    @Test
    public void testRemoveFriendNotInList() {
        assertFalse(relationships.removeFriend(user3));  // Removing a non-friend should return false
    }

    @Test
    public void testRemoveFriendNullUser() {
        assertFalse(relationships.removeFriend(null));  // Removing a null user should return false
    }

    // Tests for blockUser method
    @Test
    public void testBlockUserSuccessfully() {
        relationships.addFriend(user2);
        assertTrue(relationships.blockUser(user2));
        assertTrue(relationships.getBlocked().contains(user2));
        assertFalse(relationships.getFriends().contains(user2));  // Ensure they were removed from friends
    }

    @Test
    public void testBlockUserAlreadyBlocked() {
        relationships.blockUser(user3);
        assertFalse(relationships.blockUser(user3));  // Blocking an already blocked user should return false
    }

    @Test
    public void testBlockUserNullUser() {
        assertFalse(relationships.blockUser(null));  // Null input should return false
    }

    // Tests for unblockUser method
    @Test
    public void testUnblockUserSuccessfully() {
        relationships.blockUser(user2);
        assertTrue(relationships.unblockUser(user2));
        assertFalse(relationships.getBlocked().contains(user2));
    }

    @Test
    public void testUnblockUserNotBlocked() {
        assertFalse(relationships.unblockUser(user4));  // Unblocking a non-blocked user should return false
    }

    @Test
    public void testUnblockUserNullUser() {
        assertFalse(relationships.unblockUser(null));  // Null input should return false
    }

    // Tests for getFriends method
    @Test
    public void testGetFriendsAfterAdding() {
        relationships.addFriend(user2);
        relationships.addFriend(user3);

        ArrayList<User> friends = relationships.getFriends();
        assertTrue(friends.contains(user2));
        assertTrue(friends.contains(user3));
    }

    @Test
    public void testGetFriendsEmptyList() {
        ArrayList<User> friends = relationships.getFriends();
        assertTrue(friends.isEmpty());  // Initially, the friend list should be empty
    }

    @Test
    public void testGetFriendsAfterRemoval() {
        relationships.addFriend(user2);
        relationships.removeFriend(user2);

        ArrayList<User> friends = relationships.getFriends();
        assertFalse(friends.contains(user2));  // Ensure the friend was removed
    }

    // Tests for getBlocked method
    @Test
    public void testGetBlockedAfterBlocking() {
        relationships.blockUser(user2);
        relationships.blockUser(user3);

        ArrayList<User> blocked = relationships.getBlocked();
        assertTrue(blocked.contains(user2));
        assertTrue(blocked.contains(user3));
    }

    @Test
    public void testGetBlockedEmptyList() {
        ArrayList<User> blocked = relationships.getBlocked();
        assertTrue(blocked.isEmpty());  // Initially, the blocked list should be empty
    }

    @Test
    public void testGetBlockedAfterUnblocking() {
        relationships.blockUser(user3);
        relationships.unblockUser(user3);

        ArrayList<User> blocked = relationships.getBlocked();
        assertFalse(blocked.contains(user3));  // Ensure the user was unblocked
    }
}
