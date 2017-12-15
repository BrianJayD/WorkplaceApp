package ca.uoit.csci4100u.workplace_app;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ca.uoit.csci4100u.workplace_app.inc.Member;
import ca.uoit.csci4100u.workplace_app.inc.Shift;
import ca.uoit.csci4100u.workplace_app.inc.ShiftAdapter;
import ca.uoit.csci4100u.workplace_app.lib.LocalDbHelper;
import ca.uoit.csci4100u.workplace_app.lib.RemoteDbHelper;

/**
 * Created by brianjayd on 2017-11-29.
 */

public class CalendarActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DataSnapshot mDataSnapShot;
    private LocalDbHelper mLocalDbHelper;

    private TextView textClose, editDate;
    private Button btnConfirm;
    private CalendarView shiftCalendar;
    private Dialog myDialog;
    private Spinner memberSpinner;
    private TimePicker shiftTimePicker;
    private ListView shiftListView;

    private String currCompanyId, selectDate;
    private int mYear, mMonth, mDay;
    private List<Member> membersList;
    private List<Shift> shiftList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.company_calendar);
        Intent intent = getIntent();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        myDialog = new Dialog(this);
        currCompanyId = intent.getStringExtra("companyId");
        Log.i("BRIAN", currCompanyId);

        databaseListener();

        shiftCalendar = (CalendarView)findViewById(R.id.shift_calendar);
        shiftListView = (ListView)findViewById(R.id.shift_list);
        shiftCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                mMonth = month+=1; //Displays actual month
                mYear = year;
                mDay = day;
                selectDate = mYear + "/" + mMonth + "/" + mDay;
                Toast.makeText(getApplicationContext(), selectDate, Toast.LENGTH_SHORT).show();
                databaseListener();
                displayShifts();
                Log.i("CLICKED 1", Integer.toString(shiftList.size()));
            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();

        mYear = 1111;
        mMonth = 01;
        mDay = 01;
        selectDate = mYear + "/" + mMonth + "/" + mDay;

        databaseListener();

        myDialog.setContentView(R.layout.add_shift);
        textClose = (TextView)myDialog.findViewById(R.id.close_add);
        editDate = (TextView) myDialog.findViewById(R.id.edit_date);
        btnConfirm = (Button)myDialog.findViewById(R.id.btn_confirm_new_shift);
        shiftTimePicker = (TimePicker)myDialog.findViewById(R.id.shift_time_picker);

    }

    /**
     * This function handles the new dialog that will pop up when the user clicks the add button
     *
     * @param view The view that will be clicked (Button)
     */
    public void newShift(View view) {

        // OnClickListener for the close button
        textClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
            }
        });


        List<String> memberNames = new ArrayList<>();

        for (int i = 0; i < membersList.size(); i++ ) {
            memberNames.add(membersList.get(i).getMemberName());
            Log.i("Name", memberNames.get(i));
        }

        ArrayAdapter<String> memberArrayAdapter = new ArrayAdapter<String>
                (CalendarActivity.this, android.R.layout.simple_spinner_item, memberNames);

        memberArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        memberSpinner = (Spinner)myDialog.findViewById(R.id.spinner_employee);
        editDate.setHint(selectDate);
        memberSpinner.setAdapter(memberArrayAdapter);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectedMemberId = membersList.get(memberSpinner.getSelectedItemPosition()).getMemberId();
                String selectedMemberName = membersList.get(memberSpinner.getSelectedItemPosition()).getMemberName();
                String shiftTime = convertTime(shiftTimePicker.getHour(), shiftTimePicker.getMinute());
                Log.i("SHIFT", shiftTime);
                RemoteDbHelper.createShifts(mDatabase, currCompanyId, selectedMemberName, selectedMemberId, selectDate, shiftTime, CalendarActivity.this);
            }
        });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();

    }

    /**
     * This function grabs the members from the Firebase and puts them in membersList
     */
    private void databaseListener() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mDataSnapShot = dataSnapshot;

                membersList = RemoteDbHelper.getCompanyMembers(mDataSnapShot, currCompanyId,
                        mLocalDbHelper, CalendarActivity.this);

                shiftList = RemoteDbHelper.getShiftsForDay(mDataSnapShot, currCompanyId, selectDate, CalendarActivity.this);
                Log.i("LISTENER", "");
                displayShifts();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("DATABASE LISTENER", "FAILED");
            }
        });
    }

    private String convertTime(int hour, int min) {
        String newHour = hour + "";
        Log.i("HOUR", Integer.toString(hour));
        if (hour == 0) {
            newHour = 12 + ":" + String.format("%02d",min) + "AM";
        } else if (hour == 12) {
            newHour = String.format("%02d", hour) + ":" + String.format("%02d",min) + "PM";
        } else if (hour < 12 && hour != 0) {
            newHour = String.format("%02d", hour) + ":" + String.format("%02d",min) + "AM";
        } else if (hour > 12) {
            newHour = String.format("%02d", (hour % 12)) + ":" + String.format("%02d",min) + "PM";
        }

        return newHour;
    }

    public void displayShifts() {

        final ShiftAdapter shiftDetailArray = new ShiftAdapter(CalendarActivity.this, shiftList);
        shiftListView.setAdapter(shiftDetailArray);

        shiftListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Shift selectShift = shiftList.get(i);

                view.setSelected(true);

                Intent intent = new Intent(getBaseContext(), ShiftActions.class);
                intent.putExtra("memberId", selectShift.getMemberId());
                intent.putExtra("memberName", selectShift.getName());
                intent.putExtra("shiftDate", selectShift.getDate());
                intent.putExtra("shiftTime", selectShift.getTime());
                intent.putExtra("vacant", selectShift.getVacant());
                intent.putExtra("companyId", currCompanyId);
                startActivity(intent);

            }
        });

    }
}
