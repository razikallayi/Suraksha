package com.razikallayi.suraksha_ssf.deposit;

import android.content.Context;
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

import com.razikallayi.suraksha_ssf.BaseActivity;
import com.razikallayi.suraksha_ssf.R;
import com.razikallayi.suraksha_ssf.member.Member;
import com.razikallayi.suraksha_ssf.txn.Transaction;
import com.razikallayi.suraksha_ssf.utils.CalendarUtils;
import com.razikallayi.suraksha_ssf.utils.Utility;

import java.util.List;

public class DepositListActivity extends BaseActivity {
    public static final String ARG_MEMBER_ID = "member_id";
    private static final int MAKE_DEPOSIT_ACTIVITY_REQUEST_CODE = 0x231;
    private Context mContext;
    private Member mMember;
    private TextView mNoOfDeposits;
    private TextView mTotalDepositAmount;
    private DepositAdapter depositAdapter;
    private TextView tvActionInfo;
    private Button makeDepositBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deposit_list_activity);
        //Setup the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mContext = this;


        RecyclerView depositRecyclerView = findViewById(R.id.deposited_list);
        depositRecyclerView.setHasFixedSize(true);
        depositRecyclerView.setNestedScrollingEnabled(false);

//        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        depositRecyclerView.setLayoutManager(layoutManager);

        // use a grid layout manager with 4 columns
//        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 4);
//        depositRecyclerView.setLayoutManager(layoutManager);

        //get AccountNumber From Intent
        long memberId = getIntent().getLongExtra(ARG_MEMBER_ID, -1);
        mMember = Member.getMemberFromId(mContext, memberId);

        RelativeLayout layoutMemberInfo = findViewById(R.id.layoutMemberInfo);
        mMember.setMemberInfo(this, layoutMemberInfo);


        mNoOfDeposits = findViewById(R.id.tvNoOfDeposits);
        mTotalDepositAmount = findViewById(R.id.tvTotalDepositAmount);

        tvActionInfo = findViewById(R.id.tvMemberName);
        TextView tvActionDetails = findViewById(R.id.tvAddress);
        tvActionDetails.setVisibility(View.GONE);

        // specify an adapter
        depositAdapter = new DepositAdapter(mContext, mMember);
        depositRecyclerView.setAdapter(depositAdapter);
        loadTotals();

        makeDepositBtn = findViewById(R.id.tvActonButton);
        if (!mMember.isAccountClosed()) {
            makeDepositBtn.setBackground(getDrawable(R.drawable.touch_selector_blue));
            makeDepositBtn.setText("Make Deposit");
            makeDepositBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), MakeDepositActivity.class);
                    intent.putExtra(MakeDepositActivity.ARG_MEMBER_ID, mMember.getId());
                    startActivityForResult(intent, MAKE_DEPOSIT_ACTIVITY_REQUEST_CODE);
                }
            });
        } else {
            tvActionInfo.setText(R.string.account_closed);
            makeDepositBtn.setVisibility(View.GONE);
        }

    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == MAKE_DEPOSIT_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
//            loadTotals();
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTotals();
    }

    private void loadTotals() {
        if (mMember == null) {
            long memberId = getIntent().getLongExtra(ARG_MEMBER_ID, -1);
            mMember = Member.getMemberFromId(mContext, memberId);
        }
        List<Transaction> deposits = mMember.fetchDeposits(mContext);
        double sum = mMember.sumOfDeposits(mContext);
        mTotalDepositAmount.setText(Utility.formatAmountInRupees(mContext, sum));
        mNoOfDeposits.setText(String.valueOf(deposits.size()));
        depositAdapter.listUpdated();

        if (!mMember.isAccountClosed()) {
            tvActionInfo.setText(CalendarUtils.readableDepositMonth(mMember.getNextDepositMonthCalendar(deposits)));
        }
    }

}
