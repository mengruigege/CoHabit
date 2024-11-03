import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;

public class TestUserSearch {
    private static UserSearch userSearch = new UserSearch();
    private static User user1;
    private static User user2;

    static {
        try {
            user1 = new User("Bob", "password123", "bob@gmail.com", "1234567890", "person", "purdue");
            user2 = new User("Jim", "password234", "jim@gmail.com", "2345678901", "person2", "purdue2");
        } catch (UsernameTakenException e) {
            throw new RuntimeException(e);
        }
    }
    static {
        Database users = new Database();
        users.addUser(user1);
        users.addUser(user2);
    }
    @Test
    public void testsearchParamter(){
        ArrayList<User> search = userSearch.searchByParameter("name","Bob");
        assertEquals("Bob",search.get(0).getName());
        search = userSearch.searchByParameter("password", "password123");
        assertEquals("password123",search.get(0).getPassword());
        search = userSearch.searchByParameter("email","bob@gmail.com");
        assertEquals("bob@gmail.com",search.get(0).getEmail());
        search = userSearch.searchByParameter("phone","1234567890");
        assertEquals("1234567890",search.get(0).getPhoneNumber());
        search = userSearch.searchByParameter("desc","person");
        assertEquals("person",search.get(0).getDescription());
        search = userSearch.searchByParameter("uni","purdue");
        assertEquals("purdue",search.get(0).getUniversity());
    }
    @Test
    public void testExactMatch() {
        ArrayList<User> exact = userSearch.exactMatch(user1);
        assertEquals("Bob", exact.get(0).getName());
    }
    @Test
    public void testPartialMatch() {
        ArrayList<User> partial = userSearch.partialMatch(user1);
        // Assuming partial matches are based on a score, and we only add if score > 0
        assertTrue(partial.contains(user2));
    }

}
