package com.marchesi.federico.contagomme.TireCount;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.marchesi.federico.contagomme.DBModel.WheelList;
import com.marchesi.federico.contagomme.R;

import java.util.ArrayList;

/**
 * Created by federico.marchesi on 17/01/2017.
 */

public class TireCountAdapter extends ArrayAdapter<WheelList> {
    private boolean FrontSelected = false;
    private boolean RearSelected = false;
    private TextView frontCount;
    private TextView rearCount;
    private OnValueChangeListener changeListener;
    private ArrayList<WheelList> mArrayTireCount;

    public TireCountAdapter(Context context, ArrayList<WheelList> tireCount) {
        super(context, 0, tireCount);
        changeListener = null;
        mArrayTireCount = tireCount;
    }

    @NonNull
    @Override
    public View getView(int position, final View convertView, final ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.tire_button, parent, false);
        }


        final WheelList currentTire = getItem(position);

        FrontSelected = currentTire.getIsFrontTireSelected();
        RearSelected = currentTire.getIsRearTireSelected();

        TextView textView = (TextView) listItemView.findViewById(R.id.tire_name);
        textView.setText(currentTire.getBrandName());

        TextView viewFrontWheel = (TextView) listItemView.findViewById(R.id.front_tire);
        TextView viewRearWheel = (TextView) listItemView.findViewById(R.id.rear_tire);

        setSelected(viewFrontWheel, FrontSelected, true);
        setSelected(viewRearWheel, FrontSelected, false);

        frontCount = (TextView) listItemView.findViewById(R.id.front_tire_count);
        frontCount.setText(String.valueOf(currentTire.getTotFrontWheel()));

        rearCount = (TextView) listItemView.findViewById(R.id.rear_tire_count);
        rearCount.setText(String.valueOf(currentTire.getTotRearWheel()));

        TextView.OnClickListener mOnClickListener = (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isFrontSelected, isRearSelected;
                isFrontSelected = currentTire.getIsFrontTireSelected();
                isRearSelected = currentTire.getIsRearTireSelected();

                if (v.getId() == R.id.front_tire) {
                    if (isFrontSelected) {
                        currentTire.setFrontTireSelected(false);
                        setSelected(((TextView) v), false, true);
                        FrontSelected = false;
                    } else {
                        if (FrontSelected) return;
                        currentTire.setFrontTireSelected(true);
                        setSelected(((TextView) v), true, true);
                        FrontSelected = true;
                    }

                } else if (v.getId() == R.id.rear_tire) {
                    if (isRearSelected) {
                        currentTire.setRearTireSelected(false);
                        setSelected(((TextView) v), false, false);
                        RearSelected = false;
                    } else {
                        if (RearSelected) return;
                        currentTire.setRearTireSelected(true);
                        setSelected(((TextView) v), true, false);
                        RearSelected = true;
                    }

                }
                changeListener.onValueChange(FrontSelected, RearSelected);
            }
        });

        viewFrontWheel.setOnClickListener(mOnClickListener);
        viewRearWheel.setOnClickListener(mOnClickListener);

        return listItemView;
    }

    public boolean canProceed() {
        return FrontSelected && RearSelected;
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
                    new int[]{getContext().getResources().getColor(R.color.wheel_selected),
                            getContext().getResources().getColor(R.color.rectangle_solid_brand)});
//            gd.setCornerRadius(0f);

            view.setBackground(gd);
//            view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.wheel_selected));
        }

    }

    public void setOnValueChangeListener(OnValueChangeListener lister) {
        changeListener = lister;
    }

    public void removeOnValueChangeListener() {
        changeListener = null;
    }

    public interface OnValueChangeListener {
        void onValueChange(boolean frontSelected, boolean rearSelected);
    }

}
