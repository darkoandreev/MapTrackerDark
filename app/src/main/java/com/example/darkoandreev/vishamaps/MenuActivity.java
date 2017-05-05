package com.example.darkoandreev.vishamaps;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by darko.andreev on 5/2/2017.
 */

public class MenuActivity extends AppCompatActivity {

    private Button viewMapButton;
    private Button stopTrackingButton;
    private Button startTrackingButton;
    private Button showDatabase;
    TrackerDatabase myDB;
    Intent mIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_menu);
        myDB = new TrackerDatabase(this);

        openActivity();
        startTrackingActivity();
        stopTracking();
        viewAllFromDb();
    }

    public void openActivity() {
        viewMapButton = (Button) findViewById(R.id.map_view);

        viewMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MenuActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });

    }

    public void startTrackingActivity () {
        startTrackingButton = (Button) findViewById(R.id.start_tracking);

        startTrackingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIntent = new Intent(MenuActivity.this, LocationServiceActivity.class);
                Bundle extras = mIntent.getExtras();
                Bundle mBundle = new Bundle();
                //mBundle.extras.putString(key, value);
                mIntent.putExtras(mBundle);
                startService(mIntent);
                Toast.makeText(MenuActivity.this, "Started", Toast.LENGTH_LONG).show();

            }
        });
    }


    public void stopTracking () {
        stopTrackingButton = (Button) findViewById(R.id.stop_tracking);

        stopTrackingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(mIntent);
            }
        });
    }

    public void viewAllFromDb () {

        showDatabase = (Button) findViewById(R.id.show_database);
        showDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationServiceActivity lsa = new LocationServiceActivity();
                lsa.my.viewAll();
            }
        });

    }


}
