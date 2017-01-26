package com.marchesi.federico.contagomme;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
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

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                AlertDialog myQuittingDialogBox = new AlertDialog.Builder(BrandListActivity.this)
                        //set message, title, and icon
                        .setTitle(getResources().getString(R.string.dialog_delete_title))
                        .setMessage(getResources().getString(R.string.dialog_delete_message))
                        .setIcon(R.drawable.delete)

                        .setPositiveButton(getResources().getString(R.string.dialog_delete_button), new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                Cursor cur = brandAdapter.getCursor();
                                dbHelper.deleteBrand(cur.getInt(cur.getColumnIndex(DatabaseHelper._ID)));
                                Cursor d = dbHelper.getBrandsCursor();
                                brandAdapter.swapCursor(d);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.dialog_cancel_button), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();

                return false;
            }
        });
        Button addButton = (Button) findViewById(R.id.button_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputDialog inputDialog = new InputDialog(getBaseContext(), R.string.add_brand_dialog_title, R.string.add_brand_dialog_hint, "", "");
                inputDialog.setInputListener(new InputDialog.InputListener() {
                    @Override
                    public InputDialog.ValidationResult isInputValid(String newBrand) {
                        if (newBrand.isEmpty()) {
//                    return new InputDialog.ValidationResult(false, R.string.error_empty_name);
                        }
                        return new InputDialog.ValidationResult(true, 0);
                    }

                    @Override
                    public void onConfirm(String brandName, String raceDescr) {
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
        });
    }

    private AlertDialog AskOption() {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle(getResources().getString(R.string.dialog_delete_title))
                .setMessage(getResources().getString(R.string.dialog_delete_message))
                //.setIcon(and)

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your deleting code
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return myQuittingDialogBox;

    }
}

