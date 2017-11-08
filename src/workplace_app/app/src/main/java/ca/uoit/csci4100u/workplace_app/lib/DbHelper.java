package ca.uoit.csci4100u.workplace_app.lib;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ca.uoit.csci4100u.workplace_app.inc.Message;
import ca.uoit.csci4100u.workplace_app.inc.Chat;
import ca.uoit.csci4100u.workplace_app.inc.Company;

/**
 * A helper class meant to do database functionality
 */
public class DbHelper {

    // Nodes in the database
    private static final String COMPANIES = "companies";
    private static final String COMPANY_NAME = "company_name";
    private static final String CHATS = "chats";
    private static final String MEMBERS = "members";
    private static final String USERS = "users";
    private static final String ADMIN = "admin";
    private static final String MEMBER = "member";
    private static final String DISPLAY_NAME = "display_name";
    private static final String MESSAGES = "messages";
    private static final String PERMISSIONS = "permissions";
    private static final String EMAIL = "email";
    public static final String COMPANY_ID = "company_id";
    public static final String CHAT_ID = "chat_id";

    // Constants
    private static final String EMPTY_STRING = "";

    /**
     * Private constructor so this helper class can't be instantiated
     */
    private DbHelper() {

    }

    /**
     * A helper function to create a new user in the database
     * @param auth The firebase authentication
     * @param database The firebase database reference
     * @param displayName A string representation of the user's display name
     */
    public static void createUserDbEntry(FirebaseAuth auth, DatabaseReference database, final String displayName) {
        String userId = auth.getCurrentUser().getUid();
        String email = auth.getCurrentUser().getEmail();
        database.child(USERS).child(userId).child(DISPLAY_NAME).setValue(displayName);
        database.child(USERS).child(userId).child(EMAIL).setValue(email);
    }

    /**
     * A helper function to update the display name in the database
     * @param auth The firebase authentication
     * @param database The firebase database reference
     * @param displayName A string representation of the user's display name
     */
    public static void updateDbDisplayName(FirebaseAuth auth, DatabaseReference database, final String displayName) {
        String userId = auth.getCurrentUser().getUid();
        database.child(USERS).child(userId).child(DISPLAY_NAME).setValue(displayName);
    }

    /**
     * A helper function to update the email in the database
     * @param auth The firebase authentication
     * @param database The firebase database reference
     * @param email A string representation of the user's email
     */
    public static void updateDbEmail(FirebaseAuth auth, DatabaseReference database, final String email) {
        String userId = auth.getCurrentUser().getUid();
        database.child(USERS).child(userId).child(EMAIL).setValue(email);
    }

    /**
     * A helper function to create a new company in the database as well as putting an entry for the
     * company id under the user. This will also set the user that created the company as the 'admin'
     * @param auth The firebase authentication
     * @param database The firebase database reference
     * @param companyName A string representation of the company name
     */
    public static void createCompanyDbEntry(FirebaseAuth auth, DatabaseReference database, final String companyName) {
        String userId = auth.getCurrentUser().getUid();

        String companyId = database.child(COMPANY_NAME).push().getKey();
        database.child(COMPANIES).child(companyId).child(COMPANY_NAME).setValue(companyName);
        database.child(COMPANIES).child(companyId).child(CHATS).child(companyName).child(PERMISSIONS).setValue(false);
        database.child(COMPANIES).child(companyId).child(MEMBERS).child(userId).setValue(ADMIN);

        database.child(USERS).child(userId).child(COMPANIES).child(companyId).setValue(companyName);
    }

    /**
     * A helper function that takes the user id of the user and converts it to the display name by accessing
     * the database
     * @param dataSnapshot A snapshot of the database
     * @param userId The user id associated with the user
     * @return The display name associated with the user
     */
    public static String convertUidToDispName(DataSnapshot dataSnapshot, String userId) {
        return dataSnapshot.child(USERS).child(userId).child(DISPLAY_NAME).getValue().toString();
    }

