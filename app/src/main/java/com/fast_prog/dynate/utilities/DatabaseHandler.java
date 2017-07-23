package com.fast_prog.dynate.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fast_prog.dynate.models.RideTemp;

/**
 * Created by sarathk on 3/1/17.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "DynaRide";
    // Contacts table name
    private static final String TABLE_RIDE = "ride";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String FROM_SELF = "from_self";
    private static final String FROM_NAME = "from_name";
    private static final String FROM_MOB = "from_phone";
    private static final String TO_SELF = "to_self";
    private static final String TO_NAME = "to_name";
    private static final String TO_MOB = "to_phone";
    private static final String GRE_DATE = "gre_date";
    private static final String HIJ_DATE = "hij_date";
    private static final String TIME = "time";
    private static final String TIME_STR = "time_str";
    private static final String FROM_ISO = "from_iso";
    private static final String FROM_MOB_NO_ISO = "from_mob_no_iso";
    private static final String TO_ISO = "to_iso";
    private static final String TO_MOB_NO_ISO = "to_mob_no_iso";
    private static final String MESSAGE = "message";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_RIDE_TABLE = "CREATE TABLE " + TABLE_RIDE + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
                + FROM_SELF + " TEXT," + FROM_NAME + " TEXT," + FROM_MOB + " TEXT," + FROM_ISO + " TEXT," + FROM_MOB_NO_ISO + " TEXT,"
                + TO_SELF + " TEXT," + TO_NAME + " TEXT," + TO_MOB + " TEXT," + TO_ISO + " TEXT," + TO_MOB_NO_ISO + " TEXT,"
                + GRE_DATE + " TEXT," + HIJ_DATE + " TEXT," + TIME + " TEXT," + TIME_STR + " TEXT," + MESSAGE + " TEXT )";
        db.execSQL(CREATE_RIDE_TABLE);
    }

    public void truncateTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RIDE, null, null);
        db.close();
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RIDE);
        // Create tables again
        onCreate(db);
    }


    public void addRideTemp(RideTemp rideTemp) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FROM_SELF, rideTemp.getIsFromself());
        values.put(FROM_NAME, rideTemp.getFromName());
        values.put(FROM_MOB, rideTemp.getFromMobile());
        values.put(FROM_ISO, rideTemp.getFromISO());
        values.put(FROM_MOB_NO_ISO, rideTemp.getFromMobWithoutISO());
        values.put(TO_SELF, rideTemp.getIsToself());
        values.put(TO_NAME, rideTemp.getToName());
        values.put(TO_MOB, rideTemp.getToMobile());
        values.put(TO_ISO, rideTemp.getToISO());
        values.put(TO_MOB_NO_ISO, rideTemp.getToMobWithoutISO());
        values.put(GRE_DATE, rideTemp.getDate());
        values.put(HIJ_DATE, rideTemp.getHijriDate());
        values.put(TIME, rideTemp.getTime());
        values.put(TIME_STR, rideTemp.getTimeString());
        values.put(MESSAGE, rideTemp.getIsMessage());

        // Inserting Row
        db.insert(TABLE_RIDE, null, values);
        db.close(); // Closing database connection
    }

    public RideTemp getRideTemp(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RIDE, new String[] { KEY_ID, FROM_SELF, FROM_NAME, FROM_MOB, FROM_ISO, FROM_MOB_NO_ISO,
                TO_SELF, TO_NAME, TO_MOB, TO_ISO, TO_MOB_NO_ISO, GRE_DATE, HIJ_DATE, TIME, TIME_STR, MESSAGE }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        RideTemp rideTemp = new RideTemp(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),
                cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10),
                cursor.getString(11), cursor.getString(12), cursor.getString(13), cursor.getString(14), cursor.getString(15));
        // return contact
        cursor.close();
        db.close();

        return rideTemp;
    }

    // Getting All Contacts
    //public List<RideTemp> getAllRideTemp() {
    //    List<RideTemp> rideTempList = new ArrayList<RideTemp>();
    //    // Select All Query
    //    String selectQuery = "SELECT * FROM " + TABLE_RIDE;
    //
    //    SQLiteDatabase db = this.getWritableDatabase();
    //    Cursor cursor = db.rawQuery(selectQuery, null);
    //
    //    // looping through all rows and adding to list
    //    if (cursor.moveToFirst()) {
    //        do {
    //            RideTemp rideTemp = new RideTemp();
    //            rideTemp.setId(Integer.parseInt(cursor.getString(0)));
    //            rideTemp.setIsFromself(cursor.getString(1));
    //            rideTemp.setFromName(cursor.getString(2));
    //            rideTemp.setFromMobile(cursor.getString(3));
    //            rideTemp.setFromISO(cursor.getString(4));
    //            rideTemp.setFromMobWithoutISO(cursor.getString(5));
    //            rideTemp.setIsToself(cursor.getString(6));
    //            rideTemp.setToName(cursor.getString(7));
    //            rideTemp.setToMobile(cursor.getString(8));
    //            rideTemp.setToISO(cursor.getString(9));
    //            rideTemp.setToMobWithoutISO(cursor.getString(10));
    //            rideTemp.setDate(cursor.getString(11));
    //            rideTemp.setHijriDate(cursor.getString(12));
    //            rideTemp.setTime(cursor.getString(13));
    //            rideTemp.setTimeString(cursor.getString(14));
    //            rideTemp.setIsMessage(cursor.getString(15));
    //            // Adding contact to list
    //            rideTempList.add(rideTemp);
    //        } while (cursor.moveToNext());
    //    }
    //
    //    // return contact list
    //    return rideTempList;
    //}
    //
    //// Updating single contact
    //public int updateRideTemp(RideTemp rideTemp) {
    //    SQLiteDatabase db = this.getWritableDatabase();
    //
    //    ContentValues values = new ContentValues();
    //    values.put(FROM_SELF, rideTemp.getIsFromself());
    //    values.put(FROM_NAME, rideTemp.getFromName());
    //    values.put(FROM_MOB, rideTemp.getFromMobile());
    //    values.put(FROM_ISO, rideTemp.getFromISO());
    //    values.put(FROM_MOB_NO_ISO, rideTemp.getFromMobWithoutISO());
    //    values.put(TO_SELF, rideTemp.getIsToself());
    //    values.put(TO_NAME, rideTemp.getToName());
    //    values.put(TO_MOB, rideTemp.getToMobile());
    //    values.put(TO_ISO, rideTemp.getToISO());
    //    values.put(TO_MOB_NO_ISO, rideTemp.getToMobWithoutISO());
    //    values.put(GRE_DATE, rideTemp.getDate());
    //    values.put(HIJ_DATE, rideTemp.getHijriDate());
    //    values.put(TIME, rideTemp.getTime());
    //    values.put(TIME_STR, rideTemp.getTimeString());
    //    values.put(MESSAGE, rideTemp.getIsMessage());
    //
    //    // updating row
    //    return db.update(TABLE_RIDE, values, KEY_ID + " = ?",
    //            new String[] { String.valueOf(rideTemp.getId()) });
    //}

    // Deleting single contact
    public void deleteRideTemp(RideTemp rideTemp) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RIDE, KEY_ID + " = ?",
                new String[] { String.valueOf(rideTemp.getId()) });
        db.close();
    }


    // Getting contacts Count
    public int getRideTempCount() {
        String countQuery = "SELECT  * FROM " + TABLE_RIDE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        return count;
    }

}