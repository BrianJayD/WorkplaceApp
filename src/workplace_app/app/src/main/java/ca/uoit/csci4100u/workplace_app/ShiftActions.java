package ca.uoit.csci4100u.workplace_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    private String id, name, date, time, shiftId, companyId, myId, myName;
    private int vacant;
    private List shiftList;
    private Shift currShift;
    private Button btnUFG, btnTake;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shift_actions);

        btnUFG = (Button)findViewById(R.id.btn_ufg);
        btnTake = (Button)findViewById(R.id.btn_take_shift);

        Intent intent = getIntent();
        id = intent.getStringExtra("memberId");
        name = intent.getStringExtra("memberName");
        date = intent.getStringExtra("shiftDate");
        time = intent.getStringExtra("shiftTime");
        vacant = intent.getIntExtra("vacant", 0);
        shiftId = intent.getStringExtra("shiftId");
        companyId = intent.getStringExtra("companyId");


        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        myId = mAuth.getCurrentUser().getUid();
        databaseListener();

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public void upForGrabs(View view) {
        if (currShift.getVacant() == 0) {
            RemoteDbHelper.updateVacant(mDataSnapShot, companyId, date, id, ShiftActions.this);
        }
    }

    public void takeShift(View view) {
        if (currShift.getVacant() == 1) {
            RemoteDbHelper.updateVacant(mDataSnapShot, companyId, date, id, ShiftActions.this);
            RemoteDbHelper.updateShiftInfo(mDataSnapShot, companyId, date, myId, currShift.getMemberId(), myName, ShiftActions.this);
            id = myId;
        }
    }

    public void updateTexts() {
        Log.i("KL", "" + name);
        TextView nameText = (TextView)findViewById(R.id.name_text);
        nameText.setText(currShift.getName());
    }


    public void updateButtons() {
        TextView status = (TextView)findViewById(R.id.text_status);

        if ((myId.equals(currShift.getMemberId())) && (currShift.getVacant() == 0)) {
            status.setText("Owned");
            status.setTextColor(Color.BLACK);
            btnUFG.setEnabled(true);
            btnTake.setEnabled(false);
        } else if ((myId.equals(currShift.getMemberId())) && (currShift.getVacant() == 1)) {
            status.setText("Owned but Available/Open");
            status.setTextColor(Color.parseColor("#ff9900"));
            btnUFG.setEnabled(false);
            btnTake.setEnabled(true);
        } else if ((!myId.equals(currShift.getMemberId())) && (currShift.getVacant() == 0)) {
            status.setText("Taken");
            status.setTextColor(Color.RED);
            btnUFG.setEnabled(false);
            btnTake.setEnabled(false);
        } else if ((!myId.equals(currShift.getMemberId())) && (currShift.getVacant() == 1)) {
            status.setText("Available/Open");
            status.setTextColor(Color.parseColor("#33cc33"));
            btnUFG.setEnabled(false);
            btnTake.setEnabled(true);
        }
    }

    public void updateInfo() {
        updateTexts();
        updateButtons();
        Log.i("KL", "Finish");
    }


    public void back(View view) {
        finish();
    }

    private void databaseListener() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mDataSnapShot = dataSnapshot;

                myName = RemoteDbHelper.convertUidToDispName(mDataSnapShot, myId, ShiftActions.this);

                shiftList = RemoteDbHelper.getShiftsForDay(mDataSnapShot, companyId, date, ShiftActions.this);

                currShift = RemoteDbHelper.getShiftDetails(mDataSnapShot, companyId, date, id, ShiftActions.this);

                Log.i("KL", "Updating....");

                updateInfo();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
