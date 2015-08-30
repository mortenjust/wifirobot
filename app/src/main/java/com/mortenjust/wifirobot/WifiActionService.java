package com.mortenjust.wifirobot;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import java.awt.font.TextAttribute;

public class WifiActionService extends IntentService {
    private static final String ACTION_START_FENCE = "com.mortenjust.wifirobot.action.START_FENCE";
    private static final String EXTRA_FENCE_WIDTH = "com.mortenjust.wifirobot.extra.FENCE_WIDTH";
    String TAG = "mj.wifiservice";

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
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
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_START_FENCE.equals(action)) {
                final int fenceWidth = intent.getIntExtra(EXTRA_FENCE_WIDTH, 0);
                handleStartFence(fenceWidth);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleStartFence(int fenceWidth) {
        Log.d(TAG, "ready to set up the fence with a width of "+fenceWidth);

    }

}
