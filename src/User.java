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

    private FriendList friends;
    private ArrayList<User> blockedUsers = new ArrayList<>();

    public User(String name, String password, String email, String phoneNumber, String userDescription, String university) throws UsernameTakenException {
        this.name = name;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.description = description == null ? "" : description;
        this.university = university;

        this.friends = new FriendList();
        this.blockedUsers = new ArrayList<>();
    }

    public boolean removeFriend(User user) {
        return friends.removeFriend(user);
    }
    public boolean addFriend(User user) {
        return friends.addFriend(user);
    }
    public ArrayList<User> getFriends(User user) {
        return friends.getFriends();
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
    public void setPreferences(String bedTime, boolean alcohol, boolean smoke, boolean guests, int tidy, int roomHours) {
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

    public synchronized boolean UserExists(User user) {
        if (allUsers.contains(user)) {
            return true; //is this required? can we return the result of the
            // contains() function directly? return allUsers.contains(user);
        }
        return false;
    }


    public synchronized void deleteUser(User user) {
        if (UserExists(user)) {
            allUsers.remove(user);
        }

    }
    public static synchronized ArrayList<User> getAllUsers() {
        return new ArrayList<>(allUsers);
    }
    public String toString() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%b,%b,%b,%d,%d", this.name,
                this.password, this.email, this.phoneNumber, this.description,
                this.university, this.bedTime, this.alcohol, this.smoke, this.guests,
                this.tidy, this.roomHours);
    }
}
