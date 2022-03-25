package com.surana.taskmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseDatabase mData;
    DatabaseReference mRef;
    Button signOut,mMenuBack,mAddTask,mViewWeek;
    ImageButton btnMenu;
    LinearLayout mMenuLayout,mTopLayout;
    RecyclerView itemTaskRecycle;
    ArrayList<ItemListTask> taskArrayList;
    int yearSelect,mouthSelect,daySelect;
    private Handler handler = new Handler();
    TextView currentTime;
    EditText search_edit;
    ListView search_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        search_list = findViewById(R.id.search_mainEditText);
        search_edit = findViewById(R.id.main_searchTask);
        mViewWeek = findViewById(R.id.main_viewWeekend);
        currentTime = findViewById(R.id.currentTime_main);
        taskArrayList = new ArrayList<>();
        itemTaskRecycle = findViewById(R.id.listTask);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        signOut = findViewById(R.id.main_signOut);
        mData = FirebaseDatabase.getInstance();
        mRef = mData.getReference("task").child(mUser.getUid());
        mAddTask = findViewById(R.id.main_addTask);
        btnMenu = findViewById(R.id.main_btnMenu);
        mMenuLayout = findViewById(R.id.main_menu);
        mMenuBack = findViewById(R.id.main_back);
        mTopLayout = findViewById(R.id.main_top);

        search_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                itemTaskRecycle.setVisibility(View.GONE);
                search_list.setVisibility(View.VISIBLE);
                filter(editable.toString());
            }
        });

        if(mUser == null){
            handler.removeCallbacks(runnable);
            startActivity(new Intent(MainActivity.this,StartActivity.class));
            finish();
        }
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeCallbacks(runnable);
                startActivity(new Intent(MainActivity.this,SignOutActivity.class));
            }
        });

        mViewWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,ViewWeekendActivity.class));
            }
        });

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMenuLayout.setVisibility(View.VISIBLE);
                mTopLayout.setVisibility(View.GONE);
                itemTaskRecycle.setVisibility(View.GONE);
                currentTime.setVisibility(View.GONE);
                search_list.setVisibility(View.GONE);
            }
        });

        mMenuBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemTaskRecycle.setVisibility(View.VISIBLE);
                mTopLayout.setVisibility(View.VISIBLE);
                mMenuLayout.setVisibility(View.GONE);
                currentTime.setVisibility(View.VISIBLE);
            }
        });



        mAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeCallbacks(runnable);
                startActivity(new Intent(MainActivity.this,AddTaskActivity.class));
            }
        });
        getCurrentDay();
        getTaskDetail();
        handler.post(runnable);
    }

    private void filter(String s) {
        ArrayList<String> stringArrayList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.week_list_layout,
                stringArrayList);

        for (ItemListTask itemListTask : taskArrayList){
            if (itemListTask.getTask().toLowerCase().contains(s.toLowerCase())){
                stringArrayList.add(itemListTask.getDate()+" :- "+
                        itemListTask.getTime()+" - "+itemListTask.getTask());
            }
            adapter.notifyDataSetChanged();
        }
        search_list.setAdapter(adapter);

    }


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            if (TextUtils.isEmpty(search_edit.getText())){
                search_list.setVisibility(View.GONE);
                itemTaskRecycle.setVisibility(View.VISIBLE);
            }

            getTaskDetail();
            currentTime.setText("Current Time :-  "+String.format("%02d", getCurrentTimeHour())+":"+String.format("%02d", getCurrentTimeMin()));
            // Repeat every 2 seconds
            handler.postDelayed(runnable, 2000);
        }
    };

    private void getTaskDetail() {
        RecycleListAdapter adapter = new RecycleListAdapter(taskArrayList,MainActivity.this);
        itemTaskRecycle.setAdapter(adapter);
        itemTaskRecycle.setLayoutManager(new LinearLayoutManager(this));
        mRef.child("day").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                taskArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String hours = dataSnapshot.child("hours").getValue().toString();
                    String min = dataSnapshot.child("min").getValue().toString();
                    String day = dataSnapshot.child("day").getValue().toString();
                    String mouth = dataSnapshot.child("mouth").getValue().toString();
                    String year = dataSnapshot.child("year").getValue().toString();
                    String task = dataSnapshot.child("task").getValue().toString();

                    if (dataSnapshot.child("create").getValue().toString().equals(mUser.getUid())){
                        if (year.equals(String.valueOf(yearSelect))
                                && mouth.equals(String.valueOf(mouthSelect))) {
                                taskArrayList.add(new ItemListTask(hours + ":" + min + " : ", task,
                                        day + "/" + mouth + "/" + year));
                                Collections.sort(taskArrayList ,ItemListTask.Sort);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Calendar calendar = Calendar.getInstance();
        int dayWeek = calendar.get(Calendar.DAY_OF_WEEK);
        String todayWeekend = " ";

        switch (dayWeek) {
            case Calendar.SUNDAY:
                todayWeekend = "Sunday";
                break;
            case Calendar.MONDAY:
                todayWeekend = "Monday";
                break;
            case Calendar.TUESDAY:
                todayWeekend = "Tuesday";
                break;
            case Calendar.WEDNESDAY:
                todayWeekend = "Wednesday";
                break;
            case Calendar.THURSDAY:
                todayWeekend = "Thursday";
                break;
            case Calendar.FRIDAY:
                todayWeekend = "Friday";
                break;
            case Calendar.SATURDAY:
                todayWeekend = "Saturday";
                break;
        }


        String finalTodayWeekend = todayWeekend;
        mRef.child("week").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String week = dataSnapshot.child("week").getValue().toString();
                    if (dataSnapshot.child("create").getValue().toString().equals(mUser.getUid())
                            && week.equals(finalTodayWeekend)){
                        String hours = dataSnapshot.child("hours").getValue().toString();
                        String min = dataSnapshot.child("min").getValue().toString();
                        String task = dataSnapshot.child("task").getValue().toString();
                        if (getCurrentTimeHour() == Integer.parseInt(hours)
                                && getCurrentTimeMin() <= Integer.parseInt(min)){
                            taskArrayList.add(new ItemListTask(hours + ":" + min + " : ", task,
                                    daySelect + "/" + mouthSelect + "/" + yearSelect));
                            Collections.sort(taskArrayList ,ItemListTask.Sort);
                        }else if (getCurrentTimeHour() < Integer.parseInt(hours)){
                            taskArrayList.add(new ItemListTask(hours + ":" + min + " : ", task,
                                    daySelect + "/" + mouthSelect + "/" + yearSelect));
                            Collections.sort(taskArrayList ,ItemListTask.Sort);
                        }
                    }
                    adapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    public int getCurrentTimeHour() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("HH");
        String strDate = mdformat.format(calendar.getTime());
        return  Integer.parseInt(strDate);
    }
    public int getCurrentTimeMin() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("mm");
        String strDate = mdformat.format(calendar.getTime());
        return  Integer.parseInt(strDate);
    }


    private void getCurrentDay() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormatYear = new SimpleDateFormat("yyyy");
        yearSelect = Integer.parseInt(dateFormatYear.format(cal.getTime()));
        SimpleDateFormat dateFormatMouth = new SimpleDateFormat("MM");
        mouthSelect = Integer.parseInt(dateFormatMouth.format(cal.getTime()));
        SimpleDateFormat dateFormatDay = new SimpleDateFormat("dd");
        daySelect = Integer.parseInt(dateFormatDay.format(cal.getTime()));
        //   mDaySelect.setText(day+" / "+mouth+" / "+year);
    }

}