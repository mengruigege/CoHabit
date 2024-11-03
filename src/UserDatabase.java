import java.util.*;
import java.io.*;

public class UserDatabase {
    private String databaseOutput;
    private ArrayList<User> users;
    private String userIn;

    public UserDatabase(String userIn, String databaseOutput) {
        this.databaseOutput = databaseOutput;
        this.userIn = userIn;
        this.users = new ArrayList<>();
    }

    public synchronized boolean readUser() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(userIn));
            int count = 0;
            String line = "";
            String userName = "";
            String pwd = "";
            String email = "";
            String phoneNum = "";
            String desc = "";
            String uni = "";
            String bedtime = "";
            String alcohol = "";
            String smoke = "";
            String guests = "";
            String tidyS = "";
            String roomHoursS = "";
            int index = 0;

            while ((line = reader.readLine()) != null) {
                try {
                    index++;
                    String[] data = line.split(","); 
                    userName = data[0];
                    pwd = data[1];
                    email = data[2];
                    phoneNum = data[3];
                    desc = data[4];
                    uni = data[5];
                    bedtime = data[6];
                    alcohol = data[7];
                    smoke = data[8];
                    guests = data[9];
                    tidyS = data[10];
                    roomHoursS = data[11];
                    int tidyI = Integer.parseInt(tidyS);
                    int roomHoursI = Integer.parseInt(roomHoursS);
                    
                    users.add(new User(userName, pwd, email, phoneNum, desc, uni));
                    if (alcohol.equals("true") && smoke.equals("true") && guests.equals("true")) {
                        users.get(index).setPreferences(bedtime, true, true, true, tidyI, roomHoursI);
                    } else if (alcohol.equals("true") && smoke.equals("true") && guests.equals("false")) {
                        users.get(index).setPreferences(bedtime, true, true, false, tidyI, roomHoursI);
                    } else if (alcohol.equals("true") && smoke.equals("false") && guests.equals("false")) {
                        users.get(index).setPreferences(bedtime, true, false, false, tidyI, roomHoursI);
                    } else if (alcohol.equals("false") && smoke.equals("false") && guests.equals("true")) {
                        users.get(index).setPreferences(bedtime, false, false, true, tidyI, roomHoursI);
                    } else if (alcohol.equals("false") && smoke.equals("true") && guests.equals("true")) {
                        users.get(index).setPreferences(bedtime, false, true, true, tidyI, roomHoursI);
                    } else if (alcohol.equals("false") && smoke.equals("true") && guests.equals("false")) {
                        users.get(index).setPreferences(bedtime, false, true, false, tidyI, roomHoursI);
                    }
                } catch (Exception e) { //should we have another catch for IndexOutOfBounds to specify
                    return false;
                }
            }
            reader.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public synchronized boolean writeUser() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(databaseOutput));

            for (int i = 0; i < users.size(); i++) {
                writer.write(users.get(i).toString());
            }
            writer.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void startReading() {
        Thread readerThread = new Thread(new UserReader());
        readerThread.start(); 
    }

    public void startWriting() {
        Thread writerThread = new Thread(new UserWriter());
        writerThread.start(); 
    }

    private class UserReader implements Runnable {
        public void run() {
            readUser(); 
        }
    }

    private class UserWriter implements Runnable {
        @Override
        public void run() {
            writeUser(); 
        }
    }

}
