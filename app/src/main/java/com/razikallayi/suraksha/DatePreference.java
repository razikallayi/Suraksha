package com.razikallayi.suraksha;

/**
 * Created by Razi Kallayi on 24-02-2017 10:06.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;

import com.razikallayi.suraksha.utils.CalendarUtils;

import java.util.Calendar;

/**
 * Created by Razi Kallayi on 24-02-2017 10:03.
 */

public class DatePreference extends DialogPreference {
    public static final String defaultLoanIssueDate = "1464739200000"; //1st June 2016
    private String dateval;
    private CharSequence mSummary;
    private DatePicker picker = null;

    public DatePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static String dateToString(Calendar calendar) {
        return CalendarUtils.formatDate(calendar.getTimeInMillis());
    }

    public static Calendar defaultCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(Long.valueOf(defaultLoanIssueDate));
        return cal;
    }

    public static java.lang.String defaultCalendarString() {
        return CalendarUtils.formatDate(Long.valueOf(defaultLoanIssueDate));
    }

    @Override
    protected View onCreateDialogView() {
        picker = new DatePicker(getContext());
        picker.setMinDate(Long.valueOf(defaultLoanIssueDate));
        // setCalendarViewShown(false) attribute is only available from API level 11
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            picker.setCalendarViewShown(false);
        }

        return (picker);
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(Long.valueOf(getPersistedString(defaultLoanIssueDate)));
        picker.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            Calendar cal = CalendarUtils.normalizeDate(picker.getYear(), picker.getMonth(), picker.getDayOfMonth());
            String pickedDate = String.valueOf(cal.getTimeInMillis());

            if (callChangeListener(pickedDate)) {
                persistString(pickedDate);
                setSummary(CalendarUtils.formatDate(cal.getTimeInMillis()));
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return (a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {

        if (restoreValue) {
            if (defaultValue == null) {
                dateval = getPersistedString(defaultLoanIssueDate);
            } else {
                dateval = getPersistedString(defaultValue.toString());
            }
            setDate(dateval);
        } else {
            final boolean wasNull = dateval == null;
            setDate((String) defaultValue);
            if (!wasNull) {
                persistString(dateval);
            }
        }
    }

    public String getDate() {
        return dateval;
    }

    /**
     * @param date long date passed as string
     */
    public void setDate(String date) {
        final boolean wasBlocking = shouldDisableDependents();
        dateval = date;
        persistString(date);
        final boolean isBlocking = shouldDisableDependents();
        if (isBlocking != wasBlocking) {
            notifyDependencyChange(isBlocking);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        if (isPersistent()) {
            return super.onSaveInstanceState();
        } else {
            return new SavedState(super.onSaveInstanceState());
        }
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !state.getClass().equals(SavedState.class)) {
            super.onRestoreInstanceState(state);
            try {
                if (state != null) {
                    setDate(((SavedState) state).dateValue);
                }
            } catch (ClassCastException e) {

            }
        } else {
            SavedState s = (SavedState) state;
            super.onRestoreInstanceState(s.getSuperState());
            setDate(s.dateValue);
        }
    }

    public CharSequence getSummary() {
        return mSummary;
    }

    public void setSummary(CharSequence summary) {
        if (summary == null && mSummary != null || summary != null
                && !summary.equals(mSummary)) {
            mSummary = summary;
            notifyChanged();
        }
    }

    private static class SavedState extends Preference.BaseSavedState {

        @SuppressWarnings("unused")
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(final Parcel parcel) {
                return new SavedState(parcel);
            }

            @Override
            public SavedState[] newArray(final int size) {
                return new SavedState[size];
            }
        };
        private transient String dateValue;

        public SavedState(final Parcel p) {
            super(p);
            dateValue = p.readString();
        }

        public SavedState(final Parcelable p) {
            super(p);
        }

        @Override
        public void writeToParcel(final Parcel parcel, final int flags) {
            super.writeToParcel(parcel, flags);
            parcel.writeString(dateValue);
        }

    }
}