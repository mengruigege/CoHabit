import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Team Project Phase 2 - CoHabit
 * <p>
 * This program works to implement a roommate search algorithm
 *
 * @author Aidan Lefort, Andrew Tang, Keya Jadhav, Rithvik Siddenki, Rui Meng
 * @version November 3rd, 2024
 */

public class TestUser {
    private User user1;
    private User user2;
    private User user3;
    private User user4;

    @Before
    public void setUp() throws UsernameTakenException {
        user1 = new User("Bob", "password123", "bob@gmail.com", 
                         "1234567890", "Test user Bob", "University A");
        user2 = new User("Jim", "password234", "jim@gmail.com", 
                         "2345678901", "Test user Jim", "University B");
        user3 = new User(null, "password345", "alice@gmail.com", 
                         "3456789012", "Test User 3", "University C");
        user4 = new User("David", null, "david@gmail.com", 
                         "4567890123", "Test User 4", "Univercity D");
    }

    @Test
    public void testConstructor1() {
        assertEquals("Bob", user1.getName());
        assertEquals("password123", user1.getPassword());
        assertEquals("bob@gmail.com", user1.getEmail());
        assertEquals("1234567890", user1.getPhoneNumber());
        assertEquals("Test user Bob", user1.getDescription());
        assertEquals("University A", user1.getUniversity());
    }

    @Test
    public void testConstructor2() {
        assertEquals("Jim", user2.getName());
        assertEquals("password234", user2.getPassword());
        assertEquals("jim@gmail.com", user2.getEmail());
        assertEquals("2345678901", user2.getPhoneNumber());
        assertEquals("Test user Jim", user2.getDescription());
        assertEquals("University B", user2.getUniversity());
    }

    @Test
    public void testConstructor3() {
        assertEquals(null, user3.getName());
        assertEquals("password345", user3.getPassword());
        assertEquals("alice@gmail.com", user3.getEmail());
        assertEquals("3456789012", user3.getPhoneNumber());
        assertEquals("Test User 3", user3.getDescription());
        assertEquals("University C", user3.getUniversity());
    }

