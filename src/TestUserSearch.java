import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;

public class TestUserSearch {
    private UserSearch userSearch = new UserSearch();
    private User user1 = new User("Bob", "password123", "bob@gmail.com", "1234567890", "person", "purdue");
    private User user2 = new User("Jim", "password234", "jim@gmail.com", "2345678901", "person2", "purdue2");

    static {
        User.allUsers.add(user1);
        User.allUsers.add(user2);
    }
    public void testsearchParamter(){
        ArrayList<User> search = userSearch.searchParameter("name","Bob");
        assertEquals("Bob",search.get(0).getName());
        ArrayList<User> search = userSearch.searchParameter("password","password123");
        assertEquals("password123",search.get(0).getPassword());
        ArrayList<User> search = userSearch.searchParameter("email","bob@gmail.com");
        assertEquals("bob@gmail.com",search.get(0).getEmail());
        ArrayList<User> search = userSearch.searchParameter("phone","1234567890");
        assertEquals("1234567890",search.get(0).getPhoneNum());
        ArrayList<User> search = userSearch.searchParameter("desc","person");
        assertEquals("person",search.get(0).getDescription());
        ArrayList<User> search = userSearch.searchParameter("uni","purdue");
        assertEquals("purdue",search.get(0).getUniversity());
    }
    public void testExactMatch() {
        ArrayList<User> exact = userSearch.exactMatch(user1);
        assertEquals("Bob", exact.get(0).getName());
    }
    public void testPartialMatch() {
        ArrayList<User> partial = userSearch.partialMatch(user1);
        // Assuming partial matches are based on a score, and we only add if score > 0
        assertTrue(partial.contains(user2));
    }

}
