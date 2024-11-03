import java.util.ArrayList;

public class UserSearch implements Searchable {

    private Database db = new Database();

    public ArrayList<User> searchByParameter(String parameter, String value) {
        ArrayList<User> matchingUsers = new ArrayList<>();


            for (User user : db.getAllUsers()) {
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

        return matchingUsers;
    }

    public ArrayList<User> exactMatch(User mainUser) {
        ArrayList<User> results = new ArrayList<>();
        for (User user : db.getAllUsers()) {
            if (mainUser != user && mainUser.perfectMatch(user)) {
                results.add(user);
            }
        }
        return results;
    }
    
    public ArrayList<User> partialMatch(User mainUser) {
        ArrayList<User> results = new ArrayList<>();
        for (int i = 5; i > 0 ; i--) {
            for (User user : db.getAllUsers()) {
                if (!(mainUser.getName().equals(user.getName()))) {
                    if (mainUser.partialMatch(user) == i) {
                        results.add(user);
                    }
                }
            }
        }
       return results;
    }
}
