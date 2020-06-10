package com.layoutxml.twelveish;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.layoutxml.twelveish.activities.list_activities.ActivityImageViewActivity;

public class PromotionalNotifications {
    private final Context context;
    private final SharedPreferences preferences;

    private int counter;
    boolean showedRateAlready;
    boolean showedTutorialAlready;

    PromotionalNotifications(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
    }

    public void doPromotion() {
        loadPreferences();
        counter++;

        if (!showedTutorialAlready && counter > 30) {
            // TODO: add a check for any setting changed
            showTutorialNotification();
        }
        if (!showedRateAlready && counter > 100) {
            showRateNotification();
        }

        savePreferences();
    }

    private void loadPreferences() {
        counter = preferences.getInt(context.getString(R.string.counter), 0);

        showedRateAlready = preferences.getBoolean(context.getString(R.string.showed_rate), false);
        showedTutorialAlready = preferences.getBoolean(context.getString(R.string.showed_tutorial), false);
    }

    private void savePreferences() {
        preferences.edit().putInt(context.getString(R.string.counter), counter).apply();

        preferences.edit().putBoolean(context.getString(R.string.showed_rate), showedRateAlready).apply();
        preferences.edit().putBoolean(context.getString(R.string.showed_tutorial), showedTutorialAlready).apply();
    }

    private void showTutorialNotification() {
        String title = "Open settings";
        String content = "Don't forget to customize the watch";
        Intent viewIntent = new Intent(context, ActivityImageViewActivity.class);
        viewIntent.putExtra(title, content);
        showNotification(2, viewIntent, title, content);
        showedTutorialAlready = true;

    }

    private void showRateNotification() {
        String title = "Rate Twelveish";
        String content = "Would you like to rate Twelveish? Tap to go to the Google Play store.";
        Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.layoutxml.twelveish"));
        viewIntent.putExtra(title, content);
        showNotification(1, viewIntent, title, content);
        showedRateAlready = true;
    }

    private void showNotification(int notificationId, Intent intent, String title, String content) {
        PendingIntent viewPendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        Notification notification = new NotificationCompat.Builder(context, "Main")
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(viewPendingIntent)
                .build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.createNotificationChannel(createNotificationChannel());
        notificationManager.notify(notificationId, notification);
    }

    private NotificationChannel createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("Main", "Main", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("General notifications");
            return notificationChannel;
        }
        return null;
    }
}
