package com.example.darkoandreev.vishamaps;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
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
    private Button deleteDatabase;
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
        deleteAllFromDb();
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
                mIntent.putExtras(mBundle);
                startService(mIntent);
                Toast.makeText(MenuActivity.this, "Started", Toast.LENGTH_SHORT).show();

            }
        });
    }


    public void stopTracking () {
        stopTrackingButton = (Button) findViewById(R.id.stop_tracking);

        stopTrackingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(MenuActivity.this, LocationServiceActivity.class));
            }
        });
    }

    public void viewAllFromDb () {

        showDatabase = (Button) findViewById(R.id.show_database);
        showDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewAll();
            }
        });

    }

    public void deleteAllFromDb () {
        deleteDatabase = (Button) findViewById(R.id.clear_database);

        deleteDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    myDB.deleteAll();
                    Toast.makeText(MenuActivity.this, "Records are deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void viewAll() {

        Cursor res = myDB.getAllData();
        if (res.getCount() == 0) {

            showMessage("Errorr", "Nothing found");
            return;
        }

        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()) {

            buffer.append("Id: " + res.getString(0) + "\n");
            buffer.append("Speed: " + res.getString(1) + "\n");
            buffer.append("Latitude: " + res.getString(2) + "\n");
            buffer.append("Longitude: " + res.getString(3) + "\n");
            buffer.append("Time: " + res.getString(4) + "\n\n");

        }

        showMessage("Tracker Database", buffer.toString());
    }


    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }


}
