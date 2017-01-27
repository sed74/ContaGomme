package com.marchesi.federico.contagomme;

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
        final String brand = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_BRAND_NAME));
        int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper._ID));

        // Populate fields with extracted properties
        brandTV.setText(brand);
        //tvPriority.setText(String.valueOf(priority));
        ImageView deleteImage = (ImageView) view.findViewById(R.id.delete_button);

        deleteImage.setTag(id);

        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog myQuittingDialogBox = new AlertDialog.Builder(context)
                        //set message, title, and icon
                        .setTitle(context.getResources().getString(R.string.dialog_delete_title))
                        .setMessage(String.format(context.getResources().getString(R.string.dialog_delete_message), brand))
                        .setIcon(R.drawable.delete)

                        .setPositiveButton(context.getResources().getString(R.string.dialog_delete_button), new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                Cursor cur = cursor;
                                DatabaseHelper dbHelper = new DatabaseHelper(context);

                                ImageView delete = (ImageView) view.findViewById(R.id.delete_button);
                                dbHelper.deleteBrand((int) delete.getTag());
                                Cursor d = dbHelper.getBrandsCursor();
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
}