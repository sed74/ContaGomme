package com.marchesi.federico.contagomme.wheelcount;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
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
import android.widget.TextView;
import android.widget.Toast;

import com.marchesi.federico.contagomme.DBHelper.DatabaseHelper;
import com.marchesi.federico.contagomme.DBModel.BikeDetails;
import com.marchesi.federico.contagomme.DBModel.WheelList;
import com.marchesi.federico.contagomme.DateConverter;
import com.marchesi.federico.contagomme.R;
import com.marchesi.federico.contagomme.SettingsActivity;
import com.marchesi.federico.contagomme.Utils.FileClass;

import java.util.ArrayList;

public class WheelCountPerRaceActivity extends AppCompatActivity {

    public static final String INTENT_NAME_RACE_ID = "race_id";
    public static final String INTENT_NAME_RACE_NAME = "race_name";
    private String raceName;
    private int raceID;
    private DatabaseHelper dbHelper;
    private WheelCountPerRaceCursorAdapter wheelCountAdapter;
    private ListView listView;
    private ArrayList<WheelList> wheelLists;
    private boolean mAutoNext;
    private int mBikeCounter;
    private String mEmailRecipient;
    private boolean mAttachFile;
    private int mAttachType;
    private TextView headerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.list);
        raceID = getIntent().getIntExtra(INTENT_NAME_RACE_ID, 0);
        raceName = getIntent().getStringExtra(INTENT_NAME_RACE_NAME);

        loadPrefs(this);

        final Button nextButton = (Button) findViewById(R.id.button_next);
        nextButton.setVisibility(mAutoNext ? View.GONE : View.VISIBLE);
        headerTextView = (TextView) findViewById(R.id.moto_inserite);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextBike();
                nextButton.setEnabled(false);
            }
        });

        new Handler().post(new Runnable() {

            @Override
            public void run() {
                setAdapter();
                mBikeCounter = dbHelper.getBikeCount(raceID);
                updateHeader();
                wheelCountAdapter.setOnChangeListener(new WheelCountPerRaceCursorAdapter.OnChange() {
                    @Override
                    public void onSelectionChange(boolean frontSelected, boolean rearSelected) {

                        if (frontSelected && rearSelected) {
                            if (mAutoNext) {
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        nextBike();
                                        mBikeCounter++;
                                        updateHeader();
                                    }
                                }, 100);
                            }
                        }
                        nextButton.setEnabled(frontSelected && rearSelected);
//                        wheelCountAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onSelectionChange(int frontSelected, int rearSelected) {
                        insertBikeDetail(frontSelected, rearSelected);
                    }
                });
            }
        });

        setupActionBar();
    }

    private void insertBikeDetail(int frontSelected, int rearSelected) {
        int currentTimeStamp = (int) DateConverter.getCurrentTimeStampUnix();
        BikeDetails bikeDetail = new BikeDetails(raceID, frontSelected, rearSelected,
                currentTimeStamp);
        dbHelper.createBikeDetails(bikeDetail);

    }

    private void setAdapter() {

        if (dbHelper != null) dbHelper.closeDB();
        dbHelper = new DatabaseHelper(getBaseContext());
        Cursor c = dbHelper.getCursorById(DatabaseHelper.VIEW_RACES_WHEEL_LIST,
                DatabaseHelper.COLUMN_VIEW_RACE_ID, raceID);
        if (wheelCountAdapter != null) wheelCountAdapter = null;
        wheelCountAdapter = new WheelCountPerRaceCursorAdapter(WheelCountPerRaceActivity.this, c);
        listView.setAdapter(wheelCountAdapter);
        wheelLists = wheelCountAdapter.populateArray(c);

    }

    private void nextBike() {
        resetArray();
        Cursor c = dbHelper.getCursorById(
                DatabaseHelper.VIEW_RACES_WHEEL_LIST, DatabaseHelper.COLUMN_VIEW_RACE_ID, raceID);
        wheelCountAdapter.swapCursor(c);
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
                // back up variables
                final ArrayList<WheelList> tempList = copyValues(wheelLists);
                final int bikeCounter = mBikeCounter;

                final DatabaseHelper dbTemp = new DatabaseHelper(getBaseContext());

                dbTemp.createBackUpFroBikeDetails(raceID);
                dbTemp.deleteBikeDetailByRace(raceID);
                Toast.makeText(this, "Record after delete: " + dbHelper.getRowCount(DatabaseHelper.TABLE_BIKE_DETAILS,
                        DatabaseHelper.COLUMN_BIKE_RACE_ID, raceID), Toast.LENGTH_SHORT).show();
                mBikeCounter = 0;
                dbHelper.resetRace(raceID);
                resetWheelCounter(wheelLists);
                swapCursor();
                updateHeader();
                View view = findViewById(R.id.activity_main);

                Snackbar snackbar = Snackbar
                        .make(view, getResources().getString(R.string.race_reset), Snackbar.LENGTH_LONG)
                        .setDuration(5000)
                        .setAction(getResources().getString(R.string.reset_undo), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                dbTemp.restoreBikeDetailFromTemp(raceID);
                                Toast.makeText(WheelCountPerRaceActivity.this, "Record after restore: " +
                                        dbHelper.getRowCount(DatabaseHelper.TABLE_BIKE_DETAILS,
                                                DatabaseHelper.COLUMN_BIKE_RACE_ID, raceID), Toast.LENGTH_SHORT).show();
                                dbTemp.close();
                                dbHelper.populateWheelListTableFromRaceId(tempList);
                                wheelLists = wheelCountAdapter.populateArray(getCursor());
                                mBikeCounter = bikeCounter;
                                swapCursor();
                                updateHeader();
                                Snackbar.make(view, getResources().getString(R.string.done_undo),
                                        Snackbar.LENGTH_SHORT).show();
                            }
                        });

                snackbar.show();
                break;
            case R.id.send_email:
                sendEmail();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private ArrayList<WheelList> copyValues(ArrayList<WheelList> arrayOrigin) {
        ArrayList<WheelList> retValue = new ArrayList<>();

        for (WheelList wheel : arrayOrigin) {
            retValue.add(new WheelList(wheel.getId(), wheel.getRaceId(), wheel.getBrandId(),
                    wheel.getTotFrontWheel(), wheel.getTotRearWheel()));
        }
        return retValue;
    }

    private void resetWheelCounter(ArrayList<WheelList> wheelLists) {
        for (WheelList list : wheelLists) {
            list.setTotFrontWheel(0);
            list.setTotRearWheel(0);
        }

    }

    private Cursor getCursor() {
        return dbHelper.getCursorById(DatabaseHelper.VIEW_RACES_WHEEL_LIST,
                DatabaseHelper.COLUMN_VIEW_RACE_ID, raceID);
    }

    private void swapCursor() {

        Cursor c = getCursor();

//        Cursor old =
        wheelCountAdapter.swapCursor(c);
//    old.close();

    }

    public void loadPrefs(Context mContext) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);

        mAutoNext = sp.getBoolean(getResources().getString(R.string.pref_auto_continue), true);
        mEmailRecipient = sp.getString(SettingsActivity.KEY_PREF_EMAIL_RECIPIENT, "");
        mAttachFile = sp.getBoolean(SettingsActivity.KEY_PREF_ATTACH_FILE, false);
        mAttachType = Integer.parseInt(sp.getString(
                SettingsActivity.KEY_PREF_ATTACHMENT_TYPE, "1"));
    }

    public void sendEmail() {

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("text/html");
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this

        String emailContent = getEmailBody();

//        String currentDateTimeString = DateFormat.getDateInstance().format(new Date());
        String subject = String.format(getResources().getString(R.string.subject_no_date),
                raceName.replaceAll("\\(", " ").replaceAll("\\)", " "));
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);

        if (!mEmailRecipient.isEmpty()) {

            String[] to = mEmailRecipient.split(",");
            intent.putExtra(Intent.EXTRA_EMAIL, to);
        }

        if (mAttachFile) {
            String emailAttach = "";
            FileClass.checkForArchiveAccess(this);

            switch (mAttachType) {
                case 0: // TXT
                    emailAttach = emailContent;
                    break;
                case 1: // CSV
                    emailAttach = getEmailBodyCSV();
                    break;
                case 2: // HTML
                    emailAttach = getEmailBodyHTML();
                    break;
            }

            String[] mTestArray = getResources().getStringArray(R.array.pref_file_type);
            String createFileName = subject.replaceAll("\t", "_").replaceAll("\\(", "")
                    .replaceAll("\\)", "").replaceAll(" ", "_").replaceAll("/", "_");

            String fileName = FileClass.createFile(createFileName, emailAttach, mTestArray[mAttachType]);
            if (fileName.isEmpty()) return;
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + fileName));
        }

