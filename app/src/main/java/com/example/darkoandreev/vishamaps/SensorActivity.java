package com.example.darkoandreev.vishamaps;

/**
 * Created by dark on 5/15/2017.
 */

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

public class SensorActivity extends Service implements SensorEventListener {


    private SensorManager sm;
    private Intent mIntent;
    private TrackerDatabase myDB;
    private long lastUpdate;
    private static int ACCE_FILTER_DATA_MIN_TIME = 1000;
    private long lastSaved = System.currentTimeMillis();


    @Override
    public void onCreate() {
        super.onCreate();
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mIntent = new Intent(this, MenuActivity.class);
        this.startService(mIntent);

        isMyServiceRunning(SensorActivity.class);
        myDB = new TrackerDatabase(this);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //registering Sensor

        sm.registerListener(this,
                sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);

        //then you should return sticky
        return Service.START_STICKY;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        if ((System.currentTimeMillis() - lastSaved) > ACCE_FILTER_DATA_MIN_TIME) {

            getAccelerometer(event);
           // Toast.makeText(SensorActivity.this, "Sensor changed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    private void getAccelerometer(SensorEvent event) {

        float[] values = event.values;

        float x = values[0];
        float y = values[1];
        float z = values[2];

        float accelationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);

        long actualTime = System.currentTimeMillis();

        if (accelationSquareRoot >= 2) {

            if (actualTime - lastUpdate < 200) {

                return;
            }

            lastUpdate = actualTime;

        }

        mIntent.putExtra("X", x);
        mIntent.putExtra("Y", y);
        mIntent.putExtra("Z", z);
        mIntent.putExtra("Acceleration", accelationSquareRoot);
        sendBroadcast(mIntent);

        boolean isInserted = myDB.insertSensorData(x, y, z, accelationSquareRoot);
        if (isInserted == true) {
           // Toast.makeText(SensorActivity.this, "Sensor data inserted", Toast.LENGTH_LONG).show();
        } else {
           // Toast.makeText(SensorActivity.this, "Sensor data is NOT inserted", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("STOP_SERVICE", "DONE");
        sm.unregisterListener(this);
        Toast.makeText(this, "Stopping service", Toast.LENGTH_SHORT).show();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
