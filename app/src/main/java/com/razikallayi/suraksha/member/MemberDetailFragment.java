package com.razikallayi.suraksha.member;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.loan.LoanIssue;
import com.razikallayi.suraksha.utils.AuthUtils;
import com.razikallayi.suraksha.utils.CalendarUtils;

/**
 * A fragment representing a single Member detail screen.
 * This fragment is either contained in a {@link MemberListActivity}
 * in two-pane mode (on tablets) or a {@link MemberDetailActivity}
 * on handsets.
 */
public class MemberDetailFragment extends Fragment {
    public static final String TAG = MemberDetailFragment.class.getSimpleName();
    public static final int REQUEST_CODE_CLOSE_ACCOUNT_ACTIVITY = 0x323;
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_MEMBER_ID = "member_id";
    //    public static final int MEMBER_DETAIL_LOADER = 0;
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
    private RelativeLayout layoutMemberInfo;
    private Switch switchIsSmsEnabled;
    private View accountClosedHolder;
    private TextView closeAccountBtn;
    private TextView mMemberClosedAt;
    private Member mMember;

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
        if (mMember == null) {
            mMemberId = getArguments().getLong(ARG_MEMBER_ID);
            mMember = Member.getMemberFromId(getContext(), mMemberId);
        }
        if (AuthUtils.isAdmin(getContext()) && !mMember.isAccountClosed()) {
            setHasOptionsMenu(true);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_member_detail, container, false);
        layoutMemberInfo = rootView.findViewById(R.id.layoutMemberInfo);
        mMemberName = rootView.findViewById(R.id.tvMemberName);
        mMemberAlias = rootView.findViewById(R.id.tvMemberAlias);
        mMemberGender = rootView.findViewById(R.id.tvMemberGender);
        mMemberFather = rootView.findViewById(R.id.tvMemberFather);
        mMemberSpouse = rootView.findViewById(R.id.tvMemberSpouse);
        mMemberOccupation = rootView.findViewById(R.id.tvMemberOccupation);
        mMemberAge = rootView.findViewById(R.id.tvMemberAge);
        mMemberMobile = rootView.findViewById(R.id.tvMemberMobile);
        mMemberAddress = rootView.findViewById(R.id.tvAddress);
        mMemberNominee = rootView.findViewById(R.id.tvMemberNominee);
        mMemberRelationWithNominee = rootView.findViewById(R.id.tvMemberRelationWithNominee);
        mMemberAddressOfNominee = rootView.findViewById(R.id.tvMemberAddressOfNominee);
        mMemberRemarks = rootView.findViewById(R.id.tvMemberRemarks);
        mMemberCreatedAt = rootView.findViewById(R.id.tvMemberCreatedAt);
        mMemberUpdatedAt = rootView.findViewById(R.id.tvMemberUpdatedAt);
        switchIsSmsEnabled = rootView.findViewById(R.id.switchIsSmsEnabled);
        mMemberClosedAt = rootView.findViewById(R.id.tvMemberClosedAt);
        accountClosedHolder = rootView.findViewById(R.id.AccountClosedHolder);
        closeAccountBtn = rootView.findViewById(R.id.closeAccountBtn);

        displayMemberDetails();
        return rootView;
    }

    private void displayMemberDetails() {
        mMemberId = getArguments().getLong(ARG_MEMBER_ID);
        mMember = Member.getMemberFromId(getContext(), mMemberId);

        mMember.setMemberInfo(getContext(), layoutMemberInfo);

        mMemberName.setText(mMember.getName());
        mMemberAlias.setText(mMember.getAlias());
        mMemberGender.setText(mMember.getGender());
        mMemberFather.setText(mMember.getFather());
        mMemberSpouse.setText(mMember.getSpouse());
        mMemberOccupation.setText(mMember.getOccupation());
        mMemberAge.setText(String.valueOf(mMember.getAge()));
        mMemberMobile.setText(mMember.getMobile());
        mMemberAddress.setText(mMember.getAddress());
        mMemberNominee.setText(mMember.getNominee());
        mMemberRelationWithNominee.setText(mMember.getRelationWithNominee());
        mMemberAddressOfNominee.setText(mMember.getAddressOfNominee());
        mMemberRemarks.setText(mMember.getRemarks());
//                    mMemberClosedAt.setText(Utility.formatDateTime(data.getLong(Member.MemberQuery.COL_CLOSED_AT)));
        mMemberCreatedAt.setText(CalendarUtils.getFriendlyDayString(getContext(), mMember.getCreatedAt()));
        mMemberUpdatedAt.setText(CalendarUtils.getFriendlyDayString(getContext(), mMember.getUpdatedAt()));
        switchIsSmsEnabled.setChecked(mMember.isSmsEnabled());

        if (mMember.isAccountClosed()) {
            mMemberClosedAt.setText(CalendarUtils.formatDate(mMember.getClosedAt()));
            accountClosedHolder.setVisibility(View.VISIBLE);
            closeAccountBtn.setVisibility(View.GONE);

            switchIsSmsEnabled.setEnabled(false);
        } else {
            accountClosedHolder.setVisibility(View.GONE);
            switchIsSmsEnabled.setEnabled(true);
            switchIsSmsEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    mMember.saveIsSmsEnabled(getContext(), b);
                }
            });
            if (AuthUtils.isAdmin(getContext()) || AuthUtils.isDeveloper(getContext())) {
                closeAccountBtn.setVisibility(View.VISIBLE);
                closeAccountBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LoanIssue activeBystanderLoan = mMember.getActiveBystanderLoan(getContext());
                        if (activeBystanderLoan != null) {
                            Member loanMember = activeBystanderLoan.getMember(getContext());
                            new AlertDialog.Builder(getActivity())
                                    .setMessage(mMember.getName() + " is a security mMember for "
                                            + loanMember.getName()
                                            + ".Please close the loan first")
                                    .setPositiveButton("OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                    dialog.cancel();
                                                }
                                            }
                                    ).create().show();
                        } else {
                            Intent intent = new Intent(getActivity(), CloseAccountActivity.class);
                            intent.putExtra(CloseAccountActivity.ARG_MEMBER_ID, mMemberId);
                            getActivity().startActivityForResult(intent, REQUEST_CODE_CLOSE_ACCOUNT_ACTIVITY);
                        }
                    }
                });
            } else {
                closeAccountBtn.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        displayMemberDetails();
    }
}
