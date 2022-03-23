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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    FirebaseUser mUser;
    FirebaseAuth mAuth;
    EditText mEmail,mPassword;
    CheckBox mShow_password;
    Button mLogin;
    TextView mError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mEmail = findViewById(R.id.login_email);
        mPassword = findViewById(R.id.login_password);
        mShow_password = findViewById(R.id.login_showPassword);
        mLogin = findViewById(R.id.login);
        mPassword.setInputType(129);
        mError = findViewById(R.id.error_login);

        if (mUser !=null){
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

        mShow_password.setOnClickListener(new View.OnClickListener() {
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

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

    }

    private void login() {
        if (!TextUtils.isEmpty(mEmail.getText()) && !TextUtils.isEmpty(mPassword.getText())){

            String email =mEmail.getText().toString();
            String password =mPassword.getText().toString();

            mAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    mUser = authResult.getUser();
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
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
}