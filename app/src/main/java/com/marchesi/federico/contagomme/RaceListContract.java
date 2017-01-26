package com.marchesi.federico.contagomme;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Federico on 23/01/2017.
 */

public class RaceListContract {
    private static final String SQL_CREATE_RACE_LIST =
            "CREATE TABLE " + RaceList.TABLE_NAME + " (" +
                    RaceList._ID + " INTEGER PRIMARY KEY," +
                    RaceList.COLUMN_RACE_NAME + " TEXT," +
                    RaceList.COLUMN_RACE_PLACE + " TEXT," +
                    RaceList.COLUMN_RACE_DATE + " TEXT," +
                    RaceList.COLUMN_RACE_DESCRIPTION + " TEXT)";

    private static final String SQL_DELETE_RACE_LIST =
            "DROP TABLE IF EXISTS " + RaceList.TABLE_NAME;

    private static final String SQL_CREATE_WHEEL_LIST =
            "CREATE TABLE " + WheelList.TABLE_NAME + " (" +
                    WheelList._ID + " INTEGER PRIMARY KEY," +
                    WheelList.COLUMN_RACE_LIST_ID + " INTEGER," +
                    WheelList.COLUMN_WHEEL_BRAND + " TEXT," +
                    WheelList.COLUMN_TOT_FRONT_WHEEL + " TEXT," +
                    WheelList.COLUMN_TOT_REAR_WHEEL + " TEXT)";

    private static final String SQL_DELETE_WHEEL_LIST =
            "DROP TABLE IF EXISTS " + WheelList.TABLE_NAME;

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private RaceListContract() {
    }

    /* Inner class that defines the table contents */
    public static class RaceList implements BaseColumns {
        public static final String TABLE_NAME = "race_list";
        public static final String COLUMN_RACE_NAME = "race_name";
        public static final String COLUMN_RACE_PLACE = "race_place";
        public static final String COLUMN_RACE_DATE = "race_date";
        public static final String COLUMN_RACE_DESCRIPTION = "race_desc";
    }

    public static class WheelList implements BaseColumns {
        public static final String TABLE_NAME = "wheel_list";
        public static final String COLUMN_RACE_LIST_ID = "race_list_id";
        public static final String COLUMN_WHEEL_BRAND = "wheel_brand";
        public static final String COLUMN_TOT_FRONT_WHEEL = "tot_front_wheel";
        public static final String COLUMN_TOT_REAR_WHEEL = "tot_rear_wheel";

    }


    public class RaceListDbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "FeedReader.db";

        public RaceListDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_RACE_LIST);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_RACE_LIST);
            onCreate(db);
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }
}
