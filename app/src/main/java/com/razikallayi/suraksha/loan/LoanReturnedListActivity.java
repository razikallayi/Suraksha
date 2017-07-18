package com.razikallayi.suraksha.loan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.razikallayi.suraksha.BaseActivity;
import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.member.Member;

public class LoanReturnedListActivity extends BaseActivity {
    public static final String ARG_LOAN_ISSUE_ID = "loan_issue_id";
    public static final int LOAN_RETURN_ACTIVITY = 0x111;
    private static LoanIssue sLoanIssue;
    private long loanIssueId;
    private Context mContext;
    private Member mMember;
    private Button loanReturnButton;
    private LoanReturnedAdapter loanReturnedAdapter;
    private RecyclerView loanReturnedRecyclerView;

    public static void setLoanIssue(LoanIssue loanIssue) {
        sLoanIssue = loanIssue;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loan_return_list_activity);

        //Setup the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        mContext = getApplicationContext();

        loadLoanReturnList();

    }

    public void loadLoanReturnList() {

        loanReturnedRecyclerView = (RecyclerView) findViewById(R.id.loan_return_list);
        loanReturnedAdapter = new LoanReturnedAdapter(sLoanIssue.getLoanReturnTxnList(mContext));
        if (null != loanReturnedRecyclerView) {
            // specify an adapter
            loanReturnedRecyclerView.setAdapter(loanReturnedAdapter);
            loanReturnedRecyclerView.setHasFixedSize(true);
            loanReturnedRecyclerView.setNestedScrollingEnabled(false);

            // use a linear layout manager
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            layoutManager.setReverseLayout(true);
            layoutManager.setStackFromEnd(true);
            loanReturnedRecyclerView.setLayoutManager(layoutManager);

            int firstPosition = 0;
            loanReturnedRecyclerView.smoothScrollToPosition(firstPosition);
        }

        int accountNumber = sLoanIssue.getAccountNumber();
        mMember = Member.getMemberFromAccountNumber(mContext, accountNumber);

        TextView memberNameTv = (TextView) findViewById(R.id.tvMemberName);
        memberNameTv.setText(mMember.getName());
        TextView memberAddressTv = (TextView) findViewById(R.id.tvMemberAddress);
        memberAddressTv.setText(mMember.getAddress());
        TextView memberAcNoTv = (TextView) findViewById(R.id.tvAcNo);
        memberAcNoTv.setText(String.valueOf(mMember.getAccountNo()));

        loanReturnButton = (Button) findViewById(R.id.tvActonButton);
        removeButtonIfLoanClosed(loanReturnButton, sLoanIssue);
        loanReturnButton.setText("Loan Return");
        loanReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, LoanReturnActivity.class);
                LoanReturnActivity.setLoanIssue(sLoanIssue);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadLoanReturnList();
        removeButtonIfLoanClosed(loanReturnButton, sLoanIssue);
    }

    public void removeButtonIfLoanClosed(View loanReturnButton, LoanIssue loanIssue) {
        if (loanIssue.isClosed()) {
            loanReturnButton.setVisibility(View.GONE);
        } else {
            loanReturnButton.setVisibility(View.VISIBLE);
        }
    }
}
