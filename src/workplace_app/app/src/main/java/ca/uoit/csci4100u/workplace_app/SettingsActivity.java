package ca.uoit.csci4100u.workplace_app;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 * The 'SettingsActivity' class which is the activity the user will use to update their user
 * information
 */
public class SettingsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    /**
     * The onCreate method for the 'SettingsActivity' class. This function initializes the activity
     * and sets the member variables.
     * @param savedInstanceState The saved instance state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Handles the onClick function for the 'Update Display Name' button. This will take the values in the
     * EditText field associated with the new display name and update the display name associated with
     * the user.
     * @param view The view that has been clicked (the button)
     */
    public void handleUpdateNewDisplayName(View view) {
        final String displayName = ((EditText) findViewById(R.id.updateDisplayName)).getText().toString();

        if (!displayName.isEmpty()) {
            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build();
            mAuth.getCurrentUser().updateProfile(userProfileChangeRequest)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SettingsActivity.this, R.string.display_name_update_success,
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SettingsActivity.this, R.string.display_name_update_fail,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    /**
     * Handles the onClick function for the 'Update Password' button. This will take the values in the
     * EditText field associated with the new password and update the password associated with
     * the user.
     * @param view The view that has been clicked (the button)
     */
    public void handleUpdateNewPassword(View view) {
        final String password = ((EditText) findViewById(R.id.updatePass)).getText().toString();

        if (!password.isEmpty()) {
            mAuth.getCurrentUser().updatePassword(password)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SettingsActivity.this, R.string.password_update_success,
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SettingsActivity.this, R.string.password_update_fail,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    /**
     * Handles the onClick function for the 'Update Email' button. This will take the values in the
     * EditText field associated with the new email and update the email address associated with
     * the user. Afterwards, the new email address will be sent a verification email.
     * @param view The view that has been clicked (the button)
     */
    public void handleUpdateNewEmail(View view) {
        final String email = ((EditText) findViewById(R.id.updateEmail)).getText().toString();

        if (!email.isEmpty()) {
            mAuth.getCurrentUser().updateEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SettingsActivity.this, R.string.email_update_success,
                                        Toast.LENGTH_SHORT).show();
                                sendEmailVerification();
                            } else {
                                Toast.makeText(SettingsActivity.this, R.string.email_update_fail,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    /**
     * Handles the onClick function for the 'Back' button. This will close the current sub-activity
     * 'SettingsActivity'
     * @param view The view that has been clicked (the button)
     */
    public void handleBack(View view) {
        finish();
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
                            Toast.makeText(SettingsActivity.this,
                                    getString(R.string.success_verification) + " " +  user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SettingsActivity.this,
                                    R.string.failed_verification,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}