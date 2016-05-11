package com.razikallayi.suraksha;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.razikallayi.suraksha.utils.SettingsUtils;

public abstract class BaseActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener
{

    private static int PATTERNLOCK_UNLOCK = 42;

    protected boolean mSkipLockOnce = false;

    private SharedPreferences mPrefs;

    private SharedPreferences.Editor mEditor;

    private boolean mHasLoaded = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//    setTheme(R.style.Theme_Suraksha);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!SettingsUtils.isLoggedIn(getApplicationContext())) {
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            finish();
            return;
        }

    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        //TODO: Reset inactivity check timer
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
        // activity was open. If it's been more than two seconds the user
        // will have to enter the lock pattern to continue.
        long currentTime = SystemClock.elapsedRealtime();
        long lockedAt = mPrefs.getLong("locked_at", currentTime - 10000);
        long timedif = Math.abs(currentTime - lockedAt);
        if (timedif > 2000) {
            mHasLoaded = false;
            Toast.makeText(BaseActivity.this, "show pinactivity 1", Toast.LENGTH_SHORT).show();
        } else {
            mHasLoaded = true;
        }
    }

    private void writeLockTime() {
        writeLockTime(SystemClock.elapsedRealtime());
    }

    private void writeLockTime(long time) {
        mEditor = mPrefs.edit();
        mEditor.putLong("locked_at", time);
        mEditor.commit();
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == PATTERNLOCK_UNLOCK) {
            if (resultCode == RESULT_OK) {
                writeLockTime();
            } else {
                Toast.makeText(BaseActivity.this, "show pinactivity 2", Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void skipLockOnce() {
        mSkipLockOnce = true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key != null && key.equals(SettingsUtils.PREF_IS_LOGGED_IN)) {
            if(!SettingsUtils.isLoggedIn(getApplicationContext())){
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
                return;
            }
        }
    }
}
