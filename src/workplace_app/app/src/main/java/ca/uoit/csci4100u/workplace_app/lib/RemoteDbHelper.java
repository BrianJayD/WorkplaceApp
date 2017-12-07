package ca.uoit.csci4100u.workplace_app.lib;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ca.uoit.csci4100u.workplace_app.inc.Member;
import ca.uoit.csci4100u.workplace_app.inc.Message;
import ca.uoit.csci4100u.workplace_app.inc.Chat;
import ca.uoit.csci4100u.workplace_app.inc.Company;

/**
 * A helper class meant to do database functionality
 */
public class RemoteDbHelper {

    // Nodes in the database
    private static final String COMPANIES = "companies";
    private static final String COMPANY_NAME = "company_name";
    private static final String CHATS = "chats";
    private static final String MEMBERS = "members";
    private static final String USERS = "users";
    private static final String DISPLAY_NAME = "display_name";
    private static final String MESSAGES = "messages";
    private static final String PERMISSIONS = "permissions";
    private static final String EMAIL = "email";
    private static final String CHAT_NAME = "chat_name";
    public static final String ADMIN = "3";
    public static final String MODERATOR = "2";
    public static final String MEMBER = "1";
    public static final String COMPANY_ID = "company_id";
    public static final String CHAT_ID = "chat_id";

    // Constants
    private static final String EMPTY_STRING = "";

    /**
     * Private constructor so this helper class can't be instantiated
     */
    private RemoteDbHelper() {

    }

    public static boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    /**
     * A helper function to create a new user in the database
     * @param auth The firebase authentication
     * @param database The firebase database reference
     * @param displayName A string representation of the user's display name
     */
    public static void createUserDbEntry(FirebaseAuth auth, DatabaseReference database, final String displayName, Context context) {
        if (isNetworkAvailable(context)) {
            String userId = auth.getCurrentUser().getUid();
            String email = auth.getCurrentUser().getEmail();
            database.child(USERS).child(userId).child(DISPLAY_NAME).setValue(displayName);
            database.child(USERS).child(userId).child(EMAIL).setValue(email);
        }
    }

     /**
     * A helper function to update the display name in the database
     * @param auth The firebase authentication
     * @param database The firebase database reference
     * @param displayName A string representation of the user's display name
     */
    public static void updateDbDisplayName(FirebaseAuth auth, DatabaseReference database, final String displayName, LocalDbHelper localDbHelper, Context context) {
        if (isNetworkAvailable(context)) {
            String userId = auth.getCurrentUser().getUid();
            database.child(USERS).child(userId).child(DISPLAY_NAME).setValue(displayName);
            localDbHelper.updateUserDisplayName(userId, displayName);
        }
    }

    /**
     * A helper function to update the email in the database
     * @param auth The firebase authentication
     * @param database The firebase database reference
     * @param email A string representation of the user's email
     */
    public static void updateDbEmail(FirebaseAuth auth, DatabaseReference database, final String email, Context context) {
        if (isNetworkAvailable(context)) {
            String userId = auth.getCurrentUser().getUid();
            database.child(USERS).child(userId).child(EMAIL).setValue(email);
        }
    }

    /**
     * A helper function to create a new company in the database as well as putting an entry for the
     * company id under the user. This will also set the user that created the company as the 'admin'
     * @param auth The firebase authentication
     * @param database The firebase database reference
     * @param companyName A string representation of the company name
     */
    public static boolean createCompanyDbEntry(FirebaseAuth auth, DatabaseReference database, final String companyName, LocalDbHelper localDbHelper, Context context) {
        if (isNetworkAvailable(context)) {
            String userId = auth.getCurrentUser().getUid();

            String companyId = database.child(COMPANY_NAME).push().getKey();
            database.child(COMPANIES).child(companyId).child(COMPANY_NAME).setValue(companyName);

            createChatDbEntry(database, companyId, companyName, context);
            database.child(COMPANIES).child(companyId).child(MEMBERS).child(userId).setValue(ADMIN);

            database.child(USERS).child(userId).child(COMPANIES).child(companyId).setValue(companyName);

            savePermissionsToLocalDatabase(userId, companyId, Integer.parseInt(ADMIN), localDbHelper);
            return true;
        }
        return false;
    }

