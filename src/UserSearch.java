import java.util.ArrayList;

public class UserSearch implements Search{
    public ArrayList<User> searchParameter (String parameter, String value) {
        ArrayList<User> matchingUsers = new ArrayList<>();

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
                    if (user.getPhoneNum().equals(value)) {
                        matchingUsers.add(user);
                    }
                    break;
                case "university":
                    if (user.getUni().equalsIgnoreCase(value)) {
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

    public ArrayList<User> ExactMatch (User mainUser){
        ArrayList<User> matchingUsers = new ArrayList<>();
        for (User user : User.getAllUsers()) {
            if (mainUser != user && mainUser.perfectMatch(user)) {
                matchingUsers.add(user);
            }
        }
        return matchingUsers;

    }

    //partialMatch





}
