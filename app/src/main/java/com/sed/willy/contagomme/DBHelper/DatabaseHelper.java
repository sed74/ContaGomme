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

import com.sed.willy.contagomme.DBContract.ContaGommeContract.BikeDetailEntry;
import com.sed.willy.contagomme.DBContract.ContaGommeContract.BrandEntry;
import com.sed.willy.contagomme.DBContract.ContaGommeContract.RaceEntry;
import com.sed.willy.contagomme.DBContract.ContaGommeContract.StatsEntry;
import com.sed.willy.contagomme.DBContract.ContaGommeContract.WheelEntry;
import com.sed.willy.contagomme.DBContract.ContaGommeContract.WheelListEntry;
import com.sed.willy.contagomme.DBModel.BikeDetails;
import com.sed.willy.contagomme.DBModel.Brand;
import com.sed.willy.contagomme.DBModel.Race;
import com.sed.willy.contagomme.DBModel.WheelList;
import com.sed.willy.contagomme.R;
import com.sed.willy.contagomme.Utils.DateConverter;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper implements BaseColumns {

    // Logcat tag
    private static final String TAG = DatabaseHelper.class.getName();
    // Database Name
    private static final String DATABASE_NAME = "contaGomme.db";
    // Database Version
    private static final int DATABASE_VERSION = 16;
    // TEMP Table Name
    private static final String TEMP_BIKE_DETAIL = "temp_bike_detail";

    private Context mContext;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables
        db.execSQL(BrandEntry.CREATE_TABLE);
        db.execSQL(RaceEntry.CREATE_TABLE);
        db.execSQL(WheelEntry.CREATE_TABLE);
        db.execSQL(BikeDetailEntry.CREATE_TABLE);
        db.execSQL(WheelListEntry.CREATE_VIEW);
        db.execSQL(StatsEntry.CREATE_VIEW);

        this.populateBrand(db);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        if (oldVersion >= 13) {
            for (int i = oldVersion; i < newVersion; i++) {
                DBUpgradeScripts.upgrade(db, i + 1);
            }
        } else {
            db.execSQL("DROP TABLE IF EXISTS " + BrandEntry.TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + RaceEntry.TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + WheelEntry.TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + BikeDetailEntry.TABLE);
            db.execSQL("DROP VIEW  IF EXISTS " + WheelListEntry.VIEW);
            db.execSQL("DROP VIEW  IF EXISTS " + StatsEntry.VIEW);

            // create new tables
            onCreate(db);
        }
    }

    public void populateBrand(SQLiteDatabase db) {

        String[] tyreBrands = mContext.getResources().getStringArray(R.array.tire_brands);
        ContentValues values = new ContentValues();
        int order = 10;
        for (String b : tyreBrands) {
            values.put(BrandEntry.BRAND_NAME, b);
            values.put(BrandEntry.BRAND_ORDER, order);
            order += 10;
            db.insert(BrandEntry.TABLE, null, values);
        }
    }

    /*
    * Creating a BRAND
    */
    public long createBrand(Brand brands) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BrandEntry.BRAND_NAME, brands.getName());
        values.put(BrandEntry.BRAND_ORDER, brands.getOrder());

        // insert row
        return db.insert(BrandEntry.TABLE, null, values);
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

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {"*"};

        // Filter results WHERE "columnId" = 'idValue'
        String selection = columnID + " = ?";
        String[] selectionArgs = {String.valueOf(idValue)};

        Cursor c = db.query(
                tableName,             // The table to query
                projection,            // The columns to return
                selection,             // The columns for the WHERE clause
                selectionArgs,         // The values for the WHERE clause
                null,                  // don't group the rows
                null,                  // don't filter by row groups
                null                   // The sort order
        );


