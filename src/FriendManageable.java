import java.util.ArrayList;

public interface FriendManageable {
    public boolean addFriend(User user); //I figured the object to track users will be "User"
    public boolean removeFriend(User user); //removes user from list of friends
    public boolean restrictUser(User user); //restricts user interactions with each other
    public boolean allowUser(User user); //reverses restrictions
    public boolean blockUser(User user); //removes user from search; completely block interaction
    public boolean unblockUser(User user); //reverses blocking method
    public ArrayList<User> getFriendList(); //returns friends list
    public void setFriendList(ArrayList<User> friends); //changes friends list
}
