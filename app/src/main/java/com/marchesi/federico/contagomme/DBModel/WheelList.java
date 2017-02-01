package com.marchesi.federico.contagomme.DBModel;

/**
 * Created by federico.marchesi on 26/01/2017.
 */

public class WheelList {
    int wheelListId;
    int raceId;
    int brandId;
    int totFrontWheel;
    int totRearWheel;

    public WheelList() {
    }

    public WheelList(int RaceId, int BrandId, int TotFront, int TotRear) {
        raceId = RaceId;
        brandId = BrandId;
        totFrontWheel = TotFront;
        totRearWheel = TotRear;
    }

    public int getId() {
        return wheelListId;
    }

    public void setId(int wheelListId) {
        this.wheelListId = wheelListId;
    }

    public int getRaceId() {
        return raceId;
    }

    public void setRaceId(int raceId) {
        this.raceId = raceId;
    }

    public int getBrandId() {
        return brandId;
    }

    // getters

    // setters
    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public int getTotFrontWheel() {
        return totFrontWheel;
    }

    public void setTotFrontWheel(int totFrontWheel) {
        this.totFrontWheel = totFrontWheel;
    }

    public int getTotRearWheel() {
        return totRearWheel;
    }

    public void setTotRearWheel(int totRearWheel) {
        this.totRearWheel = totRearWheel;
    }
}
