public class UsernameTakenException extends Exception {

    //Constructs a new exception indicating a taken username.
    public UsernameTakenException(String message) { 
        super(message); 
    }
}
