import java.util.ArrayList;

/**
 * Team Project Phase 1 - CoHabit
 *
 * This program works to implement a roommate search algorithm
 *
 * @author Aidan Lefort, Andrew Tang, Keya Jadhav, Rithvik Siddenki, Rui Meng 
 *
 * @version November 3rd, 2024
 *
 */

public class FriendList implements FriendManageable, Blockable {
    private ArrayList<User> friends;
    private ArrayList<User> blocked;
    private User user;
    private Database database;

    //Constructs a newly allocated FriendList object with the specified field values.
    public FriendList(User user, Database database) {
        this.user = user;
        this.database = database;

        this.friends = new ArrayList<>(database.loadFriendsFromFile());
        this.blocked = new ArrayList<>(database.loadBlockedFromFile());
    }

    //Helps deal with null values in UserSearch.java and FriendList.java
    public FriendList() {
        this.database = new Database();
        this.friends = new ArrayList<>();
        this.blocked = new ArrayList<>();
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

    //Returns a list of users who are friends from FriendList
    public ArrayList<User> getFriendList() {
        return friends;
    }

    //Updates a list of users who are friends from FriendList
    public void setFriendList(ArrayList<User> friends) {
        this.friends = friends;
    }

    //Returns a list of users who are blocked from FriendList
    public ArrayList<User> getBlockedUsers() {
        return blocked;
    }

    //Updates a list of users who are blocked from FriendList
    public void setBlockedUsers(ArrayList<User> blocked) {
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
