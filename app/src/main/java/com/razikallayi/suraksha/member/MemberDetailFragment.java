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
    public static final int MEMBER_DETAIL_LOADER = 0;
    private long mMemberId;
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
    private TextView mMemberUpdatedAt;

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
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MEMBER_DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_member_detail, container, false);

        mMemberName = rootView.findViewById(R.id.tvMemberName);
        mMemberAlias = rootView.findViewById(R.id.tvMemberAlias);
        mMemberGender = rootView.findViewById(R.id.tvMemberGender);
        mMemberFather = rootView.findViewById(R.id.tvMemberFather);
        mMemberSpouse = rootView.findViewById(R.id.tvMemberSpouse);
        mMemberOccupation = rootView.findViewById(R.id.tvMemberOccupation);
        mMemberAge = rootView.findViewById(R.id.tvMemberAge);
        mMemberMobile = rootView.findViewById(R.id.tvMemberMobile);
        mMemberAddress = rootView.findViewById(R.id.tvMemberAddress);
        mMemberNominee = rootView.findViewById(R.id.tvMemberNominee);
        mMemberRelationWithNominee = rootView.findViewById(R.id.tvMemberRelationWithNominee);
        mMemberAddressOfNominee = rootView.findViewById(R.id.tvMemberAddressOfNominee);
        mMemberRemarks = rootView.findViewById(R.id.tvMemberRemarks);
//        mMemberClosedAt             = (TextView) rootView.findViewById(R.id.tvMemberClosedAt           );
        mMemberCreatedAt = rootView.findViewById(R.id.tvMemberCreatedAt);
        mMemberUpdatedAt = rootView.findViewById(R.id.tvMemberUpdatedAt);

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
                    Member member = Member.getMemberFromCursor(data);
                    if (member != null) {
                        mMemberName.setText(member.getName());
                        mMemberAlias.setText(member.getAlias());
                        mMemberGender.setText(member.getGender());
                        mMemberFather.setText(member.getFather());
                        mMemberSpouse.setText(member.getSpouse());
                        mMemberOccupation.setText(member.getOccupation());
                        mMemberAge.setText(String.valueOf(member.getAge()));
                        mMemberMobile.setText(member.getMobile());
                        mMemberAddress.setText(member.getAddress());
                        mMemberNominee.setText(member.getNominee());
                        mMemberRelationWithNominee.setText(member.getRelationWithNominee());
                        mMemberAddressOfNominee.setText(member.getAddressOfNominee());
                        mMemberRemarks.setText(member.getRemarks());
//                    mMemberClosedAt.setText(Utility.formatDateTime(data.getLong(Member.MemberQuery.COL_CLOSED_AT)));
                        mMemberCreatedAt.setText(CalendarUtils.getFriendlyDayString(getContext(), member.getCreatedAt()));
                        mMemberUpdatedAt.setText(CalendarUtils.getFriendlyDayString(getContext(), member.getUpdatedAt()));
                    }
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
