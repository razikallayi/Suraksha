package com.razikallayi.suraksha;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.razikallayi.suraksha.data.SurakshaContract;
import com.razikallayi.suraksha.utils.Utility;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the OnItemClickListener
 * interface.
 */
public class TxnListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    // TODO: Customize parameter argument names
    private static final String ARG_ACCOUNT_NUMBER = "account_number";
    private View rootView;
//    private OnItemClickListener mListener;

//    public static final int TXN_LOADER = 0;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TxnListFragment() {
    }

    public static TxnListFragment newInstance(int accountNumber) {
        TxnListFragment fragment = new TxnListFragment();
        Bundle args = new Bundle();
//        args.putInt(ARG_TXN_COUNT, columnCount);
        args.putInt(ARG_ACCOUNT_NUMBER, accountNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(1,null,this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.txn_list, container, false);

        Context context = rootView.getContext();
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.list);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        int accountNumber = getArguments().getInt(ARG_ACCOUNT_NUMBER);
        Cursor cursorTxn = getContext().getContentResolver().query(
                        SurakshaContract.TxnEntry.buildTxnOfAccountUri(accountNumber),
                        TXN_COLUMNS, null, null, SurakshaContract.TxnEntry._ID+ " desc, "+ SurakshaContract.TxnEntry.COLUMN_CREATED_AT+ " desc");
//        if(cursorTxn.getCount()<=0){
//
//        }
        recyclerView.setAdapter(new TxnRecyclerViewAdapter(setListAdapter(cursorTxn)));
        return rootView;
    }

    private ArrayList<Transaction> setListAdapter(Cursor cursor) {
        ArrayList<Transaction> listTxn = new ArrayList<>();
        while(cursor.moveToNext()) {
            Transaction txn = new Transaction();
            // The Cursor is now set to the right position
            txn.setId(cursor.getLong(COL_ID));
            txn.setAmount(cursor.getDouble(COL_AMOUNT));
            txn.setDefinedDepositDate(cursor.getLong(COL_MONTH_OF_DEPOSIT));
            txn.setVoucherType(cursor.getInt(COL_VOUCHER_TYPE));
            txn.setLedger(cursor.getInt(COL_LEDGER));
            txn.setLoanPayedId(cursor.getInt(COL_FK_LOAN_PAYED_ID));
            txn.setAccountNumber(cursor.getInt(COL_FK_ACCOUNT_NUMBER));
            txn.setNarration(cursor.getString(COL_NARRATION));
            txn.setCreatedAt(cursor.getString(COL_CREATED_AT));
            listTxn.add(txn);
        }
        cursor.close();
        return listTxn;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        int accountNumber = getArguments().getInt("account_number",-1);
        return  new CursorLoader(rootView.getContext(),
                SurakshaContract.TxnEntry.buildFetchAllDepositsUri(),
                Transaction.TXN_COLUMNS,
                SurakshaContract.TxnEntry.COLUMN_LEDGER +"= ? AND "+ SurakshaContract.TxnEntry.COLUMN_FK_ACCOUNT_NUMBER +"= ?",
                new String[]{String.valueOf(SurakshaContract.TxnEntry.DEPOSIT_LEDGER), String.valueOf(accountNumber)},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        ArrayList<Long> depositedDates = new ArrayList<>();
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            depositedDates.add(cursor.getLong(Transaction.COL_DEFINED_DEPOSIT_DATE));
        }
        cursor.close();
        RecyclerView recyclerViewDue = (RecyclerView) rootView.findViewById(R.id.due_list);
        LinearLayoutManager llm = new LinearLayoutManager(rootView.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewDue.setLayoutManager(llm);
        recyclerViewDue.setAdapter(
                new DueRecyclerViewAdapter(
                    getArguments().getInt("account_number",-1),
                    Utility.getPendingDepositMonths(depositedDates)
                )
        );
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnItemClickListener) {
//            mListener = (OnItemClickListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnItemClickListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }
    private static final String[] TXN_COLUMNS = new String[]{
            SurakshaContract.TxnEntry.TABLE_NAME+"."+SurakshaContract.TxnEntry._ID,
            SurakshaContract.TxnEntry.COLUMN_FK_ACCOUNT_NUMBER,
            SurakshaContract.TxnEntry.COLUMN_AMOUNT,
            SurakshaContract.TxnEntry.COLUMN_DEFINED_DEPOSIT_DATE,
            SurakshaContract.TxnEntry.COLUMN_FK_LOAN_PAYED_ID,
            SurakshaContract.TxnEntry.COLUMN_VOUCHER_TYPE,
            SurakshaContract.TxnEntry.COLUMN_LEDGER,
            SurakshaContract.TxnEntry.COLUMN_NARRATION,
            SurakshaContract.TxnEntry.COLUMN_CREATED_AT,
            SurakshaContract.TxnEntry.COLUMN_UPDATED_AT
    };

    // these indices must match the projection
    public static final int COL_ID                   = 0;
    public static final int COL_FK_ACCOUNT_NUMBER     = 1;
    public static final int COL_AMOUNT                = 2;
    public static final int COL_MONTH_OF_DEPOSIT      = 3;
    public static final int COL_FK_LOAN_PAYED_ID      = 4;
    public static final int COL_VOUCHER_TYPE          = 5;
    public static final int COL_LEDGER                = 6;
    public static final int COL_NARRATION             = 7;
    public static final int COL_CREATED_AT            = 8;
    public static final int COL_UPDATED_AT            = 9;

//    @Override
//    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
//        switch (loaderId) {
//            case TXN_LOADER:
//                int accountNumber = getArguments().getInt("account_number");
//                return new CursorLoader(getContext(),
//                        getContext().getContentResolver().query(
//                                SurakshaContract.TxnEntry.buildTxnOfAccountUri(accountNumber),
//                                TXN_COLUMNS, null, null, null);
//
//            default:
//                // An invalid id was passed in
//                return null;
//        }
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        switch (loader.getId()) {
//            case TXN_LOADER:
//                if (data != null && data.moveToFirst()) {
//                    txn.setId(cursor.getLong(COL_ID));
//                    txn.setAmount(cursor.getDouble(COL_AMOUNT));
//                    txn.setDefinedDepositDate(cursor.getInt(COL_MONTH_OF_DEPOSIT));
//                }
//                break;
//        }
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> loader) {
//
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
//    public interface OnItemClickListener {
//        // TODO: Update argument type and name
//        void onItemClick(DummyItem item);
//    }
}
