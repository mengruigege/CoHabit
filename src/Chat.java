import java.util.ArrayList;

public class Chat implements Sendable {
    private User sender;
    private User reciever;
    private ArrayList<String> messages;

    public Chat(User sender, User reciever, ArrayList<String> messageLogs) {
        this.sender = sender;
        this.reciever = reciever;
        this.messages = new ArrayList<>();
    }

    public boolean sendMessage(User sender, User reciever, String message) { 
        messages.add(message);
        return true;
    }

    public synchronized ArrayList<String> getMessages() {
        //to make sure only one thread can get a message at a time
        return messages; //return new ArrayList<>(messages); should we put in this instead? it will make a copy of the text to be returned.
    }

    public boolean deleteMessage(User sender, User reciever, String message) {
        messages.remove(message);
        return true;
    }

}

/**
private class SendMessageTask implements Runnable {
        private final User sender;
        private final User receiver;
        private final String message;

        public SendMessageTask(User sender, User receiver, String message) {
            this.sender = sender;
            this.receiver = receiver;
            this.message = message;
        }

        @Override
        public void run() {
            synchronized (Chat.this) {
                messages.add(message);
                System.out.println("Message sent: " + message);
            }
        }
    }

    private class DeleteMessageTask implements Runnable {
        private final User sender;
        private final User receiver;
        private final String message;

        public DeleteMessageTask(User sender, User receiver, String message) {
            this.sender = sender;
            this.receiver = receiver;
            this.message = message;
        }

        @Override
        public void run() {
            synchronized (Chat.this) {
                if (messages.remove(message)) {
                    System.out.println("Message deleted: " + message);
                }
            }
        }
    }
}
*/
