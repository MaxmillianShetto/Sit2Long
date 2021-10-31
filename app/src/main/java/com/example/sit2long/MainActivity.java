package com.example.sit2long;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.internal.safeparcel.SafeParcelableSerializer;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Demos enabling/disabling Activity Recognition transitions, e.g., starting or stopping a walk,
 * run, drive, etc.).
 */
public class MainActivity extends AppCompatActivity
{

    private final static String TAG = "MainActivity";

    // TODO: Review check for devices with Android 10 (29+).
    private boolean runningQOrLater =
            android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q;

    private boolean activityTrackingEnabled;
    private boolean timerStarted;

    private Button timerButton;
    private Button toggleOnOffButton;
    private Button changeActivityButton;

    private TextView activityTextView;

    private List<ActivityTransition> activityTransitionList;

    // Action fired when transitions are triggered.
    private final String TRANSITIONS_RECEIVER_ACTION =
            BuildConfig.APPLICATION_ID + "TRANSITIONS_RECEIVER_ACTION";

    private PendingIntent activityTransitionsPendingIntent;
    private TransitionsReceiver transitionsReceiver;

    private ActivityTimer activityTimer;
    private Chronometer chronometer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activityTextView = findViewById(R.id.activityStatusTextView);

        chronometer = (Chronometer) findViewById(R.id.chronometer);

        activityTimer = new ActivityTimer(chronometer, activityTextView, this);

        timerButton = findViewById(R.id.timerButton);
        toggleOnOffButton = findViewById(R.id.toggleOnOffButton);
        changeActivityButton = findViewById(R.id.activityChangeButton);

        activityTrackingEnabled = false;
        timerStarted = false;

        // List of activity transitions to track.
        activityTransitionList = new ArrayList<>();

