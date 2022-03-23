package com.surana.taskmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RegistrationActivity extends AppCompatActivity {

    EditText mUsername,mEmail,mPassword;
    CheckBox mShowPassword;
    Button mRegistration;
    FirebaseUser mUsers;
    FirebaseAuth mAuth;
    FirebaseDatabase mData;
    DatabaseReference mRef;
    TextView mError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mData = FirebaseDatabase.getInstance();
        mRef = mData.getReference("users");
        mAuth = FirebaseAuth.getInstance();
        mUsers = mAuth.getCurrentUser();
        mUsername = findViewById(R.id.registration_username);
        mEmail = findViewById(R.id.registration_email);
        mPassword = findViewById(R.id.registration_password);
        mShowPassword = findViewById(R.id.registration_showPassword);
        mRegistration = findViewById(R.id.registration);
        mError = findViewById(R.id.error_registration);
        mPassword.setInputType(129);

        if (mUsers != null){
            Intent intent = new Intent(RegistrationActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

        mShowPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int check = mPassword.getInputType();
                if (check == 1){
                    mPassword.setInputType(129);
                }else{
                    mPassword.setInputType(1);
                }
            }
        });

        if (mUsers != null){
            Intent intent = new Intent(RegistrationActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

        mRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registration();
            }
        });

    }

    private void registration() {
        if (!TextUtils.isEmpty(mUsername.getText())
                && !TextUtils.isEmpty(mEmail.getText())
                && !TextUtils.isEmpty(mPassword.getText()) ){

            String username = mUsername.getText().toString();
            String email = mEmail.getText().toString();
            String password = mPassword.getText().toString();
            String token = generateToken(10);


            mAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {

                    mUsers = authResult.getUser();
                    Map<String,String> hashMap = new HashMap();

                    hashMap.put("username",username);
                    hashMap.put("email",email);
                    hashMap.put("img_url","url");
                    hashMap.put("token",token);

                    mRef.child(token).setValue(hashMap);
                    Intent intent = new Intent(RegistrationActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                      mError.setText(e.getMessage());

                }
            });
        }else{

        }
    }
    public static String generateToken(int len) {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijk"
                +"lmnopqrstuvwxyz!@#$%&";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        return sb.toString();
    }

}