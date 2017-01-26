package com.marchesi.federico.contagomme;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.marchesi.federico.contagomme.DBHelper.DatabaseHelper;

/**
 * Created by federico.marchesi on 26/01/2017.
 */

public class BrandCursorAdapter extends CursorAdapter {
    private LayoutInflater cursorInflater;


    public BrandCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        cursorInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //return LayoutInflater.from(context).inflate(R.layout.activity_brand_list, parent, false);
        return cursorInflater.inflate(R.layout.brand_item, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {

        // Find fields to populate in inflated template
        TextView brandTV = (TextView) view.findViewById(R.id.brand_name);
        //TextView tvPriority = (TextView) view.findViewById(R.id.tvPriority);
        // Extract properties from cursor
        String brand = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_BRAND_NAME));
        int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper._ID));

        // Populate fields with extracted properties
        brandTV.setText(brand + "(" + String.valueOf(id) + ")");
        //tvPriority.setText(String.valueOf(priority));

    }
}