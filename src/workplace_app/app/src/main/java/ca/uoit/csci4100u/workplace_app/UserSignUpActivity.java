package ca.uoit.csci4100u.workplace_app;

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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ca.uoit.csci4100u.workplace_app.lib.RemoteDbHelper;

/**
 * The 'UserSignUpActivity' class which is the activity the user sees when needing to create a new
 * account. This handles creating a new account and moving back to the 'LoginActivity'
 */
public class UserSignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private static final String TAG = "UserSignUpActivity:d";

    /**
     * The onCreate method for the 'UserSignUpActivity' class. This function initializes the activity
     * and sets the member variables.
     * @param savedInstanceState The saved instance state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_sign_up);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    /**
     * Handles the onClick function for the 'Sign Up' button. This will take the values in the
     * EditText fields and create an account for the new user
     * @param view The view that has been clicked (the button)
     */
    public void handleSignUp(View view) {
        final String displayName = ((EditText) findViewById(R.id.signUpName)).getText().toString();
        final String email = ((EditText) findViewById(R.id.signUpEmail)).getText().toString();
        final String password = ((EditText) findViewById(R.id.signUpPass)).getText().toString();

        if (!displayName.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
            createAccount(displayName, email, password);
        }
    }

    /**
     * A helper function used to create a new Firebase account. This function will create a toast
     * if the account was created successfully or not
     * @param displayName A string representation of the user-specified display name
     * @param email A string representation of the user-specified email address
     * @param password A string representation of the user-specified password
     */
    private void createAccount(final String displayName, final String email, final String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            setDisplayName(displayName);
                            sendEmailVerification();
                            RemoteDbHelper.createUserDbEntry(mAuth, mDatabase, displayName, UserSignUpActivity.this);
                        } else {
                            // TODO: Check if we need to do our own error messages since it's not in a resource file
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(UserSignUpActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * A helper function to set the display name of the user
     * @param displayName A string representation of the user-specified display name
     */
    private void setDisplayName(String displayName) {
        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build();
        mAuth.getCurrentUser().updateProfile(userProfileChangeRequest)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "setDisplayName:success");
                        } else {
                            Log.d(TAG, "setDisplayName:failed");
                        }
                    }
                });
    }

    /**
     * A helper function to send an email verification to the email given in the sign up information
     */
    private void sendEmailVerification() {
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(UserSignUpActivity.this,
                                    getString(R.string.success_verification) + " " +  user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UserSignUpActivity.this,
                                    R.string.failed_verification,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
