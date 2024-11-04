# Phase 1 - L11, Team 02 - CoHabit

Submitted On Vocareum By
Rui Meng

Introduction

For this project, we have created several classes for the first phase of the creation of our very own social media app - CoHabit. CoHabit is an app that eases the roommate search issue through matching various preferences of users. We have also implemented a direct messaging service for communication amongst app users.

For the implementation of the database side of the app, we have created the necessary classes and interfaces to include all the information about the user information, preferences, and so on as listed below:

Chat.java
FriendList.java
User.java
UserDatabase.java
UserSearch.java
UsernameTakenException.java
InvalidInputException.java
Blockable.java
FriendManageable.java
Message.java
Profile.java
Searchable.java
Sendable.java
TestChat.java
TestUser.java
TestUserSearch.java
TestDatabase.java
TestFriendList.java

Description

Classes

Summary

Class
Class Description
Chat.java
Enables users to send, receive, and delete direct messages. 
FriendList.java
Enables users to add, remove, and block users into their “Friends” list.
User.java
Enables users to create a profile containing personal details such as name and contact. 
Runs a compatibility check to see which users match well.
Database.java
Reads and writes data from .txt files. Provides a framework for storing user data, message data, and friend list data. 
UserSearch.java
Allows a user to search for other users stored in the database and apply filters based on preferences.



Chat.java

All Implemented Interfaces:
Sendable

Field Summary

Modifier and Type
Field
Description
private User
sender
Indicates which user is sending a direct message
private User
receiver
Indicates which user is receiving a direct message
Private ArrayList<String>
messages
Stores all the messages present in the direct message chain between the two users.


Constructor Summary

Constructor
Description
Chat(User sender, User receiver, ArrayList<String> messages)
Constructs a newly allocated Chat object with the field values specified by input parameters.


Method Summary

Modifier and Type
Method
Description
boolean
sendMessage(User sender, User receiver, String message)
This method adds a message to the messages list. It returns true if the message was successfully added to the list.
ArrayList<String>
getMessages()
This method returns an ArrayList containing all messages, allowing other parts of the program to retrieve all messages in the chat.
boolean
deleteMessage(User sender, User receiver, String message)
This method helps a user delete a message by removing it from the ArrayList. It returns true if the message was successfully removed.


Inner Classes

SendMessageTask
This inner class handles sending messages in a multi-threaded environment by implementing Runnable.

Modifier and Type
Method
Description


SendMessageTask(User sender, User receiver, String message)
Stores parameters as instance variables, identifying the sender, receiver and the text to be sent.
void
run()
The run() method adds a message to the messages list within a synchronized block to ensure thread safety, preventing concurrent modifications. 


DeleteMessageTask
This inner class handles deleting messages in a multi-threaded environment by implementing Runnable.

Modifier and Type
Method
Description


DeleteMessageTask(User sender, User receiver, String message)
Stores parameters as instance variables, identifying the user deleting, receiver and the text to be deleted.
void
run()
The run() method deletes a message from the messages list within a synchronized block to ensure thread safety, preventing concurrent modifications. 


Testing

Chat.java is tested by unit tests included in the class TestChat.java.

Constructor Tests:
Initialization with Messages: Verifies that the constructor correctly initializes the chat with a provided list of messages, checking both the count and content.
Empty List Handling: Ensures that an empty list initializes the chat with no messages.
Null Handling: Confirms that passing null to the constructor results in an empty message list.

Message Sending Tests:
Single Message: Tests sending a single message, verifying it is added to the chat.
Multiple Messages: Confirms that multiple messages can be sent and stored in the correct order.
Null Message Handling: Checks that attempting to send a null message does not add anything to the chat.

Message Deletion Tests:
Successful Deletion: Validates that a specific message can be deleted and that the remaining messages are as expected.
No Match for Deletion: Tests the behavior when trying to delete a non-existent message, ensuring the message list remains unchanged.
Empty Chat Deletion: Verifies that attempting to delete from an empty chat returns false and keeps the chat empty.




FriendList.java

All Implemented Interfaces:
FriendManageable, Blockable

Field Summary

