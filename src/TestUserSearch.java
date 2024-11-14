import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;

public class TestUserSearch {
    private static UserSearch userSearch = new UserSearch();
    private static User user1;
    private static User user2;

    static {
        try {
            user1 = new User("Bob", "password123");
            user2 = new User("Jim", "password234");
            user1.setPreferences("11 PM", false, false, true, 5, 5);
            user2.setPreferences("12 AM", false, false, true, 3, 5);
        } catch (UsernameTakenException e) {
            throw new RuntimeException(e);
        } catch (InvalidInput e) {
            throw new RuntimeException(e);
        }
    }

    static {
        Database users = new Database();
        users.addUser(user1);
        users.addUser(user2);
    }

    @Test
    public void testsearchParamter() {
        ArrayList<User> search = userSearch.searchByParameter("name", "Bob");
        assertTrue(search.contains(user1));
        search = userSearch.searchByParameter("email", "bob@gmail.com");
        assertTrue(search.contains(user1));
        search = userSearch.searchByParameter("phone", "1234567890");
        assertTrue(search.contains(user1));
        search = userSearch.searchByParameter("university", "purdue");
        assertTrue(search.contains(user1));
    }

    @Test
    public void testExactMatch() {
        ArrayList<User> exact = userSearch.exactMatch(user1);
        assertTrue(exact.contains(user1));
    }

    @Test
    public void testPartialMatch() {
        ArrayList<User> partial = userSearch.partialMatch(user1);
        // Assuming partial matches are based on a score, and we only add if score > 0
        assertTrue(partial.contains(user2));
    }

}