        // TODO: Add activity transitions to track.
        activityTransitionList.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());
        activityTransitionList.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());

        // TODO: Initialize PendingIntent that will be triggered when a activity transition occurs.
        Intent intent = new Intent(TRANSITIONS_RECEIVER_ACTION);
        activityTransitionsPendingIntent =
                PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);

        // TODO: Create a BroadcastReceiver to listen for activity transitions.
        // The receiver listens for the PendingIntent above that is triggered by the system when an
        // activity transition occurs.
        transitionsReceiver = new TransitionsReceiver();

    }

    @Override
    protected void onStart()
    {
        super.onStart();

        // TODO: Register the BroadcastReceiver to listen for activity transitions.
        registerReceiver(transitionsReceiver, new IntentFilter(TRANSITIONS_RECEIVER_ACTION));
    }

    @Override
    protected void onPause()
    {

        // TODO: Disable activity transitions when user leaves the app.
//        if (activityTrackingEnabled)
//        {
//            disableActivityTransitions();
//        }
        super.onPause();
    }


    @Override
    protected void onStop()
    {

        // TODO: Unregister activity transition receiver when user leaves the app.

        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        unregisterReceiver(transitionsReceiver);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        // Start activity recognition if the permission was approved.
        if (isActivityRecognitionApproved() && !activityTrackingEnabled)
        {
            enableActivityTransitions();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Registers callbacks for {@link ActivityTransition} events via a custom
     * {@link BroadcastReceiver}
     */
    private void enableActivityTransitions()
    {

        Log.d(TAG, "enableActivityTransitions()");


        // Register for Transitions Updates.
        Task<Void> task = ActivityRecognition.getClient(this)
        .requestActivityUpdates(5000, activityTransitionsPendingIntent);

        task.addOnSuccessListener(
                new OnSuccessListener<Void>()
                {
                    @Override
                    public void onSuccess(Void result)
                    {
                        activityTrackingEnabled = true;
                        printToScreen("Transitions Api was successfully registered.");

                    }
                });

        task.addOnFailureListener(
                new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        printToScreen("Transitions Api could NOT be registered: " + e);
                        Log.e(TAG, "Transitions Api could NOT be registered: " + e);

                    }
                });
    }


    /**
     * Unregisters callbacks for {@link ActivityTransition} events via a custom
     * {@link BroadcastReceiver}
     */
    private void disableActivityTransitions()
    {

        Log.d(TAG, "disableActivityTransitions()");


        // TODO: Stop listening for activity changes.
        ActivityRecognition.getClient(this).removeActivityTransitionUpdates(activityTransitionsPendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>()
                {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        activityTrackingEnabled = false;
                        printToScreen("Transitions successfully unregistered.");
                    }
                })
                .addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        printToScreen("Transitions could not be unregistered: " + e);
                        Log.e(TAG, "Transitions could not be unregistered: " + e);
                    }
                });
    }

    /**
     * On devices Android 10 and beyond (29+), you need to ask for the ACTIVITY_RECOGNITION via the
     * run-time permissions.
     */
    private boolean isActivityRecognitionApproved()
    {

        // TODO: Review permission check for 29+.
        if (runningQOrLater)
        {

            return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACTIVITY_RECOGNITION
            );
        }
        else
        {
            return true;
        }
    }

    public void toggleActivityRecognitionOnOff(View view)
    {

        // TODO: Enable/Disable activity tracking and ask for permissions if needed.
        if (isActivityRecognitionApproved())
        {
            if (activityTrackingEnabled)
            {
                disableActivityTransitions();
                if (timerStarted)
                {
                    activityTimer.stopTimer();
                    timerStarted = false;
                }

            }
            else
            {
                enableActivityTransitions();
                // TODO: replace with string from input fields
                activityTimer.setActivityTime(0, 0, 10);
            }

        }
        else
        {
            // Request permission and start activity for result. If the permission is approved, we
            // want to make sure we start activity recognition tracking.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 1);
            if (isActivityRecognitionApproved())
            {
                enableActivityTransitions();
            }
            else
            {
                Toast toast = Toast.makeText(getApplicationContext(), "Grant permission in application settings before proceeding", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    boolean isStill = true;

    public void changeActivityState(View view)
    {
        Intent intent = new Intent();
        intent.setAction(TRANSITIONS_RECEIVER_ACTION);
        List<ActivityTransitionEvent> events = new ArrayList<>();
        ActivityTransitionEvent transitionEvent;
        if (isStill)
        {
            transitionEvent = new ActivityTransitionEvent(DetectedActivity.STILL,
                    ActivityTransition.ACTIVITY_TRANSITION_EXIT, SystemClock.elapsedRealtimeNanos());
            events.add(transitionEvent);
            transitionEvent = new ActivityTransitionEvent(DetectedActivity.WALKING,
                    ActivityTransition.ACTIVITY_TRANSITION_ENTER, SystemClock.elapsedRealtimeNanos());
            events.add(transitionEvent);
        }
        else
        {

            transitionEvent = new ActivityTransitionEvent(DetectedActivity.WALKING,
                    ActivityTransition.ACTIVITY_TRANSITION_EXIT, SystemClock.elapsedRealtimeNanos());
            events.add(transitionEvent);
            transitionEvent = new ActivityTransitionEvent(DetectedActivity.STILL,
                    ActivityTransition.ACTIVITY_TRANSITION_ENTER, SystemClock.elapsedRealtimeNanos());
            events.add(transitionEvent);
        }
        isStill = !isStill;
        ActivityTransitionResult result = new ActivityTransitionResult(events);
        SafeParcelableSerializer.serializeToIntentExtra(result, intent,
                "com.google.android.location.internal.EXTRA_ACTIVITY_TRANSITION_RESULT");
        sendBroadcast(intent);
    }

    private void printToScreen(@NonNull String message)
    {
        Log.d(TAG, message);
    }

    /**
     * Handles intents from the Transitions API.
     */
    public class TransitionsReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent)
        {

            Log.d(TAG, "onReceive(): " + intent);

            if (!TextUtils.equals(TRANSITIONS_RECEIVER_ACTION, intent.getAction()))
            {

                printToScreen("Received an unsupported action in TransitionsReceiver: action = " +
                        intent.getAction());
                return;
            }

            // TODO: Extract activity transition information from listener.
            if (!ActivityTransitionResult.hasResult(intent))
            {
                Log.d(TAG, "empty");

                return;
            }

            ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
            Log.d(TAG, "result(): " + result.getTransitionEvents());

            for (ActivityTransitionEvent event : result.getTransitionEvents())
            {
                if (DetectedActivity.STILL == event.getActivityType())
                {
                    continue;
                }

                boolean enteringStill = ActivityTransition.ACTIVITY_TRANSITION_ENTER == event.getTransitionType();
                boolean leavingStill = !enteringStill;
                if (enteringStill && !timerStarted)
                {
                    activityTimer.startTimer(activityTextView);
                    printToScreen("Started timer");
                    timerStarted = true;
                }
                else if (leavingStill && timerStarted)
                {
                    activityTimer.stopTimer();
                    printToScreen("Stopped timer");
                    timerStarted = false;
                }
            }
        }
    }
}
