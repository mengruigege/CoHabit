import java.util.*;

/**
 * Team Project Phase 1 - CoHabit
 * <p>
 * This program works to implement a roommate search algorithm
 *
 * @author Aidan Lefort, Andrew Tang, Keya Jadhav, Rithvik Siddenki, Rui Meng
 * @version November 3rd, 2024
 */

public interface Searchable {
    ArrayList<User> searchByParameter(String parameter, String value); // search by name/email/phone etc.

    ArrayList<User> exactMatch(User user);

    ArrayList<User> partialMatch(User user);
}