Modifier and Type
Field
Description
private ArrayList<User>
friends
Stores a list of the users who are friends of the current user.
private ArrayList<User> blocked
blocked
Stores a list of the users who are blocked by the current user.
private User
user 
Holds a reference to the user for whom the friends list and blocked list is being maintained, i.e. the current user.
private Database 
database
Database object used to load and save the friends and blocked users.


Constructor Summary

Constructor
Description
FriendList(User user, Database database)
Constructs a newly allocated FriendList object with the field values specified by input parameters.


Method Summary

Modifier and Type
Method
Description
boolean
addFriend(User user)
This method adds a user to the friends list if they are not already a friend. If the user is blocked, they are unblocked as part of this operation.
boolean
removeFriend(User user)
This method removes a user from the friends list. It returns true if successful.
boolean
blockUser(User user)
This method blocks a user. If the user is currently a friend, they are removed from the friends list.
boolean 
unblockUser(User user)
This method unblocks a user. It returns true if successful.
ArrayList<User>
getFriends()
This method returns a list of users who are currently friends.
ArrayList<User>
getBlocked()
This method returns a list of users who are currently blocked. 


Inner Classes

UserTask 
This inner class handles tasks related to friends in a multi-threaded environment by implementing Runnable.

Modifier and Type
Method
Description


UserTask(User user, String action)
Stores parameters as instance variables, identifying the current user and the action to be performed.
void
run()
Based on the action to be performed, the run() method executes the corresponding method in a multi-threaded scope.


Testing

FriendList.java is tested by unit tests included in the class TestFriendList.java.

Friend Addition Tests
testAddFriendSuccessfully: Verifies successful addition of a user to the friend list.
testAddFriendAlreadyInList: Confirms that adding an already-friended user fails.
testAddFriendNullUser: Checks that adding a null user returns false.
Friend Removal Tests
testRemoveFriendSuccessfully: Validates the successful removal of a user from the friend list.
testRemoveFriendNotInList: Ensures that attempting to remove a non-friend returns false.
testRemoveFriendNullUser: Confirms that removing a null user returns false.
User Blocking Tests
testBlockUserSuccessfully: Tests successful blocking of a user and removal from the friend list.
testBlockUserAlreadyBlocked: Checks that blocking an already blocked user fails.
testBlockUserNullUser: Ensures that blocking a null user returns false.
User Unblocking Tests
testUnblockUserSuccessfully: Validates successful unblocking of a user.
testUnblockUserNotBlocked: Confirms that unblocking a non-blocked user returns false.
testUnblockUserNullUser: Checks that unblocking a null user returns false.
Retrieve Friends Tests
testGetFriendsAfterAdding: Verifies retrieval of friends after adding users.
testGetFriendsEmptyList: Confirms that the list is empty initially.
testGetFriendsAfterRemoval: Checks that a removed user is no longer in the friends list.
Retrieve Blocked Users Tests
testGetBlockedAfterBlocking: Validates retrieval of blocked users after blocking.
testGetBlockedEmptyList: Ensures the blocked list is empty initially.
testGetBlockedAfterUnblocking: Confirms that an unblocked user is removed from the blocked list.

User.java

All Implemented Interfaces:
Profile, FriendManageable, Blockable

Field Summary

Modifier and Type
Field
Description
private String
name
Stores username.
private String
password
Stores user’s password.
private String
email 
Stores user’s email.
private String 
phoneNumber
Stores user’s phone number.
private String
description
Stores user’s description.
private String
university
Stores user’s university.
private String
bedTime
Stores user’s bedtime preferences.
private boolean
alcohol
Stores user’s alcohol preferences.
private boolean
smoke
Stores user’s smoking preferences.
private boolean
guests
Stores user’s guest frequency preferences.
private int
tidy
Stores user’s tidiness level on a scale.
private int
roomHours
Stores number of hours the user stays in their room.
private FriendList
friendUsers
Stores a list of friends associated with the user.
private FriendList
blockedUsers
Stores the blocked list of the user.


Constructor Summary

Constructor
Description
User(String name, String password, String email, String phoneNumber, String userDescription, String university)
Constructs a newly allocated User object with the field values specified by input parameters.


