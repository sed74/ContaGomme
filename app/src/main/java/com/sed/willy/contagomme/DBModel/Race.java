package com.sed.willy.contagomme.DBModel;

import com.sed.willy.contagomme.Utils.DateConverter;

/**
 * Created by federico.marchesi on 26/01/2017.
 */

public class Race {
    private int raceId;
    private String raceName;
    private String raceDesc;
    //    private String raceDate;
    private long raceDateTime;

    public Race() {
    }

    public Race(String name) {
        raceName = name;
    }

    public Race(String name, String descr, String date) {
        raceName = name;
//        raceDate = date;
        raceDateTime = DateConverter.fromDateToUnix(date, DateConverter.FORMAT_DATE);
        raceDesc = descr;
    }

    public Race(String name, String descr, long date) {
        raceName = name;
        raceDateTime = date;
//        raceDate = DateConverter.fromUnixToDate(date, DateConverter.FORMAT_DATE);
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
        String stringDate = DateConverter.fromUnixToDate(raceDateTime, DateConverter.FORMAT_DATE);
        return stringDate;
    }

    public void setDate(String raceDate) {
        this.raceDateTime = DateConverter.fromDateToUnix(raceDate, DateConverter.FORMAT_DATE);
    }

    public long getDateTime() {
        return raceDateTime;
    }

    public void setDateTime(long raceDateTime) {
        this.raceDateTime = raceDateTime;
    }
}
