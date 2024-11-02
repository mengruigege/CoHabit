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
                        if (user.getPhoneNum().equals(value)) {
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
    

    public ArrayList<User> exactMatch(User mainUser) {
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
    
    public ArrayList<User> partialMatch (User mainUser) {
        ArrayList<User> matchingUsers = new ArrayList<>();
        for (int i = 5; i > 0 ; i--) {
            for (User user : User.getAllUsers()) {
                if (!(mainUser.getName().equals(user.getName()))) {
                    if (mainUser.partial(user) == i) {
                        matchingUsers.add(user);
                    }
                }
            }
        }
       return matchingUsers;
    }

    //partialMatch - added this method, someone please cross check 
/** public ArrayList<User> partialMatch(String parameter, String value) {
        ArrayList<User> matchingUsers = new ArrayList<>();

        synchronized (lock) {
            for (User pref : User.getAllUsers()) {
                boolean isMatch = false;

                switch (parameter.toLowerCase()) {
                    case "name":
                        isMatch = pref.getName().toLowerCase().contains(value.toLowerCase());
                        break;
                    case "email":
                        isMatch = pref.getEmail().toLowerCase().contains(value.toLowerCase());
                        break;
                    case "phone":
                        isMatch = pref.getPhoneNum().contains(value);
                        break;
                    case "university":
                        isMatch = pref.getUni().toLowerCase().contains(value.toLowerCase());
                        break;
                    default:
                        System.out.println("Invalid parameter type");
                        break;
                }

                if (isMatch) {
                    matchingUsers.add(pref);
                }
            }
        }

        return matchingUsers;
    }
}
*/ 



 

}
