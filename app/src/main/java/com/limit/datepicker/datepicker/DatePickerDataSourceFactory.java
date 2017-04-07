package com.limit.datepicker.datepicker;

import android.content.Context;


public class DatePickerDataSourceFactory {
    public static final int MODE_LUNAR = 1;
    public static final int MODE_COMMON = 2;

    public static DatePicker.DatePickerDataSource getDataSourceByMode(int mode, Context context, String minDate, String maxDate) {
        switch (mode) {
            case MODE_COMMON:
                return new CommonDatePickerDataSource(context, minDate, maxDate);
            default:
                return new LunarDatePickerDataSource(context, minDate, maxDate);
        }
    }
}
