import java.util.ArrayList;

public class FriendList implements FriendManageable, Blockable {
    private ArrayList<User> friends;
    private ArrayList<User> blocked;
    private User user;
    private Database database;

    public FriendList(User user, Database database) {
        this.user = user;
        this.database = database;

        this.friends = new ArrayList<>(database.loadFriendsFromFile());
        this.blocked = new ArrayList<>(database.loadBlockedFromFile());
    }

    public FriendList() {
        this.database = new Database();
        this.friends = new ArrayList<>();
        this.blocked = new ArrayList<>();
    }

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

    public ArrayList<User> getFriendList() {
        return friends;
    }

    public void setFriendList(ArrayList<User> friends) {
        this.friends = friends;
    }

    public ArrayList<User> getBlockedUsers() {
        return blocked;
    }

    public void setBlockedUsers(ArrayList<User> blocked) {
        this.blocked = blocked;
    }

    public synchronized boolean removeFriend(User user) {
        return friends.remove(user);
    }

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
    public synchronized boolean unblockUser(User user) {
        return blocked.remove(user);
    }
    public synchronized ArrayList<User> getFriends() {
        return friends;
    }
    
    public synchronized ArrayList<User> getBlocked() {
        return blocked;
    }

    private class UserTask implements Runnable {
        private final User user;
        private final String action;

        public UserTask(User user, String action) {
            this.user = user;
            this.action = action;
        }

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
            
            if (result) {
                System.out.println(user.getName() + " successfully " + action.toLowerCase().replace("_", " ") + ".");
            } else {
                System.out.println("Failed to " + action.toLowerCase().replace("_", " ") + " " + user.getName() + ".");
            }
        }
    }

}
