package com.example.cs183.nmpalertviewer.alarms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StartupBroadcaster extends BroadcastReceiver {
    ServerPullAlarm serverPullAlarm = null;
    boolean alarmSet = false;

    public final static String startupStr = "mobile.cs183.alarms.StartupBroadcaster";

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d(this.getClass().getName(), "Broadcast Received " + intent.toString());
        if (intent.getAction().equals(startupStr)
         || intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            // Set Http Client to run at 12:00 in the afternoon every day
            if (serverPullAlarm == null) {
                serverPullAlarm = new ServerPullAlarm();
            }
            if (serverPullAlarm != null && !alarmSet) {
                serverPullAlarm.setIntervalAlarmManager(context.getApplicationContext());
                alarmSet = true;
            }
        }
    }
}
