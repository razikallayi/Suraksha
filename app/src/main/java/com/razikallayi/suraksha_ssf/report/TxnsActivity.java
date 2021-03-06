package com.razikallayi.suraksha_ssf.report;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.razikallayi.suraksha_ssf.BaseActivity;
import com.razikallayi.suraksha_ssf.R;
import com.razikallayi.suraksha_ssf.data.SurakshaContract;
import com.razikallayi.suraksha_ssf.loan.LoanIssue;
import com.razikallayi.suraksha_ssf.member.Member;
import com.razikallayi.suraksha_ssf.officer.Officer;
import com.razikallayi.suraksha_ssf.txn.Transaction;
import com.razikallayi.suraksha_ssf.utils.CalendarUtils;
import com.razikallayi.suraksha_ssf.utils.Utility;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class TxnsActivity extends BaseActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        DatePickerDialog.OnDateSetListener {

    public static final int TXN_LOADER = 0X500;
    public LinkedHashMap<Long, List<Transaction>> mTransactionsMap;
    public List<ListItem> mItems;
    public TxnAdapter mTxnAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_txn_report);

        //Setup the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
/*
        FloatingActionButton fab = findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Calendar now = Calendar.getInstance();
                    DatePickerDialog dpd = DatePickerDialog.newInstance(
                            TxnsActivity.this,
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH)
                    );
                    dpd.show(getFragmentManager(), "Pick Date");
                }
            });
        }
*/
        getSupportLoaderManager().initLoader(TXN_LOADER, null, this);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
//        String time = "You picked the following time: "+hourOfDay+"h"+minute;
//        timeTextView.setText(time);
        String date = "You picked the following date: " + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;

        Toast.makeText(this, date, Toast.LENGTH_SHORT).show();
