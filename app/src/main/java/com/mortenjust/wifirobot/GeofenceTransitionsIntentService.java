package com.mortenjust.wifirobot;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;


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
            // since we dont care which fence and probably wont' have more than one, we'll just go ahead and enable wifi now
            Log.d(TAG, "Ready to enable wifi!");
            enableWifi();
            // enable wifi
            Log.d(TAG, "Now remove geofence");
            // remove geofence
        }
    }

    public void enableWifi(){
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
        Util.issueNotification(getApplicationContext(), R.drawable.abc_btn_check_material, "Wifi enabled", "Sincerely, the robot", Util.getMainActivityPendingIntent(getApplicationContext()) );
    }
}
