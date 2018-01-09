package com.razikallayi.suraksha.account;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.razikallayi.suraksha.BaseActivity;
import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.loan.LoanIssuedFragment;
import com.razikallayi.suraksha.member.MemberListActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a single Member detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link MemberListActivity}.
 */
public class AccountDetailActivityBottom extends BaseActivity {
    public static final String ARG_ACCOUNT_NUMBER = "account_number";
    int mAccountNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_details);

        //Setup the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

//        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
//        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        ViewPager viewPager = findViewById(R.id.account_detail_container);
        if (viewPager != null) {
            mAccountNumber = getIntent().getIntExtra(ARG_ACCOUNT_NUMBER, -1);
            setupViewPager(viewPager, mAccountNumber);
        }
        TabLayout tabLayout = findViewById(R.id.tabs);
        if (viewPager != null && tabLayout != null) {
            tabLayout.setupWithViewPager(viewPager);
        }




//        int accountNumber = getIntent().getIntExtra("account_number",-1);
//        TxnListFragment txnListFragment = TxnListFragment.newInstance(accountNumber);
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.fragment_txn_list, txnListFragment)
//                .commit();

        //Load RecyclerView of Due
        //getSupportLoaderManager().initLoader(1,null,this);

//        Button btnMakeDeposit = (Button) findViewById(R.id.btnMakeDeposit);
//        btnMakeDeposit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Calendar calendar = Calendar.getInstance();
//                int accountNumber = getIntent().getIntExtra("account_number",-1);
//                if(accountNumber>0) {
//                    Account.makeDeposit(getApplicationContext(), accountNumber, calendar.MONTH, "");
//                }
//            }
//        });
//
//        Button showTxn = (Button) findViewById(R.id.show_txn);
//        showTxn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int accountNumber = getIntent().getIntExtra("account_number", -1);
//                TextView tvTransactions = (TextView) findViewById(R.id.textView5);
//                Cursor cursor = getContentResolver().query(
//                        SurakshaContract.TxnEntry.buildTxnOfAccountUri(accountNumber), null, null, null, null);
//                cursor.moveToFirst();
//                tvTransactions.setText(DatabaseUtils.dumpCursorToString(cursor));
//                cursor.close();
//            }
//        });

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

        }

    }

    private void setupViewPager(ViewPager viewPager, int accountNumber) {
        Adapter adapter = new Adapter(getSupportFragmentManager());

        LoanIssuedFragment depositFragment = (LoanIssuedFragment)
                getSupportFragmentManager().findFragmentByTag(LoanIssuedFragment.TAG);
        if (null == depositFragment) {
            Bundle arguments = new Bundle();
            arguments.putInt(LoanIssuedFragment.ARG_ACCOUNT_NUMBER, accountNumber);
            //AccountList
            depositFragment = new LoanIssuedFragment();
            depositFragment.setArguments(arguments);
//        getFragmentManager().beginTransaction()
//                .replace(R.id.account_list_container, accountListFragment)
//                .commit();
            adapter.addFragment(depositFragment, "Deposit");
        }
        viewPager.setAdapter(adapter);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
//            = new BottomNavigationView.OnNavigationItemSelectedListener() {
//
//        @Override
//        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            switch (item.getItemId()) {
//                case R.id.navigation_home:
//                    mTextMessage.setText("R.string.title_home");
//                    return true;
//                case R.id.navigation_dashboard:
//                    mTextMessage.setText("R.string.title_dashboard");
//                    return true;
//                case R.id.navigation_notifications:
//                    mTextMessage.setText("R.string.title_notifications");
//                    return true;
//            }
//            return false;
//        }
//
//    };

//
//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        int accountNumber = getIntent().getIntExtra("account_number",-1);
//        return new CursorLoader(getApplicationContext(),
//                SurakshaContract.TxnEntry.buildFetchAllDepositsUri(),
//                Transaction.TXN_COLUMNS,
//                SurakshaContract.TxnEntry.COLUMN_LEDGER +"= ? AND "+ SurakshaContract.TxnEntry.COLUMN_FK_ACCOUNT_NUMBER +"= ?",
//                new String[]{String.valueOf(SurakshaContract.TxnEntry.DEPOSIT_LEDGER), String.valueOf(accountNumber)},
//                null);
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
//        ArrayList<Long> depositedDates = new ArrayList<>();
//        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
//            depositedDates.add(cursor.getLong(Transaction.COL_DEFINED_DEPOSIT_DATE));
//        }
//        cursor.close();
//        RecyclerView recyclerViewDue = (RecyclerView) findViewById(R.id.due_list);
//        LinearLayoutManager llm = new LinearLayoutManager(this);
//        llm.setOrientation(LinearLayoutManager.VERTICAL);
//        recyclerViewDue.setLayoutManager(llm);
//        recyclerViewDue.setAdapter(new DueRecyclerViewAdapter(Utility.getPendingDepositMonths(depositedDates)));
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> loader) {
//
//    }
}