//        TextView dateTextView = (TextView) findViewById(R.id.txnDate);
//        dateTextView.setText(date);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        String selection = ""
//        String[] selectionArgs = args.getStringArray("selectionArgs");
//        String sortOrder;
        return new CursorLoader(getApplicationContext(),
                SurakshaContract.TxnEntry.CONTENT_URI,
                Transaction.TxnQuery.PROJECTION, null, null,
                SurakshaContract.TxnEntry.COLUMN_PAYMENT_DATE + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        mTransactionsMap = new LinkedHashMap<Long, List<Transaction>>();
        long dateKey;
        if (c.getCount() <= 0) {
            return;
        }
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

            //Convert cursor to transaction
//            Transaction txn = new Transaction(getApplicationContext(), c.getInt(Transaction.TxnQuery.COL_FK_ACCOUNT_NUMBER),
//                    c.getDouble(Transaction.TxnQuery.COL_AMOUNT), c.getInt(Transaction.TxnQuery.COL_VOUCHER_TYPE),
//                    c.getInt(Transaction.TxnQuery.COL_LEDGER), c.getString(Transaction.TxnQuery.COL_NARRATION),
//                    c.getLong(Transaction.TxnQuery.COL_FK_OFFICER_ID));
//            txn.setId(c.getLong(Transaction.TxnQuery.COL_ID));
//            txn.setDepositForDate(c.getLong(Transaction.TxnQuery.COL_DEFINED_DEPOSIT_DATE));
//            txn.setLoanPayedId(c.getInt(Transaction.TxnQuery.COL_FK_LOAN_PAYED_ID));
//            txn.setOfficer_id(c.getInt(Transaction.TxnQuery.COL_FK_OFFICER_ID));
//            txn.setCreatedAt(c.getLong(Transaction.TxnQuery.COL_CREATED_AT));
//            txn.setUpdatedAt(c.getLong(Transaction.TxnQuery.COL_UPDATED_AT));
            Transaction txn = Transaction.getTxnFromCursor(this, c);
            //Fetch the created Date
            dateKey = CalendarUtils.normalizeDate(c.getLong(Transaction.TxnQuery.COL_CREATED_AT));
            //Create a date key if not exist. Else add txn to the list
            Log.d("FISH", "onLoadFinished: "+dateKey);
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

        RecyclerView transactionRecyclerView = findViewById(R.id.txnRecyclerView);
        mTxnAdapter = new TxnAdapter(getLayoutInflater(), mItems);
        transactionRecyclerView.setAdapter(mTxnAdapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public abstract class ListItem {

        public static final int TYPE_HEADER = 1;
        public static final int TYPE_TXN = 2;

        abstract public int getType();
    }

    public class HeaderItem extends ListItem {
        private long date;

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

    public class TxnAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<ListItem> mItems;
        private LayoutInflater mLayoutInflater;

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
                final View itemView = mLayoutInflater.inflate(R.layout.txn_list_item, parent, false);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
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
                viewHolder.mAccountNo.setText(String.valueOf(txnItem.txn.getAccountNumber()));
                viewHolder.mMemberName.setText(Member.getMemberFromAccountNumber(getApplicationContext(), txnItem.txn.getAccountNumber()).getName());
                viewHolder.mAmountView.setText(Utility.formatAmountInRupees(getApplicationContext(),
                        txnItem.txn.getAmount()));

                viewHolder.mMonthView.setVisibility(View.GONE);
                if (txnItem.txn.getDefinedDepositMonth() != -1) {
                    viewHolder.mMonthView.setText(CalendarUtils.readableDepositMonth(txnItem.txn.getDefinedDepositMonth()));
                    viewHolder.mMonthView.setVisibility(View.VISIBLE);
                }

                int ledgerId = txnItem.txn.getLedger();
                String ledgerName = Transaction.getLedgerName(ledgerId);
                if (ledgerId == 3) {//LOAN ISSUED
                    LoanIssue loanIssue = LoanIssue.fetchLoanIssue(getApplicationContext(), txnItem.txn.getLoanPayedId());
                    String issuedDate = null;
                    if (loanIssue != null) {
                        issuedDate = CalendarUtils.formatDate(loanIssue.getIssuedAt());
                        viewHolder.mMonthView.setText(issuedDate);
                        viewHolder.mMonthView.setVisibility(View.VISIBLE);
                    }
                }

                viewHolder.mLedger.setText(ledgerName);
                viewHolder.mVoucherName.setText(Transaction.getVoucherName(txnItem.txn.getVoucherType()));
                viewHolder.mCreatedAt.setText(CalendarUtils.formatDateTime(txnItem.txn.getCreatedAt()));
                viewHolder.mCreatedBy.setText(Officer.getOfficerFromId(getApplicationContext(),
                        txnItem.txn.getOfficer_id()).getName());
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

        private class HeaderViewHolder extends RecyclerView.ViewHolder {
            TextView dateTextView;

            public HeaderViewHolder(View itemView) {
                super(itemView);
                dateTextView = itemView.findViewById(R.id.dateHeader);
            }
        }

        private class TxnViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mAccountNo;
            public final TextView mMemberName;
            public final TextView mLedger;
            public final TextView mMonthView;
            public final TextView mCreatedAt;
            public final TextView mCreatedBy;
            public final TextView mAmountView;
            public final TextView mVoucherName;

            public TxnViewHolder(View itemView) {
                super(itemView);
                mView = itemView;
                mAccountNo = itemView.findViewById(R.id.txnAccountNo);
                mMemberName = itemView.findViewById(R.id.txnMemberName);
                mLedger = itemView.findViewById(R.id.txnLedger);
                mMonthView = itemView.findViewById(R.id.txnMonth);
                mCreatedAt = itemView.findViewById(R.id.txnCreatedAt);
                mCreatedBy = itemView.findViewById(R.id.txnCreatedBy);
                mAmountView = itemView.findViewById(R.id.txnAmount);
                mVoucherName = itemView.findViewById(R.id.txnVoucherName);
            }
        }
    }
}