    @Test
    public void testConstructor4() {
        assertEquals("David", user4.getName());
        assertEquals(null, user4.getPassword());
        assertEquals("david@gmail.com", user4.getEmail());
        assertEquals("4567890123", user4.getPhoneNumber());
        assertEquals("Test User 4", user4.getDescription());
        assertEquals("Univercity D", user4.getUniversity());
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

    // Test cases for setProfilePicture and getProfilePicture methods
    @Test
    public void testSetAndGetProfilePicture() {
        byte[] samplePicture = {1, 2, 3};  // Simple mock data for profile picture
        user1.setProfilePicture(samplePicture);
        assertArrayEquals("Profile picture should match the data set", 
                          samplePicture, user1.getProfilePicture());
    }

    @Test
    public void testSetProfilePictureWithNull() {
        user2.setProfilePicture(null);  // Set null as profile picture
        assertNull("Profile picture should be null when set to null", 
                   user2.getProfilePicture());
    }

    @Test
    public void testSetProfilePictureWithEmptyArray() {
        byte[] emptyPicture = {};  // Empty byte array
        user3.setProfilePicture(emptyPicture);
        assertArrayEquals("Profile picture should be an empty array when set with an empty array", 
                          emptyPicture, user3.getProfilePicture());
    }

    @Test
    public void testOverwriteProfilePicture() {
        byte[] initialPicture = {4, 5, 6};  // Initial profile picture data
        byte[] newPicture = {7, 8, 9};      // New profile picture data to overwrite
        user1.setProfilePicture(initialPicture);
        user1.setProfilePicture(newPicture);  // Overwrite with new data
        assertArrayEquals("Profile picture should match the latest data set", 
                          newPicture, user1.getProfilePicture());
    }

    @Test
    public void testAddFriend() {
        assertTrue(user1.addFriend(user2));
        assertTrue(user1.getFriendList().contains(user2));
    }

    @Test
    public void testRemoveFriend() {
        user1.addFriend(user2);
        assertTrue(user1.removeFriend(user2));
        assertFalse(user1.getFriendList().contains(user2));
    }

    @Test
    public void testUnblockUser() {
        user1.blockUser(user2);
        assertTrue(user1.unblockUser(user2));
        assertFalse(user1.getBlockedUsers().contains(user2));
    }

    @Test
    public void testPerfectMatch() throws InvalidInput {
        user1.setPreferences("11 PM", false, false, true, 5, 5);
        user2.setPreferences("11 PM", false, false, true, 5, 5);
        assertTrue(user1.perfectMatch(user2));
    }

    @Test
    public void testNotPerfectMatch() throws InvalidInput {
        user1.setPreferences("11 PM", false, false, true, 5, 5);
        user2.setPreferences("10 PM", true, false, true, 5, 5);
        assertFalse(user1.perfectMatch(user2));
    }

    @Test
    public void testPartialMatch() throws InvalidInput {
        user1.setPreferences("11 PM", false, false, true, 5, 5);
        user2.setPreferences("11 PM", true, false, true, 4, 3);
        assertEquals(3, user1.partialMatch(user2));  // Expecting 3 matching preferences
    }

    @Test
    public void testNoPartialMatch() throws InvalidInput {
        user1.setPreferences("11 PM", false, false, true, 5, 5);
        user2.setPreferences("10 PM", true, true, false, 1, 2);
        assertEquals(0, user1.partialMatch(user2));  // Expecting 0 matches
    }

    @Test
    public void testSetPhoneNumberUser4() {
        user4.setPhoneNumber(null);
        assertEquals(null, user4.getPhoneNumber());  // Assuming null phone number defaults to empty string
    }

    @Test
    public void testSetPreference1() throws InvalidInput {
        user1.setPreferences("11 PM", false, false, true, 5, 5);
        assertEquals("11 PM, false, false, true, 5, 5", user1.getPreferences());
    }

    @Test
    public void testSetPreference2() throws InvalidInput {
        user2.setPreferences("12 AM", false, false, true, 3, 5);
        assertEquals("12 AM, false, false, true, 3, 5", user2.getPreferences());
    }

    @Test
    public void testSetPreference3() throws InvalidInput {
        user3.setPreferences("10 PM", true, true, false, 4, 0);
        assertEquals("10 PM, true, true, false, 4, 0", user3.getPreferences());
    }

    @Test
    public void testSetPreference4() throws InvalidInput {
        user4.setPreferences("11 PM", true, false, true, 2, 3);
        assertEquals("11 PM, true, false, true, 2, 3", user4.getPreferences());
    }

    @Test
    public void testToString() throws InvalidInput {
        user1.setPreferences("11 PM", false, false, true, 5, 5);
        String expected = "Bob<<END>>password123<<END>>bob@gmail.com<<END>>1234567890<<END>>" +
            "Test user Bob<<END>>University A<<END>>11 PM<<END>>false<<END>>false<<END>>true<<END>>5<<END>>5";
        assertEquals(expected, user1.toString());
    }

    @Test
    public void testToString2() throws InvalidInput {
        user2.setPreferences("12 AM", false, false, true, 3, 5);
        String expected2 = "Jim<<END>>password234<<END>>jim@gmail.com<<END>>2345678901<<END>>" +
            "Test user Jim<<END>>University B<<END>>12 AM<<END>>false<<END>>false<<END>>true<<END>>3<<END>>5";
        assertEquals(expected2, user2.toString());
    }

    @Test
    public void testToString3() throws InvalidInput {
        user3.setPreferences("10 PM", true, true, false, 4, 0);
        String expected3 = "null<<END>>password345<<END>>alice@gmail.com<<END>>3456789012<<END>>" +
            "Test User 3<<END>>University C<<END>>10 PM<<END>>true<<END>>true<<END>>false<<END>>4<<END>>0";
        assertEquals(expected3, user3.toString());
    }

    @Test
    public void testToString4() throws InvalidInput {
        user4.setPreferences("11 PM", true, false, true, 2, 3);
        String expected4 = "David<<END>>null<<END>>david@gmail.com<<END>>4567890123<<END>>" + 
            "Test User 4<<END>>Univercity D<<END>>11 PM<<END>>true<<END>>false<<END>>true<<END>>2<<END>>3";
        assertEquals(expected4, user4.toString());
    }
}
