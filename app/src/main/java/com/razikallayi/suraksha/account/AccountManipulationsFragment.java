package com.razikallayi.suraksha.account;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.deposit.DepositActivity;
import com.razikallayi.suraksha.loan.IssueLoanActivity;
import com.razikallayi.suraksha.loan.LoanIssue;
import com.razikallayi.suraksha.loan.LoanIssuedActivity;
import com.razikallayi.suraksha.loan.LoanReturnActivity;
import com.razikallayi.suraksha.member.Member;
import com.razikallayi.suraksha.txn.Transaction;
import com.razikallayi.suraksha.utils.CalendarUtils;
import com.razikallayi.suraksha.utils.SmsUtils;
import com.razikallayi.suraksha.utils.Utility;

import java.util.Calendar;
import java.util.List;

public class AccountManipulationsFragment extends Fragment {
    public static final String TAG = AccountManipulationsFragment.class.getSimpleName();
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_MEMBER_ID = "member_id";

    private final int LOAN_ISSUED_LOADER = 0X000001;
    private Member mMember;
    private LayoutInflater mInflater;
    private TextView lblLoanBlockedReason;
    private CardView cvIssueLoan, cvDepositDueAMF, cvLoanReturnDueAMF, cvSummeryAMF;
    private LoanIssue loanIssued;
    private List<Transaction> loanReturnList;

    public AccountManipulationsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mMember = Member.getMemberFromId(context, getArguments().getLong(ARG_MEMBER_ID, -1));
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mInflater = inflater;
        View mRootView = inflater.inflate(R.layout.account_manipulations_fragment, container, false);

        TextView loan = (TextView) mRootView.findViewById(R.id.loanAMF);
        loan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LoanIssuedActivity.class);
                intent.putExtra(LoanIssuedActivity.ARG_ACCOUNT_NUMBER, mMember.getAccountNo());
                startActivity(intent);

            }
        });

        TextView deposit = (TextView) mRootView.findViewById(R.id.depositAMF);
        deposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), DepositActivity.class);
                intent.putExtra(DepositActivity.ARG_ACCOUNT_NUMBER, mMember.getAccountNo());
                startActivity(intent);

            }
        });

        //Issue Loan button
        cvIssueLoan = (CardView) mRootView.findViewById(R.id.cvIssueLoan);
        lblLoanBlockedReason = (TextView) mRootView.findViewById(R.id.lblLoanBlockedReason);
        drawIssueLoanButton();


        cvLoanReturnDueAMF = (CardView) mRootView.findViewById(R.id.cvLoanReturnDueAMF);
        drawLoanDue();

        cvDepositDueAMF = (CardView) mRootView.findViewById(R.id.cvDepositDueAMF);
        drawDepositDue();

        cvSummeryAMF = (CardView) mRootView.findViewById(R.id.cvSummeryAMF);
        drawSummery();

        return mRootView;
    }

    private void drawLoanDue() {
        if (mMember.hasLoanDue(getContext())) {
            TextView lblInstalmentAmount = (TextView) cvLoanReturnDueAMF.findViewById(R.id.lblLoanReturnAmountAMF);
            TextView lblNextInstalmentCount = (TextView) cvLoanReturnDueAMF.findViewById(R.id.lblLoanInstalmentTimeAMF);

            loanIssued = mMember.getActiveLoan(getContext());
            loanReturnList = loanIssued.getLoanReturnTxnList(getContext());

            final String instalmentAmount = Utility.formatAmountInRupees(getContext(), loanIssued.getLoanInstalmentAmount());
            lblInstalmentAmount.setText(instalmentAmount);
            String nextInstalmentString = Utility.formatNumberSuffix(loanReturnList.size() + 1)
                    + " Instalment of " + loanIssued.getLoanInstalmentTimes();
            lblNextInstalmentCount.setText(nextInstalmentString);

            cvLoanReturnDueAMF.setVisibility(View.VISIBLE);
            CardView loanReturn = (CardView) cvLoanReturnDueAMF.findViewById(R.id.cvLoanReturnAMF);
            loanReturn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), LoanReturnActivity.class);
                    LoanReturnActivity.setLoanIssue(loanIssued);
                    startActivity(intent);
