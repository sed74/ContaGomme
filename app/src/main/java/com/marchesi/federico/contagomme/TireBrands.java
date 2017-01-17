package com.marchesi.federico.contagomme;

/**
 * Created by federico.marchesi on 17/01/2017.
 */

public class TireBrands {
    private String mName;
    private int mRelatedId = 0;
    private boolean mFrontTyreSelected = false;
    private boolean mRearTyreSelected = false;


    public TireBrands(String name) {
        mName = name;
    }

    public TireBrands(int id, String name) {
        mName = name;
        mRelatedId = id;
    }

    public String getName() {
        return mName;
    }

    public boolean isFrontTyreSelected() {
        return mFrontTyreSelected;
    }

    public void setFrontTyreSelected(boolean isSelected) {
        mFrontTyreSelected = isSelected;
    }

    public boolean isRearTyreSelected() {
        return mRearTyreSelected;
    }

    public void setRearTyreSelected(boolean isSelected) {
        mRearTyreSelected = isSelected;
    }
}
