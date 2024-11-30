
import java.io.*;
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

    private String username;
    private String password;
    private String email;
    private String phoneNumber;
    private String userDescription;
    private String university;
    private byte[] profilePicture;

    private String bedTime;
    private boolean alcohol;
    private boolean smoke;
    private boolean guests;
    private int tidy;
    private int roomHours;

    private boolean isConnected;
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
            System.out.println("Connected to " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
        } catch (IOException e) {
            System.out.println("Could not connect to " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
            isConnected = false;
        }
    }

    public static void main(String[] args) {
        Client client = new Client("localhost", 1102);
        client.start();
    }

    public void start() {
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

        // Left Sidebar for Profile Summary
        JPanel profilePanel = new JPanel(new BorderLayout(10, 10));
        profilePanel.setBorder(BorderFactory.createTitledBorder("Profile Summary"));
        profilePanel.setBackground(Color.LIGHT_GRAY);

        // Profile Picture Section
        JLabel profilePictureLabel = new JLabel();
        profilePictureLabel.setHorizontalAlignment(JLabel.CENTER);
        profilePictureLabel.setVerticalAlignment(JLabel.CENTER);
        if (profilePicture != null && profilePicture.length > 0) {
            try {
                ImageIcon icon = new ImageIcon(profilePicture);
                Image scaledImage = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                profilePictureLabel.setIcon(new ImageIcon(scaledImage));
            } catch (Exception e) {
                profilePictureLabel.setText("Error loading picture");
            }
        } else {
            profilePictureLabel.setText("No Profile Picture Available");
        }
        profilePanel.add(profilePictureLabel, BorderLayout.NORTH);

        // User Details Section
        JPanel userDetailsPanel = new JPanel(new GridLayout(6, 1, 5, 5));
        userDetailsPanel.add(new JLabel("Username: " + username));
        userDetailsPanel.add(new JLabel("Email: " + email));
        userDetailsPanel.add(new JLabel("Phone: " + phoneNumber));
        userDetailsPanel.add(new JLabel("University: " + university));
        userDetailsPanel.add(new JLabel("Description: " + userDescription));
        userDetailsPanel.add(new JLabel("Room Preferences: " + tidy + "/10 Tidy, " + roomHours + " hrs/day"));
        profilePanel.add(userDetailsPanel, BorderLayout.CENTER);

        frame.add(profilePanel, BorderLayout.WEST);

        // Main Action Panel
        JPanel actionPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        actionPanel.setBorder(BorderFactory.createTitledBorder("Actions"));

        // Action Buttons
        JButton sendMessageButton = new JButton("Send Message");
        JButton viewMessageButton = new JButton("View Messages");
        JButton viewFriendRequestsButton = new JButton("View Friend Requests");
        JButton sendFriendRequestButton = new JButton("Send Friend Request");
        JButton removeFriendButton = new JButton("Remove Friend");
        JButton blockUserButton = new JButton("Block User");
        JButton unblockUserButton = new JButton("Unblock User");
        JButton searchRoommatesButton = new JButton("Search Roommates");

        // Add action listeners for each button
        sendMessageButton.addActionListener(e -> sendMessage());
        viewMessageButton.addActionListener(e -> viewMessage());
        viewFriendRequestsButton.addActionListener(e -> viewFriendRequests());
        sendFriendRequestButton.addActionListener(e -> sendFriendRequest());
        removeFriendButton.addActionListener(e -> removeFriend());
        blockUserButton.addActionListener(e -> blockUser());
        unblockUserButton.addActionListener(e -> unblockUser());
        searchRoommatesButton.addActionListener(e -> searchRoommates());

        // Add buttons to action panel
        actionPanel.add(sendMessageButton);
        actionPanel.add(viewMessageButton);
        actionPanel.add(viewFriendRequestsButton);
        actionPanel.add(sendFriendRequestButton);
        actionPanel.add(removeFriendButton);
        actionPanel.add(blockUserButton);
        actionPanel.add(unblockUserButton);
        actionPanel.add(searchRoommatesButton);
        frame.add(actionPanel, BorderLayout.CENTER);

        // Right Sidebar for Profile and Account Actions
        JPanel profileActionsPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        profileActionsPanel.setBorder(BorderFactory.createTitledBorder("Profile & Account"));
        JButton viewProfileButton = new JButton("View Profile");
        JButton updateProfileButton = new JButton("Update Profile");
        JButton disconnectButton = new JButton("Disconnect and Exit");

        viewProfileButton.addActionListener(e -> viewProfile());
        updateProfileButton.addActionListener(e -> updateProfile());
        disconnectButton.addActionListener(e -> {
            disconnect();
            frame.dispose();
        });

        profileActionsPanel.add(viewProfileButton);
        profileActionsPanel.add(updateProfileButton);
        profileActionsPanel.add(disconnectButton);
        frame.add(profileActionsPanel, BorderLayout.EAST);

        // Footer Panel
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(240, 240, 240));
        JLabel footerLabel = new JLabel("CoHabit © 2024", JLabel.CENTER);
        footerPanel.add(footerLabel);
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
            } else {
                JOptionPane.showMessageDialog(null, "No profile picture available.", "Info", JOptionPane.INFORMATION_MESSAGE);
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

        try {
            // Collect and validate inputs
            username = usernameField.getText().trim();
            if (username.isEmpty() || username.contains(DELIMITER)) {
                throw new IllegalArgumentException("Invalid username.");
            }

            password = new String(passwordField.getPassword()).trim();
            if (password.isEmpty() || password.length() < 6) {
                throw new IllegalArgumentException("Password must be at least 6 characters.");
            }

            email = emailField.getText().trim();
            if (!email.matches("[^@]+@[^@]+\\.[^@]+")) {
                throw new IllegalArgumentException("Invalid email format.");
            }

            phoneNumber = phoneField.getText().trim();
            if (!phoneNumber.matches("\\d{10}")) {
                throw new IllegalArgumentException("Phone number must be 10 digits.");
            }

            userDescription = descriptionField.getText().trim();
            if (userDescription.isEmpty() || userDescription.contains(DELIMITER)) {
                throw new IllegalArgumentException("Invalid description.");
            }

            university = universityField.getText().trim();
            if (university.isEmpty() || university.contains(DELIMITER)) {
                throw new IllegalArgumentException("Invalid university.");
            }

            bedTime = bedTimeField.getText().trim();
            if (!bedTime.matches("\\d{2}:\\d{2}")) {
                throw new IllegalArgumentException("Invalid bed time format (HH:MM).");
            }

            tidy = Integer.parseInt(tidyField.getText().trim());
            if (tidy < 1 || tidy > 10) {
                throw new IllegalArgumentException("Tidy level must be between 1 and 10.");
            }

            roomHours = Integer.parseInt(roomHoursField.getText().trim());
            if (roomHours < 1 || roomHours > 24) {
                throw new IllegalArgumentException("Room hours must be between 1 and 24.");
            }

            alcohol = alcoholCheckBox.isSelected();
            smoke = smokeCheckBox.isSelected();
            guests = guestsCheckBox.isSelected();

            // Send registration request
            writer.println("register" + DELIMITER + username + DELIMITER + password + DELIMITER + email + DELIMITER
                    + phoneNumber + DELIMITER + userDescription + DELIMITER + university + DELIMITER +
                    bedTime + DELIMITER + alcohol + DELIMITER + smoke + DELIMITER + guests + DELIMITER +
                    tidy + DELIMITER + roomHours);

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
        } catch (IllegalArgumentException | IOException e) {
            JOptionPane.showMessageDialog(null, "Error during registration: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean sendMessage() {
        if (!isConnected) {
            JOptionPane.showMessageDialog(null, "Not connected to server.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Create Send Message Panel
        JPanel messagePanel = new JPanel(new GridLayout(2, 2, 10, 10));
        JTextField recipientField = new JTextField();
        JTextArea messageArea = new JTextArea(5, 20);

        messagePanel.add(new JLabel("Recipient Username:"));
        messagePanel.add(recipientField);
        messagePanel.add(new JLabel("Message:"));
        messagePanel.add(new JScrollPane(messageArea));

        // Show dialog box
        int option = JOptionPane.showConfirmDialog(
                null,
                messagePanel,
                "Send Message",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (option != JOptionPane.OK_OPTION) {
            return false; // User canceled the operation
        }

        String recipient = recipientField.getText().trim();
        String message = messageArea.getText().trim();

        // Validate inputs
        if (recipient.isEmpty() || message.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Recipient and message cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Send message to server
        writer.println("sendMessage" + DELIMITER + username + DELIMITER + recipient + DELIMITER + message);

        try {
            String response = reader.readLine();
            if (response.equals(SUCCESS)) {
                JOptionPane.showMessageDialog(null, "Message sent successfully to " + recipient + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Failed to send message: " + response, "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error sending message: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public void viewMessage() {
        if (!isConnected) {
            JOptionPane.showMessageDialog(null, "Not connected to the server.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Input dialog to get the recipient's username
        String recipient = JOptionPane.showInputDialog(
                null,
                "Enter the username to view messages with:",
                "View Messages",
                JOptionPane.PLAIN_MESSAGE
        );

        if (recipient == null || recipient.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Recipient username cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Request message history from server
        writer.println("getMessageHistory" + DELIMITER + username + DELIMITER + recipient.trim());

        try {
            String response = reader.readLine();
            if (response == null || response.equals(FAILURE)) {
                JOptionPane.showMessageDialog(
                        null,
                        "No messages found with " + recipient,
                        "Info",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                // Parse and format the messages
                StringBuilder messages = new StringBuilder("Messages with " + recipient + ":\n\n");
                for (String message : response.split(DELIMITER)) {
                    messages.append(message).append("\n");
                }

                // Display messages in a scrollable dialog
                JTextArea messageArea = new JTextArea(messages.toString());
                messageArea.setEditable(false);
                messageArea.setLineWrap(true);
                messageArea.setWrapStyleWord(true);

                JScrollPane scrollPane = new JScrollPane(messageArea);
                scrollPane.setPreferredSize(new Dimension(400, 300));

                JOptionPane.showMessageDialog(
                        null,
                        scrollPane,
                        "Messages with " + recipient,
                        JOptionPane.PLAIN_MESSAGE
                );
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Error retrieving messages: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
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

        // Create a dialog to prompt for the username to remove
        JPanel removePanel = new JPanel(new GridLayout(2, 1, 10, 10));
        JTextField usernameField = new JTextField();

        removePanel.add(new JLabel("Enter the username to remove from your friend list:"));
        removePanel.add(usernameField);

        int option = JOptionPane.showConfirmDialog(null, removePanel, "Remove Friend", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option != JOptionPane.OK_OPTION) {
            return; // User canceled the action
        }

        String friendUsername = usernameField.getText().trim();
        if (friendUsername.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Username cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Send the remove request to the server
        writer.println("removeFriend" + DELIMITER + username + DELIMITER + friendUsername);

        try {
            String response = reader.readLine();
            if (response.equals(SUCCESS)) {
                JOptionPane.showMessageDialog(null, friendUsername + " has been removed from your friend list.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Failed to remove friend: " + response, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error removing friend: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

        // Create a panel to input the username to unblock
        JPanel unblockPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        JTextField usernameField = new JTextField();

        unblockPanel.add(new JLabel("Enter the username to unblock:"));
        unblockPanel.add(usernameField);

        int option = JOptionPane.showConfirmDialog(null, unblockPanel, "Unblock User", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option != JOptionPane.OK_OPTION) {
            return; // User canceled the action
        }

        String unblockedUsername = usernameField.getText().trim();
        if (unblockedUsername.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Username cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Send the unblock request to the server
        writer.println("removeBlockedUser" + DELIMITER + username + DELIMITER + unblockedUsername);

        try {
            String response = reader.readLine();
            if (response.equals(SUCCESS)) {
                JOptionPane.showMessageDialog(null, unblockedUsername + " has been unblocked.", "Success", JOptionPane.INFORMATION_MESSAGE);
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

        // Create a panel to input the username
        JPanel profilePanel = new JPanel(new GridLayout(2, 1, 10, 10));
        JTextField usernameField = new JTextField();

        profilePanel.add(new JLabel("Enter the username to view their profile:"));
        profilePanel.add(usernameField);

        int option = JOptionPane.showConfirmDialog(null, profilePanel, "View Profile", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option != JOptionPane.OK_OPTION) {
            return; // User canceled the action
        }

        String targetUsername = usernameField.getText().trim();
        if (targetUsername.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Username cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Send request to the server
        writer.println("viewProfile" + DELIMITER + targetUsername);

        try {
            String response = reader.readLine();
            if (response == null || response.equals(FAILURE)) {
                JOptionPane.showMessageDialog(null, "Profile not found for " + targetUsername, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Parse the response and display the profile
            String[] profileData = response.split(DELIMITER);
            if (profileData.length < 7) { // Ensure response has the required fields
                JOptionPane.showMessageDialog(null, "Error: Invalid profile data received.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get the profile picture from the server
            String pictureData = reader.readLine();
            ImageIcon profilePicture = null;

            if (pictureData != null && !pictureData.equals("NO_PICTURE")) {
                byte[] imageBytes = Base64.getDecoder().decode(pictureData);
                profilePicture = new ImageIcon(imageBytes);
                // Resize the image for a better UI
                Image resizedImage = profilePicture.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                profilePicture = new ImageIcon(resizedImage);
            }

            // Profile Details Panel
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

            // User Details Section
            JPanel userDetailsPanel = new JPanel(new GridLayout(6, 1, 10, 10));
            userDetailsPanel.add(new JLabel("Username: " + targetUsername));
            userDetailsPanel.add(new JLabel("Email: " + profileData[0]));
            userDetailsPanel.add(new JLabel("Phone: " + profileData[1]));
            userDetailsPanel.add(new JLabel("University: " + profileData[2]));
            userDetailsPanel.add(new JLabel("Description: " + profileData[3]));
            userDetailsPanel.add(new JLabel("Room Preferences: " + profileData[4] + ", " + profileData[5]));
            profileViewPanel.add(userDetailsPanel, BorderLayout.CENTER);

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
        String[] options = {
                "Username", "Password", "Email", "Phone Number", "Description",
                "University", "Preferences", "Profile Picture"
        };
        String selection = (String) JOptionPane.showInputDialog(
                null,
                "Choose a parameter to update:",
                "Update Profile",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

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
            writer.println("updateProfile" + DELIMITER + oldUsername + DELIMITER + username + DELIMITER +
                    password + DELIMITER + email + DELIMITER + phoneNumber + DELIMITER + userDescription +
                    DELIMITER + university + DELIMITER + bedTime + DELIMITER + alcohol + DELIMITER +
                    smoke + DELIMITER + guests + DELIMITER + tidy + DELIMITER + roomHours);

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
        String selection = (String) JOptionPane.showInputDialog(
                null,
                "Select a search type:",
                "Search Roommates",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (selection == null) return; // User canceled

        switch (selection) {
            case "Search by Parameter":
                searchByParameterGUI();
                break;
            case "Exact Match":
                exactMatchGUI();
                break;
            case "Partial Match":
                partialMatchGUI();
                break;
        }
    }

    // GUI Wrapper for Search by Parameter
    private void searchByParameterGUI() {
        // Input panel for parameter and value
        JPanel parameterPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        parameterPanel.add(new JLabel("Parameter (e.g., university, email, etc.):"));
        JTextField parameterField = new JTextField();
        parameterPanel.add(parameterField);

        parameterPanel.add(new JLabel("Value:"));
        JTextField valueField = new JTextField();
        parameterPanel.add(valueField);

        int result = JOptionPane.showConfirmDialog(
                null,
                parameterPanel,
                "Search by Parameter",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) return; // User canceled

        String parameter = parameterField.getText().trim().toLowerCase();
        String value = valueField.getText().trim();

        if (parameter.isEmpty() || value.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Parameter and value cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Call the existing backend method and capture output
        String results = captureSearchResults(() -> searchByParameter(parameter, value));

        // Show the results
        displayResults("Search Results", results);
    }

    // GUI Wrapper for Exact Match
    private void exactMatchGUI() {
        // Call the existing backend method and capture output
        String results = captureSearchResults(this::exactMatch);

        // Show the results
        displayResults("Exact Match Results", results);
    }

    // GUI Wrapper for Partial Match
    private void partialMatchGUI() {
        // Call the existing backend method and capture output
        String results = captureSearchResults(this::partialMatch);

        // Show the results
        displayResults("Partial Match Results", results);
    }

    // Helper to capture System.out.println() output
    private String captureSearchResults(Runnable searchMethod) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        try (PrintStream ps = new PrintStream(buffer)) {
            System.setOut(ps);
            searchMethod.run();
        } finally {
            System.setOut(originalOut);
        }
        return buffer.toString().trim();
    }

    // Helper to display results in a scrollable dialog
    private void displayResults(String title, String results) {
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No results found.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JTextArea textArea = new JTextArea(results);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JOptionPane.showMessageDialog(null, scrollPane, title, JOptionPane.PLAIN_MESSAGE);
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
            System.out.println("Disconnected from the server.");
        } catch (IOException e) {
            System.out.println("Error disconnecting from server: " + e.getMessage());
        } finally {
            isConnected = false;
        }
    }

    //getters
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phoneNumber;
    }

    public String getUniversity() {
        return university;
    }

    public String getUserDescription() {
        return userDescription;
    }

    //setters
    public void setUsername(String usernameInput) {
        this.username = usernameInput;
    }

    public void setPassword(String passwordInput) {
        this.password = passwordInput;
    }

    public void setEmail(String emailInput) {
        this.email = emailInput;
    }

    public void setPhone(String phoneNumberInput) {
        this.phoneNumber = phoneNumberInput;
    }

    public void setUniversity(String universityInput) {
        this.university = universityInput;
    }

    public void setUserDescription(String userDescriptionInput) {
        this.userDescription = userDescriptionInput;
    }

    //To connect client to server

    //To check if client is connected to server

    public boolean isConnected() {
        return isConnected;
    }

    //To set user information
    public void setUserInformation() throws IOException {
        String information = reader.readLine(); //To read response from server
        String[] tokens = information.split(DELIMITER);
        if (tokens.length != 6) {
            System.out.println("Error! Invalid User Information");
            return;
        }
        username = tokens[0];
        password = tokens[1];
        email = tokens[2];
        phoneNumber = tokens[3];
        userDescription = tokens[4];
        university = tokens[5];
    }

    //To set user registration information
    public void setUserRegisterInformation(String usernameInput, String passwordInput,
                                           String emailInput,
                                           String phoneNumberInput,
                                           String userDescriptionInput,
                                           String universityInput) {
        this.username = usernameInput;
        this.password = passwordInput;
        this.email = emailInput;
        this.phoneNumber = phoneNumberInput;
        this.userDescription = userDescriptionInput;
        this.university = universityInput;
    }



    public String getMessage() throws IOException {
        return reader.readLine();
    }

    //To update a user's profile
    public boolean updateProfile(String oldUsername) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return false;
        }

        writer.println("updateProfile" + DELIMITER + oldUsername + DELIMITER + username + DELIMITER + password + DELIMITER + email + DELIMITER
                + phoneNumber + DELIMITER + userDescription + DELIMITER + university + DELIMITER +
                bedTime + DELIMITER + alcohol + DELIMITER + smoke + DELIMITER + guests + DELIMITER +
                tidy + DELIMITER + roomHours);

        try {
            String response = reader.readLine(); //To read response from server
            if ("Profile Updated".equals(response)) {
                System.out.println("Profile Updated: " + username);
                return true;
            } else {
                System.out.println("Updating failed: " + response);
                return false;
            }
        } catch (IOException e) {
            System.out.println("Error during updating: " + e.getMessage());
            return false;
        }
    }

    //To set a user's preferences
    public void setPreferences(String bedTimeInput, boolean alcoholInput, boolean smokeInput,
                               boolean guestsInput, int tidyInput, int roomHoursInput) {
        this.bedTime = bedTimeInput;
        this.alcohol = alcoholInput;
        this.smoke = smokeInput;
        this.guests = guestsInput;
        this.tidy = tidyInput;
        this.roomHours = roomHoursInput;
        if (bedTimeInput == null || tidyInput <= 0 || tidyInput > 10 || roomHoursInput < 0) {
            System.out.println("Invalid Input");
        }
    }

    //To send messages to another user
    public boolean sendMessage(String receiver, String message) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return false;
        }

        writer.println("sendMessage" + DELIMITER + username + DELIMITER + receiver + DELIMITER + message);

        try {
            String response = reader.readLine(); //To read response from server
            if ("Successfully sent message".equals(response)) {
                System.out.println("Message sent to " + receiver);
                return true;
            } else {
                System.out.println("Failed to send message.");
                return false;
            }
        } catch (IOException e) {
            System.out.println("Error sending message: " + e.getMessage());
            return false;
        }
    }

    public void viewMessage(String receiverUsername) {
        if (!isConnected) {
            System.out.println("Not connected to the server.");
            return;
        }

        try {
            // Send request to the server
            writer.println("loadMessages" + DELIMITER + username + DELIMITER + receiverUsername);

            // Read the response
            String response = reader.readLine();
            if (response == null || response.equals("Message list is empty")) {
                System.out.println("No messages found between you and " + receiverUsername);
                return;
            }

            System.out.println("Messages with " + receiverUsername + ":");
            for (String message : response.split("###")) {
                System.out.println(message);
            }
        } catch (IOException e) {
            System.out.println("Error retrieving messages: " + e.getMessage());
        }
    }

    //To load chat history between two users
    public String fetchMessages(String user, String receiver) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return null;
        }

        writer.println("loadMessages" + DELIMITER + username + DELIMITER + receiver + DELIMITER + receiver);

        try {
            String response = reader.readLine(); //To read response from server
            if ("Message List is Empty".equals(response)) {
                return null;
            } else {
                return response;
            }
        } catch (IOException e) {
            System.out.println("Error loading message history");
            return null;
        }
    }

    //To send friend requests to other users

    public boolean sendFriendRequest(String user, String potentialFriend) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return false;
        }

        writer.println("sendFriendRequest" + DELIMITER + user + DELIMITER + potentialFriend);

        try {
            String response = reader.readLine(); //To read response from server
            if ("Successfully sent friend request".equals(response)) {
                System.out.println("Friend request sent to " + potentialFriend);
                return true;
            } else {
                System.out.println("Friend request failed.");
                return false;
            }
        } catch (IOException e) {
            System.out.println("Error sending friend request: " + e.getMessage());
            return false;
        }
    }

    //To view all friend requests of a user

    public void viewFriendRequests(String user) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return;
        }

        writer.println("viewFriendRequests" + DELIMITER + user);

        try {
            String response = reader.readLine(); //To read response from server
            if (response == null || response.isEmpty() || "No friend requests".equals(response)) {
                System.out.println("You have no pending friend requests.");
                return;
            }

            String[] friendRequests = response.split(",");
            Scanner scanner = new Scanner(System.in);

            for (String requester : friendRequests) {
                String[] tokens = requester.split(":");

                System.out.println("Friend request from: " + tokens[0]);
                System.out.println("Do you want to (1) Accept or (2) Decline?");
                String choice = scanner.nextLine();

                switch (choice) {
                    case "1": //If you accept friend request
                        if (acceptFriendRequest(requester)) {
                            System.out.println("You accepted the friend request from: " + requester);
                        } else {
                            System.out.println("Failed to accept friend request from: " + requester);
                        }
                        break;
                    case "2": //If you decline friend request
                        if (declineFriendRequest(requester)) {
                            System.out.println("You declined the friend request from: " + requester);
                        } else {
                            System.out.println("Failed to decline friend request from: " + requester);
                        }
                        break;
                    default:
                        System.out.println("Invalid option. Skipping request from: " + requester);
                }
            }
        } catch (IOException e) {
            System.out.println("Error viewing friend requests: " + e.getMessage());
        }
    }

    //Helper method to accept friend request
    public boolean acceptFriendRequest(String friend) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return false;
        }

        writer.println("acceptFriendRequest" + DELIMITER + this.username + DELIMITER + friend);

        try {
            String response = reader.readLine(); //To read response from server
            if (response.equals(SUCCESS)) {
                System.out.println(friend + " is now your friend.");
                return true;
            } else {
                System.out.println("Failed to add friend.");
                return false;
            }
        } catch (IOException e) {
            System.out.println("Error adding friend: " + e.getMessage());
            return false;
        }
    }

    //Helper method to decline friend request
    public boolean declineFriendRequest(String usernameInput) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return false;
        }

        writer.println("declineFriendRequest" + username + DELIMITER + usernameInput);

        try {
            String response = reader.readLine(); //To read response from server
            return "Successfully declined friend request".equals(response);
        } catch (IOException e) {
            System.out.println("Error declining friend request: " + e.getMessage());
            return false;
        }
    }

    //To add a user as a friend
    public boolean addFriend(String user, String friend) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return false;
        }

        writer.println("acceptFriendRequest" + DELIMITER + user + DELIMITER + friend);

        try {
            String response = reader.readLine(); //To read response from server
            if ("Successfully added friend".equals(response)) {
                System.out.println(friend + " is now your friend.");
                return true;
            } else {
                System.out.println("Failed to add friend.");
                return false;
            }
        } catch (IOException e) {
            System.out.println("Error adding friend: " + e.getMessage());
            return false;
        }
    }

    //To remove user from friend list
    public boolean removeFriend(String user, String friend) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return false;
        }

        writer.println("removeFriend" + DELIMITER + user + DELIMITER + friend);

        try {
            String response = reader.readLine(); //To read response from server
            if ("Successfully removed friend".equals(response)) {
                System.out.println(friend + " has been removed from your friend list.");
                return true;
            } else {
                System.out.println("Failed to remove friend.");
                return false;
            }
        } catch (IOException e) {
            System.out.println("Error removing friend: " + e.getMessage());
            return false;
        }
    }

    //To block user
    public boolean blockUser(String user, String blockedUser) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return false;
        }

        writer.println("blockUser" + DELIMITER + user + DELIMITER + blockedUser);

        try {
            String response = reader.readLine(); //To read response from server
            if ("Successfully blocked user".equals(response)) {
                System.out.println(blockedUser + " has been blocked.");
                return true;
            } else {
                System.out.println("Failed to block user.");
                return false;
            }
        } catch (IOException e) {
            System.out.println("Error blocking user: " + e.getMessage());
            return false;
        }
    }

    //To view a user profile
    public void viewProfile(String usernameInput) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return;
        }

        writer.println("viewProfile" + DELIMITER + username);

        try {
            String response = reader.readLine(); //To read response from server
            System.out.println("Profile data: " + response);
        } catch (IOException e) {
            System.out.println("Error viewing profile: " + e.getMessage());
        }
    }

    //To view a user's profile
    public void viewFriendsList(String usernameInput) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return;
        }

        writer.println("viewFriendsList" + DELIMITER + usernameInput);

        try {
            String response = reader.readLine(); //To read response from server
            if (response.equals("Friend list is empty")) {
                System.out.println("You have no friends yet.");
            } else {
                System.out.println("Your Friends:\n" + response);
            }
        } catch (IOException e) {
            System.out.println("Error viewing friends list: " + e.getMessage());
        }
    }

    //To unblock a user
    public boolean unblockUser(String user, String blockedUser) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return false;
        }

        writer.println("removeBlockedUser" + DELIMITER + user + DELIMITER + blockedUser);

        try {
            String response = reader.readLine(); //To read response from server
            if ("Successfully removed from blocked list".equals(response)) {
                System.out.println(blockedUser + " has been unblocked.");
                return true;
            } else {
                System.out.println("Failed to unblock user.");
                return false;
            }
        } catch (IOException e) {
            System.out.println("Error unblocking user: " + e.getMessage());
            return false;
        }
    }

    //To view and manage blocked users
    public void viewBlockedUsers(String usernameInput) {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return;
        }

        writer.println("viewBlockedUsers" + DELIMITER + usernameInput);

        try {
            String response = reader.readLine(); //To read response from server
            if (response == null) {
                System.out.println("You have not blocked anyone.");
                return;
            }

            String[] blockedUsers = response.split(",");
            Scanner scanner = new Scanner(System.in);

            for (String blockedUser : blockedUsers) {
                System.out.println("Blocked User: " + blockedUser);
                System.out.println("Do you want to unblock this user? (1) Yes or (2) No");
                String choice = scanner.nextLine();

                switch (choice) {
                    case "1":
                        if (unblockUser(usernameInput, blockedUser)) {
                            System.out.println("You have unblocked " + blockedUser);
                        } else {
                            System.out.println("Failed to unblock " + blockedUser);
                        }
                        break;
                    case "2": //In case user changes their mind
                        System.out.println("You chose not to unblock " + blockedUser);
                        break;
                    default:
                        System.out.println("Invalid option. Skipping " + blockedUser);
                }
            }
        } catch (IOException e) {
            System.out.println("Error viewing blocked users: " + e.getMessage());
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
            if (response.equals(FAILURE)) {
                System.out.println("No users found with " + parameter + ": " + value);
            } else {
                System.out.println("Users matching " + parameter + " = " + value + ":\n");
                for (String token : response.split(DELIMITER)) {
                    System.out.println(token);
                }
            }
        } catch (IOException e) {
            System.out.println("Error searching users: " + e.getMessage());
        }
    }

    //To search for roommate based on exact match of preferences

    public void exactMatch() {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return;
        }

        writer.println("exactMatch" + DELIMITER + username);

        try {
            String response = reader.readLine(); //To read response from server
            if (response.equals(FAILURE)) {
                System.out.println("No exact matches found for your preferences.");
            } else {
                String[] tokens = response.split(DELIMITER);
                System.out.println("Exact Matches:\n");
                for (String token : tokens) {
                    System.out.println(token);
                }
            }
        } catch (IOException e) {
            System.out.println("Error finding exact matches: " + e.getMessage());
        }
    }

    //To search for roommate based on exact match of preferences

    public void partialMatch() {
        if (!isConnected) {
            System.out.println("Not connected to server.");
            return;
        }

        writer.println("partialMatch" + DELIMITER + username);

        try {
            String response = reader.readLine(); //To read response from server
            if (response.equals(FAILURE)) {
                System.out.println("No partial matches found for your preferences.");
            } else {
                System.out.println("Partial Matches:\n");
                for (String token : response.split(DELIMITER)) {
                    System.out.println(token);
                }
            }
        } catch (IOException e) {
            System.out.println("Error finding partial matches: " + e.getMessage());
        }
    }

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
