package com.marchesi.federico.contagomme;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
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

    private OnValueChangeListener changeListener;

    public TireAdapter(Context context, ArrayList<TireBrands> tireBrands) {
        super(context, 0, tireBrands);
        changeListener = null;

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

        TextView textView = (TextView) listItemView.findViewById(R.id.tire_name);
        textView.setText(currentTire.getName());

        TextView viewFrontWheel = (TextView) listItemView.findViewById(R.id.front_tire);
        TextView viewRearWheel = (TextView) listItemView.findViewById(R.id.rear_tire);

        TextView.OnClickListener mOnClickListener = (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isFrontSelected, isRearSelected;
                isFrontSelected = currentTire.getFrontTyreSelected();
                isRearSelected = currentTire.getRearTyreSelected();

                if (v.getId() == R.id.front_tire) {
                    if (isFrontSelected) {
                        currentTire.setFrontTyreSelected(false);
                        setSelected(((TextView) v), false);
                        FrontSelected = false;
                    } else {
                        if (FrontSelected) return;
                        currentTire.setFrontTyreSelected(true);
                        setSelected(((TextView) v), true);
                        FrontSelected = true;
                    }

                } else if (v.getId() == R.id.rear_tire) {
                    if (isRearSelected) {
                        currentTire.setRearTyreSelected(false);
                        setSelected(((TextView) v), false);
                        RearSelected = false;
                    } else {
                        if (RearSelected) return;
                        currentTire.setRearTyreSelected(true);
                        setSelected(((TextView) v), true);
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

    private void setSelected(TextView view, boolean isSelected) {
        if (!isSelected) {
//        view.setTypeface(null, Typeface.NORMAL);
//        view.setAllCaps(false);
            view.setBackgroundColor(Color.TRANSPARENT);
        } else {
//        view.setTypeface(null, Typeface.BOLD);
//        view.setAllCaps(true);
            view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.wheel_selected));
        }

    }

    public void setOnValueChangeListener(OnValueChangeListener lister) {
        changeListener = lister;

    }

    public interface OnValueChangeListener {
        void onValueChange(boolean frontSelected, boolean rearSelected);
    }

}
