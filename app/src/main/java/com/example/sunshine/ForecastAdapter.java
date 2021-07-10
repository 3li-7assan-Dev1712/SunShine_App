package com.example.sunshine;

import android.content.Context;
import android.database.Cursor;
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


public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {

    /* The context we use to utility methods, app resources and layout inflaters */
    private final Context mContext;

    private final int TODAY_LAYOUT_ID = 1;
    private final int NORMAL_LAYOUT_ID = 2;



    final private ForecastAdapterOnClickHandler mClickHandler;

    public interface ForecastAdapterOnClickHandler {
        void onClick(long date);
    }

    private Cursor mCursor;

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

    @Override
    public void onBindViewHolder(@NonNull ForecastAdapterViewHolder forecastAdapterViewHolder, int position) {
        mCursor.moveToPosition(position);


        long dateInMillis = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
        /* Get human readable string using our utility method */
        String dateString = SunshineDateUtils.getFriendlyDateString(mContext, dateInMillis, false);

        /* Read high temperature from the cursor (in degrees celsius) */
        double highInCelsius = mCursor.getDouble(MainActivity.INDEX_WEATHER_MAX_TEMP);
        /* Read low temperature from the cursor (in degrees celsius) */
        double lowInCelsius = mCursor.getDouble(MainActivity.INDEX_WEATHER_MIN_TEMP);

        String realDescription = mCursor.getString(MainActivity.INDEX_WEATHER_DESCRIPTION);
        String iconUrl = mCursor.getString(MainActivity.INDEX_WEATHER_ICON);
        Picasso.get().load("https:"+iconUrl).into(forecastAdapterViewHolder.iconImageView);
        forecastAdapterViewHolder.dateTextView.setText(dateString);
        forecastAdapterViewHolder.descriptionTextView.setText(realDescription);
        forecastAdapterViewHolder.hightWeatherTextView.setText(SunshineWeatherUtils.formatTemperature(mContext, highInCelsius));
        forecastAdapterViewHolder.lowWeatherTexaView.setText(SunshineWeatherUtils.formatTemperature(mContext, lowInCelsius));


    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }


    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }


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

            view.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            long dateInMillis = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
            mClickHandler.onClick(dateInMillis);
        }
    }
}