package com.example.android.movifo.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * An {@link IntentService} for handling asynchronous movie job tasks.
 * Created by Carl on 4/10/2017.
 */

public class MovieIntentService extends IntentService {

    public MovieIntentService() {
        super(MovieIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        MovieTasks.executeTask(this, intent.getAction());
    }
}
