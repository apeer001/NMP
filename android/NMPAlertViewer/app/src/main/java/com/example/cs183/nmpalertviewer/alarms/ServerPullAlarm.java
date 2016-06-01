package com.example.cs183.nmpalertviewer.alarms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.example.cs183.nmpalertviewer.services.ServerPullService;

import java.io.IOException;
import java.util.Calendar;

public class ServerPullAlarm extends WakefulBroadcastReceiver {
    public ServerPullAlarm() {
    }

    PendingIntent alarmIntent;

    /* Returns an alarm manager that will fire at an interval */
    public AlarmManager setIntervalAlarmManager(Context context) {
        //Log.d("HttpsClientAlarm", "Set alarm");

        Intent intent = new Intent(context, ServerPullAlarm.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // 2 minute alarm
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP,
                60 * 1000,
                120 * 1000,
                alarmIntent
        );
        return alarm;
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        //Log.d("AlarmRecv", "ALARM " + intent.toString());
        Intent i = new Intent(context, ServerPullService.class);
        context.startService(i);
    }
}
