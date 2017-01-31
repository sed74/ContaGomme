package com.marchesi.federico.contagomme.DBHelper;

/**
 * Created by federico.marchesi on 26/01/2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.marchesi.federico.contagomme.DBModel.Brand;
import com.marchesi.federico.contagomme.DBModel.Race;
import com.marchesi.federico.contagomme.DBModel.WheelList;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper implements BaseColumns {

    // Table Names
    public static final String TABLE_BRANDS = "brands";
    public static final String TABLE_RACES = "races";
    public static final String TABLE_WHEEL_LIST = "wheel_list";

    // BRANDS Table - column names
    public static final String COLUMN_BRAND_NAME = "brand_name";
    public static final String COLUMN_BRAND_ORDER = "brand_order";

    // RACES Table - column names
    public static final String COLUMN_RACE_NAME = "race_name";
    public static final String COLUMN_RACE_PLACE = "race_place";
    public static final String COLUMN_RACE_DATE = "race_date";
    public static final String COLUMN_RACE_DESCRIPTION = "race_desc";

    // WHEEL_LIST Table - column names
    public static final String COLUMN_RACE_LIST_ID = "race_list_id";
    public static final String COLUMN_WHEEL_BRAND_ID = "wheel_brand";

    // Common column names
    //private static final String KEY_ID = "_id";
    public static final String COLUMN_TOT_FRONT_WHEEL = "tot_front_wheel";
    public static final String COLUMN_TOT_REAR_WHEEL = "tot_rear_wheel";

    // Logcat tag
    private static final String TAG = DatabaseHelper.class.getName();
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "contaGomme";


    // Table Create Statements
    // BRANDS table create statement
    private static final String CREATE_TABLE_BRANDS =
            "CREATE TABLE " + TABLE_BRANDS +
                    "(" + _ID + " INTEGER PRIMARY KEY," +
                    COLUMN_BRAND_ORDER + " INTEGER," +
                    COLUMN_BRAND_NAME + " TEXT)";

    // RACES table create statement
    private static final String CREATE_TABLE_RACES =
            "CREATE TABLE " + TABLE_RACES +
                    "(" + _ID + " INTEGER PRIMARY KEY," +
                    COLUMN_RACE_NAME + " TEXT," +
                    COLUMN_RACE_PLACE + " TEXT," +
                    COLUMN_RACE_DATE + " TEXT," +
                    COLUMN_RACE_DESCRIPTION + " TEXT)";

    // WHEEL_LIST table create statement
    private static final String CREATE_TABLE_WHEEL_LIST =
            "CREATE TABLE " + TABLE_WHEEL_LIST +
                    "(" + _ID + " INTEGER PRIMARY KEY," +
                    COLUMN_RACE_LIST_ID + " INTEGER," +
                    COLUMN_WHEEL_BRAND_ID + " INTEGER," +
                    COLUMN_TOT_FRONT_WHEEL + " INTEGER," +
                    COLUMN_TOT_REAR_WHEEL + " TEXT," +
                    " FOREIGN KEY (" + COLUMN_RACE_LIST_ID + ") REFERENCES " +
                    TABLE_RACES + "(" + _ID + "), " +
                    " FOREIGN KEY (" + COLUMN_WHEEL_BRAND_ID + ") REFERENCES " +
                    TABLE_BRANDS + "(" + _ID + "))";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables
        db.execSQL(CREATE_TABLE_BRANDS);
        db.execSQL(CREATE_TABLE_RACES);
        db.execSQL(CREATE_TABLE_WHEEL_LIST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_BRANDS);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_RACES);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_WHEEL_LIST);

        // create new tables
        onCreate(db);
    }

    /*
    * Creating a BRAND
    */
    public long createBrand(Brand brands) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_BRAND_NAME, brands.getName());
        values.put(COLUMN_BRAND_ORDER, brands.getOrder());

        // insert row
        long brandId = db.insert(TABLE_BRANDS, null, values);

        return brandId;
    }

    public Cursor getCursor(String tableName, String columnOrderBy) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + tableName + " ORDER BY " + columnOrderBy;

        Log.e(TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        return c;

    }

    /*
     * get single Brand
     */
    public Brand getBrand(long todo_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_BRANDS + " WHERE "
                + _ID + " = " + todo_id;

        Log.e(TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Brand brands = new Brand();
        brands.setId(c.getInt(c.getColumnIndex(_ID)));
        brands.setName((c.getString(c.getColumnIndex(COLUMN_BRAND_NAME))));
        brands.setOrder((c.getInt(c.getColumnIndex(COLUMN_BRAND_ORDER))));

        return brands;
    }

    /*
     * getting all brands
     * */
    public List<Brand> getAllBrandss() {
        List<Brand> brands = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_BRANDS;

        Log.e(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Brand brand = new Brand();
                brand.setId(c.getInt((c.getColumnIndex(_ID))));
                brand.setName((c.getString(c.getColumnIndex(COLUMN_BRAND_NAME))));
                brand.setOrder((c.getInt(c.getColumnIndex(COLUMN_BRAND_ORDER))));

                // adding to todo list
                brands.add(brand);
            } while (c.moveToNext());
        }

        return brands;
    }

    /*
     * Updating a brand
     */
    public int updateBrand(Brand brands) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_BRAND_NAME, brands.getName());
        values.put(COLUMN_BRAND_ORDER, brands.getOrder());

        // updating row
        return db.update(TABLE_BRANDS, values, _ID + " = ?",
                new String[]{String.valueOf(brands.getId())});
    }

    /*
     * Deleting a brand
     */
    public void deleteBrand(long brandId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BRANDS, _ID + " = ?",
                new String[]{String.valueOf(brandId)});
    }

    /*
     * Creating a Race
     */
    public long createRace(Race race) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_RACE_NAME, race.getName());
