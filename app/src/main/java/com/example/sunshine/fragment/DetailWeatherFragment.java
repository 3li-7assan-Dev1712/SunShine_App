package com.example.sunshine.fragment;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.example.sunshine.R;
import com.example.sunshine.data.WeatherContract;
import com.example.sunshine.utilities.SunshineDateUtils;
import com.example.sunshine.utilities.SunshineWeatherUtils;
import com.squareup.picasso.Picasso;

public class DetailWeatherFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private Uri mUri;
    private final int ID_FOR_LOADER = 100;

    private TextView mDateView;
    private TextView mDescriptionView;
    private TextView mHighTemperatureView;
    private TextView mLowTemperatureView;
    private TextView mHumidityView;
    private TextView mWindView;
    private ImageView mWeatherIcon;
    public static final String[] WEATHER_DETAIL_PROJECTION = {
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_DESCRIPTION,
            WeatherContract.WeatherEntry.COLUMN_ICON,
    };

    public static final int INDEX_WEATHER_DATE = 0;
    public static final int INDEX_WEATHER_MAX_TEMP = 1;
    public static final int INDEX_WEATHER_MIN_TEMP = 2;
    public static final int INDEX_WEATHER_HUMIDITY = 3;
    public static final int INDEX_WEATHER_WIND_SPEED = 4;
    public static final int INDEX_WEATHER_DEGREES = 5;
    public static final int INDEX_WEATHER_DESCRIPTION = 6;
    public static final int INDEX_WEATHER_ICON = 7;

    String FORECAST_SHARE_HASHTAG = " #SunShineApp";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View detailFragmentView;
        Display display =  ((WindowManager) getContext().getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int rot = display.getRotation();
        if (rot == Surface.ROTATION_90 || rot == Surface.ROTATION_180) {
            detailFragmentView = inflater.inflate(R.layout.weather_detail_fragment_land, container, false);
        }else{
            detailFragmentView = inflater.inflate(R.layout.detail_weather_fragment, container, false);
        }

        if (savedInstanceState != null){
            mUri = savedInstanceState.getParcelable("uri");
        }
        mDateView = detailFragmentView.findViewById(R.id.date);
        mDescriptionView = detailFragmentView.findViewById(R.id.weather_description);
        mHighTemperatureView = detailFragmentView.findViewById(R.id.high_temperature);
        mLowTemperatureView = detailFragmentView.findViewById(R.id.low_temperature);
        mHumidityView = detailFragmentView.findViewById(R.id.humidity);
        mWindView = detailFragmentView.findViewById(R.id.wind_measurement);
        mWeatherIcon = detailFragmentView.findViewById(R.id.weather_icon);
        if (mUri == null) throw new NullPointerException("URI for DetailActivity cannot be null");
        LoaderManager.getInstance(this).initLoader(ID_FOR_LOADER, null, this);
        return detailFragmentView;
    }

    public DetailWeatherFragment() {
        /*empty constructor for creating the fragment*/
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Log.d("DetailActivity.class", "Uri from detail is: " + mUri.toString());
        // we make sure that the loader for our id
        switch (id){
            case ID_FOR_LOADER:
                return new CursorLoader(getContext(),
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
        boolean cursorHasValidData = false;
        if (data != null && data.moveToFirst()) {
            /* We have valid data, continue on to bind the data to the UI */
            cursorHasValidData = true;
        }

        if (!cursorHasValidData) {
            /* No data to display, simply return and do nothing */
            return;
        }


        long localDateMidnightGmt = data.getLong(INDEX_WEATHER_DATE);

        String dateText = SunshineDateUtils.getFriendlyDateString(getContext(), localDateMidnightGmt, true);
        mDateView.setText(dateText);
        /* Set the text */
        String realDescription= data.getString(INDEX_WEATHER_DESCRIPTION);
        String icon="https:" + data.getString(INDEX_WEATHER_ICON);
        Picasso.get().load(icon).into(mWeatherIcon);
        mDescriptionView.setText(realDescription);
        double highInCelsius = data.getDouble(INDEX_WEATHER_MAX_TEMP);
        Log.i("DetailActivity.class", "High Temperature in celsius: " + highInCelsius);
        String highString = SunshineWeatherUtils.formatTemperature(getContext(), highInCelsius);
        /* Set the text */
        mHighTemperatureView.setText(highString);
        /* Read low temperature from the cursor (in degrees celsius) */
        double lowInCelsius = data.getDouble(INDEX_WEATHER_MIN_TEMP);
        /*
         * If the user's preference for weather is fahrenheit, formatTemperature will convert
         * the temperature. This method will also append either °C or °F to the temperature
         * String.
         */
        String lowString = SunshineWeatherUtils.formatTemperature(getContext(), lowInCelsius);
        /* Set the text */
        mLowTemperatureView.setText(lowString);
        /* Read humidity from the cursor */
        float humidity = data.getFloat(INDEX_WEATHER_HUMIDITY);
        String humidityString = getString(R.string.format_humidity, humidity);
        /* Set the text */
        mHumidityView.setText(humidityString);
        /* Read wind speed (in MPH) and direction (in compass degrees) from the cursor  */
        float windSpeed = data.getFloat(INDEX_WEATHER_WIND_SPEED);
        float windDirection = data.getFloat(INDEX_WEATHER_DEGREES);
        String windString = SunshineWeatherUtils.getFormattedWind(getContext(), windSpeed, windDirection);
        /* Set the text */
        mWindView.setText(windString);

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable("uri", mUri);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        // will not have a use case.
    }

    public void setmUri(Uri mUri) {
        this.mUri = mUri;
    }
}