    public static boolean createChatDbEntry(DatabaseReference database, String companyId, String chatName, Context context) {
        if (isNetworkAvailable(context)) {
            String chatId = database.child(COMPANIES).child(companyId).push().getKey();
            database.child(COMPANIES).child(companyId).child(CHATS).child(chatId).child(PERMISSIONS).setValue(MEMBER);
            database.child(COMPANIES).child(companyId).child(CHATS).child(chatId).child(CHAT_NAME).setValue(chatName);
            return true;
        }
        return false;
    }

    private static void savePermissionsToLocalDatabase(String userId, String companyId, int permissions, LocalDbHelper localDbHelper) {
        if(!localDbHelper.checkPermissionsExists(userId, companyId)) {
            localDbHelper.createPermissions(permissions, userId, companyId);
        }
    }

    public static void deleteMessage(String companyId, String chatId, String messageId, DatabaseReference database, LocalDbHelper localDbHelper, Context context) {
        if (isNetworkAvailable(context)) {
            database.child(COMPANIES).child(companyId).child(CHATS).child(chatId).child(MESSAGES).child(messageId).removeValue();
            deleteMessageFromLocalDatabase(localDbHelper, messageId);
        }
    }

    private static void deleteMessageFromLocalDatabase(LocalDbHelper localDbHelper, String messageId) {
        if (localDbHelper.checkMessageExists(messageId)) {
            localDbHelper.deleteMessage(messageId);
        }
    }

    public static void deleteCompany(DatabaseReference database, DataSnapshot dataSnapshot, String companyId, LocalDbHelper localDbHelper, Context context) {
        if (isNetworkAvailable(context)) {
            Iterable<DataSnapshot> users = dataSnapshot.child(USERS).getChildren();
            for (DataSnapshot user : users) {
                String key = user.getKey();
                database.child(USERS).child(key).child(COMPANIES).child(companyId).removeValue();
            }
            database.child(COMPANIES).child(companyId).removeValue();
            deleteCompanyFromLocalDatabase(localDbHelper, companyId);
        }
    }

    private static void deleteCompanyFromLocalDatabase(LocalDbHelper localDbHelper, String companyId) {
        localDbHelper.deleteCompany(companyId);
    }

    /**
     * A helper function that takes the user id of the user and converts it to the display name by accessing
     * the database
     * @param dataSnapshot A snapshot of the database
     * @param userId The user id associated with the user
     * @return The display name associated with the user
     */
    public static String convertUidToDispName(DataSnapshot dataSnapshot, String userId, Context context) {
        if (isNetworkAvailable(context)) {
            return dataSnapshot.child(USERS).child(userId).child(DISPLAY_NAME).getValue().toString();
        }
        return null;
    }

    /**
     * A helper function that accesses the database and returns all of the messages associated with the
     * company id and the chat id
     * @param dataSnapshot A snapshot of the database
     * @param companyId The company id associated with the messages needing to be accessed
     * @param chatId The chat id associated with the messages needing to be accessed
     * @return A list of Message objects that is associated with combination of the company id and chat id
     */
    public static List<Message> getDbMessages(DataSnapshot dataSnapshot, String companyId, String chatId, LocalDbHelper localDbHelper, Context context) {
        List<Message> messageList = new ArrayList<>();
        if (isNetworkAvailable(context)) {

            Iterable<DataSnapshot> messages = dataSnapshot.child(COMPANIES).child(companyId).child(CHATS).child(chatId).child(MESSAGES).getChildren();
            for (DataSnapshot message : messages) {
                Message dbMessage = message.getValue(Message.class);
                String userId = dbMessage.getUserId();
                String displayName = convertUidToDispName(dataSnapshot, userId, context);
                dbMessage.setUserName(displayName);
                messageList.add(dbMessage);

                saveUserToLocalDatabase(localDbHelper, dbMessage.getUserId(), dbMessage.getUserName());
            }
            saveMessageListToLocalDatabase(localDbHelper, messageList, chatId);
        }
        return messageList;
    }

