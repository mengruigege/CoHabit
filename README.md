# Phase 3 - L11, Team 02 - CoHabit

**Submitted On Vocareum By
Rui Meng**

**Presentation submitted on Brightspace By
Keya Jadhav**

**Report submitted on Brightspace By
Rithvik Siddenki**

**Note:
This project uses the Maven Build.**

## _**Introduction**_

For this project, we have created our very own social media app - CoHabit. CoHabit is an app that eases the roommate search issue through matching various preferences of users. We have also implemented a direct messaging service for communication amongst app users.

For the implementation of the database side of the app, we have created the necessary classes and interfaces to include all the information about the user information, preferences, and so on as listed below:

1. Blockable.java
2. Client.java
3. ClientService.java
4. Database.java
5. DatabaseFramework.java
6. FriendManageable.java
7. InvalidInput.java
8. Profile.java
9. Relationships.java
10. Searchable.java
11. Server.java
12. ServerService.java
13. User.java
14. UsernameTakenException.java
15. TestClient.java
16. TestDatabase.java
17. TestRelationships.java
18. TestUser.java
19. TestServer.java

## _**Description**_

### **Classes**

<ins> _Summary_ </ins>

| Class                      | Description                                                                           |
|----------------------------|---------------------------------------------------------------------------------------|
| Client.java                  | Class that handles clients who wish to use the features of the app.                 |
| Database.java            | Reads and writes data from .txt files, providing a framework for storing user data.     |
| InvalidInput.java                  | Custom exception class that deals with invalid input.                         |
| User.java                | Class that deals with user characteristics such as preferences, profile image, etc.     |
| Relationships.java            | Allows users to set their relationships with other users of the app.               |
| Server.java                | Class that handles all the computational work of what the client wishes to do by communicating with the database. |
| UsernameTakenException.java | Custom exception class that deals with taken username input during registration.     |

----

**Client.java**

_All Implemented Interfaces:_
ClientService

_Field Summary_

| Modifier and Type               | Field             | Description                                                       |
|----------------------------------|-------------------|-------------------------------------------------------------------|
| private String                   | username          | Stores the user's username                                        |
| private String                   | password          | Stores the user's password                                        |
| private String                   | email             | Stores the user's email                                           |
| private String                   | phoneNumber       | Stores the user's phone number                                    |
| private String                   | userDescription   | Stores a description of the user                                  |
| private String                   | university        | Stores the user's university                                      |
| private byte[]                   | profilePicture    | Stores the user's profile picture in byte array format            |
| private String                   | bedTime           | Stores the user's bedtime                                         |
| private boolean                  | alcohol           | Indicates if the user consumes alcohol                            |
| private boolean                  | smoke             | Indicates if the user smokes                                      |
| private boolean                  | guests            | Indicates if the user allows guests                               |
| private int                       | tidy              | Indicates the user's tidiness level (1-10 scale)                  |
| private int                       | roomHours         | Indicates the number of hours the user spends in their room daily |
| private boolean                  | isConnected       | Indicates if the user is connected to the server                  |
| private Socket                   | socket            | Represents the user's connection socket                           |
| private PrintWriter              | writer            | Writes data to the server                                         |
| private BufferedReader           | reader            | Reads data from the server                                        |
| private Scanner                  | scanner           | Reads input from the user (console)                               |
| private final String             | serverAddress     | The server's address (default is "localhost")                      |
| private final int                | serverPort        | The server's port (default is 1102)                               |
| private static final String      | DELIMITER         | Delimiter used for parsing data ("<<END>>")                        |
| private static final String      | SUCCESS           | Constant for success message ("SUCCESS")                          |
| private static final String      | FAILURE           | Constant for failure message ("FAILURE")                          |

_Constructor Summary_

| Constructor                                                       | Description                                                                                          |
|-------------------------------------------------------------------|------------------------------------------------------------------------------------------------------|
| public Client(String serverAddress, int serverPort)               | Constructs a newly allocated Client object, establishes a connection to the server, and initializes necessary streams. |

_Method Summary_

| Modifier and Type   | Method                         | Description                                                                          |
|---------------------|--------------------------------|--------------------------------------------------------------------------------------|
| void                | start()                        | Starts the application, displays the main menu, and processes user selections for login, registration, or exit. |
| void                | close()                        | Closes the client connection, and releases resources like socket, reader, writer, and scanner. |
| boolean             | mainScreen()                   | Displays the user dashboard with profile and actions, returns false if the main screen is closed. |
| boolean             | login()                        | Handles user login, validates credentials, and retrieves user data upon successful login. |
| boolean             | register()                     | Handles user registration, validates inputs, and uploads profile picture upon successful registration. |
| boolean             | sendMessage()               | Handles sending a message to a selected friend. Validates message input and sends it to the server.             |
| void                | viewMessage()               | Allows the user to view their message history with a selected friend, displaying messages in a scrollable dialog. |
| void                | viewFriendList()             | Displays a scrollable list of the user's friends retrieved from the server.                  |
| void                | viewFriendRequests()         | Displays a list of pending friend requests and allows the user to accept or decline them.    |
| void                | sendFriendRequest()          | Allows the user to send a friend request to another user by entering their username.         |
| void                | removeFriend()               | Allows the user to remove a selected friend from their friend list.                          |
| void                | viewBlockList()       | Displays a list of blocked users, if any, retrieved from the server.                                |
| void                | blockUser()           | Allows the user to block a specified user by entering their username and sending the request to the server.         |
| void                | unblockUser()         | Allows the user to unblock a selected user from the block list and sends the request to the server.  |
| void                | viewProfile()         | Displays the profile of a specified user (including personal info and preferences) retrieved from the server.       |
| void                | updateProfile()                | Updates the user's profile based on input parameters from `JOptionPane` dialogs. |
| void                  | searchRoommates()              | Initiates the search for roommates based on user-selected search criteria.       |
| void                  | searchByParameter()            | Searches for roommates based on a specific parameter (e.g., name, email).       |
| void                | exactMatch()                   | Searches for roommates with an exact match of the username.                     |
| void                 | partialMatch()                 | Searches for roommates with a partial match of the username.                   |
| void                | disconnect()                   | Disconnects the user from the server and closes the socket connection.          |
| boolean           | acceptFriendRequest(String friend) | Accepts a friend request from the specified friend.                             |
| boolean           | declineFriendRequest(String usernameInput) | Declines a friend request from the specified user.                              |
| void                 | searchByParameter(String parameter, String value) | Searches for roommates based on a given parameter and value.           |
| boolean             | setProfilePicture(String filePath) | Uploads the profile picture from the provided file path.                        |

