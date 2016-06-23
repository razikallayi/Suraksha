
package com.razikallayi.suraksha;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.razikallayi.suraksha.member.MemberListActivity;
import com.razikallayi.suraksha.officer.OfficerListActivity;
import com.razikallayi.suraksha.report.TxnReportActivity;
import com.razikallayi.suraksha.utils.AuthUtils;
import com.razikallayi.suraksha.utils.SettingsUtils;

public abstract class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    protected static int LOCK_SCREEN_REQUEST = 100;
    protected boolean mSkipLockOnce = false;
    private boolean mHasLoaded = false;
    private Context mContext;

    private static final int FIVE_SECONDS = 5000; //5 seconds
    private static final int FIFTEEN_MINUTES = 900000; //15 minutes


    protected static final int mMinLockTime = FIFTEEN_MINUTES; // TODO: 17-06-2016 change to five seconds on Production
    protected static final int mMinOfficerLockTime = FIFTEEN_MINUTES;

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
            NavUtils.navigateUpFromSameTask(this);
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
        startActivityForResult(new Intent(mContext, LoginActivity.class), LOCK_SCREEN_REQUEST);
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
        }
    }

    public void setupNavDrawer() {
        //Setup the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setLogo(R.drawable.suraksha_logo_name);
            toolbar.setTitle("");
            setSupportActionBar(toolbar);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer != null) {
                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                drawer.addDrawerListener(toggle);
                toggle.syncState();


                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                if (navigationView != null) {
                    navigationView.setNavigationItemSelectedListener(this);
                }
            }
        }
    }

    protected void skipLockOnce() {
        mSkipLockOnce = true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d("FISH Key", "onSharedPreferenceChanged: " + key);
        if (key != null) {
            if (key.equals(SettingsUtils.PREF_IS_LOGGED_IN)) {
                if (!AuthUtils.isLoggedIn(mContext)) {
                    Toast.makeText(BaseActivity.this, "Log in changed", Toast.LENGTH_SHORT).show();
                    AuthUtils.logout(mContext);
                    startActivity(new Intent(mContext, LoginActivity.class));
                    finish();
                }
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_members) {
            // Handle the register member action
            Intent intent = new Intent(this, MemberListActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_reports) {
            Intent intent = new Intent(this, TxnReportActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_officer) {
            Intent intent = new Intent(this, OfficerListActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
