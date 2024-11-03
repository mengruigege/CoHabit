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
    
    public static final ArrayList<User> allUsers = new ArrayList<>();
    private static Object lock = new Object();
    private FriendList friends = new FriendList();
    private ArrayList<User> blockedUsers = new ArrayList<>();

    public User(String name, String password, String email, String phoneNumber, String userDescription, String university) throws UsernameTakenException {
        synchronized (lock) {  // is this necessary?
            boolean userExists = false;
            for (User user : allUsers) {
                if (user.getName().equals(name)) {
                    userExists = true;
                    throw new UsernameTakenException("Username already taken");
                }
            }
            // correct logic for looping through all of them
            if (!userExists) {
                this.name = name;
                this.password = password;
                this.email = email;
                this.phoneNumber = phoneNumber;
                if (this.description == null) {
                    this.description = " ";
                } else {
                    this.description = userDescription;
                }
                this.university = university;
                allUsers.add(this);
            }
        }
    }

    public boolean removeFriend(User user) {
        synchronized (lock) {
            return friends.remove(user);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String pwd) {
        this.password = pwd;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getUniversity() {
        return university;
    }

    public void setPhoneNumber(String phoneNum) {
        this.phoneNumber = phoneNum;
    }

    public void setDescription(String userDesc) {
        this.description = userDesc;
    }

    public String getDescription() {
        return description;
    }

    public void setUniversity(String university) {
        this.university = university;
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
