package com.example.appalarm.Notification;

// SnoozeReceiver.java

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class SnoozeReceiver extends BroadcastReceiver {
    public static final int NOTIFICATION_ID = 1;
    private static final int ALARM_REQUEST_CODE = 1;
    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmReceiver.stopAlarm();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);

        // Snooze the alarm for 5 minutes later
        Calendar snoozeTime = Calendar.getInstance();
        snoozeTime.add(Calendar.MINUTE, 5);

        // Reschedule the alarm
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ALARM_REQUEST_CODE, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, snoozeTime.getTimeInMillis(), pendingIntent);
    }
}

