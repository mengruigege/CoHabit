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
        this.description = userDescription == null ? "" : userDescription;
        this.university = university;

    }

    public ArrayList<User> getFriendList() {
        return friendUsers.getFriends();
    }
    public ArrayList<User> getBlockedUsers() {
        return blockedUsers.getBlocked();
    }
    public void setFriendList(ArrayList<User> friends) {
        friendUsers.setFriendList(friends);
    }
    public void setBlockedUsers(ArrayList<User> blocked) {
        blockedUsers.setBlockedUsers(blocked);
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

    public String getName() { return name; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getUniversity() { return university; }
    public String getDescription() { return description; }
    public String getPreferences() {
        return String.format("%s, %b, %b, %b, %d, %d",
                this.bedTime, this.alcohol, this.smoke, this.guests, this.tidy, this.roomHours);
    }

    public void setName(String name) { this.name = name; }
    public void setPassword(String pwd) { this.password = pwd; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNumber(String phoneNum) { this.phoneNumber = phoneNum; }
    public void setUniversity(String university) { this.university = university; }
    public void setDescription(String userDesc) { this.description = userDesc; }
    public void setPreferences(String bedTime, boolean alcohol, boolean smoke,
                               boolean guests, int tidy, int roomHours) throws InvalidInput {
            this.bedTime = bedTime;
            this.alcohol = alcohol;
            this.smoke = smoke;
            this.guests = guests;
            this.tidy = tidy;
            this.roomHours = roomHours;
            if (bedTime == null || tidy <=0 || tidy > 10 || roomHours < 0) {
                throw new InvalidInput("Invalid Input");
            }
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
                this.university, bedTime, alcohol, smoke, guests,
                tidy, roomHours);
    }
}
