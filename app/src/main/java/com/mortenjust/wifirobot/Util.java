package com.mortenjust.wifirobot;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.lang.reflect.Method;
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

    private static SharedPreferences getPrefs(Context c){
        return c.getSharedPreferences(c.getString(R.string.preference_file_name), Context.MODE_PRIVATE);
    }
    private static SharedPreferences.Editor getPrefEditor(Context context){
        return getPrefs(context).edit();
    }
    public static void writePref(Context context, String key, String value){
        getPrefEditor(context).putString(key, value).commit();
    }
    public static void writePref(Context context, String key, int value){
        getPrefEditor(context).putInt(key, value).commit();
    }
    public static void writePref(Context context, String key, boolean value){
        getPrefEditor(context).putBoolean(key, value).commit();
    }
    public static boolean getPrefBoolean(Context context, String key){
        return getPrefs(context).getBoolean(key, true);
    }
    public static int getPrefInt(Context context, String key){
        return getPrefs(context).getInt(key, 0);
    }
    public static String getPrefString(Context context, String key){
        return getPrefs(context).getString(key, "");
    }


    public static boolean isSharingWiFi(final WifiManager manager)
    {
        try
        {
            final Method method = manager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true); //in the case of visibility change in future APIs
            return (Boolean) method.invoke(manager);
        }
        catch (final Throwable ignored)
        {
        }

        return false;
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