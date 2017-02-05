package com.marchesi.federico.contagomme.WheelCountPerRace;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.marchesi.federico.contagomme.DBHelper.DatabaseHelper;
import com.marchesi.federico.contagomme.DBModel.Race;
import com.marchesi.federico.contagomme.DBModel.WheelList;
import com.marchesi.federico.contagomme.Dialog.InputDialogRace;
import com.marchesi.federico.contagomme.MainActivity;
import com.marchesi.federico.contagomme.R;
import com.marchesi.federico.contagomme.RaceCursorAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class WheelCountPerRaceActivity extends AppCompatActivity {

    String raceName;
    private DatabaseHelper dbHelper;
    private WheelCountPerRaceCursorAdapter wheelCountAdapter;
    private ListView listView;
    private ArrayList<WheelList> wheelLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.list);
        final int raceID = getIntent().getIntExtra(MainActivity.INTENT_NAME_RACE_ID, 0);
        raceName = getIntent().getStringExtra(MainActivity.INTENT_NAME_RACE_NAME);


        new Handler().post(new Runnable() {

            @Override
            public void run() {
                dbHelper = new DatabaseHelper(getBaseContext());
//                int count = dbHelper.getRowCount(DatabaseHelper.VIEW_RACES_WHEEL_LIST);
//                Toast.makeText(WheelCountPerRaceActivity.this, String.valueOf(count), Toast.LENGTH_SHORT).show();
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
                                    resetArray();
                                    Cursor c = dbHelper.getCursorById(DatabaseHelper.VIEW_RACES_WHEEL_LIST,
                                            "raceId", raceID);
                                    wheelCountAdapter.swapCursor(c);
                                    wheelCountAdapter.notifyDataSetChanged();
                                }
                            }, 500);

                        }
//                        wheelCountAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

        setupActionBar();
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
                Toast.makeText(this, "ciao", Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    private void addRace() {

        InputDialogRace inputDialog = new InputDialogRace(this, R.string.add_race_dialog_title,
                R.string.add_race_dialog_hint);

        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());
        // formattedDate have current date/time


        inputDialog.setRaceDate(formattedDate);
        inputDialog.setInputListener(new InputDialogRace.InputListener() {
            @Override
            public InputDialogRace.ValidationResult isInputValid(String input) {
                return null;
            }

            @Override
            public void onConfirm(String raceName, String raceDescr, String raceDate) {
                Race race = new Race(raceName, raceDescr, raceDate);
                //Cursor cur = wheelCountAdapter.getCursor();
                dbHelper.createRace(race);
                Cursor d = dbHelper.getCursor(DatabaseHelper.TABLE_RACES,
                        DatabaseHelper.COLUMN_RACE_DATETIME);
                wheelCountAdapter.swapCursor(d);
                //Toast.makeText(MainActivity.this, getResources().getString(R.string.data_saved), Toast.LENGTH_SHORT).show();
            }


        });
        inputDialog.show();
    }


}

