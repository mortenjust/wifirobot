package com.mortenjust.wifirobot;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.Set;

/**
 * Created by mortenjust on 8/29/15.
 */
public class Util {

    private static final String TAG = "mj.IntentDump";

    public static void issueNotification(Context context, int smallIcon, String title, String text, PendingIntent pendingIntent){
        int NOTIFICATION_ID = 0;
        NotificationCompat.Builder n = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.abc_switch_thumb_material)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID, n.build());
    }

    public static PendingIntent getMainActivityPendingIntent(Context c){
        Intent activityIntent = new Intent(c, MainActivity.class);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(c, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    public static void dumpIntent(Intent i){
        Bundle bundle = i.getExtras();
        if (bundle != null) {
            Set<String> keys = bundle.keySet();

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("IntentDump \n\r");
            stringBuilder.append("-------------------------------------------------------------\n\r");

            for (String key : keys) {
                stringBuilder.append(key).append("=").append(bundle.get(key)).append("\n\r");
            }

            stringBuilder.append("-------------------------------------------------------------\n\r");
            Log.i(TAG, stringBuilder.toString());
        }
    }
}