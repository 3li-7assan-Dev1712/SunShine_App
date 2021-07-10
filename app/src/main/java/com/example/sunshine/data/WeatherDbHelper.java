package com.example.sunshine.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.sunshine.data.WeatherContract.WeatherEntry;

public class WeatherDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "weather.db";
    public static final int DATABASE_VERSION = 10; // update the database to delete the old data and replace them by up-to-date one.
    public WeatherDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        /*
         * This String will contain a simple SQL statement that will create a table that will
         * cache our weather data.
         */
        final String SQL_CREATE_WEATHER_TABLE =

                "CREATE TABLE " + WeatherEntry.TABLE_NAME + " (" +

                        /*
                         * WeatherEntry did not explicitly declare a column called "_ID". However,
                         * WeatherEntry implements the interface, "BaseColumns", which does have a field
                         * named "_ID". We use that here to designate our table's primary key.
                         */
                        WeatherEntry._ID               + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        WeatherEntry.COLUMN_DATE       + " INTEGER NOT NULL, "                 +

                        WeatherEntry.COLUMN_MIN_TEMP   + " REAL NOT NULL, "                    +
                        WeatherEntry.COLUMN_MAX_TEMP   + " REAL NOT NULL, "                    +

                        WeatherEntry.COLUMN_HUMIDITY   + " REAL NOT NULL, "                    +

                        WeatherEntry.COLUMN_WIND_SPEED + " REAL NOT NULL, "                    +
                        WeatherEntry.COLUMN_DEGREES    + " REAL NOT NULL, "                    +

                        WeatherEntry.COLUMN_DESCRIPTION   + " REAL NOT NULL, "                 +
                        WeatherEntry.COLUMN_ICON   + " REAL NOT NULL, "                        +
                        /*
                         * To ensure this table can only contain one weather entry per date, we declare
                         * the date column to be unique. We also specify "ON CONFLICT REPLACE". This tells
                         * SQLite that if we have a weather entry for a certain date and we attempt to
                         * insert another weather entry with that date, we replace the old weather entry.
                         */
                        " UNIQUE (" + WeatherEntry.COLUMN_DATE + ") ON CONFLICT REPLACE);";

        /*
         * After we've spelled out our SQLite table creation statement above, we actually execute
         * that SQL with the execSQL method of our SQLite database object.
         */
        /*
        so know i stopped in working in the developing in android developing
        i was coding in network how get data in real time
         */
        db.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + WeatherEntry.TABLE_NAME);
        onCreate(db);
    }
}
