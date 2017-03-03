package com.sed.willy.contagomme;

import android.graphics.Color;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sed.willy.contagomme.DBModel.Brand;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by federico.marchesi on 01/03/2017.
 */

public class TyreRecylerAdapter extends RecyclerView.Adapter<TyreRecylerAdapter.ViewHolder> {
//    private String[] mDataset;

    private static final int PENDING_REMOVAL_TIMEOUT = 3000; // 3sec

    //    List<String> items;
//    List<String> itemsPendingRemoval;
    int lastInsertedIndex; // so we can add some more items for testing purposes
    boolean undoOn; // is undo on, you can turn it on from the toolbar menu
    HashMap<Brand, Runnable> pendingRunnables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be
    private Handler handler = new Handler(); // hanlder for running delayed runnables
    private ArrayList<Brand> mArrayTire;
    private ArrayList<Brand> arrayTirePendigRemoval;

    // Provide a suitable constructor (depends on the kind of dataset)
    public TyreRecylerAdapter(ArrayList<Brand> tireBrands) {

        mArrayTire = tireBrands;
        arrayTirePendigRemoval = new ArrayList<>();
    }


    // Create new views (invoked by the layout manager)
    @Override
    public TyreRecylerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        // create a new view
//        TextView v = (TextView) LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.tire_button, parent, false);
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tire_button, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        holder.mTextView.setText(mArrayTire.get(position).getName());

        final Brand item = mArrayTire.get(position);

        if (arrayTirePendigRemoval.contains(item)) {
            // we need to show the "undo" state of the row
//            holder.itemView.setBackgroundColor(TyreListRecyclerActivity.getColor(R.color.left_swype_undo_background));
            holder.mTextView.setVisibility(View.GONE);
            holder.undoButton.setVisibility(View.VISIBLE);
            holder.undoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // user wants to undo the removal, let's cancel the pending task
                    Runnable pendingRemovalRunnable = pendingRunnables.get(item);
                    pendingRunnables.remove(item);
                    if (pendingRemovalRunnable != null)
                        handler.removeCallbacks(pendingRemovalRunnable);
                    arrayTirePendigRemoval.remove(item);
                    // this will rebind the row in "normal" state
                    notifyItemChanged(mArrayTire.indexOf(item));
                }
            });
        } else {
            // we need to show the "normal" state
            holder.itemView.setBackgroundColor(Color.WHITE);
            holder.mTextView.setVisibility(View.VISIBLE);
            holder.mTextView.setText(item.getName());
            holder.undoButton.setVisibility(View.GONE);
            holder.undoButton.setOnClickListener(null);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mArrayTire.size();
    }

    public boolean isUndoOn() {
        return undoOn;
    }

    public void setUndoOn(boolean undoOn) {
        this.undoOn = undoOn;
    }

    public void pendingRemoval(int position) {
//        final String item = items.get(position);
        final Brand item = mArrayTire.get(position);
        if (!arrayTirePendigRemoval.contains(item)) {
            arrayTirePendigRemoval.add(item);
            notifyItemChanged(position);
            // let's create, store and post a runnable to remove the item
            Runnable pendingRemovalRunnable = new Runnable() {
                @Override
                public void run() {
                    remove(mArrayTire.indexOf(item));
                }
            };
            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(item, pendingRemovalRunnable);
        }

//        if (!itemsPendingRemoval.contains(item)) {
//            itemsPendingRemoval.add(item);
//            // this will redraw row in "undo" state
//            notifyItemChanged(position);
//            // let's create, store and post a runnable to remove the item
//            Runnable pendingRemovalRunnable = new Runnable() {
//                @Override
//                public void run() {
//                    remove(items.indexOf(item));
//                }
//            };
//            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
//            pendingRunnables.put(item, pendingRemovalRunnable);
//        }
    }

    public void remove(int position) {
        Brand item = mArrayTire.get(position);
        if (arrayTirePendigRemoval.contains(item)) {
            arrayTirePendigRemoval.remove(item);
        }
        if (mArrayTire.contains(item)) {
            mArrayTire.remove(position);
        }
//        String item = items.get(position);
//        if (itemsPendingRemoval.contains(item)) {
//            itemsPendingRemoval.remove(item);
//        }
//        if (items.contains(item)) {
//            items.remove(position);
//            notifyItemRemoved(position);
//        }
    }

    public boolean isPendingRemoval(int position) {
//        String item = items.get(position);
        Brand item = mArrayTire.get(position);
        return arrayTirePendigRemoval.contains(item);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        //        TextView titleTextView;
        Button undoButton;

        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.tire_name);
            undoButton = (Button) v.findViewById(R.id.undo_button);
        }
    }

    /**
     * ViewHolder capable of presenting two states: "normal" and "undo" state.
     */
    static class TestViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;
        Button undoButton;

        public TestViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_view, parent, false));
            titleTextView = (TextView) itemView.findViewById(R.id.title_text_view);
            undoButton = (Button) itemView.findViewById(R.id.undo_button);
        }

    }

}
