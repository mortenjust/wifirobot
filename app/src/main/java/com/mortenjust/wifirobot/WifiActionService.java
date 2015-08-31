package com.mortenjust.wifirobot;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.location.Location;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.awt.font.TextAttribute;
import java.util.ArrayList;

public class WifiActionService extends IntentService
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback {
    private static final String ACTION_START_FENCE = "com.mortenjust.wifirobot.action.START_FENCE";
    private static final String ACTION_STOP_FENCES = "com.mortenjust.wifirobot.action.STOP_FENCES";
    private static final String EXTRA_FENCE_WIDTH = "com.mortenjust.wifirobot.extra.FENCE_WIDTH";
    private static final String PREF_ISSUE_NOTIFICATIONS = "com.mortenjust.wifirobot.pref.ISSUE_NOTIFICATIONS";
    String TAG = "mj.wifiservice";
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    int mFenceWidth;
    PendingIntent mGeofencePendingIntent;
    ArrayList<Geofence> mGeofenceList = new ArrayList<Geofence>();
    String mCurrentAction;



    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_START_FENCE.equals(action)) {
                final int fenceWidth = intent.getIntExtra(EXTRA_FENCE_WIDTH, 0);
                handleStartFence(fenceWidth);
            }
            if (ACTION_STOP_FENCES.equals(action)) {
                handleStopFences();
            }
        }
    }



    @Override
    public void onResult(Result result) {
        Log.d(TAG, "on result");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */


    public static void stopGeofences(Context context){
        Intent intent = new Intent(context, WifiActionService.class);
        intent.setAction(ACTION_STOP_FENCES);
        context.startService(intent);
    }



    public static void startGeofence(Context context, int fenceWidth) {
        Intent intent = new Intent(context, WifiActionService.class);
        intent.setAction(ACTION_START_FENCE);
        intent.putExtra(EXTRA_FENCE_WIDTH, fenceWidth);
        context.startService(intent);
    }

    public WifiActionService() {
        super("WifiActionService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
       ///
      //  buildGoogleApiClient();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "connection failed");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "connection suspended");
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onconnected, now getting last known locesh");
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLastLocation != null){
            Log.d(TAG, "Last known location is "
                    +mLastLocation.getLatitude()+" "
                    +mLastLocation.getLongitude()+" accuracy of "
                    +mLastLocation.getAccuracy());

            // call
            if(mCurrentAction == ACTION_START_FENCE){
                setupGeofence(mLastLocation);
            }

            if(mCurrentAction == ACTION_STOP_FENCES){
                tearDownFences();
            }


        }
    }

    private void tearDownFences(){

        LocationServices.GeofencingApi.removeGeofences(
                mGoogleApiClient,
                getGeofencePendingIntent()
        ).setResultCallback(this);

    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_EXIT);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private void setupGeofence(Location location){

        mGeofenceList.add(new Geofence.Builder()
                .setRequestId("wifirobotics")
                .setCircularRegion(location.getLatitude(), location.getLongitude(), 1000)
                .setExpirationDuration(43200000) // 12 hours
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());

        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_EXIT);
        builder.addGeofences(mGeofenceList);

        LocationServices.GeofencingApi.addGeofences(
                mGoogleApiClient,
                getGeofencingRequest(),
                getGeofencePendingIntent()
        ).setResultCallback(this);
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }


    protected synchronized void buildGoogleApiClient() {
        Log.d(TAG, "building google api client");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.d(TAG, "starting building api client");

        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleStartFence(int fenceWidth) {
        mCurrentAction = ACTION_START_FENCE;
        mFenceWidth = fenceWidth;
        Log.d(TAG, "ready to set up the fence with a width of " + fenceWidth);
        Boolean doSendNotification = false;
        // only show notification the first few times or if the user has enabled it
        if(Util.getPrefInt(getApplicationContext(), "stoppedCount") <= 3){ doSendNotification = true; }

        if(!userWantsNotifications()){
            doSendNotification = false;
        } else {
            doSendNotification = true;
        }

        if(doSendNotification){
            Util.issueNotification(getApplicationContext(),
                    R.drawable.wifi_off,
                    "Wifi disabled",
                    "We'll turn it on when you get out of here",
                    Util.getMainActivityPendingIntent(getApplicationContext()));
        }
    }

    public void enableWifi(){
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
        int enabledCount = Util.getPrefInt(getApplicationContext(), "enabledCount");
        enabledCount++;
        Util.writePrefInt(getApplicationContext(), "enabledCount", enabledCount);
        Log.d(TAG, "enablecount: " + enabledCount);
    }


    public boolean userWantsNotifications(){
        Log.d(TAG, "I was asked what prefs says. It says "+Util.getPrefBoolean(getApplicationContext(), PREF_ISSUE_NOTIFICATIONS));
        return Util.getPrefBoolean(getApplicationContext(), PREF_ISSUE_NOTIFICATIONS);
    }

    private void handleStopFences(){
        mCurrentAction = ACTION_STOP_FENCES;
        enableWifi();
        int stoppedCount = Util.getPrefInt(getApplicationContext(), "stoppedCount");
        stoppedCount++;

        Util.writePrefInt(getApplicationContext(), "stoppedCount", stoppedCount);
        Log.d(TAG, "stoppedcount: " + stoppedCount);

        String contentText;

        switch (stoppedCount){
            case 1:
                contentText = "It worked! You'll get a notification the next few times the app automatically turns on your wifi";
                break;
            case 2:
                contentText = "It worked again! Pretty smooth.";
                break;
            case 3:
                contentText = "It just works. You can turn off these notifications from this notification";
                break;
            case 4:
                contentText = "Boom. Flippin' switches like the mother of robots";
                break;
            case 5:
                contentText = "Love. Ro. Bot.";
                break;
            case 6:
                contentText = "Proudly switching since 2015";
                break;
            case 7:
                contentText = "It's all in HOW you switch it.";
                break;
            case 8:
                contentText = "And we're back.";
                break;
            default:
                contentText = "Sincerely, the Wifi Robot";

        }

        Boolean doSendNotification = true;
        if (!userWantsNotifications()) {
            doSendNotification = false;
        } else {
            doSendNotification = true;
        }

        if(doSendNotification){
        Util.issueNotification(getApplicationContext(),
                R.drawable.wifi_on,
                "Wifi enabled",
                contentText,
                Util.getMainActivityPendingIntent(getApplicationContext()));
        }
    }

}
