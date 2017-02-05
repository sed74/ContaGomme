package com.marchesi.federico.contagomme.WheelCountPerRace;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.marchesi.federico.contagomme.DBHelper.DatabaseHelper;
import com.marchesi.federico.contagomme.DBModel.WheelList;
import com.marchesi.federico.contagomme.MainActivity;
import com.marchesi.federico.contagomme.R;

import java.util.ArrayList;

public class WheelCountPerRaceActivity extends AppCompatActivity {

    String raceName;
    int raceID;
    private DatabaseHelper dbHelper;
    private WheelCountPerRaceCursorAdapter wheelCountAdapter;
    private ListView listView;
    private ArrayList<WheelList> wheelLists;
    private boolean mAutoNext;
    private boolean mUseHTML;
    private int mBikeCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.list);
        raceID = getIntent().getIntExtra(MainActivity.INTENT_NAME_RACE_ID, 0);
        raceName = getIntent().getStringExtra(MainActivity.INTENT_NAME_RACE_NAME);

        Button nextButton = (Button) findViewById(R.id.button_next);
        nextButton.setVisibility(mAutoNext ? View.GONE : View.VISIBLE);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextBike();
            }
        });

        new Handler().post(new Runnable() {

            @Override
            public void run() {
                dbHelper = new DatabaseHelper(getBaseContext());
                Cursor c = dbHelper.getCursorById(DatabaseHelper.VIEW_RACES_WHEEL_LIST,
                        "raceId", raceID);
                wheelCountAdapter = new WheelCountPerRaceCursorAdapter(WheelCountPerRaceActivity.this, c);
                listView.setAdapter(wheelCountAdapter);
                wheelLists = wheelCountAdapter.populateArray(c);

                wheelCountAdapter.setOnChangeListener(new WheelCountPerRaceCursorAdapter.OnChange() {
                    @Override
                    public void onSelectionChange(boolean frontSelected, boolean rearSelected) {

                        if (frontSelected && rearSelected) {
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    nextBike();
                                }
                            }, 500);

                        }
//                        wheelCountAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

        loadPrefs(this);
        setupActionBar();
    }

    private void nextBike() {
        resetArray();
        Cursor c = dbHelper.getCursorById(
                DatabaseHelper.VIEW_RACES_WHEEL_LIST, "raceId", raceID);
        wheelCountAdapter.swapCursor(c);
        wheelCountAdapter.notifyDataSetChanged();

    }

    private void resetArray() {
        for (WheelList array : wheelLists) {
            array.resetSelection();
        }

    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(raceName);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_single_race, menu);
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
            case R.id.reset:
                final ArrayList<WheelList> tempList = new ArrayList<>(wheelLists);
                dbHelper.resetRace(raceID);
                resetWheelCounter(wheelLists);
                swapCursor();
                View view = findViewById(R.id.activity_main);

                Snackbar snackbar = Snackbar
                        .make(view, getResources().getString(R.string.race_reset), Snackbar.LENGTH_LONG)
                        .setDuration(5000)
                        .setAction(getResources().getString(R.string.reset_undo), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                wheelLists.addAll(tempList);
                                dbHelper.populateWheelList(wheelLists);
                                swapCursor();

                                Snackbar.make(view, getResources().getString(R.string.done_undo),
                                        Snackbar.LENGTH_SHORT).show();

                            }
                        });

                snackbar.show();
            case R.id.send_email:

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void resetWheelCounter(ArrayList<WheelList> wheelLists) {
        for (WheelList list : wheelLists) {
            list.setTotFrontWheel(0);
            list.setTotRearWheel(0);
        }

    }

    private void swapCursor() {
        Cursor c = dbHelper.getCursorById(DatabaseHelper.VIEW_RACES_WHEEL_LIST,
                "raceId", raceID);
        Cursor old = wheelCountAdapter.swapCursor(c);
//    old.close();

    }

    public void loadPrefs(Context mContext) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);

        PackageInfo packageInfo = null;
        String packageVersion = "";
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            packageVersion = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String appVersion = sp.getString(MainActivity.APP_VERSION, "0");

        if (!appVersion.equalsIgnoreCase(packageVersion)) {
            // a new version is running, have to clear the saved data
            SharedPreferences.Editor mEdit1 = sp.edit();
            mEdit1.clear();
            mEdit1.apply();
            Toast.makeText(this, getResources().getString(R.string.new_version_detected),
                    Toast.LENGTH_LONG).show();
            return;
        }

        mAutoNext = sp.getBoolean(MainActivity.AUTO_NEXT, false);
        mUseHTML = sp.getBoolean(MainActivity.USE_HTML, false);
        mBikeCounter = sp.getInt(MainActivity.BIKE_COUNTER, 0);
    }
}

