package com.sed.willy.contagomme;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.sed.willy.contagomme.DBModel.Brand;
import com.sed.willy.contagomme.Helper.OnStartDragListener;
import com.sed.willy.contagomme.Helper.SimpleItemTouchHelperCallback;

import java.util.ArrayList;

public class TyreListRecyclerActivity extends AppCompatActivity implements OnStartDragListener {
    private ItemTouchHelper mItemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tyre_list_recycler);

        RecyclerView view = (RecyclerView) findViewById(R.id.list);

        RecyclerListAdapter adapter = new RecyclerListAdapter(getBaseContext(), this);

        RecyclerView recyclerView = view;
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }


    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }
}
