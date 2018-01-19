package com.razikallayi.suraksha.loan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.razikallayi.suraksha.BaseActivity;
import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.member.Member;
import com.razikallayi.suraksha.utils.Utility;

public class LoanReturnedListActivity extends BaseActivity {
    public static final String ARG_LOAN_ISSUE_ID = "loan_issue_id";
    public static final String ARG_LAST_INSTALMENT_NO = "last_instalment_no";
    private static final int LOAN_RETURN_ACTIVITY_REQUEST_CODE = 0x127;
    private LoanIssue mLoanIssue;
    private Member mMember;
    private Button loanReturnButton;
    private LoanReturnedAdapter loanReturnedAdapter;
    private RecyclerView loanReturnedRecyclerView;
    private TextView tvActionDetails;
    private TextView tvActionInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loan_return_list_activity);

        //Setup the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        long loanIssueId = getIntent().getLongExtra(ARG_LOAN_ISSUE_ID, -1);
        mLoanIssue = LoanIssue.getLoanIssue(this, loanIssueId);
        if (mLoanIssue != null) {
            mMember = mLoanIssue.getMember(this);
        }

        RelativeLayout layoutMemberInfo = findViewById(R.id.layoutMemberInfo);
        mMember.setMemberInfo(this, layoutMemberInfo);

        loanReturnedRecyclerView = findViewById(R.id.loan_return_list);
        tvActionInfo = findViewById(R.id.tvMemberName);
        tvActionDetails = findViewById(R.id.tvAddress);
        loadLoanReturnList();

    }

    public void loadLoanReturnList() {
        loanReturnedAdapter = new LoanReturnedAdapter(mLoanIssue.getLoanReturnTxnList(this));
        if (null != loanReturnedRecyclerView) {
            // specify an adapter
            loanReturnedRecyclerView.setAdapter(loanReturnedAdapter);
            loanReturnedRecyclerView.setHasFixedSize(true);
            loanReturnedRecyclerView.setNestedScrollingEnabled(false);

            // use a linear layout manager
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setReverseLayout(true);
            layoutManager.setStackFromEnd(true);
            loanReturnedRecyclerView.setLayoutManager(layoutManager);

            int firstPosition = 0;
            loanReturnedRecyclerView.smoothScrollToPosition(firstPosition);
        }


        String info = Utility.formatNumberSuffix(mLoanIssue.nextInstalmentNumber(this))
                + " Instalment  " + Utility.formatAmountInRupees(this, mLoanIssue.getLoanInstalmentAmount());
        tvActionInfo.setText(info);

        Member securityMember = mLoanIssue.getSecurityMember(this);
        String details = securityMember.getInfo();
        tvActionDetails.setText(details);

        loanReturnButton = findViewById(R.id.tvActonButton);
        removeButtonIfLoanClosed(loanReturnButton, mLoanIssue);
        loanReturnButton.setText("Loan Return");
        loanReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoanReturnedListActivity.this, ReturnLoanActivity.class);
                intent.putExtra(ReturnLoanActivity.ARG_LOAN_ISSUE_ID, mLoanIssue.getId());
                startActivityForResult(intent, LOAN_RETURN_ACTIVITY_REQUEST_CODE);
            }
        });
    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == LOAN_RETURN_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
//            reloadList(data.getIntExtra(ARG_LAST_INSTALMENT_NO, -1));
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mLoanIssue == null) {
            long loanIssueId = getIntent().getLongExtra(ARG_LOAN_ISSUE_ID, -1);
            mLoanIssue = LoanIssue.getLoanIssue(this, loanIssueId);
        }
        if (mLoanIssue != null) {
            reloadList(mLoanIssue.lastInstalmentNumber(this));
        }
    }

    private void reloadList(int lastInstalmentNo) {

        long loanIssueId = getIntent().getLongExtra(ARG_LOAN_ISSUE_ID, -1);
        mLoanIssue = LoanIssue.getLoanIssue(this, loanIssueId);
        loadLoanReturnList();
        if (lastInstalmentNo >= mLoanIssue.bystanderReleaseInstalment()) {
            tvActionDetails.setText("Guarantor Closed");
        }
        removeButtonIfLoanClosed(loanReturnButton, mLoanIssue);
    }

    public void removeButtonIfLoanClosed(View loanReturnButton, LoanIssue loanIssue) {
        if (loanIssue.isClosed()) {
            tvActionInfo.setText("Loan Closed");
            loanReturnButton.setVisibility(View.GONE);
        } else {
            loanReturnButton.setVisibility(View.VISIBLE);
        }
    }
}
