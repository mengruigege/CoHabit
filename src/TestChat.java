import org.junit.Test;
import static org.junit.Assert.assertEquals;
import java.util.ArrayList;

public class TestChat{
    private User user1 = new User("Bob", "password123","bob@gmail.com","1234567890","sender","purdue");
    private User user2 = new User("Joe","password234","joe@gmail.com","2345678901","receiver","purdue2");
    private ArrayList<String> messages = new ArrayList<>();

    public void testConstructor(){
        messages.add("hello");
        messages.add("goodbye");
        Chat chat1 = new Chat(user1,user2,messages);
        assertEquals("hello",messages.get(0));
        assertEquals("goodbye",messages.get(1));
    }
    public void testSendMessage(){
        Chat chat2 = new Chat(user1,user2,new ArrayList<>());
        ArrayList<String> messages2 = chat2.getMessages();
        chat2.sendMessage(user1,user2,"hello");
        assertEquals("hello",messages2.get(0));
    }
    public void testDeleteMessage(){
        chat chat3 = new Chat(user1,user2,messages);
        messages.remove(0);
        assertEquals("goodbye",messages.get(0));
    }
}

