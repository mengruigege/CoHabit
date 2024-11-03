import java.util.ArrayList;

public class UserSearch implements Searchable {

    private static final Object lock = new Object();

    private class SearchParameterTask implements Runnable {
        private final String parameter;
        private final String value;
        private final ArrayList<User> matchingUsers;

        public SearchParameterTask(String parameter, String value, ArrayList<User> matchingUsers) {
            this.parameter = parameter;
            this.value = value;
            this.matchingUsers = matchingUsers;
        }

        public void run() {
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
        }
    }

    public ArrayList<User> searchParameter(String parameter, String value) {
        ArrayList<User> matchingUsers = new ArrayList<>();
        Thread thread = new Thread(new SearchParameterTask(parameter, value, matchingUsers));
        thread.start();
        try {
            thread.join();  
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return matchingUsers;
    }

    public ArrayList<User> searchByParameter(String parameter, String value) {
        ArrayList<User> results = new ArrayList<>();
        Thread thread = new Thread(new SearchParameterTask(parameter, value, results));
        thread.start();
        try {
            thread.join(); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); 
        }
        return results;
    }

    public ArrayList<User> exactMatch(Profile mainProfile) {
        ArrayList<User> results = new ArrayList<>();
        Thread thread = new Thread(new Runnable() {
            public void run() {
                synchronized (lock) {
                    for (User user : User.getAllUsers()) {
                        if (mainProfile != user && mainProfile.perfectMatch(user)) {
                            results.add(user);
                        }
                    }
                }
            }
        });
        thread.start();
        try {
            thread.join();  
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); 
        }
        return results;
    }

    public ArrayList<User> partialMatch(Profile mainProfile) {
        ArrayList<User> results = new ArrayList<>();
        Thread thread = new Thread(new Runnable() {
            public void run() {
                for (int i = 5; i > 0; i--) {
                    synchronized (lock) {
                        for (User user : User.getAllUsers()) {
                            if (!(mainProfile.getName().equals(user.getName()))) {
                                if (mainProfile.partial(user) == i) {
                                    results.add(user);
                                }
                            }
                        }
                    }
                }
            }
        });
        thread.start();
        try {
            thread.join();  
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); 
        }
        return results;
    }
}
