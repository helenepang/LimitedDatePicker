package com.limit.datepicker.datepicker.utils;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by helene on 17/4/7.
 */
public class LunarUtils {

    /**
     * 阳历
     * @param calendar
     * @return
     */
    public static String solarFormatDate(Calendar calendar){
        String date = String.format(Locale.getDefault(),
                "%d年%d月%d日",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH));
        return date;
    }

    /**
     * 阳历(去掉年份)
     * @param calendar
     * @return
     */
    public static String solarFormatDateWithNoYear(Calendar calendar){

        String date = String.format(Locale.getDefault(),
                "%d月%d日",
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH));
        return date;
    }

    /**
     * 农历
     * @param calendar
     * @return
     */
    public static String lunarFormatDate(Calendar calendar){
        Lunar lunar = new Lunar(calendar.getTimeInMillis());
        return String.format(Locale.getDefault(), "%d年农历%s%s",lunar.getLunarYear(),lunar.getLunarMonthString(),lunar.getLunarDayString());
    }



}
