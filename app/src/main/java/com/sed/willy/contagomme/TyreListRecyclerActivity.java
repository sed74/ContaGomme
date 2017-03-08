package com.sed.willy.contagomme;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.sed.willy.contagomme.DBHelper.DatabaseHelper;
import com.sed.willy.contagomme.Dialog.InputDialogBrand;
import com.sed.willy.contagomme.Helper.OnStartDragListener;
import com.sed.willy.contagomme.Helper.SimpleItemTouchHelperCallback;

public class TyreListRecyclerActivity extends AppCompatActivity implements OnStartDragListener {

    private ItemTouchHelper mItemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tyre_list_recycler);

        new DatabaseHelper(this).checkBrandTable();

        RecyclerView view = (RecyclerView) findViewById(R.id.list);

        final RecyclerListAdapter adapter = new RecyclerListAdapter(getBaseContext(), this);

        RecyclerView recyclerView = view;
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter, view);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        final FloatingActionButton addTire = (FloatingActionButton) findViewById(R.id.add_tire);

        addTire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddBrandDialog(adapter);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0)
                    addTire.hide();
                else if (dy < 0)
                    addTire.show();
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        new DatabaseHelper(this).purgeBrands();
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    private void showAddBrandDialog(final RecyclerListAdapter adapter) {

        final DatabaseHelper dbHelper = new DatabaseHelper(this);
        InputDialogBrand inputDialog = new InputDialogBrand(this, R.string.add_brand_dialog_title, R.string.add_brand_dialog_hint);
        inputDialog.setInputListener(new InputDialogBrand.InputListener() {
            @Override
            public InputDialogBrand.ValidationResult isInputValid(String newCoffeeType) {
                if (newCoffeeType.isEmpty()) {
//                    return new InputDialog.ValidationResult(false, R.string.error_empty_name);
                }
                return new InputDialogBrand.ValidationResult(true, 0);
            }

            @Override
            public void onConfirm(String brandName, int order) {
                adapter.addBrand(brandName);

            }
        });
        inputDialog.show();
    }

}
