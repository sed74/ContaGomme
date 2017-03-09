package com.sed.willy.contagomme;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sed.willy.contagomme.DBContract.ContaGommeContract.RaceEntry;
import com.sed.willy.contagomme.DBHelper.DatabaseHelper;
import com.sed.willy.contagomme.DBModel.Race;
import com.sed.willy.contagomme.Dialog.InputDialogRace;
import com.sed.willy.contagomme.Utils.DateConverter;
import com.sed.willy.contagomme.Utils.Global;

/**
 * Created by federico.marchesi on 26/01/2017.
 */

public class RaceCursorAdapter extends CursorAdapter {
    private static final String TAG = RaceCursorAdapter.class.getName();
    private LayoutInflater cursorInflater;
    private boolean useColors;
    private Context mContext;


    public RaceCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        mContext = context;
        cursorInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        useColors = PreferenceManager.getDefaultSharedPreferences(context).
                getBoolean(Global.KEY_PREF_COLOURED_UI, true);
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layout;
        if (useColors)
            layout = R.layout.race_item;
        else
            layout = R.layout.race_item_simple;
        //return LayoutInflater.from(context).inflate(R.layout.activity_brand_list, parent, false);
        return cursorInflater.inflate(layout, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {

        // Find fields to populate in inflated template
        TextView raceTV = (TextView) view.findViewById(R.id.race_name);

//        if(useColors){
//            RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.container);
//            layout.setBackground(mContext.getResources().getDrawable(R.attr.selectableItemBackground))
////
//        }
        //TextView tvPriority = (TextView) view.findViewById(R.id.tvPriority);
        // Extract properties from cursor
        final String race = cursor.getString(
                cursor.getColumnIndex(RaceEntry.RACE_NAME));
        int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper._ID));

        TextView raceDate = (TextView) view.findViewById(R.id.race_date);
        String stringDate = DateConverter.fromUnixToDate(
                cursor.getInt(cursor.getColumnIndex(RaceEntry.RACE_DATETIME)),
                DateConverter.FORMAT_DATE);
        raceDate.setText(stringDate);

        RelativeLayout container = (RelativeLayout) view.findViewById(R.id.container);
        container.setTag(id);

        raceTV.setTag(id);
        // Populate fields with extracted properties
        raceTV.setText(race);

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                editRace(context, v);
                return false;
            }
        });
//        raceTV.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                editRace(context, v);
//                return false;
//            }
//        });
        //tvPriority.setText(String.valueOf(priority));
        ImageView deleteImage = (ImageView) view.findViewById(R.id.delete_button);

        deleteImage.setTag(id);

        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(context)
                        //set message, title, and icon
                        .setTitle(context.getResources().getString(R.string.dialog_delete_title))
                        .setMessage(String.format(context.getResources().getString(R.string.dialog_delete_message), race))
                        .setIcon(R.drawable.delete)

                        .setPositiveButton(context.getResources().getString(R.string.dialog_delete_button), new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                DatabaseHelper dbHelper = new DatabaseHelper(context);

                                ImageView delete = (ImageView) view.findViewById(R.id.delete_button);
                                dbHelper.deleteRace((int) delete.getTag());
                                Cursor d = dbHelper.getCursor(RaceEntry.TABLE, RaceEntry.RACE_DATETIME);
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


        InputDialogRace inputDialog = new InputDialogRace(context, R.string.edit_race_dialog_title,
                R.string.add_race_dialog_hint);
        inputDialog.setRaceName(race[0].getName());
        inputDialog.setRaceDescr(race[0].getDesc());
        inputDialog.setRaceDate(race[0].getDate());

        inputDialog.setInputListener(new InputDialogRace.InputListener() {

            @Override
            public InputDialogRace.ValidationResult isInputValid(String input) {
                if (!input.isEmpty()) {
                    return new InputDialogRace.ValidationResult(true, 0);
                }

                return new InputDialogRace.ValidationResult(false, R.string.race_name_mandatory);
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
                Cursor c = dbHelper.getCursor(RaceEntry.TABLE, RaceEntry.RACE_DATETIME);
                Cursor old = swapCursor(c);
                old.close();

            }


        });
        inputDialog.show();


    }

    public boolean isUseColors() {
        return useColors;
    }
}