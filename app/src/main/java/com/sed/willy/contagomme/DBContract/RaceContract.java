package com.sed.willy.contagomme.DBContract;

import android.provider.BaseColumns;

/**
 * Created by federico.marchesi on 07/03/2017.
 */

public class RaceContract {

    private RaceContract() {
    }

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

}
