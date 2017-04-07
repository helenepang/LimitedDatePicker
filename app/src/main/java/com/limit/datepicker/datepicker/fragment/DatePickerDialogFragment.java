package com.limit.datepicker.datepicker.fragment;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.limit.datepicker.R;
import com.limit.datepicker.datepicker.DatePicker;
import com.limit.datepicker.datepicker.DatePickerDataSourceFactory;

import java.util.Calendar;

/**
 * Created by helene on 17/4/7.
 */
public class DatePickerDialogFragment extends DialogFragment implements View.OnClickListener,
        RadioGroup.OnCheckedChangeListener {

    private DatePicker mDatePicker;
    private TextView mCancel;
    private TextView mSaveButton;
    private RadioGroup mSwitcher;
    private DatePickerListener mDatePickerListener;
    private int mIsLunar;

    private int mMode = DatePickerDataSourceFactory.MODE_COMMON;
    private Calendar mCalendar = Calendar.getInstance();
    private long  mTempTimeinMillion;  //仅仅用于初始化的时间记录,点击取消,恢复成初始时间

    public interface DatePickerListener {
        void onPickDate(Calendar calendar, int isLunar);
    }

    public void setDatePickerListener(DatePickerListener mListener) {
        this.mDatePickerListener = mListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                               Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(true);
        Window window = getDialog().getWindow();
        window.setLayout(getScreenWidth(getActivity()), ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setGravity(Gravity.BOTTOM);
        // Press back button to save & dismiss
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && getDialog().isShowing()) {
                    dismiss();
                    return true;
                }
                return false;
            }
        });
        View view = inflater.inflate(R.layout.fragment_dialog_date_picker, container);
        mCancel = (TextView) view.findViewById(R.id.cancel);
        mSaveButton = (TextView) view.findViewById(R.id.save);
        mDatePicker = (DatePicker) view.findViewById(R.id.date_picker);
        mDatePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, long timeInMills) {
                mCalendar.setTimeInMillis(timeInMills);
            }
        });

        mSwitcher = (RadioGroup) view.findViewById(R.id.calendar_swicher);
        mSwitcher.setOnCheckedChangeListener(this);

        mCancel.setOnClickListener(this);
        setBtnTouchColorFade(mCancel);
        mSaveButton.setOnClickListener(this);
        setBtnTouchColorFade(mSaveButton);
        return view;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (group == mSwitcher) {
            if (checkedId == R.id.common_calendar_switcher) {
                mIsLunar = 0;//阳历
                setMode(DatePickerDataSourceFactory.MODE_COMMON);

            } else if (checkedId == R.id.lunar_calendar_switcher) {
                mIsLunar = 1;//是农历
                setMode(DatePickerDataSourceFactory.MODE_LUNAR);

            } else {
            }
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.save) {
            save();

        } else if (i == R.id.cancel) {
            dismiss();

        } else {
        }
    }

    public void setMode(int mode) {
        mMode = mode;
        if (mDatePicker != null) {
            mDatePicker.setMode(getActivity(), mode);
        }
    }

    private void resetMode(){
        if(mIsLunar == 0){
            mMode = DatePickerDataSourceFactory.MODE_COMMON;
        }else{
            mMode = DatePickerDataSourceFactory.MODE_LUNAR;
        }
        if (mDatePicker != null) {
            mDatePicker.resetMode(getActivity(), mMode);
        }
    }
    public void setTime(long time, int isLunar) {
        this.mIsLunar = isLunar;
        mTempTimeinMillion = time;
        mCalendar.setTimeInMillis(time);
    }

    public void save() {
        if (mDatePickerListener != null) {
            saveCalendarFromDatePicker();
            mTempTimeinMillion = mCalendar.getTimeInMillis();
            mDatePickerListener.onPickDate(mCalendar,mIsLunar);
        }
        dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();
        resetMode();
        updateDatePickerFromCalendar();
        ((RadioButton) mSwitcher.findViewById(mMode == DatePickerDataSourceFactory.MODE_LUNAR ?
                R.id.lunar_calendar_switcher : R.id.common_calendar_switcher))
                .setChecked(true);
     }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mCalendar.setTimeInMillis(mTempTimeinMillion);
        updateDatePickerFromCalendar();
    }

    /**点击外侧隐藏dialog执行的方法*/
    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        dismiss();
    }

    @Override
    public void onPause() {
        super.onPause();
        saveCalendarFromDatePicker();
    }

    private void updateDatePickerFromCalendar() {
        mDatePicker.updateDate(mCalendar.getTimeInMillis());
    }

    private void saveCalendarFromDatePicker() {
        mCalendar.setTimeInMillis(mDatePicker.getTimeInMills());
    }

    public static int getScreenWidth(Context context) {
        Point size = new Point();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(size);
        return size.x;
    }

    public static void setBtnTouchColorFade(final View view) {
        if (view == null)
            return;
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    view.setAlpha((float) 0.7);
                } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                    view.setAlpha(1);
                }
                return false;
            }
        });
    }
}
