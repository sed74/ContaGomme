package com.sed.willy.contagomme;

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

import java.util.ArrayList;

/**
 * Created by federico.marchesi on 17/01/2017.
 */

public class TireAdapter extends ArrayAdapter<TireBrands> {
    private boolean FrontSelected = false;
    private boolean RearSelected = false;
    private TextView frontCount;
    private TextView rearCount;
    private OnValueChangeListener changeListener;
    private ArrayList<TireBrands> mArrayTire;

    public TireAdapter(Context context, ArrayList<TireBrands> tireBrands) {
        super(context, 0, tireBrands);
        changeListener = null;
        mArrayTire = tireBrands;
    }

    @NonNull
    @Override
    public View getView(int position, final View convertView, final ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.tire_button, parent, false);
        }


        final TireBrands currentTire = getItem(position);

        FrontSelected = currentTire.getFrontTyreSelected();
        RearSelected = currentTire.getRearTyreSelected();

        TextView textView = (TextView) listItemView.findViewById(R.id.tire_name);
        textView.setText(currentTire.getName());

        TextView viewFrontWheel = (TextView) listItemView.findViewById(R.id.front_tire);
        TextView viewRearWheel = (TextView) listItemView.findViewById(R.id.rear_tire);

        setSelected(viewFrontWheel, FrontSelected, true);
        setSelected(viewRearWheel, FrontSelected, false);

        frontCount = (TextView) listItemView.findViewById(R.id.front_tire_count);
        frontCount.setText(String.valueOf(currentTire.getTotFrontSelected()));

        rearCount = (TextView) listItemView.findViewById(R.id.rear_tire_count);
        rearCount.setText(String.valueOf(currentTire.getTotRearSelected()));

        TextView.OnClickListener mOnClickListener = (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isFrontSelected, isRearSelected;
                isFrontSelected = currentTire.getFrontTyreSelected();
                isRearSelected = currentTire.getRearTyreSelected();

                if (v.getId() == R.id.front_tire) {
                    if (isFrontSelected) {
                        currentTire.setFrontTyreSelected(false);
                        setSelected(((TextView) v), false, true);
                        FrontSelected = false;
                    } else {
                        if (FrontSelected) return;
                        currentTire.setFrontTyreSelected(true);
                        setSelected(((TextView) v), true, true);
                        FrontSelected = true;
                    }

                } else if (v.getId() == R.id.rear_tire) {
                    if (isRearSelected) {
                        currentTire.setRearTyreSelected(false);
                        setSelected(((TextView) v), false, false);
                        RearSelected = false;
                    } else {
                        if (RearSelected) return;
                        currentTire.setRearTyreSelected(true);
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

    private void deselectAll(boolean isFront) {

        if (isFront) {
            FrontSelected = false;
            for (TireBrands t : mArrayTire) {
                t.setFrontTyreSelected(false);
            }
        } else {
            RearSelected = false;
            for (TireBrands t : mArrayTire) {
                t.setRearTyreSelected(false);
            }
        }
        notifyDataSetChanged();
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