---

**Database.java**

_All Implemented Interfaces:_
DatabaseFramework

_Field Summary_

| Modifier and Type                        | Field                           | Description                                                      |
|------------------------------------------|---------------------------------|------------------------------------------------------------------|
| private static ArrayList<User>           | allUsers                        | Stores a list of all users in the system.                        |
| private static HashMap<User, ArrayList<User>> | allFriends                  | Maps each user to their list of friends.                         |
| private static HashMap<User, ArrayList<String>> | allMessages                | Maps each user to their list of messages.                        |
| private static HashMap<User, ArrayList<User>> | allBlocked                  | Maps each user to their list of blocked users.                   |
| private static HashMap<User, ArrayList<User>> | allFriendRequests           | Maps each user to their list of incoming friend requests.        |
| private static final String              | USERS_FILE                     | The file where user data is stored.                              |
| private static final String              | FRIENDS_FILE                   | The file where friend relationships are stored.                  |
| private static final String              | MESSAGES_FILE                  | The file where messages between users are stored.                |
| private static final String              | BLOCKED_FILE                   | The file where blocked users data is stored.                     |
| private static final String              | FRIEND_REQUESTS_FILE           | The file where friend requests are stored.                       |
| private static final String              | PROFILE_PICTURE_FOLDER         | The folder where user profile pictures are stored.               |
| private static final String              | DELIMITER                      | The delimiter used to separate fields in file data.              |
| private static final Object              | LOCK                            | A lock object used for synchronizing access to shared resources. |

_Constructor Summary_

| Constructor                               | Description                                              |
|-------------------------------------------|----------------------------------------------------------|
| Database()                                | Constructor that initializes allUsers as a new empty ArrayList.          |

_Method Summary_

| Modifier and Type   | Method                            | Description                                                                               |
|---------------------|-----------------------------------|------------------------------------------------------------------------------------------|
| private synchronized | readFile(String file)            | Reads a file and returns an ArrayList of strings containing the file's content.          |
| private synchronized | writeFile(String file, ArrayList<String> data) | Writes the provided data to a file.                                        |
| public              | initializeDatabase()              | Initializes the database by loading users, friends, blocked users, friend requests, and messages.     |
| private synchronized | loadUsers()                       | Loads users from the specified file and adds them to the `allUsers` list.               |
| private synchronized | loadFriends()                     | Loads friends data from the file and populates the `allFriends` map.                    |
| private synchronized | loadBlocked()                     | Loads blocked users from the file and populates the `allBlocked` map.                   |
| private synchronized | loadFriendRequests()              | Loads pending friend requests and populates the `allFriendRequests` map.                |
| private synchronized | loadMessages()                    | Loads messages between users and stores them in the `allMessages` map.                  |
| private synchronized | saveUsers()                       | Saves the list of users to the specified file.                                          |
| private synchronized | saveFriends()                     | Saves the list of friends for each user to the specified file.                          |
| private synchronized | saveBlocked()                     | Saves the list of blocked users for each user to the specified file.                    |
| private synchronized | saveFriendRequests()              | Saves the list of pending friend requests for each user to the specified file.          |
| public synchronized  | saveMessages()                    | Saves all messages between users to the specified file, ensuring that messages are stored in pairs.     |
| public synchronized  | addUser(User user)                | Adds a new user to the database if the username is not already taken, and saves the changes immediately. |
| public synchronized  | addFriend(User user1, User user2) | Adds a friend relationship between two users, ensuring they are not already friends and saving the update.|
| public synchronized  | blockUser(User blocker, User blocked) | Blocks a user and removes them from the friend list if they are friends, and saves the update.       |
| public synchronized  | sendFriendRequest(User sender, User receiver) | Sends a friend request from one user to another, ensuring neither user is blocked and the request doesn't already exist. |
| public synchronized | sendMessage(User sender, User receiver, String message) | Sends a message from the sender to the receiver, stores it, and saves it to the messages file.         |
| public synchronized | removeUser(User user)            | Removes a user from the database, including their friendships, blocks, requests, and messages.          |
| public synchronized | removeFriend(User user1, User user2) | Removes the friendship between two users and saves the updated friendship data.       |
| public synchronized | unblockUser(User blocker, User unblocked) | Unblocks a user and removes them from the blocker's blocked list. Saves the update immediately.        |
| public synchronized | acceptFriendRequest(User receiver, User sender) | Accepts a friend request, adds the users as friends, and removes the request from the receiver's list.  |
| public synchronized | rejectFriendRequest(User receiver, User sender) | Rejects a friend request and removes it from the receiver’s list of requests. Saves the updated list.   |
| public synchronized | getFriends(User user)            | Returns a list of the names of the user's friends.                                        |
| public synchronized | getBlockedUsers(User user)              | Returns a list of the names of users blocked by the given user.                    |
| public synchronized | getMessage(User user1, User user2)      | Returns all messages exchanged between two users, ensuring no duplicates.          |
| public synchronized | getFriendRequests(User user)            | Returns a list of names of users who have sent friend requests to the specified user.                  |
| public synchronized | updateUserInFile(User updatedUser, String oldUsername) | Updates the user’s information in the database file, replacing the old username with updated details.   |
| public synchronized | usernameExists(String username)         | Checks if a username already exists in the database.                               |
| public synchronized | findUserByName(String name)             | Finds and returns a user by their username. Returns null if no user is found with the given name.      |
| public synchronized | saveProfilePicture(User user, byte[] profilePicture) | Saves the user's profile picture to a file.                           |
| public synchronized | loadProfilePicture(User user)           | Loads and returns the user's profile picture as a byte array. Returns null if no picture is found.      |
| public synchronized | deleteProfilePicture(User user)         | Deletes the user's profile picture file.                                           |
| public synchronized | searchByParameter(String parameter, String value, String delimiter) | Searches users based on a given parameter (e.g., name, email) and returns matching results.             |
| public synchronized | exactMatch(User user, String delimiter)     | Finds users that are an exact match to the provided user based on some "perfectMatch" criteria.        |
| public synchronized | partialMatch(User user, String delimiter)   | Finds users who have a partial match with the provided user, and sorts the results by match score.     |

