package com.example.android.movifo.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * Created by Carl on 4/10/2017.
 */

public class MovieContentProvider extends ContentProvider {

    private SQLiteOpenHelper mOpenHelper;
    private UriMatcher mUriMatcher;

    private final int MOVIE_URI_CODE = 100;
    private final int MOVIE_ID_URI_CODE = 101;
    
    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDBHelper(getContext());

        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.MovieEntry.TABLE_NAME, MOVIE_URI_CODE);
        mUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.MovieEntry.TABLE_NAME + "/#", MOVIE_ID_URI_CODE);
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (mUriMatcher.match(uri)) {
            case MOVIE_URI_CODE:
                return "vnd.android.cursor.dir/vdn." + MovieContract.MovieEntry.TABLE_NAME;
            case MOVIE_ID_URI_CODE:
                return "vnd.android.cursor.item/vdn." + MovieContract.MovieEntry.TABLE_NAME;
            default:
                throw new IllegalArgumentException("Unknown uri " + uri);
        }
    }
    
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = mOpenHelper.getReadableDatabase();
        Cursor cursor;

        switch (mUriMatcher.match(uri)) {
            case MOVIE_URI_CODE:
                cursor = database.query(MovieContract.MovieEntry.TABLE_NAME, projection, selection, selectionArgs, "", "", sortOrder);
                break;
            case MOVIE_ID_URI_CODE:
                String itemId = String.valueOf(ContentUris.parseId(uri));
                if (TextUtils.isEmpty(selection)) {
                    cursor = database.query(MovieContract.MovieEntry.TABLE_NAME, projection, MovieContract.MovieEntry._ID + " = ? ", new String[]{itemId}, "", "", sortOrder);
                } else {
                    cursor = database.query(MovieContract.MovieEntry.TABLE_NAME, projection, selection + " AND " + MovieContract.MovieEntry._ID + " = ? ",
                            appendToStringArray(selectionArgs, itemId), "", "", sortOrder);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown uri " + uri);
        }

        // Make sure that potential listeners are getting notified.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase database = mOpenHelper.getWritableDatabase();

        switch (mUriMatcher.match(uri)) {
            case MOVIE_URI_CODE:
                long itemId = database.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return Uri.withAppendedPath(uri, String.valueOf(itemId));
            default:
                throw new IllegalArgumentException("Unknown uri " + uri);
        }
    }
    
    // Not implemented.
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mOpenHelper.getWritableDatabase();
        int numberOfRowsAffected = 0;

        switch (mUriMatcher.match(uri)) {
            case MOVIE_URI_CODE:
                numberOfRowsAffected = database.update(MovieContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case MOVIE_ID_URI_CODE:
                // Extract last path segment
                String movieId = String.valueOf(ContentUris.parseId(uri));
                if (TextUtils.isEmpty(selection)) {
                    numberOfRowsAffected = database.update(MovieContract.MovieEntry.TABLE_NAME, values, MovieContract.MovieEntry._ID + " = ? ", new String[]{movieId});
                } else {
                    numberOfRowsAffected = database.update(MovieContract.MovieEntry.TABLE_NAME, values, selection + " AND " + MovieContract.MovieEntry._ID + " = ? ",
                            appendToStringArray(selectionArgs, movieId));
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown uri " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return numberOfRowsAffected;
    }

    private String[] appendToStringArray(String[] selectionArgs, String newArg) {
        String[] copyOfArgs = new String[selectionArgs.length + 1];
        System.arraycopy(selectionArgs, 0, copyOfArgs, 0, selectionArgs.length);
        copyOfArgs[selectionArgs.length] = newArg;
        return copyOfArgs;
    }
}
