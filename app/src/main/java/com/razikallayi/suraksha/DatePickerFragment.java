package com.razikallayi.suraksha;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

import static com.razikallayi.suraksha.DatePreference.defaultLoanIssueDate;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {
    private OnDateSetListener onDateSetListener;
    private long minDate = Long.valueOf(defaultLoanIssueDate);
    private long maxDate = System.currentTimeMillis();

    public static DatePickerFragment newInstance(Calendar c) {
        Bundle args = new Bundle();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        args.putInt("YEAR", year);
        args.putInt("MONTH", month);
        args.putInt("DAY", day);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        if (!getArguments().isEmpty()) {
            year = getArguments().getInt("YEAR");
            month = getArguments().getInt("MONTH");
            day = getArguments().getInt("DAY");
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(minDate);
        datePickerDialog.getDatePicker().setMaxDate(maxDate);
        return  datePickerDialog;
    }

    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        if (onDateSetListener != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            onDateSetListener.getDate(calendar);
        }
    }

    public long getMinDate() {
        return minDate;
    }

    public void setMinDate(long minDate) {
        this.minDate = minDate;
    }

    public long getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(long maxDate) {
        this.maxDate = maxDate;
    }

    public void setOnDateSetListener(OnDateSetListener listener) {
        onDateSetListener = listener;
    }

    // The callback interface
    public interface OnDateSetListener {
        void getDate(Calendar calendar);
    }
}