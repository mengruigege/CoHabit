# Phase 2 - L11, Team 02 - CoHabit

**Submitted On Vocareum By
Rui Meng**

## _**Introduction**_

For this project, we have created a few additional classes for the second phase of the creation of our very own social media app - CoHabit. CoHabit is an app that eases the roommate search issue through matching various preferences of users. We have also implemented a direct messaging service for communication amongst app users.

For the implementation of the database side of the app, we have created the necessary classes and interfaces to include all the information about the user information, preferences, and so on as listed below:

1. Chat.java
2. FriendList.java
3. User.java
4. UserDatabase.java
5. UserSearch.java
6. Client.java
7. Server.java
8. UsernameTakenException.java
9. InvalidInputException.java
10. Blockable.java
11. FriendManageable.java
12. Message.java
13. Profile.java
14. Searchable.java
15. Sendable.java
16. ClientService.java
17. ServerService.java
18. TestChat.java
19. TestUser.java
20. TestUserSearch.java
21. TestDatabase.java
22. TestFriendList.java
23. TestClient.java
24. TestServer.java

## _**Description**_

### **Classes**

<ins> _Summary_ </ins>

| Class                      | Description                                                                           |
|----------------------------|---------------------------------------------------------------------------------------|
| Chat.java                  | Enables users to send, receive, and delete direct messages.                          |
| FriendList.java            | Enables users to add, remove, and block users in their “Friends” list.              |
| User.java                  | Enables users to create a profile containing personal details and check compatibility.|
| Database.java              | Reads and writes data from .txt files, providing a framework for storing user data.  |
| UserSearch.java            | Allows users to search for other users in the database and apply filters based on preferences. |
| Client.java                | Allows users to access the features of the application without doing any computation. |
| Server.java                | Handles the computation of the features that a user wants to access in the app.       |

**Chat.java**

_All Implemented Interfaces:_
Sendable

_Field Summary_

| Modifier and Type            | Field        | Description                                                  |
|------------------------------|--------------|--------------------------------------------------------------|
| private User                 | sender       | Indicates which user is sending a direct message             |
| private User                 | receiver     | Indicates which user is receiving a direct message           |
| private ArrayList<String>    | messages     | Stores all messages in the direct message chain              |

_Constructor Summary_

| Constructor                              | Description                                                |
|------------------------------------------|------------------------------------------------------------|
| Chat(User sender, User receiver, ArrayList<String> messages) | Constructs a newly allocated Chat object with specified values. |

_Method Summary_

| Modifier and Type   | Method                             | Description                                                                      |
|---------------------|------------------------------------|----------------------------------------------------------------------------------|
| boolean             | sendMessage(User sender, User receiver, String message) | Adds a message to the messages list, returns true if added successfully.        |
| ArrayList<String>   | getMessages()                     | Returns an ArrayList containing all messages in the chat.                        |
| boolean             | deleteMessage(User sender, User receiver, String message) | Deletes a message from the messages list, returns true if removed successfully. |

_Inner Classes_

SendMessageTask
This inner class handles sending messages in a multi-threaded environment by implementing Runnable.

| Modifier and Type | Method     | Description                                              |
|-------------------|------------|----------------------------------------------------------|
| SendMessageTask(User sender, User receiver, String message) | Stores parameters as instance variables.                |
| void              | run()      | Adds a message to the messages list within a synchronized block. |


DeleteMessageTask
This inner class handles deleting messages in a multi-threaded environment by implementing Runnable.

| Modifier and Type | Method     | Description                                              |
|-------------------|------------|----------------------------------------------------------|
| DeleteMessageTask(User sender, User receiver, String message) | Stores parameters as instance variables.                |
| void              | run()      | Deletes a message from the messages list within a synchronized block. |

_Testing_
Chat.java is tested by unit tests included in the class TestChat.java.

<ins> Constructor Tests: </ins>
_Initialization with Messages:_ 
Verifies that the constructor correctly initializes the chat with a provided list of messages, checking both the count and content.

_Empty List Handling:_ 
Ensures that an empty list initializes the chat with no messages.

_Null Handling:_
Confirms that passing null to the constructor results in an empty message list.

_Message Sending Tests:_
Single Message: Tests sending a single message, verifying it is added to the chat.
Multiple Messages: Confirms that multiple messages can be sent and stored in the correct order.
Null Message Handling: Checks that attempting to send a null message does not add anything to the chat.

_Message Deletion Tests:_
Successful Deletion: Validates that a specific message can be deleted and that the remaining messages are as expected.
No Match for Deletion: Tests the behavior when trying to delete a non-existent message, ensuring the message list remains unchanged.
Empty Chat Deletion: Verifies that attempting to delete from an empty chat returns false and keeps the chat empty.

