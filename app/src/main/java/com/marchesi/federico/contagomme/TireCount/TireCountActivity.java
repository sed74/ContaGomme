package com.marchesi.federico.contagomme.TireCount;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.marchesi.federico.contagomme.DBHelper.DatabaseHelper;
import com.marchesi.federico.contagomme.DBModel.WheelList;
import com.marchesi.federico.contagomme.R;
import com.marchesi.federico.contagomme.SettingsActivity;
import com.marchesi.federico.contagomme.Utils.FileClass;
import com.marchesi.federico.contagomme.wheelcount.WheelCountPerRaceActivity;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class TireCountActivity extends AppCompatActivity {

    public static final String INTENT_NAME_RACE_ID = "race_id";
    public static final String INTENT_NAME_RACE_NAME = "race_name";
    public static final String APP_VERSION = "app_version";
    public static final String USE_HTML = "use_html";
    public static final String AUTO_NEXT = "auto_next";
    public static final String BIKE_COUNTER = "bike_counter";
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private static final String TAG = PackageInfo.class.getName();
    private static final String RACE_NAME = "race_name";
    private static final String RACE_DESCR = "race_descr";
    private static final String ARRAY_NAME = "array_name";
    private static final String ARRAY_SIZE = "array_size";
    private static final String ARRAY_FRONT_SELECTED = "array_front_selected";
    private static final String ARRAY_REAR_SELECTED = "array_rear_selected";
    private static String mRaceName;
    private static String mRaceDescr;
    private final String TEXT_SEPARATOR = ",";
    private int mAttachType = 0;
    private boolean mUseHTML = false;
    private boolean mAttachFile = false;
    private boolean mAutoNext = false;
    private String mEmailRecipient = "";

    private ArrayList<WheelList> arrayTireBrands = new ArrayList<>();
    private TireCountAdapter tireCountAdapter;
    private int mBikeCounter = 0;
    private TextView headerTextView;

    private void savePrefs(Context mContext) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor mEdit1 = sp.edit();
        mEdit1.apply();

        PackageInfo packageInfo = null;
        String packageVersion = "";
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            packageVersion = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        mEdit1.putString(APP_VERSION, packageVersion);

        mEdit1.putString(RACE_NAME, mRaceName);
        mEdit1.putString(RACE_DESCR, mRaceDescr);
        mEdit1.putBoolean(USE_HTML, mUseHTML);
        mEdit1.putBoolean(AUTO_NEXT, mAutoNext);
        mEdit1.putInt(BIKE_COUNTER, mBikeCounter);

        mEdit1.apply();
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


        String appVersion = sp.getString(APP_VERSION, "0");

        if (!appVersion.equalsIgnoreCase(packageVersion)) {
            // a new version is running, have to clear the saved data
            SharedPreferences.Editor mEdit1 = sp.edit();
            mEdit1.clear();
            mEdit1.apply();
            loadList();
            Toast.makeText(this, getResources().getString(R.string.new_version_detected),
                    Toast.LENGTH_LONG).show();
            return;
        }

        mRaceName = sp.getString(RACE_NAME, "");
        mRaceDescr = sp.getString(RACE_DESCR, "");
        mAutoNext = sp.getBoolean(AUTO_NEXT, false);
        mUseHTML = sp.getBoolean(USE_HTML, false);
        mBikeCounter = sp.getInt(BIKE_COUNTER, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        final Button nextButton = (Button) findViewById(R.id.button_next);
        headerTextView = (TextView) findViewById(R.id.moto_inserite);

        updateHeader();

        // TODO: 06/02/2017 Create Adapter Listener to process current bike

        ListView obj = (ListView) findViewById(R.id.list);
        obj.setAdapter(tireCountAdapter);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListView list = (ListView) findViewById(R.id.list);

                for (WheelList t :
                        arrayTireBrands) {
                    t.resetSelection();
                }

                tireCountAdapter.notifyDataSetChanged();
                mBikeCounter++;
                updateHeader();
            }
        });
        Button resetButton = (Button) findViewById(R.id.button_reset);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetList();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_single_race, menu);

        // TODO: 06/02/2017 Check menu

        Button nextButton = (Button) findViewById(R.id.button_next);
        nextButton.setVisibility(mAutoNext ? View.GONE : View.VISIBLE);
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
                // TODO: 06/02/2017 Handle delete button
                resetList();
                break;
            case R.id.send_email:
                // TODO: 06/02/2017 Implement Email button
                break;
            case R.id.auto_save_bike:
                mAutoNext = !item.isChecked();
                item.setChecked(mAutoNext);
                Button nextButton = (Button) findViewById(R.id.button_next);
                nextButton.setVisibility(mAutoNext ? View.GONE : View.VISIBLE);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openRaceActivity(Integer raceID, String raceName) {
        //  check if wheel_list exists for the race
        DatabaseHelper dbHelper = new DatabaseHelper(getBaseContext());
        int brandCount = dbHelper.getRowCount(DatabaseHelper.TABLE_BRANDS);
        int rowCount = dbHelper.getRowCount(DatabaseHelper.TABLE_WHEEL_LIST,
                DatabaseHelper.COLUMN_WHEEL_RACE_ID, raceID);

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


    @Override
    protected void onStop() {
        super.onStop();
        // TODO: 06/02/2017 Save ArrayList to DB
        savePrefs(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // TODO: 06/02/2017 load ArrayList from DB
        loadSettings();

    }

    private void loadSettings() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        // TODO: 06/02/2017 Load only necessary prefs (ie mAutoNext)
        mAutoNext = sharedPref.getBoolean(SettingsActivity.KEY_PREF_AUTO_CONTINUE, false);
//        mEmailRecipient = sharedPref.getString(SettingsActivity.KEY_PREF_EMAIL_RECIPIENT, "");
//        mAttachFile = sharedPref.getBoolean(SettingsActivity.KEY_PREF_ATTACH_FILE, false);
//        mAttachType = Integer.parseInt(sharedPref.getString(
//                SettingsActivity.KEY_PREF_ATTACHMENT_TYPE, "1"));

//        Toast.makeText(this, mAttachType, Toast.LENGTH_SHORT).show();
    }

    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }
        loadPrefs(this);
        tireCountAdapter = new TireCountAdapter(this, arrayTireBrands);
    }

    void loadList() {
        loadList(arrayTireBrands);
    }

    void loadList(ArrayList<WheelList> arrayList) {
        // TODO: 06/02/2017 populate array using Database instead of array resource
        DatabaseHelper dbHelper = new DatabaseHelper(getBaseContext());
        arrayList = dbHelper.getAllWheelListByRaceId(1);
    }

    private void resetList() {

        final ArrayList<WheelList> arrayTemp = new ArrayList<>();
        final int bikeCounterTemp = mBikeCounter;

        loadList(arrayTemp);
        Collections.copy(arrayTemp, arrayTireBrands);

        View view = findViewById(R.id.activity_main);
        arrayTireBrands.clear();
        loadList();
        tireCountAdapter.notifyDataSetChanged();
        mBikeCounter = 0;
        updateHeader();

        Snackbar snackbar = Snackbar
                .make(view, getResources().getString(R.string.race_reset), Snackbar.LENGTH_LONG)
                .setDuration(5000)
                .setAction(getResources().getString(R.string.reset_undo), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Collections.copy(arrayTireBrands, arrayTemp);

                        mBikeCounter = bikeCounterTemp;
                        tireCountAdapter.notifyDataSetChanged();
                        updateHeader();
                        Snackbar.make(view, getResources().getString(R.string.done_undo), Snackbar.LENGTH_SHORT).show();

                    }
                });

        snackbar.show();
    }

    private void updateHeader() {
        if (mBikeCounter > 0) {
            headerTextView.setText(String.format(getResources().getString(R.string.moto_inserite), String.valueOf(mBikeCounter)));
        } else {
            headerTextView.setText("");
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        View view = findViewById(R.id.activity_main);
        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE) {
            // BEGIN_INCLUDE(permission_result)
            // Received permission result for camera permission.

            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission has been granted, preview can be displayed
//                Log.i(TAG, "CAMERA permission has now been granted. Showing preview.");
                Snackbar.make(view, R.string.permision_available_write_storage,
                        Snackbar.LENGTH_SHORT).show();
                // TODO: 06/02/2017 implement the correct call to SendEmail method
//                sendEmail();
            } else {
//                Log.i(TAG, "CAMERA permission was NOT granted.");
                Toast.makeText(this, R.string.permision_not_available_write_storage,
                        Toast.LENGTH_LONG).show();
                FileClass.changeSharedPreference(this, SettingsActivity.KEY_PREF_ATTACH_FILE, false);
            }

        }
    }

    public void sendEmail(Context context) {

        String currentDateTimeString = DateFormat.getDateInstance().format(new Date());
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("text/html");
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this

        // TODO: 06/02/2017 Get Email Body using ArrayList (if possible) or db view
        String emailContent = "";//= getEmailBody();

        Resources res = context.getResources();
        String subject = String.format(res.getString(R.string.subject), mRaceName, String.format(currentDateTimeString));
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
//                    emailAttach = getEmailBodyCSV();
                    break;
                case 2: // HTML
//                    emailAttach = getEmailBodyHTML();
                    break;
            }

            String[] mTestArray = getResources().getStringArray(R.array.pref_file_type);
            String fileName = FileClass.createFile(subject, emailAttach, mTestArray[mAttachType]);
            if (fileName.isEmpty()) return;
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + fileName));
        }

//        Toast.makeText(this, subject, Toast.LENGTH_SHORT).show();
        intent.putExtra(Intent.EXTRA_TEXT, emailContent);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


}