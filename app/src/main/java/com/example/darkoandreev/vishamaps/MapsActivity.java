package com.example.darkoandreev.vishamaps;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {


    final static int PERMISSION_ALL = 1;
    final static String[] PERMISSIONS = {android.Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};
    private GoogleMap mMap;
    MarkerOptions mo;
    Marker marker;
    LocationManager locationManager;
    private ArrayList<LatLng> points;
    Polyline line;
    private static final float SMALLEST_DISPLACEMENT = 0.25F;
    TextView showDistance;
    TextView showSpeed;
    TrackerDatabase myDB;
    Button showDb;
    ArrayList<LatLng> pointsFromDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        myDB = new TrackerDatabase(this);

        //NEW CODE
        Intent intent = getIntent();
        pointsFromDB = new ArrayList();
        if(intent != null){
            Bundle bundle = intent.getBundleExtra("points");
            pointsFromDB = bundle.getParcelableArrayList("pbundle");
        }
        ///////////////

        showDistance = (TextView) findViewById(R.id.show_distance_time);
        showSpeed = (TextView) findViewById(R.id.show_speed);
        showDb = (Button) findViewById(R.id.showData);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mo = new MarkerOptions().position(new LatLng(0, 0)).title("My Current Location");

        points = new ArrayList<>();


        if (Build.VERSION.SDK_INT >= 23 && !isPermissionGranted()) {
            requestPermissions(PERMISSIONS, PERMISSION_ALL);
            requestLocation();
        } else requestLocation();
        if (!isLocationEnabled())
            showAlert(1);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        marker = mMap.addMarker(mo);

        //New CODE To ADD POLYLINES
        Polyline lines = mMap.addPolyline(new PolylineOptions().width(3).color(Color.RED));
        lines.setPoints(pointsFromDB);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng clickCoords) {
                Log.e("TAG", "Found @ " + clickCoords.latitude + " " + clickCoords.longitude);
                Toast.makeText(MapsActivity.this, "Latitude: " + clickCoords.latitude + "\n" + "Longitude: " + clickCoords.longitude, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng myCoordinates = new LatLng(location.getLatitude(), location.getLongitude());
        points.add(myCoordinates);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myCoordinates, 15));
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

        marker.setPosition(myCoordinates);

        final double distance = SphericalUtil.computeLength(points);
        String DISTANCE = String.valueOf(distance);

        Button calcuate = (Button) findViewById(R.id.btnCalculate);


        calcuate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDistance.setText("Distance: " + distance + " meters");
                String DISTANCE = showDistance.getText().toString();
            }

        });

    }

    /*
    private void redrawLine() {
        //mMap.clear();  //clears all Markers and Polylines
        Cursor locationCursor = myDB.getLatLng();
        locationCursor.moveToFirst();
        Log.v("CURSOR----", locationCursor.toString());

        do {

            Double latitude = (locationCursor.getDouble(locationCursor
                    .getColumnIndex("LATITUDE")));
            Log.v("Latitude from db----", String.valueOf(latitude));
            Double longitude = (locationCursor.getDouble(locationCursor
                    .getColumnIndex("LONGITUDE")));
            Log.v("Longitude from db----", String.valueOf(longitude));

            points.add(new LatLng(latitude, longitude));
            Log.v("POINTS------", String.valueOf(points));
        } while (locationCursor.moveToNext());
        PolylineOptions options = new PolylineOptions().width(5).color(Color.RED).geodesic(true);

        for (int i = 0; i < points.size(); i++) {
            LatLng point = points.get(i);
            options.add(point);
        }

        line = mMap.addPolyline(options); //add Polyline

    }
    */


    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


    private void requestLocation() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        String provider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        locationManager.requestLocationUpdates(provider, 10000, SMALLEST_DISPLACEMENT, this);
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

}
