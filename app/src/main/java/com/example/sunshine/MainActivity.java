package com.example.sunshine;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.sunshine.data.SunshinePreferences;
import com.example.sunshine.data.WeatherContract;
import com.example.sunshine.fragment.DetailWeatherFragment;
import com.example.sunshine.sync.SunShineSyncUtils;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements ForecastAdapter.ForecastAdapterOnClickHandler {

    private final String TAG = MainActivity.class.getSimpleName();
    private boolean mTowPane = false;

    /*
     * The columns of data that we are interested in displaying within our MainActivity's list of
     * weather data.
     */
    public static final String[] MAIN_FORECAST_PROJECTION = {
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_DESCRIPTION,
            WeatherContract.WeatherEntry.COLUMN_ICON,
    };

    /*
     * We store the indices of the values in the array of Strings above to more quickly be able to
     * access the data from our query. If the order of the Strings above changes, these indices
     * must be adjusted to match the order of the Strings.
     */
    public static final int INDEX_WEATHER_DATE = 0;
    public static final int INDEX_WEATHER_MAX_TEMP = 1;
    public static final int INDEX_WEATHER_MIN_TEMP = 2;
    public static final int INDEX_WEATHER_DESCRIPTION = 3;
    public static final int INDEX_WEATHER_ICON= 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null){
            getSupportActionBar().setElevation(0f);
        }
        SunshinePreferences.setPreferredWeatherLocation(this, "Khartoum, Sudan");
        SunShineSyncUtils.initialize(this);
        View view = findViewById(R.id.separater_view);
        if (view != null) {
            mTowPane = true;
            Log.d(TAG, "mTwoPane: " + mTowPane);
        }
        Log.d(TAG, "mTwoPane: " + mTowPane);
        if (mTowPane) {
            DetailWeatherFragment weatherFragment = new DetailWeatherFragment();
            long todayDateInMillis = SunshinePreferences.getTodayDateInMillis(this);
            Uri todayUri = WeatherContract.WeatherEntry.buildWeatherUriWithDate(todayDateInMillis);
            weatherFragment.setmUri(todayUri);
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().add(R.id.detail_fragment_containter, weatherFragment).commit();
        }
    }

    private void openPreferredLocationInMap() {
        float[] coords = SunshinePreferences.getLocationCoordinates(this);

        Uri geoLocation = Uri.parse("geo:" + coords[0] + "," + coords[1]);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(TAG, "Couldn't call " + geoLocation.toString() + ", no receiving apps installed!");
        }
    }


    @Override
    public void onClick(long date) {
        if (mTowPane){
            DetailWeatherFragment weatherFragment = new DetailWeatherFragment();
            Uri todayUri = WeatherContract.WeatherEntry.buildWeatherUriWithDate(date);
            weatherFragment.setmUri(todayUri);
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.detail_fragment_containter, weatherFragment).commit();
        }else {
            Intent weatherDetailIntent = new Intent(MainActivity.this, DetailActivity.class);
            Uri uriForDateClicked = WeatherContract.WeatherEntry.buildWeatherUriWithDate(date);
            weatherDetailIntent.setData(uriForDateClicked);
            startActivity(weatherDetailIntent);
        }
    }

    /**
     * This is where we inflate and set up the menu for this Activity.
     *
     * @param menu The options menu in which you place your items.
     *
     * @return You must return true for the menu to be displayed;
     *         if you return false it will not be shown.
     *
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.main, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    /**
     * Callback invoked when a menu item was selected from this Activity's menu.
     *
     * @param item The menu item that was selected by the user
     *
     * @return true if you handle the menu click here, false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.go_to_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        else if (id == R.id.open_map) {
            openPreferredLocationInMap();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}