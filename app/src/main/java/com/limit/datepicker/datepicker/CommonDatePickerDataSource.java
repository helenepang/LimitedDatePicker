package com.limit.datepicker.datepicker;

import android.content.Context;

import com.limit.datepicker.R;
import com.limit.datepicker.datepicker.simonvt.NumberPicker;
import com.limit.datepicker.datepicker.utils.CalendarDateUtils;

import java.util.Calendar;

/**
 * Common data source of DatePicker
 */

public class CommonDatePickerDataSource extends DatePicker.AbstractDatePickerDataSource {
    private String mDayFormatString;
    private String mMonthFormatString;
    public CommonDatePickerDataSource(Context context, String minDate, String maxDate) {
        super(context, minDate, maxDate);
        mDayFormatString = context.getString(R.string.day_format_string);
        mMonthFormatString = context.getString(R.string.month_format_string);
        mDayFormatter = new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.format(mDayFormatString, value);
            }
        };

        mMonthFormatter = new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.format(mMonthFormatString, value + 1);
            }
        };
    }

    public String[] monthStringsForYear(int year) {
        int monthOfYear = monthNumForYear(year) + 1;
        String[] strings = new String[monthOfYear];
        for (int i = 0; i < monthOfYear; i++) {
            strings[i] = String.format(mMonthFormatString, i + 1);
        }
        return strings;
    }

    @Override
    public String[] dayStringsForYearAndMonth(int year, int month) {
        int days = dayNumForYearAndMonth(year, month);
        String[] strings = new String[days];
        for (int i = 0; i < days; i++) {
            strings[i] = String.format(mDayFormatString, i + 1);
        }
        return strings;
    }

    @Override
    public int getMinMonth() {
        return mMinDate.get(Calendar.MONTH);
    }

    @Override
    public int getMaxMonth() {
        return mMaxDate.get(Calendar.MONTH);
    }

    @Override
    public boolean isMaxMonth(){
        return CalendarDateUtils.isSameMonth(mCurrentDate,mMaxDate);
    }

    @Override
    public boolean isMinMonth(){
        return CalendarDateUtils.isSameMonth(mCurrentDate,mMinDate);
    }

    @Override
    public boolean isMinYear() {
        return CalendarDateUtils.isSameYear(mCurrentDate,mMinDate);
    }

    @Override
    public boolean isMaxYear() {
        return CalendarDateUtils.isSameYear(mCurrentDate,mMaxDate);
    }

    @Override
    public int getMinLunarDate() {
        return 0;
    }

    @Override
    public int getMaxLunarDate() {
        return 0;
    }
}


