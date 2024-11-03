import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestDatabase {
    private Database database;
    User user1 = new User("Bob", "password123", "bob@gmail.com", "1234567890", "person", "purdue");
    User user2 = new User("Joe", "password234", "joe@gmail.com", "2345678901", "person2", "purdue2");
    public void testAddUser() {
        boolean result = database.addUser(user1);
        assertTrue(result);

        ArrayList<User> users = database.getAllUsers();
        assertEquals(1, users.size());
        assertEquals("Bob", users.get(0).getName());
    }
}
