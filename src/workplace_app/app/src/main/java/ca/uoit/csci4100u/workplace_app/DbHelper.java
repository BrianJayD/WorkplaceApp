package ca.uoit.csci4100u.workplace_app;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A helper class meant to do database functionality
 */
public class DbHelper {

    protected static final String COMPANIES = "companies";
    protected static final String COMPANY_NAME = "company_name";
    protected static final String CHATS = "chats";
    protected static final String MEMBERS = "members";
    protected static final String USERS = "users";
    protected static final String ADMIN = "admin";
    protected static final String DISPLAY_NAME = "display_name";
    protected static final String MESSAGES = "messages";

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
        database.child(USERS).child(userId).child(DISPLAY_NAME).setValue(displayName);
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
     * A helper function to create a new company in the database
     * @param auth The firebase authentication
     * @param database The firebase database reference
     * @param companyName A string representation of the company name
     */
    public static void createCompanyDbEntry(FirebaseAuth auth, DatabaseReference database, final String companyName) {
        String userId = auth.getCurrentUser().getUid();

        String companyId = database.child(COMPANY_NAME).push().getKey();
        database.child(COMPANIES).child(companyId).child(COMPANY_NAME).setValue(companyName);
        database.child(COMPANIES).child(companyId).child(CHATS).child(companyName).setValue(true);
        database.child(COMPANIES).child(companyId).child(MEMBERS).child(userId).setValue(ADMIN);

        database.child(USERS).child(userId).child(COMPANIES).child(companyId).setValue(true);
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
}
