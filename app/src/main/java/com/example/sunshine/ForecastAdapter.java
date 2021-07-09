package com.example.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunshine.utilities.SunshineDateUtils;
import com.example.sunshine.utilities.SunshineWeatherUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;


public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {

    /* The context we use to utility methods, app resources and layout inflaters */
    private final Context mContext;

    private final int TODAY_LAYOUT_ID = 1;
    private final int NORMAL_LAYOUT_ID = 2;


    /*
     * Below, we've defined an interface to handle clicks on items within this Adapter. In the
     * constructor of our ForecastAdapter, we receive an instance of a class that has implemented
     * said interface. We store that instance in this variable to call the onClick method whenever
     * an item is clicked in the list.
     */
    final private ForecastAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface ForecastAdapterOnClickHandler {
        void onClick(long date);
    }

    private Cursor mCursor;

    /**
     * Creates a ForecastAdapter.
     *
     * @param context Used to talk to the UI and app resources
     * @param clickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    public ForecastAdapter(@NonNull Context context, ForecastAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
           return TODAY_LAYOUT_ID;
        else
            return NORMAL_LAYOUT_ID;
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (like ours does) you
     *                  can use this viewType integer to provide a different layout. See
     *
     *                  for more details.
     * @return A new ForecastAdapterViewHolder that holds the View for each list item
     */

    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = null;
        if (viewType == TODAY_LAYOUT_ID){
            view = LayoutInflater
                    .from(mContext)
                    .inflate(R.layout.list_item_forecast_today, viewGroup, false);
        }

        else if (viewType == NORMAL_LAYOUT_ID){
            view = LayoutInflater
                    .from(mContext)
                    .inflate(R.layout.forecast_list_item, viewGroup, false);
        }
        else {
            try {
                throw new IllegalAccessException("Cannot recocnize the id of the item view type");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return new ForecastAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the weather
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param forecastAdapterViewHolder The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ForecastAdapterViewHolder forecastAdapterViewHolder, int position) {
        mCursor.moveToPosition(position);


        /*******************
         * Weather Summary *
         *******************/
        /* Read date from the cursor */
        long dateInMillis = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
        /* Get human readable string using our utility method */
        String dateString = SunshineDateUtils.getFriendlyDateString(mContext, dateInMillis, false);
        /* Use the weatherId to obtain the proper description */
        int weatherId = mCursor.getInt(MainActivity.INDEX_WEATHER_CONDITION_ID);
        String description = SunshineWeatherUtils.getStringForWeatherCondition(mContext, weatherId);
        /* Read high temperature from the cursor (in degrees celsius) */
        double highInCelsius = mCursor.getDouble(MainActivity.INDEX_WEATHER_MAX_TEMP);
        /* Read low temperature from the cursor (in degrees celsius) */
        double lowInCelsius = mCursor.getDouble(MainActivity.INDEX_WEATHER_MIN_TEMP);

        String realDescription = mCursor.getString(MainActivity.INDEX_WEATHER_DESCRIPTION);
        String iconUrl = mCursor.getString(MainActivity.INDEX_WEATHER_ICON);

        Picasso.get().load("https:" +iconUrl)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        try{
                            String root = Environment.getExternalStorageDirectory().toString();
                            File myDir= new File(root, "/sunshine");
                            if (!myDir.exists()){
                                myDir.mkdirs();
                            }
                            String name = new Date().toString() + ".jpg";
                            myDir = new File(myDir, name);
                            FileOutputStream out = new FileOutputStream(myDir);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                            out.flush();
                            out.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
//        String highAndLowTemperature =
//                SunshineWeatherUtils.formatHighLows(mContext, highInCelsius, lowInCelsius);

//        String weatherSummary = dateString + " - " + description + " - " + highAndLowTemperature;


        forecastAdapterViewHolder.dateTextView.setText(dateString);
        forecastAdapterViewHolder.descriptionTextView.setText(realDescription);
        forecastAdapterViewHolder.iconImageView.setImageResource(SunshineWeatherUtils.getSmallArtResourceIdForWeatherCondition(weatherId));
        forecastAdapterViewHolder.hightWeatherTextView.setText(SunshineWeatherUtils.formatTemperature(mContext, highInCelsius));
        forecastAdapterViewHolder.lowWeatherTexaView.setText(SunshineWeatherUtils.formatTemperature(mContext, lowInCelsius));


    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our forecast
     */
    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    /**
     * Swaps the cursor used by the ForecastAdapter for its weather data. This method is called by
     * MainActivity after a load has finished, as well as when the Loader responsible for loading
     * the weather data is reset. When this method is called, we assume we have a completely new
     * set of data, so we call notifyDataSetChanged to tell the RecyclerView to update.
     *
     * @param newCursor the new cursor to use as ForecastAdapter's data source
     */
    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    /**
     * A ViewHolder is a required part of the pattern for RecyclerViews. It mostly behaves as
     * a cache of the child views for a forecast item. It's also a convenient place to set an
     * OnClickListener, since it has access to the adapter and the views.
     */
    class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView dateTextView;
        final TextView descriptionTextView;
        final TextView hightWeatherTextView;
        final TextView lowWeatherTexaView;
        final ImageView iconImageView;

        ForecastAdapterViewHolder(View view) {
            super(view);

            dateTextView = view.findViewById(R.id.date);
            descriptionTextView = view.findViewById(R.id.weather_description);
            hightWeatherTextView = view.findViewById(R.id.high_temperature);
            lowWeatherTexaView = view.findViewById(R.id.low_temperature);
            iconImageView = view.findViewById(R.id.weather_icon);
//            weatherSummary = (TextView) view.findViewById(R.id.tv_weather_data);

            view.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click. We fetch the date that has been
         * selected, and then call the onClick handler registered with this adapter, passing that
         * date.
         *
         * @param v the View that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            long dateInMillis = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
            mClickHandler.onClick(dateInMillis);
        }
    }
}