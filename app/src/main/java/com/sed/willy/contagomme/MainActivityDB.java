package com.sed.willy.contagomme;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.sed.willy.contagomme.DBContract.RaceContract.RaceEntry;
import com.sed.willy.contagomme.DBContract.ViewsContract.WhellListEntry;
import com.sed.willy.contagomme.DBHelper.DatabaseHelper;
import com.sed.willy.contagomme.DBModel.Race;
import com.sed.willy.contagomme.Dialog.InputDialogRace;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivityDB extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private RaceCursorAdapter raceAdapter;
    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_db);

        listView = (ListView) findViewById(R.id.list);

        new Handler().post(new Runnable() {

            @Override
            public void run() {
                dbHelper = new DatabaseHelper(getBaseContext());
                Cursor c = dbHelper.getCursor(WhellListEntry.VIEW,
                        null);
                raceAdapter = new RaceCursorAdapter(MainActivityDB.this, c);
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
            public InputDialogRace.ValidationResult isInputValid(String input) {
                if (input.isEmpty()) {
                    return new InputDialogRace.ValidationResult(false, R.string.race_name_mandatory);
                }
                return new InputDialogRace.ValidationResult(true, 0);

            }

            @Override
            public void onConfirm(String raceName, String raceDescr, String raceDate) {
                Race race = new Race(raceName, raceDescr, raceDate);
                //Cursor cur = raceAdapter.getCursor();
                dbHelper.createRace(race);
                Cursor d = dbHelper.getCursor(RaceEntry.TABLE,
                        RaceEntry.RACE_DATETIME);
                raceAdapter.swapCursor(d);

            }


        });
        inputDialog.show();
    }

}

