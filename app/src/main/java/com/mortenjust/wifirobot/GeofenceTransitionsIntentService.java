package com.mortenjust.wifirobot;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

// just testing

public class GeofenceTransitionsIntentService extends IntentService {
    String TAG = "mj.geofenceservice";

    public GeofenceTransitionsIntentService(){
        super("GeofenceTransitionsIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()){
            Log.d(TAG, "geofence error");
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            // the service takes care of starting wifi again
            WifiActionService.stopGeofences(getApplicationContext());
        }
    }

}
