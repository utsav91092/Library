package com.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

    public static final String ISO_OFFSET_DATE_TIME = "yyyy-MM-dd'T'HH:mm:ssZ";
    public static final String ISO_DATE = "yyyy-MM-dd";


    public static final String TIME_ZONE = "UTC+05:30";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final SimpleDateFormat YYYY_MM_DD_SDF = new SimpleDateFormat(YYYY_MM_DD);

    public static Date toYearMonthDateFormat(String dateString) throws ParseException {
        YYYY_MM_DD_SDF.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));
        YYYY_MM_DD_SDF.setLenient(false);
        return YYYY_MM_DD_SDF.parse(dateString);
    }

    public static String getCurrentDateString() {
        return YYYY_MM_DD_SDF.format(new Date());
    }

    public static Date getCurrentDate() throws ParseException {
        YYYY_MM_DD_SDF.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));
        return YYYY_MM_DD_SDF.parse(YYYY_MM_DD_SDF.format(new Date()));
    }
}

