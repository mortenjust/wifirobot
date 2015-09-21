package com.mortenjust.wifirobot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import java.awt.font.TextAttribute;

public class MainActivity extends Activity {

    String TAG = "mj.mainactivity";
    private static final String PREF_ISSUE_NOTIFICATIONS = "com.mortenjust.wifirobot.pref.ISSUE_NOTIFICATIONS";
    private static final String ACTION_STOP_NOTIFICATIONS = "com.mortenjust.wifirobot.extra.STOP_NOTIFICATIONS";
    Switch wantsNotifications;


    void setupViews(){
        wantsNotifications = (Switch) findViewById(R.id.wantsNotifications);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupViews();

        boolean userWantsNotifications = Util.getPrefBoolean(getApplicationContext(), PREF_ISSUE_NOTIFICATIONS);
        if(userWantsNotifications){
            wantsNotifications.setChecked(true);
        } else {
            wantsNotifications.setChecked(false);
        }

        wantsNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Util.writePref(getApplicationContext(), PREF_ISSUE_NOTIFICATIONS, true);
                } else {
                    Util.writePref(getApplicationContext(), PREF_ISSUE_NOTIFICATIONS, false);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "on resume");

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "on new intent");
        checkForNotificationAction(intent);
    }

    private void checkForNotificationAction(Intent intent){
        Log.d(TAG, "checking for notification action. Action is " + intent.getAction());

        // if we got here from the notification action
        if(intent.getAction().equals(ACTION_STOP_NOTIFICATIONS)){
            Log.d(TAG, "ready to turn off notifications");
            // first do it int he shared prefs
            Util.writePref(getApplicationContext(), PREF_ISSUE_NOTIFICATIONS, false);
            // then switch
            wantsNotifications.setChecked(false);

            // do cool animations and UI stuff
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void handleStopNotifications(){
        // just put something in prefs
        Util.writePref(getApplicationContext(), PREF_ISSUE_NOTIFICATIONS, false);

    }
}
