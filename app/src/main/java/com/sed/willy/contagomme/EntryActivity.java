package com.sed.willy.contagomme;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.sed.willy.contagomme.DBContract.ContaGommeContract.BrandEntry;
import com.sed.willy.contagomme.DBContract.ContaGommeContract.WheelEntry;
import com.sed.willy.contagomme.DBHelper.DatabaseHelper;
import com.sed.willy.contagomme.Utils.FileClass;
import com.sed.willy.contagomme.wheelcount.WheelCountPerRaceActivity;

public class EntryActivity extends AppCompatActivity {

    public static final String APP_VERSION = "app_version";
    public static final String INTENT_NAME_RACE_ID = "race_id";
    public static final String INTENT_NAME_RACE_NAME = "race_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

//        checkVersion();

        new DatabaseHelper(this).checkBrandTable();

        RelativeLayout openRaceLayout = (RelativeLayout) findViewById(R.id.open_race);
        RelativeLayout openTireListLayout = (RelativeLayout) findViewById(R.id.open_tire_list);
        RelativeLayout openRaceListLayout = (RelativeLayout) findViewById(R.id.open_race_list);

        RelativeLayout.OnClickListener buttonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.open_race:
                        AlertDialog.Builder b = new AlertDialog.Builder(EntryActivity.this);

                        b.setTitle(getResources().getString(R.string.select_race_dialog));
                        DatabaseHelper dbHelper = new DatabaseHelper(getBaseContext());
                        final String[] raceNames = (String[]) dbHelper.getRacesNameArray();
                        final Integer[] raceIDs = (Integer[]) dbHelper.getRacesIdArray();
                        if (raceNames.length == 0) {
                            Toast toast = Toast.makeText(EntryActivity.this,
                                    getResources().getString(R.string.no_race_found),
                                    Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return;
                        }
                        dbHelper.close();
                        b.setCancelable(true);
                        b.setItems(raceNames, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                openRaceActivity(raceIDs[which], raceNames[which]);
                            }

                        });
                        b.show();

                        break;
                    case R.id.open_tire_list:
//                        Intent intent = new Intent(EntryActivity.this, BrandListActivity.class);
//                        startActivity(intent);
                        Intent newIntent = new Intent(EntryActivity.this, TireListRecyclerActivity.class);
                        startActivity(newIntent);
                        break;

                    case R.id.open_race_list:
                        Intent racesIntent = new Intent(EntryActivity.this, RaceListActivity.class);
                        startActivity(racesIntent);

                        break;

                }
            }
        };

        openRaceLayout.setOnClickListener(buttonListener);
        openTireListLayout.setOnClickListener(buttonListener);
        openRaceListLayout.setOnClickListener(buttonListener);
        FileClass.checkForArchiveAccess(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.entry_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.settings:
//                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                break;
            case R.id.backup_db:
                backDB();

                break;
            case R.id.restore_db:
                restoreDB();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void restoreDB() {
        final Context context = EntryActivity.this;
        new AlertDialog.Builder(context)
                //set message, title, and icon
                .setTitle(context.getResources().getString(R.string.restore_db_title))
                .setMessage(context.getResources().getString(R.string.restore_db_text))
                .setIcon(R.drawable.ic_database_plus_black_18dp)

                .setPositiveButton(context.getResources().getString(R.string.restore_button), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (FileClass.restoreDataBase(getBaseContext()) == 1) {
                            Toast.makeText(context,
                                    getResources().getString(R.string.restore_successfull),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(context.getResources().getString(R.string.dialog_cancel_button), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void backDB() {
        final Context context = EntryActivity.this;
        new AlertDialog.Builder(context)
                //set message, title, and icon
                .setTitle(context.getResources().getString(R.string.backup_db_title))
                .setMessage(context.getResources().getString(R.string.backup_db_text))
                .setIcon(R.drawable.ic_database_black_18dp)

                .setPositiveButton(context.getResources().getString(R.string.backup_button), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (FileClass.backUpDataBase(getBaseContext()) == 1) {
                            Toast.makeText(context, getResources().getString(R.string.backup_successfull),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(context.getResources().getString(R.string.dialog_cancel_button), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void openRaceActivity(Integer raceID, String raceName) {
        //  check if wheel_list exists for the race
        DatabaseHelper dbHelper = new DatabaseHelper(getBaseContext());
        int brandCount = dbHelper.getRowCount(BrandEntry.TABLE);
        int rowCount = dbHelper.getRowCount(WheelEntry.TABLE,
                WheelEntry.RACE_ID, raceID);

        if (rowCount == 0 || rowCount < brandCount) {
            // table wheel_list has to be build/rebuild
            dbHelper.populateWheelListTableFromRaceId(raceID);
        } else if (rowCount == 0 || rowCount > brandCount) {
            // table wheel_list has to be build/rebuild
            dbHelper.populateWheelListTableFromRaceId(raceID);
        }
        Intent wheelCountIntent = new Intent(this, WheelCountPerRaceActivity.class);
        wheelCountIntent.putExtra(INTENT_NAME_RACE_ID, raceID);
        wheelCountIntent.putExtra(INTENT_NAME_RACE_NAME, raceName);
        startActivity(wheelCountIntent);
    }

    public void saveVersion(String packageVersion) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor mEdit1 = sp.edit();

        mEdit1.putString(APP_VERSION, packageVersion);
        mEdit1.apply();

    }

    public void checkVersion() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        PackageInfo packageInfo = null;
        String packageVersion = "";
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            packageVersion = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String appVersion = sp.getString(APP_VERSION, "0");

        if (!appVersion.equalsIgnoreCase(packageVersion)) {
            // a new version is running, have to clear the saved data
            SharedPreferences.Editor mEdit1 = sp.edit();
            mEdit1.clear();
            mEdit1.apply();
//            Toast.makeText(this, getResources().getString(R.string.new_version_detected),
//                    Toast.LENGTH_LONG).show();
            saveVersion(packageVersion);
        }


    }

}
