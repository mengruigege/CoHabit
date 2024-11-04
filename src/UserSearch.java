import java.util.ArrayList;

public class UserSearch implements Searchable {

    private Database db;

    public UserSearch () {
        this.db = new Database();
    }

    public ArrayList<User> searchByParameter(String parameter, String value) {
        ArrayList<User> matchingUsers = new ArrayList<>();


            for (int i = 0; i < db.getAllUsers().size(); i++) {
                switch (parameter.toLowerCase()) {
                    case "name":
                        if (db.getAllUsers().get(i).getName().equals(value)) {
                            matchingUsers.add(db.getAllUsers().get(i));
                        }
                        break;
                    case "email":
                        if (db.getAllUsers().get(i).getEmail().equalsIgnoreCase(value)) {
                            matchingUsers.add(db.getAllUsers().get(i));
                        }
                        break;
                    case "phone":
                        if (db.getAllUsers().get(i).getPhoneNumber().equals(value)) {
                            matchingUsers.add(db.getAllUsers().get(i));
                        }
                        break;
                    case "university":
                        if (db.getAllUsers().get(i).getUniversity().equals(value)) {
                            matchingUsers.add(db.getAllUsers().get(i));
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
        for (int i = 0; i < db.getAllUsers().size(); i++) {
            if (mainUser.perfectMatch(db.getAllUsers().get(i))) {
                results.add(db.getAllUsers().get(i));
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
