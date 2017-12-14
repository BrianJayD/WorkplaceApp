package ca.uoit.csci4100u.workplace_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ca.uoit.csci4100u.workplace_app.R;

/**
 * Created by brianjayd on 2017-12-13.
 */

public class ShiftActions extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DataSnapshot mDataSnapShot;

    private String id, name, date, time;
    private TextView nameText, dateText, timeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shift_actions);

        Intent intent = getIntent();
        id = intent.getStringExtra("memberId");
        name = intent.getStringExtra("memberName");
        date = intent.getStringExtra("shiftDate");
        time = intent.getStringExtra("shiftTime");
        Log.i("OBTAINED:", id + name + date + time);


        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    protected void onStart() {
        super.onStart();

        nameText = (TextView)findViewById(R.id.name_text);
        dateText = (TextView)findViewById(R.id.date_text);
        timeText = (TextView)findViewById(R.id.time_text);

    }


    public void back(View view) {
        finish();
    }

}
