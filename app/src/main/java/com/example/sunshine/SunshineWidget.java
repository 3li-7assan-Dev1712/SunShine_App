package com.example.sunshine;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.os.Bundle;
import android.widget.RemoteViews;

import androidx.annotation.MainThread;

import com.squareup.picasso.Picasso;

/**
 * Implementation of App Widget functionality.
 */
public class SunshineWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {


        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.today_widget_layout);
        views.setImageViewResource(R.id.appwidget_image, R.drawable.art_clear);
        views.setTextViewText(R.id.appwidget_location_text, "Sudan, Khartoum");
        views.setTextViewText(R.id.appwidget_min_temp, "20");
        views.setTextViewText(R.id.appwidget_max_temp, "36");
        views.setTextViewText(R.id.appwidget_humidity, "80%");
        views.setTextViewText(R.id.appwidget_wind_speed, "27km/h N");
        views.setTextViewText(R.id.appwidget_description, "Partly cloudy");

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        SunshineIntentService.startActionUpdateWeatherWidget(context);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public static void updateSunshineWidget(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, Bundle bundle){
        for (int appWidgetId : appWidgetIds) {
            updateSunshineWidget(context, appWidgetManager, appWidgetId, bundle);
        }
    }

    public static void updateSunshineWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle bundle){
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.today_widget_layout);

        String location = bundle.getString(context.getString(R.string.location));
        String dateString = bundle.getString(context.getString(R.string.dateText));
        String realDescription = bundle.getString(context.getString(R.string.realDescription));
        String icon = bundle.getString(context.getString(R.string.icon));
        String highString = bundle.getString(context.getString(R.string.highString));
        String lowString = bundle.getString(context.getString(R.string.lowString));
        String humidityString = bundle.getString(context.getString(R.string.humidityString));
        String windString = bundle.getString(context.getString(R.string.windString));

        views.setTextViewText(R.id.appwidget_location_text, location);
        views.setTextViewText(R.id.appwidget_description, realDescription);

        Picasso.get().load(icon).into(views, R.id.appwidget_image, new int[] {appWidgetId});
        views.setTextViewText(R.id.appwidget_date_text, dateString);
        views.setTextViewText(R.id.appwidget_max_temp, highString);
        views.setTextViewText(R.id.appwidget_min_temp, lowString);
        views.setTextViewText(R.id.appwidget_humidity, humidityString);
        views.setTextViewText(R.id.appwidget_wind_speed, windString);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}

