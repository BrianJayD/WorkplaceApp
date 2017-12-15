package ca.uoit.csci4100u.workplace_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import ca.uoit.csci4100u.workplace_app.R;
import ca.uoit.csci4100u.workplace_app.inc.Shift;
import ca.uoit.csci4100u.workplace_app.lib.RemoteDbHelper;

/**
 * Created by brianjayd on 2017-12-13.
 */

public class ShiftActions extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DataSnapshot mDataSnapShot;

    private String id, name, date, time, shiftId, companyId;
    private int vacant;
    private TextView nameText, dateText, timeText;
    private List shiftList;
    private Shift currShift;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shift_actions);

        nameText = (TextView)findViewById(R.id.name_text);
        dateText = (TextView)findViewById(R.id.date_text);
        timeText = (TextView)findViewById(R.id.time_text);


        Intent intent = getIntent();
        id = intent.getStringExtra("memberId");
        name = intent.getStringExtra("memberName");
        date = intent.getStringExtra("shiftDate");
        time = intent.getStringExtra("shiftTime");
        vacant = intent.getIntExtra("vacant", 0);
        shiftId = intent.getStringExtra("shiftId");
        companyId = intent.getStringExtra("companyId");

        nameText.setText(name);
        dateText.setText(date);
        timeText.setText(time);

        Log.i("OBTAINED:", id + name + date + time + vacant);
        Log.i("OBTAINED:", shiftId + " - " + companyId);


        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        databaseListener();




    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public void upForGrabs(View view) {
       RemoteDbHelper.updateVacant(mDataSnapShot, mDatabase, companyId, date, id, 1, ShiftActions.this);
    }

    public void updateInfo() {

    }

    public void back(View view) {
        finish();
    }

    private void databaseListener() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mDataSnapShot = dataSnapshot;

                shiftList = RemoteDbHelper.getShiftsForDay(mDataSnapShot, companyId, date,
                        ShiftActions.this);

                currShift = RemoteDbHelper.getShiftDetails(mDataSnapShot, companyId, date, id, ShiftActions.this);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
