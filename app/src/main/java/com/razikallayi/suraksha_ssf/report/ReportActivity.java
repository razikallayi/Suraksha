package com.razikallayi.suraksha_ssf.report;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.razikallayi.suraksha_ssf.BaseActivity;
import com.razikallayi.suraksha_ssf.R;

import java.util.Calendar;

public class ReportActivity extends BaseActivity {
    protected static final int NAV_DAILY = 0x111;
    protected static final int NAV_MONTHLY = 0x112;
    protected static final int NAV_YEARLY = 0x113;
    protected Fragment activeFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_daily:
                    changeFragment(NAV_DAILY);
                    return true;
                case R.id.nav_monthly:
                    changeFragment(NAV_MONTHLY);
                    return true;
                case R.id.nav_yearly:
                    changeFragment(NAV_YEARLY);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        setupEnvironment();
        BottomNavigationView navigation = findViewById(R.id.navigation);
        if (navigation != null) {
            navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//            disableShiftMode(navigation);
        }
        if (null == savedInstanceState) {
            changeFragment(NAV_DAILY);
        } else {
            activeFragment = getSupportFragmentManager().getFragment(savedInstanceState, "activeFragment");
        }

    }

//    public static void disableShiftMode(BottomNavigationView view) {
//        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
//        try {
//            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
//            shiftingMode.setAccessible(true);
//            shiftingMode.setBoolean(menuView, false);
//            shiftingMode.setAccessible(false);
//            for (int i = 0; i < menuView.getChildCount(); i++) {
//                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
//                //noinspection RestrictedApi
//                item.setShiftingMode(false);
//                // set once again checked value, so view will be updated
//                //noinspection RestrictedApi
//                item.setChecked(item.getItemData().isChecked());
//            }
//        } catch (NoSuchFieldException e) {
//            Log.e("BNVHelper", "Unable to get shift mode field", e);
//        } catch (IllegalAccessException e) {
//            Log.e("BNVHelper", "Unable to change value of shift mode", e);
//        }
//    }

    protected void changeFragment(int number) {
        Fragment fragment = null;
        switch (number) {
            case NAV_DAILY:
                fragment = PeriodicalTxnReportFragment.newInstance(System.currentTimeMillis(), Calendar.DATE);
                break;
            case NAV_MONTHLY:
                fragment = PeriodicalTxnReportFragment.newInstance(System.currentTimeMillis(), Calendar.MONTH);
                break;
            case NAV_YEARLY:
                fragment = PeriodicalTxnReportFragment.newInstance(System.currentTimeMillis(), Calendar.YEAR);
                break;
        }
        activeFragment = fragment;
        getSupportFragmentManager().beginTransaction().replace(
                R.id.fragmentHolder, fragment)
                .commit();
    }

    private void setupEnvironment() {
        //Setup the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

}
