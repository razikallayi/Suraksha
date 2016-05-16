package com.razikallayi.suraksha.officer;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.razikallayi.suraksha.BaseActivity;
import com.razikallayi.suraksha.R;

public class OfficerListActivity extends BaseActivity
        implements OfficerFragment.OnClickOfficerListItemListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.officer_list_activity);

        //Setup the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (toolbar != null) {
            toolbar.setTitle(getTitle());
        }

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
    @Override
    public void onClickOfficerListItem(Officer officer) {
        Bundle arguments = new Bundle();
        arguments.putLong(OfficerDetailFragment.ARG_OFFICER_ID, officer.getId());
        OfficerDetailFragment officerDetailFragment = new OfficerDetailFragment();
        officerDetailFragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.officer_detail_container, officerDetailFragment)
                .commit();
    }
}
