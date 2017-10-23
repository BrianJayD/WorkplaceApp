package ca.uoit.csci4100u.workplace_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

/**
 * The 'LandingPageActivity' class which is the main landing page that the user will see after
 * logging in. It will allow the user to access a number of application features specific to the
 * companies that the user is a part of.
 */
public class LandingPageActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    /**
     * The onCreate method for the 'LandingPageActivity' class. This function initializes the activity
     * and sets the member variables.
     * @param savedInstanceState The saved instance state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_page);

        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Handles the onClick function for the 'Company Sign Up' button. This function creates a will
     * start the sub-activity 'CompanySignUpActivity'
     * @param view The view that has been clicked (the button)
     */
    public void handleCompanySignUp(View view) {
        Intent intent = new Intent(LandingPageActivity.this, CompanySignUpActivity.class);
        startActivity(intent);
    }

    /**
     * TODO: Fill this in after the code is written
     * @param view The view that has been clicked (the button)
     */
    public void handleChat(View view) {

    }

    /**
     * Handles the onClick function for the 'Sign Out' button. This function will start the
     * sub-activity 'LoginActivity' and then close the current activity after signing the user out
     * @param view The view that has been clicked (the button)
     */
    public void handleSignOut(View view) {
        mAuth.signOut();
        Intent intent = new Intent(LandingPageActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Handles the onClick function for the 'Settings' button. This function will start the
     * sub-activity 'SettingsActivity'.
     * @param view The view that has been clicked (the button)
     */
    public void handleSettings(View view) {
        Intent intent = new Intent(LandingPageActivity.this, SettingsActivity.class);
        startActivity(intent);
    }
}