---

**Relationships.java**

_All Implemented Interfaces:_
FriendManageable, Blockable

_Field Summary_

| Modifier and Type | Field      | Description                                                         |
|-------------------|------------|---------------------------------------------------------------------|
| private ArrayList<User> | friends     | Stores a list of the users who are friends of the current user.    |
| private ArrayList<User> | blocked     | Stores a list of the users who are blocked by the current user.     |
| private User      | user       | Holds a reference to the current user.                              |
| private Database  | database   | Database object used to load and save friends and blocked users.    |

_Constructor Summary_

| Constructor                                 | Description                                              |
|---------------------------------------------|----------------------------------------------------------|
| Relationships(User user, Database database) | Constructs a newly allocated FriendList object with the specified field values. |
| Relationships()                             | Helps deal with null values in UserSearch.java and FriendList.java|

_Method Summary_

| Modifier and Type | Method                     | Description                                               |
|-------------------|---------------------------|-----------------------------------------------------------|
| boolean           | addFriend(User user)      | Adds a user to the friends list if not already a friend. |
| boolean           | removeFriend(User user)   | Removes a user from the friends list.                     |
| boolean           | blockUser(User user)      | Blocks a user and removes them from the friends list.     |
| boolean           | unblockUser(User user)    | Unblocks a user.                                          |
| ArrayList<User>   | getFriends()              | Returns a list of users who are currently friends.        |
| ArrayList<User>   | getBlocked()              | Returns a list of users who are currently blocked.        |
| ArrayList<User>   | getFriendList()           | Returns a list of users who are friends from FriendList   |
| void              | setFriendList(ArrayList<User> friends) | Updates a list of users who are friends from FriendList |
| ArrayList<User>   | getBlockedUsers()         | Returns a list of users who are blocked from FriendList   |
| void              | setBlockedUsers(ArrayList<User> blocked)| Updates a list of users who are blocked from FriendList |

_Inner Classes_

UserTask 
This inner class handles tasks related to friends in a multi-threaded environment by implementing Runnable.

| Modifier and Type | Method     | Description                                              |
|-------------------|------------|----------------------------------------------------------|
||UserTask(User user, String action) | Stores parameters as instance variables. |
| void              | run()      | Executes the corresponding method in a multi-threaded scope. |

_Testing_

FriendList.java is tested by unit tests included in the class TestFriendList.java.

**Friend Addition Tests**

testAddFriendSuccessfully: Verifies successful addition of a user to the friend list.
testAddFriendAlreadyInList: Confirms that adding an already-friended user fails.
testAddFriendNullUser: Checks that adding a null user returns false.

**Friend Removal Tests**

testRemoveFriendSuccessfully: Validates the successful removal of a user from the friend list.
testRemoveFriendNotInList: Ensures that attempting to remove a non-friend returns false.
testRemoveFriendNullUser: Confirms that removing a null user returns false.

**User Blocking Tests**

testBlockUserSuccessfully: Tests successful blocking of a user and removal from the friend list.
testBlockUserAlreadyBlocked: Checks that blocking an already blocked user fails.
testBlockUserNullUser: Ensures that blocking a null user returns false.

**User Unblocking Tests**

testUnblockUserSuccessfully: Validates successful unblocking of a user.
testUnblockUserNotBlocked: Confirms that unblocking a non-blocked user returns false.
testUnblockUserNullUser: Checks that unblocking a null user returns false.

**Retrieve Friends Tests**

testGetFriendsAfterAdding: Verifies retrieval of friends after adding users.
testGetFriendsEmptyList: Confirms that the list is empty initially.
testGetFriendsAfterRemoval: Checks that a removed user is no longer in the friends list.

**Retrieve Blocked Users Tests**

testGetBlockedAfterBlocking: Validates retrieval of blocked users after blocking.
testGetBlockedEmptyList: Ensures the blocked list is empty initially.
testGetBlockedAfterUnblocking: Confirms that an unblocked user is removed from the blocked list.

---

**User.java**

_All Implemented Interfaces:_
Profile, FriendManageable, Blockable

_Field Summary_

