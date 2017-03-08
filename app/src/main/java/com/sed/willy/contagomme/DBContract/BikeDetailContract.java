package com.sed.willy.contagomme.DBContract;

import android.provider.BaseColumns;

/**
 * Created by federico.marchesi on 07/03/2017.
 */

public class BikeDetailContract {

    private BikeDetailContract() {
    }

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

}
