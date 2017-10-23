package ca.uoit.csci4100u.workplace_app;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * The 'LoginActivity' class which is the first activity that the user sees. This handles the
 * authentication before accessing the main application
 */
public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String TAG = "LoginActivity";

    /**
     * The onCreate method for the 'LoginActivity' class. This function initializes the activity
     * and sets the member variables. If the user is already set then move to the sub-activity
     * 'LandingPageActivity' immediately
     * @param savedInstanceState The saved instance state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            // TODO: Check if debug statements need to be added into a resource file
            Log.d(TAG, "currentUserIsSet");
            Intent intent = new Intent(LoginActivity.this, LandingPageActivity.class);
            startActivity(intent);
        } else {
            Log.d(TAG, "currentUserIsNotSet");
        }
    }

    /**
     * Handles the onClick function for the 'Login' button. This function creates a sub-activity
     * 'LandingPageActivity' if the authentication for the login is successful. Otherwise, it
     * will create a toast with an error message.
     * @param view The view that has been clicked (the button)
     */
    public void handleLogin(View view) {
        String email = ((EditText) findViewById(R.id.loginEmail)).getText().toString();
        String password = ((EditText) findViewById(R.id.loginPass)).getText().toString();

        if (!email.isEmpty() && !password.isEmpty()) {
            signIn(email, password);
        }
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

    /**
     * A helper method used to log into a new Firebase account. This function will create a toast
     * if the log in authentication was not successful. Otherwise, it will start the sub-activity
     * 'LandingPageActivity'
     * @param email A string representation of the user-specified email address
     * @param password A string representation of the user-specified password
     */
    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signIn:success");
                            Intent intent = new Intent(LoginActivity.this, LandingPageActivity.class);
                            startActivity(intent);
                        } else {
                            Log.d(TAG, "signIn:failure", task.getException());
                            Toast.makeText(LoginActivity.this, R.string.authentication_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
