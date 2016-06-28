package com.razikallayi.suraksha.account;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.RecyclerViewCursorAdapter;
import com.razikallayi.suraksha.data.SurakshaContract;
import com.razikallayi.suraksha.loan.IssueLoanActivity;
import com.razikallayi.suraksha.loan.LoanIssue;
import com.razikallayi.suraksha.loan.LoanIssue.LoanIssueQuery;
import com.razikallayi.suraksha.member.Member;
import com.razikallayi.suraksha.officer.Officer;
import com.razikallayi.suraksha.txn.Transaction;
import com.razikallayi.suraksha.utils.CalendarUtils;
import com.razikallayi.suraksha.utils.SmsUtils;
import com.razikallayi.suraksha.utils.Utility;

import java.util.Calendar;

public class AccountManipulationsFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String TAG = AccountManipulationsFragment.class.getSimpleName();
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_MEMBER_ID = "member_id";
    public static final int ISSUE_LOAN_ACTIVITY = 0X000002;

    private static final int MAKE_DEPOSIT_ACTIVITY = 0X00004;
    private final int LOAN_DUE_LOADER = 0X000001;
    private Member mMember;
    private LoanIssuedRecyclerViewAdapter mLoanIssueAdapter;
    private View mRootView;
    private CardView mLoanIssuedCardView;
    private LayoutInflater mInflater;
    private CardView cvIssueLoan;
    private CardView cvDepositDueAMF;

    public AccountManipulationsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mInflater = inflater;
        mRootView = inflater.inflate(R.layout.account_manipulations_fragment, container, false);


