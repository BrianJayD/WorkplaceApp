package ca.uoit.csci4100u.workplace_app.inc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import ca.uoit.csci4100u.workplace_app.R;

/**
 * Created by brianjayd on 2017-12-13.
 */

public class ShiftAdapter extends ArrayAdapter<Shift> {

    private Context context;
    private List<Shift> shifts;

    public ShiftAdapter(Context context, List<Shift> shifts) {
        super(context, R.layout.list_shift_item, shifts);
        this.context = context;
        this.shifts = shifts;
    }

    public View getView(int position, View newView, ViewGroup parent) {
        Shift shiftItem = shifts.get(position);

        LayoutInflater inflater = LayoutInflater.from(context);
        newView = inflater.inflate(R.layout.list_shift_item, parent, false);

        TextView shiftName = newView.findViewById(R.id.shift_item_name);
        shiftName.setText(shiftItem.getName());

        TextView shiftTime = newView.findViewById(R.id.shift_item_time);
        shiftTime.setText(shiftItem.getTime());

        return newView;
    }

}
