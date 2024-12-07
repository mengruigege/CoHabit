import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;

/**
 * Team Project Phase 2 - CoHabit
 * <p>
 * This program works to implement a roommate search algorithm
 *
 * @author Aidan Lefort, Andrew Tang, Keya Jadhav, Rithvik Siddenki, Rui Meng
 * @version November 3rd, 2024
 */

public class Client implements ClientService {

    String username;
    String password;
    String email;
    String phoneNumber;
    String userDescription;
    String university;
    private byte[] profilePicture;

    String bedTime;
    boolean alcohol;
    boolean smoke;
    boolean guests;
    int tidy;
    int roomHours;

    boolean isConnected;
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private Scanner scanner;

    private final String serverAddress = "localhost";
    private final int serverPort = 1102;

    private static final String DELIMITER = "<<END>>";
    private static final String SUCCESS = "SUCCESS";
    private static final String FAILURE = "FAILURE";

    //constructor
    public Client(String serverAddress, int serverPort) {
        try {
            socket = new Socket(serverAddress, serverPort);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            scanner = new Scanner(System.in);
            isConnected = true;
        } catch (UnknownHostException e) {
            JOptionPane.showMessageDialog(null, "Unknown Host: " + serverAddress + ". Please check the server address and port", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SocketException e) {
            JOptionPane.showMessageDialog(null, "Not connected to server.", "Error", JOptionPane.ERROR_MESSAGE);
            isConnected = false;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "An IO error occurred while trying to connect to the server.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        Client client = new Client("localhost", 1102);
        client.start();
    }

    public void start() {
        if (!isConnected) {
            return;
        }

        while (true) {
            // Create main menu panel
            JPanel mainMenuPanel = new JPanel(new GridLayout(0, 1, 10, 10));
            mainMenuPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JLabel titleLabel = new JLabel("Welcome to CoHabit", JLabel.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
            mainMenuPanel.add(titleLabel);

            JButton loginButton = new JButton("Login");
            JButton registerButton = new JButton("Register");
            JButton exitButton = new JButton("Exit");

            mainMenuPanel.add(loginButton);
            mainMenuPanel.add(registerButton);
            mainMenuPanel.add(exitButton);

            // Show the main menu dialog
            int[] optionSelected = {0}; // 0: No option, 1: Login, 2: Register, 3: Exit
            JDialog mainMenuDialog = new JDialog((Frame) null, "Main Menu", true);
            mainMenuDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            mainMenuDialog.getContentPane().add(mainMenuPanel);
            mainMenuDialog.pack();
            mainMenuDialog.setLocationRelativeTo(null);

            // Button actions
            loginButton.addActionListener(e -> {
                optionSelected[0] = 1; // Login selected
                mainMenuDialog.dispose(); // Close dialog
            });

            registerButton.addActionListener(e -> {
                optionSelected[0] = 2; // Register selected
                mainMenuDialog.dispose(); // Close dialog
            });

            exitButton.addActionListener(e -> {
                optionSelected[0] = 3; // Exit selected
                mainMenuDialog.dispose(); // Close dialog
            });

            // Display the dialog and wait for user selection
            mainMenuDialog.setVisible(true);

            // Process the user selection
            switch (optionSelected[0]) {
                case 1: // Login
                    if (login()) {
                        if (!mainScreen()) { // Proceed to main screen if login is successful
                            return; // Exit the application if the main screen is closed
                        }
                    }
                    break;
                case 2: // Register
                    if (register()) {
                        if (!mainScreen()) { // Proceed to main screen if registration is successful
                            return; // Exit the application if the main screen is closed
                        }
                    }
                    break;
                case 3: // Exit
                    close(); // Close resources and exit
                    return;
                default:
                    JOptionPane.showMessageDialog(null, "No valid option selected. Exiting.", "Exit", JOptionPane.INFORMATION_MESSAGE);
                    close(); // Close resources and exit
                    return;
            }
        }
    }

    public void close() {
        try {
            if (socket != null) socket.close();
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (scanner != null) scanner.close();
        } catch (IOException e) {
            System.out.println("Could not close client" + e.getMessage());
        }
    }

    public boolean mainScreen() {
        if (!isConnected) {
            JOptionPane.showMessageDialog(null, "Not connected to server.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        JFrame frame = new JFrame("User Dashboard");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout(10, 10));

        // Header Panel with Welcome Message
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 120, 215));
        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        headerPanel.add(welcomeLabel, BorderLayout.CENTER);
        frame.add(headerPanel, BorderLayout.NORTH);

        // Center Panel for Profile
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(BorderFactory.createTitledBorder("Profile Summary"));

        // Profile Picture Section
        JLabel profilePictureLabel = new JLabel();
        profilePictureLabel.setHorizontalAlignment(JLabel.CENTER);
        profilePictureLabel.setVerticalAlignment(JLabel.CENTER);
        if (profilePicture != null && profilePicture.length > 0) {
            try {
                ImageIcon icon = new ImageIcon(profilePicture);
                Image scaledImage = icon.getImage().getScaledInstance(210, 200, Image.SCALE_SMOOTH);
                profilePictureLabel.setIcon(new ImageIcon(scaledImage));
            } catch (Exception e) {
                profilePictureLabel.setText("Error loading picture");
            }
        } else {
            profilePictureLabel.setText("No Profile Picture Available");
        }
        centerPanel.add(profilePictureLabel, BorderLayout.NORTH);

        // User Details Section (Two Columns)
        JPanel detailsPanel = new JPanel(new GridLayout(1, 2, 20, 10));
        JPanel infoPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("User Information"));
        infoPanel.add(new JLabel("Username: " + username));
        infoPanel.add(new JLabel("Email: " + email));
        infoPanel.add(new JLabel("Phone: " + phoneNumber));
        infoPanel.add(new JLabel("University: " + university));
        infoPanel.add(new JLabel("Description: " + userDescription));
        JPanel preferencesPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        preferencesPanel.setBorder(BorderFactory.createTitledBorder("Room Preferences"));
        preferencesPanel.add(new JLabel("Tidy Level: " + tidy + "/10"));
        preferencesPanel.add(new JLabel("Hours in Room: " + roomHours + " hrs/day"));
        preferencesPanel.add(new JLabel("Alcohol: " + (alcohol ? "Allowed" : "Not Allowed")));
        preferencesPanel.add(new JLabel("Smoke: " + (smoke ? "Allowed" : "Not Allowed")));
        preferencesPanel.add(new JLabel("Guests: " + (guests ? "Allowed" : "Not Allowed")));

        detailsPanel.add(infoPanel);
        detailsPanel.add(preferencesPanel);
        centerPanel.add(detailsPanel, BorderLayout.CENTER);

        frame.add(centerPanel, BorderLayout.CENTER);

        // Left Sidebar for Actions
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Actions"));

        // Communication Category
        JPanel communicationPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        communicationPanel.setBorder(BorderFactory.createTitledBorder("Communication"));
        JButton sendMessageButton = new JButton("Send Message");
        JButton viewMessageButton = new JButton("View Messages");
        sendMessageButton.addActionListener(e -> sendMessage());
        viewMessageButton.addActionListener(e -> viewMessage());
        communicationPanel.add(sendMessageButton);
        communicationPanel.add(viewMessageButton);

        // Friendship Management Category
        JPanel friendshipPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        friendshipPanel.setBorder(BorderFactory.createTitledBorder("Friendship Management"));
        JButton viewFriendListButton = new JButton("Friends");
        JButton viewFriendRequestsButton = new JButton("View Friend Requests");
        JButton sendFriendRequestButton = new JButton("Send Friend Request");
        JButton removeFriendButton = new JButton("Remove Friend");
        viewFriendListButton.addActionListener(e -> viewFriendList());
        viewFriendRequestsButton.addActionListener(e -> viewFriendRequests());
        sendFriendRequestButton.addActionListener(e -> sendFriendRequest());
        removeFriendButton.addActionListener(e -> removeFriend());
        friendshipPanel.add(viewFriendListButton);
        friendshipPanel.add(viewFriendRequestsButton);
        friendshipPanel.add(sendFriendRequestButton);
        friendshipPanel.add(removeFriendButton);

        // Block Management Category
        JPanel blockPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        blockPanel.setBorder(BorderFactory.createTitledBorder("Block Management"));
        JButton viewBlockListButton = new JButton("Blocked Users");
        JButton blockUserButton = new JButton("Block User");
        JButton unblockUserButton = new JButton("Unblock User");
        viewBlockListButton.addActionListener(e -> viewBlockList());
        blockUserButton.addActionListener(e -> blockUser());
        unblockUserButton.addActionListener(e -> unblockUser());
        blockPanel.add(viewBlockListButton);
        blockPanel.add(blockUserButton);
        blockPanel.add(unblockUserButton);

        // Roommate Search Category
        JPanel searchPanel = new JPanel(new GridLayout(1, 1, 5, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Roommate Search"));
        JButton searchRoommatesButton = new JButton("Search Roommates");
        searchRoommatesButton.addActionListener(e -> searchRoommates());
        searchPanel.add(searchRoommatesButton);

        // Add all categories to the left panel
        leftPanel.add(communicationPanel);
        leftPanel.add(friendshipPanel);
        leftPanel.add(blockPanel);
        leftPanel.add(searchPanel);

        frame.add(leftPanel, BorderLayout.WEST);

        // Footer Panel with Profile & Account Actions
        JPanel footerPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        footerPanel.setBorder(BorderFactory.createTitledBorder("Profile & Account"));

        JButton viewProfileButton = new JButton("View Profile");
        JButton updateProfileButton = new JButton("Update Profile");
        JButton disconnectButton = new JButton("Disconnect and Exit");

        viewProfileButton.addActionListener(e -> viewProfile());
        updateProfileButton.addActionListener(e -> updateProfile());
        disconnectButton.addActionListener(e -> {
            disconnect();
            frame.dispose();
        });

        footerPanel.add(viewProfileButton);
        footerPanel.add(updateProfileButton);
        footerPanel.add(disconnectButton);

        frame.add(footerPanel, BorderLayout.SOUTH);

        // Show the frame
        frame.setVisible(true);

        // Keep the main thread alive while GUI is active
        while (frame.isDisplayable()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return false; // Indicate the main screen is closed
    }

    public boolean login() {
        if (!isConnected) {
            JOptionPane.showMessageDialog(null, "Not connected to server.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Create a Login Panel
        JPanel loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Components
        JLabel titleLabel = new JLabel("Login to CoHabit", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(15);

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(15);

        // Layout Settings
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        loginPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        loginPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        loginPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        loginPanel.add(passwordField, gbc);

        // Show Login Dialog
        int option = JOptionPane.showConfirmDialog(null, loginPanel, "Login", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option != JOptionPane.OK_OPTION) {
            return false; // User canceled login
        }

        String usernameInput = usernameField.getText().trim();
        String passwordInput = new String(passwordField.getPassword()).trim();

        // Validate Input
        if (usernameInput.isEmpty() || passwordInput.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Username and password cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Send login request
        writer.println("login" + DELIMITER + usernameInput + DELIMITER + passwordInput);

        try {
            String response = reader.readLine();
            if (response == null || response.equals(FAILURE)) {
                JOptionPane.showMessageDialog(null, "Login failed. Incorrect username or password.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Parse user information on success
            String[] userInfo = response.split(DELIMITER);
            if (userInfo.length != 12) {
                JOptionPane.showMessageDialog(null, "Error: Invalid response from server.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Set user data
            username = userInfo[0];
            password = userInfo[1];
            email = userInfo[2];
            phoneNumber = userInfo[3];
            userDescription = userInfo[4];
            university = userInfo[5];
            bedTime = userInfo[6];
            alcohol = Boolean.parseBoolean(userInfo[7]);
            smoke = Boolean.parseBoolean(userInfo[8]);
            guests = Boolean.parseBoolean(userInfo[9]);
            tidy = Integer.parseInt(userInfo[10]);
            roomHours = Integer.parseInt(userInfo[11]);

            // Check for profile picture
            String pictureData = reader.readLine();
            if (!pictureData.equals("NO_PICTURE")) {
                profilePicture = Base64.getDecoder().decode(pictureData);
            }
            return true;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error during login: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }


    public boolean register() {
        if (!isConnected) {
            JOptionPane.showMessageDialog(null, "Not connected to server.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Create Registration Form Panel
        JPanel registerPanel = new JPanel(new BorderLayout(10, 10));

        // Header label
        JLabel headerLabel = new JLabel("Register New Account", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        registerPanel.add(headerLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(12, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Input fields
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField descriptionField = new JTextField();
        JTextField universityField = new JTextField();
        JTextField bedTimeField = new JTextField();
        JCheckBox alcoholCheckBox = new JCheckBox("Yes");
        JCheckBox smokeCheckBox = new JCheckBox("Yes");
        JCheckBox guestsCheckBox = new JCheckBox("Yes");
        JTextField tidyField = new JTextField();
        JTextField roomHoursField = new JTextField();

        // Add components to form
        formPanel.add(new JLabel("Username:"));
        formPanel.add(usernameField);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Phone Number:"));
        formPanel.add(phoneField);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(descriptionField);
        formPanel.add(new JLabel("University:"));
        formPanel.add(universityField);
        formPanel.add(new JLabel("Bed Time (HH:MM):"));
        formPanel.add(bedTimeField);
        formPanel.add(new JLabel("Tidy Level (1-10):"));
        formPanel.add(tidyField);
        formPanel.add(new JLabel("Room Hours (1-24):"));
        formPanel.add(roomHoursField);
        formPanel.add(new JLabel("Do you drink alcohol?"));
        formPanel.add(alcoholCheckBox);
        formPanel.add(new JLabel("Do you smoke?"));
        formPanel.add(smokeCheckBox);
        formPanel.add(new JLabel("Guests allowed?"));
        formPanel.add(guestsCheckBox);

        registerPanel.add(formPanel, BorderLayout.CENTER);

        // Show the dialog
        int option = JOptionPane.showConfirmDialog(null, registerPanel, "Register", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option != JOptionPane.OK_OPTION) {
            return false; // User canceled registration
        }

        // Validate inputs and collect errors
        StringBuilder errors = new StringBuilder();
        try {
            username = usernameField.getText().trim();
            if (username.isEmpty() || username.contains(DELIMITER)) {
                errors.append("Invalid username.\n");
            }

            password = new String(passwordField.getPassword()).trim();
            if (password.isEmpty() || password.length() < 6) {
                errors.append("Password must be at least 6 characters.\n");
            }

            email = emailField.getText().trim();
            if (!email.matches("[^@]+@[^@]+\\.[^@]+")) {
                errors.append("Invalid email format.\n");
            }

            phoneNumber = phoneField.getText().trim();
            if (!phoneNumber.matches("\\d{10}")) {
                errors.append("Phone number must be 10 digits.\n");
            }

            userDescription = descriptionField.getText().trim();
            if (userDescription.isEmpty() || userDescription.contains(DELIMITER)) {
                errors.append("Invalid description.\n");
            }

            university = universityField.getText().trim();
            if (university.isEmpty() || university.contains(DELIMITER)) {
                errors.append("Invalid university.\n");
            }

            bedTime = bedTimeField.getText().trim();
            if (!bedTime.matches("\\d{2}:\\d{2}")) {
                errors.append("Invalid bed time format (HH:MM).\n");
            }

            try {
                tidy = Integer.parseInt(tidyField.getText().trim());
                if (tidy < 1 || tidy > 10) {
                    errors.append("Tidy level must be between 1 and 10.\n");
                }
            } catch (NumberFormatException e) {
                errors.append("Tidy level must be a number between 1 and 10.\n");
            }

            try {
                roomHours = Integer.parseInt(roomHoursField.getText().trim());
                if (roomHours < 1 || roomHours > 24) {
                    errors.append("Room hours must be between 1 and 24.\n");
                }
            } catch (NumberFormatException e) {
                errors.append("Room hours must be a number between 1 and 24.\n");
            }

            alcohol = alcoholCheckBox.isSelected();
            smoke = smokeCheckBox.isSelected();
            guests = guestsCheckBox.isSelected();

            if (errors.length() > 0) {
                throw new IllegalArgumentException(errors.toString());
            }

            // Send registration request
            writer.println("register" + DELIMITER + username + DELIMITER + password + DELIMITER + email + DELIMITER + phoneNumber + DELIMITER + userDescription + DELIMITER + university + DELIMITER + bedTime + DELIMITER + alcohol + DELIMITER + smoke + DELIMITER + guests + DELIMITER + tidy + DELIMITER + roomHours);

            String response = reader.readLine();
            if (!response.equals(SUCCESS)) {
                JOptionPane.showMessageDialog(null, "Registration failed: " + response, "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            JOptionPane.showMessageDialog(null, "Registration successful! Now, upload your profile picture.", "Success", JOptionPane.INFORMATION_MESSAGE);

            // Profile Picture Upload
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select Profile Picture");
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File profilePictureFile = fileChooser.getSelectedFile();
                byte[] profilePictureBytes = new byte[(int) profilePictureFile.length()];
                try (FileInputStream fis = new FileInputStream(profilePictureFile)) {
                    fis.read(profilePictureBytes);

                    writer.println("uploadProfilePicture" + DELIMITER + username);
                    writer.println(profilePictureBytes.length);
                    socket.getOutputStream().write(profilePictureBytes);
                    socket.getOutputStream().flush();

                    String pictureResponse = reader.readLine();
                    if (!pictureResponse.equals(SUCCESS)) {
                        JOptionPane.showMessageDialog(null, "Profile picture upload failed.", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Profile picture uploaded successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Error uploading profile picture: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            return true;
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, "Please address the following errors:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error during registration: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean sendMessage() {
        if (!isConnected) {
            JOptionPane.showMessageDialog(null, "Not connected to server.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Request the list of friends from the server
        writer.println("getFriendList" + DELIMITER + username);

        try {
            String response = reader.readLine();

            if (response == null || response.equals(FAILURE)) {
                JOptionPane.showMessageDialog(null, "You have no friends to message.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return false;
            }

            // Parse the list of friends
            String[] friends = response.split(DELIMITER);
            if (friends.length == 0) {
                JOptionPane.showMessageDialog(null, "You have no friends to message.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return false;
            }

            // Create the Send Message Panel
            JPanel messagePanel = new JPanel(new GridLayout(2, 2, 10, 10));
            JComboBox<String> friendDropdown = new JComboBox<>(friends); // Dropdown for selecting a friend
            JTextArea messageArea = new JTextArea(5, 20);

            messagePanel.add(new JLabel("Select Friend:"));
            messagePanel.add(friendDropdown);
            messagePanel.add(new JLabel("Message:"));
            messagePanel.add(new JScrollPane(messageArea));

            // Show dialog box
            int option = JOptionPane.showConfirmDialog(null, messagePanel, "Send Message", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (option != JOptionPane.OK_OPTION) {
                return false; // User canceled the operation
            }


            String recipient = (String) friendDropdown.getSelectedItem();
            writer.println("getMessageHistory" + DELIMITER + username + DELIMITER + recipient.trim());
            int messageNum;
            String history = reader.readLine();

            if (history.equals("FAILURE")) {
                messageNum = history.split(DELIMITER).length; //Ordering messages
            } else {
                messageNum = history.split(DELIMITER).length + 1; //Ordering messages
            }
            String message = messageArea.getText().trim() + String.format("##%d##", messageNum);

            // Validate message input
            if (message.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Message cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Send message to server
            writer.println("sendMessage" + DELIMITER + username + DELIMITER + recipient + DELIMITER + message);

            String sendResponse = reader.readLine();
            if (sendResponse.equals(SUCCESS)) {
                JOptionPane.showMessageDialog(null, "Message sent successfully to " + recipient + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Failed to send message: " + sendResponse, "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error retrieving friends list: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public void viewMessage() {
        if (!isConnected) {
            JOptionPane.showMessageDialog(null, "Not connected to the server.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Request friend list from server
        writer.println("getFriendList" + DELIMITER + username);

        try {
            String response = reader.readLine();

            if (response == null || response.equals(FAILURE)) {
                JOptionPane.showMessageDialog(null, "You have no friends to view messages with.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Parse the friend list
            String[] friends = response.split(DELIMITER);

            // Create a dropdown dialog to select a friend
            String recipient = (String) JOptionPane.showInputDialog(null, "Select a friend to view messages with:", "View Messages", JOptionPane.PLAIN_MESSAGE, null, friends, friends.length > 0 ? friends[0] : null);

            if (recipient == null || recipient.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "No recipient selected.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Request message history from server
            writer.println("getMessageHistory" + DELIMITER + username + DELIMITER + recipient.trim());

            response = reader.readLine();
            System.out.println(response);

            if (response == null || response.equals(FAILURE)) {
                JOptionPane.showMessageDialog(null, "No messages found with " + recipient, "Info", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Parse and format the messages
                StringBuilder messages = new StringBuilder("Messages with " + recipient + ":\n\n");
                String[] orderedMessages = new String[response.length()];
                for (String message : response.split(DELIMITER)) {
                    int order = Integer.parseInt(message.split("##")[1]);
                    orderedMessages[order - 1] = message.replace(String.format("##%d##", order), "");
                }
                for (String message : orderedMessages) {
                    if (message != null) {
                        messages.append(message).append("\n");
                    }
                }

                // Display messages in a scrollable dialog
                JTextArea messageArea = new JTextArea(messages.toString());
                messageArea.setEditable(false);
                messageArea.setLineWrap(true);
                messageArea.setWrapStyleWord(true);

                JScrollPane scrollPane = new JScrollPane(messageArea);
                scrollPane.setPreferredSize(new Dimension(400, 300));

                JOptionPane.showMessageDialog(null, scrollPane, "Messages with " + recipient, JOptionPane.PLAIN_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error retrieving messages: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void viewFriendList() {
        if (!isConnected) {
            JOptionPane.showMessageDialog(null, "Not connected to the server.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        writer.println("getFriendList" + DELIMITER + username);

        try {
            String response = reader.readLine();
            if (response == null || response.equals(FAILURE)) {
                JOptionPane.showMessageDialog(null, "Failed to retrieve the friend list or you have no friends.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Parse the friend list
            String[] friends = response.split(DELIMITER);

            if (friends.length == 0) {
                JOptionPane.showMessageDialog(null, "You have no friends in your friend list.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Display the friend list in a scrollable text area
            JTextArea friendListArea = new JTextArea();
            friendListArea.setEditable(false);
            for (String friend : friends) {
                friendListArea.append(friend + "\n");
            }

            JScrollPane scrollPane = new JScrollPane(friendListArea);
            scrollPane.setPreferredSize(new Dimension(400, 300));

            JOptionPane.showMessageDialog(null, scrollPane, "Friend List", JOptionPane.PLAIN_MESSAGE);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error retrieving friend list: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void viewFriendRequests() {
        if (!isConnected) {
            JOptionPane.showMessageDialog(null, "Not connected to the server.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        writer.println("viewFriendRequests" + DELIMITER + username);

        try {
            String response = reader.readLine();
            if (response == null || response.equals(FAILURE)) {
                JOptionPane.showMessageDialog(null, "No pending friend requests.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Parse the friend requests
            String[] requests = response.split(DELIMITER);

            if (requests.length == 0) {
                JOptionPane.showMessageDialog(null, "No pending friend requests.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Create a panel to display the requests
            JPanel requestsPanel = new JPanel(new GridLayout(requests.length, 1, 10, 10));
            requestsPanel.setBorder(BorderFactory.createTitledBorder("Pending Friend Requests"));

            // For each friend request, create a sub-panel with buttons
            for (String requester : requests) {
                JPanel requestPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                JLabel requesterLabel = new JLabel("Friend request from: " + requester);
                JButton acceptButton = new JButton("Accept");
                JButton declineButton = new JButton("Decline");

                // Add action listeners for the buttons
                acceptButton.addActionListener(e -> {
                    if (acceptFriendRequest(requester)) {
                        JOptionPane.showMessageDialog(null, "Accepted friend request from " + requester, "Success", JOptionPane.INFORMATION_MESSAGE);
                        requestPanel.setVisible(false); // Hide the panel after action
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to accept friend request from " + requester, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });

                declineButton.addActionListener(e -> {
                    if (declineFriendRequest(requester)) {
                        JOptionPane.showMessageDialog(null, "Declined friend request from " + requester, "Info", JOptionPane.INFORMATION_MESSAGE);
                        requestPanel.setVisible(false); // Hide the panel after action
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to decline friend request from " + requester, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });

                // Add components to the request panel
                requestPanel.add(requesterLabel);
                requestPanel.add(acceptButton);
                requestPanel.add(declineButton);

                // Add the request panel to the main panel
                requestsPanel.add(requestPanel);
            }

            // Display all friend requests in a scrollable pane
            JScrollPane scrollPane = new JScrollPane(requestsPanel);
            scrollPane.setPreferredSize(new Dimension(400, 300));

            JOptionPane.showMessageDialog(null, scrollPane, "Friend Requests", JOptionPane.PLAIN_MESSAGE);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error retrieving friend requests: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void sendFriendRequest() {
        if (!isConnected) {
            JOptionPane.showMessageDialog(null, "Not connected to the server.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create a panel for entering the friend's username
        JPanel inputPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        inputPanel.add(new JLabel("Enter the username:"));
        JTextField usernameField = new JTextField();
        inputPanel.add(usernameField);

        // Show dialog to enter the username
        int option = JOptionPane.showConfirmDialog(null, inputPanel, "Send Friend Request", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option != JOptionPane.OK_OPTION) {
            return; // User canceled the action
        }

        String friendUsername = usernameField.getText().trim();
        if (friendUsername.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Username cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Send friend request to the server
        writer.println("sendFriendRequest" + DELIMITER + username + DELIMITER + friendUsername);

        try {
            String response = reader.readLine();
            if (response.equals(SUCCESS)) {
                JOptionPane.showMessageDialog(null, "Friend request sent to " + friendUsername, "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Failed to send friend request: " + response, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error sending friend request: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void removeFriend() {
        if (!isConnected) {
            JOptionPane.showMessageDialog(null, "Not connected to the server.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Request the friend list from the server
        writer.println("getFriendList" + DELIMITER + username);

        try {
            String response = reader.readLine();

            if (response.equals(FAILURE)) {
                JOptionPane.showMessageDialog(null, "You have no friends to remove.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String[] friends = response.split(DELIMITER);
            if (friends.length == 0) {
                JOptionPane.showMessageDialog(null, "You have no friends to remove.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Create a dropdown menu with the friend list
            JComboBox<String> friendDropdown = new JComboBox<>(friends);

            JPanel removePanel = new JPanel(new GridLayout(2, 1, 10, 10));
            removePanel.add(new JLabel("Select a friend to remove:"));
            removePanel.add(friendDropdown);

            int option = JOptionPane.showConfirmDialog(null, removePanel, "Remove Friend", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (option != JOptionPane.OK_OPTION) {
                return; // User canceled the action
            }

            String selectedFriend = (String) friendDropdown.getSelectedItem();
            if (selectedFriend == null || selectedFriend.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "No friend selected.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Send the remove request to the server
            writer.println("removeFriend" + DELIMITER + username + DELIMITER + selectedFriend);

            response = reader.readLine();
            if (response.equals(SUCCESS)) {
                JOptionPane.showMessageDialog(null, selectedFriend + " has been removed from your friend list.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Failed to remove friend: " + response, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error removing friend: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void viewBlockList() {
        if (!isConnected) {
            JOptionPane.showMessageDialog(null, "Not connected to the server.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        writer.println("getBlockList" + DELIMITER + username);

        try {
            String response = reader.readLine();
            if (response == null || response.equals(FAILURE)) {
                JOptionPane.showMessageDialog(null, "Failed to retrieve the block list or you haven't blocked anyone.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Parse the block list
            String[] blockedUsers = response.split(DELIMITER);

            if (blockedUsers.length == 0) {
                JOptionPane.showMessageDialog(null, "Your block list is empty.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Display the block list in a scrollable text area
            JTextArea blockListArea = new JTextArea();
            blockListArea.setEditable(false);
            for (String blockedUser : blockedUsers) {
                blockListArea.append(blockedUser + "\n");
            }

            JScrollPane scrollPane = new JScrollPane(blockListArea);
            scrollPane.setPreferredSize(new Dimension(400, 300));

            JOptionPane.showMessageDialog(null, scrollPane, "Blocked Users", JOptionPane.PLAIN_MESSAGE);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error retrieving block list: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void blockUser() {
        if (!isConnected) {
            JOptionPane.showMessageDialog(null, "Not connected to the server.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create a dialog to prompt for the username to block
        JPanel blockPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        JTextField usernameField = new JTextField();

        blockPanel.add(new JLabel("Enter the username to block:"));
        blockPanel.add(usernameField);

        int option = JOptionPane.showConfirmDialog(null, blockPanel, "Block User", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option != JOptionPane.OK_OPTION) {
            return; // User canceled the action
        }

        String blockedUsername = usernameField.getText().trim();
        if (blockedUsername.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Username cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Send the block request to the server
        writer.println("blockUser" + DELIMITER + username + DELIMITER + blockedUsername);

        try {
            String response = reader.readLine();
            if (response.equals(SUCCESS)) {
                JOptionPane.showMessageDialog(null, blockedUsername + " has been blocked successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Failed to block user: " + response, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error blocking user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void unblockUser() {
        if (!isConnected) {
            JOptionPane.showMessageDialog(null, "Not connected to the server.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Request the list of blocked users from the server
        writer.println("getBlockList" + DELIMITER + username);

        try {
            String response = reader.readLine();

            if (response.equals(FAILURE)) {
                JOptionPane.showMessageDialog(null, "You have no blocked users to unblock.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String[] blockedUsers = response.split(DELIMITER);
            if (blockedUsers.length == 0) {
                JOptionPane.showMessageDialog(null, "You have no blocked users to unblock.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Create a dropdown menu with the list of blocked users
            JComboBox<String> blockedUserDropdown = new JComboBox<>(blockedUsers);

            JPanel unblockPanel = new JPanel(new GridLayout(2, 1, 10, 10));
            unblockPanel.add(new JLabel("Select a user to unblock:"));
            unblockPanel.add(blockedUserDropdown);

            int option = JOptionPane.showConfirmDialog(null, unblockPanel, "Unblock User", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (option != JOptionPane.OK_OPTION) {
                return; // User canceled the action
            }

            String selectedBlockedUser = (String) blockedUserDropdown.getSelectedItem();
            if (selectedBlockedUser == null || selectedBlockedUser.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "No user selected.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Send the unblock request to the server
            writer.println("removeBlockedUser" + DELIMITER + username + DELIMITER + selectedBlockedUser);

            response = reader.readLine();
            if (response.equals(SUCCESS)) {
                JOptionPane.showMessageDialog(null, selectedBlockedUser + " has been unblocked.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Failed to unblock user: " + response, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error unblocking user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void viewProfile() {
        if (!isConnected) {
            JOptionPane.showMessageDialog(null, "Not connected to the server.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Input dialog to get the target username
        String targetUsername = JOptionPane.showInputDialog(null, "Enter the username to view their profile:", "View Profile", JOptionPane.PLAIN_MESSAGE);

        if (targetUsername == null || targetUsername.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Username cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        targetUsername = targetUsername.trim();

        // Send request to the server
        writer.println("viewProfile" + DELIMITER + targetUsername);

        try {
            String response = reader.readLine();
            if (response == null || response.equals(FAILURE)) {
                JOptionPane.showMessageDialog(null, "Profile not found for " + targetUsername, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Parse profile data
            String[] profileData = response.split(DELIMITER);
            if (profileData.length < 12) { // Ensure response has the required fields
                JOptionPane.showMessageDialog(null, "Error: Invalid profile data received.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Fetch profile picture
            String pictureData = reader.readLine();
            ImageIcon profilePicture = null;
            if (pictureData != null && !pictureData.equals("NO_PICTURE")) {
                try {
                    byte[] imageBytes = Base64.getDecoder().decode(pictureData);
                    profilePicture = new ImageIcon(imageBytes);
                    // Resize the image for display
                    Image resizedImage = profilePicture.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                    profilePicture = new ImageIcon(resizedImage);
                } catch (Exception e) {
                    System.err.println("Error decoding profile picture: " + e.getMessage());
                }
            }

            // Create the profile view panel
            JPanel profileViewPanel = new JPanel(new BorderLayout(10, 10));
            profileViewPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Profile Picture Section
            JLabel profilePictureLabel = new JLabel();
            profilePictureLabel.setHorizontalAlignment(JLabel.CENTER);
            if (profilePicture != null) {
                profilePictureLabel.setIcon(profilePicture);
            } else {
                profilePictureLabel.setText("No Profile Picture Available");
            }
            profileViewPanel.add(profilePictureLabel, BorderLayout.NORTH);

            JPanel detailsPanel = new JPanel(new GridLayout(1, 2, 20, 10));

            // Left Column: General Information
            JPanel infoPanel = new JPanel(new GridLayout(0, 1, 5, 5));
            infoPanel.setBorder(BorderFactory.createTitledBorder("User Information"));
            infoPanel.add(new JLabel("Username: " + targetUsername));
            infoPanel.add(new JLabel("Email: " + profileData[2]));
            infoPanel.add(new JLabel("Phone: " + profileData[3]));
            infoPanel.add(new JLabel("Description: " + profileData[4]));
            infoPanel.add(new JLabel("University: " + profileData[5]));

            // Right Column: Room Preferences
            JPanel preferencesPanel = new JPanel(new GridLayout(0, 1, 5, 5));
            preferencesPanel.setBorder(BorderFactory.createTitledBorder("Room Preferences"));
            preferencesPanel.add(new JLabel("Bedtime: " + profileData[6]));
            preferencesPanel.add(new JLabel("Alcohol: " + (profileData[7].equals("true") ? "Allowed" : "Not Allowed")));
            preferencesPanel.add(new JLabel("Smoke: " + (profileData[8].equals("true") ? "Allowed" : "Not Allowed")));
            preferencesPanel.add(new JLabel("Guests: " + (profileData[9].equals("true") ? "Allowed" : "Not Allowed")));
            preferencesPanel.add(new JLabel("Tidy Level: " + profileData[10] + "/10"));
            preferencesPanel.add(new JLabel("Hours in Room: " + profileData[11] + " hrs/day"));

            // Add both columns to the details panel
            detailsPanel.add(infoPanel);
            detailsPanel.add(preferencesPanel);

            // Add details panel to the main profile panel
            profileViewPanel.add(detailsPanel, BorderLayout.CENTER);

            // Show profile details in a dialog
            JOptionPane.showMessageDialog(null, profileViewPanel, "Profile of " + targetUsername, JOptionPane.PLAIN_MESSAGE);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error retrieving profile: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateProfile() {
        if (!isConnected) {
            JOptionPane.showMessageDialog(null, "Not connected to the server.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Options for the profile update
        String[] options = {"Username", "Password", "Email", "Phone Number", "Description", "University", "Preferences", "Profile Picture"};
        String selection = (String) JOptionPane.showInputDialog(null, "Choose a parameter to update:", "Update Profile", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (selection == null) return; // User cancelled

        String oldUsername = username;

        try {
            switch (selection) {
                case "Username":
                    username = JOptionPane.showInputDialog("Enter new username:", username);
                    if (username == null || username.trim().isEmpty() || username.contains(DELIMITER)) {
                        JOptionPane.showMessageDialog(null, "Invalid username. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    break;

                case "Password":
                    password = JOptionPane.showInputDialog("Enter new password:");
                    if (password == null || password.trim().isEmpty() || password.length() < 6 || password.contains(DELIMITER)) {
                        JOptionPane.showMessageDialog(null, "Invalid password. Must be at least 6 characters.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    break;

                case "Email":
                    email = JOptionPane.showInputDialog("Enter new email:", email);
                    if (email == null || !email.matches("[^@]+@[^@]+\\.[^@]+")) {
                        JOptionPane.showMessageDialog(null, "Invalid email format. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    break;

                case "Phone Number":
                    phoneNumber = JOptionPane.showInputDialog("Enter new phone number:", phoneNumber);
                    if (phoneNumber == null || !phoneNumber.matches("\\d{10}")) {
                        JOptionPane.showMessageDialog(null, "Invalid phone number. Must be 10 digits.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    break;

                case "Description":
                    userDescription = JOptionPane.showInputDialog("Enter new description:", userDescription);
                    if (userDescription == null || userDescription.trim().isEmpty() || userDescription.contains(DELIMITER)) {
                        JOptionPane.showMessageDialog(null, "Invalid description. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    break;

                case "University":
                    university = JOptionPane.showInputDialog("Enter new university:", university);
                    if (university == null || university.trim().isEmpty() || university.contains(DELIMITER)) {
                        JOptionPane.showMessageDialog(null, "Invalid university. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    break;

                case "Preferences":
                    bedTime = JOptionPane.showInputDialog("Enter your average bedtime (e.g., 22:30):", bedTime);
                    if (bedTime == null || !bedTime.matches("\\d{2}:\\d{2}")) {
                        JOptionPane.showMessageDialog(null, "Invalid bedtime format. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String alcoholInput = JOptionPane.showInputDialog("Do you drink alcohol? (yes/no):", alcohol ? "yes" : "no");
                    alcohol = "yes".equalsIgnoreCase(alcoholInput);

                    String smokeInput = JOptionPane.showInputDialog("Do you smoke? (yes/no):", smoke ? "yes" : "no");
                    smoke = "yes".equalsIgnoreCase(smokeInput);

                    String guestsInput = JOptionPane.showInputDialog("Are you comfortable with guests? (yes/no):", guests ? "yes" : "no");
                    guests = "yes".equalsIgnoreCase(guestsInput);

                    tidy = Integer.parseInt(JOptionPane.showInputDialog("How tidy are you? (1-10):", tidy));
                    roomHours = Integer.parseInt(JOptionPane.showInputDialog("How many hours per day do you spend in your room? (1-24):", roomHours));

                    if (tidy < 1 || tidy > 10 || roomHours < 1 || roomHours > 24) {
                        JOptionPane.showMessageDialog(null, "Invalid preference values.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    break;

                case "Profile Picture":
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Select Profile Picture");
                    int result = fileChooser.showOpenDialog(null);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                        if (setProfilePicture(filePath)) {
                            JOptionPane.showMessageDialog(null, "Profile picture uploaded successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(null, "Failed to upload profile picture.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    return; // No server update needed for profile picture
            }

            // Send update request to the server
            writer.println("updateProfile" + DELIMITER + oldUsername + DELIMITER + username + DELIMITER + password + DELIMITER + email + DELIMITER + phoneNumber + DELIMITER + userDescription + DELIMITER + university + DELIMITER + bedTime + DELIMITER + alcohol + DELIMITER + smoke + DELIMITER + guests + DELIMITER + tidy + DELIMITER + roomHours);

            // Read response
            String response = reader.readLine();
            if (response.equals(SUCCESS)) {
                JOptionPane.showMessageDialog(null, "Profile updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Profile update failed: " + response, "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (IOException | NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Error updating profile: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void searchRoommates() {
        if (!isConnected) {
            JOptionPane.showMessageDialog(null, "Not connected to the server.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Dropdown for search options
        String[] options = {"Search by Parameter", "Exact Match", "Partial Match"};
        String selection = (String) JOptionPane.showInputDialog(null, "Select a search type:", "Search Roommates", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (selection == null) return; // User canceled

        switch (selection) {
            case "Search by Parameter" -> searchByParameter();
            case "Exact Match" -> exactMatch();
            case "Partial Match" -> partialMatch();
        }
    }

    public void searchByParameter() {
        JPanel parameterPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        // Dropdown for parameter options
        parameterPanel.add(new JLabel("Select Parameter:"));
        String[] parameters = {"Name", "Email", "Phone", "University"};
        JComboBox<String> parameterDropdown = new JComboBox<>(parameters);
        parameterPanel.add(parameterDropdown);

        parameterPanel.add(new JLabel("Enter Value:"));
        JTextField valueField = new JTextField();
        parameterPanel.add(valueField);

        int result = JOptionPane.showConfirmDialog(null, parameterPanel, "Search by Parameter", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) return; // User canceled

        String parameter = ((String) parameterDropdown.getSelectedItem()).toLowerCase(); // Get selected parameter
        String value = valueField.getText().trim();

        if (value.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Value cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Send request to the server
        writer.println("searchByParameter" + DELIMITER + parameter + DELIMITER + value);

        try {
            String response = reader.readLine();
            if (response == null || response.equals(FAILURE)) {
                JOptionPane.showMessageDialog(null, "No results found for the given parameter.", "Info", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JTextArea textArea = new JTextArea(response.replace(DELIMITER, "\n"));
                textArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(400, 300));

                JOptionPane.showMessageDialog(null, scrollPane, "Search by Parameter Results", JOptionPane.PLAIN_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error searching users: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void exactMatch() {
        if (writer == null) return;
        writer.println("exactMatch" + DELIMITER + username);

        try {
            String response = reader.readLine();
            if (response == null || response.equals(FAILURE)) {
                JOptionPane.showMessageDialog(null, "No exact matches found.", "Info", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JTextArea textArea = new JTextArea(response.replace(DELIMITER, "\n"));
                textArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(400, 300));

                JOptionPane.showMessageDialog(null, scrollPane, "Exact Match Results", JOptionPane.PLAIN_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error finding exact matches: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void partialMatch() {
        if (writer == null) return;

        writer.println("partialMatch" + DELIMITER + username);

        try {
            String response = reader.readLine();
            if (response == null || response.equals(FAILURE)) {
                JOptionPane.showMessageDialog(null, "No partial matches found.", "Info", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JTextArea textArea = new JTextArea(response.replace(DELIMITER, "\n")); // Each match in a new line
                textArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(400, 300));

                JOptionPane.showMessageDialog(null, scrollPane, "Partial Match Results", JOptionPane.PLAIN_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error finding partial matches: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void disconnect() {
        if (!isConnected) {
            System.out.println("Not connected to the server.");
            return;
        }

        try {
            if (socket != null) {
                socket.close();
            }
            isConnected = false;
        } catch (IOException e) {
            System.out.println("Error disconnecting from server: " + e.getMessage());
        } finally {
            isConnected = false;
        }
    }

    //Helper method to accept friend request
    public boolean acceptFriendRequest(String friend) {
        if (!isConnected) {
            JOptionPane.showMessageDialog(null, "Not connected to the server.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        writer.println("acceptFriendRequest" + DELIMITER + username + DELIMITER + friend);

        try {
            String response = reader.readLine(); //To read response from server
            return response.equals(SUCCESS);
        } catch (IOException e) {
            System.out.println("Error adding friend: " + e.getMessage());
            return false;
        }
    }

    //Helper method to decline friend request
    public boolean declineFriendRequest(String usernameInput) {
        if (!isConnected) {
            JOptionPane.showMessageDialog(null, "Not connected to the server.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        writer.println("declineFriendRequest" + DELIMITER + username + DELIMITER + usernameInput);

        try {
            String response = reader.readLine(); //To read response from server
            return response.equals(SUCCESS);
        } catch (IOException e) {
            return false;
        }
    }

    //To search for a roommate based on a specific preferences
    public void searchByParameter(String parameter, String value) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return;
        }

        writer.println("searchByParameter" + DELIMITER + parameter + DELIMITER + value);

        try {
            String response = reader.readLine(); //To read response from server
            if (!response.equals(FAILURE)) {
                for (String token : response.split(DELIMITER)) {
                    System.out.println(token);
                }
            }
        } catch (IOException e) {
            System.out.println("Error searching users: " + e.getMessage());
        }
    }

    //To search for roommate based on exact match of preferences

    //To search for roommate based on exact match of preferences


    // To upload Profile Picture for the user
    public boolean setProfilePicture(String filePath) {
        File file = new File(filePath);
        if (!file.exists() || file.isDirectory()) {
            JOptionPane.showMessageDialog(null, "Invalid file path. Please select a valid file.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        long maxFileSize = 5 * 1024 * 1024; // 5 MB limit
        if (file.length() > maxFileSize) {
            JOptionPane.showMessageDialog(null, "File size exceeds the maximum limit of 5 MB.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] fileBytes = fileInputStream.readAllBytes();
            writer.println("uploadProfilePicture" + DELIMITER + username);
            writer.println(fileBytes.length);
            socket.getOutputStream().write(fileBytes);
            socket.getOutputStream().flush();

            String response = reader.readLine();
            if (response.equals(SUCCESS)) {
                JOptionPane.showMessageDialog(null, "Profile picture uploaded successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                return true;
            } else if (response.equals("INVALID_FILE")) {
                JOptionPane.showMessageDialog(null, "The file you uploaded is not a valid PNG file.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Failed to upload profile picture.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error uploading profile picture: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
}
