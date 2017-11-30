package ca.uoit.csci4100u.workplace_app;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by brianjayd on 2017-11-29.
 */

public class CalendarActivity extends AppCompatActivity {

    private CalendarView shiftCalendar;
    private Dialog myDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.company_calendar);

        myDialog = new Dialog(this);

        shiftCalendar = (CalendarView)findViewById(R.id.shift_calendar);
        shiftCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                month+=1; //Displays actual month
                String tellDate = year + "/" + month + "/" + day;
                Toast.makeText(getApplicationContext(), tellDate, 200).show();
            }
        });

    }

    public void newShift(View view) {
        TextView textClose;
        Button btnConfirm;

        myDialog.setContentView(R.layout.add_shift);

        textClose = (TextView)myDialog.findViewById(R.id.close_add);
        btnConfirm = (Button)myDialog.findViewById(R.id.btn_confirm_new_shift);

        textClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
            }
        });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }





}
