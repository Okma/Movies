package com.example.android.movifo.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

/**
 * JSON movie result reflection class.
 * Acts as a middle-man object.
 * Can be adapted to and from database cursor and JSON.
 * Created by Carl on 4/9/2017.
 */

public class Movie {

    // Singleton GSON object instance for JSON object reflection.
    private static final Gson GSON = new Gson();

    public static final String MOVIE_DATA_EXTRA = "movie_data";

    // Default constructor for GSON.
    public Movie() {}

    /** Movie data attributes. **/
    public int id;

    public String overview;

    @SerializedName("release_date")
    public String releaseDate;

    @SerializedName("original_title")
    public String title;

    public float popularity;

    @SerializedName("vote_average")
    public float ratings;

    @SerializedName("poster_path")
    public String posterPath;

    @SerializedName("adult")
    public boolean isAdult;

    // Movie is not "favorited" by default.
    public boolean isFavorite = false;

    public String sortMethod;

    /**
     * Uses GSON to parse a given JSON string to a movie.
     * @param jsonString The JSON string representation of the given movie.
     * @return The movie reference converted from JSON.
     */
    public static Movie createMovieFromJson(String jsonString) {
        return GSON.fromJson(jsonString, Movie.class);
    }

    /**
     * Uses GSON to parse a given movie to a JSON string.
     * @param movie The provided movie reference to convert to JSON.
     * @return The JSON string representation of the given movie.
     */
    public static String createJsonFromMovie(@NonNull Movie movie) {
        return GSON.toJson(movie);
    }

    /**
     * Generates ContentValues for this movie.
     * @return ContentValues wrapping this movie's data.
     */
    public ContentValues generateContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.COLUMN_ID, this.id);
        contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, this.overview);
        contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, this.releaseDate);
        contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, this.title);
        contentValues.put(MovieContract.MovieEntry.COLUMN_RATINGS, this.ratings);
        contentValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, this.popularity);
        contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, this.posterPath);
        contentValues.put(MovieContract.MovieEntry.COLUMN_ADULT, this.isAdult);
        contentValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, this.isFavorite);
        contentValues.put(MovieContract.MovieEntry.COLUMN_SORT_METHOD, this.sortMethod);
        return contentValues;
    }

    /**
     * Creates a movie from a database cursor.
     * @return The created movie.
     */
    public static Movie createMovieFromCursor(Cursor cursor) {
        Movie movie = new Movie();
        movie.id = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ID));
        movie.overview = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW));
        movie.title = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
        movie.releaseDate = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE));
        movie.ratings = cursor.getFloat(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATINGS));
        movie.popularity = cursor.getFloat(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POPULARITY));
        movie.posterPath = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH));
        movie.isAdult = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ADULT)) > 0;
        movie.isFavorite = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_FAVORITE)) > 0;
        movie.sortMethod = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_SORT_METHOD));
        return movie;
    }
}
