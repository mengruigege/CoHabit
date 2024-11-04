/**
 * Team Project Phase 1 - CoHabit
 *
 * This program works to implement a roommate search algorithm
 *
 * @author Aidan Lefort, Andrew Tang, Keya Jadhav, Rithvik Siddenki, Rui Meng 
 *
 * @version November 3rd, 2024
 *
 */

public interface Message {
    User getSender();
    User getReceiver();
    String getTimestamp();
    MessageType getMessageType();
    String getContentPreview();  // Provides a brief representation of the content
}
