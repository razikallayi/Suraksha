package com.razikallayi.suraksha;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

/**
 * An activity representing a single Member detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link MemberListActivity}.
 */
public class AccountDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_details);

        //Setup the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }




        int accountNumber = getIntent().getIntExtra("account_number",-1);
        TxnListFragment txnListFragment = TxnListFragment.newInstance(accountNumber);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_txn_list, txnListFragment)
                .commit();

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



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

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
