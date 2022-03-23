package com.surana.taskmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

public class AddTaskActivity extends AppCompatActivity {

    LinearLayout mWeekLayout;
    ListView mWeekListView;
    ArrayAdapter<String> weekAdapter;
    ArrayList<String> weekList ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        mWeekLayout = findViewById(R.id.weekend_layout);
        mWeekListView = findViewById(R.id.list_week);

        weekList = new ArrayList<>();
        weekList.add("Monday");
        weekList.add("Tuesday");
        weekList.add("Wednesday");
        weekList.add("Thursday");
        weekList.add("Friday");
        weekList.add("Saturday");
        weekList.add("Sunday");

        weekAdapter = new ArrayAdapter<>(this,R.layout.week_list_layout,
                R.id.itemWeekList,
                weekList);

        mWeekListView.setAdapter(weekAdapter);
    }
}