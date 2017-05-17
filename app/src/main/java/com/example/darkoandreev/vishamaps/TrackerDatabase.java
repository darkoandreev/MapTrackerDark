package com.example.darkoandreev.vishamaps;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by dark on 4/27/2017.
 */

public class TrackerDatabase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "DarkicaDatabaseTracking";
    public static final int DATABASE_VERSION = 1;

    //-------- LOCATION TABLE -----------//
    public static final String LOCATION_TABLE = "location";
    public static String SPEED = "SPEED";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String TIME = "time";
    //--------------------------------//


    //-------- ACCELEROMETER TABLE -----------//
    public static final String ACCELEROMETER_TABLE = "sensors";
    public static final String CURRENT_X = "currentX";
    public static final String CURRENT_Y = "currentY";
    public static final String CURRENT_Z = "currentZ";
    public static final String ACCELERATION = "acceleration";
    //-------------------------------------//

    //-------- TRAVEL TABLE -----------//
    public static final String TRAVEL_TABLE = "travel";
    public static final String DESCRIPTION = "description";
    public static final String START_TIME = "startTime";
    //--------------------------------//


    public static final String CREATE_LOCATION_TABLE = "CREATE TABLE " + LOCATION_TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, SPEED DOUBLE, LATITUDE DOUBLE, LONGITUDE DOUBLE, TIME TEXT);";
    public static final String CREATE_ACCELEROMETER_TABLE = "CREATE TABLE " + ACCELEROMETER_TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, currentX DOUBLE, currentY DOUBLE, currentZ DOUBLE, acceleration DOUBLE);";
    public static final String CREATE_TRAVEL_TABLE = "CREATE TABLE " + TRAVEL_TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, DESCRIPTION TEXT);";


    public TrackerDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        Log.d("Database operations", "Database created");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_LOCATION_TABLE);
        db.execSQL(CREATE_ACCELEROMETER_TABLE);
        db.execSQL(CREATE_TRAVEL_TABLE);
        Log.d("Database operations", "Table created");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + LOCATION_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_ACCELEROMETER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TRAVEL_TABLE);
        onCreate(db);

    }

    public boolean insertLocationData(String speed, double latitude, double longitude, String time) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put(SPEED, speed);
        cv.put(LATITUDE, latitude);
        cv.put(LONGITUDE, longitude);
        cv.put(TIME, time);

        long result = db.insert(LOCATION_TABLE, null, cv);

        if (result == -1)
            return false;
        else
            return true;
    }

    public boolean insertTravelData (String description) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put(DESCRIPTION, description);

        long result = db.insert(TRAVEL_TABLE, null, cv);

        if (result == -1)
            return false;
        else
            return true;
    }

    public boolean insertSensorData (double currentX, double currentY, double currentZ, double acceleration) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put(CURRENT_X, currentX);
        cv.put(CURRENT_Y, currentY);
        cv.put(CURRENT_Z, currentZ);
        cv.put(ACCELERATION, acceleration);

        long result = db.insert(ACCELEROMETER_TABLE, null, cv);

        if (result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + LOCATION_TABLE, null);

        return res;
    }

    public Cursor getSensorData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + ACCELEROMETER_TABLE, null);

        return res;
    }

    public Cursor getDescriptionData () {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TRAVEL_TABLE, null);

        return res;
    }


    public void deleteAll()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from sqlite_sequence where name = 'location' OR name = 'sensors' OR name = 'travel'");
        db.execSQL("delete from " + LOCATION_TABLE);
        db.execSQL("delete from " + ACCELEROMETER_TABLE);
        db.execSQL("delete from " + TRAVEL_TABLE);
        db.close();
    }

}