---

**InvalidInput.java**

_All Implemented Interfaces:_
None

_Constructor Summary_

| Constructor                        | Description                                         |
|------------------------------------|-----------------------------------------------------|
| InvalidInput(String message)    | Constructs a new exception indicating invalid input with the provided message. |

---

**User.java**

_All Implemented Interfaces:_
Profile, FriendManageable, Blockable

_Field Summary_

| Modifier and Type | Field          | Description                                                           |
|-------------------|----------------|-----------------------------------------------------------------------|
| private String    | name           | Stores the user's name.                                               |
| private String    | password       | Stores the user's password.                                           |
| private String    | email          | Stores the user's email address.                                      |
| private String    | phoneNumber    | Stores the user's phone number.                                       |
| private String    | description    | Stores a brief description of the user.                               |
| private String    | university     | Stores the user's university name.                                    |
| private byte[]    | profilePicture | Stores the user's profile picture in byte array format.               |
| private String    | bedTime        | Stores the user's preferred bedtime.                                  |
| private boolean   | alcohol        | Stores the user's alcohol consumption preferences (true/false).        |
| private boolean   | smoke          | Stores the user's smoking preferences (true/false).                   |
| private boolean   | guests         | Stores the user's preference for guests (true/false).                 |
| private int       | tidy           | Stores the user's tidiness preference (scale or boolean).             |
| private int       | roomHours      | Stores the number of hours the user spends in their room daily.       |
| private static final String | DELIMITER | Stores the delimiter used to separate fields (constant value).         |


_Constructor Summary_

| Constructor                       | Description                                                          |
|-----------------------------------|----------------------------------------------------------------------|
| User(String name, String password, String email, String phoneNumber, String userDescription, String university) throws UsernameTakenException | Constructs a newly allocated User object with the specified field values and throws a UsernameTakenException if the username is already taken. |


_Method Summary_
           
