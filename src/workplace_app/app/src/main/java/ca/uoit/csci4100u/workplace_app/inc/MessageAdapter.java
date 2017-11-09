package ca.uoit.csci4100u.workplace_app.inc;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import ca.uoit.csci4100u.workplace_app.R;

/**
 * A customer adapter for the messages' list view in the 'ChatActivity'
 */
public class MessageAdapter extends ArrayAdapter<Message> {

    private Context mContext;
    private List<Message> mMessages;

    /**
     * Constructor for the MessageAdapter
     * @param context The context of the activity to be using this adapter
     * @param messages A list of messages each storing message data
     */
    public MessageAdapter(Context context, List<Message> messages) {
        super(context, R.layout.message_list_item, messages);
        this.mMessages = messages;
        this.mContext = context;
    }

    /**
     * A helper function to determine the number of messages
     * @return The number of messages
     */
    public int getCount() {
        return mMessages.size();
    }

    /**
     * A function to display a custom layout for each message list item depending on the user
     * @param position The position of the list item in the list
     * @param convertView The view the current list is displaying
     * @param parent The parent view (the list view)
     * @return The list view items to be returned
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        Message messageToDisplay = mMessages.get(position);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.message_list_item, parent, false);
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (messageToDisplay.getUserId().compareTo(auth.getCurrentUser().getUid()) == 0) {
            View viewById = convertView.findViewById(R.id.messageItem);
            viewById.setBackgroundResource(R.drawable.message_box_user);
        } else {
            View viewById = convertView.findViewById(R.id.messageItem);
            viewById.setBackgroundResource(R.drawable.message_box_other);
        }

        TextView lblName = convertView.findViewById(R.id.lblUserName);
        lblName.setText(messageToDisplay.getUserName());

        TextView lblAddress = convertView.findViewById(R.id.lblTime);
        lblAddress.setText(messageToDisplay.getTimeStamp());

        TextView lblCity = convertView.findViewById(R.id.lblMessage);
        lblCity.setText(messageToDisplay.getMessage());

        return convertView;
    }
}
