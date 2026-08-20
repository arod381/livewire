package com.livewire.UI;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.livewire.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CReceiver extends BroadcastReceiver {

    static int notificationID = 1;
    private static final String channel_id = "vacation_channel";
    private static final String excursion_channel_id = "excursion_channel";

    // Store notifications in a list
    public static List<NotificationData> notificationList = new ArrayList<>();

    public static class NotificationData {
        int id;
        String text;
        String channel;
        String timestamp;

        NotificationData(int id, String text, String channel, String timestamp) {
            this.id = id;
            this.text = text;
            this.channel = channel;
            this.timestamp = timestamp;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String channel = intent.hasExtra("excursion_channel") ? excursion_channel_id : channel_id;
        String key = intent.getStringExtra("key") != null ? intent.getStringExtra("key") : "No message";
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        // Add notification data to the list
        notificationList.add(new NotificationData(notificationID, key, channel, timestamp));

        Log.d("CReceiver", "Received notification key: " + key);
        Toast.makeText(context, key, Toast.LENGTH_LONG).show();
        createNotificationChannel(context, channel);

        Notification notification = new NotificationCompat.Builder(context, channel)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentText(key)
                .setContentTitle("NotificationTest")
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationID++, notification);

        Log.d("CReceiver", "Notification sent with ID: " + notificationID + " to channel: " + channel);
    }

    private void createNotificationChannel(Context context, String CHANNEL_ID) {
        CharSequence name = context.getResources().getString(R.string.channel_name);
        String description;

        if (CHANNEL_ID.equals(excursion_channel_id)) {
            name = context.getResources().getString(R.string.excursion_channel_name);
            description = context.getString(R.string.excursion_channel_description);
        } else {
            name = context.getResources().getString(R.string.channel_name);
            description = context.getString(R.string.channel_description);
        }

        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        Log.d("CReceiver", "Notification channel created: " + CHANNEL_ID);
    }
}