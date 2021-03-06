package com.surana.taskmanager;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
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
    private static final long DELAY_INTERVAL = 100;

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
        mRef = FirebaseDatabase.getInstance().getReference("task").child(mUsers.getUid());

        timer.scheduleAtFixedRate(
                new TimerTask() {
                    public void run() {


                        getCurrentDay();
                        mRef.child("day").addValueEventListener(new ValueEventListener() {
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
                        mRef.child("week").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                getWeekTask();
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

    private void getWeekTask() {
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
                    if (dataSnapshot.child("create").getValue().toString().equals(mUsers.getUid())
                            && week.equals(finalTodayWeekend)){
                        String hours = dataSnapshot.child("hours").getValue().toString();
                        String min = dataSnapshot.child("min").getValue().toString();
                        String task = dataSnapshot.child("task").getValue().toString();
                        if (hours.equals(getCurrentTimeHour())
                                && min.equals(getCurrentTimeMin())
                                && getCurrentTimeSec() <= 20) {
                            showNotification("Alarm",task);
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
                    mRef.child("day").child(key).removeValue();
                }else if (daySelect == day){
                    if (hours < Integer.parseInt(getCurrentTimeHour())){
                        mRef.child("day").child(key).removeValue();
                    }else if (hours == Integer.parseInt(getCurrentTimeHour())
                            && min < Integer.parseInt(getCurrentTimeMin())){
                        mRef.child("day").child(key).removeValue();
                    }
                }
            }else if (mouthSelect < mouth){
                mRef.child("day").child(key).removeValue();
            }

        }else if (yearSelect < year){
            mRef.child("day").child(key).removeValue();
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
                    && getCurrentTimeSec() <= 20){
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
            channel.enableLights(true);
            channel.setLightColor(Color.GRAY);
            mNotificationManager.createNotificationChannel(channel);
        }
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "YOUR_CHANNEL_ID")
                .setSmallIcon(R.mipmap.ic_app_round) // notification icon
                .setContentTitle(title) // title for notification
                .setContentText(message)// message for notification
                .setAutoCancel(true)
                .setSound(uri)
                .setVibrate(new long[]{100, 500, 1000})
                .setDefaults(Notification.DEFAULT_LIGHTS ); // clear notification after click
        Intent intent = new Intent(getApplicationContext(), YourService.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setLights(0xFFb71c1c, 1000, 2000);
      //  mBuilder.setSound(yourSoundUri);
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
