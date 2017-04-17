package com.example.android.movifo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Carl on 4/17/2017.
 */

public class MovieDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 3;

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE =
                "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +
                        MovieContract.MovieEntry._ID               + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MovieContract.MovieEntry.COLUMN_TITLE       + " TEXT NOT NULL, "            +
                        MovieContract.MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, "               +
                        MovieContract.MovieEntry.COLUMN_ID   + " INTEGER NOT NULL, "                +
                        MovieContract.MovieEntry.COLUMN_RELEASE_DATE   + " TEXT NOT NULL, "         +
                        MovieContract.MovieEntry.COLUMN_RATINGS   + " REAL NOT NULL, "              +
                        MovieContract.MovieEntry.COLUMN_POPULARITY   + " REAL NOT NULL, "           +
                        MovieContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, "            +
                        MovieContract.MovieEntry.COLUMN_ADULT    + " INTEGER NOT NULL, "            +
                        MovieContract.MovieEntry.COLUMN_FAVORITE    + " INTEGER NOT NULL, "            +
                        MovieContract.MovieEntry.COLUMN_SORT_METHOD + " TEXT NOT NULL, "            +
                        "ON CONFLICT REPLACE);";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
