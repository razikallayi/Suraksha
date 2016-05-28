package com.razikallayi.suraksha.txn;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.razikallayi.suraksha.BaseActivity;
import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.data.SurakshaContract;
import com.razikallayi.suraksha.utils.CalendarUtils;
import com.razikallayi.suraksha.utils.Utility;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;

public class TxnReportActivity extends BaseActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        DatePickerDialog.OnDateSetListener {

    public LinkedHashMap<Long, List<Transaction>> mTransactionsMap;
    public List<ListItem> mItems;

    public abstract class ListItem {

        public static final int TYPE_HEADER = 1;
        public static final int TYPE_TXN = 2;

        abstract public int getType();
    }


    public class HeaderItem extends ListItem {
        private long date;
        // here getters and setters
        // for title and so on, built
        // using date

        @Override
        public int getType() {
            return TYPE_HEADER;
        }
    }

    public class TxnItem extends ListItem {
        private Transaction txn;

        // here getters and setters
        // for title and so on, built
        // using date

        @Override
        public int getType() {
            return TYPE_TXN;
        }
    }


    public static final int TXN_LOADER = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_txn_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Calendar now = Calendar.getInstance();
                    DatePickerDialog dpd = DatePickerDialog.newInstance(
                            TxnReportActivity.this,
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH)
                    );
                    dpd.show(getFragmentManager(), "Pick Date");
                }
            });
        }


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportLoaderManager().initLoader(TXN_LOADER, null, this);
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
//        String time = "You picked the following time: "+hourOfDay+"h"+minute;
//        timeTextView.setText(time);
        String date = "You picked the following date: " + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        TextView dateTextView = (TextView) findViewById(R.id.txnDate);
        dateTextView.setText(date);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        String selection = ""
//        String[] selectionArgs = args.getStringArray("selectionArgs");
//        String sortOrder;
        return new CursorLoader(getApplicationContext(),
                SurakshaContract.TxnEntry.CONTENT_URI,
                Transaction.TxnQuery.PROJECTION, null, null, SurakshaContract.TxnEntry.COLUMN_CREATED_AT + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        mTransactionsMap = new LinkedHashMap <Long, List<Transaction>>();
        long dateKey;
        if (c.getCount() <= 0) {
            return;
        }
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

            //Convert cursor to transaction
            Transaction txn = new Transaction(getApplicationContext(), c.getInt(Transaction.TxnQuery.COL_FK_ACCOUNT_NUMBER),
                    c.getDouble(Transaction.TxnQuery.COL_AMOUNT), c.getInt(Transaction.TxnQuery.COL_VOUCHER_TYPE),
                    c.getInt(Transaction.TxnQuery.COL_LEDGER), c.getString(Transaction.TxnQuery.COL_NARRATION),
                    c.getLong(Transaction.TxnQuery.COL_FK_OFFICER_ID));
            txn.setId(c.getLong(Transaction.TxnQuery.COL_ID));
            txn.setDefinedDepositDate(c.getLong(Transaction.TxnQuery.COL_DEFINED_DEPOSIT_DATE));
            txn.setLoanPayedId(c.getInt(Transaction.TxnQuery.COL_FK_LOAN_PAYED_ID));
            txn.setCreatedAt(c.getLong(Transaction.TxnQuery.COL_CREATED_AT));
            txn.setUpdatedAt(c.getLong(Transaction.TxnQuery.COL_UPDATED_AT));

            //Fetch the created Date
            dateKey = CalendarUtils.normalizeDate(c.getLong(Transaction.TxnQuery.COL_CREATED_AT));

            //Create a date key if not exist. Else add txn to the list
            if (mTransactionsMap.containsKey(dateKey)) {
                mTransactionsMap.get(dateKey).add(txn);
            } else {
                mTransactionsMap.put(dateKey, new ArrayList<Transaction>());
                mTransactionsMap.get(dateKey).add(txn);
            }
        }

        //Iterate through the linkedHashMap and set the Header
        mItems = new ArrayList<>();
        for (long date : mTransactionsMap.keySet()) {
            HeaderItem dateHeader = new HeaderItem();
            dateHeader.date = date;
            mItems.add(dateHeader);
            for (Transaction transaction : mTransactionsMap.get(date)) {
                TxnItem item = new TxnItem();
                item.txn = transaction;
                mItems.add(item);
            }
        }


        RecyclerView transactionRecyclerView = (RecyclerView) findViewById(R.id.txnRecyclerView);
        TxnAdapter adapter = new TxnAdapter(getLayoutInflater(), mItems);
        transactionRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    public class TxnAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private LayoutInflater mLayoutInflater;
        List<ListItem> mItems;

        public TxnAdapter(LayoutInflater mLayoutInflater, List<ListItem> mItems) {
            this.mLayoutInflater = mLayoutInflater;
            this.mItems = mItems;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == ListItem.TYPE_HEADER) {
                View itemView = mLayoutInflater.inflate(R.layout.txn_list_item_header, parent, false);
                return new HeaderViewHolder(itemView);
            } else {
                View itemView = mLayoutInflater.inflate(R.layout.txn_list_item, parent, false);
                return new TxnViewHolder(itemView);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int type = getItemViewType(position);
            if (type == ListItem.TYPE_HEADER) {
                HeaderItem header = (HeaderItem) mItems.get(position);
                HeaderViewHolder viewHolder = (HeaderViewHolder) holder;
                viewHolder.dateTextView.setText(CalendarUtils.formatDate(header.date));
            } else {
                TxnItem txnItem = (TxnItem) mItems.get(position);
                TxnViewHolder viewHolder = (TxnViewHolder) holder;
                viewHolder.mAmountView.setText(Utility.formatAmountInRupees(getApplicationContext(),
                        txnItem.txn.getAmount()));
                if (txnItem.txn.getDefinedDepositMonth() != -1) {
                    viewHolder.mMonthView.setText(CalendarUtils.readableDepositMonth(txnItem.txn.getDefinedDepositMonth()));
                } else {
                    viewHolder.mMonthView.setVisibility(View.GONE);
                }
                viewHolder.mLedger.setText(Transaction.getLedgerName(txnItem.txn.getLedger()));
                viewHolder.mVoucherName.setText(Transaction.getVoucherName(txnItem.txn.getVoucherType()));
                viewHolder.mCreatedAt.setText(CalendarUtils.formatDate(txnItem.txn.getCreatedAt()));
            }
        }

        private class HeaderViewHolder extends RecyclerView.ViewHolder {
            TextView dateTextView;

            public HeaderViewHolder(View itemView) {
                super(itemView);
                dateTextView = (TextView) itemView.findViewById(R.id.dateHeader);
            }
        }

        private class TxnViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mLedger;
            public final TextView mMonthView;
            public final TextView mCreatedAt;
            public final TextView mAmountView;
            public final TextView mVoucherName;

            public TxnViewHolder(View itemView) {
                super(itemView);
                mView = itemView;
                mLedger = (TextView) itemView.findViewById(R.id.ledger);
                mMonthView = (TextView) itemView.findViewById(R.id.month);
                mCreatedAt = (TextView) itemView.findViewById(R.id.createdAt);
                mAmountView = (TextView) itemView.findViewById(R.id.amount);
                mVoucherName = (TextView) itemView.findViewById(R.id.voucherName);
            }
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        @Override
        public int getItemViewType(int position) {
            return mItems.get(position).getType();
        }
    }
}
