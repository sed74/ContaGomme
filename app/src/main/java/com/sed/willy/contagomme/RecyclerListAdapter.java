/*
 * Copyright (C) 2015 Paul Burke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sed.willy.contagomme;

import android.content.Context;
import android.content.res.Resources;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sed.willy.contagomme.DBContract.BrandContract.BrandEntry;
import com.sed.willy.contagomme.DBHelper.DatabaseHelper;
import com.sed.willy.contagomme.DBModel.Brand;
import com.sed.willy.contagomme.Helper.ItemTouchHelperAdapter;
import com.sed.willy.contagomme.Helper.ItemTouchHelperViewHolder;
import com.sed.willy.contagomme.Helper.OnStartDragListener;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Simple RecyclerView.Adapter that implements {@link ItemTouchHelperAdapter} to respond to move and
 * dismiss events from a {@link android.support.v7.widget.helper.ItemTouchHelper}.
 *
 * @author Paul Burke (ipaulpro)
 */
public class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {

    private final OnStartDragListener mDragStartListener;
    private ArrayList<Brand> mBrandItems = new ArrayList<>();
    private ArrayList<Brand> mBrandItemsToDelete = new ArrayList<>();
    private Context mContext;

    public RecyclerListAdapter(Context context, OnStartDragListener dragStartListener) {
        mDragStartListener = dragStartListener;
//        mItems.addAll(Arrays.asList(context.getResources().getStringArray(R.array.dummy_items)));
        // specify an adapter (see also next example)
        mContext = context;
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        mBrandItems = (ArrayList<Brand>) dbHelper.getAllBrands(true);
        dbHelper.close();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.brand_item_dragdrop, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
//        holder.mTireBrandName.setText(mItems.get(position));
        holder.mTireBrandName.setText(mBrandItems.get(position).getName());

        // Start a drag whenever the handle view it touched
        holder.handleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });
    }


    @Override
    public void onItemDismiss(int position) {
//        DatabaseHelper dbHelper = new DatabaseHelper(mContext);
//        dbHelper.deleteBrand(mBrandItems.get(position).getId());
//        dbHelper.close();
//        mBrandItems.remove(position);
//        notifyItemRemoved(position);
    }

    @Override
    public void onItemRemoved(final RecyclerView.ViewHolder viewHolder,
                              final RecyclerView recyclerView) {
        final DatabaseHelper dbHelper = new DatabaseHelper(mContext);
        final int adapterPosition = viewHolder.getAdapterPosition();
        final Brand brand = mBrandItems.get(adapterPosition);
        final int brandID = brand.getId();


        final Resources res = mContext.getResources();
        Snackbar snackbar = Snackbar
                .make(recyclerView, res.getString(R.string.removed_message,
                        brand.getName().toUpperCase()), Snackbar.LENGTH_LONG)
                .setAction(res.getString(R.string.button_undo), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        int mAdapterPosition = viewHolder.getAdapterPosition();

                        mBrandItems.add(adapterPosition, brand);
                        notifyItemInserted(adapterPosition);
                        recyclerView.scrollToPosition(adapterPosition);
                        mBrandItemsToDelete.remove(brand);
                        dbHelper.unDeleteBrand(brandID);
                        Snackbar.make(view, res.getString(R.string.remove_undone,
                                brand.getName().toUpperCase()), Snackbar.LENGTH_SHORT).show();
                    }
                })
                .setDuration(5000)
                .addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {

                        dbHelper.deleteBrand(brandID, false);
                    }
                });

        snackbar.show();
        dbHelper.deleteBrand(brandID, true);
        mBrandItems.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
        mBrandItemsToDelete.add(brand);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        DatabaseHelper dbHelper = new DatabaseHelper(mContext);
        dbHelper.switchBrand(mBrandItems, fromPosition, toPosition);
        dbHelper.close();
        Collections.swap(mBrandItems, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }


    @Override
    public int getItemCount() {
        return mBrandItems.size();
    }

    public void addBrand(String brandName) {
        DatabaseHelper dbHelper = new DatabaseHelper(mContext);

        int order = dbHelper.getMax(BrandEntry.TABLE, BrandEntry.BRAND_ORDER, 0);
        Brand brand = new Brand(brandName, order + 10);
        mBrandItems.add(brand);
        dbHelper.createBrand(brand);
        notifyDataSetChanged();
    }

    /**
     * Simple example of a view holder that implements {@link ItemTouchHelperViewHolder} and has a
     * "handle" view that initiates a drag event when touched.
     */
    public static class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {

        public final TextView mTireBrandName;
        public final ImageView handleView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mTireBrandName = (TextView) itemView.findViewById(R.id.brand_name);
            handleView = (ImageView) itemView.findViewById(R.id.handle);
        }

        @Override
        public void onItemSelected(int actionState) {
            TextView text = (TextView) itemView.findViewById(R.id.brand_name);

//            itemView.setBackgroundColor(Color.LTGRAY);
            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                text.setBackground(itemView.getResources().getDrawable(R.drawable.tire_rectangle_gray));
            } else if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                text.setBackground(itemView.getResources().getDrawable(R.drawable.tire_rectangle_red));
            }
        }

        @Override
        public void onItemClear() {

            TextView text = (TextView) itemView.findViewById(R.id.brand_name);
            text.setBackground(itemView.getResources().getDrawable(R.drawable.tire_rectangle_shadow));
        }
    }
}
