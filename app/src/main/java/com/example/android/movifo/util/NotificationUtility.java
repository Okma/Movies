package com.example.android.movifo.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.preference.PreferenceManager;

import com.example.android.movifo.DetailsActivity;
import com.example.android.movifo.R;

/**
 * Created by Carl on 4/10/2017.
 */

public class NotificationUtility {

    private static final int DISCOVERY_NOTIFICATION_ID = 9192;

    public static void sendDiscoveryNotification(Context context, Uri discoverMovieURI) {

        // If notifications are enabled, send a notification.
        if(checkNotificationsEnabled(context)) {

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            // Create intent to open details activity with the given movie URI.
            Intent goToMovieDiscoveryDetails = new Intent(context, DetailsActivity.class);
            goToMovieDiscoveryDetails.setData(discoverMovieURI);

            // Create a task stack builder to allow unwinding to main activity from details activity.
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
            taskStackBuilder.addNextIntentWithParentStack(goToMovieDiscoveryDetails);
            PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            // Create a new notification.
            Notification newNotification =
                    new Notification.Builder(context)
                            .setContentIntent(pendingIntent)
                            //.setContentText()
                            //.setContentTitle()
                            .build();

            // Fire the notification.
            notificationManager.notify(DISCOVERY_NOTIFICATION_ID, newNotification);

            // Record that a notification was fired.
            saveLastNotificationTime(context);
        }
    }

    /**
     * Helper method to check if notifications are enabled in preferences.
     * @param context Context used for acquiring SharedPreferences reference.
     * @return True if notifications are enabled, false otherwise.
     */
    private static boolean checkNotificationsEnabled(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(context.getString(R.string.pref_notifications_enabled_key),
                context.getResources().getBoolean(R.bool.pref_notifications_default));
    }

    /**
     * Records the last sent notification time to now.
     * @param context Context used for acquiring SharedPreferences reference.
     */
    private static void saveLastNotificationTime(Context context) {
        SharedPreferences.Editor sharedPreferencesEditor = PreferenceManager.getDefaultSharedPreferences(context).edit();

        // Record the current time (in milliseconds).
        sharedPreferencesEditor.putLong(context.getString(R.string.pref_last_notification_time_key), System.currentTimeMillis());

        sharedPreferencesEditor.apply();
    }
}
