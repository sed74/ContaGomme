package com.sed.willy.contagomme.DBModel;

/**
 * Created by federico.marchesi on 26/01/2017.
 */

public class BikeDetails {
    private int mBikeDetailId;
    private int mRaceId;
    private int mFrontBrandId;
    private int mRearBrandId;
    private int mInserted;

    public BikeDetails() {
        super();
    }

    public BikeDetails(int raceId, int frontBrandId, int rearBrandId, int inserted) {
        mRaceId = raceId;
        mFrontBrandId = frontBrandId;
        mRearBrandId = rearBrandId;
        mInserted = inserted;
    }

    // Getters
    public int getId() {
        return mBikeDetailId;
    }

    // Setters
    public void setId(int bikeDetailId) {
        this.mBikeDetailId = bikeDetailId;
    }

    public int getFrontBrandId() {
        return mFrontBrandId;
    }

    public void setFrontBrandId(int frontBrandId) {
        this.mFrontBrandId = frontBrandId;
    }

    public int getRearBrandId() {
        return mRearBrandId;
    }

    public void setRearBrandId(int rearBrandId) {
        this.mRearBrandId = rearBrandId;
    }

    public int getRaceId() {
        return mRaceId;
    }

    public void setRaceId(int raceId) {
        this.mRaceId = raceId;
    }

    public int getInserted() {
        return mInserted;
    }

    public void setInserted(int inserted) {
        this.mInserted = inserted;
    }

}