Method Summary
           
Modifier and Type
Field
Description
boolean
addFriend(User user)
This method adds a user to the friends list if they are not already a friend. If the user is blocked, they are unblocked as part of this operation.
boolean
removeFriend(User user)
This method removes a user from the friends list. It returns true if successful.
boolean
blockUser(User user)
This method blocks a user. If the user is currently a friend, they are removed from the friends list.
boolean 
unblockUser(User user)
This method unblocks a user. It returns true if successful.
ArrayList<User>
getFriendList(User user)
This method retrieves the list of friends from the friendUsers list.
ArrayList<User>
getBlockedUsers()
This method retrieves the list of blocked users from the friendUsers list.
void 
setFriendList(ArrayList<User> friends)
This method accepts a list of users to set as friends for updates or initialization.
void
setBlockedUsers(ArrayList<User> blocked)
This method accepts a list of blocked users for updates or initialization.
private String
getName()
Retrieves the user's name.
private String
getPassword()
Retrieves the user’s password.
private String
getEmail()
Retrieves the user’s email address.
private String
getPhoneNumber()
Retrieves the user’s phone number.
private String
getUniversity()
Retrieves the user’s university.
private String
getDescription()
Retrieves the user’s description.
private String
getPreferences()
Retrieves the user’s preferences.
void
setName(String name)
Updates the user's name to a new value.
void
setPassword(String pwd)
Updates the user's password. 
void
setEmail(String email)
Updates the user's email address. 
void
setPhoneNumber(String phoneNum)
Updates the user's phone number.
void
setUniversity(String university)
Updates the user's university information.
void 
setDescription(String userDesc)
Updates the user's bio or personal description.
void 
setPreferences(String bedTime, boolean alcohol, boolean smoke, boolean guests, int tidy, int roomHours)
Sets multiple lifestyle preferences for the user.
private boolean
perfectMatch(User user)
Compares this user’s preferences with another user’s preferences and returns true if they match perfectly.
private int
partialMatch(User user)
Compares this user’s preferences with another user’s preferences and returns the count of matching preferences.
private String
toString()
Returns a string representation of the user object.


Testing
User.java is tested by unit tests included in the class TestUser.java.

The TestUser class comprehensively validates the User class's functionality, covering:
Constructor behavior with various input scenarios.
Setters for each attribute, including handling of null values.
The setting and retrieval of user preferences.
The correctness of the String representation of user objects.

Database.java

All Implemented Interfaces:
None.

Field Summary

Modifier and Type
Field
Description
private static ArrayList<User>
allUsers
Stores a list of all the user objects.
private static final String 
USERS_FILE
Stores filename for storing user data.
private static final String 
FRIENDS_FILE 
Stores filename for storing friends data.
private static final String
MESSAGES_FILE
Stores filename for storing messages.
private static final String
BLOCKED_FILE
Stores filename for storing blocked users data.


Constructor Summary

Constructor
Description
Database() 
This constructor initializes the Database class, creates a new ArrayList for all users, and loads the users from the USERS_FILE.


Method Summary

Modifier and Type
Method
Description
boolean
addUser(User user)
Adds a user to the allUsers list if they are not null and their username does not already exist. Saves the updated user list to a file.
boolean
deleteUser(User user)
Removes a user from the allUsers list and saves the updated list to a file. Returns true if the user was successfully removed.
boolean
addFriend(User user1, User user2)
Adds one user to another user's friend list (bi-directionally) and saves the friends data to a file. Returns true if both users were successfully added.
boolean 
usernameExists(String username)
Checks if a username already exists in the allUsers list and returns true if it does.
User
findUserByName(String name)
Searches for a user by name in the allUsers list and returns the user if found, or null if not.
ArrayList<User>
getAllUsers()
Returns a copy of the allUsers list.
void 
loadUsersFromFile()
Loads users from the USERS_FILE and populates the allUsers list.
void
saveUsersToFile()
Saves all users in the allUsers list to the USERS_FILE.
ArrayList<User>
loadFriendsFromFile()
Loads friends for each user from the FRIENDS_FILE and returns a list of friends.
void
saveFriendsToFile() 
Saves the friends list for each user to the FRIENDS_FILE.
ArrayList<User>
loadBlockedFromFile()
Loads blocked users for each user from the BLOCKED_FILE and returns a list of blocked users.
void
saveBlockedToFile()
Saves the blocked users for each user to the BLOCKED_FILE.
void
recordMessages(String sender, String receiver, String message, String timestamp)
Records a message sent from one user to another in the MESSAGES_FILE.
ArrayList<User>
loadConversation(String user1, String user2)
Loads and returns the conversation between two users from the MESSAGES_FILE.


