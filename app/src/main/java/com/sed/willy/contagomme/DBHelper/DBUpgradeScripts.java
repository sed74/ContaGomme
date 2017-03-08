package com.sed.willy.contagomme.DBHelper;

import android.database.sqlite.SQLiteDatabase;

import com.sed.willy.contagomme.DBContract.BrandContract.BrandEntry;

/**
 * Created by federico.marchesi on 07/03/2017.
 */

public class DBUpgradeScripts {

    private DBUpgradeScripts() {
    }

    public static void upgrade(SQLiteDatabase db, int version) {

        switch (version) {
            case 14:
                db.execSQL("ALTER TABLE " + BrandEntry.TABLE + " ADD COLUMN " +
                        BrandEntry.BRAND_IS_DELETED + " INTEGER NOT NULL DEFAULT 0;");
                break;
            case 15:
                db.execSQL("DROP TABLE IF EXISTS " + BrandEntry.TABLE);
                db.execSQL(BrandEntry.CREATE_TABLE);
                break;
        }

    }
}