| Modifier and Type | Field      | Description                                                         |
|-------------------|------------|---------------------------------------------------------------------|
| private String    | name       | Stores username.                                                    |
| private String    | password   | Stores user’s password.                                            |
| private String    | email      | Stores user’s email.                                               |
| private String    | phoneNumber| Stores user’s phone number.                                       |
| private String    | description| Stores user’s description.                                       |
| private String    | university | Stores user’s university.                                        |
| private String    | bedTime    | Stores user’s bedtime preferences.                               |
| private boolean   | alcohol    | Stores user’s alcohol preferences.                               |
| private boolean   | smoke      | Stores user’s smoking preferences.                               |
| private boolean   | guests     | Stores user’s guest frequency preferences.                       |
| private int       | tidy       | Stores user’s tidiness level.                                    |
| private int       | roomHours  | Stores number of hours the user stays in their room.            |
| private FriendList| friendUsers| Stores a list of friends associated with the user.              |
| private FriendList| blockedUsers| Stores the blocked list of the user.                            |


_Constructor Summary_

| Constructor                               | Description                                              |
|-------------------------------------------|----------------------------------------------------------|
| User(String name, String password, String email, String phoneNumber, String userDescription, String university) | Constructs a newly allocated User object with the specified field values. |

_Method Summary_
           
| Modifier and Type | Method                     | Description                                               |
|-------------------|---------------------------|-----------------------------------------------------------|
| boolean           | addFriend(User user)      | Adds a user to the friends list if not already a friend. |
| boolean           | removeFriend(User user)   | Removes a user from the friends list.                     |
| boolean           | blockUser(User user)      | Blocks a user and removes them from the friends list.     |
| boolean           | unblockUser(User user)    | Unblocks a user.                                          |
| ArrayList<User>   | getFriendList(User user)  | Retrieves the list of friends.                            |
| ArrayList<User>   | getBlockedUsers()         | Retrieves the list of blocked users.                      |
| void              | setFriendList(ArrayList<User> friends) | Sets the friends list.                          |
| void              | setBlockedUsers(ArrayList<User> blocked) | Sets the blocked users list.                     |
| private String    | getName()                 | Retrieves the user's name.                                |
| private String    | getPassword()             | Retrieves the user’s password.                            |
| private String    | getEmail()                | Retrieves the user’s email address.                       |
| private String    | getPhoneNumber()          | Retrieves the user’s phone number.                        |
| private String    | getUniversity()           | Retrieves the user’s university.                          |
| private String    | getDescription()          | Retrieves the user’s description.                         |
| private String    | getPreferences()          | Retrieves the user’s preferences.                         |
| void              | setName(String name)      | Updates the user's name.                                  |
| void              | setPassword(String pwd)   | Updates the user's password.                               |
| void              | setEmail(String email)    | Updates the user's email address.                         |
| void              | setPhoneNumber(String phoneNum) | Updates the user's phone number.                    |
| void              | setUniversity(String university) | Updates the user's university information.         |
| void              | setDescription(String userDesc) | Updates the user's description.                      |
| void              | setPreferences(String bedTime, boolean alcohol, boolean smoke, boolean guests, int tidy, int roomHours) | Sets multiple lifestyle preferences for the user. |
| private boolean   | perfectMatch(User user)   | Compares this user’s preferences with another user’s.   |
| private int       | partialMatch(User user)   | Compares this user’s preferences with another user’s.   |
| private String    | toString()                | Returns a string representation of the user object.      |

_Testing_
User.java is tested by unit tests included in the class TestUser.java.

The TestUser class comprehensively validates the User class's functionality, covering:
1. Constructor behavior with various input scenarios.
2. Setters for each attribute, including handling of null values.
3. The setting and retrieval of user preferences.
4. The correctness of the String representation of user objects.

---

**Database.java**

_All Implemented Interfaces:_
None.

_Field Summary_

| Modifier and Type | Field      | Description                                                         |
|-------------------|------------|---------------------------------------------------------------------|
| private ArrayList<User> | users  | Stores a list of users in the database.                             |
| private ArrayList<Chat> | chats  | Stores a list of chats in the database.                             |
| private ArrayList<FriendList> | friendLists | Stores a list of friend lists in the database.               |

_Constructor Summary_

| Constructor                               | Description                                              |
|-------------------------------------------|----------------------------------------------------------|
| Database()                                | Constructs a newly allocated Database object.           |

_Method Summary_

| Modifier and Type | Method                     | Description                                               |
|-------------------|---------------------------|-----------------------------------------------------------|
| void              | loadUsers(String fileName) | Loads users from the specified file.                     |
| void              | saveUsers(String fileName) | Saves users to the specified file.                       |
| ArrayList<User>   | getUsers()                | Returns a list of all users in the database.             |
| void              | loadChats(String fileName) | Loads chats from the specified file.                     |
| void              | saveChats(String fileName) | Saves chats to the specified file.                       |
| ArrayList<Chat>   | getChats()                | Returns a list of all chats in the database.             |
| void              | loadFriendLists(String fileName) | Loads friend lists from the specified file.         |
| void              | saveFriendLists(String fileName) | Saves friend lists to the specified file.             |
| ArrayList<FriendList> | getFriendLists()      | Returns a list of all friend lists in the database.      |