    /**
     * A helper function that accesses the database and returns all of the messages associated with the
     * company id and the chat id
     * @param dataSnapshot A snapshot of the database
     * @param companyId The company id associated with the messages needing to be accessed
     * @param chatId The chat id associated with the messages needing to be accessed
     * @return A list of Message objects that is associated with combination of the company id and chat id
     */
    public static List<Message> getDbMessages(DataSnapshot dataSnapshot, String companyId, String chatId) {
        List<Message> messageList = new ArrayList<>();

        Iterable<DataSnapshot> messages = dataSnapshot.child(COMPANIES).child(companyId).child(CHATS).child(chatId).child(MESSAGES).getChildren();
        for (DataSnapshot message : messages) {
            Message dbMessage = message.getValue(Message.class);
            messageList.add(dbMessage);
        }
        return messageList;
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
    public static void postUserMessage(DatabaseReference database, FirebaseAuth auth, String companyId, String chatId, String message) {
        String userId = auth.getCurrentUser().getUid();
        String currentTime = DateFormat.getDateTimeInstance().format(new Date());
        Message newMessage = new Message(userId, currentTime, message);

        database.child(COMPANIES).child(companyId).child(CHATS).child(chatId).child(MESSAGES).push().setValue(newMessage);
    }

    /**
     * A helper function to get all of the companies associated with the current user
     * @param auth The firebase authentication
     * @param dataSnapshot A snapshot of the database
     * @return A list of Company objects that the user is a member of
     */
    public static List<Company> getCompanyListForCurrUser(FirebaseAuth auth, DataSnapshot dataSnapshot) {
        String userId = auth.getCurrentUser().getUid();
        Iterable<DataSnapshot> companies = dataSnapshot.child(USERS).child(userId).child(COMPANIES).getChildren();

        List<Company> companyList = new ArrayList<>();
        for (DataSnapshot company : companies) {
            Company newCompany = new Company(company.getKey().toString(), company.getValue().toString());
            companyList.add(newCompany);
        }
        return companyList;
    }

    /**
     * A helper function which will check to see if the email specified is inside of the database. If
     * it is then return it, otherwise return an empty string
     * TODO: Potentially move to async task since this operation could be quite slow
     * @param dataSnapshot A snapshot of the database
     * @param email A string representation of the specified email to be searched for
     * @return The string representation of the user id associated with the email or an empty string
     */
    public static String checkEmailExistsAndGetUid(DataSnapshot dataSnapshot, String email) {
        Iterable<DataSnapshot> users = dataSnapshot.child(USERS).getChildren();
        for (DataSnapshot user : users) {
            String userId = user.getKey();
            String dbEmail = dataSnapshot.child(USERS).child(userId).child(EMAIL).getValue().toString();
            if (email.compareTo(dbEmail) == 0) {
                return userId;
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
    public static void addMemberToCompanyDb(DatabaseReference database, DataSnapshot dataSnapshot, String companyId, String userId) {
        database.child(COMPANIES).child(companyId).child(MEMBERS).child(userId).setValue(MEMBER);
        database.child(USERS).child(userId).child(COMPANIES).child(companyId).setValue(convertCompanyIdToCompanyName(dataSnapshot, companyId));
    }

    /**
     * A helper function to get the list of chats for the specified company
     * @param dataSnapshot A snapshot of the database
     * @param companyId A string representation of the specified company id
     * @return A list of Chat objects that the company has
     */
    public static List<Chat> getChatListForSpecifiedCompany(DataSnapshot dataSnapshot, String companyId) {
        Iterable<DataSnapshot> chats = dataSnapshot.child(COMPANIES).child(companyId).child(CHATS).getChildren();

        List<Chat> chatList = new ArrayList<>();
        for (DataSnapshot chat : chats) {
            boolean permission = (Boolean) dataSnapshot.child(COMPANIES).child(companyId).child(CHATS).child(chat.getKey().toString()).child(PERMISSIONS).getValue();
            Chat newChat = new Chat(chat.getKey().toString(), permission);
            chatList.add(newChat);
        }
        return chatList;
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
}
