package com.mortenjust.wifirobot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiBroadcastReceiver extends BroadcastReceiver {
    String TAG = "mj.wifi";
    public WifiBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //
      //  Log.d(TAG, "Wifi state changed, dumping intent");
      //  Util.dumpIntent(intent);

        int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
        int previousState = intent.getIntExtra(WifiManager.EXTRA_PREVIOUS_WIFI_STATE, 0);


        // start the appropriate service, or give it the appropriate extras
        switch(state){
            case WifiManager.WIFI_STATE_DISABLING:
                Log.d(TAG, "disabling");
                break;
            case WifiManager.WIFI_STATE_DISABLED:
                Log.d(TAG, "wifi is disabled, over to you geofence service");
                WifiActionService.startGeofence(context, 50);
                break;
            case WifiManager.WIFI_STATE_ENABLED:
                Log.d(TAG, "enabled");
                break;
            case WifiManager.WIFI_STATE_ENABLING:
                Log.d(TAG, "enabling");
                break;
        }




    }
}
