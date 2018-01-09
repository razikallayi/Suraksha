package com.razikallayi.suraksha.deposit;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.member.Member;
import com.razikallayi.suraksha.utils.Utility;

import static com.razikallayi.suraksha.R.id.tvActonButton;

public class DepositFragment extends Fragment {
    public static final String TAG = DepositFragment.class.getSimpleName();
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ACCOUNT_NUMBER = "account_number";
    private Context mContext;
    private Member mMember;
    private TextView mNoOfDeposits;
    private TextView mTotalDepositAmount;

    public DepositFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = getContext();

        final View rootView = inflater.inflate(R.layout.deposit_fragment, container, false);
        RecyclerView depositRecyclerView = rootView.findViewById(R.id.deposited_list);
        depositRecyclerView.setHasFixedSize(true);
        depositRecyclerView.setNestedScrollingEnabled(false);

//        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        depositRecyclerView.setLayoutManager(layoutManager);

        // use a grid layout manager with 4 columns
//        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 4);
//        depositRecyclerView.setLayoutManager(layoutManager);

        //get AccountNumber From Intent
        int accountNumber = getArguments().getInt(ARG_ACCOUNT_NUMBER);
        mMember = Member.getMemberFromAccountNumber(mContext, accountNumber);

        mNoOfDeposits = rootView.findViewById(R.id.tvNoOfDeposits);
        int depositCount = mMember.getTotalDeposits(mContext);
        mNoOfDeposits.setText(String.valueOf(depositCount));
        mTotalDepositAmount = rootView.findViewById(R.id.tvTotalDepositAmount);
        mTotalDepositAmount.setText(Utility.formatAmountInRupees(mContext,depositCount * Utility.getMonthlyDepositAmount()));

        TextView memberNameTv = rootView.findViewById(R.id.tvMemberName);
        memberNameTv.setText(mMember.getName());
        TextView memberAddressTv = rootView.findViewById(R.id.tvMemberAddress);
        memberAddressTv.setText(mMember.getAddress());
        TextView memberAcNoTv = rootView.findViewById(R.id.tvAcNo);
        memberAcNoTv.setText(String.valueOf(mMember.getAccountNo()));

        // specify an adapter
        final DepositAdapter depositAdapter = new DepositAdapter(getContext(), mMember);
        depositRecyclerView.setAdapter(depositAdapter);

        Button makeDepositBtn = rootView.findViewById(tvActonButton);
        makeDepositBtn.setText("Make Deposit");
        if (makeDepositBtn != null) {
            makeDepositBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    depositAdapter.onMakeDepositClick(view, inflater);
                }
            });
        }

        depositAdapter.setOnMakeDepositListener(new OnMakeDepositListener() {
            @Override
            public void onMakeDeposit() {
                int depositCount = mMember.getTotalDeposits(mContext);
                mNoOfDeposits.setText(String.valueOf(depositCount));
                mTotalDepositAmount.setText(Utility.formatAmountInRupees(mContext,depositCount * Utility.getMonthlyDepositAmount()));
            }
        });

        return rootView;
    }

    // The callback interface
    public interface OnMakeDepositListener {
        void onMakeDeposit();
    }
}
