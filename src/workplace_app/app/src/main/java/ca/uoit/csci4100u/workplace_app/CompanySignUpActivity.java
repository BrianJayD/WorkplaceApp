package ca.uoit.csci4100u.workplace_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * The 'CompanySignUpActivity' class which is the activity the user sees when creating a new company
 */
public class CompanySignUpActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private static final String COMPANIES = "companies";
    private static final String COMPANY_NAME = "company_name";
    private static final String CHATS = "chats";
    private static final String MEMBERS = "members";
    private static final String CHAT_TITLE = "chat_title";
    private static final String USERS = "users";
    private static final String ADMIN = "admin";

    /**
     * The onCreate method for the 'CompanySignUpActivity' class. This function initializes the activity
     * and sets the member variables.
     * @param savedInstanceState The saved instance state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.company_sign_up);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Handles the onClick function for the 'Sign Up' button. This function creates a database
     * entry for the company as well as the user that created the company
     * @param view The view that has been clicked (the button)
     */
    public void handleCompanySignUp(View view) {
        String companyName = ((EditText) findViewById(R.id.companyName)).getText().toString();
        if (!companyName.isEmpty()) {
            String userId = mAuth.getCurrentUser().getUid();

            String companyId = mDatabase.child(COMPANY_NAME).push().getKey();
            String chatId = mDatabase.child(COMPANY_NAME).child(companyId).child(CHATS).push().getKey();
            mDatabase.child(COMPANIES).child(companyId).child(COMPANY_NAME).setValue(companyName);
            mDatabase.child(COMPANIES).child(companyId).child(CHATS).child(chatId).child(CHAT_TITLE).setValue(companyName);
            mDatabase.child(COMPANIES).child(companyId).child(MEMBERS).child(userId).setValue(ADMIN);

            mDatabase.child(USERS).child(userId).child(companyId).setValue(true);

            Toast.makeText(CompanySignUpActivity.this, R.string.company_created_successfully,
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handles the onClick function for the 'Back' button. This will close the current sub-activity
     * 'CompanySignUpActivity'
     * @param view The view that has been clicked (the button)
     */
    public void handleBack(View view) {
        finish();
    }
}
