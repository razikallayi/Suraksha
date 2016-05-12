package com.razikallayi.suraksha;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.razikallayi.suraksha.utils.SettingsUtils;

public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//
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

        //Setup the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.suraksha_logo_name);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Button btnTxnReport = (Button) findViewById(R.id.btnTxnReport);
        btnTxnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TxnReportActivity.class);
                startActivity(intent);
            }
        });

        Button btnMemberList = (Button) findViewById(R.id.btnMemberList);
        btnMemberList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MemberListActivity.class);
                startActivity(intent);
            }
        });



        Button btnCreateUser = (Button) findViewById(R.id.btnCreateUser);
        btnCreateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),CreateOfficerActivity.class));
            }
        });


        Button btnLogout = (Button) findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsUtils.setLoggedIn(getApplicationContext(),false);
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
                return;
            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_members) {
            // Handle the register member action
            Intent intent = new Intent(this,MemberListActivity.class);
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
