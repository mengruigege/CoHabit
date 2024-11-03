import java.util.*;

public interface Searchable {
    ArrayList<User> searchByParameter(String parameter, String value); // search by name/email/phone etc.
    ArrayList<User> exactMatch(Profile profile); // search by profile entered
    ArrayList<User> partialMatch(Profile profile); // search by profile entered
}
