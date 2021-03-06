package com.sed.willy.contagomme.DBHelper;

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
import android.widget.Toast;

import com.sed.willy.contagomme.DBModel.BikeDetails;
import com.sed.willy.contagomme.DBModel.Brand;
import com.sed.willy.contagomme.DBModel.Race;
import com.sed.willy.contagomme.DBModel.WheelList;
import com.sed.willy.contagomme.DateConverter;
import com.sed.willy.contagomme.R;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper implements BaseColumns {

    /*
        SELECT count(1), Front_ID, rear_id FROM "amos"."aa" where Front_ID = rear_id group by front_id, rear_id
                union
        SELECT count(1), Front_ID, rear_id FROM "amos"."aa" where Front_ID <> rear_id group by front_id, rear_id;
    */
    // Table Names
    public static final String TABLE_BRANDS = "brands";
    public static final String TABLE_RACES = "races";
    public static final String TABLE_WHEEL_LIST = "wheel_list";
    public static final String TABLE_BIKE_DETAILS = "bike_details";
    // BRANDS Table - column names
    public static final String COLUMN_BRAND_NAME = "brand_name";
    public static final String COLUMN_BRAND_ORDER = "brand_order";
    // RACES Table - column names
    public static final String COLUMN_RACE_NAME = "race_name";
    public static final String COLUMN_RACE_PLACE = "race_place";
    public static final String COLUMN_RACE_DATETIME = "race_datetime";
    public static final String COLUMN_RACE_DESCRIPTION = "race_desc";
    // WHEEL Table - column names
    public static final String COLUMN_WHEEL_RACE_ID = "wheel_race_id";
    public static final String COLUMN_WHEEL_BRAND_ID = "wheel_brand_id";
    public static final String COLUMN_WHEEL_TOT_FRONT_WHEEL = "tot_front_wheel";
    public static final String COLUMN_WHEEL_TOT_REAR_WHEEL = "tot_rear_wheel";
    // BIKE_DETAIL - column names
    public static final String COLUMN_BIKE_RACE_ID = "bike_race_id";
    public static final String COLUMN_BIKE_FRONT_BRAND_ID = "bike_front_brand_id";
    public static final String COLUMN_BIKE_REAR_BRAND_ID = "bike_rear_brand_id";
    public static final String COLUMN_BIKE_INSERTED = "bike_inserted";
    // STATISTIC VIEW
    public static final String VIEW_STATISTIC = "statistics";
    public static final String CREATE_VIEW_STATISTIC =
            "CREATE VIEW " + VIEW_STATISTIC + " AS " +
                    "SELECT count(1) as counter, " +
                    "front_brand." + COLUMN_BRAND_NAME + " as front, " +
                    "rear_brand." + COLUMN_BRAND_NAME + " as rear " +
                    " FROM " + TABLE_BIKE_DETAILS + ", " + TABLE_BRANDS + " front_brand, " +
                    TABLE_BRANDS + " rear_brand " +
                    " WHERE " +
                    "front_brand." + _ID + " = " + TABLE_BIKE_DETAILS + "." + COLUMN_BIKE_FRONT_BRAND_ID +
                    " AND " +
                    "rear_brand." + _ID + " = " + TABLE_BIKE_DETAILS + "." + COLUMN_BIKE_REAR_BRAND_ID +
                    " AND " +
                    COLUMN_BIKE_FRONT_BRAND_ID + " = " + COLUMN_BIKE_REAR_BRAND_ID +
                    " GROUP BY " + "front_brand." + COLUMN_BRAND_NAME + ", rear_brand." + COLUMN_BRAND_NAME +
                    " UNION " +
                    "SELECT count(1), " +
                    "front_brand." + COLUMN_BRAND_NAME + " as front, " +
                    "rear_brand." + COLUMN_BRAND_NAME + " as rear " +
                    " FROM " + TABLE_BIKE_DETAILS + ", " + TABLE_BRANDS + " front_brand, " +
                    TABLE_BRANDS + " rear_brand " +
                    " WHERE " +
                    "front_brand." + _ID + " = " + TABLE_BIKE_DETAILS + "." + COLUMN_BIKE_FRONT_BRAND_ID +
                    " AND " +
                    "rear_brand." + _ID + " = " + TABLE_BIKE_DETAILS + "." + COLUMN_BIKE_REAR_BRAND_ID +
                    " AND " +
                    COLUMN_BIKE_FRONT_BRAND_ID + " <> " + COLUMN_BIKE_REAR_BRAND_ID +
                    " GROUP BY " + "front_brand." + COLUMN_BRAND_NAME + ", rear_brand." + COLUMN_BRAND_NAME;
    /*
        SELECT count(1), Front_ID, rear_id FROM aa where Front_ID = rear_id group by front_id, rear_id
                union
        SELECT count(1), Front_ID, rear_id FROM aa where Front_ID <> rear_id group by front_id, rear_id;
    */
    // RACES_WHEEL_LIST VIEW
    public static final String VIEW_RACES_WHEEL_LIST = "view_races_wheel_list";
    public static final String COLUMN_VIEW_RACE_ID = "race_id";
    public static final String COLUMN_VIEW_BRAND_ID = "brand_id";
    // TEMP Table Name
    private static final String TEMP_BIKE_DETAIL = "temp_bike_detail";
    // Database Version
    private static final int DATABASE_VERSION = 13;
    // Logcat tag
    private static final String TAG = DatabaseHelper.class.getName();
    // Database Name
    private static final String DATABASE_NAME = "contaGomme";

    // Table Create Statements
    // BRANDS table create statement
    private static final String CREATE_TABLE_BRANDS =
            "CREATE TABLE " + TABLE_BRANDS +
                    "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_BRAND_ORDER + " INTEGER," +
                    COLUMN_BRAND_NAME + " TEXT)";
    //                    IS_FRONT_SELECTED + " INT, "+
//                    IS_REAR_SELECTED + "INT)";
    // RACES table create statement
    private static final String CREATE_TABLE_RACES =
            "CREATE TABLE " + TABLE_RACES +
                    "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_RACE_NAME + " TEXT," +
                    COLUMN_RACE_PLACE + " TEXT," +
//                    COLUMN_RACE_DATE + " TEXT," +
                    COLUMN_RACE_DATETIME + " INTEGER," +
                    COLUMN_RACE_DESCRIPTION + " TEXT)";


    // BIKE_DETAILS table create statement
    private static final String CREATE_TABLE_BIKE_DETAILS =
            "CREATE TABLE " + TABLE_BIKE_DETAILS +
                    "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_BIKE_RACE_ID + " INTEGER," +
                    COLUMN_BIKE_FRONT_BRAND_ID + " INTEGER," +
                    COLUMN_BIKE_REAR_BRAND_ID + " INTEGER," +
                    COLUMN_BIKE_INSERTED + " INTEGER," +
                    " FOREIGN KEY (" + COLUMN_BIKE_RACE_ID + ") REFERENCES " +
                    TABLE_RACES + "(" + _ID + ") ON DELETE CASCADE, " +
                    " FOREIGN KEY (" + COLUMN_BIKE_FRONT_BRAND_ID + ") REFERENCES " +
                    TABLE_BRANDS + "(" + _ID + ") ON DELETE CASCADE, " +
                    " FOREIGN KEY (" + COLUMN_BIKE_REAR_BRAND_ID + ") REFERENCES " +
                    TABLE_BRANDS + "(" + _ID + ") ON DELETE CASCADE)";


    // WHEEL_LIST table create statement
    private static final String CREATE_TABLE_WHEEL_LIST =
            "CREATE TABLE " + TABLE_WHEEL_LIST +
                    "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_WHEEL_RACE_ID + " INTEGER," +
                    COLUMN_WHEEL_BRAND_ID + " INTEGER," +
                    COLUMN_WHEEL_TOT_FRONT_WHEEL + " INTEGER," +
                    COLUMN_WHEEL_TOT_REAR_WHEEL + " INTEGER," +
                    " FOREIGN KEY (" + COLUMN_WHEEL_RACE_ID + ") REFERENCES " +
                    TABLE_RACES + "(" + _ID + ") ON DELETE CASCADE, " +
                    " FOREIGN KEY (" + COLUMN_WHEEL_BRAND_ID + ") REFERENCES " +
                    TABLE_BRANDS + "(" + _ID + ") ON DELETE CASCADE)";
    // RACES_WHEEL_LIST VIEW create statement
    private static final String CREATE_RACES_WHEEL_LIST_VIEW =
            "CREATE VIEW " + VIEW_RACES_WHEEL_LIST +
                    " AS SELECT " + TABLE_WHEEL_LIST + "." + _ID + ", " +
                    TABLE_RACES + "." + _ID + " " + COLUMN_VIEW_RACE_ID + ", " +
                    TABLE_BRANDS + "." + _ID + " " + COLUMN_VIEW_BRAND_ID + ", " +
                    TABLE_RACES + "." + COLUMN_RACE_NAME + ", " +
                    TABLE_BRANDS + "." + COLUMN_BRAND_NAME + ", " +
                    COLUMN_WHEEL_TOT_FRONT_WHEEL + ", " +
                    COLUMN_WHEEL_TOT_REAR_WHEEL +
                    " FROM " + TABLE_WHEEL_LIST + ", " +
                    TABLE_BRANDS + ", " + TABLE_RACES +
                    " WHERE " + TABLE_WHEEL_LIST + "." + COLUMN_WHEEL_BRAND_ID + " = " +
                    TABLE_BRANDS + "." + _ID +
                    " AND " + TABLE_WHEEL_LIST + "." + COLUMN_WHEEL_RACE_ID + " = " +
                    TABLE_RACES + "." + _ID +
                    " ORDER BY " + COLUMN_BRAND_ORDER;
    private Context mContext;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables
        db.execSQL(CREATE_TABLE_BRANDS);
        db.execSQL(CREATE_TABLE_RACES);
        db.execSQL(CREATE_TABLE_WHEEL_LIST);
        db.execSQL(CREATE_TABLE_BIKE_DETAILS);
        db.execSQL(CREATE_RACES_WHEEL_LIST_VIEW);
        db.execSQL(CREATE_VIEW_STATISTIC);

        this.populateBrand(db);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BRANDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RACES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WHEEL_LIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BIKE_DETAILS);
        db.execSQL("DROP VIEW  IF EXISTS " + VIEW_RACES_WHEEL_LIST);
        db.execSQL("DROP VIEW  IF EXISTS " + VIEW_STATISTIC);

        // create new tables
        onCreate(db);
    }

    private void populateBrand(SQLiteDatabase db) {

        String[] tyreBrands = mContext.getResources().getStringArray(R.array.tire_brands);
        ContentValues values = new ContentValues();
        int order = 10;
        for (String b : tyreBrands) {
            values.put(COLUMN_BRAND_NAME, b);
            values.put(COLUMN_BRAND_ORDER, order);
            order += 10;
            db.insert(TABLE_BRANDS, null, values);
        }
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
        return db.insert(TABLE_BRANDS, null, values);
    }

    public int getMax(String tableName, String columnName, int tableID) {
        String sqlStatement = "Select Max(" + columnName + ") from " + tableName;
        if (tableID != 0) {
            sqlStatement += " WHERE " + _ID + " = " + tableName;
        }
        Cursor c = getCursor(sqlStatement);
        int maxValue = 0;
        if (c != null) {
            c.moveToFirst();
            maxValue = c.getInt(0);
            c.close();
        }
        return maxValue;

    }

    /**
     * Returns the cursor generated by the
     *
     * @param selectQuery statement
     * @return
     */
    public Cursor getCursor(String selectQuery) {

        SQLiteDatabase db = this.getReadableDatabase();
        Log.e(TAG, selectQuery);

        return db.rawQuery(selectQuery, null);
    }

    public Cursor getCursor(String tableName, String columnOrderBy) {

        String selectQuery;
        if (columnOrderBy == null || columnOrderBy.isEmpty()) {
            selectQuery = "SELECT * FROM " + tableName;
        } else {
            selectQuery = "SELECT * FROM " + tableName + " ORDER BY " + columnOrderBy;
        }
        return getCursor(selectQuery);
    }

    public Cursor getCursorById(String tableName, String columnID, int idValue) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + tableName + " WHERE " + columnID + "= " +
                String.valueOf(idValue);

        Log.e(TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {

                int id = c.getInt((c.getColumnIndex(_ID)));
                int brandId = c.getInt((c.getColumnIndex(COLUMN_VIEW_BRAND_ID)));
                int raceid = c.getInt((c.getColumnIndex(COLUMN_VIEW_RACE_ID)));
                int totFront = c.getInt((c.getColumnIndex(COLUMN_WHEEL_TOT_FRONT_WHEEL)));
                int totRear = c.getInt((c.getColumnIndex(COLUMN_WHEEL_TOT_REAR_WHEEL)));

            } while (c.moveToNext());
        }

        return c;

    }

    /**
     * get Brand Name
     */
    public String getStringField(String tableName, String idColumnName, int idColumnValue,
                                 String returnColumnName) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT " + returnColumnName + " FROM " + tableName + " WHERE " +
                idColumnName + " = " + String.valueOf(idColumnValue);


        Log.e(TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        String retVal = "";
        if (c != null) {
            c.moveToFirst();
            retVal = c.getString(1);
            c.close();
        }
        return retVal;

    }

    /**
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

        closeDB();

        return brands;
    }

    /**
     * getting all brands
     */
    public List<Brand> getAllBrands() {
        return getAllBrands(false);
    }

    public List<Brand> getAllBrands(boolean ordered) {
        List<Brand> brands = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_BRANDS;
        if (ordered) {
            selectQuery += " ORDER BY " + COLUMN_BRAND_ORDER;
        }

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

                brands.add(brand);
            } while (c.moveToNext());
        }

        closeDB();
        return brands;
    }

    /**
     * getting all brands by Race
     */
    public ArrayList<WheelList> getAllWheelListByRaceId(int raceId) {
        ArrayList<WheelList> tires = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + VIEW_RACES_WHEEL_LIST + " WHERE " +
                COLUMN_WHEEL_RACE_ID + " = " + String.valueOf(raceId);

        Log.e(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                WheelList tire = new WheelList();
                tire.setId(c.getInt((c.getColumnIndex(_ID))));
                tire.setRaceId(c.getInt((c.getColumnIndex(COLUMN_WHEEL_RACE_ID))));
                tire.setBrandId(c.getInt((c.getColumnIndex(COLUMN_WHEEL_BRAND_ID))));
                tire.setTotFrontWheel(c.getInt((c.getColumnIndex(COLUMN_WHEEL_TOT_FRONT_WHEEL))));
                tire.setTotRearWheel(c.getInt((c.getColumnIndex(COLUMN_WHEEL_TOT_FRONT_WHEEL))));

                tires.add(tire);
            } while (c.moveToNext());
        }

        c.close();
        return tires;
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

    public void switchBrand(ArrayList<Brand> brands, int fromPosition, int toPosition) {
        SQLiteDatabase db = this.getWritableDatabase();

        Brand fromBrand = brands.get(fromPosition);
        Brand toBrand = brands.get(toPosition);

        int from = fromBrand.getOrder();
        int to = toBrand.getOrder();

        fromBrand.setOrder(to);
        updateBrand(fromBrand);

        toBrand.setOrder(from);
        updateBrand(toBrand);
//        Toast.makeText(mContext, "from: " + from + "\nto: " + to, Toast.LENGTH_SHORT).show();
    }

    /**
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
//        values.put(COLUMN_RACE_DATE, race.getDate());
        values.put(COLUMN_RACE_DATETIME, race.getDateTime());

        // insert row
        return db.insert(TABLE_RACES, null, values);
    }

    /**
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
//        race.setDate((c.getString(c.getColumnIndex(COLUMN_RACE_DATE))));
        race.setDesc((c.getString(c.getColumnIndex(COLUMN_RACE_DESCRIPTION))));
        race.setDateTime((c.getInt(c.getColumnIndex(COLUMN_RACE_DATETIME))));

        c.close();
        db.close();
        return race;
    }

    /**
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
        String selectQuery = "SELECT * FROM " + TABLE_RACES;

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
//                r.setDate(c.getString(c.getColumnIndex(COLUMN_RACE_DATE)));
                r.setDateTime(c.getInt(c.getColumnIndex(COLUMN_RACE_DATETIME)));

                // adding to tags list
                tags.add(r);
            } while (c.moveToNext());
        }
        closeDB();
        return tags;
    }

    /**
     * @param getIds
     * @return
     */
    public Object[] getRacesArray(boolean getIds) {
        Object[] retVal;
        int rowCount = this.getRowCount(TABLE_RACES);
        if (!getIds) {
            retVal = new String[rowCount];
        } else {
            retVal = new Integer[rowCount];
        }

        String selectQuery;
        if (getIds) {
            selectQuery = "SELECT " + _ID + " FROM " + TABLE_RACES +
                    " order by " + COLUMN_RACE_DATETIME;
        } else {
            selectQuery = "SELECT * FROM " + TABLE_RACES + " order by " + COLUMN_RACE_DATETIME;
        }
        Log.e(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        int i = 0;

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                if (!getIds) {
                    retVal[i] = c.getString(c.getColumnIndex(COLUMN_RACE_NAME)) + "\t(" +
                            DateConverter.fromUnixToDate(
                                    c.getInt(c.getColumnIndex(COLUMN_RACE_DATETIME)),
                                    DateConverter.FORMAT_DATE) + ")";
                } else {
                    retVal[i] = c.getInt(c.getColumnIndex(_ID));
                }
                i++;
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return retVal;
    }

    public Object[] getRacesIdArray() {
        return getRacesArray(true);
    }

    public Object[] getRacesNameArray() {
        return getRacesArray(false);
    }

    /**
     * Updating a Race
     */
    public int updateRace(Race race) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_RACE_NAME, race.getName());
        values.put(COLUMN_RACE_DESCRIPTION, race.getDesc());
