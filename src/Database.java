import java.lang.reflect.Array;
import java.util.ArrayList;
import java.nio.file.*;
import java.io.*;

/**
 * Team Project Phase 1 - CoHabit
 * <p>
 * This program works to implement a roommate search algorithm
 *
 * @author Aidan Lefort, Andrew Tang, Keya Jadhav, Rithvik Siddenki, Rui Meng
 * @version November 3rd, 2024
 */

public class Database implements DatabaseInterface {
    private static ArrayList<User> allUsers = new ArrayList<>();
    private static final String USERS_FILE = "users.txt";
    private static final String FRIENDS_FILE = "friends.txt";
    private static final String MESSAGES_FILE = "messages.txt";
    private static final String BLOCKED_FILE = "blocked.txt";
    private static final String FRIEND_REQUESTS_FILE = "friend_requests.txt";
    private static final String PROFILE_PICTURE_FOLDER = "profile_pictures";
    private static final Object lock = new Object();

    public Database() {
        synchronized (lock) {
            this.allUsers = new ArrayList<>();
        }
    }

    public synchronized boolean addUser(User user) {
        if (user != null && !usernameExists(user.getName())) {
            allUsers.add(user);
            return true;
        }
        return false; // User is null or username is taken
    }

    public boolean deleteUser(User user) {
        boolean removed;
        synchronized (lock) {
            removed = allUsers.remove(user);
        }
        if (removed) {
            saveUsersToFile();
            return true;
        }
        return removed;
    }

    public synchronized boolean addFriend(User user1, User user2) {
        boolean isFriend1 = false;
        boolean isFriend2 = false;
        for (User user : user1.getFriendList()) {
            if (user1.getName().equals(user2.getName())) {
                isFriend1 = true;
                break;
            }
        }
        for (User user : user2.getFriendList()) {
            if (user1.getName().equals(user.getName())) {
                isFriend2 = true;
                break;
            }
        }
        if (!isFriend1 && !isFriend2) {
            user1.addFriend(user2);
            return true;
        }
        return false;
    }

    public synchronized boolean removeFriend(User user1, User user2) {
        boolean isFriend1 = false;
        boolean isFriend2 = false;
        for (User user : user1.getFriendList()) {
            if (user2.getName().equals(user.getName())) {
                isFriend1 = true;
                break;
            }
        }
        for (User user : user2.getFriendList()) {
            if (user1.getName().equals(user.getName())) {
                isFriend2 = true;
                break;
            }
        }
        if (isFriend1 && isFriend2) {
            user1.removeFriend(user2);
            user2.removeFriend(user1);
            return true;
        }
        return false;
    }

