import org.junit.Test;
import java.io.*;
import java.net.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestServer {
    Server server = new Server();
    public void testLogin() {
        User user1 = new User("Bob", "password123");
        Database.addUser(user1);
        assertTrue(Server.login("Bob", "password123"));
        assertFalse(Server.login("Bob", "password234"));
        assertFalse(Server.login("Jim", "password123"));
        assertFalse(Server.login("Jim","password234"));
    }
    public void testRegister(){
        User user1 = new User("Bob", "password123");
        Database.addUser(user1);
        assertTrue(server.register(user1));
    }
    public void testSendMessage(){
        User user1 = new User("Bob","password123");
        User user2 = new User("Jim","password234");
        Database.addUser(user1);
        Database.addUser(user2);
        assertTrue(server.sendMessage(user1,user2,"Hello"));
    }
    public void testAddFriend(){
        User user1 = new User("Bob","password123");
        User user2 = new User("Jim","password234");
        Database.addUser(user1);
        Database.addUser(user2);
        assertTrue(server.addFriend(user1,user2));
    }
    public void testViewProfileExistingUser() {
        User user1 = new User("Bob","password123");
        database.addUser(user);
        assertEquals(user.toString(),Server.viewProfile("Bob"));
    }

    public void testMain() throws IOException {
        Socket socket = new Socket("localhost", 1102);
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        writer.println("login,testUser,password123");
        String response = in.readLine();
        assertEquals("Sucessful login", response);

        in.close();
        writer.close();
        socket.close();
    }
}
