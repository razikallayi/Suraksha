package com.razikallayi.suraksha;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.razikallayi.suraksha.member.Member;
import com.razikallayi.suraksha.member.MemberListActivity;
import com.razikallayi.suraksha.member.RegisterMemberActivity;
import com.razikallayi.suraksha.officer.CreateOfficerActivity;
import com.razikallayi.suraksha.report.TxnReportActivity;
import com.razikallayi.suraksha.txn.Transaction;
import com.razikallayi.suraksha.utils.AuthUtils;
import com.razikallayi.suraksha.utils.FontUtils;

public class HomeActivity extends BaseActivity {

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

        Button btnTxnReport = (Button) findViewById(R.id.btnTxnReport);
        btnTxnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TxnReportActivity.class);
                startActivity(intent);
            }
        });

        TextView member_details_fa_icon = (TextView) findViewById(R.id.members_fa_icon);
        member_details_fa_icon.setTypeface(FontUtils.getTypeface(getApplicationContext(), FontUtils.FONTAWSOME));

        TextView member_add_fa_icon = (TextView) findViewById(R.id.member_add_fa_icon);
        member_add_fa_icon.setTypeface(FontUtils.getTypeface(getApplicationContext(), FontUtils.FONTAWSOME));

        TextView officer_fa_icon = (TextView) findViewById(R.id.officer_fa_icon);
        officer_fa_icon.setTypeface(FontUtils.getTypeface(getApplicationContext(), FontUtils.FONTAWSOME));

        TextView reports_fa_icon = (TextView) findViewById(R.id.reports_fa_icon);
        reports_fa_icon.setTypeface(FontUtils.getTypeface(getApplicationContext(), FontUtils.FONTAWSOME));

        TextView debug_fa_icon = (TextView) findViewById(R.id.debug_fa_icon);
        debug_fa_icon.setTypeface(FontUtils.getTypeface(getApplicationContext(), FontUtils.FONTAWSOME));

        TextView lock_fa_icon = (TextView) findViewById(R.id.lock_fa_icon);
        lock_fa_icon.setTypeface(FontUtils.getTypeface(getApplicationContext(), FontUtils.FONTAWSOME));


        Button btnMemberList = (Button) findViewById(R.id.btnMemberList);
        btnMemberList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MemberListActivity.class);
                startActivity(intent);
            }
        });


        Button btnLogout = (Button) findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthUtils.logout(getApplicationContext());
                launchLockScreen();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        boolean isAdmin = AuthUtils.isAdmin(getApplicationContext());
        boolean isDeveloper = AuthUtils.isDeveloper(getApplicationContext());

        Button btnAddMember = (Button) findViewById(R.id.btnRegistration);
        ((View) btnAddMember.getParent()).setVisibility(View.GONE);

        Button btnCreateOfficer = (Button) findViewById(R.id.btnCreateOfficer);
        ((View) btnCreateOfficer.getParent()).setVisibility(View.GONE);

        View statusCard=findViewById(R.id.statusCard);
        statusCard.setVisibility(View.GONE);
        if (isAdmin || isDeveloper) {
            ((View) btnAddMember.getParent()).setVisibility(View.VISIBLE);
            btnAddMember.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), RegisterMemberActivity.class);
                    startActivity(intent);
                }
            });

            ((View) btnCreateOfficer.getParent()).setVisibility(View.VISIBLE);
            btnCreateOfficer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), CreateOfficerActivity.class));
                }
            });

            //Set content for content_base layout. This view is first seen when app is opened.
            TextView tvActiveMembersCount = (TextView) findViewById(R.id.tvActiveMembers);
            tvActiveMembersCount.setText(String.valueOf(Member.getActiveMembersCount(getApplicationContext())));

            TextView tvWmf = (TextView) findViewById(R.id.tvWmf);
            tvWmf.setText(getString(R.string.format_rupees, Transaction.getWmf(this)));

            TextView tvTotalDeposit = (TextView) findViewById(R.id.tvTotalDeposit);
            tvTotalDeposit.setText(getString(R.string.format_rupees, Transaction.getTotalDeposit(this)));

            TextView tvTotalLoanPayed = (TextView) findViewById(R.id.tvTotalLoanPayed);
            tvTotalLoanPayed.setText(getString(R.string.format_rupees, Transaction.getTotalLoanPayed(this)));

            TextView tvTotalLoanReturn = (TextView) findViewById(R.id.tvTotalLoanReturn);
            tvTotalLoanReturn.setText(getString(R.string.format_rupees, Transaction.getTotalLoanReturn(this)));
            statusCard.setVisibility(View.VISIBLE);
        }

        Button btnDebug = (Button) findViewById(R.id.btnDebug);
        ((View) btnDebug.getParent()).setVisibility(View.GONE);
        if (isDeveloper) {
            ((View) btnDebug.getParent()).setVisibility(View.VISIBLE);
            btnDebug.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), DebugActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
