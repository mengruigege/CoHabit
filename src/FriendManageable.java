import java.util.ArrayList;

public interface FriendManageable {
    public boolean addFriend(User user); //Object to track users will be "User"
    public boolean removeFriend(User user); //removes user from list of friends
    public boolean blockUser(User user); //removes user from search; completely block interaction
    public boolean unblockUser(User user); //reverses blocking method
    public ArrayList<User> getFriendList(); //returns friends list
    public void setFriendList(ArrayList<User> friends); //changes friends list
}
