package com.example.sunshine.utilities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.example.sunshine.MainActivity;
import com.example.sunshine.R;
import com.example.sunshine.data.SunshinePreferences;
import com.example.sunshine.data.WeatherContract;

public class NotificationUtils {


    private static final String ALI_NOTIFICATION = "Notify";
    public static int weatherId;
    public static double high;
    public static double low;
    /*
     * The columns of data that we are interested in displaying within our notification to let
     * the user know there is new weather data available.
     */
    public static final String[] WEATHER_NOTIFICATION_PROJECTION = {
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
    };


    /*
     * We store the indices of the values in the array of Strings above to more quickly be able
     * to access the data from our query. If the order of the Strings above changes, these
     * indices must be adjusted to match the order of the Strings.
     */
    public static final int INDEX_WEATHER_ID = 0;
    public static final int INDEX_MAX_TEMP = 1;
    public static final int INDEX_MIN_TEMP = 2;

    /*
     * This notification ID can be used to access our notification after we've displayed it. This
     * can be handy when we need to cancel the notification, or perhaps update it. This number is
     * arbitrary and can be set to whatever you like. 3004 is in no way significant.
     */
//  COMPLETED (1) Create a constant int value to identify the notification
    private static final int WEATHER_NOTIFICATION_ID = 3004;
    private static final String SUNSHINE_REMINDER = "sunshine-reminder";

    static Uri todaysWeatherUri= WeatherContract.WeatherEntry
            .buildWeatherUriWithDate(SunshineDateUtils.normalizeDate(System.currentTimeMillis()));
    /**
     * Constructs and displays a notification for the newly updated weather for today.
     *
     * @param context Context used to query our ContentProvider and use various Utility methods
     */
    public static void notifyUserOfNewWeather(final Context context) {
        if (allowNotification(context))
        {
            /* Build the URI for today's weather in order to show up to date data in notification */


            new AsyncTask<Void, Void, Void>(){

                @Override
                protected Void doInBackground(Void... voids) {
                    Log.d("Notification", "doInBackground");
                    Cursor cursor = context.getContentResolver().query(
                            todaysWeatherUri,
                            WEATHER_NOTIFICATION_PROJECTION,
                            null,
                            null,
                            null);
                    if (cursor == null)
                        Log.d("NotificationUtils.class", "cursor = : null");
                    if (cursor != null && cursor.moveToFirst()) {
                        weatherId = cursor.getInt(INDEX_WEATHER_ID);
                        high = cursor.getDouble(INDEX_MAX_TEMP);

                        Log.d("NotificationUtils.class", "cursor = : not null");
                        low = cursor.getDouble(INDEX_MIN_TEMP);
                        Log.d("NotificationUtils.class", "low: " + low + "high " + high + "weather id = " + weatherId);

                    }

                    return null;
                }

            }.execute();

            /*
             * The MAIN_FORECAST_PROJECTION array passed in as the second parameter is defined in our WeatherContract
             * class and is used to limit the columns returned in our cursor.
             */
            int largeArtResourceId = SunshineWeatherUtils
                    .getLargeArtResourceIdForWeatherCondition(weatherId);
            Resources rec = context.getResources();
            Bitmap largeIcon = BitmapFactory.decodeResource(rec,largeArtResourceId );

            String notificationTitle = context.getString(R.string.app_name);

            String notificationText = getNotificationText(context, weatherId, high, low);

            /* getSmallArtResourceIdForWeatherCondition returns the proper art to show given an ID */
            int smallArtResourceId = SunshineWeatherUtils
                    .getSmallArtResourceIdForWeatherCondition(weatherId);

            /*
             * NotificationCompat Builder is a very convenient way to build backward-compatible
             * notifications. In order to use it, we provide a context and specify a color for the
             * notification, a couple of different icons, the title for the notification, and
             * finally the text of the notification, which in our case in a summary of today's
             * forecast.
             */
            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
//          COMPLETED (2) Use NotificationCompat.Builder to begin building the notification
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(SUNSHINE_REMINDER,
                        context.getResources().getString(R.string.main_notification_channel_name),
                        NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(notificationChannel);
            }
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, SUNSHINE_REMINDER)
                    .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .setSmallIcon(smallArtResourceId)
                    .setLargeIcon(largeIcon)
                    .setContentTitle(notificationTitle)
                    .setContentText(notificationText)
                    .setAutoCancel(true);

//          COMPLETED (3) Create an Intent with the proper URI to start the DetailActivity
            /*
             * This Intent will be triggered when the user clicks the notification. In our case,
             * we want to open Sunshine to the DetailActivity to display the newly updated weather.
             */

//          COMPLETED (4) Use TaskStackBuilder to create the proper PendingINtent
//                    TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
//                    taskStackBuilder.addNextIntentWithParentStack(detailIntentForToday);
//                    PendingIntent resultPendingIntent = taskStackBuilder
//                            .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

//          COMPLETED (5) Set the content Intent of the NotificationBuilder
            notificationBuilder.setContentIntent(contentIntent(context));

//          COMPLETED (6) Get a reference to the NotificationManager
//content://com.example.sunshine/weather/1613606400000
//Uri from detail is: content://com.example.sunshine/weather/1613606400000
            //
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
            }
//          COMPLETED (7) Notify the user with the ID WEATHER_NOTIFICATION_ID
            /* WEATHER_NOTIFICATION_ID allows you to update or cancel the notification later on */
            Log.d("NotificationUtils.class", "" + notificationText);
            notificationManager.notify(WEATHER_NOTIFICATION_ID, notificationBuilder.build());

//          COMPLETED (8) Save the time at which the notification occurred using SunshinePreferences
            /*
             * Since we just showed a notification, save the current time. That way, we can check
             * next time the weather is refreshed if we should show another notification.
             */
            Log.d("Notification", "onPostExecute Notified successfully");

