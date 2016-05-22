package com.razikallayi.suraksha.account;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.data.SurakshaContract;
import com.razikallayi.suraksha.member.Member;

import java.util.ArrayList;

public class AccountListFragment extends Fragment {
    public static final String TAG = AccountListFragment.class.getSimpleName();
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_MEMBER_ID = "member_id";


    private static final String[] ACCOUNT_COLUMNS = {
            SurakshaContract.AccountEntry.TABLE_NAME +"."+SurakshaContract.AccountEntry._ID,
            SurakshaContract.AccountEntry.COLUMN_ACCOUNT_NUMBER,
            SurakshaContract.AccountEntry.COLUMN_MEMBER_ID,
            SurakshaContract.AccountEntry.COLUMN_OPENING_BALANCE  ,
            SurakshaContract.AccountEntry.COLUMN_INSTALMENT_AMOUNT,
            SurakshaContract.AccountEntry.TABLE_NAME +"."+SurakshaContract.AccountEntry.COLUMN_IS_ACTIVE        ,
            SurakshaContract.AccountEntry.TABLE_NAME +"."+SurakshaContract.AccountEntry.COLUMN_CLOSED_AT        ,
            SurakshaContract.AccountEntry.TABLE_NAME +"."+SurakshaContract.AccountEntry.COLUMN_CREATED_AT       ,
            SurakshaContract.AccountEntry.TABLE_NAME +"."+SurakshaContract.AccountEntry.COLUMN_UPDATED_AT
    };


    private static final int COL_ACCOUNT_ID           = 0;
    private static final int COL_ACCOUNT_NUMBER       = 1;
    private static final int COL_FK_MEMBER_ID         = 2;
    private static final int COLUMN_OPENING_BALANCE   = 3;
    private static final int COLUMN_INSTALMENT_AMOUNT = 4;
    private static final int COLUMN_IS_ACTIVE         = 5;
    private static final int COLUMN_CLOSED_AT         = 6;
    private static final int COLUMN_CREATED_AT        = 7;
    private static final int COLUMN_UPDATED_AT        = 8;

    public AccountListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.account_list, container, false);
        View recyclerView = rootView.findViewById(R.id.account_list);
        setupRecyclerView((RecyclerView) recyclerView);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
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
            intent.putExtra(ARG_MEMBER_ID, getArguments().getLong(ARG_MEMBER_ID));
            startActivityForResult(intent, 1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
//TODO USE Loader
        Cursor cursorAccounts = getContext().getContentResolver().query(SurakshaContract.AccountEntry
                        .buildAccountsOfMemberUri(getArguments().getLong(ARG_MEMBER_ID)),
                ACCOUNT_COLUMNS, null, null, null);
//        if (cursorAccounts.getCount() > 1) {
//            LinearLayoutManager layoutManager
//                    = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
//            recyclerView.setLayoutManager(layoutManager);
//        }
        recyclerView.setAdapter(new AccountRecyclerViewAdapter(setListAdapter(cursorAccounts)));
    }

    private ArrayList<Account> setListAdapter(Cursor cursor){
        ArrayList<Account> arrayList = new ArrayList<>();
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Account account =new Account();
            // The Cursor is now set to the right position
            account.setId(cursor.getInt(COL_ACCOUNT_ID));
            account.setAccountNumber(cursor.getInt(COL_ACCOUNT_NUMBER));
            account.setMember(Member.getMemberFromId(getContext(), cursor.getLong(COL_FK_MEMBER_ID)));
            account.setInstalmentAmount(cursor.getDouble(COLUMN_INSTALMENT_AMOUNT));
            account.setOpeningBalance(cursor.getDouble(COLUMN_OPENING_BALANCE));
            account.setActive(cursor.getInt(COLUMN_IS_ACTIVE) == 1);
            account.setClosedAt(COLUMN_CLOSED_AT);
            account.setCreatedAt(COLUMN_CREATED_AT);
            account.setUpdatedAt(COLUMN_UPDATED_AT);
            arrayList.add(account);
        }
        cursor.close();
        return arrayList;
    }


    public class AccountRecyclerViewAdapter
            extends RecyclerView.Adapter<AccountRecyclerViewAdapter.ViewHolder> {
        private ArrayList<Account> mAccountArray;

        public AccountRecyclerViewAdapter(ArrayList<Account> accounts) {
            mAccountArray = accounts;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity())
                    .inflate(R.layout.account_list_content,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public int getItemCount() {
            return mAccountArray.size();
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
          // Double depositDue = getAccountDue(mAccountArray.get(position).getAccountNumber(), SurakshaContract.TxnEntry.DEPOSIT_LEDGER);




            holder.mAccount = mAccountArray.get(position);
            //holder.mAccountNumber.setText(mAccountArray.get(position).getId());
            holder.mAccountNumber.setText(String.valueOf(mAccountArray.get(position).getAccountNumber()));
          //  holder.mDepositDue.setText(String.valueOf(depositDue));
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, AccountDetailActivity.class);
                                      intent.putExtra("account_number", holder.mAccount.getAccountNumber());
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
}
