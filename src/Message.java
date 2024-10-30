public interface Message {
    public boolean sendMessage(User sender, User receiver); //method for sending messages
    public String[] getMessageLogs(); //need to implement some way of storing messages between two users; Chat object?
    public void setMessageLogs(String[] messages); //set method for list of messages
    public boolean deleteMessage(String message); //removes a certain message from list of messages
}
