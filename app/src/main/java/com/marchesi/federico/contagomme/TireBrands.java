package com.marchesi.federico.contagomme;

/**
 * Created by federico.marchesi on 17/01/2017.
 */

public class TireBrands {
    private String mName;
    private int mRelatedId = 0;
    private int mTotFrontTyre = 0;
    private int mTotRearTyre = 0;


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

    public int getTotFrontTyre() {
        return mTotFrontTyre;
    }

    public int getTotRearTyre() {
        return mTotRearTyre;
    }

    public void incrementFrontTyre() {
        mTotFrontTyre += 1;
    }

    public void decrementFrontTyre() {
        mTotFrontTyre -= 1;
    }

}
