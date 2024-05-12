package com.example.tile_shop_application;

import android.app.job.JobParameters;
import android.app.job.JobService;

public class NotificationJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        new NotificationHandler(getApplicationContext())
                .send("Új csempék érkeztek! Ne maradj le róluk!");
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
