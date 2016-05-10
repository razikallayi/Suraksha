package com.razikallayi.suraksha;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.razikallayi.suraksha.data.SurakshaContract;
import com.razikallayi.suraksha.utils.Utility;

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

    public static final int MEMBER_DETAIL_LOADER = 0;

    private TextView mMemberName;
    private TextView mMemberAlias              ;
    private TextView mMemberGender             ;
    private TextView mMemberFather             ;
    private TextView mMemberSpouse             ;
    private TextView mMemberOccupation         ;
    private TextView mMemberAge                ;
    private TextView mMemberMobile             ;
    private TextView mMemberAddress            ;
    private TextView mMemberNominee            ;
    private TextView mMemberRelationWithNominee;
    private TextView mMemberAddressOfNominee   ;
    private TextView mMemberRemarks            ;
//    private TextView mMemberClosedAt           ;
    private TextView mMemberCreatedAt          ;
//    private TextView mMemberUpdatedAt          ;


    private static final String[] MEMBER_COLUMNS = new String[] {
            SurakshaContract.MemberEntry.COLUMN_NAME,
            SurakshaContract.MemberEntry.COLUMN_ALIAS,
            SurakshaContract.MemberEntry.COLUMN_GENDER,
            SurakshaContract.MemberEntry.COLUMN_FATHER,
            SurakshaContract.MemberEntry.COLUMN_SPOUSE,
            SurakshaContract.MemberEntry.COLUMN_OCCUPATION,
            SurakshaContract.MemberEntry.COLUMN_AGE,
            SurakshaContract.MemberEntry.COLUMN_MOBILE,
            SurakshaContract.MemberEntry.COLUMN_ADDRESS,
            SurakshaContract.MemberEntry.COLUMN_NOMINEE,
            SurakshaContract.MemberEntry.COLUMN_RELATION_WITH_NOMINEE,
            SurakshaContract.MemberEntry.COLUMN_ADDRESS_OF_NOMINEE,
            SurakshaContract.MemberEntry.COLUMN_REMARKS,
            SurakshaContract.MemberEntry.COLUMN_CLOSED_AT,
            SurakshaContract.MemberEntry.COLUMN_CREATED_AT,
            SurakshaContract.MemberEntry.COLUMN_UPDATED_AT
    };

    // these indices must match the projection
    private static final int COL_NAME                  =  0;
    private static final int COL_ALIAS                 =  1;
    private static final int COL_GENDER                =  2;
    private static final int COL_FATHER                =  3;
    private static final int COL_SPOUSE                =  4;
    private static final int COL_OCCUPATION            =  5;
    private static final int COL_AGE                   =  6;
    private static final int COL_MOBILE                =  7;
    private static final int COL_ADDRESS               =  8;
    private static final int COL_NOMINEE               =  9;
    private static final int COL_RELATION_WITH_NOMINEE =  10;
    private static final int COL_ADDRESS_OF_NOMINEE    =  11;
    private static final int COL_REMARKS               =  12;
    private static final int COL_CLOSED_AT             =  13;
    private static final int COL_CREATED_AT            =  14;
    private static final int COL_UPDATED_AT            =  15;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MemberDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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


        mMemberName                 = (TextView) rootView.findViewById(R.id.tvMemberName);
        mMemberAlias                = (TextView) rootView.findViewById(R.id.tvMemberAlias);
        mMemberGender               = (TextView) rootView.findViewById(R.id.tvMemberGender);
        mMemberFather               = (TextView) rootView.findViewById(R.id.tvMemberFather);
        mMemberSpouse               = (TextView) rootView.findViewById(R.id.tvMemberSpouse);
        mMemberOccupation           = (TextView) rootView.findViewById(R.id.tvMemberOccupation);
        mMemberAge                  = (TextView) rootView.findViewById(R.id.tvMemberAge);
        mMemberMobile               = (TextView) rootView.findViewById(R.id.tvMemberMobile  );
        mMemberAddress              = (TextView) rootView.findViewById(R.id.tvMemberAddress );
        mMemberNominee              = (TextView) rootView.findViewById(R.id.tvMemberNominee );
        mMemberRelationWithNominee  = (TextView) rootView.findViewById(R.id.tvMemberRelationWithNominee);
        mMemberAddressOfNominee     = (TextView) rootView.findViewById(R.id.tvMemberAddressOfNominee   );
        mMemberRemarks              = (TextView) rootView.findViewById(R.id.tvMemberRemarks            );
//        mMemberClosedAt             = (TextView) rootView.findViewById(R.id.tvMemberClosedAt           );
        mMemberCreatedAt            = (TextView) rootView.findViewById(R.id.tvMemberCreatedAt          );
//        mMemberUpdatedAt            = (TextView) rootView.findViewById(R.id.tvMemberUpdatedAt          );

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(final int loaderId, Bundle args) {
        switch (loaderId) {
            case MEMBER_DETAIL_LOADER:
                return new CursorLoader(getActivity(),
                        SurakshaContract.MemberEntry.buildMemberUri(getArguments().getLong(ARG_MEMBER_ID)),
                        MEMBER_COLUMNS,
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
                    mMemberName.setText(data.getString(COL_NAME));
                    mMemberAlias.setText(data.getString(COL_ALIAS));
                    mMemberGender.setText(data.getString(COL_GENDER));
                    mMemberFather.setText(data.getString(COL_FATHER));
                    mMemberSpouse.setText(data.getString(COL_SPOUSE));
                    mMemberOccupation.setText(data.getString(COL_OCCUPATION));
                    mMemberAge.setText(data.getString(COL_AGE));
                    mMemberMobile.setText(data.getString(COL_MOBILE));
                    mMemberAddress.setText(data.getString(COL_ADDRESS));
                    mMemberNominee.setText(data.getString(COL_NOMINEE));
                    mMemberRelationWithNominee.setText(data.getString(COL_RELATION_WITH_NOMINEE));
                    mMemberAddressOfNominee.setText(data.getString(COL_ADDRESS_OF_NOMINEE));
                    mMemberRemarks.setText(data.getString(COL_REMARKS));
//                    mMemberClosedAt.setText(Utility.formatDate(data.getLong(COL_CLOSED_AT)));
                    mMemberCreatedAt.setText(Utility.formatDate(data.getLong(COL_CREATED_AT)));
//                    mMemberUpdatedAt.setText(Utility.formatDate(data.getLong(COL_UPDATED_AT)));
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
