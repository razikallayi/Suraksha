package com.razikallayi.suraksha_ssf;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.GridLayout;

import com.razikallayi.suraksha_ssf.member.MemberListActivity;
import com.razikallayi.suraksha_ssf.member.RegisterMemberActivity;
import com.razikallayi.suraksha_ssf.officer.OfficerListActivity;
import com.razikallayi.suraksha_ssf.report.ReportActivity;
import com.razikallayi.suraksha_ssf.report.TxnsActivity;
import com.razikallayi.suraksha_ssf.utils.AuthUtils;

public class HomeActivity extends BaseActivity {

    private View membersIcon;
    private View addMemberIcon;
    private View officersIcon;
    private View reportsIcon;
    private View debugIcon;
    private View logoutIcon;
    private View txnsIcon;
    private GridLayout iconHolderGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//                .detectDiskReads()
//                .detectDiskWrites()
//                .detectAll()   // or .detectAll() for all detectable problems
//                .penaltyLog()
//                .build());
//        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//                .detectLeakedSqlLiteObjects()
//                .detectLeakedClosableObjects()
//                .penaltyLog()
//                .penaltyDeath()
//                .build());

        setContentView(R.layout.activity_home);

        setupNavDrawer();

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setLogo(R.drawable.suraksha_logo_name);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        iconHolderGrid = findViewById(R.id.iconHolderGrid);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            iconHolderGrid.setColumnCount(2);
        } else {
            iconHolderGrid.setColumnCount(3);
        }
        membersIcon = iconHolderGrid.findViewById(R.id.membersIcon);
        addMemberIcon = iconHolderGrid.findViewById(R.id.addMemberIcon);
        officersIcon = iconHolderGrid.findViewById(R.id.officersIcon);
        reportsIcon = iconHolderGrid.findViewById(R.id.reportsIcon);
        txnsIcon = iconHolderGrid.findViewById(R.id.txns_icon);
        debugIcon = iconHolderGrid.findViewById(R.id.debugIcon);
        logoutIcon = iconHolderGrid.findViewById(R.id.logoutIcon);

        membersIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MemberListActivity.class);
                startActivity(intent);
            }
        });


        officersIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), OfficerListActivity.class));
            }
        });

        reportsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ReportActivity.class);
                startActivity(intent);
            }
        });

        txnsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TxnsActivity.class);
                startActivity(intent);
            }
        });

        logoutIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), DailyTransactionActivity.class);
//                startActivity(intent);
                AuthUtils.logout(getApplicationContext());
                launchLockScreen();
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            iconHolderGrid.setColumnCount(3);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            iconHolderGrid.setColumnCount(2);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean isAdmin = AuthUtils.isAdmin(getApplicationContext());
        boolean isDeveloper = AuthUtils.isDeveloper(getApplicationContext());

        iconHolderGrid.removeView(addMemberIcon);
        addMemberIcon.setVisibility(View.GONE);
//        View statusCard=findViewById(R.id.statusCard);
//        statusCard.setVisibility(View.GONE);
        if (isAdmin || isDeveloper) {
            addMemberIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), RegisterMemberActivity.class);
                    startActivity(intent);
                }
            });

  /*          //Set content for content_base layout. This view is first seen when app is opened.
            TextView tvActiveMembersCount = findViewById(R.id.tvActiveMembers);
            tvActiveMembersCount.setText(String.valueOf(Member.getActiveMembersCount(getApplicationContext())));

            TextView tvWmf = findViewById(R.id.tvWmf);
            tvWmf.setText(getString(R.string.format_rupees, Transaction.getWmf(this)));

            TextView tvTotalDeposit = findViewById(R.id.tvTotalDeposit);
            tvTotalDeposit.setText(getString(R.string.format_rupees, Transaction.getTotalDeposit(this)));

            TextView tvTotalLoanPayed = findViewById(R.id.tvTotalLoanPayed);
            tvTotalLoanPayed.setText(getString(R.string.format_rupees, Transaction.getTotalLoanPayed(this)));

            TextView tvTotalLoanReturn = findViewById(R.id.tvTotalLoanReturn);
            tvTotalLoanReturn.setText(getString(R.string.format_rupees, Transaction.getTotalLoanReturn(this)));
            statusCard.setVisibility(View.VISIBLE);*/
            addMemberIcon.setVisibility(View.VISIBLE);
            iconHolderGrid.addView(addMemberIcon,1);
        }

        iconHolderGrid.removeView(debugIcon);
        debugIcon.setVisibility(View.GONE);
        if (isDeveloper) {
            debugIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), DebugActivity.class);
                    startActivity(intent);
                }
            });
            iconHolderGrid.addView(debugIcon,5);
            debugIcon.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
