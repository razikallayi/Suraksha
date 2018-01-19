package com.razikallayi.suraksha.report;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.utils.CalendarUtils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PeriodicalTxnReportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PeriodicalTxnReportFragment extends Fragment {

    public static final String ARG_REPORT_TYPE = "report_type";
    public static final String ARG_DATE = "date";

    private int mReportType = Calendar.DATE;

    private Calendar mDate = new GregorianCalendar();
    private RecyclerView mRecyclerView;
    private TextView mDateView;
    private TextView prev;
    private TextView next;

    public PeriodicalTxnReportFragment() {
        // Required empty public constructor
    }

    public static PeriodicalTxnReportFragment newInstance(long date, int type) {
        PeriodicalTxnReportFragment fragment = new PeriodicalTxnReportFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_DATE, date);
        args.putInt(ARG_REPORT_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mDate.setTimeInMillis(CalendarUtils.normalizeDate(getArguments().getLong(ARG_DATE)));
            mReportType = getArguments().getInt(ARG_REPORT_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_daily_transaction, container, false);

        prev = rootView.findViewById(R.id.prev);
        mDateView = rootView.findViewById(R.id.date);
        next = rootView.findViewById(R.id.next);
        mRecyclerView = rootView.findViewById(R.id.dailyTxnList);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));

        mDateView.setText(CalendarUtils.formatDate(mDate.getTimeInMillis()));
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findDate(false);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findDate(true);
            }
        });
        mDateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        dateSetListener,
                        mDate.get(Calendar.YEAR),
                        mDate.get(Calendar.MONTH),
                        mDate.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getActivity().getFragmentManager(), "Pick Date");
            }
        });


        loadAdapter(mDate);
        return rootView;
    }

    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
            Calendar calendar = new GregorianCalendar();
            calendar.set(year, monthOfYear, dayOfMonth);
            mDate.setTimeInMillis(CalendarUtils.normalizeDate(calendar.getTimeInMillis()));
            loadAdapter(mDate);
        }
    };

    private void findDate(boolean isNext) {
        FindNextDateTask findNextDateTask = new FindNextDateTask(getContext(), mReportType, isNext);
        findNextDateTask.execute(mDate.getTimeInMillis());
    }


    private void loadAdapter(Calendar date) {
        if (mReportType == Calendar.YEAR) {
            mDateView.setText(String.valueOf(date.get(Calendar.YEAR)));
        } else if (mReportType == Calendar.MONTH) {
            mDateView.setText(CalendarUtils.readableDepositMonth(date));
        } else {
            StringBuilder title = new StringBuilder();
            title.append(CalendarUtils.formatDate(date.getTimeInMillis()))
                    .append(" ")
                    .append(date.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()));
            mDateView.setText(title.toString().toUpperCase());
        }

        mDate = date;
        LoadDataTask loadDataTask = new LoadDataTask(mRecyclerView, mReportType);
        loadDataTask.execute(date.getTimeInMillis());
    }

    private class FindNextDateTask extends AsyncTask<Long, Integer, Long> {
        private final Context context;
        private final boolean isNext;
        private final int reportType;

        public FindNextDateTask(Context context, int reportType, boolean isNext) {
            this.context = context;
            this.reportType = reportType;
            this.isNext = isNext;
        }

        @Override
        protected Long doInBackground(Long... date) {
            return DailyTxn.getNextTxn(context, date[0], reportType, isNext);
        }

        @Override
        protected void onPostExecute(Long date) {
            if (date > 0) {
                Calendar cal = new GregorianCalendar();
                cal.setTimeInMillis(date);
                loadAdapter(cal);
            }
        }
    }

    private class LoadDataTask extends AsyncTask<Long, Void, List<DailyItem>> {
        private RecyclerView rv;
        private int reportType;

        public LoadDataTask(RecyclerView rv, int reportType) {
            this.rv = rv;
            this.reportType = reportType;
        }

        @Override
        protected List<DailyItem> doInBackground(Long... date) {
            switch (reportType) {
                case Calendar.YEAR:
                    return DailyTxn.getTxnReportOfYear(rv.getContext(), date[0]);
                case Calendar.MONTH:
                    return DailyTxn.getTxnReportOfMonth(rv.getContext(), date[0]);
                default:
                    return DailyTxn.getTxnReportOfDate(rv.getContext(), date[0]);
            }
        }

        @Override
        protected void onPostExecute(List<DailyItem> dailyItems) {
            super.onPostExecute(dailyItems);
            if (dailyItems != null) {
                rv.setVisibility(View.VISIBLE);
                rv.setAdapter(new PeriodicalTransactionAdapter(dailyItems));
            } else {
                rv.setAdapter(null);
                rv.setVisibility(View.GONE);
            }
        }
    }
}
