import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static Database database = new Database();

    // method to check if the login was successful or not
    public static boolean login(String username, String password) {
        //might have to make getAllUsers in database static
        User user;
        user = database.findUserByName(username);
        if (user.getPassword().equals(password)) {
            return true;
        }
        return false;
    }

    // method used to add the new register user to the database and return true if successful
    public boolean register(User user) {
        database.addUser(user);
        return true;
    }

    public static boolean sendMessage(User sender, User reciever, String message) {
        String senderName = sender.getName();
        if (reciever == null) {
            return false;
        }
        String recieverName = reciever.getName();
        database.recordMessages(senderName, recieverName, message);
        return true;
    }

    public static boolean addFriend(User user, User friend) {
        if (friend == null) {
            return false;
        }
        database.addFriend(user, friend);   //methods have same name might want to change
        return true;
    }

    public static String viewProfile(String username) {
        User user = database.findUserByName(username);
        return user.toString();
    }

    public static void main(String[] args) {

        database.loadUsersFromFile();
        User currentUser;
        // some way to read all data that already exists in the database
        //open the ServerSocket and use the specific port
        try (ServerSocket serverSocket = new ServerSocket(1102)) {
            while (true) {
                try (Socket socket = serverSocket.accept();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
                    while (true) {
                        String line = reader.readLine();
                        String[] parts = line.split(",");
                        // this is the main part that help to decide what to do with information of line
                        //"login, username, password,
                        if (line.contains("login")) {
                            String username = parts[1];
                            String password = parts[2];
                            if (Server.login(username, password)) { // not sure about this
                                writer.println("Sucessful login");
                            } else writer.println("Wrong username or password");

                        }
                        // should be in format sendMessage,sender,reciever,message
                        if (line.contains("sendMessage")) {
                            User sender = database.findUserByName(parts[1]);
                            User receiver = database.findUserByName(parts[2]);
                            String message = parts[3];
                            if (Server.sendMessage(sender, receiver, message)) {
                                writer.println("Successfully sent message"); //not sure what the output is here
                            } else {
                                writer.println("Something went wrong");
                            }
                        }
                        // should be in format addFriend,user,friend
                        if (line.contains("addFriend")) {
                            User user = database.findUserByName(parts[1]);
                            User friend = database.findUserByName(parts[2]);
                            if (Server.addFriend(user, friend)) {
                                writer.println("Successfully added friend");
                            } else {
                                writer.println("Entered a nonexistent friend");
                            }
                        }


                        // format is viewProfile,username
                        if (line.contains("viewProfile")) {
                            String username = parts[1];
                            writer.println(Server.viewProfile(username));
                        }

                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}



