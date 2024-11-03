import java.util.ArrayList;

public class UserSearch implements Searchable {

    private static final Object lock = new Object();
    
    public ArrayList<User> searchParameter (String parameter, String value) {
        ArrayList<User> matchingUsers = new ArrayList<>();

        synchronized (lock) {

            for (User user : User.allUsers) {
                switch (parameter.toLowerCase()) {
                    case "name":
                        if (user.getName().equals(value)) {
                            matchingUsers.add(user);
                        }
                        break;
                    case "email":
                        if (user.getEmail().equalsIgnoreCase(value)) {
                            matchingUsers.add(user);
                        }
                        break;
                    case "phone":
                        if (user.getPhoneNumber().equals(value)) {
                            matchingUsers.add(user);
                        }
                        break;
                    case "university":
                        if (user.getDescription().equalsIgnoreCase(value)) {
                            matchingUsers.add(user);
                        }
                        break;
                    default:
                        System.out.println("Invalid parameter type");
                        break;
                }
            }
        }

        return matchingUsers;
    }

    public ArrayList<User> searchByParameter(String parameter, String value) {
        ArrayList<User> matchingUsers = new ArrayList<>();

        synchronized (lock) {
            for (User user : User.getAllUsers()) {
                if (mainUser != user && mainUser.perfectMatch(user)) {
                    matchingUsers.add(user);
                }
            }
        }
        return matchingUsers;
    }

    public ArrayList<User> exactMatch(Profile mainProfile) {
        ArrayList<User> matchingUsers = new ArrayList<>();

        synchronized (lock) {
            for (User user : User.getAllUsers()) {
                if (mainProfile != user && mainProfile.perfectMatch(user)) {
                    matchingUsers.add(user);
                }
            }
        }
        return matchingUsers;
    }
    
    public ArrayList<User> partialMatch (Profile mainProfile) {
        ArrayList<User> matchingUsers = new ArrayList<>();
        for (int i = 5; i > 0 ; i--) {
            for (User user : User.getAllUsers()) {
                if (!(mainProfile.getName().equals(user.getName()))) {
                    if (mainProfile.partial(user) == i) {
                        matchingUsers.add(user);
                    }
                }
            }
        }
       return matchingUsers;
    }
}
