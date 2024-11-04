/**
 * Team Project Phase 1 - CoHabit
 * <p>
 * This program works to implement a roommate search algorithm
 *
 * @author Aidan Lefort, Andrew Tang, Keya Jadhav, Rithvik Siddenki, Rui Meng
 * @version November 3rd, 2024
 */

public interface Profile { //boolean return type to check for success
    public String getName(); //getters

    public String getDescription();

    public String getUniversity();

    public void setName(String newName); //setters

    public void setDescription(String newDesc);

    public void setUniversity(String newUni);
}
