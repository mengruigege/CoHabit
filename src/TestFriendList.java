import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;

public class TestFriendList {
    private User user1 = new User("Bob", "password123", "bob@gmail.com", "1234567890", "person", "purdue");
    private User user2 = new User("Jim", "password234", "jim@gmail.com", "2345678901", "person2", "purdue2");

    public TestFriendList() throws UsernameTakenException {
    }

    public void testConstructor() {
        FriendList friendList = new FriendList(user1);
        ArrayList<User> friendsList = friendList.getFriends();
        ArrayList<User> blocked = friendList.getBlocked();
        assertEquals(1, friendsList.size());
        assertEquals(1, blocked.size());
    }
    public void testAddFriend() {
        FriendList friendList = new FriendList(user1);
        boolean result = friendList.addFriend(user2);
        assertTrue(result);

    }
    public void testRemoveFriend() {
        FriendList friendList = new FriendList(user1);
        friendList.addFriend(user2);
        boolean result = friendList.removeFriend(user2);
        assertTrue(result);
        ArrayList<User> friends = friendList.getFriends();
        assertFalse(friends.contains(user2));
    }
    public void testAllowUser() {
        FriendList friendList = new FriendList(user1);
        friendList.addFriend(user2);
        boolean result = friendList.unblockUser(user2);
        assertTrue(result);
        ArrayList<User> restricted = friendList.getBlocked();
        assertFalse(restricted.contains(user2));
    }
    public void testallowUser(){
        FriendList friendList = new FriendList(user1);
        friendList.addFriend(user2);
        boolean result = friendList.unblockUser(user2);
        assertTrue(result);
    }
    public void testBlockUser() {
        FriendList friendList = new FriendList(user1);
        friendList.addFriend(user2);
        boolean result = friendList.blockUser(user2);
        assertTrue(result);
        ArrayList<User> blocked = friendList.getBlocked();
        assertTrue(blocked.contains(user2));
    }
    public void testUnblockUser() {
        FriendList friendList = new FriendList(user1);
        friendList.blockUser(user2);
        boolean result = friendList.unblockUser(user2);
        assertTrue(result);
        ArrayList<User> blocked = friendList.getBlocked();
        assertFalse(blocked.contains(user2));
    }
}
