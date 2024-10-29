public interface MessageInterface {
    public boolean sendMessage(User sender, User receiver);
    public String[] getMessageLogs(); //need to implement some way of storing messages between two users; Chat object?
    public void setMessageLogs(String[] messages);
    public boolean deleteMessage(String message);
}
