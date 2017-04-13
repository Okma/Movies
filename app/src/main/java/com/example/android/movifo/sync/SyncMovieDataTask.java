package com.example.android.movifo.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.android.movifo.MainActivity;
import com.example.android.movifo.R;
import com.example.android.movifo.data.Movie;
import com.example.android.movifo.data.MovieContract;
import com.example.android.movifo.util.JsonUtility;
import com.example.android.movifo.util.NetworkUtility;

import java.net.URL;

/**
 * Task for fetching new movie data using TheMovieDB API.
 * Created by Carl on 4/9/2017.
 */

public class SyncMovieDataTask {

    private static final String TAG = SyncMovieDataTask.class.getSimpleName();

    // Flag for ensuring initialize is only called once.
    private static boolean bSyncInitialized = false;

    synchronized public static void syncMovieData(final Context context) {
        // If device is not online and is running in foreground, inform user if device is offline.
        if(!NetworkUtility.isDeviceOnline(context) && MainActivity.bApplicationInForeground) {
            Toast.makeText(context, context.getString(R.string.device_offline), Toast.LENGTH_LONG).show();
            return;
        }

        try {
            // Construct the movie request URL, based on the preferences of the user.
            URL movieRequestUrl = NetworkUtility.buildMovieQueryURL(context);

            // Add the request to the Volley queue.
            NetworkUtility.addToRequestQueue(context,
                    new StringRequest(Request.Method.GET, movieRequestUrl.toString(),
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    onSuccessRequestResponse(context, response);
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    }));
        } catch (Exception e) {
            Log.e(TAG, "syncMovieData: " + e.getMessage());
        }
    }

    /**
     * Handle function following a successful request.
     * @param context Context reference.
     * @param jsonResponse The JSON response as a string.
     */
    private static void onSuccessRequestResponse(Context context, String jsonResponse) {
        if(!jsonResponse.isEmpty()) {
            // Extract individual JSON object strings from results.
            String[] objectStrings = JsonUtility.expandResultsString(jsonResponse);

            // Ensure there are results returned.
            if(objectStrings.length > 0) {
                // Acquire content resolver reference.
                ContentResolver contentResolver = context.getContentResolver();

                for (String objectString : objectStrings) {
                    // Parse jsonResponse string to Movie instance.
                    Movie movie = Movie.createMovieFromJson(objectString);

                    // Parse movie to content values for DB functions.
                    ContentValues contentValues = movie.generateContentValues();

                    int rowsUpdated = contentResolver.update(MovieContract.MovieEntry.CONTENT_URI,
                            contentValues,
                            MovieContract.MovieEntry.COLUMN_ID + "=" + String.valueOf(movie.id),
                            null);

                    // If updating failed, try inserting instead.
                    if(rowsUpdated <= 0) {
                        // Parse movie to a ContentValues and insert into database.
                        contentResolver.insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);
                    }
                }
            }
        }
    }

    synchronized public static void initializeSyncService(Context context) {
        // If already initialized, return.
        if(bSyncInitialized) return;
        bSyncInitialized = true;

        // Query the database for the number of entries to see if the cache is empty.
        Cursor cursor = context.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry.COLUMN_ID},
                null,
                null,
                null);

        // Check if there are no items in the database.
        // If so, start a data sync.
        if(cursor == null || cursor.getCount() == 0) {
            syncMovieData(context);
        }

        // Safely close the cursor.
        if(cursor != null) {
            cursor.close();
        }
    }
}
