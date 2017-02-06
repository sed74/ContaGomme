package com.marchesi.federico.contagomme.DBModel;

import android.content.Context;

import com.marchesi.federico.contagomme.DBHelper.DatabaseHelper;

/**
 * Created by federico.marchesi on 26/01/2017.
 */

public class WheelList {
    private int mWheelListId;
    private int mRaceId;
    private int mBrandId;
    private String mBrandName;
    private int mTotFrontWheel = 0;
    private int mTotRearWheel = 0;
    private boolean mFrontTireSelected = false;
    private boolean mRearTireSelected = false;
    private Context mContext;


    public WheelList() {
    }

    public WheelList(Context context, int wheelListId) {
        mWheelListId = wheelListId;
        mContext = context;
    }

    public WheelList(int wheelListId, int raceId, int brandId) {
        mWheelListId = wheelListId;
        mRaceId = raceId;
        mBrandId = brandId;
//        mBrandName = new DatabaseHelper(mContext).getStringField(DatabaseHelper.TABLE_BRANDS,
//                DatabaseHelper._ID, brandId, DatabaseHelper.COLUMN_BRAND_NAME);
    }

    public WheelList(int wheelListId, int raceId, int brandId, int totFront, int totRear) {
        mWheelListId = wheelListId;
        mRaceId = raceId;
        mBrandId = brandId;
        mTotFrontWheel = totFront;
        mTotRearWheel = totRear;

    }

    public WheelList(Context context, int wheelListId, int raceId, int brandId, int totFront, int totRear) {
        mWheelListId = wheelListId;
        mRaceId = raceId;
        mBrandId = brandId;
        mTotFrontWheel = totFront;
        mTotRearWheel = totRear;
        mContext = context;

        mBrandName = new DatabaseHelper(context).getStringField(DatabaseHelper.TABLE_BRANDS,
                DatabaseHelper._ID, brandId, DatabaseHelper.COLUMN_BRAND_NAME);
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

    public void resetCounter() {
        mTotFrontWheel = 0;
        mTotRearWheel = 0;
    }

    public String getBrandName() {
        return mBrandName;
    }
}
