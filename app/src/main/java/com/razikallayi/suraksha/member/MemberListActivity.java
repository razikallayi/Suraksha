package com.razikallayi.suraksha.member;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.razikallayi.suraksha.BaseActivity;
import com.razikallayi.suraksha.FragmentViewPagerAdapter;
import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.data.SurakshaContract;

/**
 * An activity representing a list of Members. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MemberDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MemberListActivity extends BaseActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
        SearchView.OnQueryTextListener {

    private static final int MEMBER_LIST_LOADER = 0;
    private static final String[] MEMBER_COLUMNS = {
            SurakshaContract.MemberEntry.TABLE_NAME + "." + SurakshaContract.MemberEntry._ID,
            SurakshaContract.MemberEntry.COLUMN_NAME,
            SurakshaContract.MemberEntry.COLUMN_ADDRESS,
            SurakshaContract.MemberEntry.COLUMN_AVATAR,
            SurakshaContract.MemberEntry.COLUMN_ACCOUNT_NO,
            SurakshaContract.MemberEntry.COLUMN_CLOSED_AT,
    };
    private static final int COL_MEMBER_ID = 0;
    private static final int COL_MEMBER_NAME = 1;
    private static final int COL_MEMBER_ADDRESS = 2;
    private static final int COL_MEMBER_AVATAR = 3;
    private static final int COL_MEMBER_ACCOUNT_NO = 4;
    private static final int COL_CLOSED_AT = 5;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private MemberListAdapter mMemberListAdapter;
    // If non-null, this is the current filter the user has provided.
    private String mCurFilter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_list_activity);
// TODO: 28-06-2016 check if save instance state is null. [Ref]See resposnsive ui->40.Built 2pane tablet ui
        //Setup the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (toolbar != null) {
            toolbar.setTitle(getTitle());
        }

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        if (fab != null) {
//            fab.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    // Handle the register member action
//                    Intent intent = new Intent(getApplicationContext(), RegisterMemberActivity.class);
//                    startActivity(intent);
//                }
//            });
//        }

        if (findViewById(R.id.member_list_and_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }


        // The MemberListAdapter will take data from a source and
        // use it to populate the ListView it's attached to.
        mMemberListAdapter = new MemberListAdapter();
        final RecyclerView recyclerView = findViewById(R.id.member_list);

        mMemberListAdapter.setOnItemClickListener(new MemberListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, long memberId, String memberName) {
                if (mTwoPane) {
                    RecyclerView.State state = new RecyclerView.State();


                    ViewPager viewPager = findViewById(R.id.member_detail_container);
                    if (viewPager != null) {
                        setupViewPager(viewPager, memberId);
                    }

                } else {
                    Intent intent = new Intent(getApplicationContext(), MemberDetailActivity.class);
                    intent.putExtra(MemberDetailActivity.ARG_MEMBER_ID, memberId);
                    intent.putExtra(MemberDetailActivity.ARG_MEMBER_NAME, memberName);
                    startActivity(intent);
                }

            }
        });

        if (recyclerView != null) {
            recyclerView.setAdapter(mMemberListAdapter);
        }

        getSupportLoaderManager().initLoader(MEMBER_LIST_LOADER, null, this);
    }

    private void setupViewPager(ViewPager viewPager, long memberId) {
        FragmentViewPagerAdapter adapter = new FragmentViewPagerAdapter(getSupportFragmentManager());

        MemberSummeryFragment memberSummeryFragment = (MemberSummeryFragment)
                getSupportFragmentManager().findFragmentByTag(MemberSummeryFragment.TAG);
        if (null == memberSummeryFragment) {
            Bundle arguments = new Bundle();
            //MemberSummeryFragment
            arguments.putLong(MemberSummeryFragment.ARG_MEMBER_ID, memberId);
            memberSummeryFragment = new MemberSummeryFragment();
            memberSummeryFragment.setArguments(arguments);
            adapter.addFragment(memberSummeryFragment, "Account");
        }


        MemberDetailFragment memberDetailFragment = (MemberDetailFragment)
                getSupportFragmentManager().findFragmentByTag(MemberDetailFragment.TAG);
        if (memberDetailFragment == null) {
            Bundle arguments = new Bundle();
            //MemberDetails
            arguments.putLong(MemberDetailFragment.ARG_MEMBER_ID, memberId);
            memberDetailFragment = new MemberDetailFragment();
            memberDetailFragment.setArguments(arguments);
            adapter.addFragment(memberDetailFragment, "Personal");
        }
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView mSearchView = (SearchView) menu.findItem(R.id.search).getActionView();
        mSearchView.setOnQueryTextListener(this);
        // Assumes current activity is the searchable activity
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setIconifiedByDefault(true); // Do not iconify the widget; expand it by default
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // Called when the action bar search text has changed.  Update
        // the search filter, and restart the loader to do a new query
        // with this filter.
        String newFilter = !TextUtils.isEmpty(newText) ? newText : null;
        // Don't do anything if the filter hasn't actually changed.
        // Prevents restarting the loader when restoring state.
        if (mCurFilter == null && newFilter == null) {
            return true;
        }
        if (mCurFilter != null && mCurFilter.equals(newFilter)) {
            return true;
        }
        //When search is blank load all list
        if (mCurFilter != null && newFilter == null) {
            mCurFilter = null;
            getSupportLoaderManager().restartLoader(MEMBER_LIST_LOADER, null, this);
            return true;
        }
        mCurFilter = newFilter;
        Bundle queryBundle = new Bundle();
        queryBundle.putString("query", mCurFilter);
        getSupportLoaderManager().restartLoader(MEMBER_LIST_LOADER, queryBundle, this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // Don't care about this.
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (args != null) {
            //Used for searching
            return new CursorLoader(getApplicationContext(),
                    SurakshaContract.MemberEntry.CONTENT_URI,
                    MEMBER_COLUMNS,

                    SurakshaContract.MemberEntry.COLUMN_NAME
                            + " LIKE ? OR " + SurakshaContract.MemberEntry.COLUMN_ADDRESS
                            + " LIKE ? OR " + SurakshaContract.MemberEntry.COLUMN_ACCOUNT_NO + "=?",

                    new String[]{String.valueOf("%" + args.get("query") + "%"), String.valueOf("%"
                            + args.get("query") + "%"), String.valueOf(args.get("query"))},

                    SurakshaContract.MemberEntry.TABLE_NAME + "." + SurakshaContract.MemberEntry._ID
                            + " COLLATE NOCASE");
        } else {
            return new CursorLoader(getApplicationContext(),
                    SurakshaContract.MemberEntry.CONTENT_URI,
                    MEMBER_COLUMNS, null, null,
                    SurakshaContract.MemberEntry._ID.concat(" COLLATE NOCASE"));
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMemberListAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMemberListAdapter.swapCursor(null);
    }
}