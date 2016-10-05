package com.razikallayi.suraksha.loan;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.data.SurakshaContract;

public class LoanIssuedFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String TAG = LoanIssuedFragment.class.getSimpleName();
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ACCOUNT_NUMBER = "account_number";
    private static final int LOAN_ISSUED_LOADER = 0x123;
    private int accountNumber;
    private LoanIssuedRecyclerViewAdapter mLoanIssueAdapter;

    public LoanIssuedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        accountNumber = getArguments().getInt(ARG_ACCOUNT_NUMBER, -1);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.loan_issued_fragment, container, false);
        RecyclerView loanIssuedRecyclerView = (RecyclerView) rootView.findViewById(R.id.loan_issued_list);
        Log.d("FISH", "onCreateView: in fragment");
//        loanIssuedRecyclerView.setHasFixedSize(true);

//        // use a linear layout manager
//        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
//        loanIssuedRecyclerView.setLayoutManager(layoutManager);

//
//        // use a linear layout manager
//        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 4);
//        loanIssuedRecyclerView.setLayoutManager(layoutManager);

        // specify an adapter
        mLoanIssueAdapter = new LoanIssuedRecyclerViewAdapter();
        mLoanIssueAdapter.setFragmentManager(getActivity().getSupportFragmentManager());
        loanIssuedRecyclerView.setAdapter(mLoanIssueAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOAN_ISSUED_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOAN_ISSUED_LOADER:
                return new CursorLoader(getContext(), SurakshaContract.LoanIssueEntry.CONTENT_URI,
                        LoanIssue.LoanIssueQuery.PROJECTION,
                        SurakshaContract.LoanIssueEntry.TABLE_NAME + "."
                                + SurakshaContract.LoanIssueEntry.COLUMN_FK_ACCOUNT_NUMBER + " = ? and "
                                + SurakshaContract.TxnEntry.COLUMN_LEDGER + " = ? and "
                                + SurakshaContract.TxnEntry.COLUMN_VOUCHER_TYPE + " = ? ",
                        new String[]{String.valueOf(accountNumber),
                                String.valueOf(SurakshaContract.TxnEntry.LOAN_PAYED_LEDGER),
                                String.valueOf(SurakshaContract.TxnEntry.PAYMENT_VOUCHER)
                        },
                        SurakshaContract.LoanIssueEntry.TABLE_NAME + "."
                                + SurakshaContract.LoanIssueEntry._ID + " desc");
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

    public interface OnItemClickListener {
        void onItemClick(View view, long loanIssueId);
    }

}
