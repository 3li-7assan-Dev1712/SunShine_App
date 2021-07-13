package com.example.sunshine;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;

import com.example.sunshine.fragment.DetailWeatherFragment;

public class DetailActivity extends AppCompatActivity/* implements LoaderManager.LoaderCallbacks<Cursor>*/{

    String FORECAST_SHARE_HASHTAG = " #SunShineApp";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Uri mUri = getIntent().getData();
        if (mUri == null) throw new NullPointerException("URI for DetailActivity cannot be null");
        if (savedInstanceState == null){
            DetailWeatherFragment detailWeatherFragment = new DetailWeatherFragment();
            detailWeatherFragment.setmUri(mUri);
            getSupportFragmentManager().beginTransaction().add(R.id.detail_container, detailWeatherFragment).commit();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int item_which_clicked = item.getItemId();
        switch (item_which_clicked){
            case R.id.settings_from_detail:
                Intent goToSettingIntent = new Intent(this, SettingsActivity.class);
                startActivity(goToSettingIntent);
                break;
            case R.id.share_id:
                Intent intent = createShareForecastIntent();
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
        }
        return super.onOptionsItemSelected(item);
    }
    private Intent createShareForecastIntent() {
        return ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText("weatherOfTheSelectedDay" + FORECAST_SHARE_HASHTAG)
                .getIntent();
    }
}