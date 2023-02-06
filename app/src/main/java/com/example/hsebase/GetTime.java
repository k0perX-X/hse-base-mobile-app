package com.example.hsebase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GetTime {
    static public String initTime() {
        Date currentTime = new Date();
        Locale locale = new Locale("ru", "RU");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", locale);

        return simpleDateFormat.format(currentTime) + ", " +
                getDayStringOld(currentTime, locale).substring(0, 1).toUpperCase() +
                getDayStringOld(currentTime, locale).substring(1);
    }

    private static String getDayStringOld(Date date, Locale locale) {
        DateFormat formatter = new SimpleDateFormat("EEEE", locale);
        return formatter.format(date);
    }
}
