package com.example.android.movifo.sync;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;

/**
 * Custom {@link AsyncQueryHandler} for updating database off main thread.
 * Created by Carl on 4/11/2017.
 */

public class MovieQueryHandler extends AsyncQueryHandler {

    public static final int UPDATE_FAVORITES_TOKEN = 101;

    public MovieQueryHandler(ContentResolver cr) {
        super(cr);
    }

    @Override
    protected void onUpdateComplete(int token, Object cookie, int result) {
        super.onUpdateComplete(token, cookie, result);
    }
}
