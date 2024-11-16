import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class TestServer {
    Server server = new Server();
    @Test
    public void testLogin() {
        User user1 = new User("Bob", "password123");
        Server.addUser(user1);
        assertTrue(Server.login("Bob", "password123"));
        assertFalse(Server.login("Bob", "password234"));
        assertFalse(Server.login("Jim", "password123"));
        assertFalse(Server.login("Jim","password234"));
    }
    @Test
    public void testRegister(){
        User user1 = new User("Bob", "password123");
        User user2 = new User("Jim","password234");
        Server.addUser(user1);
        assertTrue(server.register(user1));
        assertFalse(server.register(user2));
    }
    @Test
    public void testSendMessage(){
        User user1 = new User("Bob","password123");
        User user2 = new User("Jim","password234");
        Server.addUser(user1);
        Server.addUser(user2);
        assertTrue(server.sendMessage(user1,user2,"Hello"));
    }
    @Test
    public void testAddFriend(){
        User user1 = new User("Bob","password123");
        User user2 = new User("Jim","password234");
        Server.addUser(user1);
        Server.addUser(user2);
        assertTrue(server.addFriend(user1,user2));
    }
    @Test
    public void testViewProfileExistingUser() {
        User user1 = new User("Bob","password123");
        Server.addUser(user1);
        assertEquals(user1.toString(),Server.viewProfile("Bob"));
    }
    @Test
    public void testMain() throws IOException {
        Socket socket = new Socket("localhost", 1102);
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        writer.println("login,testUser,password123");
        String response = in.readLine();
        assertEquals("Sucessful login", response);

        writer.close();
        in.close();
        socket.close();
    }
}
