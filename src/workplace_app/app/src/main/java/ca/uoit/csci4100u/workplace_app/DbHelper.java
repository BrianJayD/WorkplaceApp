package ca.uoit.csci4100u.workplace_app;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

/**
 * A helper class meant to do database functionality
 */
public class DbHelper {

    protected static final String COMPANIES = "companies";
    protected static final String COMPANY_NAME = "company_name";
    protected static final String CHATS = "chats";
    protected static final String MEMBERS = "members";
    protected static final String CHAT_TITLE = "chat_title";
    protected static final String USERS = "users";
    protected static final String ADMIN = "admin";
    protected static final String DISPLAY_NAME = "display_name";

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
        String chatId = database.child(COMPANY_NAME).child(companyId).child(CHATS).push().getKey();
        database.child(COMPANIES).child(companyId).child(COMPANY_NAME).setValue(companyName);
        database.child(COMPANIES).child(companyId).child(CHATS).child(chatId).child(CHAT_TITLE).setValue(companyName);
        database.child(COMPANIES).child(companyId).child(MEMBERS).child(userId).setValue(ADMIN);

        database.child(USERS).child(userId).child(COMPANIES).child(companyId).setValue(true);
    }
}
