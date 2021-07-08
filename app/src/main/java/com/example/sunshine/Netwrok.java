package com.example.sunshine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class Netwrok extends AppCompatActivity {

    Button[] buttons;
    TextView result;

    private String googleUrl = "https://www.google.com";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_netwrok);
        buttons = new Button[4];
        buttons[0] = findViewById(R.id.website_button);
        result = findViewById(R.id.tv_result);
        buttons[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(googleUrl);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                if(intent.resolveActivity(getPackageManager()) != null)
                    startActivity(intent);
            }
        });
        buttons[1] = findViewById(R.id.map_button);
        buttons[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String addressPlace = "1600 Amphitheatre Parkway, CA";
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("geo")
                        .path("0,0")
                        .query(addressPlace);
                Uri uri = builder.build();
                Intent intent = new Intent(Intent.ACTION_VIEW,  uri);
                if(intent.resolveActivity(getPackageManager()) != null)
                    startActivity(intent);
            }
        });

        buttons[2] = findViewById(R.id.camera_button);
        buttons[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareText("Hi there!");
            }
        });
        buttons[3] = findViewById(R.id.do_it_button);

        Intent intent = getIntent();
        String fromEditText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if(intent.hasExtra(Intent.EXTRA_TEXT))
            result.setText(fromEditText);

    }
    public void shareText(String textToShare){
        String text = textToShare;
        String title= "Learn how to share text";
        String mimeType= "text/plain";
        Intent intent = ShareCompat.IntentBuilder.from(this)
                .setChooserTitle(title)
                .setText(text)
                .setType(mimeType).getIntent();
        startActivity(intent);

    }

}