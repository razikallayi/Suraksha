package com.razikallayi.suraksha.dailyTxn;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.razikallayi.suraksha.BaseActivity;
import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.data.SurakshaContract;
import com.razikallayi.suraksha.txn.Transaction;

import java.util.Calendar;


public class DailyTransactionActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor>  {


    private static final int CURSOR_LOADER = 0;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private DailyTransactionAdapter mDailyTxnAdapter;
    //private CursorPagerAdapter mCursorPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    public static final int TXN_DETAIL_LOADER = 0;




    // The callbacks through which we will interact with the LoaderManager.
    private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_transaction);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

/*
        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putLong(DailyTransactionFragment.ARG_DATE, System.currentTimeMillis());
            DailyTransactionFragment fragment = new DailyTransactionFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.view_pager_container, fragment)
                    .commit();
        }
*/
        mDailyTxnAdapter = new DailyTransactionAdapter(getSupportFragmentManager(), Transaction.TxnQuery.PROJECTION,null);
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.view_pager_container);
        mViewPager.setAdapter(mDailyTxnAdapter);

         /*
         * Initializes the CursorLoader. The URL_LOADER value is eventually passed
         * to onCreateLoader().
         */
        getSupportLoaderManager().initLoader(CURSOR_LOADER, null, this);

    }
    /*
   * Callback that's invoked when the system has initialized the Loader and
   * is ready to start the query. This usually happens when initLoader() is
   * called. The loaderID argument contains the ID value passed to the
   * initLoader() call.
   */
    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle)
    {
    /*
     * Takes action based on the ID of the Loader that's being created
     */
        switch (loaderID) {
            case CURSOR_LOADER:
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                Log.d("FISH", "onCreateLoader: "+calendar.getTimeInMillis());
                // Returns a new CursorLoader
                return new CursorLoader(
                        this,   // Parent activity context
                        SurakshaContract.TxnEntry.buildTxnOnDate(calendar.getTimeInMillis()),// Table to query
                        Transaction.TxnQuery.PROJECTION,     // Projection to return
                        null,            // No selection clause
                        null,            // No selection arguments
                        null             // Default sort order
                );
            default:
                // An invalid id was passed in
                return null;
        }
    }

    /*
 * Defines the callback that CursorLoader calls
 * when it's finished its query
 */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
    /*
     * Moves the query results into the adapter, causing the
     * ListView fronting this adapter to re-display
     */
        mDailyTxnAdapter.swapCursor(cursor);
        if (cursor != null && cursor.moveToFirst()) {
            Log.d("FISH", "Amount: " + cursor.getString(Transaction.TxnQuery.COL_AMOUNT));
            Log.d("FISH", "Account: " + cursor.getString(Transaction.TxnQuery.COL_FK_ACCOUNT_NUMBER));
            Log.d("FISH", "ledger: " + cursor.getString(Transaction.TxnQuery.COL_LEDGER));
            Log.d("FISH", "voucher type: " + cursor.getString(Transaction.TxnQuery.COL_VOUCHER_TYPE));
        }
    }

    /*
 * Invoked when the CursorLoader is being reset. For example, this is
 * called if the data in the provider changes and the Cursor becomes stale.
 */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    /*
     * Clears out the adapter's reference to the Cursor.
     * This prevents memory leaks.
     */
        mDailyTxnAdapter.swapCursor(null);
    }

}
