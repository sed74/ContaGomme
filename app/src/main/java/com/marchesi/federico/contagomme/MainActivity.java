package com.marchesi.federico.contagomme;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
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


        tireAdapter = new TireAdapter(this, arrayTireBrands);

        ListView obj = (ListView) findViewById(R.id.list);
        obj.setAdapter(tireAdapter);

/*        view[0] = (ButtonTire) findViewById(R.id.tire1);
        view[1] = (ButtonTire) findViewById(R.id.tire2);
        view[2] = (ButtonTire) findViewById(R.id.tire3);
        view[3] = (ButtonTire) findViewById(R.id.tire4);
        view[4] = (ButtonTire) findViewById(R.id.tire5);
        view[5] = (ButtonTire) findViewById(R.id.tire6);
        view[6] = (ButtonTire) findViewById(R.id.tire7);
        view[7] = (ButtonTire) findViewById(R.id.tire8);



        int i = 0;
        for (String tire : tyreBrands) {
            view[i].setName(tire);
            i++;
        }*/


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
