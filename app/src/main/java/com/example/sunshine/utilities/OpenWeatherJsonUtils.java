package com.example.sunshine.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.example.sunshine.data.SunshinePreferences;
import com.example.sunshine.data.WeatherContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class OpenWeatherJsonUtils {

    private static final String TAG = OpenWeatherJsonUtils.class.toString();

    public static ContentValues[] getRealWeatherData (String forecastResponse, Context context) throws JSONException {
        long normalizedUtcStartDay = SunshineDateUtils.getNormalizedUtcDateForToday();
        JSONObject jsonObject = new JSONObject(forecastResponse);
        JSONObject locationObj = jsonObject.getJSONObject(JsonConstants.location);
        float lat = (float) locationObj.getDouble(SunshinePreferences.PREF_COORD_LAT);
        float lon = (float) locationObj.getDouble(SunshinePreferences.PREF_COORD_LONG);
        SunshinePreferences.setLocationCoordinates(context, lat, lon);
        JSONObject forecastObj = jsonObject.getJSONObject(JsonConstants.forecast);
        JSONArray forecastArray = forecastObj.getJSONArray(JsonConstants.forecast_day);
        ContentValues[] weatherContentValues = new ContentValues[forecastArray.length()];
        for (int i = 0; i < forecastArray.length(); i++){
            JSONObject weatherObj = forecastArray.getJSONObject(i);

            long dateInMillis = normalizedUtcStartDay + SunshineDateUtils.DAY_IN_MILLIS * i;

            JSONObject dayObj = weatherObj.getJSONObject(JsonConstants.day);
            double maxtemp_c = dayObj.getDouble(JsonConstants.maxtemp_c);
            double mintemp_c = dayObj.getDouble(JsonConstants.mintemp_c);
            double maxwind_kph = dayObj.getDouble(JsonConstants.maxwind_kph);
            String humidity = dayObj.getString(JsonConstants.humidity);
            long debugDate = weatherObj.getLong(JsonConstants.date_epoch) * 1000;
            Log.v(TAG, "date from api: " + debugDate);
            Log.v(TAG, "date from device: " + dateInMillis);
            JSONObject conditionObj = dayObj.getJSONObject(JsonConstants.condition);
            String description = conditionObj.getString(JsonConstants.text);
            String iconUrl = conditionObj.getString(JsonConstants.icon);
            ContentValues weatherValues = new ContentValues();
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATE, debugDate);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, humidity);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, maxwind_kph);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, 0);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, maxtemp_c);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, mintemp_c);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DESCRIPTION, description);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_ICON, iconUrl);

            weatherContentValues[i] = weatherValues;
        }
        return weatherContentValues;
    }
}
