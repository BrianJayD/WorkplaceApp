package ca.uoit.csci4100u.workplace_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ca.uoit.csci4100u.workplace_app.lib.RemoteDbHelper;

public class CompanyManagement extends AppCompatActivity {

    private String mCurrCompany;
    private DatabaseReference mDatabase;
    private DataSnapshot mDataSnapShot;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.company_management);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        Intent intent = getIntent();
        mCurrCompany = intent.getStringExtra("currCompany");
        mAuth = FirebaseAuth.getInstance();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mDataSnapShot = dataSnapshot;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Handles the onClick function for the 'Add Member' button. This function will add a user to
     * the specified company (through the company list spinner)
     * @param view The view that has been clicked (the button)
     */
    public void handleAddMember(View view) {
        String email = ((EditText)findViewById(R.id.newMemberEmail)).getText().toString();

        if (!email.isEmpty()) {
            String userId = RemoteDbHelper.checkEmailExistsAndGetUid(mDataSnapShot, email);
            if (!userId.isEmpty()) {
                Toast.makeText(CompanyManagement.this, R.string.user_added,
                        Toast.LENGTH_SHORT).show();
                RemoteDbHelper.addMemberToCompanyDb(mDatabase, mDataSnapShot, mCurrCompany, userId);
            } else {
                Toast.makeText(CompanyManagement.this, R.string.user_does_not_exist,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void handleAddChatRoom(View view) {
        String chatName = ((EditText)findViewById(R.id.newMemberEmail)).getText().toString();

    }
}