//        Cursor c = db.rawQuery(selectQuery, null);

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

        String selectQuery = "SELECT  * FROM " + BrandEntry.TABLE + " WHERE "
                + _ID + " = " + todo_id;

        Log.e(TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Brand brands = new Brand();
        brands.setId(c.getInt(c.getColumnIndex(_ID)));
        brands.setName((c.getString(c.getColumnIndex(BrandEntry.BRAND_NAME))));
        brands.setOrder((c.getInt(c.getColumnIndex(BrandEntry.BRAND_ORDER))));

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
        String selectQuery = "SELECT * FROM " + BrandEntry.TABLE + " WHERE " + BrandEntry.BRAND_IS_DELETED +
                "=?";
        if (ordered) {
            selectQuery += " ORDER BY " + BrandEntry.BRAND_ORDER;
        }

        Log.e(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, new String[]{"0"});

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Brand brand = new Brand();
                brand.setId(c.getInt((c.getColumnIndex(_ID))));
                brand.setName((c.getString(c.getColumnIndex(BrandEntry.BRAND_NAME))));
                brand.setOrder((c.getInt(c.getColumnIndex(BrandEntry.BRAND_ORDER))));

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

        String selectQuery = "SELECT * FROM " + WheelListEntry.VIEW + " WHERE " +
                WheelEntry.RACE_ID + " = " + String.valueOf(raceId);

        Log.e(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                WheelList tire = new WheelList();
                tire.setId(c.getInt((c.getColumnIndex(_ID))));
                tire.setRaceId(c.getInt((c.getColumnIndex(WheelEntry.RACE_ID))));
                tire.setBrandId(c.getInt((c.getColumnIndex(WheelEntry.BRAND_ID))));
                tire.setTotFrontWheel(c.getInt((c.getColumnIndex(WheelEntry.TOT_FRONT_WHEEL))));
                tire.setTotRearWheel(c.getInt((c.getColumnIndex(WheelEntry.TOT_FRONT_WHEEL))));

                tires.add(tire);
            } while (c.moveToNext());
        }

        c.close();
        return tires;
    }

    /**
     * Updating a brand
     */
    public int updateBrand(Brand brands) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BrandEntry.BRAND_NAME, brands.getName());
        values.put(BrandEntry.BRAND_ORDER, brands.getOrder());

        // updating row
        return db.update(BrandEntry.TABLE, values, _ID + " = ?",
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
        if (brandId >= 0) {
            db.delete(BrandEntry.TABLE, _ID + " = ? AND " + BrandEntry.BRAND_IS_DELETED + " = ? ",
                    new String[]{String.valueOf(brandId), String.valueOf(BrandEntry.ROW_DELETED)});
        } else {
            db.delete(BrandEntry.TABLE, BrandEntry.BRAND_IS_DELETED + " = ? ",
                    new String[]{String.valueOf(BrandEntry.ROW_DELETED)});
        }

    }

    /**
     * Delete ALL brands with is_deleted = 1
     */
    public void purgeBrands() {
        deleteBrand(-1);
    }

    /**
     * Marking a brand as Deleted for undo purposes
     */
    public void deleteBrand(long brandId, boolean markAsDeleted) {
        if (!markAsDeleted) {
            deleteBrand(brandId);
            return;
        } else {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(BrandEntry.BRAND_IS_DELETED, BrandEntry.ROW_DELETED);

            db.update(BrandEntry.TABLE, values, _ID + "=?", new String[]{String.valueOf(brandId)});
        }
    }

    /**
     * Undo a brand deletion
     *
     * @param brandID
     */
    public void unDeleteBrand(int brandID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BrandEntry.BRAND_IS_DELETED, BrandEntry.ROW_NOT_DELETED);

        db.update(BrandEntry.TABLE, values, _ID + "=?", new String[]{String.valueOf(brandID)});
    }

    /**
     * Creating a Race
     */
    public long createRace(Race race) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(RaceEntry.RACE_NAME, race.getName());
//        values.put(COLUMN_RACE_PLACE, race.getPlace());
        values.put(RaceEntry.RACE_DESCRIPTION, race.getDesc());
