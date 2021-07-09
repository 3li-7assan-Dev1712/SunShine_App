package com.example.sunshine.data;

import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.example.sunshine.utilities.SunshineDateUtils;

public class WeatherContract {

    public static final String CONTENT_AUTHORITY= "com.example.sunshine";
    public static final String PATH_WEATHER = "weather";
    public static final Uri BASE_URI_WEATHER= Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class WeatherEntry implements BaseColumns{


        /*
         Do steps 2 through 10 within the WeatherEntry class

         */

        public static Uri CONTENT_URI = BASE_URI_WEATHER.buildUpon().appendPath(PATH_WEATHER).build();

        public static final String TABLE_NAME = "weather";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_WEATHER_ID = "weather_id";
        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_MAX_TEMP = "max";
        public static final String COLUMN_HUMIDITY = "humidity";
        public static final String COLUMN_PRESSURE = "pressure";
        public static final String COLUMN_WIND_SPEED = "wind";
        public static final String COLUMN_DEGREES = "degrees";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_ICON = "icon";

        /*
        this method is responsible for getting the current time in milli seconds so we can compare
        it and get any time  that we want
        the current time is represented in long type
        for instance 16(hours) * 60(minutes) * 60(seconds) * 1000(milli seconds) {60,420,000}

        then we return a value of date>= current time in milli seconds :)
         */
        public static String getSqlSelectForTodayOnwards() {
            long normalizedUtcNow = SunshineDateUtils.normalizeDate(System.currentTimeMillis());
            Log.d("WeatehrContrac.class", "Current time in milli seconds: " + normalizedUtcNow);
            return WeatherContract.WeatherEntry.COLUMN_DATE + " >= " + normalizedUtcNow;
        }

        public static Uri buildWeatherUriWithDate(long date) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(date))
                    .build();
        }
    }

}
