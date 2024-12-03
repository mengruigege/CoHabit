import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

public class TestRelationships {

    private Relationships relationships;
    private User bob, jim, alice;

    @BeforeEach
    public void setup() throws UsernameTakenException, InvalidInput {
        // Initialize test users
        bob = new User("Bob", "password123", "bob@example.com", "123-456-7890", "Description", "UniversityA");
        jim = new User("Jim", "password234", "jim@example.com", "234-567-8901", "Description", "UniversityB");
        alice = new User("Alice", "password345", "alice@example.com", "345-678-9012", "Description", "UniversityC");

        // Set preferences (optional)
        bob.setPreferences("10:00 PM", true, false, true, 5, 10);
        jim.setPreferences("11:00 PM", false, true, false, 4, 15);
        alice.setPreferences("10:00 PM", true, false, true, 5, 10);

        // Initialize relationships for Bob
        relationships = new Relationships(bob, null);
    }

    // Test cases for `sendFriendRequest`
    @Test
    public void testSendFriendRequestValid() {
        assertTrue(relationships.sendFriendRequest(jim));
        assertTrue(relationships.getOutgoingRequests().contains(jim));
    }

    @Test
    public void testSendFriendRequestAlreadyFriend() {
        relationships.addFriend(jim);
        assertFalse(relationships.sendFriendRequest(jim));
    }

    @Test
    public void testSendFriendRequestDuplicateRequest() {
        relationships.sendFriendRequest(jim);
        assertFalse(relationships.sendFriendRequest(jim));
    }

    // Test cases for `receiveFriendRequest`
    @Test
    public void testReceiveFriendRequestValid() {
        relationships.receiveFriendRequest(jim);
        assertTrue(relationships.getIncomingRequests().contains(jim));
    }

    @Test
    public void testReceiveFriendRequestDuplicateRequest() {
        relationships.receiveFriendRequest(jim);
        relationships.receiveFriendRequest(jim); // Duplicate
        assertEquals(1, relationships.getIncomingRequests().size());
    }

    // Test cases for `acceptFriendRequest`
    @Test
    public void testAcceptFriendRequestValid() {
        relationships.receiveFriendRequest(jim);
        assertTrue(relationships.acceptFriendRequest(jim));
        assertTrue(relationships.getFriends().contains(jim));
        assertFalse(relationships.getIncomingRequests().contains(jim));
    }

    @Test
    public void testAcceptFriendRequestNoRequest() {
        assertFalse(relationships.acceptFriendRequest(jim)); // No request exists
    }

    @Test
    public void testAcceptFriendRequestDuplicateFriend() {
        relationships.addFriend(jim);
        assertFalse(relationships.acceptFriendRequest(jim)); // Already a friend
    }

    // Test cases for `declineFriendRequest`
    @Test
    public void testDeclineFriendRequestValid() {
        relationships.receiveFriendRequest(jim);
        assertTrue(relationships.declineFriendRequest(jim));
        assertFalse(relationships.getIncomingRequests().contains(jim));
    }

    @Test
    public void testDeclineFriendRequestNoRequest() {
        assertFalse(relationships.declineFriendRequest(jim)); // No request exists
    }

    // Test cases for `addFriend`
    @Test
    public void testAddFriendValid() {
        assertTrue(relationships.addFriend(jim));
        assertTrue(relationships.getFriends().contains(jim));
    }

    @Test
    public void testAddFriendDuplicate() {
        relationships.addFriend(jim);
        assertFalse(relationships.addFriend(jim)); // Already a friend
    }

    // Test cases for `removeFriend`
    @Test
    public void testRemoveFriendValid() {
        relationships.addFriend(jim);
        assertTrue(relationships.removeFriend(jim));
        assertFalse(relationships.getFriends().contains(jim));
    }

    @Test
    public void testRemoveFriendNotFriend() {
        assertFalse(relationships.removeFriend(jim)); // Not a friend
    }

    // Test cases for `blockUser`
    @Test
    public void testBlockUserValid() {
        relationships.blockUser(jim);
        assertTrue(relationships.getBlocked().contains(jim));
    }

    @Test
    public void testBlockUserAlreadyBlocked() {
        relationships.blockUser(jim);
        assertFalse(relationships.blockUser(jim)); // Already blocked
    }

    @Test
    public void testBlockUserUnfriend() {
        relationships.addFriend(jim);
        relationships.blockUser(jim);
        assertFalse(relationships.getFriends().contains(jim)); // Should unfriend
    }

    // Test cases for `unblockUser`
    @Test
    public void testUnblockUserValid() {
        relationships.blockUser(jim);
        assertTrue(relationships.unblockUser(jim));
        assertFalse(relationships.getBlocked().contains(jim));
    }

    @Test
    public void testUnblockUserNotBlocked() {
        assertFalse(relationships.unblockUser(jim)); // Not blocked
    }

    // Test cases for `hasPendingOutgoingRequest`
    @Test
    public void testHasPendingOutgoingRequestValid() {
        relationships.sendFriendRequest(jim);
        assertTrue(relationships.hasPendingOutgoingRequest(jim));
    }

    @Test
    public void testHasPendingOutgoingRequestNoRequest() {
        assertFalse(relationships.hasPendingOutgoingRequest(jim));
    }

    // Test cases for `getIncomingRequests`
    @Test
    public void testGetIncomingRequests() {
        relationships.receiveFriendRequest(jim);
        ArrayList<User> incoming = relationships.getIncomingRequests();
        assertEquals(1, incoming.size());
        assertTrue(incoming.contains(jim));
    }

    // Test cases for `getOutgoingRequests`
    @Test
    public void testGetOutgoingRequests() {
        relationships.sendFriendRequest(jim);
        ArrayList<User> outgoing = relationships.getOutgoingRequests();
        assertEquals(1, outgoing.size());
        assertTrue(outgoing.contains(jim));
    }

    // Test cases for `getFriendList`
    @Test
    public void testGetFriendList() {
        relationships.addFriend(jim);
        ArrayList<User> friends = relationships.getFriendList();
        assertEquals(1, friends.size());
        assertTrue(friends.contains(jim));
    }

    // Test cases for `getBlockedUsers`
    @Test
    public void testGetBlockedUsers() {
        relationships.blockUser(jim);
        ArrayList<User> blocked = relationships.getBlockedUsers();
        assertEquals(1, blocked.size());
        assertTrue(blocked.contains(jim));
    }
}
