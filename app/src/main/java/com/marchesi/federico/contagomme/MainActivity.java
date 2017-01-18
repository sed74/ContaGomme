package com.marchesi.federico.contagomme;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<TireBrands> arrayTireBrands = new ArrayList<>();
    TireAdapter tireAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        LinearLayout linearLayout = new LinearLayout(this);
//        setContentView(linearLayout);
//        linearLayout.setOrientation(LinearLayout.VERTICAL);
//
        String[] tyreBrands = getResources().getStringArray(R.array.tire_brands);

        for (String tire : tyreBrands) {
            arrayTireBrands.add(new TireBrands(tire));

        }

        final Button nextButton = (Button) findViewById(R.id.button_next);

        tireAdapter = new TireAdapter(this, arrayTireBrands);

        tireAdapter.setOnValueChangeListener(new TireAdapter.OnValueChangeListener() {
            @Override
            public void onValueChange(boolean frontSelected, boolean rearSelected) {
                nextButton.setEnabled(frontSelected && rearSelected);
            }
        });


        ListView obj = (ListView) findViewById(R.id.list);
        obj.setAdapter(tireAdapter);


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListView list = (ListView) findViewById(R.id.list);

//                for (int i = 0; i < list.getChildCount(); i++) {
//                    list.setSelection(i);
//                    TextView text = (TextView) list.findViewById(R.id.tire_name);
//                    text.setText("fede");
//                }
                if (tireAdapter.canProceed()) {
                    Toast.makeText(MainActivity.this, "Procedo!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Non procedo!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void init() {

    }

    private TextView setTextViewStandardProperties(TextView textView) {
        textView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        0);
        params.setMargins(5, 15, 5, 5);
        params.weight = 1;
        params.gravity = Gravity.CENTER;
        textView.setPadding(16, 16, 16, 16);
        textView.setLayoutParams(params);
        textView.setBackgroundColor(Color.LTGRAY);
        return textView;
    }

}