_Testing_

Database.java is tested by unit tests included in the class TestDatabase.java.

1. testAddUser: Verifies that user1 can be added successfully and checks if the name matches.
2. testDeleteUser: Confirms that user1 can be deleted and is no longer in the user list.
3. testAddFriend: Checks if friendship can be established between user1 and user2.
4. testUsernameExists: Validates that the username "Bob" exists in the database.
5. testFindUserByName: Ensures that retrieving user1 by name returns the correct user object.

---

**UserSearch.java**

_All Implemented Interfaces_
Searchable

_Field Summary_

| Modifier and Type | Field      | Description                                                         |
|-------------------|------------|---------------------------------------------------------------------|
| private Database   | database   | Reference to the database to perform user searches.                 |
| private ArrayList<User> | foundUsers | List of users that match search criteria.                        |

_Constructor Summary_

| Constructor                               | Description                                              |
|-------------------------------------------|----------------------------------------------------------|
| UserSearch()             | Constructs a newly allocated UserSearch object with a reference to the database. |

_Method Summary_

| Modifier and Type | Method                     | Description                                               |
|-------------------|---------------------------|-----------------------------------------------------------|
| ArrayList<User>   | searchByParameter(String parameter, String value) | Searches for users by parameters such as name, university, etc and returns a list of matching users. |
| ArrayList<User>   | exactMatch(User mainUser)| Returns ArrayList of all users who are an exact match based on the search |
| ArrayList<User>   | partialMatch(User mainUser) | Returns ArrayList of all users who are a partial match based on the search |

_Testing_
UserSearch.java is tested by unit tests included in the class TestUserSearch.java.

1. testSearchParameter:
Validates searching users by various parameters (name, password, email, phone, description, university) and checks that the correct user details are returned for each search.

2. testExactMatch:
Confirms that the exactMatch method retrieves user1 correctly, verifying that the match is based on the entire user object.

3. testPartialMatch:
Checks the partialMatch method to ensure it can identify user2 as a partial match for user1, indicating that user matching is functioning as expected.

---

**Client.java**

_All Implemented Interfaces_
ClientService

_Field Summary_

| Modifier and Type          | Field             | Description                                                              |
|----------------------------|-------------------|---------------------------------------------------------------------------|
| private User               | currentUser       | Stores the user who is currently accessing the app.                      |
| private boolean            | isConnected       | Checks if the server and client are connected to each other.             |
| private Socket             | socket            | Establishes an endpoint for communication between server and client.     |
| private PrintWriter        | out               | Represents an output stream connected to the network socket.             |
| private BufferedReader     | in                | Represents an input stream connected to a socket.                        |
| private final String       | serverAddress     | Stores hostname/IP address of server.                                    |
| private final int          | serverPort        | Stores the port on which the server is configured.                       |

_Constructor Summary_

| Constructor        | Description                                                                                           |
|--------------------|-------------------------------------------------------------------------------------------------------|
| Client(User user) | Constructs a newly allocated Client object and initializes the field values specified by input parameters. |

_Method Summary_

| Modifier and Type          | Method Name               | Description                                                            |
|----------------------------|---------------------------|------------------------------------------------------------------------|
| public boolean             | connect                   | To connect the client to the server.                                   |
| public void                | disconnect                | To disconnect the client from the server.                              |
| public boolean             | login                     | To allow users to login to their CoHabit account.                      |
| public boolean             | register                  | To allow users to register or sign up for the app.                     |
| public boolean             | sendMessage               | To send messages to other users.                                       |
| public boolean             | sendFriendRequest         | To send friend requests to other users.                                |
| public void                | viewFriendRequests        | To view all friend requests that a user has.                           |
| public boolean             | acceptFriendRequest       | To accept a friend request sent by another user.                       |
| public boolean             | declineFriendRequest      | To decline a friend request sent by another user.                      |
| public boolean             | addFriend                 | To add another user as a friend.                                       |
| public boolean             | removeFriend              | To remove another user from the friends list.                          |
| public boolean             | blockUser                 | To block a user.                                                       |
| public void                | viewProfile               | To view a user’s profile.                                              |
| public boolean             | unblockUser               | To unblock a user.                                                     |


\\More methods to be addes

_Testing_
Client.java is tested by unit tests included in the class TestClient.java.

