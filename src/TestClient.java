import org.junit.Test;
import static org.junit.Assert.*;

public class TestClient {
    @Test
    public void testClient() throws UsernameTakenException {
        Client client1 = new Client(new User("Bob", "password123"));
        assertTrue(client1.connect("localhost", 1102));
        client1.disconnect();
    }
    @Test
    public void testLogin() throws UsernameTakenException {
        Client client1 = new Client(new User("Bob", "password123"));
        client1.connect("localhost", 1102);
        assertTrue(client1.login("Bob", "password123"));
        client1.disconnect();
    }
    @Test
    public void testRegister() throws UsernameTakenException {
        Client client1 = new Client(new User("Bob", "password123"));
        client1.connect("localhost", 1102);
        assertTrue(client1.register(new User("Bob", "password123")));
        client1.disconnect();
    }
    @Test
    public void testSendMessage() throws UsernameTakenException {
        Client client1 = new Client(new User("Bob", "password123"));
        client1.connect("localhost", 1102);
        assertTrue(client1.sendMessage("Bob", "Hello"));
        client1.disconnect();
    }
    @Test
    public void testSendFriendRequest() throws UsernameTakenException {
        Client client1 = new Client(new User("Bob", "password123"));
        client1.connect("localhost", 1102);
        assertTrue(client1.sendFriendRequest("Bob", "friend"));
        client1.disconnect();
    }
    @Test
    public void testAddFriend() throws UsernameTakenException {
        Client client1 = new Client(new User("Bob", "password123"));
        client1.connect("localhost", 1102);
        assertTrue(client1.addFriend("Bob", "friend"));
        client1.disconnect();
    }
    @Test
    public void testRemoveFriend() throws UsernameTakenException {
        Client client1 = new Client(new User("Bob", "password123"));
        client1.connect("localhost", 1102);
        assertTrue(client1.removeFriend("Bob", "friend"));
        client1.disconnect();
    }
    @Test
    public void testBlockUser() throws UsernameTakenException {
        Client client1 = new Client(new User("Bob", "password123"));
        client1.connect("localhost", 1102);
        assertTrue(client1.blockUser("Bob", "friend"));
        client1.disconnect();
    }
    @Test
    public void testViewProfile() throws UsernameTakenException {
        Client client1 = new Client(new User("Bob", "password123"));
        client1.connect("localhost", 1102);
        client1.viewProfile("Bob");
        client1.disconnect();
    }
    @Test
    public void testDisconnect() throws UsernameTakenException {
        Client client1 = new Client(new User("Bob", "password123"));
        client1.connect("localhost", 1102);
        client1.disconnect();
        assertFalse(client1.isConnected());
    }
}
