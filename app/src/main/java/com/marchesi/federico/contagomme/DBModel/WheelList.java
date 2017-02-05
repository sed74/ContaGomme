package com.marchesi.federico.contagomme.DBModel;

/**
 * Created by federico.marchesi on 26/01/2017.
 */

public class WheelList {
    private int mWheelListId;
    private int mRaceId;
    private int mBrandId;
    private int mTotFrontWheel = 0;
    private int mTotRearWheel = 0;
    private boolean mFrontTireSelected = false;
    private boolean mRearTireSelected = false;


    public WheelList() {
    }

    public WheelList(int wheelListId) {
        mWheelListId = wheelListId;
    }

    public WheelList(int wheelListId, int raceId, int brandId) {
        mWheelListId = wheelListId;
        mRaceId = raceId;
        mBrandId = brandId;
    }

    public WheelList(int wheelListId, int raceId, int brandId, int totFront, int totRear) {
        mWheelListId = wheelListId;
        mRaceId = raceId;
        mBrandId = brandId;
        mTotFrontWheel = totFront;
        mTotRearWheel = totRear;
    }

    // getters
    public int getId() {
        return mWheelListId;
    }

    // setters
    public void setId(int wheelListId) {
        this.mWheelListId = wheelListId;
    }

    public int getTotRearWheel() {
        return mTotRearWheel;
    }

    public void setTotRearWheel(int totRearWheel) {
        this.mTotRearWheel = totRearWheel;
    }

    public int getTotFrontWheel() {
        return mTotFrontWheel;
    }

    public void setTotFrontWheel(int totFrontWheel) {
        this.mTotFrontWheel = totFrontWheel;
    }

    public int getRaceId() {
        return mRaceId;
    }

    public void setRaceId(int raceId) {
        this.mRaceId = raceId;
    }

    public int getBrandId() {
        return mBrandId;
    }

    public void setBrandId(int brandId) {
        this.mBrandId = brandId;
    }

    public boolean getIsFrontTireSelected() {
        return mFrontTireSelected;
    }

    public boolean getIsRearTireSelected() {
        return mRearTireSelected;
    }

    public void setFrontTireSelected(boolean mFrontTireSelected) {
        this.mFrontTireSelected = mFrontTireSelected;
        if (mFrontTireSelected) {
            incrementFront();
        } else {
            decrementFront();
        }
    }

    public void setRearTireSelected(boolean mRearTireSelected) {
        this.mRearTireSelected = mRearTireSelected;
        if (mRearTireSelected) {
            incrementRear();
        } else {
            decrementRear();
        }
    }

    public void incrementFront() {
        mTotFrontWheel++;
    }

    public void incrementRear() {
        mTotRearWheel++;
    }

    public void decrementFront() {
        mTotFrontWheel--;
        if (mTotFrontWheel < 0) mTotFrontWheel = 0;
    }

    public void decrementRear() {
        mTotRearWheel--;
        if (mTotRearWheel < 0) mTotRearWheel = 0;
    }

    public void resetSelection() {
        mFrontTireSelected = false;
        mRearTireSelected = false;
    }
}
