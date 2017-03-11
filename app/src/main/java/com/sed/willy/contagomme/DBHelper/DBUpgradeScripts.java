package com.sed.willy.contagomme.DBHelper;

import android.database.sqlite.SQLiteDatabase;

import com.sed.willy.contagomme.DBContract.ContaGommeContract.BrandEntry;


/**
 * Class tha implements a method to upgrade database version by version, in order to avoid
 * DROP/DELETE every time.<br>
 * This should reduce the data loss.<br>
 * The method is called once for each version between <i>oldVersion</i> and <i>newVersion</i> of the
 * Database
 */

public class DBUpgradeScripts {

    private DBUpgradeScripts() {
    }

    /**
     * Method called in order to apply upgrade script in order, in order to avoid the drop/recreate
     * of tables
     *
     * @param db:        an open Writeable connection to the DB
     * @param toVersion: the version TO which we are upgrading.
     */
    public static void upgrade(SQLiteDatabase db, int toVersion) {

        switch (toVersion) {
            case 14:
                db.execSQL("ALTER TABLE " + BrandEntry.TABLE + " ADD COLUMN " +
                        BrandEntry.BRAND_IS_DELETED + " INTEGER NOT NULL DEFAULT 0;");
                break;
            case 15:
                // Check on IS_DELETED field is added
                db.execSQL("DROP TABLE IF EXISTS " + BrandEntry.TABLE);
                db.execSQL(BrandEntry.CREATE_TABLE);
                break;
            case 16:
                //do nothing, simply added extension to DB name.
                break;
        }

    }
}
