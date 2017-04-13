package com.example.android.movifo.sync;

import android.content.Context;

/**
 * Movie tasks that can be executed by an intent service.
 * Created by Carl on 4/10/2017.
 */

public class MovieTasks {

    public static final String ACTION_SYNC_MOVIES = "update_movies";
    public static final String ACTION_SEND_NOTIFICATION = "send_notification";
    public static final String ACTION_DISMISS_NOTIFICATIONS = "dismiss_notifications";

    public static void executeTask(Context context, String actionString) {
        // Check action string to determine which action to perform.
        switch(actionString) {
            case ACTION_SEND_NOTIFICATION:
                break;
            case ACTION_DISMISS_NOTIFICATIONS:
                break;
            case ACTION_SYNC_MOVIES:
                SyncMovieDataTask.syncMovieData(context);
                break;
            default:
        }
    }
}
