package com.marchesi.federico.contagomme;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by federico.marchesi on 02/02/2017.
 */

public class DateConverter {
    public static final String FORMAT_DATE = "dd/MM/yyyy";
    public static final String FORMAT_DATE_SHORT = "dd/MM/yy";
    public static final String FORMAT_DATE_TIME_SHORT = "dd/MM/yyyy HH:mm:ss";
    public static final String FORMAT_DATE_TIME = "dd/MM/yyyy HH:mm:ss z";


    private DateConverter() {
    }

    public static String fromUnixToDate(long unixDate, String dateFormat) {
        Date date = new Date(unixDate * 1000L); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.ITALY); // the format of your date
        sdf.setTimeZone(TimeZone.getDefault()); // give a timezone reference for formating (see comment at the bottom

        return sdf.format(date);

    }

    public static String fromUnixToDate(long unixDate) {
        return fromUnixToDate(unixDate, FORMAT_DATE);
    }

    public static String fromUnixToDateTime(long unixDate) {
        return fromUnixToDate(unixDate, FORMAT_DATE_TIME);
    }

    public static long fromDateToUnix(String stringDate, String dateFormat) {

        DateFormat formatter = new SimpleDateFormat(dateFormat, Locale.ITALY);
        Date date = null;
        try {
            date = formatter.parse(stringDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (date.getTime() / 1000L);
    }

}
