import java.util.ArrayList;

public class FriendList implements FriendManageable, Blockable, Listable<User> {
    private ArrayList<User> friends = new ArrayList<>();
    private ArrayList<User> restricted = new ArrayList<>(); //should these be declared final?
    private ArrayList<User> blocked = new ArrayList<>();
    private User owner; 

    public FriendList(User owner) {
        this.owner = owner;
        this.friends = new ArrayList<>();
        this.blocked = new ArrayList<>();
        this.friends.add(owner);
        this.blocked.add(owner);

    }

    public synchronized boolean addFriend(User user) {
        if (user != null && !friends.contains(user) && !blocked.contains(user)) {
            friends.add(user);
            return true;
        }
        return false;
    }

    public synchronized boolean removeFriend(User user) {
        return friends.remove(user);
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
                case "RESTRICT_USER":
                    result = restrictUser(user);
                    break;
                case "ALLOW_USER":
                    result = allowUser(user);
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
