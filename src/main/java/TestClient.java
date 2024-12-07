import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestClient {
    private Client client;

    @Before
    public void setUp() {
        client = new Client("localhost", 1102);
    }

    @After
    public void tearDown() {
        client.disconnect();
    }

    @Test
    public void testStart() {
        client.start(); // Verifies no exception is thrown during execution.
    }

    @Test
    public void testClose() {
        client.close(); // Verify it closes without exception.
        assertFalse("Client should not be connected after closing.", 
                    client.isConnected);
    }

    @Test
    public void testMainScreen() {
        if (client.isConnected) {
            boolean result = client.mainScreen();
            assertFalse("Main screen should close and return false when closed.", 
                        result);
        } else {
            assertFalse("Main screen should not open when disconnected.", 
                        client.mainScreen());
        }
    }

    @Test
    public void testLogin() {
        if (client.isConnected) {
            boolean result = client.login();
            assertFalse("Login should fail with dummy inputs in GUI.", 
                        result);
        } else {
            assertFalse("Login should fail when disconnected.", 
                        client.login());
        }
    }

    @Test
    public void testRegister() {
        if (client.isConnected) {
            boolean result = client.register();
            assertFalse("Register should fail with dummy inputs in GUI.", 
                        result);
        } else {
            assertFalse("Register should fail when disconnected.", 
                        client.register());
        }
    }

    @Test
    public void testSendMessage() {
        boolean result = client.sendMessage();
        assertFalse("Sending a message should fail without a recipient and content.", 
                    result);
    }

    @Test
    public void testViewMessage() {
        client.viewMessage(); // Verify no exceptions are thrown.
    }

    @Test
    public void testViewFriendList() {
        client.viewFriendList(); // Verify no exceptions are thrown.
    }

    @Test
    public void testViewFriendRequests() {
        client.viewFriendRequests(); // Verify no exceptions are thrown.
    }

    @Test
    public void testSendFriendRequest() {
        client.sendFriendRequest(); // Verify no exceptions are thrown.
    }

    @Test
    public void testRemoveFriend() {
        client.removeFriend(); // Verify no exceptions are thrown.
    }

    @Test
    public void testViewBlockList() {
        client.viewBlockList(); // Verify no exceptions are thrown.
    }

    @Test
    public void testBlockUser() {
        client.blockUser(); // Verify no exceptions are thrown.
    }

    @Test
    public void testUnblockUser() {
        client.unblockUser(); // Verify no exceptions are thrown.
    }

    @Test
    public void testViewProfile() {
        client.viewProfile(); // Verify no exceptions are thrown.
    }

    @Test
    public void testUpdateProfile() {
        client.updateProfile(); // Verify no exceptions are thrown.
    }

    @Test
    public void testSearchRoommates() {
        client.searchRoommates(); // Verify no exceptions are thrown.
    }

    @Test
    public void testExactMatch() {
        client.exactMatch(); // Verify no exceptions are thrown.
    }

    @Test
    public void testPartialMatch() {
        client.partialMatch(); // Verify no exceptions are thrown.
    }

    @Test
    public void testDisconnect() {
        client.disconnect();
        assertFalse("Client should not be connected after disconnecting.", 
                    client.isConnected);
    }

    @Test
    public void testSetProfilePicture() {
        boolean result = client.setProfilePicture("invalid_path.png");
        assertFalse("Setting profile picture should fail for an invalid file path.", 
                    result);
    }
}