| Modifier and Type | Method                           | Description                                               |
|-------------------|----------------------------------|-----------------------------------------------------------|
| ArrayList<User>   | getFriendList()                  | Retrieves the list of friends from the `relationships` object. |
| ArrayList<User>   | getBlockedUsers()                | Retrieves the list of blocked users from the `relationships` object. |
| ArrayList<User>   | getOutgoingFriendRequest()       | Retrieves the list of outgoing friend requests from the `relationships` object. |
| ArrayList<User>   | getIncomingFriendRequest()       | Retrieves the list of incoming friend requests from the `relationships` object. |
| void              | setFriendList(ArrayList<User> friends) | Sets the friends list in the `relationships` object. |
| void              | setBlockedUsers(ArrayList<User> blocked) | Sets the blocked users list in the `relationships` object. |
| boolean           | removeFriend(User user)          | Removes a user from the friends list in the `relationships` object. |
| boolean           | addFriend(User user)             | Adds a user to the friends list if not already a friend in the `relationships` object. |
| boolean           | blockUser(User user)             | Blocks a user and removes them from the friends list in the `relationships` object. |
| boolean           | unblockUser(User user)           | Unblocks a user in the `relationships` object. |
| void              | addIncomingRequest(User sender)  | Adds a friend request to incoming requests in the `relationships` object. |
| void              | sendFriendRequest(User receiver) | Sends a friend request to the receiver in the `relationships` object. |
| void              | removeIncomingRequest(User sender) | Removes a friend request from incoming requests in the `relationships` object. |
| void              | removeOutgoingRequest(User receiver) | Removes a friend request from outgoing requests in the `relationships` object. |
| boolean           | acceptFriendRequest(User sender) | Accepts a friend request and adds the sender to the friends list in the `relationships` object. |
| boolean           | declineFriendRequest(User sender) | Declines a friend request and removes it from incoming requests in the `relationships` object. |
| String            | getName()                        | Retrieves the user's name.                                |
| String            | getPassword()                    | Retrieves the user's password.                            |
| String            | getEmail()                       | Retrieves the user's email.                               |
| String            | getPhoneNumber()                 | Retrieves the user's phone number.                        |
| String            | getUniversity()                  | Retrieves the user's university.                          |
| String            | getDescription()                 | Retrieves the user's description.                         |
| String            | getPreferences()                 | Retrieves the user's preferences as a formatted string.   |
| boolean           | isAlcohol()                      | Checks if the user has alcohol preferences.               |
| boolean           | isSmoke()                        | Checks if the user has smoking preferences.               |
| boolean           | isGuests()                       | Checks if the user has guest preferences.                 |
| void              | setName(String name)             | Sets the user's name.                                     |
| void              | setPassword(String pwd)          | Sets the user's password.                                 |
| void              | setEmail(String email)           | Sets the user's email.                                    |
| void              | setPhoneNumber(String phoneNum)  | Sets the user's phone number.                             |
| void              | setUniversity(String university) | Sets the user's university.                               |
| void              | setDescription(String userDesc)  | Sets the user's description.                              |
| byte[]            | getProfilePicture()              | Retrieves the user's profile picture.                     |
| void              | setProfilePicture(byte[] pictureData) | Sets the user's profile picture.                       |
| void              | setPreferences(String bedTime, boolean alcohol, boolean smoke, boolean guests, int tidy, int roomHours) | Sets the user's preferences. |
| void              | setAlcohol(boolean alcohol)      | Sets the user's alcohol preference.                       |
| void              | setSmoke(boolean smoke)          | Sets the user's smoking preference.                       |
| void              | setGuests(boolean guests)        | Sets the user's guest preference.                         |
| void              | setTidy(int tidy)                | Sets the user's tidiness preference (throws `InvalidInput` if invalid). |
| void              | setRoomHours(int roomHours)      | Sets the user's room hours (throws `InvalidInput` if invalid). |
| void              | setIncomingFriendRequest(ArrayList<User> incomingRequests) | Sets the incoming friend requests in the `relationships` object. |
| void              | setOutgoingFriendRequest(ArrayList<User> outgoingRequests) | Sets the outgoing friend requests in the `relationships` object. |
| boolean           | perfectMatch(User user)          | Determines if two users have all the exact same preferences. |
| int               | partialMatch(User user)          | Determines how many preferences two users have in common. |
| String            | getBedTime()                     | Retrieves the user's bed time preference.                 |
| boolean           | getAlcohol()                     | Retrieves the user's alcohol preference.                  |
| boolean           | getSmoke()                       | Retrieves the user's smoking preference.                  |
| boolean           | getGuests()                      | Retrieves the user's guest preference.                    |
| int               | getTidy()                        | Retrieves the user's tidiness preference.                 |
| int               | getRoomHours()                   | Retrieves the user's room hours.                          |
| String            | toString()                       | Returns a string representation of the user object.       |

---

**Relationships.java**

_All Implemented Interfaces_
FriendManageable, Blockable

_Field Summary_

| Modifier and Type         | Field              | Description                                                         |
|---------------------------|--------------------|---------------------------------------------------------------------|
| private ArrayList<User>    | friends            | Stores the list of the user's friends.                              |
| private ArrayList<User>    | blocked            | Stores the list of blocked users for the current user.              |
| private ArrayList<User>    | incomingRequests   | Stores the list of incoming friend requests for the user.           |
| private ArrayList<User>    | outgoingRequests   | Stores the list of outgoing friend requests from the user.          |
| private User               | user               | A reference to the user associated with this relationships object.  |

_Constructor Summary_

| Constructor                                      | Description                                                                                   |
|--------------------------------------------------|-----------------------------------------------------------------------------------------------|
| Relationships(User user, Database database)      | Constructs a newly allocated Relationships object with the specified user and initializes empty lists for friends, blocked users, incoming requests, and outgoing requests. |
| Relationships()                                  | Constructs a newly allocated Relationships object and initializes empty lists for friends, blocked users, incoming requests, and outgoing requests. |

_Method Summary_

| Modifier and Type | Method                               | Description                                                       |
|-------------------|--------------------------------------|-------------------------------------------------------------------|
| boolean           | addFriend(User user)                 | Adds a user to the friends list if not already a friend.         |
| void              | receiveFriendRequest(User sender)    | Receives a friend request from another user.                      |
| boolean           | acceptFriendRequest(User sender)     | Accepts a friend request from a specified user.                   |
| boolean           | declineFriendRequest(User sender)    | Declines a friend request from a specified user.                  |
| void              | setIncomingRequests(ArrayList<User> incomingRequests) | Sets the list of incoming friend requests.                        |
| void              | setOutgoingRequests(ArrayList<User> outgoingRequests) | Sets the list of outgoing friend requests.                        |
| boolean           | addOutgoingRequest(User receiver)    | Adds an outgoing friend request if not already present.           |
| boolean           | addIncomingRequest(User sender)      | Adds an incoming friend request if not already present.           |
| boolean           | removeOutgoingRequest(User receiver) | Removes an outgoing friend request.                               |
| boolean           | removeIncomingRequest(User sender)   | Removes an incoming friend request.                               |
| boolean           | isFriend(User user)                  | Helper method to check if a user is already a friend.             |
| boolean           | hasPendingOutgoingRequest(User user) | Helper method to check for a pending outgoing request.            |
| ArrayList<User>   | getIncomingRequests()                | Returns a list of incoming friend requests.                       |
| ArrayList<User>   | getOutgoingRequests()                | Returns a list of outgoing friend requests.                       |
| boolean           | addFriend(User user)                 | Adds a user to the friends list if not already a friend.         |
| ArrayList<User>   | getFriendList()                      | Returns a list of users who are friends.                          |
| void              | setFriendList(ArrayList<User> friends) | Updates the list of users who are friends.                      |
| ArrayList<User>   | getBlockedUsers()                    | Returns a list of users who are blocked.                          |
| void              | setBlockedUsers(ArrayList<User> blocked) | Updates the list of users who are blocked.                      |
| boolean           | removeFriend(User user)              | Removes a user from the friends list.                             |
| boolean           | blockUser(User user)                 | Blocks a user and removes them from the friends list.             |
| boolean           | unblockUser(User user)               | Unblocks a user by removing from the blocked list.                |
| ArrayList<User>   | getFriends()                         | Returns a list of users who are currently friends.                |
| ArrayList<User>   | getBlocked()                         | Returns a list of users who are currently blocked.                |

