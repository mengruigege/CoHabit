import java.util.ArrayList;

/**
 * Team Project Phase 1 - CoHabit
 * <p>
 * This program works to implement a roommate search algorithm
 *
 * @author Aidan Lefort, Andrew Tang, Keya Jadhav, Rithvik Siddenki, Rui Meng
 * @version November 3rd, 2024
 */

public class User implements Profile, FriendManageable, Blockable {

    private String name;
    private String password;
    private String email;
    private String phoneNumber;
    private String description;
    private String university;
    private byte[] profilePicture;

    private String bedTime;
    private boolean alcohol;
    private boolean smoke;
    private boolean guests;
    private int tidy;
    private int roomHours;

    private static final String DELIMITER = "<<END>>";

    private Relationships relationships = new Relationships();

    //Constructs a newly allocated User object with the specified field values.
    public User(String name, String password, String email, String phoneNumber, 
                String userDescription, String university) throws UsernameTakenException {
        this.name = name;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.description = userDescription == null ? "" : userDescription;
        this.university = university;

    }

    //Retrieves the list of friends.
    public ArrayList<User> getFriendList() {
        return relationships.getFriends();
    }

    //Retrieves the list of blocked users.
    public ArrayList<User> getBlockedUsers() {
        return relationships.getBlocked();
    }

    public ArrayList<User> getOutgoingFriendRequest() {
        return relationships.getOutgoingRequests();
    }

    public ArrayList<User> getIncomingFriendRequest() {
        return relationships.getIncomingRequests();
    }

    //Sets the friends list.
    public void setFriendList(ArrayList<User> friends) {
        relationships.setFriendList(friends);
    }

    //Sets the blocked users list.
    public void setBlockedUsers(ArrayList<User> blocked) {
        relationships.setBlockedUsers(blocked);
    }

    //Removes a user from the friends list.
    public boolean removeFriend(User user) {
        return relationships.removeFriend(user);
    }

    //Adds a user to the friends list if not already a friend.
    public boolean addFriend(User user) {
        return relationships.addFriend(user);
    }

    //Blocks a user and removes them from the friends list.
    public boolean blockUser(User user) {
        return relationships.blockUser(user);
    }

    //Unblocks a user.
    public boolean unblockUser(User user) {
        return relationships.unblockUser(user);
    }

    // Adds a friend request to incoming requests
    public void addIncomingRequest(User sender) {
        relationships.receiveFriendRequest(sender);
    }

    // Adds a friend request to outgoing requests
    public void sendFriendRequest(User receiver) {
        relationships.sendFriendRequest(receiver);
    }

    // Removes a friend request from incoming requests
    public void removeIncomingRequest(User sender) {
        relationships.declineFriendRequest(sender);
    }

    // Removes a friend request from outgoing requests
    public void removeOutgoingRequest(User receiver) {
        relationships.getOutgoingRequests().remove(receiver);
    }

    // Accepts a friend request and adds the sender to the friends list
    public boolean acceptFriendRequest(User sender) {
        return relationships.acceptFriendRequest(sender);
    }

    // Declines a friend request and removes it from incoming requests
    public boolean declineFriendRequest(User sender) {
        return relationships.declineFriendRequest(sender);
    }

    //getters and setters for user personal details such as name, password, contact details, and so on
    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getUniversity() {
        return university;
    }

    public String getDescription() {
        return description;
    }

    public String getPreferences() {
        return String.format("%s, %b, %b, %b, %d, %d", this.bedTime, 
                             this.alcohol, this.smoke, this.guests, this.tidy, this.roomHours);
    }

    public boolean isAlcohol() {
        return alcohol;
    }

    public boolean isSmoke() {
        return smoke;
    }

    public boolean isGuests() {
        return guests;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String pwd) {
        this.password = pwd;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNum) {
        this.phoneNumber = phoneNum;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public void setDescription(String userDesc) {
        this.description = userDesc;
    }

    public byte[] getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(byte[] pictureData) {
        this.profilePicture = pictureData;
    }

    public void setPreferences(String bedTime, boolean alcohol, boolean smoke, 
                               boolean guests, int tidy, int roomHours) throws InvalidInput {
        this.bedTime = bedTime;
        this.alcohol = alcohol;
        this.smoke = smoke;
        this.guests = guests;
        this.tidy = tidy;
        this.roomHours = roomHours;
    }

    public void setAlcohol(boolean alcohol) {
        this.alcohol = alcohol;
    }

    public void setSmoke(boolean smoke) {
        this.smoke = smoke;
    }

    public void setGuests(boolean guests) {
        this.guests = guests;
    }

    public void setTidy(int tidy) throws InvalidInput {
        if (tidy <= 0 || tidy > 10) {
            throw new InvalidInput("Tidy value must be between 1 and 10");
        }
        this.tidy = tidy;
    }

    public void setRoomHours(int roomHours) throws InvalidInput {
        if (roomHours < 0) {
            throw new InvalidInput("Room hours cannot be negative");
        }
        this.roomHours = roomHours;
    }

    // Sets the incoming friend requests
    public void setIncomingFriendRequest(ArrayList<User> incomingRequests) {
        relationships.setIncomingRequests(incomingRequests);
    }

    // Sets the outgoing friend requests
    public void setOutgoingFriendRequest(ArrayList<User> outgoingRequests) {
        relationships.setOutgoingRequests(outgoingRequests);
    }


    // Method that determines if two users have all the exact same preferences.
    public boolean perfectMatch(User user) {
        if (user == null || bedTime == null) {
            return false;
        }
        return this.bedTime.equals(user.bedTime) && this.alcohol == user.alcohol 
            && this.smoke == user.smoke && this.guests == user.guests 
            && this.tidy == user.tidy && this.roomHours == user.roomHours;
    }

    // Method that determines if 2 users have some matched preferences and checks the count of matched preferences
    public int partialMatch(User user) {
        int count = 0;
        if (this.bedTime != null && this.bedTime.equals(user.bedTime)) {
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

    public String getBedTime() {
        return bedTime;
    }

    public boolean getAlcohol() {
        return alcohol;
    }

    public boolean getSmoke() {
        return smoke;
    }

    public boolean getGuests() {
        return guests;
    }

    public int getTidy() {
        return tidy;
    }

    public int getRoomHours() {
        return roomHours;
    }

    //Returns a string representation of the user object
    public String toString() {
        return String.join(DELIMITER, this.name, this.password, this.email, 
                           this.phoneNumber, this.description, this.university, bedTime, 
                           String.valueOf(alcohol), String.valueOf(smoke), String.valueOf(guests), 
                           String.valueOf(tidy), String.valueOf(roomHours));
    }
}
