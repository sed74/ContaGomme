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
import com.marchesi.federico.contagomme.DBModel.Brand;

public class BrandListActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private BrandCursorAdapter brandAdapter;
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
                Cursor c = dbHelper.getBrandsCursor();
                brandAdapter = new BrandCursorAdapter(BrandListActivity.this, c);
                listView.setAdapter(brandAdapter);
            }

        });

        Button addButton = (Button) findViewById(R.id.button_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBrand();

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

    private void addBrand() {

        InputDialog inputDialog = new InputDialog(this, R.string.add_brand_dialog_title, R.string.add_brand_dialog_hint, "", "");
        inputDialog.setInputListener(new InputDialog.InputListener() {
            @Override
            public InputDialog.ValidationResult isInputValid(String newCoffeeType) {
                if (newCoffeeType.isEmpty()) {
//                    return new InputDialog.ValidationResult(false, R.string.error_empty_name);
                }
                return new InputDialog.ValidationResult(true, 0);
            }

            @Override
            public void onConfirm(String brandName, String brandDescr) {
                Brand brand = new Brand(brandName);
                Cursor cur = brandAdapter.getCursor();
                dbHelper.createBrand(brand);
                Cursor d = dbHelper.getBrandsCursor();
                brandAdapter.swapCursor(d);
                //Toast.makeText(MainActivity.this, getResources().getString(R.string.data_saved), Toast.LENGTH_SHORT).show();
            }
        });
        inputDialog.show();
    }


}

