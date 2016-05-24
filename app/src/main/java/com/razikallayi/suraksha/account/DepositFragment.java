package com.razikallayi.suraksha.account;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.txn.Transaction;

import java.util.List;

public class DepositFragment extends Fragment {
    public static final String TAG = DepositFragment.class.getSimpleName();
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ACCOUNT_NUMBER = "account_number";
    private int mAccountNumber;
    private Account mAccount;
    private Context mContext;
    private RecyclerView mDepositRecyclerView;

    public DepositFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        mContext = getContext();

        final View rootView = inflater.inflate(R.layout.deposit_list, container, false);
        mDepositRecyclerView = (RecyclerView) rootView.findViewById(R.id.deposited_list);
        mDepositRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mDepositRecyclerView.setLayoutManager(layoutManager);

        //get AccountNumber From Intent
        mAccountNumber = getArguments().getInt(ARG_ACCOUNT_NUMBER);
        mAccount = Account.getAccountFromAccountNumber(mContext, mAccountNumber);

        // specify an adapter
        DepositAdapter depositAdapter = new DepositAdapter(getContext(), mAccount);
        mDepositRecyclerView.setAdapter(depositAdapter);






        final Context mContext = getContext();
        final Account mAccount = Account.getAccountFromAccountNumber(mContext, mAccountNumber);
        final List<Transaction> oldDeposits = mAccount.fetchDeposits(mContext);
        FloatingActionButton makeDepositBtn = (FloatingActionButton) rootView.findViewById(R.id.fabMakeDeposit);
        if (makeDepositBtn != null) {
            makeDepositBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "Clicked Fab Depsit", Toast.LENGTH_SHORT).show();
//                    Calendar depositMonth = CalendarUtils.getInstance();
//                    if (oldDeposits.isEmpty()) {
//                        depositMonth = CalendarUtils.getSurakshaStartDate();
//                    } else {
//                        depositMonth.setTimeInMillis(oldDeposits.get(oldDeposits.size()).getDefinedDepositMonth());
//                        depositMonth.add(Calendar.MONTH, 1);
//                    }
//
//                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
//                    builder.setMessage("Deposit " + Utility.formatAmountInRupees(mContext,
//                            Utility.getMonthlyDepositAmount()) + " in account " + mAccountNumber + " ?");
//                    builder.setTitle(CalendarUtils.readableDepositMonth(depositMonth));
//                    final View remarks_dialog_content = rootView.findViewById(R.id.remark_dialog_layout);
//                    builder.setView(remarks_dialog_content);
//                    final Calendar nextDepositMonth = depositMonth;
//                    builder.setPositiveButton("Deposit", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            String remarks = ((EditText) remarks_dialog_content.findViewById(R.id.remarksEditText)).getText().toString();
//                            mAccount.makeDeposit(mContext, nextDepositMonth.getTimeInMillis(), remarks);
//                        }
//
//                    });
//                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//                        }
//                    });
//                    builder.show();
                }
            });
        }
















        return rootView;
    }
}
