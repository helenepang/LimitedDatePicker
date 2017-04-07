package com.limit.datepicker.datepicker;

import android.content.Context;

import com.limit.datepicker.datepicker.simonvt.NumberPicker;
import com.limit.datepicker.datepicker.utils.Lunar;


/**
 * A Chinese lunar calendar data source
 */
public class LunarDatePickerDataSource extends DatePicker.AbstractDatePickerDataSource {
    private Lunar mCurrentLunar;


    public LunarDatePickerDataSource(Context context, String minDate, String maxDate) {
        super(context, minDate, maxDate);
        mContext = context;
        mCurrentLunar = new Lunar(mCurrentDate.getTimeInMillis());

        mDayFormatter = new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return Lunar.getLunarDayString(value);
            }
        };

        mMonthFormatter = new NumberPicker.Formatter() {
            @Override
            public String format(int value) { //1.2.3.4.
                int leapMonth = Lunar.getLunarLeapMonth(mCurrentLunar.getLunarYear());
                String string = "";
                if (mCurrentLunar.isLeapYear() && value == leapMonth + 1) {
                    string = "闰";
                }
                return string + Lunar.getLunarMonthString(mCurrentLunar.isLeapYear() && value >= leapMonth+1 ? value - 1 : value);
            }
        };
    }

    @Override
    public int monthNum() {
        return monthNumForYear(mCurrentLunar.getLunarYear());
    }

    @Override
    public int monthNumForYear(int year) {
        return getLunarMonthNum(year);
    }

    @Override
    public int dayNum() {
        return dayNumForYearAndMonth(mCurrentLunar.getLunarYear(), getLunarMonthIndexWithLeap(mCurrentLunar));
    }

    @Override
    public int dayNumForYearAndMonth(int year, int month) {
        return getLunarDaysOfMonth(year, month);
    }

    @Override
    protected void updateCurrentTime(long timeInMills) {
        super.updateCurrentTime(timeInMills);
        mCurrentLunar = new Lunar(timeInMills);
    }

    @Override
    public int getYear() {
        return mCurrentLunar.getLunarYear();
    }

    @Override
    public int getMonth() {
        return getLunarMonthIndexWithLeap(mCurrentLunar);
    }

    @Override
    public int getDayOfMonth() {
        return mCurrentLunar.getLunarDay();
    }

    @Override
    public void updateDay(int newDay) {
        long timeInMills = Lunar.getTimeInMillsWithLunar(mCurrentLunar.getLunarYear(), mCurrentLunar.getLunarMonth(),
                mCurrentLunar.isLeap(), newDay);
        updateDate(timeInMills);
    }

    @Override
    public void updateMonth(int newMonth) {
        int leapMonth = Lunar.getLunarLeapMonth(mCurrentLunar.getLunarYear());
        int lunarMonth = leapMonth > 0 && newMonth >= leapMonth+1 ? newMonth - 1 : newMonth;
        int lunarDay = ifSpinnerDayOverMaxDayOfMonth(mCurrentLunar.getLunarYear(),newMonth,mCurrentLunar.getLunarDay());
        long timeInMills = Lunar.getTimeInMillsWithLunar(mCurrentLunar.getLunarYear(),lunarMonth ,
                newMonth == leapMonth + 1, lunarDay);
        updateDate(timeInMills);
    }

    //防止日期越界跳到下一个月
    private int ifSpinnerDayOverMaxDayOfMonth(int lunarYear, int lunarMonth, int currentLunarDay){
        int daynum = dayNumForYearAndMonth(lunarYear, lunarMonth);
        return currentLunarDay>daynum?daynum:currentLunarDay;
    }

    @Override
    public void updateYear(int newYear) {
        long timeInMills = Lunar.getTimeInMillsWithLunar(newYear, mCurrentLunar.getLunarMonth(),
                Lunar.getLunarLeapMonth(newYear) == mCurrentLunar.getLunarMonth(), mCurrentLunar.getLunarDay());
        updateDate(timeInMills);
    }

    // ------------------- Lunar Helpers ---------------------

    /**
     * Get the month counts of a lunar year
     * @param lunarYear
     * @return
     */
    private static int getLunarMonthNum(int lunarYear) {
        return Lunar.getLunarLeapMonth(lunarYear) > 0 ? 13 : 12;
    }

    /**
     * Start from 1 - 13
     * @param lunar
     * @return
     */
    private static int getLunarMonthIndexWithLeap(Lunar lunar) {
        if (lunar.isLeapYear()) {
            int leapMonth = Lunar.getLunarLeapMonth(lunar.getLunarYear());
            if (lunar.isLeap() || lunar.getLunarMonth() > leapMonth) {
                return lunar.getLunarMonth() + 1;
            }
        }
        return lunar.getLunarMonth();
    }

    /**
     * Get the days count of lunarYear, lunarMonthWithLeap
     * @param lunarYear
     * @param lunarMonthWithLeap The month index with leap month,
     *                           so if the leap month of the year is 9,
     *                           then the leap month index is 10
     * @return
     */
    private static int getLunarDaysOfMonth(int lunarYear, int lunarMonthWithLeap) {
        int leapMonth = Lunar.getLunarLeapMonth(lunarYear);
        if (leapMonth != 0 && leapMonth == lunarMonthWithLeap - 1) {
            return Lunar.getLunarLeapDays(lunarYear);
        }
        return Lunar.getLunarMonthDays(lunarYear, leapMonth!=0 && lunarMonthWithLeap > leapMonth ? lunarMonthWithLeap - 1 : lunarMonthWithLeap);
    }

    @Override
    public int getMinMonth() {
        Lunar lunar = new Lunar(mMinDate.getTime());
        return getLunarMonthIndexWithLeap(lunar);
    }

    @Override
    public int getMaxMonth() {
        Lunar lunar = new Lunar(mMaxDate.getTime());
        return getLunarMonthIndexWithLeap(lunar);
    }

    /**
     * 以下覆写是因为:
     * 农历当中的最大年份月份日期等均需要对应农历的年份规则
     * */

    @Override
    public int getMinLunarDate() {
        Lunar lunar = new Lunar(mMinDate.getTime());
        return lunar.getLunarDay();
    }

    @Override
    public int getMaxLunarDate() {
        Lunar lunar = new Lunar(mMaxDate.getTime());
        return lunar.getLunarDay();
    }

    @Override
    public boolean isMinMonth() {
        Lunar minLunar = new Lunar(mMinDate.getTime());
        Lunar curLunar = new Lunar(mCurrentDate.getTime());
        return isMinYear() && minLunar.getLunarMonth() == curLunar.getLunarMonth();
    }

    @Override
    public boolean isMaxMonth() {
        Lunar maxLunar = new Lunar(mMaxDate.getTime());
        Lunar curLunar = new Lunar(mCurrentDate.getTime());
        return isMaxYear() && maxLunar.getLunarMonth() == curLunar.getLunarMonth();
    }

    @Override
    public boolean isMinYear() {
        Lunar minLunar = new Lunar(mMinDate.getTime());
        Lunar curLunar = new Lunar(mCurrentDate.getTime());
        return minLunar.getLunarYear() == curLunar.getLunarYear();
    }

    @Override
    public boolean isMaxYear() {
        Lunar maxLunar = new Lunar(mMaxDate.getTime());
        Lunar curLunar = new Lunar(mCurrentDate.getTime());
        return maxLunar.getLunarYear() == curLunar.getLunarYear();
    }
}