package com.example.sunshine.utilities;


import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();
    private static final String BASE_REAL_OPEN_WEATHER_URL =
            "https://api.weatherapi.com/v1/forecast.json";

    private static final String aqi = "no";
    private static final String alerts = "no";
    private static final String aqi_key = "aqi";
    private static final String alerts_key = "alerts";
    private static final int days_number = 10;
    private static final String days = "days";
    private static final String key = "key";
    private static final String api_key = "3bee1fa297304b5883052008210906";

    private static final String QUERY_PARAM = "q";



    public static URL getOpenWeatherUrl (String query){
        Uri openWeatherUri = Uri.parse(BASE_REAL_OPEN_WEATHER_URL).buildUpon()
                .appendQueryParameter(key, api_key)
                .appendQueryParameter(QUERY_PARAM, query)
                .appendQueryParameter(days, Integer.toString(days_number))
                .appendQueryParameter(aqi_key, aqi)
                .appendQueryParameter(alerts_key, alerts).build();

        try {
            URL openWeatherUrl = new URL(openWeatherUri.toString());
            Log.v(TAG, "OPEN WEATHER URL: " + openWeatherUrl);
            return openWeatherUrl;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*this method is responsible for getting the json response from the open weather server*/
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            String response = null;
            if (hasInput) {
                response = scanner.next();
            }
            scanner.close();
            return response;
        } finally {
            urlConnection.disconnect();
        }
    }
}
