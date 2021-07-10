package com.example.sunshine.utilities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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



    public static double high;
    public static double low;
    public static String description;
    public static String icon;

    public static final String[] WEATHER_NOTIFICATION_PROJECTION = {
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_DESCRIPTION,
            WeatherContract.WeatherEntry.COLUMN_ICON
    };


    public static final int INDEX_MAX_TEMP = 0;
    public static final int INDEX_MIN_TEMP = 1;
    public static final int INDEX_DESCRIPTION= 2;
    public static final int INDEX_ICON= 3;

    private static final int WEATHER_NOTIFICATION_ID = 3004;
    private static final String SUNSHINE_REMINDER = "sunshine-reminder";

    static Uri todaysWeatherUri= WeatherContract.WeatherEntry.CONTENT_URI;
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

    public static boolean allowNotification(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(context.getString(R.string.pref_enable_notifications_key), context.getResources().getBoolean(R.bool.show_notifications_by_default));
    }
}
