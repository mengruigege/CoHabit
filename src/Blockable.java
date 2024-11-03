import java.util.ArrayList;

public interface Blockable {
    boolean blockUser(User user);
    boolean unblockUser(User user);
    ArrayList<User> getBlockedUsers();
}
