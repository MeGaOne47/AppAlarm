package com.example.appalarm.Notification;// AlarmReceiver.java

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.appalarm.R;


public class AlarmReceiver extends BroadcastReceiver {

    private static final int NOTIFICATION_ID = 1;
    private static final String NOTIFICATION_CHANNEL_ID = "alarm_channel";
    private static final CharSequence NOTIFICATION_CHANNEL_NAME = "AlarmChannel";

    private static MediaPlayer mediaPlayer;

    @Override
    public void onReceive(Context context, Intent intent) {
        showNotification(context);

        // Play the alarm sound
        if (mediaPlayer == null || !mediaPlayer.isPlaying()) {
            mediaPlayer = MediaPlayer.create(context, R.raw.alarm);
            mediaPlayer.setLooping(true); // Loop the sound to repeat until user cancels the alarm.
            mediaPlayer.start();
        }
    }

    // Stop the alarm sound when the alarm is canceled.
    public static void stopAlarm() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void showNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // Create the notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Alarm")
                .setContentText("Time to wake up!")
                .setSmallIcon(R.drawable.ic_alarm)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        // Create the dismiss action intent
        Intent dismissIntent = new Intent(context, DismissReceiver.class);
        dismissIntent.setAction("DISMISS_ACTION");
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(context, 0, dismissIntent, 0);
        builder.addAction(R.drawable.ic_dismiss, "Dismiss", dismissPendingIntent);

        // Create the snooze action intent
        Intent snoozeIntent = new Intent(context, SnoozeReceiver.class);
        snoozeIntent.setAction("SNOOZE_ACTION");
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(context, 0, snoozeIntent, 0);
        builder.addAction(R.drawable.ic_snooze, "Snooze", snoozePendingIntent);

        // Show the notification
        Notification notification = builder.build();
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}
