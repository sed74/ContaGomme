package com.marchesi.federico.contagomme.DBModel;

/**
 * Created by federico.marchesi on 26/01/2017.
 */

public class Race {
    int raceId;
    String raceName;
    String raceDesc;
    String raceDate;

    public Race() {
    }

    public Race(String name) {
        raceName = name;
    }

    public Race(String name, String descr, String date) {
        raceName = name;
        raceDate = date;
        raceDesc = descr;
    }

    // getters
    public int getId() {
        return raceId;
    }

    // Setters
    public void setId(int raceId) {
        this.raceId = raceId;
    }

    public String getName() {
        return raceName;
    }

    public void setName(String name) {
        raceName = name;
    }

    public String getDesc() {
        return raceDesc;
    }

    public void setDesc(String raceDesc) {
        this.raceDesc = raceDesc;
    }

    public String getDate() {
        return raceDate;
    }

    public void setDate(String raceDate) {
        this.raceDate = raceDate;
    }
}
