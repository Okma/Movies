package com.example.android.movifo.data;

import android.net.Uri;

import com.tjeannin.provigen.ProviGenBaseContract;
import com.tjeannin.provigen.annotation.Column;
import com.tjeannin.provigen.annotation.ContentUri;
import com.tjeannin.provigen.annotation.Id;

/**
 * Movie database contract class.
 * Created by Carl on 4/10/2017.
 */

public final class MovieContract {

    private static final String URI_SPECIFIER = "content://";
    private static final String CONTENT_AUTHORITY = "com.example.android.movifo";

    private static final Uri BASE_CONTENT_URI = Uri.parse(URI_SPECIFIER + CONTENT_AUTHORITY);

    public interface MovieEntry extends ProviGenBaseContract {

        String TABLE_NAME = "movies";

        @ContentUri
        Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        @Column(Column.Type.INTEGER)
        String COLUMN_ID = "id";

        @Column(Column.Type.TEXT)
        String COLUMN_OVERVIEW = "overview";

        @Column(Column.Type.TEXT)
        String COLUMN_RELEASE_DATE = "release_date";

        @Column(Column.Type.REAL)
        String COLUMN_RATINGS = "ratings";

        @Column(Column.Type.REAL)
        String COLUMN_POPULARITY = "popularity";

        @Column(Column.Type.TEXT)
        String COLUMN_TITLE = "title";

        @Column(Column.Type.TEXT)
        String COLUMN_POSTER_PATH = "poster_path";

        @Column(Column.Type.INTEGER)
        String COLUMN_ADULT = "is_adult";

        @Column(Column.Type.INTEGER)
        String COLUMN_FAVORITE = "is_favorite";
    }

}
