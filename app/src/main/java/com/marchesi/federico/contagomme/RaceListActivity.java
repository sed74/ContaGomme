package com.marchesi.federico.contagomme;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.marchesi.federico.contagomme.DBHelper.DatabaseHelper;
import com.marchesi.federico.contagomme.DBModel.Race;
import com.marchesi.federico.contagomme.Dialog.InputDialogRace;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class RaceListActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private RaceCursorAdapter raceAdapter;
    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand_list);

        listView = (ListView) findViewById(R.id.list);


        new Handler().post(new Runnable() {

            @Override
            public void run() {
                dbHelper = new DatabaseHelper(getBaseContext());
                Cursor c = dbHelper.getCursor(DatabaseHelper.TABLE_RACES,
                        DatabaseHelper.COLUMN_RACE_DATE);
                raceAdapter = new RaceCursorAdapter(RaceListActivity.this, c);
                listView.setAdapter(raceAdapter);
            }

        });


        Button addButton = (Button) findViewById(R.id.button_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRace();

            }
        });
        setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dbHelper != null) {
            dbHelper.close();
        }
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
            public InputDialogRace.ValidationResult isInputValid(String newCoffeeType) {
                if (newCoffeeType.isEmpty()) {
//                    return new InputDialog.ValidationResult(false, R.string.error_empty_name);
                }
                return new InputDialogRace.ValidationResult(true, 0);
            }

            @Override
            public void onConfirm(String raceName, String raceDescr, String raceDate) {
                Race race = new Race(raceName, raceDescr, raceDate);
                //Cursor cur = raceAdapter.getCursor();
                dbHelper.createRace(race);
                Cursor d = dbHelper.getCursor(DatabaseHelper.TABLE_RACES,
                        DatabaseHelper.COLUMN_RACE_DATE);
                raceAdapter.swapCursor(d);
                //Toast.makeText(MainActivity.this, getResources().getString(R.string.data_saved), Toast.LENGTH_SHORT).show();
            }


        });
        inputDialog.show();
    }


}

