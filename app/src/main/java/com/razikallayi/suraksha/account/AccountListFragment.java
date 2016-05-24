package com.razikallayi.suraksha.account;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.RecyclerViewCursorAdapter;
import com.razikallayi.suraksha.data.SurakshaContract;
import com.razikallayi.suraksha.member.Member;

public class AccountListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String TAG = AccountListFragment.class.getSimpleName();
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_MEMBER_ID = "member_id";

    private static final int CREATE_ACCOUNT_ACTIVITY = 1;
    private final int ACCOUNT_LIST_LOADER = 1;
    private long mMemberId;
    private AccountRecyclerViewAdapter mAccountListAdapter;

    public AccountListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.account_list, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.account_list);
        mAccountListAdapter = new AccountRecyclerViewAdapter();
        recyclerView.setAdapter(mAccountListAdapter);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null == savedInstanceState) {
            mMemberId = getArguments().getLong(ARG_MEMBER_ID);
            getLoaderManager().initLoader(ACCOUNT_LIST_LOADER, null, this);
            setHasOptionsMenu(true);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.account_list_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.add_account) {
            Intent intent = new Intent(getContext(), CreateAccountActivity.class);
            intent.putExtra(ARG_MEMBER_ID, mMemberId);
            startActivityForResult(intent, CREATE_ACCOUNT_ACTIVITY);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(), SurakshaContract.AccountEntry.buildAccountsOfMemberUri(mMemberId),
                Account.AccountQuery.PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAccountListAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAccountListAdapter.swapCursor(null);
    }


    public class AccountRecyclerViewAdapter
            extends RecyclerViewCursorAdapter<AccountListFragment.ViewHolder> {

        public AccountRecyclerViewAdapter() {}

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity())
                    .inflate(R.layout.account_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, Cursor cursor) {

            final Account account = new Account();
            // The Cursor is now set to the right position
            account.setId(cursor.getInt(Account.AccountQuery.COL_ID));
            account.setAccountNumber(cursor.getInt(Account.AccountQuery.COL_ACCOUNT_NUMBER));
            account.setMember(Member.getMemberFromId(getContext(), cursor.getLong(Account.AccountQuery.COL_MEMBER_ID)));
            account.setInstalmentAmount(cursor.getDouble(Account.AccountQuery.COL_INSTALMENT_AMOUNT));
            account.setOpeningBalance(cursor.getDouble(Account.AccountQuery.COL_OPENING_BALANCE));
            account.setActive(cursor.getInt(Account.AccountQuery.COL_IS_ACTIVE) == 1);
            account.setClosedAt(Account.AccountQuery.COL_CLOSED_AT);
            account.setCreatedAt(Account.AccountQuery.COL_CREATED_AT);
            account.setUpdatedAt(Account.AccountQuery.COL_UPDATED_AT);

            holder.mAccount = account;
            //holder.mAccountNumber.setText(mAccountArray.get(position).getId());
            holder.mAccountNumber.setText(String.valueOf(account.getAccountNumber()));
            //  holder.mDepositDue.setText(String.valueOf(depositDue));
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, AccountDetailActivity.class);
                    intent.putExtra("account_number", account.getAccountNumber());
                    context.startActivity(intent);
                }
            });
        }

     /*   private double getAccountDue(String accountNumber,String ledger){
            Log.d(TAG, "getAccountDue: startted. acno:"+accountNumber+" ledger: "+ledger);
            String dueSelectionFromTxnForAccount=
                    SurakshaContract.TxnEntry.COLUMN_FK_ACCOUNT_NUMBER+
                    " = ? and "+SurakshaContract.TxnEntry.COLUMN_LEDGER+" = ?";
            Log.d(TAG, "Due selection : "+dueSelectionFromTxnForAccount);
            Cursor curLastDepositDate=getContext().getContentResolver().query(SurakshaContract.TxnEntry.buildTxnOfAccount(accountNumber),
                    new String[]{SurakshaContract.TxnEntry.TABLE_NAME+"."+SurakshaContract.TxnEntry.COLUMN_CREATED_AT},
                    dueSelectionFromTxnForAccount,
                    new String[]{SurakshaContract.TxnEntry.COLUMN_FK_ACCOUNT_NUMBER,ledger},
                    SurakshaContract.TxnEntry.TABLE_NAME+"."+SurakshaContract.TxnEntry.COLUMN_CREATED_AT+" desc"
            );
            Log.d(TAG, "getAccountDue: "+curLastDepositDate.toString());
            //curLastDepositDate.moveToFirst();
            Log.d(TAG, "getAccountDue: is null "+(curLastDepositDate != null));
            Log.d(TAG, "getAccountDue: count  "+(curLastDepositDate.getCount()));
            Log.d(TAG, "getAccountDue: count  "+(curLastDepositDate.getColumnCount()));
            if (curLastDepositDate != null) {

                Calendar lastDepositDate = Calendar.getInstance();
                lastDepositDate.setTimeInMillis(curLastDepositDate.getLong(0));
                curLastDepositDate.close();
                Calendar currentDate = Calendar.getInstance();
                int dueMonths = currentDate.get(Calendar.MONTH) - lastDepositDate.get(Calendar.MONTH);
                switch (ledger){
                    case SurakshaContract.TxnEntry.DEPOSIT_LEDGER:
                        Log.d(TAG, "getAccountDue: "+dueMonths * Utility.getMonthlyDepositAmount());
                        return dueMonths * Utility.getMonthlyDepositAmount();
                    default:
                        return 0;
                }
            }
            Log.d(TAG, "getAccountDue: before final return");
            return 0;
        }
*/
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mAccountNumber;
        //            public final TextView mTotalDue;
//            public final TextView mDepositDue;
//            public final TextView mLoanDue;
//            public final TextView mTotalDeposit;
//            public final TextView mLoanTaken;
//            public final TextView mLoanRepayed;
//            public final TextView mLastTxnDate;
        public Account mAccount;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mAccountNumber = (TextView) view.findViewById(R.id.tvAccountNumber);
//                mTotalDue = (TextView) view.findViewById(R.id.tvTotalDue);
//                mDepositDue = (TextView) view.findViewById(R.id.tvDepositDue);
//                mLoanDue = (TextView) view.findViewById(R.id.tvLoanDue);
//                mTotalDeposit = (TextView) view.findViewById(R.id.tvTotalDeposit);
//                mLoanTaken = (TextView) view.findViewById(R.id.tvLoanTaken);
//                mLoanRepayed = (TextView) view.findViewById(R.id.tvLoanRepayed);
//                mLastTxnDate = (TextView) view.findViewById(R.id.tvLastTxnDate);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mAccountNumber.getText() + "'";
        }
    }
}
