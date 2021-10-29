package com.example.sit2long;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {
    private int tooLong;
    private int timeLapse;
    public GoogleApiClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        client = new GoogleApiClient.Builder(MainActivity.this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(MainActivity.this)
                .addOnConnectionFailedListener(MainActivity.this).build();
        client.connect();

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("Notification","Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }
        Button btn = (Button) findViewById(R.id.button);
    btn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            sentNotification("Hello", "Hello to u friends!");
        }
    });
//        startService(new Intent(this, Notification.class));
    }

    private void  sentNotification( String title, String message){
        Intent snoozeIntent = new Intent(this, MainActivity.class);
        PendingIntent pInt = PendingIntent.getActivity(this,0,snoozeIntent,0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this,"Notification");
        builder.setContentTitle(title);
        builder.setContentTitle(message);
        builder.setSmallIcon(R.drawable.ic_launcher_background);
        builder.addAction(R.drawable. ic_launcher_foreground , "OK" , pInt);
        builder.setAutoCancel(true);

        NotificationManagerCompat manager = NotificationManagerCompat.from(MainActivity.this);
        manager.notify(1,builder.build());
    }

    @Override
    public void onConnected(Bundle bundle) {
        Intent intent = new Intent(MainActivity.this, MovementTracker.class);
        PendingIntent pIntent = PendingIntent.getService(MainActivity.this,0,intent, PendingIntent.FLAG_UPDATE_CURRENT);
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(client, 0,pIntent);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
