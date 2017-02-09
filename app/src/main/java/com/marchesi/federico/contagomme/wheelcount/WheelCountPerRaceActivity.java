package com.marchesi.federico.contagomme.wheelcount;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
import android.widget.Toast;

import com.marchesi.federico.contagomme.DBHelper.DatabaseHelper;
import com.marchesi.federico.contagomme.DBModel.WheelList;
import com.marchesi.federico.contagomme.MainActivity;
import com.marchesi.federico.contagomme.R;
import com.marchesi.federico.contagomme.SettingsActivity;
import com.marchesi.federico.contagomme.Utils.FileClass;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WheelCountPerRaceActivity extends AppCompatActivity {

    private final String TEXT_SEPARATOR = ",";
    private String raceName;
    private int raceID;
    private DatabaseHelper dbHelper;
    private WheelCountPerRaceCursorAdapter wheelCountAdapter;
    private ListView listView;
    private ArrayList<WheelList> wheelLists;
    private boolean mAutoNext;
    private boolean mUseHTML;
    private int mBikeCounter;
    private String mEmailRecipient;
    private boolean mAttachFile;
    private int mAttachType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.list);
        raceID = getIntent().getIntExtra(MainActivity.INTENT_NAME_RACE_ID, 0);
        raceName = getIntent().getStringExtra(MainActivity.INTENT_NAME_RACE_NAME);

        loadPrefs(this);

        final Button nextButton = (Button) findViewById(R.id.button_next);
        nextButton.setVisibility(mAutoNext ? View.GONE : View.VISIBLE);

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
                                    }
                                }, 100);
                            }
                        }
                        nextButton.setEnabled(frontSelected && rearSelected);
//                        wheelCountAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

        setupActionBar();
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
                final ArrayList<WheelList> tempList = copyValues(wheelLists);
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

                                dbHelper.populateWheelListTableFromRaceId(tempList);

                                wheelLists = wheelCountAdapter.populateArray(getCursor());
                                swapCursor();
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

        mAutoNext = sp.getBoolean(getResources().getString(R.string.pref_auto_continue), true);
        //mUseHTML = sp.getBoolean(MainActivity.USE_HTML, false);
        mBikeCounter = sp.getInt(getResources().getString(R.string.pref_bike_counter), 0);
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

        String currentDateTimeString = DateFormat.getDateInstance().format(new Date());
        String subject = String.format(getResources().getString(R.string.subject_no_date),
                raceName);
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

        emailContent += "Gara di <b>" + raceName + "</b><br><br>";

//        if (!mRaceDescr.isEmpty()) {
//            emailContent += "\n\n" + mRaceDescr + "<br>";
//        }
        for (WheelList t : wheelLists) {
            emailContent += "<p>";
            emailContent += "<b>" + t.getBrandName() + "</b><br>";
            emailContent += "Anteriori: " + t.getTotFrontWheel() + "<br>";
            emailContent += "Posteriori: " + t.getTotRearWheel() + "<br>";
            emailContent += "</p>";
        }
        //emailContent += "</body></meta></html>";
//        Toast.makeText(this, emailContent, Toast.LENGTH_LONG).show();
        return emailContent;
    }

    private String getEmailBody() {

        String emailContent = "";

        emailContent += "Gara di " + raceName;
        emailContent += "\n\n";

//        if (!mRaceDescr.isEmpty()) {
//            emailContent += "\n\n" + mRaceDescr;
//        }
        for (WheelList t : wheelLists) {
            emailContent += "\n\n" + t.getBrandName();
            emailContent += "\n" + "Anteriori: " + t.getTotFrontWheel();
            emailContent += "\n" + "Posteriori: " + t.getTotRearWheel();
        }

        return emailContent;
    }

    private String getEmailBodyCSV() {

        String emailContent = "";

        emailContent += getResources().getString(R.string.race_name) + raceName;
        emailContent += "\n";
        emailContent += getResources().getString(R.string.brand) + TEXT_SEPARATOR;
        emailContent += getResources().getString(R.string.front_tire) + TEXT_SEPARATOR;
        emailContent += getResources().getString(R.string.rear_tire) + TEXT_SEPARATOR;

        for (WheelList t : wheelLists) {
            emailContent += "\n" + t.getBrandName() + TEXT_SEPARATOR;
            emailContent += t.getTotFrontWheel() + TEXT_SEPARATOR;
            emailContent += t.getTotRearWheel() + TEXT_SEPARATOR;
        }

        return emailContent;
    }

}

