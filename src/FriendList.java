import java.util.ArrayList;

public class FriendList implements Friend {
    private ArrayList<User> friends = new ArrayList<User>();
    private ArrayList<User> restricted = new ArrayList<>();
    private ArrayList<User> blocked = new ArrayList<>();

    public synchronized boolean addFriend(User user) {
        if (user != null && !friends.contains(user) && !blocked.contains(user)) {
            friends.add(user);
            return true;
        }
        return false;
    }
    public synchronized boolean removeFriend(User user) {
        return (friends.remove(user));
    }
    public synchronized boolean restrictUser(User user) {
        if (user != null && friends.contains(user) && !restricted.contains(user)) {
            restricted.add(user);
            return true;
        }
        return false;
    }
    public synchronized boolean allowUser(User user) {
        return restricted.remove(user);
    }
    public synchronized boolean blockUser(User user) {
        if (user != null && !blocked.contains(user)) {
            blocked.add(user);
            friends.remove(user);
            restricted.remove(user);
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
    public synchronized ArrayList<User> getRestricted() {
        return restricted;
    }
    public synchronized ArrayList<User> getBlocked() {
        return blocked;
    }

    // Inner class for executing friend tasks
    private class FriendTasks implements Runnable {
        private final User user;
        private final String action;

        public FriendTasks(User user, String action) {
            this.user = user;
            this.action = action;
        }
        
        public void run() {
            boolean result = false;
            switch (action.toUpperCase()) {
                case "ADD_FRIEND":
                    result = addFriend(user);
                    break;
                case "BLOCK_USER":
                    result = blockUser(user);
                    break;
                case "RESTRICT_USER":
                    result = restrictUser(user);
                    break;
            }
            if (result) {
                System.out.println(user + " successfully " + action.toLowerCase().replace("_", " ") + "."); 
            } else {
                System.out.println("Failed to " + action.toLowerCase().replace("_", " ") + " " + user + "."); 
            }
        }
    }

    public void executeAction(User user, String action) {
        FriendTasks task = new FriendTasks(user, action); 
        Thread thread = new Thread(task); 
        thread.start(); 
    }
}

}
