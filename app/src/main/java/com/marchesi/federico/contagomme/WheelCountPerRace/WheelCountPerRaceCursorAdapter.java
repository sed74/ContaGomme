package com.marchesi.federico.contagomme.WheelCountPerRace;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.marchesi.federico.contagomme.DBHelper.DatabaseHelper;
import com.marchesi.federico.contagomme.DBModel.Race;
import com.marchesi.federico.contagomme.DBModel.WheelList;
import com.marchesi.federico.contagomme.Dialog.InputDialogRace;
import com.marchesi.federico.contagomme.R;

import java.util.ArrayList;

/**
 * Created by federico.marchesi on 26/01/2017.
 */

public class WheelCountPerRaceCursorAdapter extends CursorAdapter {
    private static final String TAG = WheelCountPerRaceCursorAdapter.class.getName();
    Context mContext;
    private LayoutInflater cursorInflater;
    private boolean FrontSelected = false;
    private boolean RearSelected = false;
    private TextView frontCount;
    private TextView rearCount;
    private ArrayList<WheelList> wheelLists = new ArrayList<>();
    private View.OnClickListener clickListener;
    private OnChange onChangeListener;


    public WheelCountPerRaceCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        mContext = context;
        cursorInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

    }

    public ArrayList<WheelList> populateArray(Cursor cursor) {

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                wheelLists.add(new WheelList(
                        cursor.getInt(
                                cursor.getColumnIndex(DatabaseHelper._ID)),
                        cursor.getInt(
                                cursor.getColumnIndex("raceId")),
                        cursor.getInt(
                                cursor.getColumnIndex("brandId")),
                        cursor.getInt(
                                cursor.getColumnIndex(DatabaseHelper.COLUMN_WHEEL_TOT_FRONT_WHEEL)),
                        cursor.getInt(
                                cursor.getColumnIndex(DatabaseHelper.COLUMN_WHEEL_TOT_REAR_WHEEL))));
            } while (cursor.moveToNext());
        }
        return wheelLists;
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //return LayoutInflater.from(context).inflate(R.layout.activity_brand_list, parent, false);
//        populateArray(cursor);
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
                cursor.getColumnIndex(DatabaseHelper.COLUMN_BRAND_NAME));
        int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper._ID));

        final WheelList currentWheelList = getObjectFromId(id);
        if (currentWheelList == null) {
            Toast.makeText(context, "Errore!", Toast.LENGTH_SHORT).show();
        }

        TextView tireName = (TextView) view.findViewById(R.id.tire_name);
        tireName.setText(race);

        FrontSelected = currentWheelList.getIsFrontTireSelected();
        RearSelected = currentWheelList.getIsRearTireSelected();

        TextView viewFrontWheel = (TextView) view.findViewById(R.id.front_tire);
        TextView viewRearWheel = (TextView) view.findViewById(R.id.rear_tire);

        setSelected(viewFrontWheel, FrontSelected, true);
        setSelected(viewRearWheel, FrontSelected, false);

        frontCount = (TextView) view.findViewById(R.id.front_tire_count);
        frontCount.setText(String.valueOf(cursor.getInt(
                cursor.getColumnIndex(DatabaseHelper.COLUMN_WHEEL_TOT_FRONT_WHEEL))));

        rearCount = (TextView) view.findViewById(R.id.rear_tire_count);
        rearCount.setText(String.valueOf(cursor.getInt(
                cursor.getColumnIndex(DatabaseHelper.COLUMN_WHEEL_TOT_REAR_WHEEL))));


        TextView.OnClickListener mOnClickListener = (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isFrontSelected, isRearSelected;
                isFrontSelected = currentWheelList.getIsFrontTireSelected();
                isRearSelected = currentWheelList.getIsRearTireSelected();

                if (v.getId() == R.id.front_tire) {
                    if (isFrontSelected) {
                        currentWheelList.setFrontTireSelected(false);
                        setSelected(((TextView) v), false, true);
                        FrontSelected = false;
                    } else {
                        if (FrontSelected) return;
                        currentWheelList.setFrontTireSelected(true);
                        setSelected(((TextView) v), true, true);
                        FrontSelected = true;
                    }

                } else if (v.getId() == R.id.rear_tire) {
                    if (isRearSelected) {
                        currentWheelList.setRearTireSelected(false);
                        setSelected(((TextView) v), false, false);
                        RearSelected = false;
                    } else {
                        if (RearSelected) return;
                        currentWheelList.setRearTireSelected(true);
                        setSelected(((TextView) v), true, false);
                        RearSelected = true;
                    }

                }
                onChangeListener.onSelectionChange(FrontSelected, RearSelected);
                DatabaseHelper dbHelper = new DatabaseHelper(context);
                dbHelper.updateWheelList(currentWheelList);
                dbHelper.close();
//                notifyDataSetChanged();
            }
        });

        viewFrontWheel.setOnClickListener(mOnClickListener);
        viewRearWheel.setOnClickListener(mOnClickListener);
    }

    public void setOnChangeListener(OnChange listener) {
        onChangeListener = listener;
    }

    public WheelList getObjectFromId(int wheelListId) {
        for (WheelList array : wheelLists) {
            if (array.getId() == wheelListId) return array;
        }
        return null;
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
                Cursor c = dbHelper.getCursor(DatabaseHelper.TABLE_RACES, DatabaseHelper.COLUMN_RACE_DATETIME);
                Cursor old = swapCursor(c);
                old.close();
                //Toast.makeText(MainActivity.this, getResources().getString(R.string.data_saved), Toast.LENGTH_SHORT).show();
            }


        });
        inputDialog.show();
    }

    private void setSelected(TextView view, boolean isSelected, boolean isFront) {
        if (!isSelected) {
            view.setTypeface(null, Typeface.NORMAL);
//        view.setAllCaps(false);
            view.setBackgroundColor(Color.TRANSPARENT);

        } else {
            view.setTypeface(null, Typeface.BOLD);
//        view.setAllCaps(true);

            GradientDrawable gd = new GradientDrawable(
                    isFront ? GradientDrawable.Orientation.RIGHT_LEFT :
                            GradientDrawable.Orientation.LEFT_RIGHT,
                    new int[]{mContext.getResources().getColor(R.color.wheel_selected),
                            mContext.getResources().getColor(R.color.rectangle_solid_brand)});
//            gd.setCornerRadius(0f);

            view.setBackground(gd);
//            view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.wheel_selected));
        }

    }

    public interface OnChange {
        void onSelectionChange(boolean frontSelected, boolean rearSelected);
    }


}