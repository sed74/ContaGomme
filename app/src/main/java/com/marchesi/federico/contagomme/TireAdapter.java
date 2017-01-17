package com.marchesi.federico.contagomme;

import android.content.Context;
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
    private ArrayList<TireBrands> mTireBrands;

    public TireAdapter(Context context, ArrayList<TireBrands> tireBrands) {
        super(context, 0, tireBrands);

        mTireBrands = tireBrands;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.tire_button, parent, false);
        }

        TireBrands currentTire = getItem(position);

        TextView textView = (TextView) listItemView.findViewById(R.id.tire_name);
        textView.setText(currentTire.getName());


        return listItemView;
    }
}
