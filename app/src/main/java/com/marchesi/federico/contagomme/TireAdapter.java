package com.marchesi.federico.contagomme;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
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
    private ArrayList<TireBrands> mTireBrands;

    public TireAdapter(Context context, ArrayList<TireBrands> tireBrands) {
        super(context, 0, tireBrands);

        mTireBrands = tireBrands;
    }

    @NonNull
    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
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
                isFrontSelected = currentTire.isFrontTyreSelected();
                isRearSelected = currentTire.isRearTyreSelected();

                if (v.getId() == R.id.front_tire) {
                    if (currentTire.isFrontTyreSelected()) {
                        ((TextView) v).setTypeface(null, Typeface.NORMAL);
                        currentTire.setFrontTyreSelected(false);
                        ((TextView) v).setAllCaps(false);
                        v.setBackgroundColor(Color.TRANSPARENT);
                        currentTire.setFrontTyreSelected(true);
                    } else {
                        if (isFrontSelected && isRearSelected) return;
                        ((TextView) v).setTypeface(null, Typeface.BOLD);
                        currentTire.setFrontTyreSelected(true);
                        ((TextView) v).setAllCaps(true);
                        v.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.wheel_selected));
                        currentTire.setFrontTyreSelected(false);
                    }

                } else if (v.getId() == R.id.rear_tire) {
                    if (currentTire.isRearTyreSelected()) {
                        ((TextView) v).setTypeface(null, Typeface.NORMAL);
                        ((TextView) v).setAllCaps(false);
                        currentTire.setRearTyreSelected(false);
                        v.setBackgroundColor(Color.TRANSPARENT);
                        currentTire.setFrontTyreSelected(true);
                    } else {
                        if (isFrontSelected && isRearSelected) return;
                        ((TextView) v).setTypeface(null, Typeface.BOLD);
                        currentTire.setRearTyreSelected(true);
                        ((TextView) v).setAllCaps(true);
                        v.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.wheel_selected));
                        currentTire.setFrontTyreSelected(false);
                    }

                }
            }
        });

        viewFrontWheel.setOnClickListener(mOnClickListener);
        viewRearWheel.setOnClickListener(mOnClickListener);

        return listItemView;
    }
}
