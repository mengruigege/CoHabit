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
    String searchByParameter(String parameter, String value, String delimiter); // search by name/email/phone etc.

    String exactMatch(User user, String delimiter);

    String partialMatch(User user, String delimiter);
}
