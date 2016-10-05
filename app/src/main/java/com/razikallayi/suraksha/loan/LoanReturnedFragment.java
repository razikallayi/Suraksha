package com.razikallayi.suraksha.loan;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.data.SurakshaContract;
import com.razikallayi.suraksha.txn.Transaction;

public class LoanReturnedFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String TAG = LoanReturnedFragment.class.getSimpleName();
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_LOAN_ISSUE_ID = "loan_issue_id";
    private static final int LOAN_RETURN_LOADER = 0x123;
    private long loanIssueId;
    private LoanReturnedRecyclerViewAdapter loanReturnedAdapter;

    public LoanReturnedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        loanIssueId = getArguments().getLong(ARG_LOAN_ISSUE_ID, -1);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.loan_return_fragment, container, false);
        RecyclerView loanReturnedRecyclerView = (RecyclerView) rootView.findViewById(R.id.loan_return_list);
//        loanReturnedRecyclerView.setHasFixedSize(true);

//        // use a linear layout manager
//        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
//        loanReturnedRecyclerView.setLayoutManager(layoutManager);

        // use a linear layout manager
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 4);
        loanReturnedRecyclerView.setLayoutManager(layoutManager);

        // specify an adapter
        loanReturnedAdapter = new LoanReturnedRecyclerViewAdapter();
        loanReturnedRecyclerView.setAdapter(loanReturnedAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOAN_RETURN_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOAN_RETURN_LOADER:
                return new CursorLoader(getContext(), SurakshaContract.TxnEntry.CONTENT_URI,
                        Transaction.TxnQuery.PROJECTION,
                        SurakshaContract.TxnEntry.TABLE_NAME + "."
                                + SurakshaContract.TxnEntry.COLUMN_FK_LOAN_PAYED_ID + " = ? and "
                                + SurakshaContract.TxnEntry.COLUMN_LEDGER + " = ? and "
                                + SurakshaContract.TxnEntry.COLUMN_VOUCHER_TYPE + " = ? ",
                        new String[]{String.valueOf(loanIssueId),
                                String.valueOf(SurakshaContract.TxnEntry.LOAN_RETURN_LEDGER),
                                String.valueOf(SurakshaContract.TxnEntry.RECEIPT_VOUCHER)
                        },
                        SurakshaContract.TxnEntry.TABLE_NAME + "."
                                + SurakshaContract.TxnEntry._ID + " desc");
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        switch (loader.getId()) {
            case LOAN_RETURN_LOADER:
                loanReturnedAdapter.swapCursor(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        loanReturnedAdapter.swapCursor(null);
    }
}