---

**Server.java**

_All Implemented Interfaces_
ServerService

_Field Summary_

| Modifier and Type       | Field              | Description                                                                                      |
|-------------------------|--------------------|--------------------------------------------------------------------------------------------------|
| static Database          | database           | A reference to the `Database` object used to interact with the database for user-related actions. |
| private static final String | DELIMITER        | A constant delimiter used for separating fields in string representations of user data.         |
| private static final String | SUCCESS          | A constant string used to denote a successful operation or response.                             |
| private static final String | FAILURE          | A constant string used to denote a failed operation or response.                                 |
| private final Socket     | clientSocket       | A socket for handling communication between the client and the server in a networked environment. |

_Constructor Summary_

| Constructor              | Description                                                                                     |
|--------------------------|-------------------------------------------------------------------------------------------------|
| Server(Socket clientSocket) | Constructs a new `Server` object with a specified `Socket` for client-server communication.      |

_Method Summary_

| Modifier and Type    | Method                         | Description                                                                            |
|----------------------|--------------------------------|----------------------------------------------------------------------------------------|
| public void        | run()                        | Handles communication with the client. Receives and processes various commands such as login, registration, etc. |

----

**UsernameTakenException.java**

_All Implemented Interfaces:_
None

_Constructor Summary_

| Constructor                               | Description                                              |
|-------------------------------------------|----------------------------------------------------------|
| UsernameTakenException(String message)    | Constructs a new exception indicating a taken username.  |

----

### **Interfaces**

1. Blockable.java

* boolean blockUser()
* unblockUser()
* ArrayList<User> getBlockedUsers();

2. ClientService.java

* void start()  
* void close()  
* boolean mainScreen()  
* boolean login()  
* boolean register()  
* boolean sendMessage()  
* void viewFriendRequests()  
* void sendFriendRequest()  
* void removeFriend()  
* void blockUser()  
* void unblockUser()  
* void viewProfile()  
* void updateProfile()  
* void searchRoommates()  
* void disconnect()  
* boolean acceptFriendRequest(String friend)  
* boolean declineFriendRequest(String sender)  
* void searchByParameter(String parameter, String value)  
* void exactMatch()  
* void partialMatch()  
* boolean setProfilePicture(String filePath)

3. DatabaseFramework.java

* boolean addUser(User user)  
* boolean removeUser(User user)  
* boolean usernameExists(String username)  
* User findUserByName(String name)  
* boolean addFriend(User user1, User user2)  
* boolean removeFriend(User user1, User user2)  
* boolean blockUser(User blocker, User blocked)  
* boolean unblockUser(User blocker, User unblocked)  
* boolean sendFriendRequest(User sender, User receiver)  
* boolean acceptFriendRequest(User receiver, User sender)  
* boolean rejectFriendRequest(User receiver, User sender)  
* boolean sendMessage(User sender, User receiver, String message)  
* void saveProfilePicture(User user, byte[] profilePicture)  
* byte[] loadProfilePicture(User user)  
* void deleteProfilePicture(User user)  
* String searchByParameter(String parameter, String value, String delimiter)  
* String exactMatch(User user, String delimiter)  
* String partialMatch(User user, String delimiter)
  
4. FriendManageable.java

* boolean addFriend(User user)  
* boolean removeFriend(User user)  
* boolean blockUser(User user)  
* boolean unblockUser(User user)  
* ArrayList<User> getFriendList()  
* void setFriendList(ArrayList<User> friends)

5. Profile.java

* String getName()  
* String getDescription()  
* String getUniversity()  
* void setName(String newName)  
* void setDescription(String newDesc)  
* void setUniversity(String newUni)  
* byte[] getProfilePicture()  
* void setProfilePicture(byte[] pictureData)
  
6. Searchable.java

* String searchByParameter(String parameter, String value, String delimiter)  
* String exactMatch(User user, String delimiter)  
* String partialMatch(User user, String delimiter)

7. ServerService.java

* String login(String username, String password)  
* boolean register(User user)  
* boolean sendMessage(User sender, User receiver, String message)  
* ArrayList<String> getMessageHistory(User user1, User user2)  
* boolean sendFriendRequest(User sender, User receiver)  
* ArrayList<String> viewFriendRequests(User user)  
* boolean declineFriendRequest(User receiver, User sender)  
* boolean acceptFriendRequest(User receiver, User sender)  
* boolean removeFriend(User remover, User removed)  
* boolean blockUser(User blocker, User blocked)  
* boolean unblockUser(User unblocker, User unblocked)  
* ArrayList<String> viewBlockedUsers(User user)  
* ArrayList<String> viewFriendsList(User user)  
* String viewProfile(String username)  
* String partialMatch(User user)  
* String exactMatch(User user)  
* String searchByParameter(String parameter, String value)

---

### **Test Classes**

1. TestClient.java

