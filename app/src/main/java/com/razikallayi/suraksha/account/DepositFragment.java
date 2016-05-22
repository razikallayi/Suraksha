package com.razikallayi.suraksha.account;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.utils.CalendarUtils;
import com.razikallayi.suraksha.utils.Utility;

public class DepositFragment extends Fragment implements DepositAdapter.OnCheckedChangeListener {
    public static final String TAG = DepositFragment.class.getSimpleName();
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ACCOUNT_NUMBER = "account_number";
    private int mAccountNumber;
    private Account mAccount;
    private RecyclerView mDepositRecyclerView;

    public DepositFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.deposit_list, container, false);
        if (rootView instanceof RecyclerView) {
             mDepositRecyclerView = (RecyclerView) rootView;

            mDepositRecyclerView.setHasFixedSize(true);

//            // use a linear layout manager
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            mDepositRecyclerView.setLayoutManager(layoutManager);

            mAccountNumber = getArguments().getInt(ARG_ACCOUNT_NUMBER);
            mAccount = Account.getAccountFromAccountNumber(getContext(), mAccountNumber);
            // specify an adapter
            DepositAdapter depositAdapter = new DepositAdapter(
                    mAccount.fetchDeposits(getContext()), mAccount, this);
            mDepositRecyclerView.setAdapter(depositAdapter);
        }
        return rootView;
    }

    @Override
    public void onCheckedChangedDepositMonth(final View view, final String month, boolean isChecked) {
        if (!isChecked) {
            return;
        }
        final CompoundButton checkbox = (CompoundButton) view;
        if (!checkbox.isEnabled()) {
            return;
        }

        LayoutInflater inflater = getLayoutInflater(null);
        final View remarks_dialog_content = inflater.inflate(R.layout.remarks_dialog_content, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(checkbox.getContext());
        builder.setMessage("Deposit " +Utility.formatAmountInRupees(getContext(),
                Utility.getMonthlyDepositAmount())+ " in account " + mAccountNumber + " ?")
                .setTitle(checkbox.getText())
                .setView(remarks_dialog_content)
                .setPositiveButton("Deposit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String remarks = ((EditText) remarks_dialog_content.findViewById(R.id.remarksEditText)).getText().toString();
                        mAccount.makeDeposit(view.getContext(), CalendarUtils.writableDepositMonth(month), remarks);
                        checkbox.setEnabled(false);//Disable checkbox

                    }

                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkbox.setChecked(false);
                        dialog.cancel();
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        checkbox.setChecked(false);
                        dialog.cancel();
                    }
                })
                .show();
    }
}
