import java.util.*;

public interface Searchable {
    ArrayList<User> searchByParameter(String parameter, String value); // search by name/email/phone etc.
    ArrayList<User> exactMatch(User user); 
    ArrayList<User> partialMatch(User user); 
}
