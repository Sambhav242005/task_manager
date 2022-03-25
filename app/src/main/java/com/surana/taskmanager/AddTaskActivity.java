package com.surana.taskmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.codetroopers.betterpickers.datepicker.DatePickerBuilder;
import com.codetroopers.betterpickers.datepicker.DatePickerDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AddTaskActivity extends AppCompatActivity implements DatePickerDialogFragment.DatePickerDialogHandler {

    FirebaseUser mUsers;
    DatabaseReference mRef;
    FirebaseDatabase mData;
    Button btn_layoutDay,btn_layoutWeek,mAddTaskSubmit,mSelectTime;
    Button mDaySelect;
    ListView mWeekListView;
    WeekAdapter weekAdapter;
    List<WeekItemList> weekList ;
    String select = "day";
    EditText taskEdit;
    private int hours =-1 ,min=-1;
    int year=0;
    int mouth=0;
    int day=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        mUsers = FirebaseAuth.getInstance().getCurrentUser();
        mData = FirebaseDatabase.getInstance();
        mRef = mData.getReference("task").child(mUsers.getUid());
        mSelectTime = findViewById(R.id.select_time);
        mDaySelect = findViewById(R.id.select_dateAddTask);
        mWeekListView = findViewById(R.id.list_week);
        btn_layoutDay = findViewById(R.id.addTask_day);
        btn_layoutWeek = findViewById(R.id.addTask_week);
        mAddTaskSubmit = findViewById(R.id.addTaskSubmit);
        taskEdit = findViewById(R.id.edit_task);

        weekList = new ArrayList<>();
        weekList.add(new WeekItemList("Monday"));
        weekList.add(new WeekItemList("Tuesday"));
        weekList.add(new WeekItemList("Wednesday"));
        weekList.add(new WeekItemList("Thursday"));
        weekList.add(new WeekItemList("Friday"));
        weekList.add(new WeekItemList("Saturday"));
        weekList.add(new WeekItemList("Sunday"));

        weekAdapter = new WeekAdapter(weekList,this);

        mWeekListView.setAdapter(weekAdapter);

        btn_layoutDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select = "day";
                mDaySelect.setVisibility(View.VISIBLE);
                mWeekListView.setVisibility(View.GONE);
            }
        });
        btn_layoutWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select = "week";
                mWeekListView.setVisibility(View.VISIBLE);
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
                        mSelectTime.setText(String.format("%02d", hours)+" : "+String.format("%02d", min));
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
                                mDaySelect.setText(day+" / "+mouth+" / "+year);
                            }
                        },year,mouth-1,day);
                dpb.show();
            }
        });
        mAddTaskSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hours != -1 && min != -1 &&
                        !TextUtils.isEmpty(taskEdit.getText())){
                    AddSubmit();
                }

            }
        });

        mWeekListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                String item = weekAdapter.getItem(i).getItemName();

                if (item.contains("Select")){
                    String alreadyItem = removeWords(item," Select");
                    weekAdapter.getItem(i).setItemName(alreadyItem);
                }else{
                    String check = weekAdapter.getItem(i).getItemName()+" Select";
                    weekAdapter.getItem(i).setItemName(check);
                }



                weekAdapter.notifyDataSetChanged();
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

        switch (select){
            case "day":
                day_select();
                break;
            case "week":
                week_list();
                break;
        }



    }

    private void toMainActivity(){
        startActivity(new Intent(AddTaskActivity.this,MainActivity.class));
        finish();
    }

    private void day_select() {
        String token = generateToken(15);
        if(year > 0 && mouth > 0 ) {
            Map<String,String> add = new HashMap<>();
            add.put("create",mUsers.getUid());
            add.put("day", String.valueOf(day));
            add.put("mouth", String.valueOf(mouth));
            add.put("year", String.valueOf(year));
            add.put("hours", String.format("%02d", hours));
            add.put("min", String.format("%02d", min));
            add.put("task",taskEdit.getText().toString());
            mRef.child(select).child(token).setValue(add);
            toMainActivity();
        }
    }

    private  void week_list(){
        ArrayList<String> selectWeekItem = new ArrayList<>();
        for (int i = 0;i<weekList.size();i++){
            String item = weekAdapter.getItem(i).getItemName();
            if (item.contains(" Select")){
                selectWeekItem.add(removeWords(item," Select"));
            }
        }
        if (selectWeekItem.size() >0) {
            for (int j = 0; j < selectWeekItem.size(); j++) {
                String token = generateToken(15);
                Map<String,String> add = new HashMap<>();
                add.put("create",mUsers.getUid());
                add.put("week",selectWeekItem.get(j));
                add.put("hours", String.format("%02d", hours));
                add.put("min", String.format("%02d", min));
                add.put("task",taskEdit.getText().toString());
                mRef.child(select).child(token).setValue(add);
                toMainActivity();
            }
        }
    }

    private void getCurrentDay() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormatYear = new SimpleDateFormat("yyyy");
        year = Integer.parseInt(dateFormatYear.format(cal.getTime()));
        SimpleDateFormat dateFormatMouth = new SimpleDateFormat("MM");
        mouth = Integer.parseInt(dateFormatMouth.format(cal.getTime()));
        SimpleDateFormat dateFormatDay = new SimpleDateFormat("dd");
        day = Integer.parseInt(dateFormatDay.format(cal.getTime()));
     //   mDaySelect.setText(day+" / "+mouth+" / "+year);
    }

    @Override
    public void onDialogDateSet(int reference, int yearSelect, int monthOfYear, int dayOfMonth) {
        year = yearSelect;
        mouth = monthOfYear;
        day = dayOfMonth;
    }

    public static String removeWords(String word ,String remove) {
        return word.replace(remove,"");
    }
}