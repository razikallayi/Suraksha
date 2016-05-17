package com.razikallayi.suraksha;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.razikallayi.suraksha.utils.LoginUtils;

public abstract class BaseActivity extends AppCompatActivity {

    protected static int LOCK_SCREEN_REQUEST = 100;
    protected boolean mSkipLockOnce = false;
    private SharedPreferences mPrefs;
    private boolean mHasLoaded = false;
    private Context mContext;

    protected static final int mMinLockTime = 5000; //5 seconds
    protected static final int mMinOfficerLockTime = 900000; //15 minutes


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mContext = getApplicationContext();
        if (!LoginUtils.isLoggedIn(mContext)) {
            launchLockScreen();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOCK_SCREEN_REQUEST) {
            if (resultCode == RESULT_OK) {
                writeLockTime();
            } else {
                finish();
            }
        }
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
            writeLockTime();
        } else {
            writeLockTime(SystemClock.elapsedRealtime() - 10000);
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
        long currentTime = SystemClock.elapsedRealtime();
        long lockedAt = mPrefs.getLong("locked_at", currentTime - 10000); //10 Seconds
        long timedif = Math.abs(currentTime - lockedAt);

        if (timedif > mMinLockTime && timedif <= mMinOfficerLockTime) { //between 5seconds and 15 minutes
            mHasLoaded = false;
            launchLockScreen();
        } else if (timedif > mMinOfficerLockTime) {//15 minutes
            mHasLoaded = false;
            LoginUtils.logout(mContext);
            launchLockScreen();
        } else {
            mHasLoaded = true;
        }
    }
    private void writeLockTime() {
        writeLockTime(SystemClock.elapsedRealtime());
    }
    private void writeLockTime(long time) {
        mPrefs.edit().putLong("locked_at", time).commit();
    }

    protected void skipLockOnce() {
        mSkipLockOnce = true;
    }

//    @Override
//    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//        if (key != null && key.equals(SettingsUtils.PREF_IS_LOGGED_IN)) {
//            if(!SettingsUtils.isLoggedIn(mContext)){
//                Toast.makeText(BaseActivity.this, "Log in changed", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(mContext,LoginActivity.class));
//                finish();
//                return;
//            }
//        }
//    }

}
