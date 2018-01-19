package com.razikallayi.suraksha;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.razikallayi.suraksha.member.MemberListActivity;
import com.razikallayi.suraksha.officer.OfficerListActivity;
import com.razikallayi.suraksha.report.ReportActivity;
import com.razikallayi.suraksha.utils.AuthUtils;

public abstract class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int THIRTY_SECONDS = 30000; //30 seconds
    protected static final int mMinLockTime = THIRTY_SECONDS;
    private static final int FIFTEEN_MINUTES = 900000; //15 minutes
    protected static final int mMinOfficerLockTime = FIFTEEN_MINUTES;
    protected static int LOCK_SCREEN_REQUEST = 100;
    protected boolean mSkipLockOnce = false;
    private boolean mHasLoaded = false;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getApplicationContext();
        if (savedInstanceState == null) {
            if (!AuthUtils.isLoggedIn(mContext)) {
                launchLockScreen();
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOCK_SCREEN_REQUEST) {
            if (resultCode == RESULT_OK) {
                mHasLoaded = true;
                skipLockOnce();
            } else {
                finish(); //Finish on user double tap back button
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.base, menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.base, menu);//Add settings menu to every activity
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
//            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_home_activity) {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void launchLockScreen() {
        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, LOCK_SCREEN_REQUEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*
                Save the current time If a lock pattern has been set
        If this activity never loaded set the lock time to
        10 seconds ago.
        This is to prevent the following scenario:
            1. Activity Starts
            2. Lock screen is displayed
            3. User presses the home button
            4. "lock time" is set in onPause to when the home button was pressed
            5. Activity is started again within 2 seconds and no lock screen is shown this time.
        */
        if (mHasLoaded) {
            AuthUtils.writeLockTime(mContext);
        } else {
            AuthUtils.writeLockTime(mContext, SystemClock.elapsedRealtime() - 10000);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setOfficer();
        if (mSkipLockOnce) {
            mSkipLockOnce = false;
            return;
        }

        // If a lock pattern is set we need to check the time for when the last
        // activity was open. If it's been more than two seconds(Minimum time) the user
        // will have to enter the lock pattern to continue.
        long lastActiveTimeGap = AuthUtils.getTimeDiffInMillis(mContext);
        if (lastActiveTimeGap > mMinLockTime && lastActiveTimeGap <= mMinOfficerLockTime) { //between 5seconds and 15 minutes
            mHasLoaded = false;
            launchLockScreen();
        } else if (lastActiveTimeGap > mMinOfficerLockTime) {//15 minutes
            mHasLoaded = false;
            AuthUtils.logout(mContext);
            launchLockScreen();
        } else if (!AuthUtils.isLoggedIn(mContext)) {//if not logged In
            mHasLoaded = false;
            launchLockScreen();
        } else {
            mHasLoaded = true;
            AuthUtils.writeLockTime(mContext);
        }
    }

    public void setupNavDrawer() {
        //Setup the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);

            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            if (drawer != null) {
                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                drawer.addDrawerListener(toggle);
                toggle.syncState();
            }
        }
        NavigationView navigationView = findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_members:
                // Handle the member action
                startActivity(new Intent(this, MemberListActivity.class));
                break;
            case R.id.nav_reports:
                startActivity(new Intent(this, ReportActivity.class));
                break;
            case R.id.nav_officer:
                startActivity(new Intent(this, OfficerListActivity.class));
                break;
            case R.id.nav_logout:
                AuthUtils.logout(getApplicationContext());
                launchLockScreen();
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    private void setOfficer() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        if (navigationView != null) {
            View navHeader = navigationView.getHeaderView(0);
            TextView lblOfficer = navHeader.findViewById(R.id.officer_nav_header);
            if (lblOfficer != null && AuthUtils.isLoggedIn(navigationView.getContext())) {
                lblOfficer.setText(AuthUtils.getAuthenticatedOfficer(navigationView.getContext()).getName());
            }
        }
    }

    protected void skipLockOnce() {
        mSkipLockOnce = true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


}
