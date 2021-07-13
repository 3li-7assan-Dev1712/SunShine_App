package com.example.sunshine;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class SunshineIntentService extends IntentService {

    private static final String ACTION_UPDATE_WIDGET = "com.example.sunshine.action.update_widget";

    public SunshineIntentService() {
        super("SunshineIntentService");
    }



    public static void startActionUpdateWeatherWidget(Context context){
        Intent intent = new Intent(context, SunshineIntentService.class);
        intent.setAction(ACTION_UPDATE_WIDGET);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_WIDGET.equals(action)) {
                handleActionUpdateWeatherWidget();
            }
        }
    }



    private void handleActionUpdateWeatherWidget(){

    }
}
