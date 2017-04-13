package com.example.android.movifo;

import android.content.ContentValues;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.android.movifo.data.Movie;
import com.example.android.movifo.data.MovieContract;
import com.example.android.movifo.databinding.ActivityDetailsBinding;
import com.example.android.movifo.sync.MovieQueryHandler;
import com.example.android.movifo.util.JsonUtility;
import com.example.android.movifo.util.NetworkUtility;
import com.example.android.movifo.util.PicassoUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = DetailsActivity.class.getSimpleName();
    private ActivityDetailsBinding detailsActivityBinding;

    // Internally cached movie data on creation.
    private Movie mMovieData;
    private MovieQueryHandler queryHandler;

    // Reviews string response for this movie.
    private String[] mMovieReviews;

    // Videos string response for this movie.
    private String mMovieTrailerIdentifier;

    // Movie review adapter for populating reviews.
    private MovieReviewsAdapter mMovieReviewsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detailsActivityBinding = DataBindingUtil.setContentView(this, R.layout.activity_details);

        // Create a new query handler to send favorite updates asynchronously.
        queryHandler = new MovieQueryHandler(getContentResolver());

        // Create a new MovieReviewsAdapter to render reviews.
        mMovieReviewsAdapter = new MovieReviewsAdapter();

        // Create layout manager for review recycler view.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        detailsActivityBinding.detailsReviews.rvReviews.setLayoutManager(linearLayoutManager);

        // Bind adapter to reviews RecyclerView.
        detailsActivityBinding.detailsReviews.rvReviews.setAdapter(mMovieReviewsAdapter);

        // Read JSON string data from incoming intent, if it exists.
        Intent incomingIntent = getIntent();
        if(incomingIntent.hasExtra(Movie.MOVIE_DATA_EXTRA)) {
            setMovieData(Movie.createMovieFromJson(incomingIntent.getStringExtra(Movie.MOVIE_DATA_EXTRA)));
        }
    }

    /**
     * Assigns and begins parsing movie data from given Movie object.
     * @param movieData The Movie to set as new cached movie data.
     */
    public void setMovieData(Movie movieData) {
        this.mMovieData = movieData;

        // Cache the number of volley requests to send.
        // One for movie videos and one for movie reviews.
        final int NUM_REQUESTS = 2;

        // Create a synchronization object to sync the volley responses.
        final CountDownLatch SYNC_LATCH = new CountDownLatch(NUM_REQUESTS);

        // Fire new request to fetch movie reviews data.
        NetworkUtility.addToRequestQueue(this,
                new StringRequest(NetworkUtility.buildReviewsURL(mMovieData.id).toString(),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                mMovieReviews = JsonUtility.expandResultsString(response);

                                // Wait for all requests to finish.
                                SYNC_LATCH.countDown();

                                Log.d(TAG, "onResponse(reviews): " + response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }));

        // Fire new request to fetch movie video data.
        NetworkUtility.addToRequestQueue(this,
                new StringRequest(NetworkUtility.buildVideoURL(mMovieData.id).toString(),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    // Extract result objects from results array.
                                    String[] results = JsonUtility.expandResultsString(response);

                                    if(results.length > 0) {
                                        // Arbitrarily use the first trailer in the array.
                                        JSONObject responseObject = new JSONObject(results[0]);
                                        mMovieTrailerIdentifier = responseObject.getString("key");
                                    } else {
                                        // No trailers found for this movie.
                                        // Assign trailer identifier to null.
                                        mMovieTrailerIdentifier = null;
                                    }
                                } catch (JSONException e) {
                                    Log.e(TAG, "onResponse: " + e.getMessage());
                                }

                                // Wait for all requests to finish.
                                SYNC_LATCH.countDown();

                                //
                                Log.d(TAG, "onResponse(videos): " + response);

                                // Call callback.
                                onFinishDataLoad();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }));

        setDetailsFromMovieData(mMovieData);
    }

    /**
     * Fills in UI with data provided from a given movie.
     * @param movieData The Movie data to fill UI with.
     */
    private void setDetailsFromMovieData(Movie movieData) {
        detailsActivityBinding.tvMovieTitle.setText(movieData.title);
        PicassoUtility.loadPosterImageIntoImageView(movieData, detailsActivityBinding.ivPosterDetails);

        /** Release Date and Ratings **/
        final String assembledRatingsString = String.valueOf(movieData.ratings) + " / 10";
        detailsActivityBinding.detailsRatingsReleaseDate.tvRatings.setText(assembledRatingsString);

        try {
            // First parse the release date string using the expected format.
            SimpleDateFormat startingDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date releaseDate = startingDateFormat.parse(movieData.releaseDate);

            // Parse the Date object to the desired format.
            SimpleDateFormat desiredDateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
            detailsActivityBinding.detailsRatingsReleaseDate.tvReleaseDate.setText(desiredDateFormat.format(releaseDate));
        } catch (ParseException e) {
            Log.e(TAG, "onCreate: " + e.getMessage());
        }

        /** Synopsis **/
        detailsActivityBinding.detailsSynopsis.tvSynopsis.setText(movieData.overview);
    }

    /**
     * Callback function after all Volley requests finish fetching data for this movie.
     */
    public void onFinishDataLoad() {
        // Populate reviews with acquired data.
        if(mMovieReviews != null && mMovieReviews.length > 0) {
            mMovieReviewsAdapter.setReviewData(mMovieReviews);
            // Hide the no reviews text.
            detailsActivityBinding.detailsReviews.tvNoReviews.setVisibility(View.GONE);

            // Show the reviews recycler view.
            detailsActivityBinding.detailsReviews.rvReviews.setVisibility(View.VISIBLE);
        }

        // Hide loading bar.
        detailsActivityBinding.pbLoading.setVisibility(View.INVISIBLE);

        // Show all detail views.
        detailsActivityBinding.detailsView.setVisibility(View.VISIBLE);
    }

    /**
     * Checks if cached movie is a favorite.
     * @return True if movie is a favorite, false otherwise.
     */
    private boolean isMovieFavorite() {
        return mMovieData.isFavorite;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);

        // Set favorite star icon to reflect isFavorite.
        if (isMovieFavorite()){
            menu.findItem(R.id.favorite_star).setIcon(R.drawable.ic_star);
        } else{
            menu.findItem(R.id.favorite_star).setIcon(R.drawable.ic_star_o);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.favorite_star:
                // Toggle movie data's isFavorite.
                mMovieData.isFavorite = !mMovieData.isFavorite;

                StringBuilder toastTextBuilder =
                        new StringBuilder()
                        .append(mMovieData.title);

                // Change star icon to reflect favorite change.
                if(isMovieFavorite()) {
                    item.setIcon(R.drawable.ic_star);
                    toastTextBuilder.append(" is now a favorite!");
                } else {
                    item.setIcon(R.drawable.ic_star_o);
                    toastTextBuilder.append(" is no longer a favorite.");
                }

                // Cancel previous duplicate operation, if running.
                queryHandler.cancelOperation(MovieQueryHandler.UPDATE_FAVORITES_TOKEN);

                // Asynchronously update favorite to database.
                ContentValues updatedFavorites = new ContentValues();
                updatedFavorites.put(MovieContract.MovieEntry.COLUMN_FAVORITE, mMovieData.isFavorite);
                queryHandler.startUpdate(MovieQueryHandler.UPDATE_FAVORITES_TOKEN,
                        null,
                        MovieContract.MovieEntry.CONTENT_URI,
                        updatedFavorites,
                        MovieContract.MovieEntry.COLUMN_ID + " = " + String.valueOf(mMovieData.id),
                        null);

                // Inform user that they have changed their favorite settings for this movie.
                Toast.makeText(this, toastTextBuilder.toString(), Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }
    }

    /**
     * Callback when "Share" button is selected.
     */
    public void onShareTrailer(View view) {
        // If a trailer was found, launch an intent to share it.
        if(mMovieTrailerIdentifier != null) {
            // Create new intent to share the trailer.
            ShareCompat.IntentBuilder shareIntentBuilder = ShareCompat.IntentBuilder.from(this);
            shareIntentBuilder.setType("text/plain")
                    .setText(NetworkUtility.buildYoutubeTrailerUri(mMovieTrailerIdentifier).toString());

            Intent builtIntent = shareIntentBuilder.getIntent();
            // If "share video" intent can be handled, start it.
            if (builtIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(builtIntent);
            }
        } else {
            // No trailer found!
            onNoTrailerFound();
        }
    }

    /**
     * Callback when "Play" button is selected.
     */
    public void onPlayTrailer(View view) {
        // If a trailer was found, launch an intent to view it.
        if(mMovieTrailerIdentifier != null) {
            // Create new intent to play trailer on Youtube.
            Intent playTrailerIntent = new Intent(Intent.ACTION_VIEW);
            playTrailerIntent.setData(NetworkUtility.buildYoutubeTrailerUri(mMovieTrailerIdentifier));

            // If "play video" intent can be handled, start it.
            if (playTrailerIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(playTrailerIntent);
            }
        } else {
            // No trailer found!
            onNoTrailerFound();
        }
    }

    /**
     * Called when interacting with buttons when no trailer was cached.
     */
    private void onNoTrailerFound() {
        Toast.makeText(this, "No trailer found for this movie!", Toast.LENGTH_LONG).show();
    }

}
