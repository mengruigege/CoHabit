import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class TestDatabase {
    private Database database;
    User user1 = new User("Bob", "password123", "bob@gmail.com", "1234567890", "person", "purdue");;
    User user2 = new User("Jim", "password234", "jim@gmail.com", "2345678901", "person2", "purdue2");

    public TestDatabase() throws UsernameTakenException {
    }
    @Test
    public void testAddUser() {
        boolean result = database.addUser(user1);
        assertTrue(result);
        ArrayList<User> users = database.getAllUsers();
        assertEquals("Bob", users.get(0).getName());
    }
    @Test
    public void testDeleteUser() {
        database.addUser(user1);
        boolean result = database.deleteUser(user1);
        assertTrue(result);
        ArrayList<User> users = database.getAllUsers();
        assertFalse(users.contains(user1));
    }
    @Test
    public void testAddFriend() {
        database.addUser(user1);
        database.addUser(user2);
        boolean result = database.addFriend(user1, user2);
        assertTrue(result);
    }
    @Test
    public void testUsernameExists() {
        database.addUser(user1);
        assertTrue(database.usernameExists("Bob"));
    }
    @Test
    public void testFindUserByName() {
        database.addUser(user1);
        database.addUser(user2);
        User find = database.findUserByName("Bob");
        assertEquals("Bob", find.getName());
    }
}
