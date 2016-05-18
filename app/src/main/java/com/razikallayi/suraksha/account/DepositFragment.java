package com.razikallayi.suraksha.account;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.utils.Utility;

import java.util.Calendar;
import java.util.List;

public class DepositFragment extends Fragment{
    public static final String TAG = DepositFragment.class.getSimpleName();
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ACCOUNT_NUMBER = "account_number";
    private int mAccountNumber;
    private Account mAccount;

    public DepositFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.deposit_list, container, false);
        if(rootView instanceof RecyclerView) {
            RecyclerView rvPendingDeposit=(RecyclerView) rootView;

            rvPendingDeposit.setHasFixedSize(true);

//            // use a linear layout manager
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            rvPendingDeposit.setLayoutManager(layoutManager);

            mAccountNumber = getArguments().getInt(ARG_ACCOUNT_NUMBER);
            mAccount = Account.getAccountFromAccountNumber(getContext(),mAccountNumber);
            List<Calendar> pendingMonthsList = Utility
                    .getPendingDepositMonthsFromTxn(mAccount.fetchDeposits(getContext()));
            // specify an adapter
            PendingDepositAdapter pendingDepositAdapter = new PendingDepositAdapter(pendingMonthsList);
            rvPendingDeposit.setAdapter(pendingDepositAdapter);
        }
        return rootView;
    }
}
