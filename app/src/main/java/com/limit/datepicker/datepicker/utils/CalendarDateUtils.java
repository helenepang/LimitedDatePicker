package com.limit.datepicker.datepicker.utils;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class CalendarDateUtils {
    /**
     * Return if the two calendar is in the same day, ignoring time
     * @param day1
     * @param day2
     * @return
     */
    public static boolean isSameDay(Calendar day1, Calendar day2) {
        return day1.get(Calendar.YEAR) == day2.get(Calendar.YEAR)
                && day1.get(Calendar.MONTH) == day2.get(Calendar.MONTH)
                && day1.get(Calendar.DAY_OF_MONTH) == day2.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Return if the two calendar is in the same month
     * @param day1
     * @param day2
     * @return
     */
    public static boolean isSameMonth(Calendar day1, Calendar day2) {
        return day1.get(Calendar.YEAR) == day2.get(Calendar.YEAR)
                && day1.get(Calendar.MONTH) == day2.get(Calendar.MONTH);
    }

    /**
     * Return if the two calendar is in the same year
     * @param day1
     * @param day2
     * @return
     */
    public static boolean isSameYear(Calendar day1, Calendar day2) {
        return day1.get(Calendar.YEAR) == day2.get(Calendar.YEAR);
    }


    /**
     * Get julian day by unix timestamp
     * @param mills
     * @return
     */
    public static double getJulianDay(long mills) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(mills);

        int year;
        int month;
        float day;

        int b = 0;
        double fraction;
        fraction = (calendar.get(Calendar.HOUR_OF_DAY) / 0.000024 + calendar.get(Calendar.MINUTE) / 0.001440);

        DecimalFormat decimalFormat = new DecimalFormat("0");
        day = calendar.get(Calendar.DAY_OF_MONTH);
        day = Float.parseFloat(decimalFormat.format(day) + "." + decimalFormat.format(Math.round(fraction)));
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;

        if (month < 3) {
            year--;
            month += 12;
        }
        if (calendar.getTimeInMillis() - calendar.getGregorianChange().getTime() > 0) {
            int a = year / 100;
            b = 2 - a + a / 4;
        }

        double julianDay;
        julianDay = Math.floor(365.25 * year) + Math.floor(30.6001 * (month + 1)) + day + 1720994.5 + b;
        return julianDay;
    }

    /**
     * Get unix timestamp by julian day
     * @param julianDay
     * @return
     */
    public static long getMills(double julianDay) {
        double intBit, fraction, a, b, c, d, e, m, aux;
        Date date = new Date();
        julianDay += 0.5;
        intBit = Math.floor(julianDay);
        fraction = julianDay - intBit;

        if (intBit >= 2299161.0) {
            a = Math.floor((intBit - 1867216.25) / 36524.25);
            a = intBit + 1 + a - Math.floor(a / 4);
        } else {
            a = intBit;
        }

        b = a + 1524;
        c = Math.floor((b - 122.1) / 365.25);
        d = Math.floor(365.25 * c);
        e = Math.floor((b - d) / 30.6001);
        aux = b - d - Math.floor(30.6001 * e) + fraction;

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, (int) aux);
        aux = ((aux - calendar.get(Calendar.DAY_OF_MONTH)) * 24);
        calendar.set(Calendar.HOUR_OF_DAY, (int) aux);
        calendar.set(Calendar.MINUTE, (int) ((aux - calendar.get(Calendar.HOUR_OF_DAY)) * 60));

        if (e < 13.5) {
            m = e - 1;
        } else {
            m = e - 13;
        }
        // Se le resta uno al mes por el manejo de JAVA, donde los meses empiezan en 0.
        calendar.set(Calendar.MONTH, (int) m - 1);
        if (m > 2.5) {
            calendar.set(Calendar.YEAR, (int) (c - 4716));
        } else {
            calendar.set(Calendar.YEAR, (int) (c - 4715));
        }
        return calendar.getTimeInMillis();
    }
}
