package ca.uoit.csci4100u.workplace_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * The 'ChatActivity' class which is the activity the user will use to post and see messages
 */
public class ChatActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private DataSnapshot mDataSnapShot;
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

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mDataSnapShot = dataSnapshot;
                // TODO: The companyId and chatId should be taken from an intent at this stage. The below code should be deleted after this is done
                Iterable<DataSnapshot> companies = mDataSnapShot.child(DbHelper.COMPANIES).getChildren();
                String companyId = "";
                for (DataSnapshot company : companies) {
                    companyId = company.getKey();
                    break;
                }
                Iterable<DataSnapshot> chats = mDataSnapShot.child(DbHelper.COMPANIES).child(companyId).child(DbHelper.CHATS).getChildren();
                String chatId = "";
                for (DataSnapshot chat : chats) {
                    chatId = chat.getKey();
                    break;
                }
                // TODO: -----------------------------------------------------------

                List<Message> messageList = DbHelper.getDbMessages(mDataSnapShot, companyId, chatId);
                ((TextView) findViewById(R.id.chatMessages)).setText("");
                for (Message message : messageList) {
                    ((TextView) findViewById(R.id.chatMessages)).append(message.toString(mDataSnapShot) + "\n");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled:failed");
            }
        });
    }

    /**
     * The onClick function for the "Post" button. This function will take the information the user
     * has typed and post it to the database
     * @param view The view that has been clicked (the button)
     */
    public void handlePostMessage(View view) {
        final String message = ((EditText) findViewById(R.id.userMessage)).getText().toString();
        // TODO: The companyId and chatId should be taken from an intent at this stage. The below code should be deleted after this is done
        Iterable<DataSnapshot> companies = mDataSnapShot.child(DbHelper.COMPANIES).getChildren();
        String companyId = "";
        for (DataSnapshot company : companies) {
            companyId = company.getKey();
            break;
        }
        Iterable<DataSnapshot> chats = mDataSnapShot.child(DbHelper.COMPANIES).child(companyId).child(DbHelper.CHATS).getChildren();
        String chatId = "";
        for (DataSnapshot chat : chats) {
            chatId = chat.getKey();
            break;
        }
        // TODO: -----------------------------------------------------------

        if (!message.isEmpty()) {
            DbHelper.postUserMessage(mDatabase, mAuth, companyId, chatId, message);
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
