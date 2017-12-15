package ca.uoit.csci4100u.workplace_app;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    private List<Shift> shiftList;
    private Shift currShift;
    private Button btnUFG, btnTake, btnTrade, btnConfirm;
    private Dialog myDialog;
    private TextView close;
    private Spinner shiftSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shift_actions);

        btnUFG = (Button)findViewById(R.id.btn_ufg);
        btnTake = (Button)findViewById(R.id.btn_take_shift);
        btnTrade = (Button)findViewById(R.id.btn_trade_shift);
        myDialog = new Dialog(this);
        myDialog.setContentView(R.layout.trade_shift);
        btnConfirm = (Button)myDialog.findViewById(R.id.btn_confirm_trade);
        close = (TextView)myDialog.findViewById(R.id.close_trade);

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

    public void tradeShift(View view) {
        if (currShift.getVacant() == 1) {
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myDialog.dismiss();
                }
            });

            List<String> myShifts = new ArrayList<>();
            final List<Shift> condensedShifts = new ArrayList<>();
            String toGet, searchDate, searchTime;
            Log.i("COUNT", myShifts.size() + "");
            for (int i = 0; i < shiftList.size(); i++) {
                if (myId.equals(shiftList.get(i).getMemberId())) {
                    searchDate = shiftList.get(i).getDate();
                    searchTime = shiftList.get(i).getTime();
                    toGet = searchDate + " - " + searchTime;
                    myShifts.add(toGet);
                    condensedShifts.add(shiftList.get(i));
                    Log.i("ADD", "" + toGet);
                }
            }

            ArrayAdapter<String> shiftAdapter = new ArrayAdapter<String>(ShiftActions.this, android.R.layout.simple_spinner_item, myShifts);

            shiftAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            shiftSpinner = (Spinner)myDialog.findViewById(R.id.spinner_shift);
            shiftSpinner.setAdapter(shiftAdapter);


            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String selectedId = condensedShifts.get(shiftSpinner.getSelectedItemPosition()).getMemberId();
                    String selectedName = condensedShifts.get(shiftSpinner.getSelectedItemPosition()).getName();
                    String selectedTime = condensedShifts.get(shiftSpinner.getSelectedItemPosition()).getTime();
                    String selectedDate = condensedShifts.get(shiftSpinner.getSelectedItemPosition()).getDate();
                    Log.i("NEWNAME", currShift.getName());

                    RemoteDbHelper.tradeShifts(mDataSnapShot, companyId, selectedDate, selectedTime, myId, currShift.getName(), currShift.getMemberId(), ShiftActions.this);
                    RemoteDbHelper.tradeShifts(mDataSnapShot, companyId, currShift.getDate(), currShift.getTime(), currShift.getMemberId(), selectedName, selectedId, ShiftActions.this);

                    id = myId;

                }
            });


            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.show();


        }


    }

    public void updateTexts() {
        TextView nameText = (TextView)findViewById(R.id.name_text);
        nameText.setText(currShift.getName());
        TextView dateText = (TextView)findViewById(R.id.date_text);
        dateText.setText(currShift.getDate());
        TextView timeText = (TextView)findViewById(R.id.time_text);
        timeText.setText(currShift.getTime());
    }


    public void updateButtons() {
        TextView status = (TextView)findViewById(R.id.text_status);

        if ((myId.equals(currShift.getMemberId())) && (currShift.getVacant() == 0)) {
            status.setText("Owned");
            status.setTextColor(Color.BLACK);
            btnUFG.setEnabled(true);
            btnTake.setEnabled(false);
            btnTrade.setEnabled(false);
        } else if ((myId.equals(currShift.getMemberId())) && (currShift.getVacant() == 1)) {
            status.setText("Owned but Available/Open");
            status.setTextColor(Color.parseColor("#ff9900"));
            btnUFG.setEnabled(false);
            btnTake.setEnabled(true);
            btnTrade.setEnabled(false);
        } else if ((!myId.equals(currShift.getMemberId())) && (currShift.getVacant() == 0)) {
            status.setText("Taken");
            status.setTextColor(Color.RED);
            btnUFG.setEnabled(false);
            btnTake.setEnabled(false);
            btnTrade.setEnabled(false);
        } else if ((!myId.equals(currShift.getMemberId())) && (currShift.getVacant() == 1)) {
            status.setText("Available/Open");
            status.setTextColor(Color.parseColor("#33cc33"));
            btnUFG.setEnabled(false);
            btnTake.setEnabled(true);
            btnTrade.setEnabled(true);
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

                updateInfo();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
