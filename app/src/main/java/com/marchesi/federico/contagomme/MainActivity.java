package com.marchesi.federico.contagomme;

import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        LinearLayout linearLayout = new LinearLayout(this);
        setContentView(linearLayout);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        String[] tyreBrands = getResources().getStringArray(R.array.tire_brands);
        for (String tire : tyreBrands) {
            TextView textView = new TextView(this);
            textView = setTextViewStandardProperties(textView);
            textView.setText(tire);
            linearLayout.addView(textView);
        }


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
