package com.marchesi.federico.contagomme;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.marchesi.federico.contagomme.DBHelper.DatabaseHelper;
import com.marchesi.federico.contagomme.Dialog.InputDialog;
import com.marchesi.federico.contagomme.WheelCountPerRace.WheelCountPerRaceActivity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public static final String INTENT_NAME_RACE_ID = "race_id";
    public static final String INTENT_NAME_RACE_NAME = "race_name";
    public static final String APP_VERSION = "app_version";
    public static final String USE_HTML = "use_html";
    public static final String AUTO_NEXT = "auto_next";
    public static final String BIKE_COUNTER = "bike_counter";
    private static final String TAG = PackageInfo.class.getName();
    private static final String RACE_NAME = "race_name";
    private static final String RACE_DESCR = "race_descr";
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;
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
    private ArrayList<TireBrands> arrayTireBrands = new ArrayList<>();
    private TireAdapter tireAdapter;
    private int mBikeCounter = 0;
    private TextView headerTextView;

    private void savePrefs(Context mContext) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor mEdit1 = sp.edit();
        clearArrayPref(sp);
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

        saveArrayPrefs(mEdit1);

        mEdit1.apply();
    }

    private void saveArrayPrefs(SharedPreferences.Editor mEdit1) {

        int arraySize = arrayTireBrands.size();
        mEdit1.putInt(ARRAY_SIZE, arraySize);

        for (int i = 0; i < arraySize; i++) {

            mEdit1.putString(ARRAY_NAME + String.valueOf(i), arrayTireBrands.get(i).getName());
            mEdit1.putInt(ARRAY_FRONT_SELECTED + String.valueOf(i),
                    arrayTireBrands.get(i).getTotFrontSelected());
            mEdit1.putInt(ARRAY_REAR_SELECTED + String.valueOf(i),
                    arrayTireBrands.get(i).getTotRearSelected());
        }
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
        loadArrayPref(sp);
    }

    private void loadArrayPref(SharedPreferences sp) {
        arrayTireBrands.clear();
        int arraySize = sp.getInt(ARRAY_SIZE, 0);
        if (arraySize == 0) {
            loadList();
            return;
        }
        for (int i = 0; i < arraySize; i++) {
            arrayTireBrands.add(new TireBrands(
                    sp.getString(ARRAY_NAME + String.valueOf(i), ""),
                    sp.getInt(ARRAY_FRONT_SELECTED + String.valueOf(i), 0),
                    sp.getInt(ARRAY_REAR_SELECTED + String.valueOf(i), 0)
            ));
        }
    }

    private void clearArrayPref(SharedPreferences sp) {

        SharedPreferences.Editor editor = sp.edit();
        int arraySize = sp.getInt(ARRAY_SIZE, 0);

        if (arraySize == 0) return;

        for (int i = 0; i < arraySize; i++) {
            editor.remove(ARRAY_NAME + String.valueOf(i));
            editor.remove(ARRAY_FRONT_SELECTED + String.valueOf(i));
            editor.remove(ARRAY_REAR_SELECTED + String.valueOf(i));
        }
        editor.remove(ARRAY_SIZE);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        final Button nextButton = (Button) findViewById(R.id.button_next);
        headerTextView = (TextView) findViewById(R.id.moto_inserite);

        updateHeader();

        tireAdapter.setOnValueChangeListener(new TireAdapter.OnValueChangeListener() {
            @Override
            public void onValueChange(boolean frontSelected, boolean rearSelected) {
                if (mAutoNext && (frontSelected && rearSelected)) {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            nextButton.callOnClick();
                        }
                    }, 500);
                } else {
                    nextButton.setEnabled(frontSelected && rearSelected);
                }
            }
        });

        ListView obj = (ListView) findViewById(R.id.list);
        obj.setAdapter(tireAdapter);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListView list = (ListView) findViewById(R.id.list);

                for (TireBrands t :
                        arrayTireBrands) {
                    t.resetSelection();
                }

                tireAdapter.notifyDataSetChanged();
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