/*
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    final View remarksView = mInflater.inflate(R.layout.remarks_dialog_content, null);
//                    final TextView txtNarration = (TextView) cvLoanReturnDueAMF.findViewById(R.id.txtRemarksLoanReturnDue);
                    builder.setView(remarksView);
                    builder.setMessage("Receive " + Utility.formatNumberSuffix(loanReturnList.size() + 1) + " instalment "
                            + instalmentAmount + " from " + mMember.getName());

                    builder.setPositiveButton("Receive", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final String strNarration = String.valueOf(
                                    ((EditText) (remarksView.findViewById(R.id.txtRemarks))).getText());
                            loanIssued.saveLoanReturn(getContext(), strNarration);
                            mMember = Member.getMemberFromId(getContext(), mMember.getId());
                            drawIssueLoanButton();
                            drawLoanDue();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                    */

                }
            });
        } else {
            cvLoanReturnDueAMF.setVisibility(View.GONE);
        }
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
//                    final String remarks = ((EditText) cvDepositDue.findViewById(R.id.txtRemarksDepositDue)).getText().toString();
                    final View remarksView = mInflater.inflate(R.layout.remarks_dialog_content, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Deposit " + depositAMount + " for " + mMember.getName() + " in account "
                            + mMember.getAccountNo());
                    builder.setView(remarksView);
                    builder.setPositiveButton("Deposit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final String strNarration = String.valueOf(
                                    ((EditText) (remarksView.findViewById(R.id.txtRemarks))).getText());
                            makeDeposit(nextDepositMonth.getTimeInMillis(), strNarration);
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


    private void drawSummery() {
        String summery = "";
        TextView lblSummeryAMF = (TextView) cvSummeryAMF.findViewById(R.id.lblSummeryAMF);
        double total = 0;
        if (mMember.hasDepositDue(getContext())) {
            //Get Next deposit Month
            final Calendar nextDepositMonth = mMember.getNextDepositMonthCalendar(getContext());
            String summeryPendingMonths = "";
            Calendar calCurrentDate = Calendar.getInstance();
            calCurrentDate.setTimeInMillis(CalendarUtils.normalizeDate(System.currentTimeMillis()));
            while (nextDepositMonth.getTimeInMillis() < calCurrentDate.getTimeInMillis()) {
                summeryPendingMonths += CalendarUtils.readableShortDepositMonth(nextDepositMonth)
                        + ": \t" + Utility.formatAmountInRupees(getContext(), Utility.getMonthlyDepositAmount())
                        + "\n";
                nextDepositMonth.add(Calendar.MONTH, 1);
                total += Utility.getMonthlyDepositAmount();
            }
            summery += summeryPendingMonths;
        }

        if (mMember.hasLoanDue(getContext())) {
            String instalmentAmount = Utility.formatAmountInRupees(getContext(), loanIssued.getLoanInstalmentAmount());
            String summeryPendingLoans = "";
            for (int i = loanReturnList.size() + 1; i <= loanIssued.getLoanInstalmentTimes(); i++) {
                summeryPendingLoans += Utility.formatNumberSuffix(i) + " Instalment : " + instalmentAmount
                        + "\n";
                total += loanIssued.getLoanInstalmentAmount();
            }
            summery += summeryPendingLoans;
        }
        summery += "Total : " + Utility.formatAmountInRupees(getContext(), total);
        lblSummeryAMF.setText(summery);

        View summerySms = cvSummeryAMF.findViewById(R.id.summerySms);
        final String finalSummery = summery;
        summerySms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SmsUtils.smsEnabled(getContext())) {
                    boolean result = SmsUtils.sendSms(finalSummery, mMember.getMobile());
                    if (result) {
                        Toast.makeText(getContext(), "SMS sent to " + mMember.getName(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mMember = Member.getMemberFromId(getContext(), mMember.getId());
        drawIssueLoanButton();
        drawLoanDue();
        drawDepositDue();
    }


    private void drawIssueLoanButton() {
        if (mMember.isHasLoan()) {
            cvIssueLoan.setVisibility(View.GONE);
            LoanIssue activeLoan = mMember.getActiveLoan(getContext());
            int nextInstalmentCount = activeLoan.nextInstalmentCount(getContext());
            int remainingInstalments = activeLoan.getLoanInstalmentTimes() - (nextInstalmentCount - 1);
            lblLoanBlockedReason.setText(String.format("%s %s", remainingInstalments, " remaining instalments"));
            lblLoanBlockedReason.setVisibility(View.VISIBLE);
        } else if (mMember.isLoanBlocked()) {
            cvIssueLoan.setVisibility(View.GONE);
            LoanIssue bystanderLoan = mMember.getActiveBystanderLoan(getContext());
            //if the member is a bystander for another loan
            if (bystanderLoan != null) {
                int nextInstalmentCount = bystanderLoan.nextInstalmentCount(getContext());
                int remainingInstalments = bystanderLoan.bystanderReleaseInstalment() - (nextInstalmentCount - 1);
                Member bystanderFor = bystanderLoan.getMember(getContext());
                //Show the details of bystander as reason to block loan
                if (bystanderFor != null) {
                    // TODO: 29-06-2016 change to string resource.
                    lblLoanBlockedReason.setText(Html.fromHtml("Loan guarantor of <b>" + bystanderFor.getName()
                            + " [" + bystanderFor.getAccountNo() + "]</b>. Capable to issue loan after <b>"
                            + remainingInstalments + "</b> more instalments."));
                    lblLoanBlockedReason.setVisibility(View.VISIBLE);
                } else {
                    //Check the logic here. I think this else block is not needed
                    showLoanIssueButton();
                }
            }
        } else {
            showLoanIssueButton();
        }
    }

    private void showLoanIssueButton() {
        lblLoanBlockedReason.setVisibility(View.GONE);
        cvIssueLoan.setVisibility(View.VISIBLE);
        cvIssueLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), IssueLoanActivity.class);
                intent.putExtra(IssueLoanActivity.ARG_ACCOUNT_NUMBER, mMember.getAccountNo());
                intent.putExtra(IssueLoanActivity.ARG_MEMBER_ID, mMember.getId());
                startActivity(intent);
            }
        });
    }


}