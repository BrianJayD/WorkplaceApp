package ca.uoit.csci4100u.workplace_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ca.uoit.csci4100u.workplace_app.inc.Message;
import ca.uoit.csci4100u.workplace_app.inc.MessageAdapter;
import ca.uoit.csci4100u.workplace_app.lib.DbHelper;

/**
 * The 'ChatActivity' class which is the activity the user will use to post and see messages
 */
public class ChatActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private DataSnapshot mDataSnapShot;
    private String mCurrCompany;
    private String mCurrChat;
    private List<Message> messageList;
    private static final String TAG = "ChatActivity:d";

    /**
     * The onCreate method for the 'ChatActivity' class. This function initializes the activity
     * and sets the member variables. This function will also set the database listener so that
     * any changes to the database will refresh the chat's view with the new messages
     * @param savedInstanceState The saved instance state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        messageList = new ArrayList<>();

        Intent landingPageIntent = getIntent();
        mCurrCompany = landingPageIntent.getStringExtra(DbHelper.COMPANY_ID);
        mCurrChat = landingPageIntent.getStringExtra(DbHelper.CHAT_ID);

        final ListView feed = findViewById(R.id.messageFeed);
        final MessageAdapter messageAdapter = new MessageAdapter(ChatActivity.this, messageList);
        feed.setAdapter(messageAdapter);
        feed.setOnItemClickListener(ChatActivity.this);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mDataSnapShot = dataSnapshot;
                messageList = DbHelper.getDbMessages(mDataSnapShot, mCurrCompany, mCurrChat);
                messageAdapter.clear();
                for (Message message : messageList) {
                    messageAdapter.add(message);
                }
                messageAdapter.notifyDataSetChanged();
                feed.setSelection(messageAdapter.getCount() - 1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled:failed");
            }
        });
    }

    @Override
    public void onItemClick(AdapterView aView, View source,
                            int position, long id) {

        // Do nothing right now
    }


    /**
     * The onClick function for the "Post" button. This function will take the information the user
     * has typed and post it to the database
     * @param view The view that has been clicked (the button)
     */
    public void handlePostMessage(View view) {
        final String message = ((EditText) findViewById(R.id.userMessage)).getText().toString();

        if (!message.isEmpty()) {
            DbHelper.postUserMessage(mDatabase, mDataSnapShot, mAuth, mCurrCompany, mCurrChat, message);
        }
    }

    /**
     * Handles the onClick function for the 'Back' button. This will close the current sub-activity
     * 'ChatActivity'
     * @param view The view that has been clicked (the button)
     */
    public void handleBack(View view) {
        finish();
    }
}