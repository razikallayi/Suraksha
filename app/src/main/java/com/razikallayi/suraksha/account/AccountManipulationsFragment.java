package com.razikallayi.suraksha.account;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
import com.razikallayi.suraksha.member.MemberDetailActivity;
import com.razikallayi.suraksha.txn.Transaction;
import com.razikallayi.suraksha.utils.CalendarUtils;
import com.razikallayi.suraksha.utils.SmsUtils;
import com.razikallayi.suraksha.utils.Utility;

import java.util.Calendar;
import java.util.List;

public class AccountManipulationsFragment extends Fragment {
    public static final String TAG = AccountManipulationsFragment.class.getSimpleName();
    public static final int PERMISSION_REQUEST_SEND_SMS = 0X00103;
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_MEMBER_ID = "member_id";

    private final int LOAN_ISSUED_LOADER = 0X000001;
    private Member mMember;
    private LayoutInflater mInflater;
    private TextView lblLoanBlockedReason;
    private View cvIssueLoan, cvDepositDueAMF, cvLoanReturnDueAMF, cvSummeryAMF;
    private LoanIssue loanIssued;
    private List<Transaction> loanReturnList;
    private String finalSummery;

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

        TextView loansBtn = mRootView.findViewById(R.id.loanAMF);
        if (mMember.hasLoan()) {
            loansBtn.setVisibility(View.VISIBLE);
            loansBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), LoanIssuedActivity.class);
                    intent.putExtra(LoanIssuedActivity.ARG_ACCOUNT_NUMBER, mMember.getAccountNo());
                    startActivity(intent);

                }
            });
        } else {
            loansBtn.setVisibility(View.GONE);
        }

        TextView depositsBtn = mRootView.findViewById(R.id.depositAMF);
        depositsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), DepositActivity.class);
                intent.putExtra(DepositActivity.ARG_ACCOUNT_NUMBER, mMember.getAccountNo());
                startActivity(intent);

            }
        });

        //Issue Loan button
        cvIssueLoan = mRootView.findViewById(R.id.issueLoanView);
        lblLoanBlockedReason = mRootView.findViewById(R.id.lblLoanBlockedReason);
        drawIssueLoanButton();


        cvLoanReturnDueAMF = mRootView.findViewById(R.id.loanReturnDueCardAMF);
        drawLoanDue();

        cvDepositDueAMF = mRootView.findViewById(R.id.depositDueCardAMF);
        drawDepositDue();

        cvSummeryAMF = mRootView.findViewById(R.id.summeryCardAMF);
        drawSummery();

        return mRootView;
    }

    private void drawLoanDue() {
        if (mMember.hasLoanDue(getContext())) {
            TextView lblInstalmentAmount = cvLoanReturnDueAMF.findViewById(R.id.lblLoanReturnAmountAMF);
            TextView lblNextInstalmentCount = cvLoanReturnDueAMF.findViewById(R.id.lblLoanInstalmentTimeAMF);

            loanIssued = mMember.getActiveLoan(getContext());
            loanReturnList = loanIssued.getLoanReturnTxnList(getContext());

            final String instalmentAmount = Utility.formatAmountInRupees(getContext(), loanIssued.getLoanInstalmentAmount());
            lblInstalmentAmount.setText(instalmentAmount);
            String nextInstalmentString = Utility.formatNumberSuffix(loanReturnList.size() + 1)
                    + " Instalment of " + loanIssued.getLoanInstalmentTimes();
            lblNextInstalmentCount.setText(nextInstalmentString);

            cvLoanReturnDueAMF.setVisibility(View.VISIBLE);
            View loanReturn = cvLoanReturnDueAMF.findViewById(R.id.loanReturnAMF);
            loanReturn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), LoanReturnActivity.class);
                    intent.putExtra(LoanReturnActivity.ARG_LOAN_ISSUE_ID, loanIssued.getId());
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
                    + " " + CalendarUtils.readableDepositMonth(txn.getDefinedDepositMonth());
            boolean result = SmsUtils.sendSms(message, mobileNumber);
            if (result) {
                Toast.makeText(context, "SMS sent to " + mMember.getName(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void drawDepositDue() {
        final View cvDepositDue = cvDepositDueAMF;
        if (mMember.hasDepositDue(getContext())) {
            TextView lblDepositMonthAMF = cvDepositDue.findViewById(R.id.lblDepositMonthAMF);
            //Get Next deposit Month
            final Calendar nextDepositMonth = mMember.getNextDepositMonthCalendar(getContext());
            lblDepositMonthAMF.setText(CalendarUtils.readableDepositMonth(nextDepositMonth));

            final String depositAMount = Utility.formatAmountInRupees(getContext(), Utility.getMonthlyDepositAmount());
            ((TextView) cvDepositDue.findViewById(R.id.lblDepositAmountAMF)).setText(depositAMount);
            cvDepositDue.findViewById(R.id.makeDepositAMF).setOnClickListener(new View.OnClickListener() {
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
        StringBuilder summery = new StringBuilder();
        TextView lblSummeryAMF = cvSummeryAMF.findViewById(R.id.lblSummeryAMF);
        double total = 0;
        if (mMember.hasDepositDue(getContext())) {
            //Get Next deposit Month
            final Calendar nextDepositMonth = mMember.getNextDepositMonthCalendar(getContext());
            StringBuilder summeryPendingMonths = new StringBuilder();
            Calendar calCurrentDate = Calendar.getInstance();
            calCurrentDate.setTimeInMillis(CalendarUtils.normalizeDate(System.currentTimeMillis()));
            summeryPendingMonths.append("<b>Deposits</b><br/>");
            while (nextDepositMonth.getTimeInMillis() < calCurrentDate.getTimeInMillis()) {
                summeryPendingMonths
                        .append(CalendarUtils.readableShortDepositMonth(nextDepositMonth))
                        .append(": ").append(Utility.formatAmountInRupees(getContext(), Utility.getMonthlyDepositAmount()))
                        .append(" <br/>");
                nextDepositMonth.add(Calendar.MONTH, 1);
                total += Utility.getMonthlyDepositAmount();
            }
            summery.append(summeryPendingMonths);
        }
        String grandString = "";
        if (mMember.hasLoanDue(getContext())) {
            summery.append("<b>Deposit Total : ")
                    .append(Utility.formatAmountInRupees(getContext(), total))
                    .append("</b><br/>");
            grandString = "<br/>Grand ";
            String instalmentAmount = Utility.formatAmountInRupees(getContext(), loanIssued.getLoanInstalmentAmount());
            StringBuilder summeryPendingLoans = new StringBuilder();
            Double loanTotal = 0.0;
            summeryPendingLoans.append("<br/><b>Loans</b><br/>");
            for (int i = loanReturnList.size() + 1; i <= loanIssued.getLoanInstalmentTimes(); i++) {
                summeryPendingLoans.append(Utility.formatNumberSuffix(i)).append(" Instalment : ").append(instalmentAmount).append("<br/>");
                loanTotal += loanIssued.getLoanInstalmentAmount();
            }
            summery.append(summeryPendingLoans);
            summery.append("<b>Loan Total : ")
                    .append(Utility.formatAmountInRupees(getContext(), loanTotal))
                    .append("</b>");
            total += loanTotal;
        }
        summery.append("<br/><b>").append(grandString).append("Total : ").append(Utility.formatAmountInRupees(getContext(), total)).append("</b>");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            lblSummeryAMF.setText(Html.fromHtml(String.valueOf(summery), Html.FROM_HTML_MODE_COMPACT));
        } else {
            lblSummeryAMF.setText(Html.fromHtml(String.valueOf(summery)));
        }

        View summerySms = cvSummeryAMF.findViewById(R.id.summerySms);
        finalSummery = String.valueOf(summery);
        summerySms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context c = getContext();
                int permissionCheck = ContextCompat.checkSelfPermission(c,
                        android.Manifest.permission.SEND_SMS);
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) c,
                            Manifest.permission.SEND_SMS)) {
                        //show explanation to user
                    } else {
                        ActivityCompat.requestPermissions((Activity) c,
                                new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_SEND_SMS);
                    }
                } else {
                    sendSms();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_SEND_SMS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendSms();
                } else {

                }
            }
        }

    }

    private void sendSms() {
        if (SmsUtils.smsEnabled(getContext())) {
            if (SmsUtils.editSummery(getContext())) {
                boolean result = SmsUtils.sendSms(finalSummery, mMember.getMobile());
                if (result) {
                    Toast.makeText(getContext(), "SMS sent to " + mMember.getName(),
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setData(Uri.parse("sms:" + mMember.getMobile()));
                sendIntent.putExtra("sms_body", finalSummery);
                startActivity(sendIntent);
            }

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mMember = Member.getMemberFromId(getContext(), mMember.getId());
        drawIssueLoanButton();
        drawLoanDue();
        drawDepositDue();
        drawSummery();
    }


    private void drawIssueLoanButton() {
        if (mMember.hasLoan()) {
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
                final Member bystanderFor = bystanderLoan.getMember(getContext());
                //Show the details of bystander as reason to block loan
                if (bystanderFor != null) {
                    // TODO: 29-06-2016 change to string resource.
                    lblLoanBlockedReason.setText(Html.fromHtml("Loan guarantor of <b>" + bystanderFor.getName()
                            + " [" + bystanderFor.getAccountNo() + "]</b>.<br/> Capable to issue loan after <b>"
                            + remainingInstalments + "</b> more instalments."));
                    lblLoanBlockedReason.setBackground(getResources().getDrawable(R.drawable.touch_selector_white));
                    lblLoanBlockedReason.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent intent = new Intent(getContext(), MemberDetailActivity.class);
                            intent.putExtra(MemberDetailActivity.ARG_MEMBER_ID, bystanderFor.getId());
                            intent.putExtra(MemberDetailActivity.ARG_MEMBER_NAME, bystanderFor.getName());
                            startActivity(intent);
                        }
                    });
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