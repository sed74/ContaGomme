package com.marchesi.federico.contagomme.DBModel;

/**
 * Created by federico.marchesi on 26/01/2017.
 */

public class WheelList {
    private int mWheelListId;
    private int mRaceId;
    private int mBrandId;
    private int mTotFrontWheel;
    private int mTotRearWheel;

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

    public void setId(int wheelListId) {
        this.mWheelListId = wheelListId;
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

    // setters
    public void setBrandId(int brandId) {
        this.mBrandId = brandId;
    }

    public int getTotFrontWheel() {
        return mTotFrontWheel;
    }

    public void setTotFrontWheel(int totFrontWheel) {
        this.mTotFrontWheel = totFrontWheel;
    }

    public int getTotRearWheel() {
        return mTotRearWheel;
    }

    public void setTotRearWheel(int totRearWheel) {
        this.mTotRearWheel = totRearWheel;
    }
}
