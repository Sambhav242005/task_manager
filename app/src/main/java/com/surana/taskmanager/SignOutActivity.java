package com.surana.taskmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignOutActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_out);

        mAuth = FirebaseAuth.getInstance();

        mAuth.signOut();
        mUser = mAuth.getCurrentUser();

        if(mUser == null){
            startActivity(new Intent(SignOutActivity.this,StartActivity.class));
            finish();
        }else{
            Log.d("sambhav_app","error");
        }

    }
}