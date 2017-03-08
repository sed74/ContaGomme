package com.sed.willy.contagomme.DBContract;

import android.provider.BaseColumns;


/**
 * DB Contract
 * Contains all constants for Table, View, and Column names, plus other useful constants.
 * the root class contains <strong>global</strong> useful values.
 * Each subclass contains info about a single table/view
 */

public final class ContaGommeContract {

    //To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private ContaGommeContract() {
    }

    /**
     * Table that holds the count of all tires for each brand, for both Front and rear Tire
     */
    public static final class WheelEntry implements BaseColumns {

        public static final String TABLE = "wheel_list";

        // WHEEL Table - column names
        public static final String RACE_ID = "wheel_race_id";

        public static final String BRAND_ID = "wheel_brand_id";

        public static final String TOT_FRONT_WHEEL = "tot_front_wheel";

        public static final String TOT_REAR_WHEEL = "tot_rear_wheel";

        // WHEEL_LIST table create statement
        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE +
                        "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        RACE_ID + " INTEGER," +
                        BRAND_ID + " INTEGER," +
                        TOT_FRONT_WHEEL + " INTEGER," +
                        TOT_REAR_WHEEL + " INTEGER," +
                        " FOREIGN KEY (" + RACE_ID + ") REFERENCES " +
                        RaceEntry.TABLE + "(" + _ID + ") ON DELETE CASCADE, " +
                        " FOREIGN KEY (" + BRAND_ID + ") REFERENCES " +
                        BrandEntry.TABLE + "(" + _ID + ") ON DELETE CASCADE)";

    }

    /**
     * Table the holds the count of all bikes, telling their front and rear tires.
     */
    public static final class BikeDetailEntry implements BaseColumns {

        public static final String TABLE = "bike_details";

        // BIKE_DETAIL - column names
        public static final String RACE_ID = "bike_race_id";

        public static final String FRONT_BRAND_ID = "bike_front_brand_id";

        public static final String REAR_BRAND_ID = "bike_rear_brand_id";

        public static final String INSERTED = "bike_inserted";


        // BIKE_DETAILS table create statement
        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE +
                        "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        RACE_ID + " INTEGER," +
                        FRONT_BRAND_ID + " INTEGER," +
                        REAR_BRAND_ID + " INTEGER," +
                        INSERTED + " INTEGER," +
                        " FOREIGN KEY (" + RACE_ID + ") REFERENCES " +
                        TABLE + "(" + _ID + ") ON DELETE CASCADE, " +
                        " FOREIGN KEY (" + FRONT_BRAND_ID + ") REFERENCES " +
                        TABLE + "(" + _ID + ") ON DELETE CASCADE, " +
                        " FOREIGN KEY (" + REAR_BRAND_ID + ") REFERENCES " +
                        TABLE + "(" + _ID + ") ON DELETE CASCADE)";

    }

    /**
     * Table for the list of the races
     */
    public static final class RaceEntry implements BaseColumns {

        public static final String TABLE = "races";

        // BRANDS Table - column names
        public static final String RACE_NAME = "race_name";

        public static final String RACE_PLACE = "race_place";

        public static final String RACE_DATETIME = "race_datetime";

        public static final String RACE_DESCRIPTION = "race_desc";

        // RACES table create statement
        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE +
                        "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        RACE_NAME + " TEXT," +
                        RACE_PLACE + " TEXT," +
                        RACE_DATETIME + " INTEGER," +
                        RACE_DESCRIPTION + " TEXT)";

    }

    /**
     * Table for the list of the Tire Brand
     */
    public static final class BrandEntry implements BaseColumns {

        public static final String TABLE = "brands";


        // BRANDS Table - column names
        public static final String BRAND_NAME = "brand_name";
        public static final String BRAND_ORDER = "brand_order";
        public static final String BRAND_IS_DELETED = "is_deleted";

        // Possibile Values for COLUMN_BRAND_IS_DELETED
        public static final int ROW_DELETED = 1;
        public static final int ROW_NOT_DELETED = 0;

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE +
                        "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        BRAND_ORDER + " INTEGER," +
                        BRAND_IS_DELETED + " INTEGER NOT NULL DEFAULT 0 " +
                        "CHECK (" + BRAND_IS_DELETED + " IN (0,1)), " +
                        BRAND_NAME + " TEXT )";
        /*
        Changes:
        Vers 15: added CHECK on COLUMN_BRAND_IS_DELETED
         */

    }

    /**
     * View to group info from a single race, in order to show the amount of bikes with a certain
     * set of tires
     */
    public static final class StatsEntry implements BaseColumns {

        public static final String VIEW = "statistics";

        public static final String CREATE_VIEW =
                "CREATE VIEW " + VIEW + " AS " +
                        "SELECT count(1) as counter, " +
                        "front_brand." + BrandEntry.BRAND_NAME + " as front, " +
                        "rear_brand." + BrandEntry.BRAND_NAME + " as rear " +
                        " FROM " + BikeDetailEntry.TABLE + ", " + BrandEntry.TABLE + " front_brand, " +
                        BrandEntry.TABLE + " rear_brand " +
                        " WHERE " +
                        "front_brand." + _ID + " = " + BikeDetailEntry.TABLE + "." + BikeDetailEntry.FRONT_BRAND_ID +
                        " AND " +
                        "rear_brand." + _ID + " = " + BikeDetailEntry.TABLE + "." + BikeDetailEntry.REAR_BRAND_ID +
                        " AND " +
                        BikeDetailEntry.FRONT_BRAND_ID + " = " + BikeDetailEntry.REAR_BRAND_ID +
                        " GROUP BY " + "front_brand." + BrandEntry.BRAND_NAME + ", rear_brand." + BrandEntry.BRAND_NAME +
                        " UNION " +
                        "SELECT count(1), " +
                        "front_brand." + BrandEntry.BRAND_NAME + " as front, " +
                        "rear_brand." + BrandEntry.BRAND_NAME + " as rear " +
                        " FROM " + BikeDetailEntry.TABLE + ", " + BrandEntry.TABLE + " front_brand, " +
                        BrandEntry.TABLE + " rear_brand " +
                        " WHERE " +
                        "front_brand." + _ID + " = " + BikeDetailEntry.TABLE + "." + BikeDetailEntry.FRONT_BRAND_ID +
                        " AND " +
                        "rear_brand." + _ID + " = " + BikeDetailEntry.TABLE + "." + BikeDetailEntry.REAR_BRAND_ID +
                        " AND " +
                        BikeDetailEntry.FRONT_BRAND_ID + " <> " + BikeDetailEntry.REAR_BRAND_ID +
                        " GROUP BY " + "front_brand." + BrandEntry.BRAND_NAME + ", rear_brand." + BrandEntry.BRAND_NAME;

    }

    /**
     * View to group info from a single race, in order to show the amount of front and rear tire for
     * each brand.
     */
    public static final class WheelListEntry implements BaseColumns {

        // RACES_WHEEL_LIST VIEW
        public static final String VIEW = "view_races_wheel_list";

        // RACES_WHEEL_LIST COLUMNS
        public static final String RACE_ID = "race_id";
        public static final String BRAND_ID = "brand_id";
        public static final String BRAND_NAME = BrandEntry.BRAND_NAME;
        public static final String RACE_NAME = RaceEntry.RACE_NAME;
        public static final String TOT_FRONT_WHEEL = WheelEntry.TOT_FRONT_WHEEL;
        public static final String TOT_REAR_WHEEL = WheelEntry.TOT_REAR_WHEEL;

        // RACES_WHEEL_LIST VIEW create statement
        public static final String CREATE_VIEW =
                "CREATE VIEW " + VIEW +
                        " AS SELECT " + WheelEntry.TABLE + "." + _ID + ", " +
                        RaceEntry.TABLE + "." + _ID + " " + RACE_ID + ", " +
                        BrandEntry.TABLE + "." + _ID + " " + BRAND_ID + ", " +
                        RaceEntry.TABLE + "." + RaceEntry.RACE_NAME + " " + RACE_NAME + ", " +
                        BrandEntry.TABLE + "." + BrandEntry.BRAND_NAME + " " + BRAND_NAME + ", " +
                        TOT_FRONT_WHEEL + ", " +
                        TOT_REAR_WHEEL +
                        " FROM " + WheelEntry.TABLE + ", " +
                        BrandEntry.TABLE + ", " + RaceEntry.TABLE +
                        " WHERE " + WheelEntry.TABLE + "." + WheelEntry.BRAND_ID + " = " +
                        BrandEntry.TABLE + "." + _ID +
                        " AND " + WheelEntry.TABLE + "." + WheelEntry.RACE_ID + " = " +
                        RaceEntry.TABLE + "." + _ID +
                        " ORDER BY " + BrandEntry.BRAND_ORDER;


    }
}
