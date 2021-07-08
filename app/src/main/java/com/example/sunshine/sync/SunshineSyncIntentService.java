package com.example.sunshine.sync;

import android.app.IntentService;
import android.content.Intent;

public class SunshineSyncIntentService extends IntentService {

    //  COMPLETED (6) Create a constructor that calls super and passes the name of this class
    public SunshineSyncIntentService() {
        super("SunshineSyncIntentService");
    }

    //  COMPLETED (7) Override onHandleIntent, and within it, call SunshineSyncTask.syncWeather
    @Override
    protected void onHandleIntent(Intent intent) {
        SunshineSyncTask.syncWeather(this);
    }
}