    private static void saveUserToLocalDatabase(LocalDbHelper localDbHelper, String userId, String userName) {
        if (!localDbHelper.checkUserExists(userId)) {
            localDbHelper.createUser(userId, userName);
        }
    }

    private static void saveMessageListToLocalDatabase(LocalDbHelper localDbHelper, List<Message> messageList, String chatId) {
        for (Message message : messageList) {
            if (!localDbHelper.checkMessageExists(message.getMessageId())) {
                localDbHelper.createMessage(message.getMessageId(), message.getUserId(), message.getTimeStamp(), message.getMessage());
            }

            if (!localDbHelper.checkChatMessageExists(chatId, message.getMessageId())) {
                localDbHelper.createChatMessage(chatId, message.getMessageId());
            }
        }
    }

    /**
     * A helper function to add the message the user has created to the database that is associated with the company
     * id and the chat id
     * @param database The firebase database reference
     * @param auth The firebase authentication
     * @param companyId The company id associated with the message being posted
     * @param chatId The chat id associated with the message being posted
     * @param message The message that contains the information being posted
     */
    public static void postUserMessage(DatabaseReference database, DataSnapshot dataSnapshot, FirebaseAuth auth, String companyId, String chatId, String message, Context context) {
        if (isNetworkAvailable(context)) {
            String userId = auth.getCurrentUser().getUid();
            String userName = convertUidToDispName(dataSnapshot, userId, context);
            String currentTime = DateFormat.getDateTimeInstance().format(new Date());

            String messageId = database.child(COMPANIES).child(companyId).child(CHATS).child(chatId).child(MESSAGES).push().getKey();
            Message newMessage = new Message(messageId, userId, userName, currentTime, message);
            database.child(COMPANIES).child(companyId).child(CHATS).child(chatId).child(MESSAGES).child(messageId).setValue(newMessage);
        }
    }

    /**
     * A helper function to get all of the companies associated with the current user
     * @param auth The firebase authentication
     * @param dataSnapshot A snapshot of the database
     * @return A list of Company objects that the user is a member of
     */
    public static List<Company> getCompanyListForCurrUser(FirebaseAuth auth, DataSnapshot dataSnapshot, LocalDbHelper localDbHelper, Context context) {
        List<Company> companyList = new ArrayList<>();
        if (isNetworkAvailable(context)) {
            String userId = auth.getCurrentUser().getUid();
            Iterable<DataSnapshot> companies = dataSnapshot.child(USERS).child(userId).child(COMPANIES).getChildren();

            for (DataSnapshot company : companies) {
                Company newCompany = new Company(company.getKey().toString(), company.getValue().toString());
                companyList.add(newCompany);
            }
            saveCompanyListToLocalDatabase(localDbHelper, companyList, userId);
        }
        return companyList;
    }

    private static void saveCompanyListToLocalDatabase(LocalDbHelper localDbHelper, List<Company> companyList, String userId) {
        for (Company company : companyList) {
            if (!localDbHelper.checkCompanyExists(company.getCompanyId())) {
                localDbHelper.createCompany(company.getCompanyId(), company.getCompanyName());
            }
            if (!localDbHelper.checkUserCompanyExists(userId, company.getCompanyId())) {
                localDbHelper.createUserCompany(userId, company.getCompanyId());
            }
        }
    }

    /**
     * A helper function which will check to see if the email specified is inside of the database. If
     * it is then return it, otherwise return an empty string
     * @param dataSnapshot A snapshot of the database
     * @param email A string representation of the specified email to be searched for
     * @return The string representation of the user id associated with the email or an empty string
     */
    public static String checkEmailExistsAndGetUid(DataSnapshot dataSnapshot, String email, Context context) {
        if (isNetworkAvailable(context)) {
            Iterable<DataSnapshot> users = dataSnapshot.child(USERS).getChildren();
            for (DataSnapshot user : users) {
                String userId = user.getKey();
                String dbEmail = dataSnapshot.child(USERS).child(userId).child(EMAIL).getValue().toString();
                if (email.compareTo(dbEmail) == 0) {
                    return userId;
                }
            }
        }
        return EMPTY_STRING;
    }

