package com.example.cs183.nmpalertviewer.alarms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartupBroadcaster extends BroadcastReceiver {
    ServerPullAlarm serverPullAlarm = null;
    boolean alarmSet = false;

    @Override
    public void onReceive(final Context context, Intent intent) {
        //Log.d(this.getClass().getName(), "Broadcast Received " + intent.toString());
        if (intent.getAction().equals("mobile.cs183.alarms.StartupBroadcaster")
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