//        values.put(COLUMN_RACE_PLACE, race.getPlace());
        values.put(COLUMN_RACE_DESCRIPTION, race.getDesc());
        values.put(COLUMN_RACE_DATE, race.getDate());

        // insert row
        long tag_id = db.insert(TABLE_RACES, null, values);

        return tag_id;
    }

    /*
     * get single Race
     */
    public Race getRace(long todo_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_RACES + " WHERE "
                + _ID + " = " + todo_id;

        Log.e(TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Race race = new Race();
        race.setId(c.getInt(c.getColumnIndex(_ID)));
        race.setName((c.getString(c.getColumnIndex(COLUMN_RACE_NAME))));
        race.setDate((c.getString(c.getColumnIndex(COLUMN_RACE_DATE))));
        race.setDesc((c.getString(c.getColumnIndex(COLUMN_RACE_DESCRIPTION))));

        return race;
    }

    /*
     * Deleting a race
     */
    public void deleteRace(long raceId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RACES, _ID + " = ?",
                new String[]{String.valueOf(raceId)});
    }


    /**
     * getting all races
     */
    public List<Race> getAllRaces() {
        List<Race> tags = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_RACES;

        Log.e(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Race r = new Race();
                r.setId(c.getInt((c.getColumnIndex(_ID))));
                r.setName(c.getString(c.getColumnIndex(COLUMN_RACE_NAME)));
                r.setDesc(c.getString(c.getColumnIndex(COLUMN_RACE_DESCRIPTION)));
//                r.setPlace(c.getString(c.getColumnIndex(COLUMN_RACE_PLACE)));
                r.setDate(c.getString(c.getColumnIndex(COLUMN_RACE_DATE)));

                // adding to tags list
                tags.add(r);
            } while (c.moveToNext());
        }
        return tags;
    }

    /*
     * Updating a Race
     */
    public int updateRace(Race race) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_RACE_NAME, race.getName());
        values.put(COLUMN_RACE_DESCRIPTION, race.getDesc());
//        values.put(COLUMN_RACE_PLACE, race.getPlace());
        values.put(COLUMN_RACE_DATE, race.getDate());

        // updating row
        return db.update(TABLE_RACES, values, _ID + " = ?",
                new String[]{String.valueOf(race.getId())});
    }

    /*
     * getting all WHEEL_LIST under single race
     * */
    public List<WheelList> getAllWheelListByRace(String raceId) {
        List<WheelList> wheelLists = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_WHEEL_LIST + " wl, "
                + TABLE_BRANDS + " b WHERE wl." + COLUMN_RACE_LIST_ID + " = " + raceId +
                " AND wl." + COLUMN_WHEEL_BRAND_ID + " = " + "b." + _ID;

        Log.e(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                WheelList wl = new WheelList();
                wl.setWheelListId(c.getInt((c.getColumnIndex(_ID))));
//                wl.setNote((c.getString(c.getColumnIndex(KEY_TODO))));
//                wl.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));

                // adding to todo list
                wheelLists.add(wl);
            } while (c.moveToNext());
        }

        return wheelLists;
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}