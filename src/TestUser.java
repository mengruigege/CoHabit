import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestUser {
    private User user1;
    private User user2;
    private User user3;
    private User user4;

    @Before
    public void setUp() throws UsernameTakenException {
        user1 = new User("Bob", "password123", "bob@gmail.com", "1234567890", "person", "purdue");
        user2 = new User("Joe", "password234", "joe@gmail.com", "2234567890", "person2", "purdue2");
        user3 = new User(null, "password345", "jim@gmail.com", "3334567890", "person3", "purdue3");
        user4 = new User("James", null, null, "4567890123", "person4", "purdue4");
    }

    @Test
    public void testConstructor1() {
        assertEquals("Bob", user1.getName());
        assertEquals("password123", user1.getPassword());
        assertEquals("bob@gmail.com", user1.getEmail());
        assertEquals("1234567890", user1.getPhoneNumber());
        assertEquals("person", user1.getDescription());
        assertEquals("purdue", user1.getUniversity());
    }

    @Test
    public void testConstructor2() {
        assertEquals("Joe", user2.getName());
        assertEquals("password234", user2.getPassword());
        assertEquals("joe@gmail.com", user2.getEmail());
        assertEquals("2234567890", user2.getPhoneNumber());
        assertEquals("person2", user2.getDescription());
        assertEquals("purdue2", user2.getUniversity());
    }

    @Test
    public void testConstructor3() {
        assertEquals(null, user3.getName());
        assertEquals("password345", user3.getPassword());
        assertEquals("jim@gmail.com", user3.getEmail());
        assertEquals("3334567890", user3.getPhoneNumber());
        assertEquals("person3", user3.getDescription());
        assertEquals("purdue3", user3.getUniversity());
    }

    @Test
    public void testConstructor4() {
        assertEquals("James", user4.getName());
        assertEquals(null, user4.getPassword());
        assertEquals(null, user4.getEmail());
        assertEquals("4567890123", user4.getPhoneNumber());
        assertEquals("person4", user4.getDescription());
        assertEquals("purdue4", user4.getUniversity());
    }

    @Test
    public void testSetNameUser1() {
        user1.setName("newname");
        assertEquals("newname", user1.getName());
    }

    @Test
    public void testSetNameUser2() {
        user2.setName("newname2");
        assertEquals("newname2", user2.getName());
    }

    @Test
    public void testSetNameUser3() {
        user3.setName("Jim");
        assertEquals("Jim", user3.getName());
    }

    @Test
    public void testSetNameUser4() {
        user4.setName("newname4");
        assertEquals("newname4", user4.getName());
    }

    @Test
    public void testSetPasswordUser1() {
        user1.setPassword("newpassword");
        assertEquals("newpassword", user1.getPassword());
    }

    @Test
    public void testSetPasswordUser2() {
        user2.setPassword(null);
        assertEquals(null, user2.getPassword());  // Assuming null password defaults to empty string
    }

    @Test
    public void testSetPasswordUser3() {
        user3.setPassword(null);
        assertEquals(null, user3.getPassword());  // Assuming null password defaults to empty string
    }

    @Test
    public void testSetPasswordUser4() {
        user4.setPassword("newpassword4");
        assertEquals("newpassword4", user4.getPassword());
    }

    @Test
    public void testSetEmailUser1() {
        user1.setEmail("newemail@gmail.com");
        assertEquals("newemail@gmail.com", user1.getEmail());
    }

    @Test
    public void testSetEmailUser2() {
        user2.setEmail("newemail2@gmail.com");
        assertEquals("newemail2@gmail.com", user2.getEmail());
    }

    @Test
    public void testSetEmailUser3() {
        user3.setEmail("newemail3@gmail.com");
        assertEquals("newemail3@gmail.com", user3.getEmail());
    }

    @Test
    public void testSetEmailUser4() {
        user4.setEmail("newemail4@gmail.com");
        assertEquals("newemail4@gmail.com", user4.getEmail());
    }

    @Test
    public void testSetPhoneNumberUser1() {
        user1.setPhoneNumber("1111111111");
        assertEquals("1111111111", user1.getPhoneNumber());
    }

    @Test
    public void testSetPhoneNumberUser2() {
        user2.setPhoneNumber("2222222222");
        assertEquals("2222222222", user2.getPhoneNumber());
    }

    @Test
    public void testSetPhoneNumberUser3() {
        user3.setPhoneNumber(null);
        assertEquals(null, user3.getPhoneNumber());  // Assuming null phone number defaults to empty string
    }

    @Test
    public void testSetPhoneNumberUser4() {
        user4.setPhoneNumber(null);
        assertEquals(null, user4.getPhoneNumber());  // Assuming null phone number defaults to empty string
    }

    @Test
    public void testSetPreference1() {
        user1.setPreferences("11 PM", false, false, true, 5, 5);
        assertEquals("11 PM, false, false, true, 5, 5", user1.getPreferences());
    }

    @Test
    public void testSetPreference2() {
        user2.setPreferences("12 AM", false, false, true, 3, 5);
        assertEquals("12 AM, false, false, true, 3, 5", user2.getPreferences());
    }

    @Test
    public void testSetPreference3() {
        user3.setPreferences("10 PM", true, true, false, 4, 0);
        assertEquals("10 PM, true, true, false, 4, 0", user3.getPreferences());
    }

    @Test
    public void testSetPreference4() {
        user4.setPreferences("11 PM", true, false, true, 2, 3);
        assertEquals("11 PM, true, false, true, 2, 3", user4.getPreferences());
    }

    @Test
    public void testToString() {
        String expected = "Bob,password123,bob@gmail.com,1234567890,person,purdue,11 PM,false,false,true,5,5";
        assertEquals(expected, user1.toString());
    }

    @Test
    public void testToString2() {
        String expected2 = "Joe,password234,joe@gmail.com,2234567890,person2,purdue2,12 AM, ,false,true, ,5";
        assertEquals(expected2, user2.toString());
    }

    @Test
    public void testToString3() {
        String expected3 = ",password345, jim@gmail.com, 3334567890, person3, purdue3 ,10 PM,true, ,false,4,";
        assertEquals(expected3, user3.toString());
    }

    @Test
    public void testToString4() {
        String expected4 = "James, , ,4567890123,person4,purdue4,11 PM,true,false, ,2, ";
        assertEquals(expected4, user4.toString());
    }
}
