package com.example.android.movifo.sync;

import com.example.android.movifo.util.NetworkUtility;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by Carl on 4/10/2017.
 */

public class MovieFirebaseJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters job) {
        // Invoked job will sync movie data in background.
        SyncMovieDataTask.syncMovieData(this);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        // Stop all requests if the job is terminated.
        NetworkUtility.clearQueue();
        return false;
    }
}
