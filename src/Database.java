import java.util.ArrayList;
import java.io.*;

public class Database {
    private ArrayList<User> allUsers;
    private static final String USERS_FILE = "users.txt";
    private static final String FRIENDS_FILE = "friends.txt";
    private static final String MESSAGES_FILE = "messages.txt";

    public UserDatabase() {
        this.allUsers = new ArrayList<>();
        loadUsersFromFile();
    }

    public boolean addUser(User user) {
        if (user != null && !usernameExists(user.getName())) {
            allUsers.add(user);
            saveUsersToFile();
            return true;
        }
        return false; // User is null or username is taken
    }

    public boolean deleteUser(User user) {
        boolean removed = allUsers.remove(user);
        if (removed) {
            saveUsersToFile();
            return true;
        }
        return removed;
    }

    public boolean addFriend(User user1, User user2) {
        if (user1.addFriend(user2) && user2.addFriend(user1)) {
            saveFriendsToFile();
            return true;
        }
        return false;
    }

    public boolean usernameExists(String username) {
        for (User user : allUsers) {
            if (user.getName().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public User findUserByName(String name) {
        for (User user : allUsers) {
            if (user.getName().equals(name)) {
                return user;
            }
        }
        return null; // User not found
    }

    public ArrayList<User> getAllUsers() {
        return new ArrayList<>(allUsers);
    }

    private void loadUsersFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length == 6) {
                    String name = tokens[0];
                    String password = tokens[1];
                    String email = tokens[2];
                    String phoneNumber = tokens[3];
                    String description = tokens[4];
                    String university = tokens[5];

                    User user = new User(name, password, email, phoneNumber, description, university);
                    allUsers.add(user);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UsernameTakenException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveUsersToFile() {
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(USERS_FILE))) {
            for (User user : allUsers) {
                pw.println(user);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFriendsFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(FRIENDS_FILE))) {
            String line;
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
                                friendUser.addFriend(user);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveFriendsToFile() {
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(FRIENDS_FILE))) {
            for (User user : allUsers) {
                String line = user.getName() + ":";

                for (User friend : user.getFriends()) {
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


    public void recordMessages(String sender, String receiver, String message, String timestamp) {
        String log = String.format("%s,%s,%s,%s", sender, receiver, timestamp, message);
        try (PrintWriter pr = new PrintWriter(new FileOutputStream(MESSAGES_FILE, true))) {
            pr.println(log);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> loadConversation(String user1, String user2) {
        ArrayList<String> messages = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(MESSAGES_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String [] tokens = line.split(",", 4);
                if (tokens.length == 4) {
                    String sender = tokens[0];
                    String receiver = tokens[1];

                    if ((sender.equals(user1) && receiver.equals(user2)) || (sender.equals(user2) && receiver.equals(user1))) {
                        messages.add(line);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return messages;
    }
}
