package ca.uoit.csci4100u.workplace_app.inc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ca.uoit.csci4100u.workplace_app.R;

public class MessageAdapter extends ArrayAdapter<Message> {
    private Context context;
    private List<Message> data;

    public MessageAdapter(Context context, List<Message> data) {
        super(context, R.layout.message_list_item, data);
        this.data = data;
        this.context = context;
    }

    public int getCount() {
        return data.size();
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Message messageToDisplay = data.get(position);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.message_list_item, parent, false);
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
