package com.marchesi.federico.contagomme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final String RACE_NAME = "race_name";
    private static final String RACE_DESCR = "race_descr";
    private static final String USE_HTML = "use_html";
    private static final String AUTO_NEXT = "auto_next";

    private static final String ARRAY_NAME = "array_name";
    private static final String ARRAY_SIZE = "array_size";
    private static final String ARRAY_FRONT_SELECTED = "array_front_selected";
    private static final String ARRAY_REAR_SELECTED = "array_rear_selected";

    private static String mRaceName;
    private static String mRaceDescr;
    private static boolean mUseHTML = false;
    private static boolean mAutoNext = false;
    private ArrayList<TireBrands> arrayTireBrands = new ArrayList<>();
    private TireAdapter tireAdapter;
    private int bikeCounter = 0;
    private TextView headerTextView;

    private void savePrefs(Context mContext) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor mEdit1 = sp.edit();
        mEdit1.clear();
        mEdit1.apply();

        mEdit1.putString(RACE_NAME, mRaceName);
        mEdit1.putString(RACE_DESCR, mRaceDescr);
        mEdit1.putBoolean(USE_HTML, mUseHTML);
        mEdit1.putBoolean(AUTO_NEXT, mAutoNext);
        saveArrayPrefs(mEdit1);

        mEdit1.apply();
    }

    private void saveArrayPrefs(SharedPreferences.Editor mEdit1) {

        int arraySize = arrayTireBrands.size();
        int i = 0;
        mEdit1.putInt(ARRAY_SIZE, arraySize);
        for (TireBrands tire : arrayTireBrands) {
            mEdit1
                    .putString(ARRAY_NAME + String.valueOf(i), tire.getName())
                    .putInt(ARRAY_FRONT_SELECTED + String.valueOf(i), tire.getTotFrontSelected())
                    .putInt(ARRAY_REAR_SELECTED + String.valueOf(i), tire.getTotRearSelected());
            i++;
        }
        mEdit1.apply();
    }

    public void loadPrefs(Context mContext) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);

        mRaceName = sp.getString(RACE_NAME, "");
        mRaceDescr = sp.getString(RACE_DESCR, "");
        mAutoNext = sp.getBoolean(AUTO_NEXT, false);
        mUseHTML = sp.getBoolean(USE_HTML, false);
        loadArrayPref(sp);
    }

    private void loadArrayPref(SharedPreferences sp) {
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
        tireAdapter.notifyDataSetChanged();
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
                bikeCounter++;
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
//            case R.id.use_html:
//                mUseHTML = !item.isChecked();
//                item.setChecked(mUseHTML);
//                break;
            case R.id.auto_save_bike:
                mAutoNext = !item.isChecked();
                item.setChecked(mAutoNext);
                Button nextButton = (Button) findViewById(R.id.button_next);
                nextButton.setVisibility(mAutoNext ? View.GONE : View.VISIBLE);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        savePrefs(this);
    }

    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }
        loadPrefs(this);
        //loadList();
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
/*
        String[] tyreBrands = getResources().getStringArray(R.array.tire_brands);

        for (String tire : tyreBrands) {
            arrayTireBrands.add(new TireBrands(tire));
        }
*/

    }

    void loadList(ArrayList<TireBrands> arrayList) {
        String[] tyreBrands = getResources().getStringArray(R.array.tire_brands);

        for (String tire : tyreBrands) {
            arrayList.add(new TireBrands(tire));
        }

    }

    private void resetList() {

        final ArrayList<TireBrands> arrayTemp = new ArrayList<>();
        final int bikeCounterTemp = bikeCounter;

        loadList(arrayTemp);
        Collections.copy(arrayTemp, arrayTireBrands);

        View view = findViewById(R.id.activity_main);
        arrayTireBrands.clear();
        loadList();
        tireAdapter.notifyDataSetChanged();
        bikeCounter = 0;
        updateHeader();

        Snackbar snackbar = Snackbar
                .make(view, getResources().getString(R.string.race_reset), Snackbar.LENGTH_LONG)
                .setAction(getResources().getString(R.string.reset_undo), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Collections.copy(arrayTireBrands, arrayTemp);
                        bikeCounter = bikeCounterTemp;
                        tireAdapter.notifyDataSetChanged();
                        updateHeader();
                        Snackbar snackbar1 = Snackbar.make(view, getResources().getString(R.string.done_undo), Snackbar.LENGTH_SHORT);
                        snackbar1.show();
                    }
                });

        snackbar.show();
    }

    private void updateHeader() {
        if (bikeCounter > 0) {
            headerTextView.setText(String.format(getResources().getString(R.string.moto_inserite), String.valueOf(bikeCounter)));
        } else {
            headerTextView.setText("");
        }
    }

    public void sendEmail() {

        //String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        String currentDateTimeString = DateFormat.getDateInstance().format(new Date());
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("text/html");
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this

        Resources res = getResources();
        String subject = String.format(res.getString(R.string.subject), mRaceName, String.format(currentDateTimeString));
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);

        Toast.makeText(this, subject, Toast.LENGTH_SHORT).show();
        if (mUseHTML) {
            intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(getEmailBodyHTML()));
        } else {
            intent.putExtra(Intent.EXTRA_TEXT, getEmailBody());
        }

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

}
