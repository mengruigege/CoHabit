import static org.junit.Assert.assertEquals;

public class TestUser {
        private User user1 = new User("Bob", "password123", "bob@gmail.com", "1234567890", "person", "purdue");;
        private User user2 = new User("Joe", "password234", "joe@gmail.com", "2234567890", "person2", "purdue2");

        public void testConstructor() {
            assertEquals("Bob", user1.getName());
            assertEquals("password123", user1.getPassword());
            assertEquals("bob@gmail.com", user1.getEmail());
            assertEquals("1234567890", user1.getPhoneNum());
            assertEquals("person", user1.getDescription());
        }

        public void testGetandSet() {
            user1.setName("newname");
            user1.setPassword("newpassword");
            user1.setEmail("newemail@gmail.com");
            user1.setPhoneNumber("1111111111");
            user1.setDescription("newdescription");
            user1.setUniversity("newuni");

            assertEquals("newname", user1.getName());
            assertEquals("newpassword", user1.getPassword());
            assertEquals("newemail@gmail.com", user1.getEmail());
            assertEquals("1111111111", user1.getPhoneNum());
            assertEquals("Unewdescription", user1.getDescription());
        }

        public void testsetPreference() {
            user1.setPreferences("10 PM", true, false, true, 3, 5);
            assertEquals("10 PM, true, false, true, 3, 5", user1.getPreferences());
        }

        public void testtoString() {
            user1.setPreferences("10 PM", true, false, true, 3, 5);
            String expected = "Bob,password123,bob@gmail.com,1234567890,person,purdue,10 PM,true,false,true,3,5";
            assertEquals(expected, user1.toString());
        }
    }

