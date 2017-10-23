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
import com.google.firebase.auth.FirebaseAuth;

/**
 * The 'ResetPasswordActivity' class which is used to reset the user's password if they forgot it
 */
public class ResetPasswordActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String TAG = "ResetPasswordActivity";

    /**
     * The onCreate method for the 'ResetPasswordActivity' class. This function initializes the activity
     * and sets the member variables.
     * @param savedInstanceState The saved instance state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);

        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Handles the onClick function for the 'Reset password' button. This function sends an email to
     * the inputted email address to reset the user's password
     * @param view The view that has been clicked (the button)
     */
    public void handleReset(View view) {
        String email = ((EditText) findViewById(R.id.resetEmail)).getText().toString();

        if (!email.isEmpty()) {
            resetEmail(email);
        }
    }

    /**
     * Handles the onClick function for the 'Back' button. This will close the current sub-activity
     * 'ResetPasswordActivity'
     * @param view The view that has been clicked (the button)
     */
    public void handleBack(View view) {
        finish();
    }

    /**
     * A helper function used to reset the email associated with the Firebase account. This function
     * will create a toast if the reset email was sent successfully or not.
     * @param email A string representation of the user-specified email address
     */
    private void resetEmail(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "resetEmail:success");
                            Toast.makeText(ResetPasswordActivity.this, R.string.reset_password_email_sent,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, "resetEmail:failure");
                            Toast.makeText(ResetPasswordActivity.this, R.string.reset_password_email_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



}
