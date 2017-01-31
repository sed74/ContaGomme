package com.marchesi.federico.contagomme.WheelCountPerRace;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.marchesi.federico.contagomme.DBHelper.DatabaseHelper;
import com.marchesi.federico.contagomme.DBModel.Race;
import com.marchesi.federico.contagomme.Dialog.InputDialogRace;
import com.marchesi.federico.contagomme.R;

/**
 * Created by federico.marchesi on 26/01/2017.
 */

public class WheelCountPerRaceCursorAdapter extends CursorAdapter {
    private static final String TAG = WheelCountPerRaceCursorAdapter.class.getName();
    private LayoutInflater cursorInflater;


    public WheelCountPerRaceCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        cursorInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //return LayoutInflater.from(context).inflate(R.layout.activity_brand_list, parent, false);
        return cursorInflater.inflate(R.layout.tire_button, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {

        // Find fields to populate in inflated template
        TextView raceTV = (TextView) view.findViewById(R.id.race_name);
        //TextView tvPriority = (TextView) view.findViewById(R.id.tvPriority);
        // Extract properties from cursor
        final String race = cursor.getString(
                cursor.getColumnIndex(DatabaseHelper.COLUMN_RACE_NAME));
        int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper._ID));

        TextView raceDate = (TextView) view.findViewById(R.id.race_date);
        raceDate.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_RACE_DATE)));

        raceTV.setTag(id);
        // Populate fields with extracted properties
        raceTV.setText(race);

        raceTV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                editRace(context, v);
                return false;
            }
        });
        //tvPriority.setText(String.valueOf(priority));
        ImageView deleteImage = (ImageView) view.findViewById(R.id.delete_button);

        deleteImage.setTag(id);

        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog myQuittingDialogBox = new AlertDialog.Builder(context)
                        //set message, title, and icon
                        .setTitle(context.getResources().getString(R.string.dialog_delete_title))
                        .setMessage(String.format(context.getResources().getString(R.string.dialog_delete_message), race))
                        .setIcon(R.drawable.delete)

                        .setPositiveButton(context.getResources().getString(R.string.dialog_delete_button), new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                Cursor cur = cursor;
                                DatabaseHelper dbHelper = new DatabaseHelper(context);

                                ImageView delete = (ImageView) view.findViewById(R.id.delete_button);
                                dbHelper.deleteRace((int) delete.getTag());
                                Cursor d = dbHelper.getCursor(DatabaseHelper.TABLE_RACES, DatabaseHelper.COLUMN_RACE_DATE);
                                swapCursor(d);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(context.getResources().getString(R.string.dialog_cancel_button), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();

            }
        });

    }

    private void editRace(final Context context, View v) {
        final DatabaseHelper dbHelper = new DatabaseHelper(context);

        int id = (int) v.getTag();
        final Race[] race = {dbHelper.getRace(id)};


        InputDialogRace inputDialog = new InputDialogRace(context, R.string.add_race_dialog_title,
                R.string.add_race_dialog_hint);
        inputDialog.setRaceName(race[0].getName());
        inputDialog.setRaceDescr(race[0].getDesc());
        inputDialog.setRaceDate(race[0].getDate());

        inputDialog.setInputListener(new InputDialogRace.InputListener() {

            @Override
            public InputDialogRace.ValidationResult isInputValid(String input) {
                return null;
            }

            @Override
            public void onConfirm(String raceName, String raceDescr, String raceDate) {
                if (!raceName.isEmpty())
                    race[0].setName(raceName);

                if (!raceDescr.isEmpty())
                    race[0].setDesc(raceDescr);

                if (!raceDate.isEmpty())
                    race[0].setDate(raceDate);

                dbHelper.updateRace(race[0]);
                Cursor c = dbHelper.getCursor(DatabaseHelper.TABLE_RACES, DatabaseHelper.COLUMN_RACE_DATE);
                Cursor old = swapCursor(c);
                old.close();
                //Toast.makeText(MainActivity.this, getResources().getString(R.string.data_saved), Toast.LENGTH_SHORT).show();
            }


        });
        inputDialog.show();


//
//        InputDialogBrand inputDialog = new InputDialogBrand(context,
//                R.string.edit_brand_dialog_title, R.string.add_race_dialog_hint);
//
//        inputDialog.setInitialInput(race[0].getName());
////        inputDialog.setInitialOrder(oldOrder);
//        inputDialog.setInputListener(new InputDialogBrand.InputListener() {
//            @Override
//            public InputDialogBrand.ValidationResult isInputValid(String newCoffeeType) {
//                if (newCoffeeType.isEmpty()) {
////                    return new InputDialog.ValidationResult(false, R.string.error_empty_name);
//                }
//                return new InputDialogBrand.ValidationResult(true, 0);
//            }
//
//            @Override
//            public void onConfirm(String brandName, int order) {
//
//                race[0].setName(brandName);
////                race[0].setDate(order);
//                dbHelper.updateRace(race[0]);
//                Cursor c = dbHelper.getBrandsCursor();
//                Cursor old = swapCursor(c);
//                old.close();
//
//                //Toast.makeText(MainActivity.this, getResources().getString(R.string.data_saved), Toast.LENGTH_SHORT).show();
//            }
//        });
//        inputDialog.show();
    }

//    @Override
//    public Cursor swapCursor(Cursor newCursor) {
//        Cursor oldCursor = super.swapCursor(newCursor);
//        if (oldCursor != null) {
//            oldCursor.close();
//        }
//        return oldCursor;
//    }
}