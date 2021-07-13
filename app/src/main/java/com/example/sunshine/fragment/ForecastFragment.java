package com.example.sunshine.fragment;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunshine.ForecastAdapter;
import com.example.sunshine.MainActivity;
import com.example.sunshine.R;
import com.example.sunshine.data.WeatherContract;
import com.example.sunshine.sync.SunShineSyncUtils;

public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ID_FORECAST_LOADER = 44;
    private ForecastAdapter mForecastAdapter;
    private RecyclerView mForecastRecycler;
    private int mPosition = RecyclerView.NO_POSITION;
    private ProgressBar mLoadingIndicator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        SunShineSyncUtils.initialize(getContext());
        View forecastView = inflater.inflate(R.layout.forecast_fragment, container, false);
        mForecastRecycler= forecastView.findViewById(R.id.recyclerview_forecast);
        mForecastRecycler.setHasFixedSize(true);
        if (getContext() != null) {
            LoaderManager.getInstance(this).initLoader(ID_FORECAST_LOADER, null, this);
            mForecastRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
            mForecastAdapter = new ForecastAdapter(getContext(), (ForecastAdapter.ForecastAdapterOnClickHandler) getActivity());
            mForecastRecycler.setAdapter(mForecastAdapter);
        }
        mLoadingIndicator = forecastView.findViewById(R.id.progress_indicator);
        showLoading();

        return forecastView;
    }

    private void showLoading() {
        /* Then, hide the weather data */
        mForecastRecycler.setVisibility(View.INVISIBLE);
        /* Finally, show the loading indicator */
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        switch (id) {

            case ID_FORECAST_LOADER:
                Uri forecastQueryUri = WeatherContract.WeatherEntry.CONTENT_URI;
                String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
                String selection = WeatherContract.WeatherEntry.getSqlSelectForTodayOnwards();

                return new CursorLoader(getContext(),
                        forecastQueryUri,
                        MainActivity.MAIN_FORECAST_PROJECTION,
                        selection,
                        null,
                        sortOrder);

            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

        mForecastAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        mForecastRecycler.smoothScrollToPosition(mPosition);
        if (data.getCount() != 0) showWeatherDataView();
        else showNoWeatherData();
    }

    private void showNoWeatherData() {
        AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
        dialog.setTitle("Internet/Input Error");
        dialog.setMessage("Please make sure you've input the city name correct and check your internet connection.");
        dialog.show();
        mLoadingIndicator.setVisibility(View.INVISIBLE);
    }

    private void showWeatherDataView() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mForecastRecycler.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        // has no use in my app functionality *_*
    }
}
