package com.example.licenta;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
//import androidx.work.Worker;
//import androidx.work.WorkerParameters;

public class NotificationWorker{//} extends Worker {
//
//    private static final String CHANNEL_ID = "channel_id";
//    private static final String CHANNEL_NAME = "Notification Channel";
//    private static final int NOTIFICATION_ID = 1;
//
//    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
//        super(context, workerParams);
//    }
//
//    @NonNull
//    @Override
//    public Result doWork() {
//        // Retrieve upcoming events from the database or data source
//        // and create the notification
//
//        String title = "Upcoming Event";
//        String message = "Event description";
//
//        // Create the notification channel
//        createNotificationChannel();
//
//        // Build the notification
//        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_baseline_calendar_month_24)
//                .setContentTitle(title)
//                .setContentText(message)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setColor(Color.BLUE)
//                .build();
//
//        // Show the notification
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
//        notificationManager.notify(NOTIFICATION_ID, notification);
//
//        return Result.success();
//    }
//
//    private void createNotificationChannel() {
//        NotificationChannel channel = new NotificationChannel(
//                CHANNEL_ID,
//                CHANNEL_NAME,
//                NotificationManager.IMPORTANCE_HIGH
//        );
//        channel.setDescription("Notification Channel");
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
//        notificationManager.createNotificationChannel(channel);
//    }
}
