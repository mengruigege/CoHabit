/**
 * Team Project Phase 1 - CoHabit
 * <p>
 * This program works to implement a roommate search algorithm
 *
 * @author Aidan Lefort, Andrew Tang, Keya Jadhav, Rithvik Siddenki, Rui Meng
 * @version November 3rd, 2024
 */

public class UsernameTakenException extends Exception {

    //Constructs a new exception indicating a taken username.
    public UsernameTakenException(String message) {
        super(message);
    }
}