//        values.put(COLUMN_RACE_DATE, race.getDate());
        values.put(RaceEntry.RACE_DATETIME, race.getDateTime());

        // insert row
        return db.insert(RaceEntry.TABLE, null, values);
    }

    /**
     * get single Race
     */
    public Race getRace(long todo_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + RaceEntry.TABLE + " WHERE "
                + _ID + " = " + todo_id;

        Log.e(TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Race race = new Race();
        race.setId(c.getInt(c.getColumnIndex(_ID)));
        race.setName((c.getString(c.getColumnIndex(RaceEntry.RACE_NAME))));
//        race.setDate((c.getString(c.getColumnIndex(RaceEntry.RACE_DATE))));
        race.setDesc((c.getString(c.getColumnIndex(RaceEntry.RACE_DESCRIPTION))));
        race.setDateTime((c.getInt(c.getColumnIndex(RaceEntry.RACE_DATETIME))));

        c.close();
        db.close();
        return race;
    }

    /**
     * Deleting a race
     */
    public void deleteRace(long raceId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(RaceEntry.TABLE, _ID + " = ?",
                new String[]{String.valueOf(raceId)});
    }


    /**
     * getting all races
     */
    public List<Race> getAllRaces() {
        List<Race> tags = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + RaceEntry.TABLE;

        Log.e(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Race r = new Race();
                r.setId(c.getInt((c.getColumnIndex(_ID))));
                r.setName(c.getString(c.getColumnIndex(RaceEntry.RACE_NAME)));
                r.setDesc(c.getString(c.getColumnIndex(RaceEntry.RACE_DESCRIPTION)));
//                r.setPlace(c.getString(c.getColumnIndex(COLUMN_RACE_PLACE)));
//                r.setDate(c.getString(c.getColumnIndex(COLUMN_RACE_DATE)));
                r.setDateTime(c.getInt(c.getColumnIndex(RaceEntry.RACE_DATETIME)));

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
        int rowCount = this.getRowCount(RaceEntry.TABLE);
        if (!getIds) {
            retVal = new String[rowCount];
        } else {
            retVal = new Integer[rowCount];
        }

        String selectQuery;
        if (getIds) {
            selectQuery = "SELECT " + _ID + " FROM " + RaceEntry.TABLE +
                    " order by " + RaceEntry.RACE_DATETIME;
        } else {
            selectQuery = "SELECT * FROM " + RaceEntry.TABLE + " order by " + RaceEntry.RACE_DATETIME;
        }
        Log.e(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        int i = 0;

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                if (!getIds) {
                    retVal[i] = c.getString(c.getColumnIndex(RaceEntry.RACE_NAME)) + "\t(" +
                            DateConverter.fromUnixToDate(
                                    c.getInt(c.getColumnIndex(RaceEntry.RACE_DATETIME)),
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
        values.put(RaceEntry.RACE_NAME, race.getName());
        values.put(RaceEntry.RACE_DESCRIPTION, race.getDesc());
//        values.put(COLUMN_RACE_PLACE, race.getPlace());
//        values.put(COLUMN_RACE_DATE, race.getDate());
        values.put(RaceEntry.RACE_DATETIME, race.getDateTime());

        // updating row
        return db.update(RaceEntry.TABLE, values, _ID + " = ?",
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

        String selectQuery = "SELECT  * FROM " + WheelEntry.TABLE + " wl, "
                + BrandEntry.TABLE + " b WHERE wl." + WheelEntry.RACE_ID + " = " + raceId +
                " AND wl." + WheelEntry.BRAND_ID + " = " + "b." + _ID;

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
        values.put(WheelEntry.BRAND_ID, wheelList.getBrandId());
        values.put(WheelEntry.RACE_ID, wheelList.getRaceId());
        values.put(WheelEntry.TOT_FRONT_WHEEL, wheelList.getTotFrontWheel());
        values.put(WheelEntry.TOT_REAR_WHEEL, wheelList.getTotRearWheel());

        // insert row
        return db.insert(WheelEntry.TABLE, null, values);
    }

    /**
     * get single WheelList
     */
    public WheelList getWheelList(long todo_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + WheelEntry.TABLE + " WHERE "
                + _ID + " = " + todo_id;

        Log.e(TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        WheelList wheelList = new WheelList();
        wheelList.setId(c.getInt(c.getColumnIndex(_ID)));
        wheelList.setBrandId((c.getInt(c.getColumnIndex(WheelEntry.BRAND_ID))));
        wheelList.setRaceId((c.getInt(c.getColumnIndex(WheelEntry.RACE_ID))));
        wheelList.setTotFrontWheel((c.getInt(c.getColumnIndex(WheelEntry.TOT_FRONT_WHEEL))));
        wheelList.setTotRearWheel((c.getInt(c.getColumnIndex(WheelEntry.TOT_REAR_WHEEL))));
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
        values.put(WheelEntry.BRAND_ID, wheelList.getBrandId());
        values.put(WheelEntry.RACE_ID, wheelList.getRaceId());
        values.put(WheelEntry.TOT_FRONT_WHEEL, wheelList.getTotFrontWheel());
        values.put(WheelEntry.TOT_REAR_WHEEL, wheelList.getTotRearWheel());

        // updating row
        return db.update(WheelEntry.TABLE, values, _ID + " = ?",
                new String[]{String.valueOf(wheelList.getId())});
    }

    /**
     * Deleting a wheel_list
     */
    public void deleteWheelList(long wheelListId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(WheelEntry.TABLE, _ID + " = ?",
                new String[]{String.valueOf(wheelListId)});
    }

    /**
     * Deleting a wheel_list
     */
    public void deleteWheelListByRace(long raceId, boolean onlyMissingBrands) {
        SQLiteDatabase db = this.getWritableDatabase();

        if (onlyMissingBrands) {
            db.delete(WheelEntry.TABLE, WheelEntry.RACE_ID + " = ?" + " AND " +
                    WheelEntry.BRAND_ID + " NOT IN (SELECT " + _ID + " FROM " +
                    BrandEntry.TABLE + ")", new String[]{String.valueOf(raceId)});
        } else {
            db.delete(WheelEntry.TABLE, WheelEntry.RACE_ID + " = ?",
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
        values.put(BikeDetailEntry.RACE_ID, bikeDetails.getRaceId());
        values.put(BikeDetailEntry.FRONT_BRAND_ID, bikeDetails.getFrontBrandId());
        values.put(BikeDetailEntry.REAR_BRAND_ID, bikeDetails.getRearBrandId());
        values.put(BikeDetailEntry.INSERTED, bikeDetails.getInserted());

        // insert row
        return db.insert(BikeDetailEntry.TABLE, null, values);
    }

    /**
     * Deleting a Bike Detail
     */
    public void deleteBikeDetailByRace(long raceId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(BikeDetailEntry.TABLE, BikeDetailEntry.RACE_ID + " = ?",
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
        sqlSelect += BikeDetailEntry.RACE_ID + ",";
        sqlSelect += BikeDetailEntry.FRONT_BRAND_ID + ",";
        sqlSelect += BikeDetailEntry.REAR_BRAND_ID + ",";
        sqlSelect += BikeDetailEntry.INSERTED + " ";
        sqlSelect += " FROM " + BikeDetailEntry.TABLE;
        sqlSelect += " WHERE " + BikeDetailEntry.RACE_ID + " = " + raceId;

        db.execSQL(sqlSelect);
    }

    public void restoreBikeDetailFromTemp(int raceId) {
        String sqlSelect;

        sqlSelect = "INSERT INTO " + BikeDetailEntry.TABLE;
        sqlSelect += "(";
        sqlSelect += _ID + ",";
        sqlSelect += BikeDetailEntry.RACE_ID + ",";
        sqlSelect += BikeDetailEntry.FRONT_BRAND_ID + ",";
        sqlSelect += BikeDetailEntry.REAR_BRAND_ID + ",";
        sqlSelect += BikeDetailEntry.INSERTED + ") ";
        sqlSelect += " SELECT ";
        sqlSelect += _ID + ",";
        sqlSelect += BikeDetailEntry.RACE_ID + ",";
        sqlSelect += BikeDetailEntry.FRONT_BRAND_ID + ",";
        sqlSelect += BikeDetailEntry.REAR_BRAND_ID + ",";
        sqlSelect += BikeDetailEntry.INSERTED + " ";
        sqlSelect += " FROM " + TEMP_BIKE_DETAIL;
        sqlSelect += " WHERE " + BikeDetailEntry.RACE_ID + " = " + raceId;

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
        String selectQuery = "SELECT * FROM " + BikeDetailEntry.TABLE;
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
                bikeDetail.setRaceId((c.getInt(c.getColumnIndex(BikeDetailEntry.RACE_ID))));
                bikeDetail.setFrontBrandId((c.getInt(c.getColumnIndex(BikeDetailEntry.FRONT_BRAND_ID))));
                bikeDetail.setRearBrandId((c.getInt(c.getColumnIndex(BikeDetailEntry.REAR_BRAND_ID))));
                bikeDetail.setInserted((c.getInt(c.getColumnIndex(BikeDetailEntry.INSERTED))));

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

        String insertStatement = "INSERT INTO " + WheelEntry.TABLE + " (" +
                WheelEntry.BRAND_ID + "," + WheelEntry.RACE_ID + ")" +
                " SELECT " + BrandEntry.TABLE + "." + _ID + ", " + String.valueOf(raceID) +
                " FROM " + BrandEntry.TABLE + " WHERE " + _ID + " NOT IN (SELECT " +
                WheelEntry.BRAND_ID + " FROM " + WheelEntry.TABLE + " WHERE " +
                WheelEntry.RACE_ID + " = " + String.valueOf(raceID) + ")";
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
        values.put(WheelEntry.TOT_FRONT_WHEEL, 0);
        values.put(WheelEntry.TOT_REAR_WHEEL, 0);

        // updating row
        int retVal = db.update(WheelEntry.TABLE, values, WheelEntry.RACE_ID + " = ?",
                new String[]{String.valueOf(raceID)});
        closeDB();
        return retVal;
    }

    public int getBikeCount(int raceID) {
        String sqlQuery = "SELECT Sum(" + WheelEntry.TOT_FRONT_WHEEL + ") as count FROM " +
                WheelEntry.TABLE + " WHERE " + WheelEntry.RACE_ID + " = " + raceID;
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
            moveOtherBrandSQL = "UPDATE " + BrandEntry.TABLE + " set " + BrandEntry.BRAND_ORDER + " = (" +
                    BrandEntry.BRAND_ORDER + " - 10) WHERE " + BrandEntry.BRAND_ORDER + " = " +
                    String.valueOf(currentOrder + 10);
            moveCurrentBrandSQL = "UPDATE " + BrandEntry.TABLE + " set " + BrandEntry.BRAND_ORDER + " = (" +
                    BrandEntry.BRAND_ORDER + " + 10) WHERE " + _ID + " = " + brandId;
        } else {
            moveOtherBrandSQL = "UPDATE " + BrandEntry.TABLE + " set " + BrandEntry.BRAND_ORDER + " = (" +
                    BrandEntry.BRAND_ORDER + " + 10) WHERE " + BrandEntry.BRAND_ORDER + " = " +
                    String.valueOf(currentOrder - 10);
            moveCurrentBrandSQL = "UPDATE " + BrandEntry.TABLE + " set " + BrandEntry.BRAND_ORDER + " = (" +
                    BrandEntry.BRAND_ORDER + " - 10) WHERE " + _ID + " = " + brandId;
        }

        executeScript(moveOtherBrandSQL);
        executeScript(moveCurrentBrandSQL);

    }

    public void checkBrandTable() {

        long rowCount = getRowCount(BrandEntry.TABLE);
        if (rowCount == 0) {
            SQLiteDatabase db = this.getWritableDatabase();
            this.populateBrand(db);
            db.close();
            Toast.makeText(mContext, mContext.getResources().getString(R.string.tire_table_populated),
                    Toast.LENGTH_LONG).show();
        }

    }

}