            SunshinePreferences.saveLastNotificationTime(context, System.currentTimeMillis());
        }else {
            Toast.makeText(context, "Sorry, to show notification please allow them first from the app settings", Toast.LENGTH_SHORT).show();
        }


    }

    /**
     * Constructs and returns the summary of a particular day's forecast using various utility
     * methods and resources for formatting. This method is only used to create the text for the
     * notification that appears when the weather is refreshed.
     * <p>
     * The String returned from this method will look something like this:
     * <p>
     * Forecast: Sunny - High: 14°C Low 7°C
     *
     * @param context   Used to access utility methods and resources
     * @param weatherId ID as determined by Open Weather Map
     * @param high      High temperature (either celsius or fahrenheit depending on preferences)
     * @param low       Low temperature (either celsius or fahrenheit depending on preferences)
     * @return Summary of a particular day's forecast
     */
    private static String getNotificationText(Context context, int weatherId, double high, double low) {

        /*
         * Short description of the weather, as provided by the API.
         * e.g "clear" vs "sky is clear".
         */
//        = SunshineWeatherUtils
//                .getStringForWeatherCondition(context, weatherId);

        String shortDescription = context.getString(R.string.light_rain);
        String notificationFormat = context.getString(R.string.format_notification);

        /* Using String's format method, we create the forecast summary */
        String notificationText = String.format(notificationFormat,
                shortDescription,
                SunshineWeatherUtils.formatTemperature(context, high),
                SunshineWeatherUtils.formatTemperature(context, low));

        return notificationText;
    }
    public static PendingIntent contentIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);

        return PendingIntent.getActivity(context,
                WEATHER_NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT // using the same PendingIntent and update the older one in reusing it:)
        );
    }

    public Bitmap largeIcon(Context context){

        Resources rec = context.getResources();
        Bitmap largeIcon = BitmapFactory.decodeResource(rec, R.drawable.ic_local_drink_black_24px);
        return largeIcon;
    }
    public static boolean allowNotification(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(context.getString(R.string.pref_enable_notifications_key), context.getResources().getBoolean(R.bool.show_notifications_by_default));
    }
}