| **Test Name**              | **Functionality**                                                                                         |
|----------------------------|----------------------------------------------------------------------------------------------------------|
| testStart()               | Verifies that no exception is thrown when starting the client application.                                 |
| testClose()               | Verifies that the client can close without exceptions and the connection is properly terminated.          |
| `testMainScreen()`          | Verifies that the main screen closes and returns `false` if the client is disconnected.                   |
| `testLogin()`               | Verifies that login fails when dummy inputs are used or when disconnected.                                |
| `testRegister()`            | Verifies that registration fails with dummy inputs or when disconnected.                                 |
| `testSendMessage()`         | Verifies that sending a message fails without a recipient or message content.                            |
| `testViewMessage()`         | Verifies that no exceptions are thrown when viewing messages.                                             |
| `testViewFriendList()`      | Verifies that no exceptions are thrown when viewing the friend's list.                                    |
| `testViewFriendRequests()`  | Verifies that no exceptions are thrown when viewing pending friend requests.                              |
| `testSendFriendRequest()`   | Verifies that no exceptions are thrown when sending a friend request.                                     |
| `testRemoveFriend()`        | Verifies that no exceptions are thrown when removing a friend.                                           |
| `testViewBlockList()`       | Verifies that no exceptions are thrown when viewing the blocked users list.                              |
| `testBlockUser()`           | Verifies that no exceptions are thrown when blocking a user.                                             |
| `testUnblockUser()`         | Verifies that no exceptions are thrown when unblocking a user.                                           |
| `testViewProfile()`         | Verifies that no exceptions are thrown when viewing a profile.                                           |
| `testUpdateProfile()`       | Verifies that no exceptions are thrown when updating a profile.                                          |
| `testSearchRoommates()`     | Verifies that no exceptions are thrown when searching for roommates.                                      |
| `testExactMatch()`          | Verifies that no exceptions are thrown when performing an exact match search.                            |
| `testPartialMatch()`        | Verifies that no exceptions are thrown when performing a partial match search.                           |
| `testDisconnect()`          | Verifies that the client is properly disconnected and no longer connected.                               |
| `testSetProfilePicture()`   | Verifies that setting a profile picture fails when provided with an invalid file path.                   |


2. TestDatabase.java

| **Test Name**                             | **Functionality**                                                                          |
|-------------------------------------------|--------------------------------------------------------------------------------------------|
| testAddUserValid()                        | Verifies that a user is added successfully to the database.                                |
| testAddUserDuplicate()                    | Verifies that attempting to add a duplicate user fails.                                    |
| testAddUserNull()                         | Verifies that attempting to add a null user fails.                                         |
| testFindUserByNameValid()                 | Verifies that the correct user is returned when searching by name.                         |
| testFindUserByNameInvalid()               | Verifies that null is returned when searching for a non-existent user.                     |
| testFindUserByNameNull()                  | Verifies that null is returned when the name parameter is null.                            |
| testSendFriendRequestValid()              | Verifies that a valid friend request is successfully sent.                                 |
| testSendFriendRequestDuplicate()          | Verifies that sending a duplicate friend request fails.                                    |
| testSendFriendRequestBlocked()            | Verifies that a friend request cannot be sent to a blocked user.                           |
| testAcceptFriendRequestValid()            | Verifies that a valid friend request is accepted successfully.                             |
| testAcceptFriendRequestNoRequest()        | Verifies that accepting a non-existent friend request fails.                               |
| testAcceptFriendRequestInvalidUsers()     | Verifies that accepting a friend request with invalid users fails.                                          |
| testRejectFriendRequestValid()            | Verifies that a valid friend request is rejected successfully.                                              |
| testRejectFriendRequestInvalidUsers()     | Verifies that rejecting a friend request with invalid users fails.                                          |
| testRemoveUserValid()                     | Verifies that a user is removed successfully from the database.                                              |
| testRemoveUserNonExistent()               | Verifies that attempting to remove a non-existent user fails.                                               |
| testRemoveUserNull()                      | Verifies that attempting to remove a null user fails.                                                      |
| testBlockUserValid()                      | Verifies that a user is successfully blocked.                                                                |
| testBlockUserAlreadyBlocked()             | Verifies that attempting to block a user who is already blocked fails.                                      |
| testBlockUserInvalid()                    | Verifies that blocking a null user fails.                                                                  |
| testUnblockUserValid()                    | Verifies that a user is successfully unblocked.                                                              |
| testUnblockUserNotBlocked()               | Verifies that attempting to unblock a user who is not blocked fails.                                        |
| testUnblockUserInvalid()                  | Verifies that unblocking a null user fails.                                                                |
| testSendMessageValid()                    | Verifies that a message is sent successfully between two users.                                             |
| testSendMessageEmpty()                    | Verifies that sending an empty message fails.                                                               |
| testSendMessageInvalidUser()              | Verifies that attempting to send a message with a null sender fails.                                        |
| testGetMessageWithMessages()              | Verifies that messages are retrieved correctly when there are messages.                                      |
| testGetMessageNoMessages()                | Verifies that no messages are returned if no messages exist between users.                                  |
| testGetMessageInvalidUsers()              | Verifies that an empty list is returned when invalid users are provided.                                    |
| testPartialMatchWithResults()             | Verifies that a partial match search returns users with matching preferences.                               |
| testPartialMatchNoResults()               | Verifies that a partial match search returns an empty result if no matches are found.                       |
| testPartialMatchNullUser()                | Verifies that a null user in partial match search results in null.                                          |
| testExactMatchWithResults()               | Verifies that an exact match search returns users with exactly matching preferences.                        |
| testExactMatchNoResults()                 | Verifies that an exact match search returns an empty result if no exact matches are found.                  |
| testExactMatchNullUser()                  | Verifies that an exact match search with a null user results in null.                                        |

3. TestRelationships.java

