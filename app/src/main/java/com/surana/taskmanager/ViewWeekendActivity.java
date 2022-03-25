package com.surana.taskmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewWeekendActivity extends AppCompatActivity {

    DatabaseReference mRef;
    FirebaseUser mUser;
    ListView listView;
    ArrayList<String> arrayListKey;
    ArrayAdapter<String> adapter;
    Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_weekend);

        arrayListKey = new ArrayList<>();

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference("task").child(mUser.getUid()).child("week");
        btnBack = findViewById(R.id.viewWeekendBack);
        listView = findViewById(R.id.viewWeekendListView);

        addWeekTaskList();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mRef.child(arrayListKey.get(i)).removeValue();
                arrayListKey.remove(i);
                addWeekTaskList();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ViewWeekendActivity.this,MainActivity.class));
                finish();
            }
        });

    }

    private void addWeekTaskList() {
        ArrayList<String> arrayListWeek = new ArrayList<>();
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String time = dataSnapshot.child("hours").getValue().toString() + ":" +
                            dataSnapshot.child("min").getValue().toString();
                    String task = dataSnapshot.child("task").getValue().toString();
                    String week = dataSnapshot.child("week").getValue().toString();

                    arrayListWeek.add(week+" :-  "+time+" - "+task);
                    arrayListKey.add(dataSnapshot.getKey());
                    adapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adapter = new ArrayAdapter<String>(this,R.layout.week_list_layout,arrayListWeek);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}