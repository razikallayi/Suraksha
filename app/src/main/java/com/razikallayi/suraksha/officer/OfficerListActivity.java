package com.razikallayi.suraksha.officer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.razikallayi.suraksha.BaseActivity;
import com.razikallayi.suraksha.R;

public class OfficerListActivity extends BaseActivity
        implements OfficerFragment.OnClickOfficerListItemListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

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

        if (findViewById(R.id.officer_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }
    @Override
    public void onClickOfficerListItem(Officer officer) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putLong(OfficerDetailFragment.ARG_OFFICER_ID, officer.getId());
            OfficerDetailFragment officerDetailFragment = new OfficerDetailFragment();
            officerDetailFragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.officer_detail_container, officerDetailFragment)
                    .commit();
        } else {
            Intent intent = new Intent(getApplicationContext(), OfficerDetailActivity.class);
            intent.putExtra(OfficerDetailActivity.ARG_OFFICER_ID, officer.getId());
            startActivity(intent);
        }


    }
}