| **Test Name**                                | **Functionality**                                                                                          |
|---------------------------------------------|-----------------------------------------------------------------------------------------------------------|
| testSendFriendRequestValid()                | Verifies that a valid friend request is sent successfully and added to the outgoing requests list.          |
| testSendFriendRequestAlreadyFriend()        | Verifies that sending a friend request to an existing friend fails.                                          |
| testSendFriendRequestDuplicateRequest()     | Verifies that sending a duplicate friend request fails.                                                      |
| testReceiveFriendRequestValid()             | Verifies that a friend request is received successfully and added to the incoming requests list.            |
| testReceiveFriendRequestDuplicateRequest()  | Verifies that receiving a duplicate friend request does not add it again to the incoming requests list.     |
| testAcceptFriendRequestValid()              | Verifies that accepting a valid friend request adds the user as a friend and removes the request from the incoming list. |
| testAcceptFriendRequestNoRequest()          | Verifies that attempting to accept a non-existent friend request fails.                                       |
| testAcceptFriendRequestDuplicateFriend()    | Verifies that accepting a friend request from a user who is already a friend fails.                         |
| testDeclineFriendRequestValid()             | Verifies that declining a friend request removes it from the incoming requests list.                         |
| testDeclineFriendRequestNoRequest()         | Verifies that declining a non-existent friend request fails.                                                |
| testAddFriendValid()                        | Verifies that adding a friend successfully adds the user to the friend list.                                |
| testAddFriendDuplicate()                    | Verifies that adding a user who is already a friend fails.                                                  |
| testRemoveFriendValid()                     | Verifies that removing a friend successfully removes them from the friend list.                             |
| testRemoveFriendNotFriend()                 | Verifies that attempting to remove a user who is not a friend fails.                                         |
| testBlockUserValid()                        | Verifies that blocking a user successfully adds them to the blocked list.                                   |
| testBlockUserAlreadyBlocked()               | Verifies that attempting to block a user who is already blocked fails.                                      |
| testBlockUserUnfriend()                     | Verifies that blocking a friend removes them from the friend list.                                           |
| testUnblockUserValid()                      | Verifies that unblocking a user successfully removes them from the blocked list.                             |
| testUnblockUserNotBlocked()                 | Verifies that attempting to unblock a user who is not blocked fails.                                        |
| testHasPendingOutgoingRequestValid()        | Verifies that checking for a pending outgoing friend request returns true if one exists.                    |
| testHasPendingOutgoingRequestNoRequest()    | Verifies that checking for a pending outgoing friend request returns false if no request exists.            |
| testGetIncomingRequests()                   | Verifies that the list of incoming friend requests is returned correctly.                                   |
| testGetOutgoingRequests()                   | Verifies that the list of outgoing friend requests is returned correctly.                                   |
| testGetFriendList()                         | Verifies that the list of friends is returned correctly.                                                    |
| testGetBlockedUsers()                       | Verifies that the list of blocked users is returned correctly.                                              |


4. TestServer.java

| **Test Name**                                      | **Functionality**                                                                                       |
|---------------------------------------------------|--------------------------------------------------------------------------------------------------------|
| testLoginValidCredentials()                       | Verifies login with valid credentials (username and password).                                          |
| testLoginInvalidPassword()                        | Verifies login with an invalid password for an existing user.                                           |
| testLoginNonExistentUser()                        | Verifies login with a non-existent user.                                                                |
| testRegisterExistingUser()                        | Verifies registering an already existing user fails.                                                     |
| testRegisterInvalidUser()                         | Verifies registering an invalid user (e.g., missing required data) fails.                               |
| testSendMessageValid()                            | Verifies sending a valid message between users works correctly.                                         |
| testSendMessageToNonExistentUser()                | Verifies sending a message to a non-existent user fails.                                                 |
| testSendMessageEmptyMessage()                     | Verifies sending an empty message fails.                                                                |
| testGetMessageHistoryValidUsers()                 | Verifies getting the message history between two users works correctly when there are messages.         |
| testGetMessageHistoryNoMessages()                 | Verifies getting the message history between two users returns an empty list when no messages exist.    |
| testGetMessageHistoryNonExistentUser()            | Verifies getting the message history with a non-existent user returns an empty list.                    |
| testSendFriendRequestValid()                      | Verifies sending a valid friend request works correctly.                                                |
| testSendFriendRequestNonExistentUser()            | Verifies sending a friend request to a non-existent user fails.                                          |
| testSendFriendRequestDuplicate()                  | Verifies sending a duplicate friend request fails.                                                      |
| testViewFriendRequestsWithPending()               | Verifies viewing pending friend requests works correctly for a user with requests.                      |
| testViewFriendRequestsNoRequests()                | Verifies viewing friend requests returns an empty list for a user with no pending requests.             |
| testViewFriendRequestsNonExistentUser()           | Verifies viewing friend requests for a non-existent user returns an empty list.                         |
| testDeclineFriendRequestValid()                   | Verifies declining a friend request works correctly.                                                    |
| testDeclineFriendRequestNonExistentUser()         | Verifies declining a friend request with a non-existent user fails.                                      |
| testAcceptFriendRequestValid()                    | Verifies accepting a valid friend request works correctly.                                               |
| testAcceptFriendRequestNoRequest()                | Verifies accepting a friend request with no pending request fails.                                       |
| testAcceptFriendRequestNonExistentUser()          | Verifies accepting a friend request for a non-existent user fails.                                       |
| testRemoveFriendValid()                           | Verifies removing a friend works correctly.                                                              |
| testRemoveFriendNotFriends()                      | Verifies removing a user who is not a friend fails.                                                     |
| testRemoveFriendNonExistentUser()                 | Verifies removing a non-existent user as a friend fails.                                                 |
| testBlockUserValid()                              | Verifies blocking a user works correctly.                                                                |
| testBlockUserAlreadyBlocked()                     | Verifies blocking an already blocked user fails.                                                        |
| testBlockUserNonExistent()                        | Verifies blocking a non-existent user fails.                                                            |
| testUnblockUserValid()                            | Verifies unblocking a user works correctly.                                                              |
| testUnblockUserNotBlocked()                       | Verifies unblocking a user who is not blocked fails.                                                    |
| testUnblockUserNonExistent()                      | Verifies unblocking a non-existent user fails.                                                          |
| testViewBlockedUsersWithBlocked()                 | Verifies viewing blocked users works correctly when users are blocked.                                   |
| testViewBlockedUsersNoBlocked()                   | Verifies viewing blocked users returns an empty list when no users are blocked.                         |
| testViewBlockedUsersNonExistentUser()             | Verifies viewing blocked users for a non-existent user returns an empty list.                           |
| testViewFriendsListWithFriends()                  | Verifies viewing friends list works correctly when users have friends.                                   |
| testViewFriendsListNoFriends()                    | Verifies viewing friends list returns an empty list when no friends are added.                          |
| testViewFriendsListNonExistentUser()              | Verifies viewing the friends list for a non-existent user returns an empty list.                        |
| testPartialMatchWithMatches()                     | Verifies that partial match returns some result when there are matching users.                          |
| testPartialMatchNonExistentUser()                 | Verifies partial match with a non-existent user returns null.                                            |
| testExactMatchWithMatches()                       | Verifies that exact match returns some result when there are matching users.                            |
| testExactMatchNoMatches()                         | Verifies exact match with no matches returns an empty string.                                            |
| testExactMatchNonExistentUser()                   | Verifies exact match with a non-existent user returns null.                                              |


