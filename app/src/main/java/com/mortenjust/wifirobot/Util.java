package com.mortenjust.wifirobot;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    private static final String ACTION_STOP_NOTIFICATIONS = "com.mortenjust.wifirobot.extra.STOP_NOTIFICATIONS";

    public static void issueNotification(Context context, int smallIcon, String title, String text, PendingIntent pendingIntent){
        int NOTIFICATION_ID = 0;
        NotificationCompat.Builder n = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.abc_switch_thumb_material)
                .setContentTitle(title)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setContentText(text)
                .addAction(R.drawable.abc_ic_clear_mtrl_alpha, "Got it. Stop these notifications", getStopNotificationsPendingIntent(context))
                .setContentIntent(pendingIntent);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID, n.build());
    }

    public static PendingIntent getStopNotificationsPendingIntent(Context c){
        // return a pending intentn
        Intent activityIntent = new Intent(c, MainActivity.class);
        activityIntent.setAction(ACTION_STOP_NOTIFICATIONS);
        activityIntent.putExtra("action", ACTION_STOP_NOTIFICATIONS);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(c, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent getMainActivityPendingIntent(Context c){
        Intent activityIntent = new Intent(c, MainActivity.class);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(c, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void writePrefString(Context context, String key, String value){
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sharedPref.edit();
        e.putString(key, value);
    }

    public static void writePrefBoolean(Context context, String key, Boolean value){
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sharedPref.edit();
        e.putBoolean(key, value);
        e.commit();
    }

    public static void writePrefInt(Context context, String key, int value){
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sharedPref.edit();
        e.putInt(key, value);
        e.commit();
    }

    public static boolean getPrefBoolean(Context context, String key){
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_name), Context.MODE_PRIVATE);
        return sharedPref.getBoolean(key, true);
    }


    public static int getPrefInt(Context context, String key){
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_name), Context.MODE_PRIVATE);
        return sharedPref.getInt(key, 0);
    }

    public static String getPrefString(Context context, String key){
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_name), Context.MODE_PRIVATE);
        return sharedPref.getString(key, "");
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