\\ To be added

---
**Server.java**

_All Implemented Interfaces_
ServerService

_Field Summary_
| Modifier and Type          | Field       | Description                     |
|----------------------------|-------------|---------------------------------|
| public static Database     | database    | Creates a new database.        |

_Method Summary_

| Modifier and Type               | Method Name                        | Description                                                      |
|-------------------------------------|----------------------------------------|------------------------------------------------------------------------|
| public static boolean    | login(String username, String password)| To allow users to login to their CoHabit account on the server side.   |
| public static boolean    | register(User user)                    | To allow users to register or sign up for the app to the server side.  |
| public static boolean    | sendMessage(User sender, User receiver, String message) | To send messages to other users on the server side.   |
| public static String     | loadMessage(User sender, User receiver)| To load chat history on the server side.                               |
| public static boolean    | sendFriendRequest                      | To send friend requests to other users on the server side.             |
| public static ArrayList<User>  | viewFriendRequests(User user)    | To view all friend requests that a user has on the server side.        |
| public static boolean    | acceptFriendRequest                    | To accept a friend request sent by another user on the server side.    |
| public static boolean    | declineFriendRequest                   | To decline a friend request sent by another user on the server side.   |
| public static boolean    | addFriend                              | To add another user as a friend on the server side.                    |
| public static boolean    | removeFriend                           | To remove another user from the friends list on the server side.       |
| public static boolean    | blockUser                              | To block a user on the server side.                                    |
| public static ArrayList<User>  | viewBlockedUsers(User user)      | To view all blocked users on the server side.                          |
| public static boolean          | removeBlockedUser                | To unblock a user on the server side.                                  |
| public static void             | viewProfile                      | To view a user’s profile on the server side.                           |
| public static ArrayList<User>  | viewFriendsList(User user)       | To view the list of friends of a user on the server side.              |

_Testing_
Server.java is tested by unit tests included in the class TestServer.java.

---

### **Exceptions**

_UsernameTakenException.java_

| Constructor                               | Description                                              |
|-------------------------------------------|----------------------------------------------------------|
| UsernameTakenException(String username)   | Constructs a new exception indicating a taken username.  |

_InvalidInputException.java_

Constructor
| Constructor                               | Description                                              |
|-------------------------------------------|----------------------------------------------------------|
| InvalidInputException(String input)       | Constructs a new exception indicating invalid input.    |

---

### **Interfaces**

1. Blockable.java

* Boolean blockUser()
* unblockUser()
* ArrayList<User> getBlockedUsers(); 

2. FriendManageable.java

* public boolean addFriend(User user)
* public boolean removeFriend(User user) 
* public boolean blockUser(User user)
* public boolean unblockUser(User user) 
* public ArrayList<User> getFriendList()
* public void setFriendList(ArrayList<User> friends)

3. Message.java

* public boolean addFriend(User user)
* public boolean removeFriend(User user) 
* public boolean blockUser(User user)
* public boolean unblockUser(User user) 
* public ArrayList<User> getFriendList()
* public void setFriendList(ArrayList<User> friends)

4. Profile.java

* public String getName() 
* public String getDescription()
* public String getUniversity()
* public void setName(String newName) 
* public void setDescription(String newDesc)
* public void setUniversity(String newUni)
* public byte[] getProfilePicture();
* public void setProfilePicture(byte[] pictureData);

5. Searchable.java

* ArrayList<User> searchByParameter(String parameter, String value)    
* ArrayList<User> exactMatch(User user)
* ArrayList<User> partialMatch(User user)

6. Sendable.java

* boolean sendMessage(User sender, User receiver, String message)
* boolean deleteMessage(User sender, User receiver, String message)
* ArrayList<String> getMessages()

---

### **Extra Credit - Images**

* enum MessageType
<br> Defines three types of messages: TEXT, IMAGE, and VIDEO

* Methods added to Database.java
  
| Modifier | Function Name                      | Description                                |
|----------|------------------------------------|--------------------------------------------|
| public   | `saveProfilePicture(User user)`    | Saves user's profile picture to a file.    |
| public   | `loadProfilePicture(User user)`    | Loads profile picture from a file.         |
| public   | `deleteProfilePicture(User user)`  | Deletes user's profile picture file.       |

  
* Methods addes to User.java

| Modifier | Function Name                | Description                                                                                      |
|----------|------------------------------|--------------------------------------------------------------------------------------------------|
| public   | `setDescription(String userDesc)` | Sets the user's description.                                                                |
| public   | `getProfilePicture()`        | Retrieves the user's profile picture data as a byte array.                                       |

---

