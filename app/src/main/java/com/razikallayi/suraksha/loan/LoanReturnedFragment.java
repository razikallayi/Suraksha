package com.razikallayi.suraksha.loan;


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

public class LoanReturnedFragment extends Fragment {
    public static final String TAG = LoanReturnedFragment.class.getSimpleName();
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_LOAN_ISSUE_ID = "loan_issue_id";
    private long loanIssueId;
    private View rootView;
    private Context mContext;
    private Member mMember;

    public LoanReturnedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.loan_return_fragment, container, false);
        mContext = this.getContext();

        loanIssueId = getArguments().getLong(ARG_LOAN_ISSUE_ID, -1);
        final LoanIssue loanIssue = LoanIssue.getLoanIssue(mContext, loanIssueId);

        RecyclerView loanReturnedRecyclerView = (RecyclerView) rootView.findViewById(R.id.loan_return_list);
        final LoanReturnedAdapter loanReturnedAdapter = new LoanReturnedAdapter(loanIssue.getLoanReturnTxnList(mContext));
        if (null != loanReturnedRecyclerView) {
            // specify an adapter
            loanReturnedRecyclerView.setAdapter(loanReturnedAdapter);
            loanReturnedRecyclerView.setHasFixedSize(true);
            loanReturnedRecyclerView.setNestedScrollingEnabled(false);

            //        // use a linear layout manager
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            layoutManager.setReverseLayout(true);
            layoutManager.setStackFromEnd(true);
            loanReturnedRecyclerView.setLayoutManager(layoutManager);

            int firstPosition = 0;
            loanReturnedRecyclerView.smoothScrollToPosition(firstPosition);
        }

        int accountNumber = loanIssue.getAccountNumber();
        mMember = Member.getMemberFromAccountNumber(mContext, accountNumber);

        TextView memberNameTv = (TextView) rootView.findViewById(R.id.tvMemberName);
        memberNameTv.setText(mMember.getName());
        TextView memberAddressTv = (TextView) rootView.findViewById(R.id.tvMemberAddress);
        memberAddressTv.setText(mMember.getAddress());
        TextView memberAcNoTv = (TextView) rootView.findViewById(R.id.tvAcNo);
        memberAcNoTv.setText(String.valueOf(mMember.getAccountNo()));

        final Button loanReturnButton = (Button) rootView.findViewById(R.id.tvActonButton);
        removeButtonIfLoanClosed(loanReturnButton,loanIssue);
        loanReturnButton.setText("Loan Return");
//        loanReturnButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                loanReturnedAdapter.onLoanReturnClick(view, inflater, loanIssue);
//            }
//        });
//
//        loanReturnedAdapter.setOnLoanReturnListener(new OnLoanReturnListener() {
//            @Override
//            public void onLoanReturn() {
//                removeButtonIfLoanClosed(loanReturnButton,loanIssue);
//            }
//        });


        return rootView;
    }

    public void removeButtonIfLoanClosed(View loanReturnButton, LoanIssue loanIssue){
        if(loanIssue.isClosed()){
            loanReturnButton.setVisibility(View.GONE);
        }else {
            loanReturnButton.setVisibility(View.VISIBLE);
        }
    }




}