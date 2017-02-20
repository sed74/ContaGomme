package com.sed.willy.contagomme;

/**
 * Created by federico.marchesi on 18/01/2017.
 */

public class WheelCounter {
    private int mTotFront = 0;
    private int mTotRear = 0;

    public int getTotFront() {
        return mTotFront;
    }

    public void setTotFront(int totFront) {
        this.mTotFront = totFront;
    }

    public int getTotRear() {
        return mTotRear;
    }

    public void setTotRear(int totRear) {
        this.mTotRear = totRear;
    }
}

