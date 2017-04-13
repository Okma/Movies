package com.example.android.movifo.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.tjeannin.provigen.ProviGenOpenHelper;
import com.tjeannin.provigen.ProviGenProvider;
import com.tjeannin.provigen.model.Contract;

/**
 * Created by Carl on 4/10/2017.
 */

public class MovieContentProvider extends ProviGenProvider {

    // Define array of contract classes to populate database with.
    private static Class[] contracts = new Class[]{MovieContract.MovieEntry.class};

    private static final String DATABASE_NAME = "movies.db";
    private int DATABASE_VERSION = 3;

    @Override
    public SQLiteOpenHelper openHelper(Context context) {
        return new ProviGenOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION, contracts);
    }

    @Override
    public Class[] contractClasses() {
        return contracts;
    }


}
