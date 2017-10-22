package ca.uoit.csci4100u.workplace_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

/**
 * The 'LoginActivity' class which is the first activity that the user sees. This handles the
 * authentication before accessing the main application
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * The onCreate method for the 'LoginActivity' class. This function initializes the activity.
     * @param savedInstanceState The saved instance state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
    }

    /**
     * TODO: Fill this in when code is written
     * @param view The view that has been clicked (the button)
     */
    public void handleLogin(View view) {

    }

    /**
     * Handles the onClick function for the 'Sign Up' button. This function creates a sub-activity
     * 'SignUpActivity' which will allow a user to create an account in the database
     * @param view The view that has been clicked (the button)
     */
    public void handleSignUp(View view) {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }
}
