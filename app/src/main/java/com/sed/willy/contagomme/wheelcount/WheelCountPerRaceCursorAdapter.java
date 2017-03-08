package com.sed.willy.contagomme.wheelcount;

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

import com.sed.willy.contagomme.DBContract.BrandContract.BrandEntry;
import com.sed.willy.contagomme.DBContract.ViewsContract.WhellListEntry;
import com.sed.willy.contagomme.DBHelper.DatabaseHelper;
import com.sed.willy.contagomme.DBModel.WheelList;
import com.sed.willy.contagomme.R;

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
    private OnChange onChangeListener;

    private int frontBrandSelected;
    private int rearBrandSelected;


    public WheelCountPerRaceCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        mContext = context;
        cursorInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

    }

    public ArrayList<WheelList> populateArray(Cursor cursor) {
        if (wheelLists != null) {
            wheelLists = null;
            wheelLists = new ArrayList<>();
        }
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                wheelLists.add(new WheelList(
                        cursor.getInt(
                                cursor.getColumnIndex(WhellListEntry._ID)),
                        cursor.getString(
                                cursor.getColumnIndex(WhellListEntry.BRAND_NAME)),
                        cursor.getInt(
                                cursor.getColumnIndex(WhellListEntry.RACE_ID)),
                        cursor.getInt(
                                cursor.getColumnIndex(WhellListEntry.BRAND_ID)),
                        cursor.getInt(
                                cursor.getColumnIndex(WhellListEntry.TOT_FRONT_WHEEL)),
                        cursor.getInt(
                                cursor.getColumnIndex(WhellListEntry.TOT_REAR_WHEEL))));
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
        // Extract properties from cursor
        final String race = cursor.getString(
                cursor.getColumnIndex(BrandEntry.BRAND_NAME));
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
                cursor.getColumnIndex(WhellListEntry.TOT_FRONT_WHEEL))));

        rearCount = (TextView) view.findViewById(R.id.rear_tire_count);
        rearCount.setText(String.valueOf(cursor.getInt(
                cursor.getColumnIndex(WhellListEntry.TOT_REAR_WHEEL))));


        TextView.OnClickListener mOnClickListener = (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isFrontSelected, isRearSelected;
                isFrontSelected = currentWheelList.getIsFrontTireSelected();
                isRearSelected = currentWheelList.getIsRearTireSelected();

                handleSelection(currentWheelList, v, v.getId() == R.id.front_tire,
                        (v.getId() == R.id.front_tire ? !isFrontSelected : !isRearSelected));
//                if (v.getId() == R.id.front_tire) {
//
//                    if (isFrontSelected) {
//                        currentWheelList.setFrontTireSelected(false);
//                        setSelected(((TextView) v), false, true);
//                        FrontSelected = false;
//                    } else {
//                        if (FrontSelected) return;
//                        currentWheelList.setFrontTireSelected(true);
//                        setSelected(((TextView) v), true, true);
//                        FrontSelected = true;
//                    }
//
//                } else if (v.getId() == R.id.rear_tire) {
//                    if (isRearSelected) {
//                        currentWheelList.setRearTireSelected(false);
//                        setSelected(((TextView) v), false, false);
//                        RearSelected = false;
//                    } else {
//                        if (RearSelected) return;
//                        currentWheelList.setRearTireSelected(true);
//                        setSelected(((TextView) v), true, false);
//                        RearSelected = true;
//                    }
//
//                }
                onChangeListener.onSelectionChange(FrontSelected, RearSelected);
                DatabaseHelper dbHelper = new DatabaseHelper(context);
                dbHelper.updateWheelList(currentWheelList);
                dbHelper.close();

            }
        });

        viewFrontWheel.setOnClickListener(mOnClickListener);
        viewRearWheel.setOnClickListener(mOnClickListener);
    }

    /**
     * Method used to respond to clicks on the list
     *
     * @param currentWheelList
     * @param v
     * @param isFront
     * @param isSelected
     */
    private void handleSelection(WheelList currentWheelList, View v, boolean isFront,
                                 boolean isSelected) {

        // If we're selecting a front wheel when another front wheel is selected, we skip.
        // If we're selecting a rear wheel when another rear wheel is selected, we skip.
        if ((isFront && isSelected && FrontSelected) ||
                (!isFront && isSelected && RearSelected)) return;

        currentWheelList.setTireSelected(isFront, isSelected);
        setSelected(((TextView) v), isSelected, isFront);

        if (isFront) {
            frontBrandSelected = (isSelected ? currentWheelList.getBrandId() : 0);
            FrontSelected = isSelected;
        } else {
            rearBrandSelected = (isSelected ? currentWheelList.getBrandId() : 0);
            RearSelected = isSelected;
        }

        if (frontBrandSelected != 0 && rearBrandSelected != 0) {
            onChangeListener.onSelectionChange(frontBrandSelected, rearBrandSelected);
            frontBrandSelected = 0;
            rearBrandSelected = 0;
        }
    }

    public WheelList getObjectFromId(int wheelListId) {
        for (WheelList array : wheelLists) {
            if (array.getId() == wheelListId) return array;
        }
        return null;
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

    public void setOnChangeListener(OnChange listener) {
        onChangeListener = listener;
    }

    public interface OnChange {
        void onSelectionChange(boolean frontSelected, boolean rearSelected);

        void onSelectionChange(int frontSelected, int rearSelected);
    }


}