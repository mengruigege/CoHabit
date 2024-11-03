import static org.junit.Assert.assertEquals;

public class TestUser {
        private User user1 = new User("Bob", "password123", "bob@gmail.com", "1234567890", "person", "purdue");;
        private User user2 = new User("Joe", "password234", "joe@gmail.com", "2234567890", "person2", "purdue2");
        private User user3 = new User(null, "password345", "jim@gmail.com", "3334567890", "person3", "purdue3");
        private User user4 = new User("James", null, null, "4567890123", "person4", "purdue4");

        public void testConstructor() {
            assertEquals("Bob", user1.getName());
            assertEquals("password123", user1.getPassword());
            assertEquals("bob@gmail.com", user1.getEmail());
<<<<<<< HEAD:src/TestCases.java
            assertEquals("1234567890", user1.getPhoneNum());
            assertEquals("person", user1.getDescription());
            assertEquals("purdue",user1.getUniversity());

            assertEquals("Joe", user2.getName());
            assertEquals("password234", user2.getPassword());
            assertEquals("joe@gmail.com", user2.getEmail());
            assertEquals("2234567890", user2.getPhoneNum());
            assertEquals("person2", user2.getDescription());
            assertEquals("purdue2", user2.getUniversity());

            assertEquals("", user3.getName());
            assertEquals("password345", user3.getPassword());
            assertEquals("jim@gmail.com", user3.getEmail());
            assertEquals("3334567890", user3.getPhoneNum());
            assertEquals("person3", user3.getDescription());
            assertEquals("purdue3", user3.getUniversity());

            assertEquals("James", user4.getName());
            assertEquals("", user4.getPassword());
            assertEquals("", user4.getEmail());
            assertEquals("4567890123", user4.getPhoneNum());
            assertEquals("person4", user4.getDescription());
            assertEquals("purdue4", user4.getUniversity());
        }

        public void testSet() {
=======
            assertEquals("1234567890", user1.getPhoneNumber());
            assertEquals("person", user1.getDescription());
        }

        public void testGetAndSet() {
>>>>>>> e340144a428dcbe6d3b3ceab45efc027262b24c2:src/TestUser.java
            user1.setName("newname");
            user1.setPassword("newpassword");
            user1.setEmail("newemail@gmail.com");
            user1.setPhoneNumber("1111111111");
            user1.setDescription("newdescription");
            user1.setUniversity("newuni");

            assertEquals("newname", user1.getName());
            assertEquals("newpassword", user1.getPassword());
            assertEquals("newemail@gmail.com", user1.getEmail());
<<<<<<< HEAD:src/TestCases.java
            assertEquals("1111111111", user1.getPhoneNum());
            assertEquals("Unewdescription", user1.getDescription());
            assertEquals("newuni", user1.getUniversity());

            user2.setName("newname2");
            user2.setPassword(null);
            user2.setEmail("newemail2@gmail.com");
            user2.setPhoneNumber("2222222222");
            user2.setDescription("newdescription2");
            user2.setUniversity("newuni2");

            assertEquals("newname2", user2.getName());
            assertEquals("", user2.getPassword());
            assertEquals("newemail2@gmail.com", user2.getEmail());
            assertEquals("2222222222", user2.getPhoneNum());
            assertEquals("Unewdescription2", user2.getDescription());
            asserEquals("newuni2", user2.getUniversity());

            user3.setName("Jim");
            user3.setPassword(null);
            user3.setEmail("newemail3@gmail.com");
            user3.setPhoneNumber(null);
            user3.setDescription("newdescription3");
            user3.setUniversity("newuni3");

            assertEquals("Jim", user3.getName());
            assertEquals("", user3.getPassword());
            assertEquals("newemail3@gmail.com", user3.getEmail());
            assertEquals("", user3.getPhoneNum());
            assertEquals("Unewdescription3", user3.getDescription());
            assertEquals("newuni3",user3.getUniversity());

            user4.setName("nename4");
            user4.setPassword("newpassword4");
            user4.setEmail("newemail4@gmail.com");
            user4.setPhoneNumber(null);
            user4.setDescription(null);
            user4.setUniversity("newuni4");

            assertEquals("newname4", user4.getName());
            assertEquals("newpassword4", user4.getPassword());
            assertEquals("newemail4@gmail.com", user4.getEmail());
            assertEquals("", user4.getPhoneNum());
            assertEquals("", user4.getDescription());
            assertEquals("newuni4",user4.getUniversity());
        }

        public void testsetPreference() {
            user1.setPreferences("11 PM", false, false, true, 5, 5);
            assertEquals("11 PM, true, false, true, 3, 5", user1.getPreferences());
            user2.setPreferences("12 AM", null, false, true, null, 5);
            assertEquals("12 AM, "", false, true, "", 5", user2.getPreferences());
            user3.setPreferences("10 PM", true, null, false, 4, null);
            assertEquals("10 PM, true, "", false, 4, """, user3.getPreferences());
            user4.setPreferences("11 PM", true, false, null, 2, null);
            assertEquals("11 PM, true, false, "", 2, """, user4.getPreferences());
        }

        public void testtoString() {
            String expected = "Bob,password123,bob@gmail.com,1234567890,person,purdue,11 PM,false,false,true,5,5";
=======
            assertEquals("1111111111", user1.getPhoneNumber());
            assertEquals("Unewdescription", user1.getDescription());
        }

        public void testSetPreference() {
            user1.setPreferences("10 PM", true, false, true, 3, 5);
            assertEquals("10 PM, true, false, true, 3, 5", user1.getPreferences());
        }

        public void testToString() {
            user1.setPreferences("10 PM", true, false, true, 3, 5);
            String expected = "Bob,password123,bob@gmail.com,1234567890,person,purdue,10 PM,true,false,true,3,5";
>>>>>>> e340144a428dcbe6d3b3ceab45efc027262b24c2:src/TestUser.java
            assertEquals(expected, user1.toString());
            String expected2 = "Joe,password234,joe@gmail.com,2234567890,person2,purdue2,12 AM,"",false,true,"",5";
            assertEquals(expected2, user2.toString());
            String expected3 = """,password345, jim@gmail.com, 3334567890, person3, purdue3 ,10 PM,true,"",false,4,""";
            assertEquals(expected3,user3.toString());
            String expected4 = "James, "", "",4567890123,person4,purdue4,11 PM,true,false,"",2,""";
            assertEquals(expected4, user4.toString());
        }
    }

