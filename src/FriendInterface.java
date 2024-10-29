public interface FriendInterface {
    public boolean addFriend(User user); //I figured the object to track users will be "User"
    public boolean removeFriend(User user);
    public boolean restrictUser(User user);
    public boolean allowUser(User user);
    public boolean blockUser(User user);
    public boolean unblockUser(User user);
    public User[] getFriendList();
    public void setFriendList(User[] friends);
}
