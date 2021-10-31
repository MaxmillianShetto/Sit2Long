package com.example.sit2long;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Chronometer;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;

public class ActivityTimer
{
    private int totalTimeInSeconds;
    private int activityHours;
    private int activityMinutes;
    private int activitySeconds;

    private TextView activityStatusTextView;

    private Chronometer activityChronometer;
    
    private Context context;

    public ActivityTimer()
    {
        totalTimeInSeconds = 0;
        activityHours = 0;
        activityMinutes = 0;
        activitySeconds = 0;
    }

    public ActivityTimer(Chronometer chronometer, TextView statusView, Context activityContext)
    {
        totalTimeInSeconds = 0;
        activityHours = 0;
        activityMinutes = 0;
        activitySeconds = 0;
        activityChronometer = chronometer;
        activityStatusTextView = statusView;
        context = activityContext;
    }

    public String getTotalTimeInSeconds()
    {
        return convertTimeSetToString(totalTimeInSeconds);
    }

    public int getActivitySeconds()
    {
        return activitySeconds;
    }

    public void setActivitySeconds(int activitySeconds)
    {
        this.activitySeconds = activitySeconds;
    }

    public int getActivityMinutes()
    {
        return activityMinutes;
    }

    public void setActivityMinutes(int activityMinutes)
    {
        this.activityMinutes = activityMinutes;
    }

    public int getActivityHours()
    {
        return activityHours;
    }

    public void setActivityHours(int activityHours)
    {
        this.activityHours = activityHours;
    }

    public void setActivityTime(int hours, int minutes, int seconds)
    {
        totalTimeInSeconds = hours * 3600 + minutes * 60 + seconds;
    }

    public void setActivityChronometer(Chronometer chronometer)
    {
        this.activityChronometer = chronometer;
    }

    public String convertTimeSetToString(int timeInSeconds)
    {
        int hours   = (int) (timeInSeconds /3600);
        int minutes = (int) (timeInSeconds - hours*3600)/60;
        int seconds = (int) (timeInSeconds - hours*3600 - minutes*60);

        return timeUnitToString(hours) + ":" + timeUnitToString(minutes) + ":" + timeUnitToString(seconds);
    }

    public String timeUnitToString(int timeUnit)
    {
        if (timeUnit < 10)
        {
            return "0" + timeUnit;
        }
        else
        {
            return "" + timeUnit;
        }
    }


    public void startTimer(TextView textView)
    {
        activityChronometer.setBase(SystemClock.elapsedRealtime());
        activityChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener()
        {
            boolean notifiedUser = false;

            @Override
            public void onChronometerTick(Chronometer chronometer)
            {

                int secondsElapsed = (int) ((SystemClock.elapsedRealtime() - chronometer.getBase()) / 1000);

                if (secondsElapsed >= totalTimeInSeconds && !notifiedUser)
                {
                    notifiedUser = true;
                    Log.i("TimerNotification", "You have been siting for too long");
                    notifyUser(textView);
                    sendNotification("too long", context);
                }

                Log.i("Seconds", convertTimeSetToString(secondsElapsed));
                chronometer.setText(convertTimeSetToString(secondsElapsed));
            }
        });
        Log.i("Timer", "timer started");
        activityChronometer.start();
    }

    private void notifyUser(TextView textView)
    {
        String message = "You have been sitting for too long";
        updateActivityStatusTextView(textView, message);
    }

    private void updateActivityStatusTextView(TextView view, String message)
    {
        view.setText(message);
    }

    private void sendNotification(String message, Context mainActivityContext)
    {
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(mainActivityContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(mainActivityContext, 0, intent, 0);

        String CHANNEL_ID = "notification_channel";
        String CHANNEL_NAME = "Notification Channel";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mainActivityContext, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.sym_action_chat)
                .setContentTitle("My notification")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);


        NotificationManager notificationManager = (NotificationManager) mainActivityContext.getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel notifChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            notificationManager.createNotificationChannel(notifChannel);
        }

        notificationManager.notify(1, builder.build());

        Log.i("Timer", "Notification sent");
    }

    public void stopTimer()
    {
        activityChronometer.stop();
        Log.i("Timer", "timer stopped");
        activityStatusTextView.setText("Moved");
    }
}