    public synchronized boolean usernameExists(String username) {
        for (User user : allUsers) {
            if (user.getName().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public synchronized User findUserByName(String name) {
        for (User user : allUsers) {
            if (user.getName().equals(name)) {
                return user;
            }
        }
        return null; // User not found
    }

    public ArrayList<User> getAllUsers() {
        synchronized (lock) {
            return allUsers;
        }
    }

    public synchronized void loadUsersFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            ArrayList<User> users = new ArrayList<>();
            while ((line = br.readLine()) != null) {

                String[] data = line.split(",");
                String name = data[0];
                String password = data[1];
                String email = data[2];
                String phoneNumber = data[3];
                String description = data[4];
                String university = data[5];

                User user = new User(name, password, email, phoneNumber, description, university);
                loadProfilePicture(user);
                users.add(user);
            }
            allUsers = users;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UsernameTakenException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveProfilePicture(User user) {
        byte[] pictureData = user.getProfilePicture();
        if (pictureData == null) {
            return;
        }
        File pictureFile = new File(PROFILE_PICTURE_FOLDER, user.getName() + ".png");
        try (FileOutputStream fos = new FileOutputStream(pictureFile)) {
            fos.write(pictureData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadProfilePicture(User user) {
        File pictureFile = new File(PROFILE_PICTURE_FOLDER, user.getName() + ".png");
        if (pictureFile.exists()) {
            try (FileInputStream fis = new FileInputStream(pictureFile)) {
                byte[] pictureData = fis.readAllBytes();
                user.setProfilePicture(pictureData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteProfilePicture(User user) {
        File pictureFile = new File(PROFILE_PICTURE_FOLDER, user.getName() + ".png");
        if (pictureFile.exists()) {
            pictureFile.delete();
        }
    }

    public synchronized void saveUsersToFile() {
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(USERS_FILE))) {
            for (User user : allUsers) {
                pw.println(user);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized ArrayList<User> loadFriendsFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(FRIENDS_FILE))) {
            String line;
            ArrayList<User> friendList = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(":");
                if (tokens.length == 2) {
                    String username = tokens[0];
                    String[] friends = tokens[1].split(",");

                    User user = findUserByName(username);

                    if (user != null) {
                        for (String friend : friends) {
                            User friendUser = findUserByName(friend);
                            if (friendUser != null) {
                                friendList.add(friendUser);
                            }
                        }
                    }
                }
            }
            return friendList;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public synchronized void saveFriendsToFile() {
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(FRIENDS_FILE))) {
            for (User user : allUsers) {
                String line = user.getName() + ":";

                for (User friend : user.getFriendList()) {
                    line += friend.getName() + ",";
                }

                if (line.endsWith(",")) {
                    line = line.substring(0, line.length() - 1);
                }
                pw.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized ArrayList<User> loadBlockedFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(BLOCKED_FILE))) {
            String line;
            ArrayList<User> blockedList = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(":");
                if (tokens.length == 2) {
                    String username = tokens[0];
                    String[] blocked = tokens[1].split(",");

                    User user = findUserByName(username);

                    if (user != null) {
                        for (String block : blocked) {
                            User blockedUser = findUserByName(block);
                            if (blockedUser != null) {
                                blockedList.add(blockedUser);
                            }
                        }
                    }
                }
            }
            return blockedList;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public synchronized void saveBlockedToFile() {
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(BLOCKED_FILE))) {
            for (User user : allUsers) {
                String line = user.getName() + ":";

                for (User friend : user.getFriendList()) {
                    line += friend.getName() + ",";
                }

                if (line.endsWith(",")) {
                    line = line.substring(0, line.length() - 1);
                }
                pw.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void addFriendRequest(User sender, User receiver) {
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(FRIEND_REQUESTS_FILE, true))) {
            pw.println(sender.getName() + ":" + receiver.getName() + ":PENDING");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Updates the status of a friend request (ACCEPTED or DECLINED)
    public synchronized void updateFriendRequestStatus(User sender, User receiver, String status) {
        ArrayList<String> requests = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FRIEND_REQUESTS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(":");
                if (tokens.length == 3) {
                    String currentSender = tokens[0];
                    String currentReceiver = tokens[1];
                    String currentStatus = tokens[2];

                    if (currentSender.equals(sender.getName()) && currentReceiver.equals(receiver.getName())) {
                        requests.add(currentSender + ":" + currentReceiver + ":" + status); // Update the status
                    } else {
                        requests.add(line); // Keep the original line if no match
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Write the updated requests back to the file
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(FRIEND_REQUESTS_FILE))) {
            for (String request : requests) {
                pw.println(request);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Loads all pending friend requests from the file
    public synchronized ArrayList<String> loadFriendRequestsFromFile() {
        ArrayList<String> friendRequests = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FRIEND_REQUESTS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                friendRequests.add(line); // Load each request as a line in the list
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return friendRequests;
    }

    public synchronized void recordMessages(String sender, String receiver, String message) {
        String log = String.format("%s,%s,%s", sender, receiver, message);
        try (PrintWriter pr = new PrintWriter(new FileOutputStream(MESSAGES_FILE, true))) {
            pr.println(log);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized ArrayList<String> loadConversation(String user1, String user2) {
        ArrayList<String> messages = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(MESSAGES_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",", 3);
                if (tokens.length == 3) {
                    String sender = tokens[0];
                    String receiver = tokens[1];

                    if ((sender.equals(user1) && receiver.equals(user2))
                            || (sender.equals(user2) && receiver.equals(user1))) {
                        messages.add(tokens[2]);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return messages;
    }
}
