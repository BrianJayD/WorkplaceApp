package ca.uoit.csci4100u.workplace_app;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import ca.uoit.csci4100u.workplace_app.inc.Member;
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

    private String currCompanyId, selectDate;
    private int mYear, mMonth, mDay;
    private List<Member> membersList;

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

        shiftCalendar = (CalendarView)findViewById(R.id.shift_calendar);
        shiftCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                month+=1; //Displays actual month
                selectDate = year + "/" + month + "/" + day;
                Toast.makeText(getApplicationContext(), selectDate, Toast.LENGTH_LONG).show();
            }
        });

        databaseListener();

    }


    @Override
    protected void onStart() {
        super.onStart();

        databaseListener();

        myDialog.setContentView(R.layout.add_shift);
        textClose = (TextView)myDialog.findViewById(R.id.close_add);
        editDate = (TextView) myDialog.findViewById(R.id.edit_date);
        btnConfirm = (Button)myDialog.findViewById(R.id.btn_confirm_new_shift);

    }

    /**
     * This function handles the new dialog that will pop up when the user clicks the add button
     *
     * @param view The view that will be clicked (Button)
     */
    public void newShift(View view) {

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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("DATABASE LISTENER", "FAILED");
            }
        });
    }
}