//        CardView cvDeposit = (CardView) mRootView.findViewById(R.id.cvDeposit);
//        cvDeposit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Intent intent = new Intent(getContext(), DepositActivity.class);
////                intent.putExtra(DepositActivity.ARG_ACCOUNT_NUMBER, mMember.getAccountNo());
////                startActivityForResult(intent, MAKE_DEPOSIT_ACTIVITY);
//
//                //Get Next deposit Month
//                Calendar depositMonth = mMember.getNextDepositMonthCalendar(getContext());
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                builder.setMessage("Deposit " + Utility.formatAmountInRupees(builder.getContext(),
//                        Utility.getMonthlyDepositAmount()) + " for " + mMember.getName()
//                        + "(" + mMember.getAccountNo() + ")" + " ?");
//                builder.setTitle(CalendarUtils.readableDepositMonth(depositMonth));
//
//                final View remarks_dialog_content = mInflater.inflate(R.layout.remarks_dialog_content, null);
//                builder.setView(remarks_dialog_content);
//                final Calendar nextDepositMonth = depositMonth;
//                builder.setPositiveButton("Deposit", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        String remarks = ((EditText) remarks_dialog_content.findViewById(R.id.remarksEditText)).getText().toString();
//                        makeDeposit(nextDepositMonth.getTimeInMillis(), remarks);
//                    }
//                });
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//                builder.show();
//            }
//        });

        //Issue Loan button
        cvIssueLoan = (CardView) mRootView.findViewById(R.id.cvIssueLoan);
        drawIssueLoanButton();


        CardView cvLoanReturnDueAMF = (CardView) mRootView.findViewById(R.id.cvLoanReturnDueAMF);
        cvLoanReturnDueAMF.setVisibility(mMember.hasLoanDue(getContext()) ? View.VISIBLE : View.GONE);

        cvDepositDueAMF = (CardView) mRootView.findViewById(R.id.cvDepositDueAMF);
        drawDepositDue();


        mLoanIssuedCardView = (CardView) mRootView.findViewById(R.id.loanIssuedCardView);
        RecyclerView recyclerView = (RecyclerView) mLoanIssuedCardView.findViewById(R.id.loanIssuedList);
        mLoanIssueAdapter = new LoanIssuedRecyclerViewAdapter();
        recyclerView.setAdapter(mLoanIssueAdapter);
        return mRootView;
    }

    public void makeDeposit(long depositMonth, String remarks) {
        Context context = getContext();
        Transaction txn = mMember.makeDeposit(context, depositMonth, remarks);
        drawDepositDue();
        if (SmsUtils.smsEnabledAfterDeposit(context)) {
            String mobileNumber = mMember.getMobile();
            String message = mMember.getName() + ", your " + context.getResources().getString(R.string.app_name)
                    + " account " + mMember.getAccountNo()
                    + " is credited with a deposit of " + (int) txn.getAmount()
                    + CalendarUtils.readableDepositMonth(txn.getDefinedDepositMonth());
            boolean result = SmsUtils.sendSms(message, mobileNumber);
            if (result) {
                Toast.makeText(context, "SMS sent to " + mMember.getName(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mMember = Member.getMemberFromId(context, getArguments().getLong(ARG_MEMBER_ID, -1));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(LOAN_DUE_LOADER, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMember = Member.getMemberFromId(getContext(), mMember.getId());
        drawIssueLoanButton();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOAN_DUE_LOADER:
                if (null == mMember) return null;
                return new CursorLoader(getContext(), SurakshaContract.LoanIssueEntry.CONTENT_URI,
                        LoanIssueQuery.PROJECTION,
                        SurakshaContract.LoanIssueEntry.TABLE_NAME + "."
                                + SurakshaContract.LoanIssueEntry.COLUMN_FK_ACCOUNT_NUMBER + " = ?",
                        new String[]{String.valueOf(mMember.getAccountNo())},
                        SurakshaContract.LoanIssueEntry.TABLE_NAME + "."
                                + SurakshaContract.LoanIssueEntry._ID + " desc");
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        switch (loader.getId()) {
            case LOAN_DUE_LOADER:
                if (data == null || data.getCount() <= 0) {
                    mLoanIssuedCardView.setVisibility(View.GONE);
                } else {
                    mLoanIssuedCardView.setVisibility(View.VISIBLE);
                    mLoanIssueAdapter.swapCursor(data);
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mLoanIssueAdapter.swapCursor(null);
    }

    private void drawIssueLoanButton() {
        if (mMember.isHasLoan() || mMember.isLoanBlocked()) {
            cvIssueLoan.setVisibility(View.GONE);
        } else {
            cvIssueLoan.setVisibility(View.VISIBLE);
            cvIssueLoan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), IssueLoanActivity.class);
                    intent.putExtra(IssueLoanActivity.ARG_ACCOUNT_NUMBER, mMember.getAccountNo());
                    intent.putExtra(IssueLoanActivity.ARG_MEMBER_ID, mMember.getId());
                    getActivity().startActivityForResult(intent, ISSUE_LOAN_ACTIVITY);
                }
            });
        }
    }

    private void drawDepositDue() {
        final CardView cvDepositDue = cvDepositDueAMF;
        if (mMember.hasDepositDue(getContext())) {
            TextView lblDepositMonthAMF = (TextView) cvDepositDue.findViewById(R.id.lblDepositMonthAMF);
            //Get Next deposit Month
            final Calendar nextDepositMonth = mMember.getNextDepositMonthCalendar(getContext());
            lblDepositMonthAMF.setText(CalendarUtils.readableDepositMonth(nextDepositMonth));

            final String depositAMount = Utility.formatAmountInRupees(getContext(), Utility.getMonthlyDepositAmount());
            ((TextView) cvDepositDue.findViewById(R.id.lblDepositAmountAMF)).setText(depositAMount);
            cvDepositDue.findViewById(R.id.cvDepositAMF).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String remarks = ((EditText) cvDepositDue.findViewById(R.id.remarksEditText)).getText().toString();

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Deposit " + depositAMount + " for " + mMember.getName() + " in account "
                            + mMember.getAccountNo() + "\n" + remarks);

                    builder.setPositiveButton("Deposit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            makeDeposit(nextDepositMonth.getTimeInMillis(), remarks);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }
            });
            cvDepositDue.setVisibility(View.VISIBLE);
        } else {
            cvDepositDue.setVisibility(View.GONE);
        }
    }

    public class LoanIssuedRecyclerViewAdapter
            extends RecyclerViewCursorAdapter<ViewHolder> {

        public LoanIssuedRecyclerViewAdapter() {
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity())
                    .inflate(R.layout.loan_issued_list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, Cursor cursor) {
            // Set selected state; use a state list drawable to style the view
            holder.bindData(cursor);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView amountLoanIssueItem;
        public final TextView guarantorNameLoanIssueItem;
        public final TextView issued_date_loan_issue_item;
        public final TextView purpose_loan_issue_item;
        public final TextView office_statement_loan_issue_item;
        public final TextView officer_loan_issue_item;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            amountLoanIssueItem = (TextView) view.findViewById(R.id.amountLoanIssueItem);
            guarantorNameLoanIssueItem = (TextView) view.findViewById(R.id.guarantorNameLoanIssueItem);
            issued_date_loan_issue_item = (TextView) view.findViewById(R.id.issued_date_loan_issue_item);
            purpose_loan_issue_item = (TextView) view.findViewById(R.id.purpose_loan_issue_item);
            office_statement_loan_issue_item = (TextView) view.findViewById(R.id.office_statement_loan_issue_item);
            officer_loan_issue_item = (TextView) view.findViewById(R.id.officer_loan_issue_item);
        }

        public void bindData(final Cursor cursor) {

            final Context context = getContext();
            LoanIssue loanIssue = LoanIssue.getLoanIssueFromCursor(context, cursor);

            amountLoanIssueItem.setText(Utility.formatAmountInRupees(context, loanIssue.getAmount()));
            guarantorNameLoanIssueItem.setText(Member.getMemberFromAccountNumber(
                    context, loanIssue.getSecurityAccountNo()).getName());
            issued_date_loan_issue_item.setText(CalendarUtils.formatDate(loanIssue.getCreatedAt()));
            purpose_loan_issue_item.setText(loanIssue.getPurpose());
            office_statement_loan_issue_item.setText(loanIssue.getOfficeStatement());
            officer_loan_issue_item.setText(Officer.getOfficerNameFromId(
                    context, loanIssue.getTransaction().getOfficer_id()));
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(context, AccountDetailActivity.class);
//                    intent.putExtra("account_number", accountNumber);
//                    context.startActivity(intent);
                }
            });
        }
    }
}