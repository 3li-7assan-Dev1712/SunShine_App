package com.example.sunshine;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

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
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

