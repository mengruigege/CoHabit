public class FriendRequest {
    private User sender;
    private User receiver;
    private RequestStatus status;

    public enum RequestStatus {
        PENDING,
        ACCEPTED,
        DECLINED
    }

    public FriendRequest(User sender, User receiver) {
        this.sender = sender;
        this.receiver = receiver;
        this.status = RequestStatus.PENDING;
    }

    public User getSender() {
        return sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void accept() {
        this.status = RequestStatus.ACCEPTED;
    }

    public void decline() {
        this.status = RequestStatus.DECLINED;
    }

    @Override
    public String toString() {
        return "FriendRequest from " + sender.getName() + " to " + receiver.getName() + " - Status: " + status;
    }
}
