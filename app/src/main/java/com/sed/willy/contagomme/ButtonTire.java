package com.sed.willy.contagomme;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by federico.marchesi on 17/01/2017.
 */

public class ButtonTire extends LinearLayout {
    public ButtonTire(Context context) {
        super(context);
        setupLayout();
    }

    public ButtonTire(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupLayout();
    }

    public ButtonTire(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupLayout();
    }

    private void setupLayout() {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.tire_button, this);
    }

    public void setName(String name) {
        TextView textView = (TextView) this.findViewById(R.id.tire_name);
        textView.setText(name);
    }
}
