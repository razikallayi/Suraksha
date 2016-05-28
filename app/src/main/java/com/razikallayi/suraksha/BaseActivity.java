package com.razikallayi.suraksha;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.razikallayi.suraksha.utils.AuthUtils;
import com.razikallayi.suraksha.utils.SettingsUtils;

public abstract class BaseActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    protected static int LOCK_SCREEN_REQUEST = 100;
    protected boolean mSkipLockOnce = false;
    private boolean mHasLoaded = false;
    private Context mContext;

    protected static final int mMinLockTime = 5000; //5 seconds
    //    protected static final int mMinOfficerLockTime = 15000; //15 sec
    protected static final int mMinOfficerLockTime = 900000; //15 minutes

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
        inflater.inflate(R.menu.settings_menu, menu);//Add settings menu to every activity
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

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


    protected void skipLockOnce() {
        mSkipLockOnce = true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d("FISH", "onSharedPreferenceChanged: " + key);
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

}
