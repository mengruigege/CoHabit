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

}
