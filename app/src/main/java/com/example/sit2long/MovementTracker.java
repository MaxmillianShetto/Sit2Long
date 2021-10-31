package com.example.sit2long;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;


import androidx.annotation.Nullable;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

public class MovementTracker extends IntentService {
     public MovementTracker(){
         super("MovementTracker");


     }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(ActivityRecognitionResult.hasResult(intent)){
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            detectActivity(result.getProbableActivities());
        }
    }

    public void detectActivity(List<DetectedActivity> activities){
        for(DetectedActivity activity : activities){
            switch (activity.getType()){
                case DetectedActivity.STILL:
                    Log.i("Info","--------------------------Wake up man ----------------------");

            }
        }
    }
}
