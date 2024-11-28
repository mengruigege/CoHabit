import java.util.ArrayList;

/**
 * Team Project Phase 1 - CoHabit
 * <p>
 * This program works to implement a roommate search algorithm
 *
 * @author Aidan Lefort, Andrew Tang, Keya Jadhav, Rithvik Siddenki, Rui Meng
 * @version November 3rd, 2024
 */

public class Relationships implements FriendManageable, Blockable {
    private ArrayList<User> friends;
    private ArrayList<User> blocked;
    private ArrayList<User> incomingRequests;
    private ArrayList<User> outgoingRequests;
    private User user;

    //Constructs a newly allocated Relationships object with the specified field values.
    public Relationships(User user, Database database) {
        this.user = user;

        this.friends = new ArrayList<>();
        this.blocked = new ArrayList<>();

        this.incomingRequests = new ArrayList<>();
        this.outgoingRequests = new ArrayList<>();
    }

    //Helps deal with null values in UserSearch.java and Relationships.java
    public Relationships() {
        this.friends = new ArrayList<>();
        this.blocked = new ArrayList<>();
        this.incomingRequests = new ArrayList<>();
        this.outgoingRequests = new ArrayList<>();
    }

    // Sends a friend request to another user
    public boolean sendFriendRequest(User receiver) {
        if (receiver != null && !isFriend(receiver) && !hasPendingOutgoingRequest(receiver)) {
            outgoingRequests.add(receiver);
            receiver.addFriend(user);
            return true;
        }
        return false;
    }

    // Receives a friend request from another user
    public synchronized void receiveFriendRequest(User sender) {
        if (!incomingRequests.contains(sender) && !isFriend(sender)) {
            incomingRequests.add(sender);
        }
    }

    // Accepts a friend request from a specified user
    public synchronized boolean acceptFriendRequest(User sender) {
        if (incomingRequests.contains(sender)) {
            incomingRequests.remove(sender);
            addFriend(sender);
            sender.addFriend(user); // Mutual friendship
            return true;
        }
        return false;
    }

    // Declines a friend request from a specified user
    public synchronized boolean declineFriendRequest(User sender) {
        return incomingRequests.remove(sender);
    }

    // Sets the list of incoming friend requests
    public synchronized void setIncomingRequests(ArrayList<User> incomingRequests) {
        if (incomingRequests != null) {
            this.incomingRequests = new ArrayList<>(incomingRequests); // Defensive copy
        }
    }

    // Sets the list of outgoing friend requests
    public synchronized void setOutgoingRequests(ArrayList<User> outgoingRequests) {
        if (outgoingRequests != null) {
            this.outgoingRequests = new ArrayList<>(outgoingRequests); // Defensive copy
        }
    }

    // Adds an outgoing friend request (if not already present)
    public synchronized boolean addOutgoingRequest(User receiver) {
        if (receiver != null && !outgoingRequests.contains(receiver)) {
            return outgoingRequests.add(receiver);
        }
        return false;
    }

    // Adds an incoming friend request (if not already present)
    public synchronized boolean addIncomingRequest(User sender) {
        if (sender != null && !incomingRequests.contains(sender)) {
            return incomingRequests.add(sender);
        }
        return false;
    }

    // Removes an outgoing friend request
    public synchronized boolean removeOutgoingRequest(User receiver) {
        return outgoingRequests.remove(receiver);
    }

    // Removes an incoming friend request
    public synchronized boolean removeIncomingRequest(User sender) {
        return incomingRequests.remove(sender);
    }

    // Helper method to check if a user is already a friend
    public synchronized boolean isFriend(User user) {
        return friends.contains(user);
    }

    // Helper method to check for a pending outgoing request
    public synchronized boolean hasPendingOutgoingRequest(User user) {
        return outgoingRequests.contains(user);
    }

    // Returns a list of incoming friend requests
    public synchronized ArrayList<User> getIncomingRequests() {
        System.out.println(incomingRequests);
        return incomingRequests;
    }

    // Returns a list of outgoing friend requests
    public synchronized ArrayList<User> getOutgoingRequests() {
        return outgoingRequests;
    }

    //Adds a user to the friends list if not already a friend.
    public synchronized boolean addFriend(User user) {
        if (user != null && !friends.contains(user)) {
            friends.add(user);
            if (blocked.contains(user)) {
                unblockUser(user);
            }
            return true;
        }
        return false;
    }

    //Returns a list of users who are friends from Relationships
    public synchronized ArrayList<User> getFriendList() {
        return friends;
    }

    //Updates a list of users who are friends from Relationships
    public synchronized void setFriendList(ArrayList<User> friends) {
        this.friends = friends;
    }

    //Returns a list of users who are blocked from Relationships
    public synchronized ArrayList<User> getBlockedUsers() {
        return blocked;
    }

    //Updates a list of users who are blocked from Relationships
    public synchronized void setBlockedUsers(ArrayList<User> blocked) {
        this.blocked = blocked;
    }

    //Removes a user from the friends list.
    public synchronized boolean removeFriend(User user) {
        return friends.remove(user);
    }

    //Blocks a user and removes them from the friends list.
    public synchronized boolean blockUser(User user) {
        if (user != null && !blocked.contains(user)) {
            blocked.add(user);
            if (friends.contains(user)) {
                removeFriend(user);
            }
            return true;
        }
        return false;
    }

    //Unblocks a user by removing from the blocked list
    public synchronized boolean unblockUser(User user) {
        return blocked.remove(user);
    }

    //Returns a list of users who are currently friends.
    public synchronized ArrayList<User> getFriends() {
        return friends;
    }

    //Returns a list of users who are currently blocked.
    public synchronized ArrayList<User> getBlocked() {
        return blocked;
    }

    //To maintain a multi-threaded but thread-safe environment
    private class UserTask implements Runnable {
        private final User user;
        private final String action;

        public UserTask(User user, String action) {
            this.user = user;
            this.action = action;
        }

        //Executes the corresponding method in a multi-threaded scope.
        public void run() {
            boolean result = false;
            switch (action.toUpperCase()) {
                case "ADD_FRIEND":
                    result = addFriend(user);
                    break;
                case "REMOVE_FRIEND":
                    result = removeFriend(user);
                    break;
                case "BLOCK_USER":
                    result = blockUser(user);
                    break;
                case "UNBLOCK_USER":
                    result = unblockUser(user);
                    break;
                default:
                    System.out.println("Unknown action: " + action);
                    return;
            }

            if (result) { //Prints the result
                System.out.println(user.getName() + " successfully " + action.toLowerCase().replace("_", " ") + ".");
            } else {
                System.out.println("Failed to " + action.toLowerCase().replace("_", " ") + " " + user.getName() + ".");
            }
        }
    }

}
