import java.util.ArrayList;


public class Chat implements Sendable {
    private User sender;
    private User receiver;
    private ArrayList<String> messages;

    //Constructor to initialize all variables with their respective objects
    public Chat(User sender, User receiver, ArrayList<String> messages) {
        this.sender = sender;
        this.receiver = receiver;
        this.messages = messages;
    }

    //Synchronized method to make sure only one thread can send a message at a time
    public synchronized boolean sendMessage(User sender, User receiver, String message) {
        return messages.add(message);
    }

    //Synchronized method to make sure only one thread can get a message at a time
    public synchronized ArrayList<String> getMessages() {
        ArrayList<String> nullList = new ArrayList<>();
        if(messages != null) {
            return messages;
        } else {
            return nullList;
        }
    }

    //Synchronized method to make sure only one thread can delete a message at a time
    public synchronized boolean deleteMessage(User sender, User receiver, String message) {
        return messages.remove(message);
    }

    //Inner class to implement multi-threading in a thread-safe environment for sending messages
    private class SendMessageTask implements Runnable {
            private final User sender;
            private final User receiver;
            private final String message;

            public SendMessageTask(User sender, User receiver, String message) {
                this.sender = sender;
                this.receiver = receiver;
                this.message = message;
            }

            public void run() {
                synchronized (Chat.this) {
                    messages.add(message);
                    System.out.println("Message sent: " + message);
                }
            }
        }

    //Inner class to implement multi-threading in a thread-safe environment for deleting messages
    private class DeleteMessageTask implements Runnable {
            private final User sender;
            private final User receiver;
            private final String message;

            public DeleteMessageTask(User sender, User receiver, String message) {
                this.sender = sender;
                this.receiver = receiver;
                this.message = message;
            }

            public void run() {
                synchronized (Chat.this) {
                    if (messages.remove(message)) {
                        System.out.println("Message deleted: " + message);
                    }
                }
            }
     }
}

