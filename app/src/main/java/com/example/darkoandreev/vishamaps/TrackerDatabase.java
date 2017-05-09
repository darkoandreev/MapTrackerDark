package com.example.darkoandreev.vishamaps;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by darko.andreev on 4/27/2017.
 */

public class TrackerDatabase extends SQLiteOpenHelper {


    public static String SPEED = "SPEED";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String id = "ID";
    public static final String TIME = "time";
    public static final String DATABASE_NAME = "newTrackerDatabase";
    public static final String TABLE_NAME = "newTrackerTable";

    public static final int DATABASE_VERSION = 1;
    public static final String CREATE_QUERY = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, SPEED DOUBLE, LATITUDE DOUBLE, LONGITUDE DOUBLE, TIME TEXT);";

    public TrackerDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        Log.d("Database operations", "Database created");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_QUERY);
        Log.d("Database operations", "Table created");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }

    public boolean insertData(String speed, double latitude, double longitude, String time) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(SPEED, speed);
        cv.put(LATITUDE, latitude);
        cv.put(LONGITUDE, longitude);
        cv.put(TIME, time);


        long result = db.insert(TABLE_NAME, null, cv);

        if (result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        return res;
    }

    public Cursor getLatLng() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.query(TABLE_NAME, new String[]{
                        "latitude", "longitude"}, null, null,
                null, null, null);

        return res;
    }

    public void deleteAll()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from sqlite_sequence where name = 'newTrackerTable'");
        db.execSQL("delete from " + TABLE_NAME);
        db.close();
    }


}
