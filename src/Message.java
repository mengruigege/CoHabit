public interface Message {
    User getSender();
    User getReceiver();
    String getTimestamp();
    MessageType getMessageType();
    String getContentPreview();  // Provides a brief representation of the content
}
