package com.surana.taskmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.codetroopers.betterpickers.datepicker.DatePickerBuilder;
import com.codetroopers.betterpickers.datepicker.DatePickerDialogFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class AddTaskActivity extends AppCompatActivity implements DatePickerDialogFragment.DatePickerDialogHandler {

    DatabaseReference mRef;
    FirebaseDatabase mData;
    Button btn_layoutDay,btn_layoutWeek,btn_layoutYearly,mSelectYearly,mAddTaskSubmit,mSelectTime;
    Button mDaySelect;
    ListView mWeekListView;
    ArrayAdapter<String> weekAdapter;
    ArrayList<String> weekList ;
    String select = "day";
    EditText taskEdit;
    private int hours =-1 ,min=-1;
    int year=2022;
    int mouth=03;
    int day=20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        mData = FirebaseDatabase.getInstance();
        mRef = mData.getReference("day");
        mSelectTime = findViewById(R.id.select_time);
        mDaySelect = findViewById(R.id.select_dateAddTask);
        mWeekListView = findViewById(R.id.list_week);
        btn_layoutDay = findViewById(R.id.addTask_day);
        btn_layoutWeek = findViewById(R.id.addTask_week);
        btn_layoutYearly = findViewById(R.id.addTask_yearly);
        mSelectYearly = findViewById(R.id.select_yearly);
        mAddTaskSubmit = findViewById(R.id.addTaskSubmit);
        taskEdit = findViewById(R.id.edit_task);

        weekList = new ArrayList<>();
        weekList.add("Monday");
        weekList.add("Tuesday");
        weekList.add("Wednesday");
        weekList.add("Thursday");
        weekList.add("Friday");
        weekList.add("Saturday");
        weekList.add("Sunday");

        weekAdapter = new ArrayAdapter<>(this,R.layout.week_list_layout,
                weekList);

        mWeekListView.setAdapter(weekAdapter);

        btn_layoutDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select = "day";
                mDaySelect.setVisibility(View.VISIBLE);
                mWeekListView.setVisibility(View.GONE);
                mSelectYearly.setVisibility(View.GONE);
            }
        });
        btn_layoutWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select = "week";
                mWeekListView.setVisibility(View.VISIBLE);
                mDaySelect.setVisibility(View.GONE);
                mSelectYearly.setVisibility(View.GONE);
            }
        });
        btn_layoutYearly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select = "yearly";
                mSelectYearly.setVisibility(View.VISIBLE);
                mWeekListView.setVisibility(View.GONE);
                mDaySelect.setVisibility(View.GONE);
            }
        });
        mSelectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TimePickerDialog.OnTimeSetListener timeSetListener  = new TimePickerDialog.OnTimeSetListener(){
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectHour, int selectMin) {
                        hours = selectHour;
                        min = selectMin;
                        mSelectTime.setText(hours+" : "+min);
                    }
                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddTaskActivity.this,
                        timeSetListener, hours, min, true);
                timePickerDialog.show();

            }
        });
        getCurrentDay();
        mDaySelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog dpb = new DatePickerDialog(AddTaskActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                year =i;
                                mouth = i1+1;
                                day = i2;
                            }
                        },year,mouth-1,day);
                dpb.show();
            }
        });

        mDaySelect.setText(day+" / "+mouth+" / "+year);

        mAddTaskSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hours != -1 && min != -1 &&
                        !TextUtils.isEmpty(taskEdit.getText()) &&
                        !TextUtils.isEmpty(select)){
                    AddSubmit();
                }else {

                }

            }
        });

    }
    public static String generateToken(int len) {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        return sb.toString();
    }

    private void AddSubmit() {
        String token = generateToken(15);
    }

    private void getCurrentDay() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormatYear = new SimpleDateFormat("yyyy");
        year = Integer.parseInt(dateFormatYear.format(cal.getTime()));
        SimpleDateFormat dateFormatMouth = new SimpleDateFormat("MM");
        mouth = Integer.parseInt(dateFormatMouth.format(cal.getTime()));
        SimpleDateFormat dateFormatDay = new SimpleDateFormat("dd");
        day = Integer.parseInt(dateFormatDay.format(cal.getTime()));

    }

    @Override
    public void onDialogDateSet(int reference, int yearSelect, int monthOfYear, int dayOfMonth) {
        year = yearSelect;
        mouth = monthOfYear;
        day = dayOfMonth;
    }
}