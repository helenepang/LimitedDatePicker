package com.limit.datepicker;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.limit.datepicker.datepicker.fragment.DatePickerDialogFragment;
import com.limit.datepicker.datepicker.utils.LunarUtils;

import java.util.Calendar;

public class MainActivity extends Activity implements DatePickerDialogFragment.DatePickerListener{

    /**
     * 日历模式:阳历
     */
    private static final int MODE_COMMON = 0;
    /**
     * 日历模式:农历
     */
    private static final int MODE_LUNAR = 1;

    /**
     * 当前显示的日历
     */
    private Calendar mCurrentCalendar = Calendar.getInstance();

    /***
     * 日历dialog
     */
    private DatePickerDialogFragment mDatePickerDialog;

    /**
     * 当前选中的格式,默认阳历
     */
    private int mDateMode = MODE_COMMON;

    /**
     * 显示日期的TextView
     */
    private TextView tvDateContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvDateContent = $(R.id.tv_date);
        tvDateContent.setText(LunarUtils.solarFormatDate(mCurrentCalendar));
    }


    public void onShowDatePicker(View view){
        if (mDatePickerDialog == null) {
            mDatePickerDialog = new DatePickerDialogFragment();
            mDatePickerDialog.setDatePickerListener(this);
        }
        if (!mDatePickerDialog.isVisible()) {
            mDatePickerDialog.setTime(mCurrentCalendar.getTimeInMillis(), mDateMode);
            mDatePickerDialog.show(getFragmentManager(), "DatePickerDialogFragment");
        }
    }

    /***
     * 选中日期后回调
     * @param calendar
     * @param isLunar
     */
    @Override
    public void onPickDate(Calendar calendar, int isLunar) {
        mCurrentCalendar.setTimeInMillis(calendar.getTimeInMillis());
        this.mDateMode = isLunar;

        switch (isLunar){
            case MODE_COMMON:
                tvDateContent.setText(LunarUtils.solarFormatDate(calendar));
                break;
            case MODE_LUNAR:
                tvDateContent.setText(getResources().getString(R.string.default_string_format_date,
                        LunarUtils.lunarFormatDate(calendar),
                        LunarUtils.solarFormatDateWithNoYear(calendar)));
                break;
            default:
        }
    }

    public <T> T $(int id) {
        return (T) findViewById(id);
    }
}
