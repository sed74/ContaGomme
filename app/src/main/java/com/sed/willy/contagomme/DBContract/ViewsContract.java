package com.sed.willy.contagomme.DBContract;

import android.provider.BaseColumns;

import com.sed.willy.contagomme.DBContract.BikeDetailContract.BikeDetailEntry;
import com.sed.willy.contagomme.DBContract.BrandContract.BrandEntry;
import com.sed.willy.contagomme.DBContract.RaceContract.RaceEntry;
import com.sed.willy.contagomme.DBContract.WheelContract.WheelEntry;

/**
 * Created by federico.marchesi on 07/03/2017.
 */

public class ViewsContract {

    private ViewsContract() {
    }

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
