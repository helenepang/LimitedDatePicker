package com.limit.datepicker.datepicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.limit.datepicker.R;
import com.limit.datepicker.datepicker.simonvt.NumberPicker;
import com.limit.datepicker.datepicker.utils.CalendarDateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Aurora Liu on 3/25/16.
 *
 * Provides a widget for selecting a date in Chinese lunar calendar or
 * common calendar in spinner mode.
 *
 * You can also use other calendar data sources which implementing {@link DatePickerDataSource}.
 *
 * You can use a custom layout by specify a layout resource with the R.styleable.DatePicker_pickerLayout attribute
 * But it must contains three NumberPickers with ids: year, month and day
 */
public class DatePicker extends FrameLayout {
    private static final String LOG_TAG = DatePicker.class.getSimpleName();

    private static final int DEFUALT_MODE = DatePickerDataSourceFactory.MODE_COMMON;

    private DatePickerDelegate mDelegate;
    private DatePickerDataSource mDataSource;
    private int mMode = DEFUALT_MODE;

    // ----------------------- Constructors -------------------------

    public DatePicker(Context context) {
        this(context, null);
    }

    public DatePicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DatePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public DatePicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);

        final TypedArray attributesArray = context.obtainStyledAttributes(attrs, R.styleable.DatePicker,
                defStyleAttr, defStyleRes);
        mMode = attributesArray.getInt(R.styleable.DatePicker_calendarMode, DEFUALT_MODE);
        String minDate = attributesArray.getString(R.styleable.DatePicker_minDate);
        String maxDate = attributesArray.getString(R.styleable.DatePicker_maxDate);
        int layoutResourceId = attributesArray.getResourceId(
                R.styleable.DatePicker_pickerLayout, 0);
        attributesArray.recycle();

        mDataSource = DatePickerDataSourceFactory.getDataSourceByMode(mMode, context, minDate, maxDate);
        mDelegate = new BaseDatePickerDelegate(this, context, mDataSource, layoutResourceId);
    }

    /**
     * Get current data source mode
     * @return
     */
    public int getMode() {
        return mMode;
    }

    /**
     * Switch the mode of DatePicker
     * @param context
     * @param mode
     */
    public void setMode(Context context, int mode) {
        if (this.mMode != mode) {
            this.mMode = mode;
            DatePickerDataSource dataSource = DatePickerDataSourceFactory.getDataSourceByMode(mode, context, null, null);
            dataSource.setMinDate(mDataSource.getMinDate().getTimeInMillis());
            dataSource.setMaxDate(mDataSource.getMaxDate().getTimeInMillis());
            dataSource.updateDate(mDataSource.getTimeInMills());
            mDataSource = dataSource;
            mDelegate.setDataSource(mDataSource);
        }
    }

    public void resetMode(Context context, int mode){
        if(mode == DEFUALT_MODE){
            DatePickerDataSource dataSource = DatePickerDataSourceFactory.getDataSourceByMode(mode, context, null, null);
            dataSource.setMinDate(mDataSource.getMinDate().getTimeInMillis());
            dataSource.setMaxDate(mDataSource.getMaxDate().getTimeInMillis());
            dataSource.updateDate(mDataSource.getTimeInMills());
            mDataSource = dataSource;
            mDelegate.setDataSource(mDataSource);
        }

    }

    /**
     * Get current selected time
     * @return
     */
    public long getTimeInMills() {
        return mDataSource.getTimeInMills();
    }

    public void setOnDateChangedListener(OnDateChangedListener onDateChangedListener) {
        this.mDelegate.setOnDateChangedListener(onDateChangedListener);
    }

    /**
     * Update the current date.
     * @param timeInMills
     */
    public void updateDate(long timeInMills) {
        mDataSource.updateDate(timeInMills);
    }

    /**
     * Gets the minimal date supported by this {@link DatePicker} in
     * milliseconds since January 1, 1970 00:00:00 in
     * {@link TimeZone#getDefault()} time zone.
     *
     * @return The minimal supported date.
     */
    public long getMinDate() {
        return mDataSource.getMinDate().getTimeInMillis();
    }

    /**
     * Sets the minimal date supported by this {@link NumberPicker} in
     * milliseconds since January 1, 1970 00:00:00 in
     * {@link TimeZone#getDefault()} time zone.
     *
     * @param minDate The minimal supported date.
     */
    public void setMinDate(long minDate) {
        mDataSource.setMinDate(minDate);
    }

    /**
     * Gets the maximal date supported by this {@link DatePicker} in
     * milliseconds since January 1, 1970 00:00:00 in
     * {@link TimeZone#getDefault()} time zone.
     * <p>
     * Note: The default maximal date is 12/31/2070.
     * <p>
     *
     * @return The maximal supported date.
     */
    public long getMaxDate() {
        return mDataSource.getMaxDate().getTimeInMillis();
    }

    /**
     * Sets the maximal date supported by this {@link DatePicker} in
     * milliseconds since January 1, 1970 00:00:00 in
     * {@link TimeZone#getDefault()} time zone.
     *
     * @param maxDate The maximal supported date.
     */
    public void setMaxDate(long maxDate) {
        mDataSource.setMaxDate(maxDate);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return mDataSource.onSaveInstanceState(superState);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        BaseSavedState ss = (BaseSavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        mDataSource.onRestoreInstanceState(ss);
    }


    @Override
    public void setEnabled(boolean enabled) {
        if (mDelegate.isEnabled() == enabled) {
            return;
        }
        super.setEnabled(enabled);
        mDelegate.setEnabled(enabled);
    }

    @Override
    public boolean isEnabled() {
        return mDelegate.isEnabled();
    }


    // ----------------------- Listeners Declaration -------------------------
    /**
     * The callback used to indicate the user changed the date.
     */
    public interface OnDateChangedListener {
        /**
         * Called upon a date change.
         *
         * @param view The view associated with this listener.
         * @param timeInMills
         */
        void onDateChanged(DatePicker view, long timeInMills);
    }

    // ----------------------- DataSource Definition -------------------------
    private interface OnDataSourceChangeListener {
        void onDataSourceChanged(DatePickerDataSource dataSource);
    }

    public interface DatePickerDataSource {
        void setOnDataSourceChangeListener(OnDataSourceChangeListener onDataSourceChangeListener);

        int monthNum();
        String[] monthStrings();
        NumberPicker.Formatter getMonthFormatter();

        int dayNum();
        String[] dayStrings();
        NumberPicker.Formatter getDayFormatter();

        Calendar getMinDate();
        Calendar getMaxDate();
        boolean isMinDate();
        boolean isMaxDate();

        boolean isMinMonth();
        boolean isMaxMonth();

        boolean isMinYear();
        boolean isMaxYear();

        int getMinLunarDate();
        int getMaxLunarDate();

        int getMinMonth();
        int getMaxMonth();

        void setMinDate(long minDate);
        void setMaxDate(long maxDate);

        long getTimeInMills();
        int getYear();
        int getMonth();
        int getDayOfMonth();

        void updateYear(int newYear);
        void updateMonth(int newMonth);
        void updateDay(int newDay);
        void updateDate(long timeInMills);

        Parcelable onSaveInstanceState(Parcelable superState);
        void onRestoreInstanceState(Parcelable state);
    }

    public abstract static class AbstractDatePickerDataSource implements DatePickerDataSource {
        protected static final String DATE_FORMAT = "yyyy/MM/dd";

//        protected static final int DEFAULT_START_YEAR = 1970;
        protected static final int DEFAULT_START_YEAR = Calendar.getInstance().get(Calendar.YEAR);//当前年份
        protected static final int DEFAULT_START_MONTH = Calendar.getInstance().get(Calendar.MONTH);//当前月份
        protected static final int DEFAULT_START_DAY = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+1;//当前日

        protected static final int DEFAULT_END_YEAR = Calendar.getInstance().get(Calendar.YEAR)+1;//明年年份;

        protected final java.text.DateFormat mDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

        // Callbacks
        protected OnDataSourceChangeListener mOnDataSourceChangeListener;

        protected Context mContext;
        protected Calendar mMinDate;
        protected Calendar mMaxDate;
        protected Calendar mCurrentDate;
        protected Calendar mTmpDate;

        protected NumberPicker.Formatter mDayFormatter = new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.format(Locale.getDefault(), "%d", value);
            }
        };

        protected NumberPicker.Formatter mMonthFormatter = new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.format(Locale.getDefault(), "%d", value + 1);
            }
        };

        public AbstractDatePickerDataSource(Context context, String minDate, String maxDate) {
            mContext = context;
            mTmpDate = Calendar.getInstance();
            mCurrentDate = Calendar.getInstance();
            // set the default minDate to 1970.1.1
            mMinDate = Calendar.getInstance();
//            mMinDate.set(DEFAULT_START_YEAR, Calendar.JANUARY, 1);
            mMinDate.set(DEFAULT_START_YEAR, DEFAULT_START_MONTH, DEFAULT_START_DAY);

            // set the default minDate to 明年 2017.x.x
            mMaxDate = Calendar.getInstance();
            int maxEndYearMonth = monthNumForYear(DEFAULT_END_YEAR) - 1;

            mMaxDate.set(DEFAULT_END_YEAR, maxEndYearMonth, dayNumForYearAndMonth(DEFAULT_END_YEAR, maxEndYearMonth));
            if (minDate != null) {
                try {
                    Date date = mDateFormat.parse(minDate);
                    mMinDate.setTimeInMillis(date.getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (maxDate != null) {
                try {
                    Date date = mDateFormat.parse(maxDate);
                    mMaxDate.setTimeInMillis(date.getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public int monthNum() {
            return monthNumForYear(mCurrentDate.get(Calendar.YEAR));
        }

        public int monthNumForYear(int year) {
            mTmpDate.set(Calendar.YEAR, year);
            return mTmpDate.getActualMaximum(Calendar.MONTH) + 1;
        }

        @Override
        public String[] monthStrings() {
            return monthStringsForYear(mCurrentDate.get(Calendar.YEAR));
        }

        public String[] monthStringsForYear(int year) {
            int monthOfYear = monthNumForYear(year) + 1;
            String[] strings = new String[monthOfYear];
            for (int i = 0; i < monthOfYear; i++) {
                strings[i] = String.format(Locale.getDefault(), "%d", i + 1);
            }
            return strings;
        }

        @Override
        public int dayNum() {
            return dayNumForYearAndMonth(mCurrentDate.get(Calendar.YEAR), mCurrentDate.get(Calendar.MONTH));
        }

        public int dayNumForYearAndMonth(int year, int month) {
            mTmpDate.set(Calendar.YEAR, year);
            mTmpDate.set(Calendar.MONTH, month);
            return mTmpDate.getActualMaximum(Calendar.DAY_OF_MONTH);
        }

        @Override
        public String[] dayStrings() {
            return dayStringsForYearAndMonth(mCurrentDate.get(Calendar.YEAR), mCurrentDate.get(Calendar.MONTH));
        }

        public String[] dayStringsForYearAndMonth(int year, int month) {
            int days = dayNumForYearAndMonth(year, month);
            String[] strings = new String[days];
            for (int i = 0; i < days; i++) {
                strings[i] = String.format(Locale.getDefault(), "%d", i + 1);
            }
            return strings;
        }

        @Override
        public NumberPicker.Formatter getDayFormatter() {
            return mDayFormatter;
        }

        @Override
        public NumberPicker.Formatter getMonthFormatter() {
            return mMonthFormatter;
        }

        @Override
        public void updateDate(long timeInMills) {
            mTmpDate.setTimeInMillis(timeInMills);
            if (mTmpDate.before(mMinDate)) {
                mTmpDate.setTimeInMillis(mMinDate.getTimeInMillis());
            } else if (mTmpDate.after(mMaxDate)) {
                mTmpDate.setTimeInMillis(mMaxDate.getTimeInMillis());
            }
            updateCurrentTime(mTmpDate.getTimeInMillis());
            // send notifier
            onDateChanged();
        }

        @Override
        public Calendar getMinDate() {
            return mMinDate;
        }

        @Override
        public Calendar getMaxDate() {
            return mMaxDate;
        }

        @Override
        public boolean isMaxDate() {
            return CalendarDateUtils.isSameDay(mCurrentDate, mMaxDate);
        }

        @Override
        public boolean isMinDate() {
            return CalendarDateUtils.isSameDay(mCurrentDate, mMinDate);
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
        public void setMaxDate(long maxDate) {
            mTmpDate.setTimeInMillis(maxDate);
            if (CalendarDateUtils.isSameDay(mTmpDate, mMaxDate)) {
                return;
            }
            mMaxDate.setTimeInMillis(maxDate);
            if (mCurrentDate.after(mMaxDate)) {
                updateCurrentTime(mMaxDate.getTimeInMillis());
                onDateChanged();
            }
        }

        @Override
        public void setMinDate(long minDate) {
            mTmpDate.setTimeInMillis(minDate);
            if (CalendarDateUtils.isSameDay(mTmpDate, mMinDate)) {
                return;
            }
            mMinDate.setTimeInMillis(minDate);
            if (mCurrentDate.before(mMinDate)) {
                updateCurrentTime(mMinDate.getTimeInMillis());
                onDateChanged();
            }
        }

        @Override
        public long getTimeInMills() {
            return mCurrentDate.getTimeInMillis();
        }

        @Override
        public int getYear() {
            return mCurrentDate.get(Calendar.YEAR);
        }

        @Override
        public int getMonth() {
            return mCurrentDate.get(Calendar.MONTH);
        }

        @Override
        public int getDayOfMonth() {
            return mCurrentDate.get(Calendar.DAY_OF_MONTH);
        }

        @Override
        public void updateYear(int newYear) {
            mTmpDate.setTimeInMillis(mCurrentDate.getTimeInMillis());
            mTmpDate.set(Calendar.YEAR, newYear);
            updateDate(mTmpDate.getTimeInMillis());
        }

        @Override
        public void updateMonth(int newMonth) {
            mTmpDate.setTimeInMillis(mCurrentDate.getTimeInMillis());
            int maxMonthOfYear = monthNum();
            int oldMonth = mTmpDate.get(Calendar.MONTH);
            if (oldMonth == maxMonthOfYear && newMonth == 0) {
                mTmpDate.add(Calendar.MONTH, 1);
            } else if (oldMonth == 0 && newMonth == maxMonthOfYear) {
                mTmpDate.add(Calendar.MONTH, -1);
            } else {
                mTmpDate.add(Calendar.MONTH, newMonth - oldMonth);
            }
            updateDate(mTmpDate.getTimeInMillis());
        }

        @Override
        public void updateDay(int newDay) {
            mTmpDate.setTimeInMillis(mCurrentDate.getTimeInMillis());
            int maxDayOfMonth = dayNum();
            int oldDay = mTmpDate.get(Calendar.DAY_OF_MONTH);
            if (oldDay == maxDayOfMonth && newDay == 1) {
                mTmpDate.add(Calendar.DAY_OF_MONTH, 1);
            } else if (oldDay == 1 && newDay == maxDayOfMonth) {
                mTmpDate.add(Calendar.DAY_OF_MONTH, -1);
            } else {
                mTmpDate.add(Calendar.DAY_OF_MONTH, newDay - oldDay);
            }
            updateDate(mTmpDate.getTimeInMillis());
        }

        @Override
        public Parcelable onSaveInstanceState(Parcelable superState) {
            return new SavedState(superState, getTimeInMills());
        }

        @Override
        public void onRestoreInstanceState(Parcelable state) {
            SavedState ss = (SavedState) state;
            updateDate(ss.mTimeInMills);
        }

        @Override
        public void setOnDataSourceChangeListener(OnDataSourceChangeListener onDataSourceChangeListener) {
            this.mOnDataSourceChangeListener = onDataSourceChangeListener;
        }

        protected void onDateChanged() {
            if (mOnDataSourceChangeListener != null) {
                mOnDataSourceChangeListener.onDataSourceChanged(this);
            }
        }

        protected void updateCurrentTime(long timeInMills) {
            mCurrentDate.setTimeInMillis(timeInMills);
        }
    }

    /**
     * Class for managing state storing/restoring.
     */
    private static class SavedState extends BaseSavedState {

        private final long mTimeInMills;

        /**
         * Constructor called from {@link DatePicker#onSaveInstanceState()}
         */
        private SavedState(Parcelable superState, long timeInMills) {
            super(superState);
            mTimeInMills = timeInMills;
        }

        /**
         * Constructor called from {@link #CREATOR}
         */
        private SavedState(Parcel in) {
            super(in);
            mTimeInMills = in.readLong();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeLong(mTimeInMills);
        }

        @SuppressWarnings("all")
        // suppress unused and hiding
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {

            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
    // ----------------------- Delegate Definition -------------------------
    /**
     * A delegate interface that defined the public API of the DatePicker. Allows different
     * DatePicker implementations. This would need to be implemented by the DatePicker delegates
     * for the real behavior.
     *
     * @hide
     */
    interface DatePickerDelegate {
        void setOnDateChangedListener(OnDateChangedListener onDateChangedListener);
        void setDataSource(DatePickerDataSource dataSource);

        void setEnabled(boolean enabled);
        boolean isEnabled();
    }

    static class BaseDatePickerDelegate implements DatePickerDelegate, NumberPicker.OnValueChangeListener,
        OnDataSourceChangeListener {

        private static final boolean DEFAULT_ENABLED_STATE = true;
        private static final int DEFAULT_LAYOUT_RESOURCE = R.layout.layout_date_picker;

        // The delegator
        protected DatePicker mDelegator;

        // The context
        protected Context mContext;

        // Callbacks
        OnDateChangedListener mOnDateChangedListener;

        // DataSource
        protected DatePickerDataSource mDataSource;

        private boolean mIsEnabled = DEFAULT_ENABLED_STATE;

        private final NumberPicker mDaySpinner;
        private final NumberPicker mMonthSpinner;
        private final NumberPicker mYearSpinner;

        public BaseDatePickerDelegate(DatePicker delegator, Context context, DatePickerDataSource dataSource,
                                      int layoutResId) {
            mDelegator = delegator;
            mContext = context;

            if (layoutResId <= 0)
                layoutResId = DEFAULT_LAYOUT_RESOURCE;

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(layoutResId, mDelegator, true);

            // day
            mDaySpinner = (NumberPicker) mDelegator.findViewById(R.id.day);
            mDaySpinner.setOnLongPressUpdateInterval(100);
            mDaySpinner.setOnValueChangedListener(this);

            // month
            mMonthSpinner = (NumberPicker) mDelegator.findViewById(R.id.month);
            mMonthSpinner.setOnLongPressUpdateInterval(200);
            mMonthSpinner.setOnValueChangedListener(this);

            // year
            mYearSpinner = (NumberPicker) mDelegator.findViewById(R.id.year);
            mYearSpinner.setOnLongPressUpdateInterval(100);
            mYearSpinner.setOnValueChangedListener(this);

            setDataSource(dataSource);
        }

        /**
         * 更新数据源
         * @param dataSource
         */
        @Override
        public void setDataSource(DatePickerDataSource dataSource) {
            if (mDataSource != dataSource && dataSource != null) {
                mDataSource = dataSource;
                mDataSource.setOnDataSourceChangeListener(this);
                mDaySpinner.setFormatter(mDataSource.getDayFormatter());
                mMonthSpinner.setFormatter(mDataSource.getMonthFormatter());
                updateSpinners();
            }
        }

        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            if (picker == mDaySpinner) {
                mDataSource.updateDay(newVal);
            } else if (picker == mMonthSpinner) {
                mDataSource.updateMonth(newVal);
            } else if (picker == mYearSpinner) {
                mDataSource.updateYear(newVal);
            }
        }

        @Override
        public void onDataSourceChanged(DatePickerDataSource dataSource) {
            updateSpinners();
            if (mOnDateChangedListener != null) {
                mOnDateChangedListener.onDateChanged(mDelegator, mDataSource.getTimeInMills());
            }
        }

        private void updateSpinners() {
            if(mDataSource instanceof CommonDatePickerDataSource){
                updateSolarSpinners();
            }else{
                updateLunarSpinners();
            }
            // year spinner range does not change based on the current date
            mYearSpinner.setMinValue(mDataSource.getMinDate().get(Calendar.YEAR));
            mYearSpinner.setMaxValue(mDataSource.getMaxDate().get(Calendar.YEAR));

            mDaySpinner.setWrapSelectorWheel(false);
            mMonthSpinner.setWrapSelectorWheel(false);
            mYearSpinner.setWrapSelectorWheel(false);
            // set the spinner values
            mYearSpinner.setValue(mDataSource.getYear());
            mMonthSpinner.setValue(mDataSource.getMonth());
            mDaySpinner.setValue(mDataSource.getDayOfMonth());
        }


        private void updateSolarSpinners(){
            // set the spinner ranges respecting the min and max dates
            if(mDataSource.isMinYear()){
                mMonthSpinner.setMinValue(mDataSource.getMinMonth());//最小月份
                mMonthSpinner.setMaxValue(mDataSource.monthNum()-1);
                mMonthSpinner.setWrapSelectorWheel(false);
            }else{
                mMonthSpinner.setMinValue(0);
                mMonthSpinner.setMaxValue(mDataSource.getMaxMonth());//最大月份
                mMonthSpinner.setWrapSelectorWheel(false);
            }
            if (mDataSource.isMinMonth()) {//最小的月
                mDaySpinner.setMinValue(mDataSource.getMinDate().get(Calendar.DAY_OF_MONTH));
                mDaySpinner.setMaxValue(mDataSource.dayNum());
                mDaySpinner.setWrapSelectorWheel(false);
            } else if (mDataSource.isMaxMonth()) {//最大月
                mDaySpinner.setMinValue(1);
                mDaySpinner.setMaxValue(mDataSource.getMaxDate().get(Calendar.DAY_OF_MONTH));
                mDaySpinner.setWrapSelectorWheel(false);
            } else {
                mDaySpinner.setMinValue(1);
                mDaySpinner.setMaxValue(mDataSource.dayNum());
                mDaySpinner.setWrapSelectorWheel(false);
            }
        }

        private void updateLunarSpinners(){
            if(mDataSource.isMinYear()){
                mMonthSpinner.setMinValue(mDataSource.getMinMonth());//最小月份
                mMonthSpinner.setMaxValue(mDataSource.monthNum());
                mMonthSpinner.setWrapSelectorWheel(false);
            }else{
                mMonthSpinner.setMinValue(1);
                mMonthSpinner.setMaxValue(mDataSource.getMaxMonth());//最大月份
                mMonthSpinner.setWrapSelectorWheel(false);
            }
            if (mDataSource.isMinMonth()) {//最小的月
                mDaySpinner.setMinValue(mDataSource.getMinLunarDate());
                mDaySpinner.setMaxValue(mDataSource.dayNum());
                mDaySpinner.setWrapSelectorWheel(false);
            } else if (mDataSource.isMaxMonth()) {//最大月
                mDaySpinner.setMinValue(1);
                mDaySpinner.setMaxValue(mDataSource.getMaxLunarDate());
                mDaySpinner.setWrapSelectorWheel(false);
            } else {
                mDaySpinner.setMinValue(1);
                mDaySpinner.setMaxValue(mDataSource.dayNum());
                mDaySpinner.setWrapSelectorWheel(false);
            }
        }
        @Override
        public void setOnDateChangedListener(OnDateChangedListener onDateChangedListener) {
            this.mOnDateChangedListener = onDateChangedListener;
        }

        @Override
        public void setEnabled(boolean enabled) {
            mDaySpinner.setEnabled(enabled);
            mMonthSpinner.setEnabled(enabled);
            mYearSpinner.setEnabled(enabled);
            mIsEnabled = enabled;
        }

        @Override
        public boolean isEnabled() {
            return mIsEnabled;
        }
    }
}
