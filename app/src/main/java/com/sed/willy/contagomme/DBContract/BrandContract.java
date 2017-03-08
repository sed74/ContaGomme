package com.sed.willy.contagomme.DBContract;

import android.provider.BaseColumns;

/**
 * Created by federico.marchesi on 07/03/2017.
 */

public class BrandContract {

    private BrandContract() {
    }

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

}
