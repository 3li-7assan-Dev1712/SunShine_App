package com.example.sunshine;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.example.sunshine.data.SunshinePreferences;
import com.example.sunshine.data.WeatherContract;
import com.example.sunshine.fragment.DetailWeatherFragment;
import com.example.sunshine.utilities.SunshineDateUtils;
import com.example.sunshine.utilities.SunshineWeatherUtils;
import com.squareup.picasso.Picasso;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class SunshineIntentService extends IntentService {

    private static final String ACTION_UPDATE_WIDGET = "com.example.sunshine.action.update_widget";
    private static final int ID_FOR_LOADER = 10;
    private static final String TAG = SunshineIntentService.class.getSimpleName();

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
        String location = SunshinePreferences.getPreferredWeatherLocation(this);
        long todayDateInMillis = SunshinePreferences.getTodayDateInMillis(this);
        Uri requestUri = WeatherContract.WeatherEntry.buildWeatherUriWithDate(todayDateInMillis);
        Cursor data = getContentResolver().query(requestUri,
                DetailWeatherFragment.WEATHER_DETAIL_PROJECTION,
                null,
                null,
                null);
        if (data != null && data.moveToFirst()){
            long localDateMidnightGmt = data.getLong(DetailWeatherFragment.INDEX_WEATHER_DATE);

            String dateText = SunshineDateUtils.getFriendlyDateString(this, localDateMidnightGmt, true);
            String realDescription= data.getString(DetailWeatherFragment.INDEX_WEATHER_DESCRIPTION);
            String icon="https:" + data.getString(DetailWeatherFragment.INDEX_WEATHER_ICON);
            double highInCelsius = data.getDouble(DetailWeatherFragment.INDEX_WEATHER_MAX_TEMP);
            String highString = SunshineWeatherUtils.formatTemperature(this, highInCelsius);
            double lowInCelsius = data.getDouble(DetailWeatherFragment.INDEX_WEATHER_MIN_TEMP);
            String lowString = SunshineWeatherUtils.formatTemperature(this, lowInCelsius);
            float humidity = data.getFloat(DetailWeatherFragment.INDEX_WEATHER_HUMIDITY);
            String humidityString = getString(R.string.format_humidity, humidity);
            float windSpeed = data.getFloat(DetailWeatherFragment.INDEX_WEATHER_WIND_SPEED);
            float windDirection = data.getFloat(DetailWeatherFragment.INDEX_WEATHER_DEGREES);
            String windString = SunshineWeatherUtils.getFormattedWind(this, windSpeed, windDirection);
            /*Using a bundle because there's is gonna be a lot of params*/
            Bundle weatherDataBundle = new Bundle();
            weatherDataBundle.putString(getString(R.string.location), location);
            weatherDataBundle.putString(getString(R.string.dateText), dateText);
            weatherDataBundle.putString(getString(R.string.realDescription), realDescription);
            weatherDataBundle.putString(getString(R.string.icon), icon);
            weatherDataBundle.putString(getString(R.string.highString), highString);
            weatherDataBundle.putString(getString(R.string.lowString), lowString);
            weatherDataBundle.putString(getString(R.string.humidityString), humidityString);
            weatherDataBundle.putString(getString(R.string.windString), windString);
            weatherDataBundle.putParcelable(getString(R.string.uri), requestUri);

            AppWidgetManager manager = AppWidgetManager.getInstance(this);
            int[] widgetIds = manager.getAppWidgetIds(new ComponentName(this, SunshineWidget.class));
            SunshineWidget.updateSunshineWidget(this,
                    manager,
                    widgetIds,
                    weatherDataBundle);
            data.close();
            Log.d(TAG, "updated widget successfullly");
        }else{
            throw new NullPointerException("Cursor must not be null in the service");
        }

    }
}
