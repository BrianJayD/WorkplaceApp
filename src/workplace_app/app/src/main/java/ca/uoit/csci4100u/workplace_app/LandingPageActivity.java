package ca.uoit.csci4100u.workplace_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
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

    private String location;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DataSnapshot mDataSnapShot;
    private Company mCurrCompany;
    private Chat mCurrChat;
    private LocalDbHelper mLocalDbHelper;
    private List<Company> mCompanyList;
    private List<Chat> mChatList;
    private List<String> mAnnoucements;
    private static final String TAG = "LandingPageActivity:d";

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

        // Initialize member variables
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mCurrCompany = null;
        mCurrChat = null;
        mAnnoucements = new ArrayList<>();
        mLocalDbHelper = new LocalDbHelper(this);

        // Add a listener to the remote database that will update the spinners to any new changes
        addListenerToDatabase();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Create a database entry for the current user if one does not already exist
        createUserInLocalDatabase(mAuth.getCurrentUser().getUid(), mAuth.getCurrentUser().getDisplayName());

        // Update variables from local database, remote listener will override these if the database changes
        updateFromLocalDB();

        // Setup the drawer
        populateDrawer();

        // Force onPrepareOptionsMenu to be called again
        invalidateOptionsMenu();

        // Updates UI
        //updateUserInterface();
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem management = menu.findItem(R.id.menu_management);
        MenuItem settings = menu.findItem(R.id.menu_item_settings);
        MenuItem create = menu.findItem(R.id.create_company);
        if (!RemoteDbHelper.isNetworkAvailable(LandingPageActivity.this)) {
            management.setVisible(false);
            settings.setVisible(false);
            create.setVisible(false);
            return true;
        } else {
            if (mCurrCompany == null) {
                management.setVisible(false);
            } else {
                management.setVisible(true);
            }
            return true;
        }
    }

    private void updateFromLocalDB() {
        String userId = mAuth.getCurrentUser().getUid();

        mCompanyList = mLocalDbHelper.getCompanyListForCurrUser(userId);

        if (mCompanyList.size() > 0) {
            mCurrCompany = mCompanyList.get(0);
            mChatList = mLocalDbHelper.getChatListForSpecifiedCompany(mCurrCompany.getCompanyId());
            mAnnoucements = mLocalDbHelper.getAnnouncementsForCompany(mCurrCompany.getCompanyId());
            updateUserInterface();
        } else {
            mCurrCompany = null;
            mCurrChat = null;
            mAnnoucements = null;
            TextView title = (TextView) findViewById(R.id.selectedCompany);
            title.setText(getString(R.string.no_company));
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mCurrCompany = mCompanyList.get(position);
            if (RemoteDbHelper.isNetworkAvailable(LandingPageActivity.this)) {
                mChatList = RemoteDbHelper.getChatListForSpecifiedCompany(mDataSnapShot, mCurrCompany.getCompanyId(), mLocalDbHelper, LandingPageActivity.this);
                mAnnoucements = RemoteDbHelper.getAnnouncementsForSpecifiedCompany(mDataSnapShot, mCurrCompany.getCompanyId(), mLocalDbHelper, LandingPageActivity.this);
            } else {
                mChatList = mLocalDbHelper.getChatListForSpecifiedCompany(mCurrCompany.getCompanyId());
                mAnnoucements = mLocalDbHelper.getAnnouncementsForCompany(mCurrCompany.getCompanyId());
            }
            updateUserInterface();
        }
    }

    private void updateUserInterface(){
        updateVisibility();
        updateSpinner();
        updateAnnouncements();
        updateTitle();
    }

    private void updateVisibility() {
        View view = findViewById(R.id.companyFeatures);
        if (mCompanyList.size() > 0) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    private void updateTitle() {
        TextView title = (TextView)findViewById(R.id.selectedCompany);
        title.setText(mCurrCompany.getCompanyName());
    }

    private void updateAnnouncements() {
        ArrayAdapter<String> announcementsAdapter = new ArrayAdapter<>(LandingPageActivity.this, android.R.layout.simple_list_item_1, mAnnoucements);
        ListView announcementsList = (ListView) findViewById(R.id.announcements);
        announcementsList.setAdapter(announcementsAdapter);
    }

    private void updateSpinner(){
        ArrayAdapter<Chat> chatAdapter = new ArrayAdapter<>(LandingPageActivity.this, android.R.layout.simple_spinner_item, mChatList);
        chatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        Spinner spinnerChatItems = (Spinner) findViewById(R.id.chatList);
        spinnerChatItems.setAdapter(chatAdapter);

        Chat selectedChat = (Chat)(((Spinner)findViewById(R.id.chatList)).getSelectedItem());
        if (selectedChat != null) {
            mCurrChat = selectedChat;
        }
    }

    private void populateDrawer() {
        ListView drawerList = (ListView)findViewById(R.id.left_drawer);

        List<String> companyNames = new ArrayList<>();

        for (int i = 0; i < mCompanyList.size(); i++) {
            companyNames.add(mCompanyList.get(i).getCompanyName());
        }

        drawerList.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, companyNames));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    private void createUserInLocalDatabase(String userId, String userName) {
        if (!mLocalDbHelper.checkUserExists(userId)) {
            mLocalDbHelper.createUser(userId, userName);
        }
    }

    private void addListenerToDatabase() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                mDataSnapShot = dataSnapshot;

                // Get the current list of companies that the user is a part of
                mCompanyList = RemoteDbHelper.getCompanyListForCurrUser(mAuth, mDataSnapShot, mLocalDbHelper, LandingPageActivity.this);
                if (mCompanyList.size() > 0) {
                    mCurrCompany = mCompanyList.get(0);
                    mChatList = RemoteDbHelper.getChatListForSpecifiedCompany(mDataSnapShot, mCurrCompany.getCompanyId(), mLocalDbHelper, LandingPageActivity.this);
                    mAnnoucements = RemoteDbHelper.getAnnouncementsForSpecifiedCompany(mDataSnapShot, mCurrCompany.getCompanyId(), mLocalDbHelper, LandingPageActivity.this);
                    location = RemoteDbHelper.getCompanyLocation(mDataSnapShot, mCurrCompany.getCompanyId(), LandingPageActivity.this);

                    updateUserInterface();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled:failed");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menu_item_settings:
                intent = new Intent(LandingPageActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_management:
                intent = new Intent(LandingPageActivity.this, CompanyManagement.class);
                intent.putExtra("currCompany", mCurrCompany.getCompanyId());
                startActivity(intent);
                return true;
            case R.id.create_company:
                intent = new Intent(LandingPageActivity.this, CompanySignUpActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_sign_out:
                mAuth.signOut();
                intent = new Intent(LandingPageActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void handleMaps(View view) {
        Intent showMap = new Intent(this, MapsActivity.class);
        System.out.println("->" + mCurrCompany.getCompanyLoc());
        showMap.putExtra("location", location);
        startActivity(showMap);

    }

    public void handleCalendar(View view) {
        Intent showCalendar = new Intent(this, CalendarActivity.class);
        showCalendar.putExtra("companyId", mCurrCompany.getCompanyId());
        startActivity(showCalendar);
    }

    /**
     * Handles the onClick function for the 'Chat Rooms' button. This function will start the
     * sub-activity 'ChatActivity' and tell the 'ChatActivity' which company/chat to load
     * @param view The view that has been clicked (the button)
     */
    public void handleChat(View view) {
        Intent intent = new Intent(LandingPageActivity.this, ChatActivity.class);
        Chat selectedChat = (Chat)(((Spinner)findViewById(R.id.chatList)).getSelectedItem());
        if (selectedChat != null) {
            mCurrChat = selectedChat;
        }
        if (mCurrCompany != null && mCurrChat != null) {
            intent.putExtra(RemoteDbHelper.COMPANY_ID, mCurrCompany.getCompanyId());
            intent.putExtra(RemoteDbHelper.CHAT_ID, mCurrChat.getChatId());
            startActivity(intent);
        } else {
            Toast.makeText(LandingPageActivity.this, R.string.select_comp_or_chat,
                    Toast.LENGTH_SHORT).show();
        }
    }
}
