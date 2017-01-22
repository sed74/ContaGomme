package com.marchesi.federico.contagomme;

/**
 * Created by federico.marchesi on 17/01/2017.
 */

public class TireBrands {
    private String mName;
    private int mRelatedId = 0;
    private boolean mFrontTyreSelected = false;
    private boolean mRearTyreSelected = false;
    private int mTotFrontSelected = 0;
    private int mTotRearSelected = 0;


    public TireBrands(String name) {
        mName = name;
    }

    public TireBrands(String name, int frontSelected, int rearSelected) {
        mName = name;
        mTotFrontSelected = frontSelected;
        mTotRearSelected = rearSelected;
    }

    public String getName() {
        return mName;
    }

    public boolean getFrontTyreSelected() {
        return mFrontTyreSelected;
    }

    public void setFrontTyreSelected(boolean isSelected) {

        mFrontTyreSelected = isSelected;
        if (isSelected) {
            incrementFront();
        } else {
            decrementFront();
        }
    }

    public void resetSelection() {
        mFrontTyreSelected = false;
        mRearTyreSelected = false;
    }

    public boolean getRearTyreSelected() {
        return mRearTyreSelected;
    }

    public void setRearTyreSelected(boolean isSelected) {
        mRearTyreSelected = isSelected;
        if (isSelected) {
            incrementRear();
        } else {
            decrementRear();
        }
    }

    public int getTotFrontSelected() {
        return mTotFrontSelected;
    }

    public void setTotFrontSelected(int totFrontSelected) {
        this.mTotFrontSelected = totFrontSelected;
    }

    public int getTotRearSelected() {
        return mTotRearSelected;
    }

    public void setmTotRearSelected(int totRearSelected) {
        this.mTotRearSelected = totRearSelected;
    }

    public void incrementFront() {
        mTotFrontSelected++;
    }

    public void incrementRear() {
        mTotRearSelected++;
    }

    public void decrementFront() {
        mTotFrontSelected--;
        if (mTotFrontSelected < 0) mTotFrontSelected = 0;
    }

    public void decrementRear() {
        mTotRearSelected--;
        if (mTotRearSelected < 0) mTotRearSelected = 0;
    }

}

