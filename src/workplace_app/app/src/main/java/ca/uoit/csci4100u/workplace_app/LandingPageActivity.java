package ca.uoit.csci4100u.workplace_app;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import ca.uoit.csci4100u.workplace_app.inc.Chat;
import ca.uoit.csci4100u.workplace_app.inc.Company;
import ca.uoit.csci4100u.workplace_app.lib.LocalDbHelper;
import ca.uoit.csci4100u.workplace_app.lib.RemoteDbHelper;

/**
 * The 'LandingPageActivity' class which is the main landing page that the user will see after
 * logging in. It will allow the user to access a number of application features specific to the
 * companies that the user is a part of.
 */
public class LandingPageActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DataSnapshot mDataSnapShot;
    private String mCurrCompany;
    private String mCurrChat;
    private LocalDbHelper localDbHelper;
    private static final String TAG = "LandingPageActivity:d";
    private Toolbar toolbar;

    /**
     * The onCreate method for the 'LandingPageActivity' class. This function initializes the activity
     * and sets the member variables. This sets the database listener to listen for changes in the
     * database as well as a listener for the company list spinner (to update the chat list spinner)
     * @param savedInstanceState The saved instance state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_page);

        toolbar = findViewById(R.id.landing_toolbar);
        setSupportActionBar(toolbar);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mCurrCompany = null;
        mCurrChat = null;

        localDbHelper = new LocalDbHelper(this);

        final String userId = mAuth.getCurrentUser().getUid();
        final String userName = mAuth.getCurrentUser().getDisplayName();
        if (!localDbHelper.checkUserExists(userId)) {
            localDbHelper.createUser(userId, userName);
        }

        localDbHelper.getCompanyListForCurrUser(userId);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                mDataSnapShot = dataSnapshot;
                List<Company> companyListForCurrUser = RemoteDbHelper.getCompanyListForCurrUser(mAuth, mDataSnapShot, localDbHelper);
                /*
                 * TODO: Create a local database to check if the remote isn't there
                 */
//                if (haveNetworkConnection()) {
//                    companyListForCurrUser = RemoteDbHelper.getCompanyListForCurrUser(mAuth, mDataSnapShot, localDbHelper);
//                } else {
//                    companyListForCurrUser = localDbHelper.getCompanyListForCurrUser(userId);
//                }


                ArrayAdapter<Company> companyAdapter = new ArrayAdapter<>(LandingPageActivity.this, android.R.layout.simple_spinner_item, companyListForCurrUser);
                companyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                Spinner spinnerCompanyItems = findViewById(R.id.companyList);
                spinnerCompanyItems.setAdapter(companyAdapter);

                Company selectedCompany = (Company)((Spinner)findViewById(R.id.companyList)).getSelectedItem();
                if (selectedCompany != null) {
                    mCurrCompany = selectedCompany.getCompanyId();
                }

                spinnerCompanyItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        mCurrCompany = ((Company)((Spinner)findViewById(R.id.companyList)).getSelectedItem()).getCompanyId();

                        List<Chat> spinnerChatList = RemoteDbHelper.getChatListForSpecifiedCompany(mDataSnapShot, mCurrCompany);
                        ArrayAdapter<Chat> chatAdapter = new ArrayAdapter<>(LandingPageActivity.this, android.R.layout.simple_spinner_item, spinnerChatList);
                        chatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                        Spinner spinnerChatItems = findViewById(R.id.chatList);
                        spinnerChatItems.setAdapter(chatAdapter);

                        Chat selectedChat = (Chat)(((Spinner)findViewById(R.id.chatList)).getSelectedItem());
                        if (selectedChat != null) {
                            mCurrChat = selectedChat.getChatId();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled:failed");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_settings:
                Intent intent = new Intent(LandingPageActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Handles the onClick function for the 'Company Sign Up' button. This function starts the
     * sub-activity 'CompanySignUpActivity'
     * @param view The view that has been clicked (the button)
     */
    public void handleCompanySignUp(View view) {
        Intent intent = new Intent(LandingPageActivity.this, CompanySignUpActivity.class);
        startActivity(intent);
    }

    /**
     * Handles the onClick function for the 'Chat Rooms' button. This function will start the
     * sub-activity 'ChatActivity' and tell the 'ChatActivity' which company/chat to load
     * @param view The view that has been clicked (the button)
     */
    public void handleChat(View view) {
        Company selectedCompany = (Company)(((Spinner)findViewById(R.id.companyList)).getSelectedItem());
        mCurrCompany = selectedCompany.getCompanyId();

        Intent intent =  new Intent(LandingPageActivity.this, ChatActivity.class);
        if (mCurrCompany != null && mCurrChat != null) {
            intent.putExtra(RemoteDbHelper.COMPANY_ID, mCurrCompany);
            intent.putExtra(RemoteDbHelper.CHAT_ID, mCurrChat);
            startActivity(intent);
        } else {
            Toast.makeText(LandingPageActivity.this, R.string.select_comp_or_chat,
                    Toast.LENGTH_SHORT).show();
        }
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

    /**
     * Check if the device currently is connected to a network
     * @return True or false based on if the device is connected to a network
     */
    private boolean haveNetworkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    /**
     * Handles the onClick function for the 'Add Member' button. This function will add a user to
     * the specified company (through the company list spinner)
     * @param view The view that has been clicked (the button)
     */
    public void handleAddMember(View view) {
        String email = ((EditText)findViewById(R.id.newMemberEmail)).getText().toString();

        if (!email.isEmpty()) {
            mCurrCompany = ((Company) ((Spinner) findViewById(R.id.companyList)).getSelectedItem()).getCompanyId();
            String userId = RemoteDbHelper.checkEmailExistsAndGetUid(mDataSnapShot, email);
            if (!userId.isEmpty()) {
                Toast.makeText(LandingPageActivity.this, R.string.user_added,
                        Toast.LENGTH_SHORT).show();
                RemoteDbHelper.addMemberToCompanyDb(mDatabase, mDataSnapShot, mCurrCompany, userId);
            } else {
                Toast.makeText(LandingPageActivity.this, R.string.user_does_not_exist,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