5. TestUser.java

| **Test Name**                                      | **Functionality**                                                                                       |
|---------------------------------------------------|--------------------------------------------------------------------------------------------------------|
| testConstructor1()                                 | Verifies correct initialization of the `User` object with valid input for all fields.                    |
| testConstructor2()                                 | Verifies correct initialization of another `User` object with valid input.                              |
| testConstructor3()                                 | Verifies behavior when the `User` object is initialized with `null` for the name field.                  |
| testConstructor4()                                 | Verifies behavior when the `User` object is initialized with `null` for the password field.              |
| testSetNameUser1()                                 | Verifies that the `setName` method updates the `name` field correctly.                                   |
| testSetNameUser2()                                 | Verifies that the `setName` method works correctly with a new name for the second user.                  |
| testSetNameUser3()                                 | Verifies setting a name for a `User` object with `null` as the original name.                            |
| testSetNameUser4()                                 | Verifies setting a new name for the fourth user.                                                        |
| testSetPasswordUser1()                             | Verifies that the `setPassword` method updates the `password` field correctly.                           |
| testSetPasswordUser2()                             | Verifies that setting the `password` to `null` works as expected.                                        |
| testSetPasswordUser3()                             | Verifies that setting the `password` to `null` works as expected for a `User` object initialized with a password. |
| testSetPasswordUser4()                             | Verifies that the `setPassword` method works for setting a new password.                                |
| testSetEmailUser1()                                | Verifies the `setEmail` method correctly updates the email field.                                         |
| testSetEmailUser2()                                | Verifies the `setEmail` method updates the email for the second user correctly.                          |
| testSetEmailUser3()                                | Verifies that the `setEmail` method correctly updates the email when the user’s initial email is `null`.  |
| testSetEmailUser4()                                | Verifies that the `setEmail` method works for the fourth user.                                           |
| testSetPhoneNumberUser1()                          | Verifies the `setPhoneNumber` method works to update the phone number.                                   |
| testSetPhoneNumberUser2()                          | Verifies the `setPhoneNumber` method updates the phone number for the second user correctly.             |
| testSetPhoneNumberUser3()                          | Verifies the behavior when setting `null` for the phone number.                                           |
| testSetAndGetProfilePicture()                      | Verifies that a profile picture can be set and retrieved correctly.                                      |
| testSetProfilePictureWithNull()                    | Verifies setting the profile picture to `null` works correctly.                                           |
| testSetProfilePictureWithEmptyArray()              | Verifies setting the profile picture with an empty byte array works correctly.                           |
| testOverwriteProfilePicture()                      | Verifies overwriting a profile picture works correctly.                                                  |
| testAddFriend()                                    | Verifies the `addFriend` method works correctly when adding a new friend.                                |
| testRemoveFriend()                                 | Verifies the `removeFriend` method works correctly by removing an added friend.                          |
| testUnblockUser()                                  | Verifies that the `unblockUser` method works correctly after blocking a user.                            |
| testPerfectMatch()                                 | Verifies that two users are a perfect match if their preferences completely align.                       |
| testNotPerfectMatch()                              | Verifies that two users are not a perfect match if their preferences differ.                             |
| testPartialMatch()                                 | Verifies that the `partialMatch` method returns the correct number of matching preferences.              |
| testNoPartialMatch()                               | Verifies that the `partialMatch` method returns 0 when there are no matching preferences.               |
| testSetPhoneNumberUser4()                          | Verifies setting the phone number to `null` works correctly for the fourth user.                         |
| testSetPreference1()                               | Verifies setting preferences correctly for the first user.                                               |
| testSetPreference2()                               | Verifies setting preferences correctly for the second user.                                              |
| testSetPreference3()                               | Verifies setting preferences correctly for the third user.                                               |
| testSetPreference4()                               | Verifies setting preferences correctly for the fourth user.                                              |
| testToString()                                     | Verifies that the `toString` method returns the correct formatted string for a `User` object.            |
| testToString2()                                    | Verifies the `toString` method returns the correct formatted string for another `User` object.           |
| testToString3()                                    | Verifies the `toString` method works for a `User` object with `null` values for some fields.              |
| testToString4()                                    | Verifies the `toString` method works correctly for a user with missing password.                         |

----
