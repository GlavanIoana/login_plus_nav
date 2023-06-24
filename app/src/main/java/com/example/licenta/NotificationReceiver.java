package com.example.licenta;

import static com.example.licenta.MainActivity.CHANNEL_1_ID;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Timer;
import java.util.TimerTask;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String TAG="NotificationReceiver";
    private NotificationManagerCompat notificationManager;
    private final int maxProgress=10;

    @Override
    public void onReceive(Context context, Intent intent) {
//        String message = intent.getStringExtra("toastMessage");
//        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
//

        notificationManager = NotificationManagerCompat.from(context);

        String action = intent.getAction();
        if (action != null) {
            switch (action) {
                case "START":
                    // Handle "start" button click
                    startNotificationProgress(context);
                    break;
                case "DELAY":
                    int delayDuration = 5; // Delay duration in minutes (e.g., 5 seconds)
                    showDelayedNotification(context, delayDuration);
                    break;
                // ... other cases ...
            }
        }
    }

    private void startNotificationProgress(Context context) {
        // Create a new notification with progress
        NotificationCompat.Builder progressBuilder = new NotificationCompat.Builder(context, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_baseline_calendar_month_24)
                .setContentTitle("Notification with Progress")
                .setProgress(maxProgress, 0, false)
                .setOnlyAlertOnce(true); // Set initial progress

        // Update the notification with progress in a loop or background task
        // You can use a TimerTask or a background service to update the progress

        // For example, update the progress every second until a given hour
        TimerTask timerTask = new TimerTask() {
            private int progress = 0;

            @Override
            public void run() {
                if (progress < maxProgress) {
                    // Update the progress
                    progressBuilder.setProgress(maxProgress, progress, false);
                    notificationManager.notify(1, progressBuilder.build());

                    // Increment the progress
                    progress++;
                } else {
                    // Remove the progress bar when the desired hour is reached
                    progressBuilder.setProgress(0, 0, false)
                            .setContentText("Progress complete!");
                    notificationManager.notify(1, progressBuilder.build());

                    // Cancel the timer or stop the background task
                    cancel();
                }
            }
        };

        // Start the timer or execute the background task
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 0, 5000); // Update progress every 10 seconds
    }

    private void cancelNotification(Context context) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(1); // Cancel the existing notification with ID 1
        Log.d(TAG, "cancelNotification call");
    }

    private void showDelayedNotification(Context context, long delayDuration) {
        // Cancel the current notification immediately
        cancelNotification(context);

        // Delayed notification details
        String title = "Titlu";
        String message = "Mesaj";

        Intent startIntent = new Intent(context, NotificationReceiver.class);
        startIntent.setAction("START");
        PendingIntent startPendingIntent = PendingIntent.getBroadcast(context,
                0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent delayIntent = new Intent(context, NotificationReceiver.class);
        delayIntent.setAction("DELAY");
        PendingIntent delayPendingIntent = PendingIntent.getBroadcast(
                context, 0, delayIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        long delayInMillis = delayDuration * 60 * 1000; // 5 minutes

        new Handler().postDelayed(() -> {
            Notification notification = null;//MainActivity.buildNotification(context, title, message, startPendingIntent, delayPendingIntent);
            Log.d(TAG, "Showing delayed notification");
            notificationManager.notify(1, notification);
        }, delayInMillis);

    }

}
