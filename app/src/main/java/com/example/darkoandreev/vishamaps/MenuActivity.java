package com.example.darkoandreev.vishamaps;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by darko.andreev on 5/2/2017.
 */

public class MenuActivity extends AppCompatActivity {

    private Button viewMapButton;
    private Button stopTrackingButton;
    private Button startTrackingButton;
    private Button showDatabase;
    private Button deleteDatabase;
    SensorManager sm;
    TrackerDatabase myDB;
    Intent mIntent, nIntent;
    LocationManager locationManager;
    SensorActivity sa;
    final static int PERMISSION_ALL = 1;
    final static String[] PERMISSIONS = {android.Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_menu);
        myDB = new TrackerDatabase(this);
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 23 && !isPermissionGranted()) {
            requestPermissions(PERMISSIONS, PERMISSION_ALL);

        } else
        if (!isLocationEnabled())
            showAlert(1);

        openActivity();
        startTrackingActivity();
        stopTracking();
        viewAllFromDb();
        deleteAllFromDb();
    }


    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isPermissionGranted() {
        if (checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED || checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.v("mylog", "Permission is granted");
            return true;
        } else {
            Log.v("mylog", "Permission not granted");
            return false;
        }
    }

    private void showAlert(final int status) {
        String message, title, btnText;
        if (status == 1) {
            message = "Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                    "use this app";
            title = "Enable Location";
            btnText = "Location Settings";
        } else {
            message = "Please allow this app to access location!";
            title = "Permission access";
            btnText = "Grant";
        }
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setTitle(title)
                .setMessage(message)
                .setPositiveButton(btnText, new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        if (status == 1) {
                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(myIntent);
                        } else
                            requestPermissions(PERMISSIONS, PERMISSION_ALL);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        finish();
                    }
                });
        dialog.show();
    }

    public void openActivity() {
        viewMapButton = (Button) findViewById(R.id.map_view);
        viewMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<LatLng> points;
                points = getLatLngFromDb();
                if (points != null && points.size() > 0) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("pbundle", points);
                    Intent intent = new Intent(MenuActivity.this, MapsActivity.class);
                    intent.putExtra("points", bundle);
                    startActivity(intent);
                } else {
                    Toast.makeText(MenuActivity.this, "No record in DB", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private ArrayList<LatLng> getLatLngFromDb() {
        Cursor res = myDB.getAllData();
        if (res.getCount() == 0) {
            showMessage("Error", "Nothing found");
            return null;
        }
        ArrayList<LatLng> list = new ArrayList<>();
        while (res.moveToNext()) {
            //Getting Latitude and longitude from DB and creating new LatLng object using them
            // and finally adding them to list
            list.add(new LatLng(Double.parseDouble(res.getString(2)), Double.parseDouble(res.getString(3))));
        }
        return list;
    }

    private ArrayList<String> getSpeedFromDb () {
        Cursor res = myDB.getAllData();
        if (res.getCount() == 0) {
            showMessage("Error", "Nothing found");
            return null;
        }
        ArrayList<String> list = new ArrayList<>();
        while (res.moveToNext()) {
            list.add(res.getString(1));
        }
        return list;
    }

    public void startTrackingActivity() {
        startTrackingButton = (Button) findViewById(R.id.start_tracking);

        startTrackingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIntent = new Intent(MenuActivity.this, LocationServiceActivity.class);
                nIntent = new Intent (MenuActivity.this, SensorActivity.class);
                Bundle extras = mIntent.getExtras();
                Bundle mBundle = new Bundle();
                Bundle nBundle = new Bundle();
                mIntent.putExtras(mBundle);
                nIntent.putExtras(nBundle);
                startService(mIntent);
                startService(nIntent);
                //sa.startSensor();

                Toast.makeText(MenuActivity.this, "Started", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void stopTracking() {
        stopTrackingButton = (Button) findViewById(R.id.stop_tracking);

        stopTrackingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // sa.stopSensor();
                stopService(new Intent(MenuActivity.this, LocationServiceActivity.class));
                stopService(new Intent(MenuActivity.this, SensorActivity.class));

            }
        });
    }

    public void viewAllFromDb() {

        showDatabase = (Button) findViewById(R.id.show_database);
        showDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewAll();
            }
        });

    }

    public void deleteAllFromDb() {
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
        Cursor res2 = myDB.getSensorData();
        if ((res.getCount() == 0) && (res2.getCount() == 0)){

            showMessage("Errorr", "Nothing found");
            return;
        }

        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext() && res2.moveToNext()) {

            buffer.append("Id: " + res.getString(0) + "\n");
            buffer.append("Speed: " + res.getString(1) + "\n");
            buffer.append("Latitude: " + res.getString(2) + "\n");
            buffer.append("Longitude: " + res.getString(3) + "\n");
            buffer.append("Time: " + res.getString(4) + "\n");
            buffer.append("X: " + res2.getString(1) + "\n");
            buffer.append("Y: " + res2.getString(2) + "\n");
            buffer.append("Z: " + res2.getString(3) + "\n");
            buffer.append("Acceleration: " + res2.getString(4) + "\n\n");

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
