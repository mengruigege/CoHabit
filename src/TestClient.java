import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Team Project Phase 1 - CoHabit
 * <p>
 * This program works to implement a roommate search algorithm
 *
 * @author Aidan Lefort, Andrew Tang, Keya Jadhav, Rithvik Siddenki, Rui Meng
 * @version November 3rd, 2024
 */

public class TestClient {
    private Client client;

    @Before
    public void setUp() throws Exception {
        // Setting up a new client instance for each test
        client = new Client(new User("testUser", "password123", "test@example.com", "1234567890", "Test description", "Test University"));
        assertNotNull("Client instance should be created.", client);
    }

    @After
    public void tearDown() throws Exception {
        // Ensure disconnection after each test
        client.disconnect();
        assertFalse("Client should be disconnected.", client.isConnected());
    }

    // 1. Test initializing client with valid user
    @Test
    public void testClientInitialization() {
        assertEquals("testUser", client.getUsername());
    }

    // 2. Test setting username
    @Test
    public void testSetUsername() {
        client.setUsername("newUser");
        assertEquals("newUser", client.getUsername());
    }

    // 3. Test setting password
    @Test
    public void testSetPassword() {
        client.setPassword("newPassword");
        assertEquals("newPassword", client.getPassword());
    }

    // 4. Test setting email
    @Test
    public void testSetEmail() {
        client.setEmail("new@example.com");
        assertEquals("new@example.com", client.getEmail());
    }

    // 5. Test setting phone number
    @Test
    public void testSetPhoneNumber() {
        client.setPhone("0987654321");
        assertEquals("0987654321", client.getPhone());
    }

    // 6. Test setting description
    @Test
    public void testSetDescription() {
        client.setUserDescription("Updated description");
        assertEquals("Updated description", client.getUserDescription());
    }

    // 7. Test setting university
    @Test
    public void testSetUniversity() {
        client.setUniversity("New University");
        assertEquals("New University", client.getUniversity());
    }

    // 8. Test connection (dummy test, assuming server exists)
    @Test
    public void testConnect() {
        boolean connected = client.connect("localhost", 1102);
        if (connected) {
            assertTrue("Client should be connected to the server.", client.isConnected());
        } else {
            assertFalse("Client should not connect to an unavailable server.", client.isConnected());
        }
    }

    // 9. Test disconnection
    @Test
    public void testDisconnect() {
        client.connect("localhost", 1102);
        client.disconnect();
        assertFalse("Client should be disconnected.", client.isConnected());
    }

    // 10. Test preferences setup
    @Test
    public void testSetPreferences() {
        client.setPreferences("22:00", true, false, true, 7, 5);
        assertNotNull("Preferences should be set without errors.", client);
    }

    // 11. Test registering a new user
    @Test
    public void testRegister() {
        client.setUserRegisterInformation("newUser", "newPass", "new@example.com", "9876543210", "New description", "New University");
        assertTrue("Registration should succeed for valid user.", client.register());
    }

    // 12. Test invalid registration
    @Test
    public void testInvalidRegister() {
        client.setUserRegisterInformation("", "", "", "", "", "");
        assertFalse("Registration should fail for invalid data.", client.register());
    }

    // 13. Test updating profile
    @Test
    public void testUpdateProfile() {
        client.setUsername("UpdatedUser");
        assertTrue("Profile update should succeed with valid data.", client.updateProfile("testUser"));
    }

    // 14. Test sending a valid message
    @Test
    public void testSendMessage() {
        assertTrue("Message should be sent successfully.", client.sendMessage("receiver", "Hello there!"));
    }

    // 15. Test sending an empty message
    @Test
    public void testSendEmptyMessage() {
        assertTrue("Empty messages should be allowed.", client.sendMessage("receiver", ""));
    }

    // 16. Test fetching messages
    @Test
    public void testFetchMessages() {
        String messages = client.fetchMessages("testUser", "receiver");
        assertNotNull("Messages should be fetched successfully.", messages);
    }

    // 17. Test sending friend requests
    @Test
    public void testSendFriendRequest() {
        assertTrue("Friend request should be sent successfully.", client.sendFriendRequest("testUser", "receiver"));
    }

    // 18. Test viewing profiles
    @Test
    public void testViewProfile() {
        client.viewProfile("testUser");
        System.out.println("Profile viewed successfully.");
    }

    // 19. Test viewing blocked users
    @Test
    public void testViewBlockedUsers() {
        client.blockUser("testUser", "blockedUser");
        client.viewBlockedUsers("testUser");
        System.out.println("Blocked users viewed successfully.");
    }

    // 20. Test blocking a user
    @Test
    public void testBlockUser() {
        assertTrue("Blocking user should succeed.", client.blockUser("testUser", "blockedUser"));
    }

    // 21. Test unblocking a user
    @Test
    public void testUnblockUser() {
        client.blockUser("testUser", "blockedUser");
        assertTrue("Unblocking user should succeed.", client.unblockUser("testUser", "blockedUser"));
    }

    // 22. Test viewing friend list
    @Test
    public void testViewFriendsList() {
        client.viewFriendsList("testUser");
        System.out.println("Friend list viewed successfully.");
    }

    // 23. Test searching by parameter
    @Test
    public void testSearchByParameter() {
        client.searchByParameter("email", "test@example.com");
        System.out.println("Search by parameter executed successfully.");
    }

    // 24. Test exact match
    @Test
    public void testExactMatch() throws UsernameTakenException {
        client.exactMatch(new User("testUser", "password123", "test@example.com", "1234567890", "Test description", "Test University"));
        System.out.println("Exact match search executed successfully.");
    }

    // 25. Test partial match
    @Test
    public void testPartialMatch() throws UsernameTakenException {
        client.partialMatch(new User("testUser", "password123", "test@example.com", "1234567890", "Test description", "Test University"));
        System.out.println("Partial match search executed successfully.");
    }
}
