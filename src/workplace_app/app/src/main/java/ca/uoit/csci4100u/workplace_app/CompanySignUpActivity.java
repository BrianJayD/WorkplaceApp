package ca.uoit.csci4100u.workplace_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ca.uoit.csci4100u.workplace_app.lib.DbHelper;

/**
 * The 'CompanySignUpActivity' class which is the activity the user sees when creating a new company
 */
public class CompanySignUpActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

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
     * entry for the company as well as a company entry for the user
     * @param view The view that has been clicked (the button)
     */
    public void handleCompanySignUp(View view) {
        final String companyName = ((EditText) findViewById(R.id.companyName)).getText().toString();
        if (!companyName.isEmpty()) {
            DbHelper.createCompanyDbEntry(mAuth, mDatabase, companyName);

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