Testing
Database.java is tested by unit tests included in the class TestDatabase.java.
testAddUser: Verifies that user1 can be added successfully and checks if the name matches.
testDeleteUser: Confirms that user1 can be deleted and is no longer in the user list.
testAddFriend: Checks if friendship can be established between user1 and user2.
testUsernameExists: Validates that the username "Bob" exists in the database.
testFindUserByName: Ensures that retrieving user1 by name returns the correct user object.

UserSearch.java

All Implemented Interfaces
Searchable

Field Summary

Modifier and Type
Field
Description
private Database
db
An instance of the Database class that is used to access the collection of users for searching purposes.


Constructor Summary

Constructor
Description
public UserSearch()
An instance of the Database class that is used to access the collection of users for searching purposes.


Method Summary

Modifier and Type
Method
Description
public ArrayList<User>
searchByParameter(String parameter, String value)
Searches for users in the database based on a specified parameter (such as name, email, phone number, or university). Returns an ArrayList of users that match the specified criteria.
public ArrayList<User>
exactMatch(User mainUser)
Finds and returns a list of users whose preferences match exactly with the preferences of a current user. 
public ArrayList<User>
partialMatch(User mainUser)
Finds users that have a certain number of matching preferences with the current user. The method iterates through possible matching counts (from 5 down to 1) and adds users that match the specified count of preferences.


Testing
UserSearch.java is tested by unit tests included in the class TestUserSearch.java.

testSearchParameter:
Validates searching users by various parameters (name, password, email, phone, description, university) and checks that the correct user details are returned for each search.

testExactMatch:
Confirms that the exactMatch method retrieves user1 correctly, verifying that the match is based on the entire user object.

testPartialMatch:
Checks the partialMatch method to ensure it can identify user2 as a partial match for user1, indicating that user matching is functioning as expected.

Exceptions

UsernameTakenException.java

Constructor
Description
public UsernameTakenException(String message)
Calls the constructor of the exception superclass with the message passed in as the parameter.


InvalidInputException.java

Constructor
Description
public InvalidInput(String message)
Calls the constructor of the exception superclass with the message passed in as the parameter.



Interfaces 

 Blockable.java

1.1 Boolean blockUser()
1.2 unblockUser()
1.3 ArrayList<User> getBlockedUsers(); 

FriendManageable.java

2.1 public boolean addFriend(User user)
2.2 public boolean removeFriend(User user) 
2.3 public boolean blockUser(User user)
2.4 public boolean unblockUser(User user) 
2.5 public ArrayList<User> getFriendList()
2.6 public void setFriendList(ArrayList<User> friends)

Message.java

3.1 public boolean addFriend(User user)
3.2 public boolean removeFriend(User user) 
3.3 public boolean blockUser(User user)
3.4 public boolean unblockUser(User user) 
3.5 public ArrayList<User> getFriendList()
3.6 public void setFriendList(ArrayList<User> friends)

Profile.java

4.1 public String getName() 
4.2 public String getDescription()
4.3 public String getUniversity()
4.4 public void setName(String newName) 
4.5 public void setDescription(String newDesc)
4.6 public void setUniversity(String newUni)

Searchable.java

5.1 ArrayList<User> searchByParameter(String parameter, String value)    
5.2 ArrayList<User> exactMatch(User user)
5.3 ArrayList<User> partialMatch(User user)

Sendable.java

6.1 boolean sendMessage(User sender, User receiver, String message)
6.2 boolean deleteMessage(User sender, User receiver, String message)
6.3 ArrayList<String> getMessages()



*****

