import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;

public class TestChat {
    private User user1;
    private User user2;
    private ArrayList<String> messages;

    @Before
    public void setUp() throws UsernameTakenException {
        user1 = new User("Bob", "password123", "bob@gmail.com", "1234567890", "sender", "purdue");
        user2 = new User("Joe", "password234", "joe@gmail.com", "2345678901", "receiver", "purdue2");
        messages = new ArrayList<>();
    }

    // Tests for Chat constructor
    @Test
    public void testConstructorWithInitialMessages() {
        messages.add("hello");
        messages.add("goodbye");
        Chat chat = new Chat(user1, user2, messages);

        ArrayList<String> retrievedMessages = chat.getMessages();
        assertEquals(2, retrievedMessages.size());
        assertEquals("hello", retrievedMessages.get(0));
        assertEquals("goodbye", retrievedMessages.get(1));
    }

    @Test
    public void testConstructorEmptyMessages() {
        Chat chat = new Chat(user1, user2, new ArrayList<>());
        assertTrue(chat.getMessages().isEmpty());  // Chat should start with no messages
    }

    @Test
    public void testConstructorNullMessages() {
        Chat chat = new Chat(user1, user2, null);
        assertTrue(chat.getMessages().isEmpty());  // Chat should handle null by initializing an empty list
    }

    // Tests for sendMessage method
    @Test
    public void testSendMessageSuccessfully() {
        Chat chat = new Chat(user1, user2, new ArrayList<>());
        chat.sendMessage(user1, user2, "hello");

        ArrayList<String> messages = chat.getMessages();
        assertEquals(1, messages.size());
        assertEquals("hello", messages.get(0));
    }

    @Test
    public void testSendMultipleMessages() {
        Chat chat = new Chat(user1, user2, new ArrayList<>());
        chat.sendMessage(user1, user2, "hi");
        chat.sendMessage(user2, user1, "hello back");
        chat.sendMessage(user1, user2, "how are you?");

        ArrayList<String> messages = chat.getMessages();
        assertEquals(3, messages.size());
        assertEquals("hi", messages.get(0));
        assertEquals("hello back", messages.get(1));
        assertEquals("how are you?", messages.get(2));
    }

    @Test
    public void testSendMessageNullContent() {
        Chat chat = new Chat(user1, user2, new ArrayList<>());
        chat.sendMessage(user1, user2, null);

        assertTrue(chat.getMessages().isEmpty());  // Null message should not be added
    }

    // Tests for deleteMessage(User sender, User receiver, String message) method
    @Test
    public void testDeleteMessageSuccessfully() {
        Chat chat = new Chat(user1, user2, new ArrayList<>());
        chat.sendMessage(user1, user2, "hello from user1");
        chat.sendMessage(user2, user1, "reply from user2");

        assertTrue(chat.deleteMessage(user1, user2, "hello from user1"));  // Deletes specific message
        ArrayList<String> remainingMessages = chat.getMessages();
        assertEquals(1, remainingMessages.size());
        assertEquals("reply from user2", remainingMessages.get(0));
    }

    @Test
    public void testDeleteMessageNoMatch() {
        Chat chat = new Chat(user1, user2, new ArrayList<>());
        chat.sendMessage(user1, user2, "message from user1");

        assertFalse(chat.deleteMessage(user1, user2, "nonexistent message"));  // Message doesn't exist
        ArrayList<String> remainingMessages = chat.getMessages();
        assertEquals(1, remainingMessages.size());
        assertEquals("message from user1", remainingMessages.get(0));
    }

    @Test
    public void testDeleteMessageFromEmptyChat() {
        Chat chat = new Chat(user1, user2, new ArrayList<>());
        assertFalse(chat.deleteMessage(user1, user2, "any message"));  // Attempt to delete from empty chat

        assertTrue(chat.getMessages().isEmpty());  // Should remain empty
    }
}
