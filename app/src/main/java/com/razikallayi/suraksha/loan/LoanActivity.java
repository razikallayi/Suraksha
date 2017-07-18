package com.razikallayi.suraksha.loan;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.razikallayi.suraksha.BaseActivity;
import com.razikallayi.suraksha.R;

public class LoanActivity extends BaseActivity {
    public static final String ARG_ACCOUNT_NUMBER = "account_number";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loan_activity);
        //Setup the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        LoanIssuedFragment loanIssuedFragment = new LoanIssuedFragment();
        Bundle bundle = new Bundle();
        int accountNumber = getIntent().getIntExtra(ARG_ACCOUNT_NUMBER, -1);
        bundle.putInt(ARG_ACCOUNT_NUMBER, accountNumber);
        loanIssuedFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .addToBackStack(LoanReturnedFragment.TAG)
                .replace(R.id.loanFragment, loanIssuedFragment).commit();
    }
}
