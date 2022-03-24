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
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseDatabase mData;
    DatabaseReference mRef;
    Button signOut,mMenuBack,mAddTask;
    ImageButton btnMenu;
    LinearLayout mMenuLayout,mTopLayout;
    RecyclerView itemTaskRecycle;
    ArrayList<ItemListTask> taskArrayList;
    int yearSelect,mouthSelect,daySelect,currentHour,currentMin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskArrayList = new ArrayList<>();
        itemTaskRecycle = findViewById(R.id.listTask);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        signOut = findViewById(R.id.main_signOut);
        mData = FirebaseDatabase.getInstance();
        mRef = mData.getReference("task");
        mAddTask = findViewById(R.id.main_addTask);
        btnMenu = findViewById(R.id.main_btnMenu);
        mMenuLayout = findViewById(R.id.main_menu);
        mMenuBack = findViewById(R.id.main_back);
        mTopLayout = findViewById(R.id.main_top);

        if(mUser == null){
            startActivity(new Intent(MainActivity.this,StartActivity.class));
            finish();
        }
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(MainActivity.this,SignOutActivity.class));
            }
        });

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMenuLayout.setVisibility(View.VISIBLE);
                mTopLayout.setVisibility(View.GONE);
                itemTaskRecycle.setVisibility(View.GONE);
            }
        });

        mMenuBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemTaskRecycle.setVisibility(View.VISIBLE);
                mTopLayout.setVisibility(View.VISIBLE);
                mMenuLayout.setVisibility(View.GONE);
            }
        });

        mAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,AddTaskActivity.class));
            }
        });
        getCurrentDay();
        RecycleListAdapter adapter = new RecycleListAdapter(taskArrayList,MainActivity.this);
        itemTaskRecycle.setAdapter(adapter);
        itemTaskRecycle.setLayoutManager(new LinearLayoutManager(this));

        mRef.child("day").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                taskArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if (dataSnapshot.child("create").getValue().toString().equals(mUser.getUid())){
                        String hours = dataSnapshot.child("hours").getValue().toString();
                        String min = dataSnapshot.child("min").getValue().toString();
                        String day = dataSnapshot.child("day").getValue().toString();
                        String mouth = dataSnapshot.child("mouth").getValue().toString();
                        String year = dataSnapshot.child("year").getValue().toString();
                        String task = dataSnapshot.child("task").getValue().toString();
                       if (year.equals(String.valueOf(yearSelect))
                               && mouth.equals(String.valueOf(mouthSelect))
                               && day.equals(String.valueOf(daySelect))){
                           taskArrayList.add(new ItemListTask(hours+":"+min+" : ",task,
                                   day+"/"+mouth+"/"+year));
                           adapter.notifyDataSetChanged();
                       }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adapter.notifyDataSetChanged();

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