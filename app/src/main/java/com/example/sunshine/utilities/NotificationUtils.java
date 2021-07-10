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
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class NotificationUtils {


    private static final String ALI_NOTIFICATION = "Notify";

    public static double high;
    public static double low;
    public static String description;
    public static String icon;

    /*
     * The columns of data that we are interested in displaying within our notification to let
     * the user know there is new weather data available.
     */
    public static final String[] WEATHER_NOTIFICATION_PROJECTION = {
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_DESCRIPTION,
            WeatherContract.WeatherEntry.COLUMN_ICON
    };


    /*
     * We store the indices of the values in the array of Strings above to more quickly be able
     * to access the data from our query. If the order of the Strings above changes, these
     * indices must be adjusted to match the order of the Strings.
     */
    public static final int INDEX_MAX_TEMP = 0;
    public static final int INDEX_MIN_TEMP = 1;
    public static final int INDEX_DESCRIPTION= 2;
    public static final int INDEX_ICON= 3;

    /*
     * This notification ID can be used to access our notification after we've displayed it. This
     * can be handy when we need to cancel the notification, or perhaps update it. This number is
     * arbitrary and can be set to whatever you like. 3004 is in no way significant.
     */
//  COMPLETED (1) Create a constant int value to identify the notification
    private static final int WEATHER_NOTIFICATION_ID = 3004;
    private static final String SUNSHINE_REMINDER = "sunshine-reminder";

    static Uri todaysWeatherUri= WeatherContract.WeatherEntry.CONTENT_URI;
    /**
     * Constructs and displays a notification for the newly updated weather for today.
     *
     * @param context Context used to query our ContentProvider and use various Utility methods
     */
    public static void notifyUserOfNewWeather(final Context context) throws IOException {
        if (allowNotification(context))
        {
            final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, SUNSHINE_REMINDER);
            /* Build the URI for today's weather in order to show up to date data in notification */



            new  AsyncTask<Void, Void, Void>(){

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
                        high = cursor.getDouble(INDEX_MAX_TEMP);
                        description = cursor.getString(INDEX_DESCRIPTION);
                        icon = cursor.getString(INDEX_ICON);
                        Log.d("NotificationUtils.class", "cursor = : not null");
                        low = cursor.getDouble(INDEX_MIN_TEMP);
                        try {
                            notificationBuilder.setLargeIcon(Picasso.get().load("https:" + icon).get());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    assert cursor != null;
                    cursor.close();
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    String notificationTitle = context.getString(R.string.app_name);

                    String notificationText = getNotificationText(context, description, high, low);

                    NotificationManager notificationManager = (NotificationManager)
                            context.getSystemService(Context.NOTIFICATION_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel notificationChannel = new NotificationChannel(SUNSHINE_REMINDER,
                                context.getResources().getString(R.string.main_notification_channel_name),
                                NotificationManager.IMPORTANCE_HIGH);
                        notificationManager.createNotificationChannel(notificationChannel);
                    }
//                    NotificationCompat.Builder notificationBuilder = null;
//                        notificationBuilder = new NotificationCompat.Builder(context, SUNSHINE_REMINDER)
                    notificationBuilder
                            .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                            .setSmallIcon(R.drawable.art_clear)
                            .setContentTitle(notificationTitle)
                            .setContentText(notificationText)
                            .setAutoCancel(true);

                    notificationBuilder.setContentIntent(contentIntent(context));
                    //
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                        notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
                    }
                    Log.d("NotificationUtils.class", "" + notificationText);
                    notificationManager.notify(WEATHER_NOTIFICATION_ID, notificationBuilder.build());
                    Log.d("Notification", "onPostExecute Notified successfully");

                    SunshinePreferences.saveLastNotificationTime(context, System.currentTimeMillis());
                }

                private String getNotificationText(Context context, String description, double high, double low) {

                    String notificationFormat = context.getString(R.string.format_notification);

                    /* Using String's format method, we create the forecast summary */
                    String notificationText = String.format(notificationFormat,
                            description,
                            SunshineWeatherUtils.formatTemperature(context, high),
                            SunshineWeatherUtils.formatTemperature(context, low));

                    return notificationText;
                }
                public PendingIntent contentIntent(Context context) {
                    Intent intent = new Intent(context, MainActivity.class);

                    return PendingIntent.getActivity(context,
                            WEATHER_NOTIFICATION_ID,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT // using the same PendingIntent and update the older one in reusing it:)
                    );
                }

            }.execute();

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
     * @param high      High temperature (either celsius or fahrenheit depending on preferences)
     * @param low       Low temperature (either celsius or fahrenheit depending on preferences)
     * @return Summary of a particular day's forecast
     */
    private static String getNotificationText(Context context, String description, double high, double low) {

        String notificationFormat = context.getString(R.string.format_notification);

        /* Using String's format method, we create the forecast summary */
        String notificationText = String.format(notificationFormat,
                description,
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
