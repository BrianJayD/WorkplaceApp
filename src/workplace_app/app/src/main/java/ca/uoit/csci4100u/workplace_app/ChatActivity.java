package ca.uoit.csci4100u.workplace_app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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
import ca.uoit.csci4100u.workplace_app.lib.LocalDbHelper;
import ca.uoit.csci4100u.workplace_app.lib.RemoteDbHelper;

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
    private LocalDbHelper localDbHelper;
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
        mCurrCompany = landingPageIntent.getStringExtra(RemoteDbHelper.COMPANY_ID);
        mCurrChat = landingPageIntent.getStringExtra(RemoteDbHelper.CHAT_ID);
        localDbHelper = new LocalDbHelper(this);

        final ListView feed = (ListView) findViewById(R.id.messageFeed);
        final MessageAdapter messageAdapter = new MessageAdapter(ChatActivity.this, messageList);
        feed.setAdapter(messageAdapter);
        feed.setOnItemClickListener(ChatActivity.this);

        messageList = localDbHelper.getMessagesForCurrChat(mCurrChat);
        for (Message message : messageList) {
            messageAdapter.add(message);
        }
        messageAdapter.notifyDataSetChanged();
        feed.setSelection(messageAdapter.getCount() - 1);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mDataSnapShot = dataSnapshot;
                messageList = RemoteDbHelper.getDbMessages(mDataSnapShot, mCurrCompany, mCurrChat, localDbHelper, ChatActivity.this);
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
        final Message selectedMessage = messageList.get(position);
        if (RemoteDbHelper.isNetworkAvailable(ChatActivity.this)) {
            if (selectedMessage.getUserId().compareTo(mAuth.getUid()) == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                builder.setMessage(R.string.delete_this)
                        .setTitle(R.string.delete_this);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        RemoteDbHelper.deleteMessage(mCurrCompany, mCurrChat, selectedMessage.getMessageId(), mDatabase, localDbHelper, ChatActivity.this);
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Do nothing
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }

    /**
     * The onClick function for the "Post" button. This function will take the information the user
     * has typed and post it to the database
     * @param view The view that has been clicked (the button)
     */
    public void handlePostMessage(View view) {
        if (!RemoteDbHelper.isNetworkAvailable(ChatActivity.this)) {
            Toast.makeText(ChatActivity.this, R.string.no_network_connection,
                    Toast.LENGTH_SHORT).show();
        } else {
            final String message = ((EditText) findViewById(R.id.userMessage)).getText().toString();

            if (!message.isEmpty()) {
                RemoteDbHelper.postUserMessage(mDatabase, mDataSnapShot, mAuth, mCurrCompany, mCurrChat, message, ChatActivity.this);
            }
            ((EditText) findViewById(R.id.userMessage)).setText("");
        }
    }
}