/*
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        Toast
                .makeText(this, "DensityDPI = " + String.valueOf(metrics.densityDpi) +
                                "\n Height = " + String.valueOf(metrics.heightPixels),
                        Toast.LENGTH_LONG).show();
*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem item = menu.getItem(2);
        item.setChecked(mAutoNext);

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
            case R.id.send_email:
                sendEmail();
                break;
            case R.id.give_race_name:
                addRaceName();
                break;
            case R.id.reset:
                resetList();
                break;
            case R.id.brands:
                Intent intent = new Intent(this, BrandListActivity.class);
                startActivity(intent);
                break;
            case R.id.races:
                Intent racesIntent = new Intent(this, RaceListActivity.class);
                startActivity(racesIntent);
                break;
            case R.id.auto_save_bike:
                mAutoNext = !item.isChecked();
                item.setChecked(mAutoNext);
                Button nextButton = (Button) findViewById(R.id.button_next);
                nextButton.setVisibility(mAutoNext ? View.GONE : View.VISIBLE);
                break;
            case R.id.settings:
//                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
            case R.id.race_menu:

                View menuItemView = findViewById(R.id.race_menu); // SAME ID AS MENU ID
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(MainActivity.this, menuItemView);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.race_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        int id = item.getItemId();

                        //noinspection SimplifiableIfStatement
                        switch (id) {
                            case R.id.send_email:
                                sendEmail();
                                break;
                            case R.id.reset:
                                resetList();
                                break;
                            case R.id.brands:
                                Intent intent = new Intent(MainActivity.this, BrandListActivity.class);
                                startActivity(intent);
                                break;
                            case R.id.races:
                                Intent racesIntent = new Intent(MainActivity.this, RaceListActivity.class);
                                startActivity(racesIntent);
                                break;
                            case R.id.open_race:
                                AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
                                b.setTitle(getResources().getString(R.string.select_race_dialog));
                                DatabaseHelper dbHelper = new DatabaseHelper(getBaseContext());
                                final String[] raceNames = (String[]) dbHelper.getRacesNameArray();
                                final Integer[] raceIDs = (Integer[]) dbHelper.getRacesIdArray();
                                dbHelper.close();
                                b.setItems(raceNames, new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        dialog.dismiss();

                                        openRaceActivity(raceIDs[which], raceNames[which]);
//                                        Toast.makeText(MainActivity.this, "Gara selezionata:\n" +
//                                                raceNames[which] + "\nID: " + raceIDs[which], Toast.LENGTH_SHORT).show();
                                    }

                                });
                                b.show();
//                                Intent openracesIntent = new Intent(MainActivity.this, RaceListActivity.class);
//                                startActivity(openracesIntent);
                                break;
                        }
                        return true;
                    }
                });
                popup.show();//showing popup menu
                return true;
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
        savePrefs(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSettings();

    }

    private void loadSettings() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        mAutoNext = sharedPref.getBoolean(SettingsActivity.KEY_PREF_AUTO_CONTINUE, false);
        mEmailRecipient = sharedPref.getString(SettingsActivity.KEY_PREF_EMAIL_RECIPIENT, "");
        mAttachFile = sharedPref.getBoolean(SettingsActivity.KEY_PREF_ATTACH_FILE, false);
        mAttachType = Integer.parseInt(sharedPref.getString(
                SettingsActivity.KEY_PREF_ATTACHMENT_TYPE, "1"));

//        Toast.makeText(this, mAttachType, Toast.LENGTH_SHORT).show();
    }

    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }
        loadPrefs(this);
        tireAdapter = new TireAdapter(this, arrayTireBrands);
    }

    private TextView setTextViewStandardProperties(TextView textView) {
        textView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        0);
        params.setMargins(5, 15, 5, 5);
        params.weight = 1;
        params.gravity = Gravity.CENTER;
        textView.setPadding(16, 16, 16, 16);
        textView.setLayoutParams(params);
        textView.setBackgroundColor(Color.LTGRAY);
        return textView;
    }

    void loadList() {
        loadList(arrayTireBrands);
    }

    void loadList(ArrayList<TireBrands> arrayList) {
        String[] tyreBrands = getResources().getStringArray(R.array.tire_brands);

        for (String tire : tyreBrands) {
            arrayList.add(new TireBrands(tire));
        }

    }

    private void resetList() {

        final ArrayList<TireBrands> arrayTemp = new ArrayList<>();
        final int bikeCounterTemp = mBikeCounter;

        loadList(arrayTemp);
        Collections.copy(arrayTemp, arrayTireBrands);

        View view = findViewById(R.id.activity_main);
        arrayTireBrands.clear();
        loadList();
        tireAdapter.notifyDataSetChanged();
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
                        tireAdapter.notifyDataSetChanged();
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

    public void sendEmail() {

        String currentDateTimeString = DateFormat.getDateInstance().format(new Date());
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("text/html");
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this

        String emailContent = getEmailBody();

        Resources res = getResources();
        String subject = String.format(res.getString(R.string.subject), mRaceName, String.format(currentDateTimeString));
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);

        if (!mEmailRecipient.isEmpty()) {

            String[] to = mEmailRecipient.split(",");
            intent.putExtra(Intent.EXTRA_EMAIL, to);
        }

        if (mAttachFile) {
            String emailAttach = "";
            checkForArchiveAccess();

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
            String fileName = createFile(subject, emailAttach, mTestArray[mAttachType]);
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

        emailContent += "Gara di <b>" + mRaceName + "</b><br><br>";

        if (!mRaceDescr.isEmpty()) {
            emailContent += "\n\n" + mRaceDescr + "<br>";
        }
        for (TireBrands t :
                arrayTireBrands) {
            emailContent += "<p>";
            emailContent += "<b>" + t.getName() + "</b><br>";
            emailContent += "Anteriori: " + t.getTotFrontSelected() + "<br>";
            emailContent += "Posteriori: " + t.getTotRearSelected() + "<br>";
            emailContent += "</p>";
        }
        //emailContent += "</body></meta></html>";
//        Toast.makeText(this, emailContent, Toast.LENGTH_LONG).show();
        return emailContent;
    }

    private String getEmailBody() {

        String emailContent = "";

        emailContent += "Gara di " + mRaceName;
        emailContent += "\n\n";

        if (!mRaceDescr.isEmpty()) {
            emailContent += "\n\n" + mRaceDescr;
        }
        for (TireBrands t :
                arrayTireBrands) {
            emailContent += "\n\n" + t.getName();
            emailContent += "\n" + "Anteriori: " + t.getTotFrontSelected();
            emailContent += "\n" + "Posteriori: " + t.getTotRearSelected();
        }

        return emailContent;
    }

    private String getEmailBodyCSV() {

        String emailContent = "";

        emailContent += getResources().getString(R.string.race_name) + mRaceName;
        emailContent += "\n";
        emailContent += getResources().getString(R.string.brand) + TEXT_SEPARATOR;
        emailContent += getResources().getString(R.string.front_tire) + TEXT_SEPARATOR;
        emailContent += getResources().getString(R.string.rear_tire) + TEXT_SEPARATOR;

        for (TireBrands t : arrayTireBrands) {
            emailContent += "\n" + t.getName() + TEXT_SEPARATOR;
            emailContent += t.getTotFrontSelected() + TEXT_SEPARATOR;
            emailContent += t.getTotRearSelected() + TEXT_SEPARATOR;
        }

        return emailContent;
    }

    private void addRaceName() {

        InputDialog inputDialog = new InputDialog(this, R.string.race_name_title, R.string.add_race_name, mRaceName, mRaceDescr);
        inputDialog.setInputListener(new InputDialog.InputListener() {
            @Override
            public InputDialog.ValidationResult isInputValid(String newCoffeeType) {
                if (newCoffeeType.isEmpty()) {
//                    return new InputDialog.ValidationResult(false, R.string.error_empty_name);
                }
                return new InputDialog.ValidationResult(true, 0);
            }

            @Override
            public void onConfirm(String raceName, String raceDescr) {
//                DBHelper db = new DBHelper(MainActivity.this);
//                db.insertCoffeeType(newCoffeeType, newCoffeeDescr);
                mRaceName = raceName;
                mRaceDescr = raceDescr;
                Toast.makeText(MainActivity.this, getResources().getString(R.string.data_saved), Toast.LENGTH_SHORT).show();
            }
        });
        inputDialog.show();
    }

    private String createFile(String fileName, String fileContent, String extension) {

        try {

            if (!fileName.endsWith(".")) fileName += ".";
            fileName += extension;
            // this will create a new name everytime and unique
            File root = new File(Environment.getExternalStorageDirectory(), "ContaGomme");
//            File root = new File(getExternalFilesDir(null), fileName);
            // if external memory exists and folder with name Notes
            if (!root.exists()) {
                root.mkdirs(); // this will create folder.
            }

            File filepath = new File(root, fileName);  // file path to save
            FileWriter writer = new FileWriter(filepath);
            writer.append(fileContent);
            writer.flush();
            writer.close();

            return filepath.getAbsolutePath();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();

        }
        return "";
    }

    private void checkForArchiveAccess() {
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

//                Toast.makeText(this, getResources().getString(R.string.explain_why_write_storage),
//                        Toast.LENGTH_LONG).show();
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_EXTERNAL_STORAGE);
            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_EXTERNAL_STORAGE);

                // REQUEST_WRITE_EXTERNAL_STORAGE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
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
                sendEmail();
            } else {
//                Log.i(TAG, "CAMERA permission was NOT granted.");
                Toast.makeText(this, R.string.permision_not_available_write_storage,
                        Toast.LENGTH_LONG).show();
                changeSharedPreference(SettingsActivity.KEY_PREF_ATTACH_FILE, false);
            }
            // END_INCLUDE(permission_result)

        }
    }

    void changeSharedPreference(String prefName, Object value) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sharedPref.edit();
        if (value instanceof Boolean) {
            edit.putBoolean(prefName, (Boolean) value);
        } else if (value instanceof Integer) {
            edit.putInt(prefName, (Integer) value);
        } else if (value instanceof String) {
            edit.putString(prefName, value.toString());
        }
        edit.apply();
    }

}