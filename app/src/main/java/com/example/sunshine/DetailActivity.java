package com.example.sunshine;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.app.ShareCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunshine.data.WeatherContract;
import com.example.sunshine.utilities.SunshineDateUtils;
import com.example.sunshine.utilities.SunshineWeatherUtils;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private Uri mUri;
    private final int ID_FOR_LOADER = 100;

    private TextView mDateView;
    private TextView mDescriptionView;
    private TextView mHighTemperatureView;
    private TextView mLowTemperatureView;
    private TextView mHumidityView;
    private TextView mWindView;
    private TextView mPressureView;
    private ImageView mWeatherIcon;

    private String mForecastSummary;

    public static final String[] WEATHER_DETAIL_PROJECTION = {
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    };
    //  COMPLETED (19) Create constant int values representing each column name's position above
    /*
     * We store the indices of the values in the array of Strings above to more quickly be able
     * to access the data from our query. If the order of the Strings above changes, these
     * indices must be adjusted to match the order of the Strings.
     */
    public static final int INDEX_WEATHER_DATE = 0;
    public static final int INDEX_WEATHER_MAX_TEMP = 1;
    public static final int INDEX_WEATHER_MIN_TEMP = 2;
    public static final int INDEX_WEATHER_HUMIDITY = 3;
    public static final int INDEX_WEATHER_PRESSURE = 4;
    public static final int INDEX_WEATHER_WIND_SPEED = 5;
    public static final int INDEX_WEATHER_DEGREES = 6;
    public static final int INDEX_WEATHER_CONDITION_ID = 7;

    String FORECAST_SHARE_HASHTAG = " #SunShineApp";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mDateView = findViewById(R.id.date);
        mDescriptionView = findViewById(R.id.weather_description);
        mHighTemperatureView = findViewById(R.id.high_temperature);
        mLowTemperatureView = findViewById(R.id.low_temperature);
        mHumidityView = findViewById(R.id.humidity);
        mWindView = findViewById(R.id.wind_measurement);
        mPressureView = findViewById(R.id.pressure);
        mWeatherIcon = findViewById(R.id.weather_icon);

        mUri = getIntent().getData();
//      COMPLETED (17) Throw a NullPointerException if that URI is null
        if (mUri == null) throw new NullPointerException("URI for DetailActivity cannot be null");
        getSupportLoaderManager().initLoader(ID_FOR_LOADER, null, this);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int item_which_clicked = item.getItemId();
        switch (item_which_clicked){
            case R.id.settings_from_detail:
                Intent goToSettingIntent = new Intent(this, SettingsActivity.class);
                startActivity(goToSettingIntent);
                break;
            case R.id.share_id:
                Intent intent = createShareForecastIntent();
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
        }
        return super.onOptionsItemSelected(item);
    }
    private Intent createShareForecastIntent() {
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText("weatherOfTheSelectedDay" + FORECAST_SHARE_HASHTAG)
                .getIntent();
        return shareIntent;
    }
/*
content://com.example.sunshine/weather/1613260800000
 */
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Log.d("DetailActivity.class", "Uri from detail is: " + mUri.toString());
        // we make sure that the loader for our id
        switch (id){
            case ID_FOR_LOADER:
                return new CursorLoader(this,
                        mUri,
                        WEATHER_DETAIL_PROJECTION,
                        null,
                        null,
                        null);
            default:
                throw new UnsupportedOperationException("Cannot get data in background thread using this id of another ininsiliszed loader :)");
        }

    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
//      COMPLETED (25) Check before doing anything that the Cursor has valid data
        /*
         * Before we bind the data to the UI that will display that data, we need to check the
         * cursor to make sure we have the results that we are expecting. In order to do that, we
         * check to make sure the cursor is not null and then we call moveToFirst on the cursor.
         * Although it may not seem obvious at first, moveToFirst will return true if it contains
         * a valid first row of data.
         *
         * If we have valid data, we want to continue on to bind that data to the UI. If we don't
         * have any data to bind, we just return from this method.
         */
        boolean cursorHasValidData = false;
        if (data != null && data.moveToFirst()) {
            /* We have valid data, continue on to bind the data to the UI */
            cursorHasValidData = true;
        }

        if (!cursorHasValidData) {
            /* No data to display, simply return and do nothing */
            return;
        }