//        values.put(COLUMN_RACE_PLACE, race.getPlace());
//        values.put(COLUMN_RACE_DATE, race.getDate());
        values.put(COLUMN_RACE_DATETIME, race.getDateTime());

        // updating row
        return db.update(TABLE_RACES, values, _ID + " = ?",
                new String[]{String.valueOf(race.getId())});
    }

    public void populateWheelListTableFromRaceId(ArrayList<WheelList> wheelList) {
        if (wheelList == null) return;

        for (WheelList list : wheelList) {
            this.updateWheelList(list);
        }
    }

    /**
     * getting all WHEEL_LIST under single race
     */
    public List<WheelList> getAllWheelListByRace(String raceId) {
        List<WheelList> wheelLists = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_WHEEL_LIST + " wl, "
                + TABLE_BRANDS + " b WHERE wl." + COLUMN_WHEEL_RACE_ID + " = " + raceId +
                " AND wl." + COLUMN_WHEEL_BRAND_ID + " = " + "b." + _ID;

        Log.e(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                WheelList wl = new WheelList();
                wl.setId(c.getInt((c.getColumnIndex(_ID))));
//                wl.setNote((c.getString(c.getColumnIndex(KEY_TODO))));
//                wl.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));

                wheelLists.add(wl);
            } while (c.moveToNext());
        }
        c.close();
        return wheelLists;
    }

    /**
     * Creating a WHEEL_LIST
     */
    public long createWheelList(WheelList wheelList) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_WHEEL_BRAND_ID, wheelList.getBrandId());
        values.put(COLUMN_WHEEL_RACE_ID, wheelList.getRaceId());
        values.put(COLUMN_WHEEL_TOT_FRONT_WHEEL, wheelList.getTotFrontWheel());
        values.put(COLUMN_WHEEL_TOT_REAR_WHEEL, wheelList.getTotRearWheel());

        // insert row
        return db.insert(TABLE_WHEEL_LIST, null, values);
    }

    /**
     * get single WheelList
     */
    public WheelList getWheelList(long todo_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_WHEEL_LIST + " WHERE "
                + _ID + " = " + todo_id;

        Log.e(TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        WheelList wheelList = new WheelList();
        wheelList.setId(c.getInt(c.getColumnIndex(_ID)));
        wheelList.setBrandId((c.getInt(c.getColumnIndex(COLUMN_WHEEL_BRAND_ID))));
        wheelList.setRaceId((c.getInt(c.getColumnIndex(COLUMN_WHEEL_RACE_ID))));
        wheelList.setTotFrontWheel((c.getInt(c.getColumnIndex(COLUMN_WHEEL_TOT_FRONT_WHEEL))));
        wheelList.setTotRearWheel((c.getInt(c.getColumnIndex(COLUMN_WHEEL_TOT_REAR_WHEEL))));
//        race.setDateTime((c.getFloat(c.getColumnIndex(COLUMN_RACE_DATETIME))));
        closeDB();
        return wheelList;
    }

    /**
     * Updating a WHEEL_LIST
     */
    public long updateWheelList(WheelList wheelList) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_WHEEL_BRAND_ID, wheelList.getBrandId());
        values.put(COLUMN_WHEEL_RACE_ID, wheelList.getRaceId());
        values.put(COLUMN_WHEEL_TOT_FRONT_WHEEL, wheelList.getTotFrontWheel());
        values.put(COLUMN_WHEEL_TOT_REAR_WHEEL, wheelList.getTotRearWheel());

        // updating row
        return db.update(TABLE_WHEEL_LIST, values, _ID + " = ?",
                new String[]{String.valueOf(wheelList.getId())});
    }

    /**
     * Deleting a wheel_list
     */
    public void deleteWheelList(long wheelListId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WHEEL_LIST, _ID + " = ?",
                new String[]{String.valueOf(wheelListId)});
    }

    /**
     * Deleting a wheel_list
     */
    public void deleteWheelListByRace(long raceId, boolean onlyMissingBrands) {
        SQLiteDatabase db = this.getWritableDatabase();

        if (onlyMissingBrands) {
            db.delete(TABLE_WHEEL_LIST, COLUMN_WHEEL_RACE_ID + " = ?" + " AND " +
                    COLUMN_WHEEL_BRAND_ID + " NOT IN (SELECT " + _ID + " FROM " +
                    TABLE_BRANDS + ")", new String[]{String.valueOf(raceId)});
        } else {
            db.delete(TABLE_WHEEL_LIST, COLUMN_WHEEL_RACE_ID + " = ?",
                    new String[]{String.valueOf(raceId)});
        }
        closeDB();
    }

    /**
     * @param bikeDetails
     * @return
     */
    public long createBikeDetails(BikeDetails bikeDetails) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_BIKE_RACE_ID, bikeDetails.getRaceId());
        values.put(COLUMN_BIKE_FRONT_BRAND_ID, bikeDetails.getFrontBrandId());
        values.put(COLUMN_BIKE_REAR_BRAND_ID, bikeDetails.getRearBrandId());
        values.put(COLUMN_BIKE_INSERTED, bikeDetails.getInserted());

        // insert row
        return db.insert(TABLE_BIKE_DETAILS, null, values);
    }

    /**
     * Deleting a Bike Detail
     */
    public void deleteBikeDetailByRace(long raceId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BIKE_DETAILS, COLUMN_BIKE_RACE_ID + " = ?",
                new String[]{String.valueOf(raceId)});

    }

    public void createBackUpFroBikeDetails(int raceId) {
        String sqlSelect;
        SQLiteDatabase db = this.getWritableDatabase();

        sqlSelect = "DROP TABLE IF EXISTS " + TEMP_BIKE_DETAIL;
//
        db.execSQL(sqlSelect);

        sqlSelect = "CREATE TEMP TABLE IF NOT EXISTS " + TEMP_BIKE_DETAIL;
        sqlSelect += " AS SELECT ";
        sqlSelect += _ID + ",";
        sqlSelect += COLUMN_BIKE_RACE_ID + ",";
        sqlSelect += COLUMN_BIKE_FRONT_BRAND_ID + ",";
        sqlSelect += COLUMN_BIKE_REAR_BRAND_ID + ",";
        sqlSelect += COLUMN_BIKE_INSERTED + " ";
        sqlSelect += " FROM " + TABLE_BIKE_DETAILS;
        sqlSelect += " WHERE " + COLUMN_BIKE_RACE_ID + " = " + raceId;

        db.execSQL(sqlSelect);
    }

    public void restoreBikeDetailFromTemp(int raceId) {
        String sqlSelect;

        sqlSelect = "INSERT INTO " + TABLE_BIKE_DETAILS;
        sqlSelect += "(";
        sqlSelect += _ID + ",";
        sqlSelect += COLUMN_BIKE_RACE_ID + ",";
        sqlSelect += COLUMN_BIKE_FRONT_BRAND_ID + ",";
        sqlSelect += COLUMN_BIKE_REAR_BRAND_ID + ",";
        sqlSelect += COLUMN_BIKE_INSERTED + ") ";
        sqlSelect += " SELECT ";
        sqlSelect += _ID + ",";
        sqlSelect += COLUMN_BIKE_RACE_ID + ",";
        sqlSelect += COLUMN_BIKE_FRONT_BRAND_ID + ",";
        sqlSelect += COLUMN_BIKE_REAR_BRAND_ID + ",";
        sqlSelect += COLUMN_BIKE_INSERTED + " ";
        sqlSelect += " FROM " + TEMP_BIKE_DETAIL;
        sqlSelect += " WHERE " + COLUMN_BIKE_RACE_ID + " = " + raceId;

        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL(sqlSelect);

    }

    public ArrayList<BikeDetails> getAllBikeDetail() {
        return getAllBikeDetail(null);
    }

    /**
     * getting all brands
     */
    public ArrayList<BikeDetails> getAllBikeDetail(String whereClause) {
        ArrayList<BikeDetails> bikeDetails = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_BIKE_DETAILS;
        if (!whereClause.isEmpty() || whereClause == null) {
            selectQuery += " " + whereClause;
        }

        Log.e(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                BikeDetails bikeDetail = new BikeDetails();
                bikeDetail.setId(c.getInt((c.getColumnIndex(_ID))));
                bikeDetail.setRaceId((c.getInt(c.getColumnIndex(COLUMN_BIKE_RACE_ID))));
                bikeDetail.setFrontBrandId((c.getInt(c.getColumnIndex(COLUMN_BIKE_FRONT_BRAND_ID))));
                bikeDetail.setRearBrandId((c.getInt(c.getColumnIndex(COLUMN_BIKE_REAR_BRAND_ID))));
                bikeDetail.setInserted((c.getInt(c.getColumnIndex(COLUMN_BIKE_INSERTED))));

                bikeDetails.add(bikeDetail);
            } while (c.moveToNext());
        }

        closeDB();
        return bikeDetails;
    }

    /**
     * @param wheelList
     */
    private void updateBikeDetails(SQLiteDatabase db, WheelList wheelList) {
        // TODO: 16/02/2017 End method when the Insert Method is done
    }

    /**
     * get Table count
     */
    public int getRowCount(String tableName, String columnId, int valueID) {
        String selectQuery = "SELECT COUNT(*) as count FROM " + tableName +
                " WHERE " + columnId + " = " + String.valueOf(valueID);

        Log.e(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        int rowCount = 0;
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            rowCount = c.getInt((c.getColumnIndex("count")));
        }

        c.close();
        db.close();
        return rowCount;
    }

    public int getRowCount(String tableName) {
        String selectQuery = "SELECT COUNT(*) as count FROM " + tableName;

        Log.e(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        int rowCount = 0;
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            rowCount = c.getInt((c.getColumnIndex("count")));
        }

        closeDB();
        return rowCount;
    }

    public void populateWheelListTableFromRaceId(Integer raceID) {
        deleteWheelListByRace(raceID, true);

        String insertStatement = "INSERT INTO " + TABLE_WHEEL_LIST + " (" +
                COLUMN_WHEEL_BRAND_ID + "," + COLUMN_WHEEL_RACE_ID + ")" +
                " SELECT " + TABLE_BRANDS + "." + _ID + ", " + String.valueOf(raceID) +
                " FROM " + TABLE_BRANDS + " WHERE " + _ID + " NOT IN (SELECT " +
                COLUMN_WHEEL_BRAND_ID + " FROM " + TABLE_WHEEL_LIST + " WHERE " +
                COLUMN_WHEEL_RACE_ID + " = " + String.valueOf(raceID) + ")";
        Log.e(TAG, insertStatement);

        executeScript(insertStatement);
    }

    public void executeScript(String sqlScript) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.e(TAG, sqlScript);
        db.execSQL(sqlScript);
        closeDB();
    }

    /**
     * closing database
     */
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    public int resetRace(int raceID) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_WHEEL_TOT_FRONT_WHEEL, 0);
        values.put(COLUMN_WHEEL_TOT_REAR_WHEEL, 0);

        // updating row
        int retVal = db.update(TABLE_WHEEL_LIST, values, COLUMN_WHEEL_RACE_ID + " = ?",
                new String[]{String.valueOf(raceID)});
        closeDB();
        return retVal;
    }

    public int getBikeCount(int raceID) {
        String sqlQuery = "SELECT Sum(" + COLUMN_WHEEL_TOT_FRONT_WHEEL + ") as count FROM " +
                TABLE_WHEEL_LIST + " WHERE " + COLUMN_WHEEL_RACE_ID + " = " + raceID;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(sqlQuery, null);
        int bikeCount = 0;
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            bikeCount = c.getInt((c.getColumnIndex("count")));
        }
        c.close();

        return bikeCount;
    }

    public void moveBrand(int brandId, int currentOrder, boolean moveUp) {
        String moveOtherBrandSQL;
        String moveCurrentBrandSQL;
        if (!moveUp) {
            moveOtherBrandSQL = "UPDATE " + TABLE_BRANDS + " set " + COLUMN_BRAND_ORDER + " = (" +
                    COLUMN_BRAND_ORDER + " - 10) WHERE " + COLUMN_BRAND_ORDER + " = " +
                    String.valueOf(currentOrder + 10);
            moveCurrentBrandSQL = "UPDATE " + TABLE_BRANDS + " set " + COLUMN_BRAND_ORDER + " = (" +
                    COLUMN_BRAND_ORDER + " + 10) WHERE " + _ID + " = " + brandId;
        } else {
            moveOtherBrandSQL = "UPDATE " + TABLE_BRANDS + " set " + COLUMN_BRAND_ORDER + " = (" +
                    COLUMN_BRAND_ORDER + " + 10) WHERE " + COLUMN_BRAND_ORDER + " = " +
                    String.valueOf(currentOrder - 10);
            moveCurrentBrandSQL = "UPDATE " + TABLE_BRANDS + " set " + COLUMN_BRAND_ORDER + " = (" +
                    COLUMN_BRAND_ORDER + " - 10) WHERE " + _ID + " = " + brandId;
        }

        executeScript(moveOtherBrandSQL);
        executeScript(moveCurrentBrandSQL);

    }
}