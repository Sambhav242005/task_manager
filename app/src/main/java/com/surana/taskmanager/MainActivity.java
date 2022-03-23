package com.surana.taskmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseDatabase mData;
    DatabaseReference mRef;
    Button signOut,mMenuBack;
    ImageButton btnMenu;
    LinearLayout mMenuLayout,mTopLayout;
    LinearLayout mCalendarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        signOut = findViewById(R.id.main_signOut);
        mData = FirebaseDatabase.getInstance();
        mRef = mData.getReference();
        btnMenu = findViewById(R.id.main_btnMenu);
        mMenuLayout = findViewById(R.id.main_menu);
        mMenuBack = findViewById(R.id.main_back);
        mTopLayout = findViewById(R.id.main_top);
        mCalendarLayout = findViewById(R.id.main_calendar);

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

    }
}