//      COMPLETED (26) Display a readable data string
        /****************
         * Weather Date *
         ****************/
        /*
         * Read the date from the cursor. It is important to note that the date from the cursor
         * is the same date from the weather SQL table. The date that is stored is a GMT
         * representation at midnight of the date when the weather information was loaded for.
         *
         * When displaying this date, one must add the GMT offset (in milliseconds) to acquire
         * the date representation for the local date in local time.
         * SunshineDateUtils#getFriendlyDateString takes care of this for us.
         */


        long localDateMidnightGmt = data.getLong(INDEX_WEATHER_DATE);
        String dateText = SunshineDateUtils.getFriendlyDateString(this, localDateMidnightGmt, true);

        mDateView.setText(dateText);

//      COMPLETED (27) Display the weather description (using SunshineWeatherUtils)
        /***********************
         * Weather Description *
         ***********************/
        /* Read weather condition ID from the cursor (ID provided by Open Weather Map) */
        int weatherId = data.getInt(INDEX_WEATHER_CONDITION_ID);
        /* Use the weatherId to obtain the proper description */
        String description = SunshineWeatherUtils.getStringForWeatherCondition(this, weatherId);

        /* Set the text */
        mDescriptionView.setText(description);

//      COMPLETED (28) Display the high temperature
        /**************************
         * High (max) temperature *
         **************************/
        /* Read high temperature from the cursor (in degrees celsius) */
        double highInCelsius = data.getDouble(INDEX_WEATHER_MAX_TEMP);
        Log.i("DetailActivity.class", "High Temperature in celsius: " + highInCelsius);
        /*
         * If the user's preference for weather is fahrenheit, formatTemperature will convert
         * the temperature. This method will also append either °C or °F to the temperature
         * String.
         */
        String highString = SunshineWeatherUtils.formatTemperature(this, highInCelsius);


        /* Set the text */
        mHighTemperatureView.setText(highString);

//      COMPLETED (29) Display the low temperature
        /*************************
         * Low (min) temperature *
         *************************/
        /* Read low temperature from the cursor (in degrees celsius) */
        double lowInCelsius = data.getDouble(INDEX_WEATHER_MIN_TEMP);
        /*
         * If the user's preference for weather is fahrenheit, formatTemperature will convert
         * the temperature. This method will also append either °C or °F to the temperature
         * String.
         */
        String lowString = SunshineWeatherUtils.formatTemperature(this, lowInCelsius);

        /* Set the text */
        mLowTemperatureView.setText(lowString);

//      COMPLETED (30) Display the humidity
        /************
         * Humidity *
         ************/
        /* Read humidity from the cursor */
        float humidity = data.getFloat(INDEX_WEATHER_HUMIDITY);
        String humidityString = getString(R.string.format_humidity, humidity);

        /* Set the text */
        mHumidityView.setText(humidityString);

//      COMPLETED (31) Display the wind speed and direction
        /****************************
         * Wind speed and direction *
         ****************************/
        /* Read wind speed (in MPH) and direction (in compass degrees) from the cursor  */
        float windSpeed = data.getFloat(INDEX_WEATHER_WIND_SPEED);
        float windDirection = data.getFloat(INDEX_WEATHER_DEGREES);
        String windString = SunshineWeatherUtils.getFormattedWind(this, windSpeed, windDirection);

        /* Set the text */
        mWindView.setText(windString);

        mWeatherIcon.setImageResource(SunshineWeatherUtils.getSmallArtResourceIdForWeatherCondition(weatherId));
//      COMPLETED (32) Display the pressure
        /************
         * Pressure *
         ************/
        /* Read pressure from the cursor */
        float pressure = data.getFloat(INDEX_WEATHER_PRESSURE);

        /*
         * Format the pressure text using string resources. The reason we directly access
         * resources using getString rather than using a method from SunshineWeatherUtils as
         * we have for other data displayed in this Activity is because there is no
         * additional logic that needs to be considered in order to properly display the
         * pressure.
         */
        String pressureString = getString(R.string.format_pressure, pressure);

        /* Set the text */
        mPressureView.setText(pressureString);

//      COMPLETED (33) Store a forecast summary in mForecastSummary
        /* Store the forecast summary String in our forecast summary field to share later */
        mForecastSummary = String.format("%s - %s - %s/%s",
                dateText, description, highString, lowString);
    }

//  COMPLETED (34) Override onLoaderReset, but don't do anything in it yet
    /**
     * Called when a previously created loader is being reset, thus making its data unavailable.
     * The application should at this point remove any references it has to the Loader's data.
     * Since we don't store any of this cursor's data, there are no references we need to remove.
     *
     * @param loader The Loader that is being reset.
     */

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}