    /**
     * A helper function to add the specified user to the specified company in the database as well
     * as putting an entry for the company id under the user
     * @param database The firebase database reference
     * @param dataSnapshot A snapshot of the database
     * @param companyId A string representation of the specified company id
     * @param userId A string representation of the specified user id
     */
    public static void addMemberToCompanyDb(DatabaseReference database, DataSnapshot dataSnapshot, String companyId, String userId, LocalDbHelper localDbHelper, Context context) {
        if (isNetworkAvailable(context)) {
            database.child(COMPANIES).child(companyId).child(MEMBERS).child(userId).setValue(MEMBER);
            database.child(USERS).child(userId).child(COMPANIES).child(companyId).setValue(convertCompanyIdToCompanyName(dataSnapshot, companyId));

            savePermissionsToLocalDatabase(userId, companyId, Integer.parseInt(MEMBER), localDbHelper);
        }
    }

    /**
     * A helper function to get the list of chats for the specified company
     * @param dataSnapshot A snapshot of the database
     * @param companyId A string representation of the specified company id
     * @return A list of Chat objects that the company has
     */
    public static List<Chat> getChatListForSpecifiedCompany(DataSnapshot dataSnapshot, String companyId, LocalDbHelper localDbHelper, Context context) {
        List<Chat> chatList = new ArrayList<>();
        if (isNetworkAvailable(context)) {
            Iterable<DataSnapshot> chats = dataSnapshot.child(COMPANIES).child(companyId).child(CHATS).getChildren();

            for (DataSnapshot chat : chats) {
                String chatId = chat.getKey().toString();
                String chatName = (String) dataSnapshot.child(COMPANIES).child(companyId).child(CHATS).child(chat.getKey().toString()).child(CHAT_NAME).getValue();
                int permission = Integer.parseInt(dataSnapshot.child(COMPANIES).child(companyId).child(CHATS).child(chat.getKey().toString()).child(PERMISSIONS).getValue().toString());

                Chat newChat = new Chat(chatId, chatName, permission);
                chatList.add(newChat);
            }
            saveChatListToLocalDatabase(localDbHelper, chatList, companyId);
        }
        return chatList;
    }

    private static void saveChatListToLocalDatabase(LocalDbHelper localDbHelper, List<Chat> chatList, String companyId) {
        for (Chat chat : chatList) {
            if (!localDbHelper.checkChatExists(chat.getChatId())) {
                localDbHelper.createChat(chat.getChatId(), chat.getChatName(), chat.getChatPermission());
            }

            if (!localDbHelper.checkCompanyChatExists(companyId, chat.getChatId())) {
                localDbHelper.createCompanyChat(companyId, chat.getChatId());
            }
        }
    }

    /**
     * A helper function to convert the company id to the company name
     * @param dataSnapshot A snapshot of the database
     * @param companyId A string representation of the specified company id
     * @return A string representation of the company name
     */
    private static String convertCompanyIdToCompanyName(DataSnapshot dataSnapshot, String companyId) {
        return dataSnapshot.child(COMPANIES).child(companyId).child(COMPANY_NAME).getValue().toString();
    }

    public static List<Member> getCompanyMembers(DataSnapshot dataSnapshot, String companyId, LocalDbHelper localDbHelper, Context context) {
        List<Member> memberList = new ArrayList<>();
        if (isNetworkAvailable(context)) {
            Iterable<DataSnapshot> members = dataSnapshot.child(COMPANIES).child(companyId).child(MEMBERS).getChildren();

            for (DataSnapshot member : members) {

                String memberId = member.getKey().toString();
                String memberName = (String) dataSnapshot.child(USERS).child(member.getKey()).child(DISPLAY_NAME).getValue();
                String email = (String) dataSnapshot.child(USERS).child(member.getKey()).child(EMAIL).getValue();
                Member newMember = new Member(memberId, memberName, email);


                memberList.add(newMember);
            }

        }

        return memberList;
    }

}
