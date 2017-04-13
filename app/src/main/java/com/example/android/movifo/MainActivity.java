package com.example.android.movifo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.preference.PreferenceManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.android.movifo.data.Movie;
import com.example.android.movifo.data.MovieContract;
import com.example.android.movifo.databinding.ActivityMainBinding;
import com.example.android.movifo.sync.SyncMovieDataTask;
import com.example.android.movifo.util.DeviceUtility;

import static com.example.android.movifo.data.Movie.MOVIE_DATA_EXTRA;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        SharedPreferences.OnSharedPreferenceChangeListener, MovieAdapter.MovieAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ActivityMainBinding mainActivityBinding;
    private MovieAdapter mMovieAdapter;

    private static final int MOVIE_LOADER_ID = 9;

    // Flag for detecting if the app is in the foreground.
    public static boolean bApplicationInForeground = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivityBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // Create movie adapter for rendering poster images and touch events.
        mMovieAdapter = new MovieAdapter(this, this);

        final int NUM_GRID_COLUMNS =
                DeviceUtility.getDeviceOrientation(this) == Configuration.ORIENTATION_LANDSCAPE ?
                        getResources().getInteger(R.integer.num_posters_land) :
                        getResources().getInteger(R.integer.num_posters_port);

        // Establish grid layout for recycler view.
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, NUM_GRID_COLUMNS);
        mainActivityBinding.rvMoviePosters.setLayoutManager(gridLayoutManager);

        // Assign movie adapter.
        mainActivityBinding.rvMoviePosters.setAdapter(mMovieAdapter);

        // Ensure movie data is synced properly.
        SyncMovieDataTask.initializeSyncService(this);

        // Initialize cursor content loader.
        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);

        // Register MainActivity as a shared preference listener.
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bApplicationInForeground = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        bApplicationInForeground = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister MainActivity as a shared preference listener.
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Acquire menu inflater to inflate settings menu.
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Settings menu was clicked.
            case R.id.settings_menu:
                // Create explicit intent to move to SettingsActivity.
                Intent openSettings = new Intent(this, SettingsActivity.class);
                startActivity(openSettings);
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        switch(id) {
            case MOVIE_LOADER_ID:
                // Establish sorting parameter based on sorting preference.
                String sortPreference = sharedPreferences.getString(getString(R.string.sort_preference_key), getString(R.string.sort_preference_most_popular_key));

                // Optionally assigned sortOrder and/or selection, depending on preferences.
                String sortBy = null;
                String selection = null;

                if(sortPreference.equals(getString(R.string.sort_preference_most_popular_key))) {
                    sortBy = MovieContract.MovieEntry.COLUMN_POPULARITY;
                } else if (sortPreference.equals(getString(R.string.sort_preference_highest_rated_key))){
                    sortBy = MovieContract.MovieEntry.COLUMN_RATINGS;
                } else {
                    // Show only movies where isFavorite is true.
                    selection = MovieContract.MovieEntry.COLUMN_FAVORITE + " = 1";
                }

                return new CursorLoader(this,
                        MovieContract.MovieEntry.CONTENT_URI,
                        null,
                        selection,
                        null,
                        sortBy);
            default:
                throw new RuntimeException("Loader with id " + String.valueOf(id) + " is not implemented!");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMovieAdapter.setMovieData(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.setMovieData(null);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // If sort preference was changed, re-sync movie data to new sort preference.
        if(key.equals(getString(R.string.sort_preference_key))) {
            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
        }
    }

    @Override
    public void onImageClick(Movie movie) {
        // Create explicit intent to transition to details page with movie data as extra.
        Intent goToDetailsIntent = new Intent(this, DetailsActivity.class);
        goToDetailsIntent.putExtra(MOVIE_DATA_EXTRA, Movie.createJsonFromMovie(movie));
        startActivity(goToDetailsIntent);
    }
}
