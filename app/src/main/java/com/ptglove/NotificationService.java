package com.ptglove;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import androidx.core.app.NotificationCompat;

public class NotificationService extends Service {
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final static String default_notification_channel_id = "default";
    Timer timer;
    TimerTask timerTask;
    String TAG = "Timers";
    int Your_X_SECS = 5;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d("timer: ", "started");
        startTimer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("timer: ", "stopped");
        stopTimerTask();
        super.onDestroy();
    }

    //we are going to use a handler to be able to run in our TimerTask
    final Handler handler = new Handler();

    public void startTimer() {
        timer = new Timer();
        initializeTimerTask();
        Calendar.getInstance().get(Calendar.YEAR);
        Calendar date = Calendar.getInstance();
        if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 9) {
            date.set(Calendar.HOUR_OF_DAY, 9);
        } else if (date.get(Calendar.HOUR_OF_DAY) >= 19) {
            date.set(Calendar.HOUR_OF_DAY, 9);
            date.set(Calendar.DATE, date.get(Calendar.DATE) + 1);
        } else {
            date.set(Calendar.HOUR_OF_DAY, date.get(Calendar.HOUR_OF_DAY) + 5);
        }
        Log.d("timer: ", "running");
        //timer.schedule(timerTask, 5000, Your_X_SECS * 1000); //
        timer.schedule(timerTask, date.getTime());
    }

    public void stopTimerTask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        createNotification();
                    }
                });
            }
        };
    }

    private void createNotification() {
        Log.d("timer: ", "ran");
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), default_notification_channel_id);
        mBuilder.setContentTitle("Physical Therapy Glove");
        mBuilder.setContentText("Notification Listener Service Example");
        mBuilder.setTicker("Notification Listener Service Example");
        mBuilder.setSmallIcon(R.mipmap.michigan);
        mBuilder.setAutoCancel(true);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
    }
}