//        Toast.makeText(this, subject, Toast.LENGTH_SHORT).show();
        intent.putExtra(Intent.EXTRA_TEXT, emailContent);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private String getEmailBodyHTML() {

        String emailContent = "";//"<html><body><br>";

        emailContent += getResources().getString(R.string.race_name) + " <b>" + raceName + "</b><br><br>";

        for (WheelList t : wheelLists) {
            emailContent += "<p>";
            emailContent += "<b>" + t.getBrandName() + "</b><br>";
            emailContent += getResources().getString(R.string.front_tires) + ":: " + t.getTotFrontWheel() + "<br>";
            emailContent += getResources().getString(R.string.rear_tires) + ": " + t.getTotRearWheel() + "<br>";
            emailContent += "</p>";
        }
        emailContent += "<p>";
        emailContent += "<b>" + getResources().getString(R.string.total_no_of_bikes) + "</b>";
        emailContent += " " + mBikeCounter;
        emailContent += "</p>";
        return emailContent;
    }

    private String getEmailBody() {

        String emailContent = "";

        emailContent += getResources().getString(R.string.race_name) + " " + raceName;
        emailContent += "\n\n";

        for (WheelList t : wheelLists) {
            emailContent += "\n\n" + t.getBrandName();
            emailContent += "\n" + getResources().getString(R.string.front_tires) + ": " + t.getTotFrontWheel();
            emailContent += "\n" + getResources().getString(R.string.rear_tires) + ": " + t.getTotRearWheel();
        }
        emailContent += "\n\n" + getResources().getString(R.string.total_no_of_bikes) + ": " +
                mBikeCounter;
        return emailContent;
    }

    private String getEmailBodyCSV() {
        String TEXT_SEPARATOR = ",";

        String emailContent = "";

        emailContent += getResources().getString(R.string.race_name) + raceName;
        emailContent += "\n";

        emailContent += getResources().getString(R.string.brand) + TEXT_SEPARATOR;
        emailContent += getResources().getString(R.string.front_tires) + TEXT_SEPARATOR;
        emailContent += getResources().getString(R.string.rear_tires) + TEXT_SEPARATOR;

        for (WheelList t : wheelLists) {
            emailContent += "\n" + t.getBrandName() + TEXT_SEPARATOR;
            emailContent += t.getTotFrontWheel() + TEXT_SEPARATOR;
            emailContent += t.getTotRearWheel() + TEXT_SEPARATOR;
        }
        emailContent += "\n" + getResources().getString(R.string.total_no_of_bikes) + TEXT_SEPARATOR +
                mBikeCounter + TEXT_SEPARATOR;

        return emailContent;
    }

    private void updateHeader() {
        if (mBikeCounter > 0) {
            headerTextView.setText(String.format(getResources().getString(R.string.moto_inserite), String.valueOf(mBikeCounter)));
        } else {
            headerTextView.setText(getResources().getString(R.string.no_bike_inserted));
        }
    }

}

