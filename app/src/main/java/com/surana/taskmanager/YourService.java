package com.surana.taskmanager;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;


public class YourService extends Service {


    private static final long UPDATE_INTERVAL = 2000;
    private static final long DELAY_INTERVAL = 2000;

    DatabaseReference mRef;
    FirebaseUser mUsers;

    private Timer timer = new Timer();
    private int year;
    private int mouth;
    private int day;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mUsers = FirebaseAuth.getInstance().getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference("task").child("day");

        timer.scheduleAtFixedRate(
                new TimerTask() {
                    public void run() {


                        getCurrentDay();


                        mRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                                    getTaskNofication(dataSnapshot);

                                    removeTask(dataSnapshot);

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                },
                DELAY_INTERVAL,
                UPDATE_INTERVAL
        );


        return super.onStartCommand(intent, flags, startId);
    }

    private void removeTask(DataSnapshot dataSnapshot) {

        int daySelect = Integer.parseInt(dataSnapshot.child("day").getValue().toString());
        int mouthSelect = Integer.parseInt(dataSnapshot.child("mouth").getValue().toString());
        int yearSelect = Integer.parseInt(dataSnapshot.child("year").getValue().toString());
        int hours = Integer.parseInt(dataSnapshot.child("hours").getValue().toString());
        int min = Integer.parseInt(dataSnapshot.child("min").getValue().toString());
        String key = dataSnapshot.getKey();

        if (yearSelect == year){

            if (mouthSelect == mouth ){
                if (daySelect < day) {
                    mRef.child(key).removeValue();
                }else if (daySelect == day){
                    if (hours < Integer.parseInt(getCurrentTimeHour())){
                        mRef.child(key).removeValue();
                    }else if (hours == Integer.parseInt(getCurrentTimeHour())
                            && min < Integer.parseInt(getCurrentTimeMin())){
                        mRef.child(key).removeValue();
                    }
                }
            }else if (mouthSelect < mouth){
                mRef.child(key).removeValue();
            }

        }else if (yearSelect < year){
            mRef.child(key).removeValue();
        }
    }

    private void getTaskNofication(DataSnapshot dataSnapshot) {
        String create = dataSnapshot.child("create").getValue().toString();
        if (mUsers != null && create.equals(mUsers.getUid())){
            int daySelect = Integer.parseInt(dataSnapshot.child("day").getValue().toString());
            int mouthSelect = Integer.parseInt(dataSnapshot.child("mouth").getValue().toString());
            int yearSelect = Integer.parseInt(dataSnapshot.child("year").getValue().toString());
            String hours = dataSnapshot.child("hours").getValue().toString();
            String min = dataSnapshot.child("min").getValue().toString();
            String task = dataSnapshot.child("task").getValue().toString();

            if (day == daySelect && mouth == mouthSelect && year==yearSelect
                    && hours.equals(getCurrentTimeHour())
                    && min.equals(getCurrentTimeMin())
                    && getCurrentTimeSec() <= 10){
                showNotification("Alarm",task);
            }

        }
    }

    private void showNotification(String title, String message) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("YOUR_CHANNEL_ID",
                    "YOUR_CHANNEL_NAME",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Alarm");
            mNotificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "YOUR_CHANNEL_ID")
                .setSmallIcon(R.mipmap.ic_app_round) // notification icon
                .setContentTitle(title) // title for notification
                .setContentText(message)// message for notification
                .setAutoCancel(true); // clear notification after click
        Intent intent = new Intent(getApplicationContext(), YourService.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pi);
        mNotificationManager.notify(0, mBuilder.build());
    }
    public String getCurrentTimeHour() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("HH");
        String strDate = mdformat.format(calendar.getTime());
        return  strDate;
    }
    public String getCurrentTimeMin() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("mm");
        String strDate = mdformat.format(calendar.getTime());
        return  strDate;
    }
    public int getCurrentTimeSec() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("ss");
        String strDate = mdformat.format(calendar.getTime());
        return Integer.parseInt(strDate);
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
}
