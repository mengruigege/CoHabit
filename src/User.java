import java.util.ArrayList;

public class User implements Profile, FriendManageable, Blockable {

    private String name;
    private String password;
    private String email;
    private String phoneNumber;
    private String description;
    private String university;

    private String bedTime;
    private boolean alcohol;
    private boolean smoke;
    private boolean guests;
    private int tidy;
    private int roomHours;

    private FriendList friendUsers;
    private FriendList blockedUsers;

    public User(String name, String password, String email, String phoneNumber, String userDescription, String university) throws UsernameTakenException {
        this.name = name;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.description = description == null ? "" : description;
        this.university = university;

        this.friendUsers = new FriendList();
        this.blockedUsers = new FriendList();
    }

    public ArrayList<User> getFriendUsers() {
        return friendUsers.getFriends();
    }
    public boolean removeFriend(User user) {return friendUsers.removeFriend(user);}
    public boolean addFriend(User user) {
        return friendUsers.addFriend(user);
    }
    public boolean blockUser(User user) {
        return friendUsers.blockUser(user);
    }
    public boolean unblockUser(User user) {
        return friendUsers.unblockUser(user);
    }
    public ArrayList<User> getFriendList(User user) {
        return friendUsers.getFriends();
    }
    public ArrayList<User> getBlockedUsers(User user) {
        return blockedUsers.getBlocked();
    }
    public void setFriendList(ArrayList<User> friends) {
        this.friendUsers.setFriendList(friends);
    }

    public String getName() { return name; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getUniversity() { return university; }
    public String getDescription() { return description; }
    public String getPreferences() {
        return String.format("Bedtime: %s, Alcohol: %b, Smoke: %b, Guests: %b, Tidiness: %d, Room Hours: %d",
                this.bedTime, this.alcohol, this.smoke, this.guests, this.tidy, this.roomHours);
    }

    public void setName(String name) { this.name = name; }
    public void setPassword(String pwd) { this.password = pwd; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNumber(String phoneNum) { this.phoneNumber = phoneNum; }
    public void setUniversity(String university) { this.university = university; }
    public void setDescription(String userDesc) { this.description = userDesc; }
    public void setPreferences(String bedTime, boolean alcohol, boolean smoke,
                               boolean guests, int tidy, int roomHours) {
        this.bedTime = bedTime;
        this.alcohol = alcohol;
        this.smoke = smoke;
        this.guests = guests;
        this.tidy = tidy;
        this.roomHours = roomHours;
    }

    // Equals method that determines if two users have all the same preferences.
    public boolean perfectMatch(User user) {
        return this.bedTime.equals(user.bedTime) &&
                this.alcohol == user.alcohol &&
                this.smoke == user.smoke &&
                this.guests == user.guests &&
                this.tidy == user.tidy &&
                this.roomHours == user.roomHours;
    }

    public int partialMatch(User user) {
        int count = 0;
        if (this.bedTime.equals(user.bedTime)) {
            count++;
        }
        if (this.alcohol == user.alcohol) {
            count++;
        }
        if (this.smoke == user.smoke) {
            count++;
        }
        if (this.guests == user.guests) {
            count++;
        }
        if (this.tidy == user.tidy) {
            count++;
        }
        if (this.roomHours == user.roomHours) {
            count++;
        }
        return count;

    }

    public String toString() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%b,%b,%b,%d,%d", this.name,
                this.password, this.email, this.phoneNumber, this.description,
                this.university, this.bedTime, this.alcohol, this.smoke, this.guests,
                this.tidy, this.roomHours);
    }
}
