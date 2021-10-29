package com.example.sit2long;


import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class Notification extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Toast.makeText(this,"Hello",Toast.LENGTH_LONG).show();
//        CountDownTimer t = new CountDownTimer(60000, 10000) {
//            @Override
//            public void onTick(long l) {
//
//                Log.e("tags", "Some staffs");
//
//            }
//
//            @Override
//            public void onFinish() {
//                Log.e("tags", "Some staffs finishes");
//            }
//        }.start();


//            while(true){

//                Thread.sleep(1000);
              for(int i=1; i<4;i++){
                  Intent snoozeIntent = new Intent(this, MainActivity.class);
                  PendingIntent pInt = PendingIntent.getActivity(this,0,snoozeIntent,0);
                  NotificationCompat.Builder builder = new NotificationCompat.Builder(Notification.this,"Notification");
                builder.setContentTitle("My Title");
                builder.setContentTitle("We are very happy to see u here!");
                builder.setSmallIcon(R.drawable.ic_launcher_background);
                builder.addAction(R.drawable. ic_launcher_foreground , "OK" , pInt);
                builder.setAutoCancel(true);

                NotificationManagerCompat manager = NotificationManagerCompat.from(Notification.this);
                manager.notify(1,builder.build());
            }



        return START_STICKY;
    }
}
