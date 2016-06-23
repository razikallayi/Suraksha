package com.razikallayi.suraksha.member;

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

import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.data.SurakshaContract;
import com.razikallayi.suraksha.utils.AuthUtils;
import com.razikallayi.suraksha.utils.CalendarUtils;

/**
 * A fragment representing a single Member detail screen.
 * This fragment is either contained in a {@link MemberListActivity}
 * in two-pane mode (on tablets) or a {@link MemberDetailActivity}
 * on handsets.
 */
public class MemberDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String TAG = MemberDetailFragment.class.getSimpleName();
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_MEMBER_ID = "member_id";

    private long mMemberId;

    public static final int MEMBER_DETAIL_LOADER = 0;

    private TextView mMemberName;
    private TextView mMemberAlias;
    private TextView mMemberGender;
    private TextView mMemberFather;
    private TextView mMemberSpouse;
    private TextView mMemberOccupation;
    private TextView mMemberAge;
    private TextView mMemberMobile;
    private TextView mMemberAddress;
    private TextView mMemberNominee;
    private TextView mMemberRelationWithNominee;
    private TextView mMemberAddressOfNominee;
    private TextView mMemberRemarks;
    //    private TextView mMemberClosedAt           ;
    private TextView mMemberCreatedAt;
    private TextView mMemberUpdatedAt          ;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MemberDetailFragment() {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_member_details_activity, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.edit_member) {
            Intent intent = new Intent(getContext(), EditMemberActivity.class);
            intent.putExtra(EditMemberActivity.ARG_MEMBER_ID, mMemberId);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AuthUtils.isAdmin(getContext())) {
            setHasOptionsMenu(true);
        }
        mMemberId = getArguments().getLong(ARG_MEMBER_ID);

//        if (getArguments().containsKey(ARG_MEMBER_ID)) {
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
        getLoaderManager().initLoader(MEMBER_DETAIL_LOADER, null, this);


//        //AccountList
//        Bundle arguments = new Bundle();
//        arguments.putString(AccountListFragment.ARG_MEMBER_ID,
//                getArguments().getString(MemberDetailFragment.ARG_MEMBER_ID));
//        AccountListFragment accountListFragment = new AccountListFragment();
//        accountListFragment.setArguments(arguments);
//        getFragmentManager().beginTransaction()
//                .replace(R.id.account_list_container, accountListFragment)
//                .commit();

        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_member_detail, container, false);


        mMemberName = (TextView) rootView.findViewById(R.id.tvMemberName);
        mMemberAlias = (TextView) rootView.findViewById(R.id.tvMemberAlias);
        mMemberGender = (TextView) rootView.findViewById(R.id.tvMemberGender);
        mMemberFather = (TextView) rootView.findViewById(R.id.tvMemberFather);
        mMemberSpouse = (TextView) rootView.findViewById(R.id.tvMemberSpouse);
        mMemberOccupation = (TextView) rootView.findViewById(R.id.tvMemberOccupation);
        mMemberAge = (TextView) rootView.findViewById(R.id.tvMemberAge);
        mMemberMobile = (TextView) rootView.findViewById(R.id.tvMemberMobile);
        mMemberAddress = (TextView) rootView.findViewById(R.id.tvMemberAddress);
        mMemberNominee = (TextView) rootView.findViewById(R.id.tvMemberNominee);
        mMemberRelationWithNominee = (TextView) rootView.findViewById(R.id.tvMemberRelationWithNominee);
        mMemberAddressOfNominee = (TextView) rootView.findViewById(R.id.tvMemberAddressOfNominee);
        mMemberRemarks = (TextView) rootView.findViewById(R.id.tvMemberRemarks);
//        mMemberClosedAt             = (TextView) rootView.findViewById(R.id.tvMemberClosedAt           );
        mMemberCreatedAt = (TextView) rootView.findViewById(R.id.tvMemberCreatedAt);
        mMemberUpdatedAt            = (TextView) rootView.findViewById(R.id.tvMemberUpdatedAt          );

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(final int loaderId, Bundle args) {
        switch (loaderId) {
            case MEMBER_DETAIL_LOADER:
                return new CursorLoader(getActivity(),
                        SurakshaContract.MemberEntry.buildMemberUri(mMemberId),
                        Member.MemberQuery.PROJECTION,
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
            case MEMBER_DETAIL_LOADER:
                if (data != null && data.moveToFirst()) {
                    mMemberName.setText(data.getString(Member.MemberQuery.COL_NAME));
                    mMemberAlias.setText(data.getString(Member.MemberQuery.COL_ALIAS));
                    mMemberGender.setText(data.getString(Member.MemberQuery.COL_GENDER));
                    mMemberFather.setText(data.getString(Member.MemberQuery.COL_FATHER));
                    mMemberSpouse.setText(data.getString(Member.MemberQuery.COL_SPOUSE));
                    mMemberOccupation.setText(data.getString(Member.MemberQuery.COL_OCCUPATION));
                    mMemberAge.setText(data.getString(Member.MemberQuery.COL_AGE));
                    mMemberMobile.setText(data.getString(Member.MemberQuery.COL_MOBILE));
                    mMemberAddress.setText(data.getString(Member.MemberQuery.COL_ADDRESS));
                    mMemberNominee.setText(data.getString(Member.MemberQuery.COL_NOMINEE));
                    mMemberRelationWithNominee.setText(data.getString(Member.MemberQuery.COL_RELATION_WITH_NOMINEE));
                    mMemberAddressOfNominee.setText(data.getString(Member.MemberQuery.COL_ADDRESS_OF_NOMINEE));
                    mMemberRemarks.setText(data.getString(Member.MemberQuery.COL_REMARKS));
//                    mMemberClosedAt.setText(Utility.formatDateTime(data.getLong(Member.MemberQuery.COL_CLOSED_AT)));
                    mMemberCreatedAt.setText(CalendarUtils.formatDateTime(data.getLong(Member.MemberQuery.COL_CREATED_AT)));
                    mMemberUpdatedAt.setText(CalendarUtils.formatDateTime(data.getLong(Member.MemberQuery.COL_UPDATED_AT)));
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
