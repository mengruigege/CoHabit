import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;

public class TestFriendList {
    private User user1;
    private User user2;
    private User user3;
    private User user4;
    private FriendList friendList;
    private Database database;

    @Before
    public void setUp() throws UsernameTakenException {
        database = new Database(); // Initialize with mock or empty database
        user1 = new User("Bob", "password123", "bob@gmail.com", "1234567890", "person", "purdue");
        user2 = new User("Jim", "password234", "jim@gmail.com", "2345678901", "person2", "purdue2");
        user3 = new User("Alice", "password345", "alice@gmail.com", "3456789012", "person3", "purdue3");
        user4 = new User("David", "password456", "david@gmail.com", "4567890123", "person4", "purdue4");
        friendList = new FriendList(user1, database);
    }

    // Tests for addFriend method
    @Test
    public void testAddFriendSuccessfully() {
        assertTrue(friendList.addFriend(user2));
        assertTrue(friendList.getFriends().contains(user2));
    }

    @Test
    public void testAddFriendAlreadyInList() {
        friendList.addFriend(user2);
        assertFalse(friendList.addFriend(user2));  // Duplicate addition should fail
    }

    @Test
    public void testAddFriendNullUser() {
        assertFalse(friendList.addFriend(null));  // Null input should return false
    }

    // Tests for removeFriend method
    @Test
    public void testRemoveFriendSuccessfully() {
        friendList.addFriend(user2);
        assertTrue(friendList.removeFriend(user2));
        assertFalse(friendList.getFriends().contains(user2));
    }

    @Test
    public void testRemoveFriendNotInList() {
        assertFalse(friendList.removeFriend(user3));  // Removing a non-friend should return false
    }

    @Test
    public void testRemoveFriendNullUser() {
        assertFalse(friendList.removeFriend(null));  // Removing a null user should return false
    }

    // Tests for blockUser method
    @Test
    public void testBlockUserSuccessfully() {
        friendList.addFriend(user2);
        assertTrue(friendList.blockUser(user2));
        assertTrue(friendList.getBlocked().contains(user2));
        assertFalse(friendList.getFriends().contains(user2));  // Ensure they were removed from friends
    }

    @Test
    public void testBlockUserAlreadyBlocked() {
        friendList.blockUser(user3);
        assertFalse(friendList.blockUser(user3));  // Blocking an already blocked user should return false
    }

    @Test
    public void testBlockUserNullUser() {
        assertFalse(friendList.blockUser(null));  // Null input should return false
    }

    // Tests for unblockUser method
    @Test
    public void testUnblockUserSuccessfully() {
        friendList.blockUser(user2);
        assertTrue(friendList.unblockUser(user2));
        assertFalse(friendList.getBlocked().contains(user2));
    }

    @Test
    public void testUnblockUserNotBlocked() {
        assertFalse(friendList.unblockUser(user4));  // Unblocking a non-blocked user should return false
    }

    @Test
    public void testUnblockUserNullUser() {
        assertFalse(friendList.unblockUser(null));  // Null input should return false
    }

    // Tests for getFriends method
    @Test
    public void testGetFriendsAfterAdding() {
        friendList.addFriend(user2);
        friendList.addFriend(user3);

        ArrayList<User> friends = friendList.getFriends();
        assertTrue(friends.contains(user2));
        assertTrue(friends.contains(user3));
    }

    @Test
    public void testGetFriendsEmptyList() {
        ArrayList<User> friends = friendList.getFriends();
        assertTrue(friends.isEmpty());  // Initially, the friend list should be empty
    }

    @Test
    public void testGetFriendsAfterRemoval() {
        friendList.addFriend(user2);
        friendList.removeFriend(user2);

        ArrayList<User> friends = friendList.getFriends();
        assertFalse(friends.contains(user2));  // Ensure the friend was removed
    }

    // Tests for getBlocked method
    @Test
    public void testGetBlockedAfterBlocking() {
        friendList.blockUser(user2);
        friendList.blockUser(user3);

        ArrayList<User> blocked = friendList.getBlocked();
        assertTrue(blocked.contains(user2));
        assertTrue(blocked.contains(user3));
    }

    @Test
    public void testGetBlockedEmptyList() {
        ArrayList<User> blocked = friendList.getBlocked();
        assertTrue(blocked.isEmpty());  // Initially, the blocked list should be empty
    }

    @Test
    public void testGetBlockedAfterUnblocking() {
        friendList.blockUser(user3);
        friendList.unblockUser(user3);

        ArrayList<User> blocked = friendList.getBlocked();
        assertFalse(blocked.contains(user3));  // Ensure the user was unblocked
    }
}
