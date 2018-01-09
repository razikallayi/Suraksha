package com.razikallayi.suraksha.deposit;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.razikallayi.suraksha.BaseActivity;
import com.razikallayi.suraksha.R;

public class DepositActivity extends BaseActivity {
    public static final String ARG_ACCOUNT_NUMBER = "account_number";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deposit_make_activity);
        //Setup the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        DepositFragment depositFragment = new DepositFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_ACCOUNT_NUMBER, getIntent().getIntExtra(ARG_ACCOUNT_NUMBER, -1));
        depositFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.depositFragment, depositFragment).commit();

    }
}
