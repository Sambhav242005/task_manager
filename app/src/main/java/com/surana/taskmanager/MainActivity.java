package com.surana.taskmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseDatabase mData;
    DatabaseReference mRef;
    Button signOut,mMenuBack,mAddTask;
    ImageButton btnMenu;
    LinearLayout mMenuLayout,mTopLayout;
    LinearLayout mCalendarLayout;
    RecyclerView itemTaskRecycle;
    ArrayList<ItemListTask> taskArrayList;

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
                mCalendarLayout.setVisibility(View.GONE);
            }
        });

        mMenuBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTopLayout.setVisibility(View.VISIBLE);
                mMenuLayout.setVisibility(View.GONE);
                mCalendarLayout.setVisibility(View.VISIBLE);
            }
        });

        mAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,AddTaskActivity.class));
            }
        });

        listAddTask();
        initRecycle();
    }

    private void initRecycle() {
        RecycleListAdapter adapter = new RecycleListAdapter(taskArrayList,MainActivity.this);
        itemTaskRecycle.setAdapter(adapter);
        itemTaskRecycle.setLayoutManager(new LinearLayoutManager(this));
        adapter.notifyDataSetChanged();
    }

    private void listAddTask() {
        mRef.child("day").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String hours = dataSnapshot.child("hours").getValue().toString();
                    String min = dataSnapshot.child("min").getValue().toString();
                    String day = dataSnapshot.child("day").getValue().toString();
                    String mouth = dataSnapshot.child("mouth").getValue().toString();
                    String year = dataSnapshot.child("year").getValue().toString();
                    String task = dataSnapshot.child("task").getValue().toString();
                    taskArrayList.add(new ItemListTask(hours+":"+min+" : ",task,day+"/"+mouth+"/"+year));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}