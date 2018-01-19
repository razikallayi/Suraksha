package com.razikallayi.suraksha.loan;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.RelativeLayout;

import com.razikallayi.suraksha.BaseActivity;
import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.data.SurakshaContract;
import com.razikallayi.suraksha.member.Member;

public class LoanIssuedListActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String ARG_ACCOUNT_NUMBER = "account_number";
    private static final int LOAN_ISSUED_LOADER = 0x123;
    private int accountNumber;
    private LoanIssuedAdapter mLoanIssueAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.loan_issued_activity);
        //Setup the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        accountNumber = getIntent().getIntExtra(ARG_ACCOUNT_NUMBER, -1);
        RecyclerView loanIssuedRecyclerView = findViewById(R.id.loan_issued_list);
        mLoanIssueAdapter = new LoanIssuedAdapter();
        RelativeLayout layoutMemberInfo = findViewById(R.id.layoutMemberInfo);
        Member member = Member.getMemberFromAccountNumber(this, accountNumber);
        member.setMemberInfo(this, layoutMemberInfo);
        loanIssuedRecyclerView.setAdapter(mLoanIssueAdapter);

        getSupportLoaderManager().initLoader(LOAN_ISSUED_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOAN_ISSUED_LOADER:
                return new CursorLoader(getApplicationContext(), SurakshaContract.LoanIssueEntry.CONTENT_URI,
                        LoanIssue.LoanIssueQuery.PROJECTION,
                        SurakshaContract.LoanIssueEntry.TABLE_NAME + "."
                                + SurakshaContract.LoanIssueEntry.COLUMN_ACCOUNT_NUMBER + " = ? and "
                                + SurakshaContract.TxnEntry.COLUMN_LEDGER + " = ? and "
                                + SurakshaContract.TxnEntry.COLUMN_VOUCHER_TYPE + " = ? ",
                        new String[]{String.valueOf(accountNumber),
                                String.valueOf(SurakshaContract.TxnEntry.LOAN_ISSUED_LEDGER),
                                String.valueOf(SurakshaContract.TxnEntry.PAYMENT_VOUCHER)
                        },
                        SurakshaContract.LoanIssueEntry.TABLE_NAME + "."
                                + SurakshaContract.LoanIssueEntry.COLUMN_ISSUED_AT + " desc");
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        switch (loader.getId()) {
            case LOAN_ISSUED_LOADER:
                mLoanIssueAdapter.swapCursor(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mLoanIssueAdapter.swapCursor(null);
    }
}
