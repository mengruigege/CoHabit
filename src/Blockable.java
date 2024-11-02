import java.util.ArrayList;

public interface Blockable {
    boolean blockUser();
    boolean unblockUser();
    ArrayList<User> getBlockedUsers();
}
