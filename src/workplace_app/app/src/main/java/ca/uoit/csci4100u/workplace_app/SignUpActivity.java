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

/**
 * The 'SignUpActivity' class which is the activity the user sees when needing to create a new
 * account. This handles creating a new account and moving back to the 'LoginActivity'
 */
public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String TAG = "SignUpActivity";

    /**
     * The onCreate method for the 'SignUpActivity' class. This function initializes the activity.
     * @param savedInstanceState The saved instance state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Handles the onClick function for the 'Sign Up' button. This will take the values in the
     * EditText fields and create an account for the new user
     * @param view The view that has been clicked (the button)
     */
    public void handleSignUp(View view) {
        String email = ((EditText) findViewById(R.id.signUpEmail)).getText().toString();
        String password = ((EditText) findViewById(R.id.signUpEmail)).getText().toString();

        createAccount(email, password);
        finish();
    }

    /**
     * Handles the onClick function for the 'Back' button. This will close the current sub-activity
     * 'SignUpActivity'
     * @param view The view that has been clicked (the button)
     */
    public void handleBack(View view) {
        finish();
    }

    /**
     * A helper method used to create a new firebase account
     * @param email A string representation of the user-specified email address
     * @param password A string representation of the user-specified password
     */
    private void createAccount(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            //FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "ERROR", Toast.LENGTH_LONG);
                        }
                    }
                });
    }
}
