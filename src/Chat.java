import java.util.ArrayList;

/**
 * Team Project Phase 1 - CoHabit
 * <p>
 * This program works to implement a roommate search algorithm
 *
 * @author Aidan Lefort, Andrew Tang, Keya Jadhav, Rithvik Siddenki, Rui Meng
 * @version November 3rd, 2024
 */

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

    //Adds a message to the messages list, returns true if added successfully
    //Synchronized to ensure a thread-safe environment
    public synchronized boolean sendMessage(User sender, User receiver, String message) {
        if (message != null) {
            boolean isBlocked = false;
            boolean isFriend1 = false;
            boolean isFriend2 = false;
            for (User user : sender.getBlockedUsers()) {
                if (receiver.getName().equals(user.getName())) {
                    isBlocked = true;
                    break;
                }
            }
            for (User user : receiver.getBlockedUsers()) {
                if (sender.getName().equals(user.getName())) {
                    isBlocked = true;
                    break;
                }
            }
            for (User user : sender.getFriendList()) {
                if (receiver.getName().equals(user.getName())) {
                    isFriend1 = true;
                    break;
                }
            }
            for (User user : receiver.getFriendList()) {
                if (sender.getName().equals(user.getName())) {
                    isFriend2 = true;
                    break;
                }
            }
            if (!isBlocked && isFriend1 && isFriend2) {
                messages.add(message);
            }
        } else {
            return false;
        }
        return true;
    }

    //Returns an ArrayList containing all messages in the chat.
    //Synchronized to ensure a thread-safe environment
    public synchronized ArrayList<String> getMessages() {
        ArrayList<String> nullList = new ArrayList<>();
        if (messages != null) {
            return messages;
        } else {
            return nullList;
        }
    }

    //Deletes a message from the messages list, returns true if removed successfully.
    //Synchronized to ensure a thread-safe environment
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

        public void run() { //Adds a message to the messages list within a synchronized block.
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

        public void run() { //Deletes a message from the messages list within a synchronized block.
            synchronized (Chat.this) {
                if (messages.remove(message)) {
                    System.out.println("Message deleted: " + message);
                }
            }
        }
    }
}

