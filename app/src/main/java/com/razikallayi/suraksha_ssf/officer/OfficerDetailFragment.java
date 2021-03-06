package com.razikallayi.suraksha_ssf.officer;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.razikallayi.suraksha_ssf.R;
import com.razikallayi.suraksha_ssf.data.SurakshaContract;
import com.razikallayi.suraksha_ssf.member.MemberDetailActivity;
import com.razikallayi.suraksha_ssf.member.MemberListActivity;
import com.razikallayi.suraksha_ssf.utils.CalendarUtils;

/**
 * A fragment representing a single Member detail screen.
 * This fragment is either contained in a {@link MemberListActivity}
 * in two-pane mode (on tablets) or a {@link MemberDetailActivity}
 * on handsets.
 */
public class OfficerDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String TAG = OfficerDetailFragment.class.getSimpleName();
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_OFFICER_ID = "officer_id";

    public static final int OFFICER_DETAIL_LOADER = 0;

    private TextView mOfficerIsAdmin;
    private TextView mOfficerName;
    private TextView mOfficerMobile;
    private TextView mOfficerAddress;
    private TextView mOfficerCreatedAt;
    private TextView mOfficerUpdatedAt;


    private static final String[] OFFICER_COLUMNS = new String[] {
            SurakshaContract.OfficerEntry.COLUMN_NAME,
            SurakshaContract.OfficerEntry.COLUMN_MOBILE,
            SurakshaContract.OfficerEntry.COLUMN_ADDRESS,
            SurakshaContract.OfficerEntry.COLUMN_CREATED_AT,
            SurakshaContract.OfficerEntry.COLUMN_UPDATED_AT,
            SurakshaContract.OfficerEntry.COLUMN_IS_ADMIN
    };

    // these indices must match the projection
    private static final int COL_NAME                  =  0;
    private static final int COL_MOBILE                =  1;
    private static final int COL_ADDRESS               =  2;
    private static final int COL_CREATED_AT            =  3;
    private static final int COL_UPDATED_AT            =  4;
    private static final int COL_IS_ADMIN              =  5;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public OfficerDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

//        if (getArguments().containsKey(ARG_OFFICER_ID)) {
//            Activity activity = this.getActivity();
//            CollapsingToolbarLayout appBarLayout =
//                    (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
//            if (appBarLayout != null) {
//                appBarLayout.setTitle("Details");
//            }
//        }
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(OFFICER_DETAIL_LOADER, getArguments(), this);


//        //AccountList
//        Bundle arguments = new Bundle();
//        arguments.putString(AccountListFragment.ARG_OFFICER_ID,
//                getArguments().getString(MemberDetailFragment.ARG_OFFICER_ID));
//        AccountListFragment accountListFragment = new AccountListFragment();
//        accountListFragment.setArguments(arguments);
//        getFragmentManager().beginTransaction()
//                .replace(R.id.account_list_container, accountListFragment)
//                .commit();

        super.onActivityCreated(savedInstanceState);

    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_officer_details_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.edit_officer) {
            Intent editOfficer = new Intent(getContext(),EditOfficerActivity.class);
            editOfficer.putExtra(EditOfficerActivity.ARG_OFFICER_ID,
                    getArguments().getLong(ARG_OFFICER_ID));
            startActivity(editOfficer);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.officer_detail_fragment, container, false);


        mOfficerIsAdmin = rootView.findViewById(R.id.tvIsAdmin);
        mOfficerName = rootView.findViewById(R.id.tvOfficerName);
        mOfficerMobile = rootView.findViewById(R.id.tvOfficerMobile);
        mOfficerAddress = rootView.findViewById(R.id.tvOfficerAddress);
        mOfficerCreatedAt = rootView.findViewById(R.id.tvOfficerCreatedAt);
        mOfficerUpdatedAt = rootView.findViewById(R.id.tvOfficerUpdatedAt);

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(final int loaderId, Bundle args) {
        switch (loaderId) {
            case OFFICER_DETAIL_LOADER:
                return new CursorLoader(getContext(),
                        SurakshaContract.OfficerEntry.buildOfficerUri(args
                                .getLong(ARG_OFFICER_ID)),
                        OFFICER_COLUMNS,
                        null,
                        null,
                        null);
            default:
                // An invalid id was passed in
                return null;
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case OFFICER_DETAIL_LOADER:
                if (data != null && data.moveToFirst()) {
                    mOfficerName.setText(data.getString(COL_NAME));
                    mOfficerMobile.setText(data.getString(COL_MOBILE));
                    mOfficerAddress.setText(data.getString(COL_ADDRESS));
                    mOfficerCreatedAt.setText(CalendarUtils.formatDateTime(data.getLong(COL_CREATED_AT)));
                    mOfficerUpdatedAt.setText(CalendarUtils.formatDateTime(data.getLong(COL_UPDATED_AT)));
                    if(data.getInt(COL_IS_ADMIN) == 1){
                        mOfficerIsAdmin.setVisibility(View.VISIBLE);
                    }else {
                        mOfficerIsAdmin.setVisibility(View.GONE);
                    }
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
