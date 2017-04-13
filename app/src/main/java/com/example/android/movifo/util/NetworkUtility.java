package com.example.android.movifo.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.android.movifo.R;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Network utility library providing various functionality.
 * Created by Carl on 4/9/2017.
 */

public class NetworkUtility {

    private static final String TAG = NetworkUtility.class.getSimpleName();

    /** Base URL for the discover movie endpoint in the MovieDB API **/
    private final static String BASE_API_URL = "https://api.themoviedb.org/3/movie/";

    /** Base URL for image locations. **/
    private final static String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";

    /** Base URL for watching a Youtube video. **/
    private final static String YOUTUBE_WATCH_URL = "https://youtube.com/watch";

    /** Default image size param **/
    private final static String IMAGE_SIZE_PARAM = "w185";

    /** Query Params **/
    private final static String API_KEY_PARAM = "api_key";

    /** Authentication API key **/
    // @TODO: CHANGE TO USE YOUR API KEY HERE
    private final static String API_KEY = "YOUR_API_KEY";

    /** Volley request queue for making asynchronous network calls. **/
    private static RequestQueue mRequestQueue;

    /**
     * Returns singleton instance of internal Volley queue.
     * @param context Context used to create the queue if it doesn't exist.
     * @return The Volley.RequestQueue singleton.
     */
    private static RequestQueue getRequestQueue(Context context) {
        if(mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(context);
        }
        return mRequestQueue;
    }

    /**
     * Adds a request to the internal Volley request queue.
     * @param context Context used to create the queue if it doesn't exist.
     * @param request Request to be added to the queue.
     * @param <T> The generic type specifier of the queue.
     */
    public static <T> Request<T> addToRequestQueue(Context context, Request<T> request) {
        return getRequestQueue(context).add(request);
    }

    /**
     * Clears the request queue of all requests, if valid.
     */
    public static void clearQueue() {
        if(mRequestQueue != null) {
            RequestQueue.RequestFilter catchAllFilter = new RequestQueue.RequestFilter() {
                @Override
                public boolean apply(Request<?> request) {
                    return true;
                }
            };
            mRequestQueue.cancelAll(catchAllFilter);
        }
    }

    /**
     * Converts a given DataSortPreference to the string used for an HTTP request query param.
     * See https://developers.themoviedb.org/3/discover for more info.
     * @param context Context reference for acquiring SharedPreferences.
     * @return The string of the query param for the user's sorting preference.
     */
    private static String convertSortPreferenceToQueryString(Context context) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        // Read the current value of the sorting shared preference.
        String sortPreference = sharedPreferences.getString(context.getString(R.string.sort_preference_key),
                context.getString(R.string.sort_preference_most_popular_key));

        if(sortPreference.equals(context.getString(R.string.sort_preference_highest_rated_key))) {
            return "top_rated";
        } else if(sortPreference.equals(context.getString(R.string.sort_preference_most_popular_key))) {
            return "popular";
        } else {
            Log.e(TAG, "convertSortPreferenceToQueryString: Invalid sort preference found!");
            throw new RuntimeException();
        }
    }

    /**
     * Helper method for creating a URL to TheMovieDB with API key attached for authentication.
     * @param baseUrlString The URL string to send the request.
     * @return The formatted URL request.
     */
    private static URL buildAuthQueryURL(String baseUrlString) {
        // Assemble the URI, adding in the api key and sort query params.
        Uri builtURI = Uri.parse(baseUrlString).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();

        // Attempt to convert the built URI to a URL object.
        URL url = null;
        try {
            url = new URL(builtURI.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * Build the movie query URL for making an HTTP request for movies data.
     * @param context Context reference for accessing SharedPreferences.
     * @return The built URL for requesting movies data.
     */
    public static URL buildMovieQueryURL(Context context) {
        final String sortPreferenceAsString = convertSortPreferenceToQueryString(context);
        final String assembledBaseURL = BASE_API_URL.concat(sortPreferenceAsString);
        return buildAuthQueryURL(assembledBaseURL);
    }

    /**
     * Builds the URI for playing a Youtube video.
     * @param videoSpecifier The unique identifier for the youtube video.
     * @return The built URI for playing the specified video.
     */
    public static Uri buildYoutubeTrailerUri(String videoSpecifier) {
        return Uri.parse(YOUTUBE_WATCH_URL).buildUpon()
                .appendQueryParameter("v", videoSpecifier)
                .build();
    }

    /**
     * Build the movie query URL for making an HTTP request for the given movie's reviews.
     * @param movieId ID specifying which movie to query for.
     * @return The built URL for requesting the movie's reviews.
     */
    public static URL buildReviewsURL(int movieId) {
        final String reviewsURL = BASE_API_URL + String.valueOf(movieId) + "/reviews";
        return buildAuthQueryURL(reviewsURL);
    }

    /**
     * Build the movie query URL for making an HTTP request for the given movie's videos.
     * @param movieId ID specifying which movie to query for.
     * @return The built URL for requesting the movie's videos.
     */
    public static URL buildVideoURL(int movieId) {
        final String videosURL = BASE_API_URL + String.valueOf(movieId) + "/videos";
        return buildAuthQueryURL(videosURL);
    }

    /**
     * String assembler for TheMovieDB's image URL.
     * @param relativePath The path to the specific image requested.
     * @return A string of the image URL.
     */
    public static String constructImageURLString(String relativePath) {
        return BASE_IMAGE_URL + IMAGE_SIZE_PARAM + relativePath;
    }

    /**
     * Checks if the device is connected to the internet.
     * @param context Context for getting ConnectivityManager from system services.
     * @return True if connected to the internet, false otherwise.
     */
    public static boolean isDeviceOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
