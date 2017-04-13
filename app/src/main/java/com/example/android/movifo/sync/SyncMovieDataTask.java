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

            final String popularQueryString = "popular";
            final String highestRatedQueryString = "top_rated";

            // Construct the movie request URL for highest rated movies.
            URL popularQueryUrl = NetworkUtility.buildMovieQueryURL(popularQueryString);

            // Add the request to the Volley queue.
            NetworkUtility.addToRequestQueue(context,
                    new StringRequest(Request.Method.GET, popularQueryUrl.toString(),
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    onSuccessRequestResponse(context, response, context.getString(R.string.sort_preference_most_popular_key));
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    }));

            // Construct the movie request URL for highest rated movies.
            URL highestRatedQueryUrl = NetworkUtility.buildMovieQueryURL(highestRatedQueryString);

            // Add the request to the Volley queue.
            NetworkUtility.addToRequestQueue(context,
                    new StringRequest(Request.Method.GET, highestRatedQueryUrl.toString(),
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    onSuccessRequestResponse(context, response, context.getString(R.string.sort_preference_highest_rated_key));
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
    synchronized private static void onSuccessRequestResponse(Context context, String jsonResponse, String sortMethod) {
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
                    contentValues.put(MovieContract.MovieEntry.COLUMN_SORT_METHOD, sortMethod);

                    Cursor checkCursor = contentResolver.query(MovieContract.MovieEntry.CONTENT_URI,
                            new String[]{MovieContract.MovieEntry.COLUMN_ID},
                            MovieContract.MovieEntry.COLUMN_ID + " = " + movie.id,
                            null,
                            null);

                    // Check if movie already exists in database.
                    if(checkCursor.getCount() > 0) {
                        // Make sure to persist favorites value.
                        contentValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE,
                                checkCursor.getString(checkCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_FAVORITE)));

                        contentResolver.update(MovieContract.MovieEntry.CONTENT_URI,
                                contentValues,
                                MovieContract.MovieEntry.COLUMN_ID + " = " + String.valueOf(movie.id),
                                null);
                    }
                    // If the entry doesn't exist, try inserting instead.
                    else {
                        // Parse movie to a ContentValues and insert into database.
                        contentResolver.insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);
                    }
                    checkCursor.close();
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
