package com.sed.willy.contagomme.DBContract;

import android.provider.BaseColumns;

/**
 * Created by federico.marchesi on 07/03/2017.
 */

public class WheelContract {

    private WheelContract() {
    }

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
                        RaceContract.RaceEntry.TABLE + "(" + _ID + ") ON DELETE CASCADE, " +
                        " FOREIGN KEY (" + BRAND_ID + ") REFERENCES " +
                        BrandContract.BrandEntry.TABLE + "(" + _ID + ") ON DELETE CASCADE)";

    }

}
