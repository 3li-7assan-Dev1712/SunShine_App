package com.example.sunshine.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;

import com.example.sunshine.data.SunshinePreferences;
import com.example.sunshine.data.WeatherContract;
import com.example.sunshine.utilities.JsonConstants;
import com.example.sunshine.utilities.NetworkUtils;
import com.example.sunshine.utilities.NotificationUtils;
import com.example.sunshine.utilities.OpenWeatherJsonUtils;

import java.net.URL;

public class SunshineSyncTask {

    /**
     * Performs the network request for updated weather, parses the JSON from that request, and
     * inserts the new weather information into our ContentProvider. Will notify the user that new
     * weather has been loaded if the user hasn't been notified of the weather within the last day
     * AND they haven't disabled notifications in the preferences screen.
     *
     * @param context Used to access utility methods and the ContentResolver
     */
    synchronized public static void syncWeather(Context context) {

        try {
            /*
             * The getUrl method will return the URL that we need to get the forecast JSON for the
             * weather. It will decide whether to create a URL based off of the latitude and
             * longitude or off of a simple location as a String.
             */

            String location = SunshinePreferences.getPreferredWeatherLocation(context);
            URL realWeatherRequestUrl = NetworkUtils.getOpenWeatherUrl(location);

            /* Use the URL to retrieve the JSON */
            assert realWeatherRequestUrl != null;
            String reaJsonWeatherResponse = NetworkUtils.getResponseFromHttpUrl(realWeatherRequestUrl);
            /* Parse the JSON into a list of weather values */
            ContentValues[] realWeatherValues = OpenWeatherJsonUtils
                    .getRealWeatherData(reaJsonWeatherResponse, context);

            /*
             * In cases where our JSON contained an error code, getWeatherContentValuesFromJson
             * would have returned null. We need to check for those cases here to prevent any
             * NullPointerExceptions being thrown. We also have no reason to insert fresh data if
             * there isn't any to insert.
             */
            if (realWeatherValues.length != 0) {
                /* Get a handle on the ContentResolver to delete and insert data */
                ContentResolver sunshineContentResolver = context.getContentResolver();

                /* Delete old weather data because we don't need to keep multiple days' data */
                sunshineContentResolver.delete(
                        WeatherContract.WeatherEntry.CONTENT_URI,
                        null,
                        null);

                /* Insert our new weather data into Sunshine's ContentProvider */
                sunshineContentResolver.bulkInsert(
                        WeatherContract.WeatherEntry.CONTENT_URI,
                        realWeatherValues);

                /*
                 * Finally, after we insert data into the ContentProvider, determine whether or not
                 * we should notify the user that the weather has been refreshed.
                 */
                boolean notificationsEnabled = SunshinePreferences.areNotificationsEnabled(context);

                /*
                 * If the last notification was shown was more than 1 day ago, we want to send
                 * another notification to the user that the weather has been updated. Remember,
                 * it's important that you shouldn't spam your users with notifications.
                 */
                long timeSinceLastNotification = SunshinePreferences
                        .getEllapsedTimeSinceLastNotification(context);
//
                boolean oneDayPassedSinceLastNotification = true;

                if (timeSinceLastNotification >= DateUtils.DAY_IN_MILLIS) {
                    oneDayPassedSinceLastNotification = true;
                }

                /*
                 * We only want to show the notification if the user wants them shown and we
                 * haven't shown a notification in the past day.
                 */

                if (notificationsEnabled && oneDayPassedSinceLastNotification) {
                    Log.d("Any", "just wait and will show notification");
                    NotificationUtils.notifyUserOfNewWeather(context);
                }

                /* If the code reaches this point, we have successfully performed our sync */

            }

        } catch (Exception e) {
            /* Server probably invalid */
            e.printStackTrace();
        }
    }
}
