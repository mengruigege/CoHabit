import java.util.ArrayList;

public class User implements Profile {
    private String name;
    private String pwd;
    private String email;
    private String phoneNum;
    private String userDesc;
    private String uni;
    private String bedTime;
    private boolean alcohol;
    private boolean smoke;
    private boolean guests;
    private int tidy;
    private int roomHours;
    
    public static final ArrayList<User> allUsers = new ArrayList<>();
    private FriendList friendList;


//constructor for user
    public User(String name, String pwd, String email, String phoneNum, String userDesc, String uni) {

        this.name = name;
        this.pwd = pwd;
        this.email = email;
        this.phoneNum = phoneNum;
        this.userDesc = userDesc;
        this.uni = uni;
        allUsers.add(this);
    }

//getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getUserDesc() {
        return userDesc;
    }

    public void setUserDesc(String userDesc) {
        this.userDesc = userDesc;
    }

    public String getUni() {
        return uni;
    }

    public void setUni(String uni) {
        this.uni = uni;
    }

    public String getPreferences() {
        return String.format("Bedtime: %s, Alcohol: %b, Smoke: %b, Guests: %b, Tidiness: %d, Room Hours: %d",
                this.bedTime, this.alcohol, this.smoke, this.guests, this.tidy, this.roomHours);
    }
    
    public void setPreferences(String bedTime, boolean alcohol, boolean smoke, boolean guests, int tidy, int roomHours) {
        this.bedTime = bedTime;
        this.alcohol = alcohol;
        this.smoke = smoke;
        this.guests = guests;
        this.tidy = tidy;
        this.roomHours = roomHours;
    }
    
    // Equals method that determines if two users have all the same preferences. - are we looking for best match or perfect match?
    public boolean perfectMatch(User user) {
        return this.bedTime.equals(user.bedTime) &&
                this.alcohol == user.alcohol &&
                this.smoke == user.smoke &&
                this.guests == user.guests &&
                this.tidy == user.tidy &&
                this.roomHours == user.roomHours;
    }

    public synchronized boolean UserExists(User user) {
        if (allUsers.contains(user)) {
            return true; //is this required? can we return the result of the contains() function directly? return allUsers.contains(user);
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


}
