import java.util.ArrayList;

/**
 * Team Project Phase 1 - CoHabit
 * <p>
 * This program works to implement a roommate search algorithm
 *
 * @author Aidan Lefort, Andrew Tang, Keya Jadhav, Rithvik Siddenki, Rui Meng
 * @version November 3rd, 2024
 */

public class UserSearch implements Searchable {

    private Database db;

    //Constructs a newly allocated UserSearch object with a reference to the database.
    public UserSearch() {
        this.db = new Database();
    }

    //Searches for users by name, university, etc and returns a list of matching users.
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

    //Returns ArrayList of all users who are an exact match based on the search
    public ArrayList<User> exactMatch(User mainUser) {
        ArrayList<User> results = new ArrayList<>();
        for (int i = 0; i < db.getAllUsers().size(); i++) {
            if (mainUser.perfectMatch(db.getAllUsers().get(i))) {
                results.add(db.getAllUsers().get(i));
            }
        }
        return results;
    }

    //Returns ArrayList of all users who are a partial match based on the search
    public ArrayList<User> partialMatch(User mainUser) {
        ArrayList<User> results = new ArrayList<>();
        for (int i = 5; i > 0; i--) {
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
