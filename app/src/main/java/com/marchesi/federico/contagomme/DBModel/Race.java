package com.marchesi.federico.contagomme.DBModel;

/**
 * Created by federico.marchesi on 26/01/2017.
 */

public class Race {
    int raceId;
    String raceName;
    String racePlace;
    String raceDesc;
    String raceDate;

    public Race() {
    }

    public Race(String name) {
        raceName = name;
    }

    public Race(String name, String place) {
        raceName = name;
        racePlace = place;
    }

    // getters
    public int getRaceId() {
        return raceId;
    }

    // Setters
    public void setRaceId(int raceId) {
        this.raceId = raceId;
    }

    public String getRaceName() {
        return raceName;
    }

    public void setRaceName(String name) {
        raceName = name;
    }

    public String getRaceDesc() {
        return raceDesc;
    }

    public void setRaceDesc(String raceDesc) {
        this.raceDesc = raceDesc;
    }

    public String getRacePlace() {
        return racePlace;
    }

    public void setRacePlace(String place) {
        racePlace = place;
    }

    public String getRaceDate() {
        return raceDate;
    }

    public void setRaceDate(String raceDate) {
        this.raceDate = raceDate;
    }
}
