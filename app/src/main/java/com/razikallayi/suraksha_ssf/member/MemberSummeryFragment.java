package com.razikallayi.suraksha_ssf.member;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.razikallayi.suraksha_ssf.R;
import com.razikallayi.suraksha_ssf.data.SurakshaContract;
import com.razikallayi.suraksha_ssf.deposit.DepositListActivity;
import com.razikallayi.suraksha_ssf.deposit.MakeDepositActivity;
import com.razikallayi.suraksha_ssf.loan.IssueLoanActivity;
import com.razikallayi.suraksha_ssf.loan.LoanIssue;
import com.razikallayi.suraksha_ssf.loan.LoanIssuedListActivity;
import com.razikallayi.suraksha_ssf.loan.ReturnLoanActivity;
import com.razikallayi.suraksha_ssf.loan.LoanReturnedListActivity;
import com.razikallayi.suraksha_ssf.txn.Transaction;
import com.razikallayi.suraksha_ssf.utils.CalendarUtils;
import com.razikallayi.suraksha_ssf.utils.SmsUtils;
import com.razikallayi.suraksha_ssf.utils.Utility;
import com.razikallayi.suraksha_ssf.utils.WordUtils;

import java.util.Calendar;
import java.util.Locale;

public class MemberSummeryFragment extends Fragment {
    public static final String TAG = MemberSummeryFragment.class.getSimpleName();
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_MEMBER_ID = "member_id";

    private Member mMember;
    private TextView lblLoanBlockedReason;
    private View cvIssueLoan, cvDepositDueAMF, cvLoanReturnDueAMF, cvSummeryAMF;
    private LoanIssue loanIssued;
    private StringBuilder smsSummery;
    private TextView loansBtn;

    public MemberSummeryFragment() {
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
        View mRootView = inflater.inflate(R.layout.member_summery_fragment, container, false);
        RelativeLayout layoutMemberInfo = mRootView.findViewById(R.id.layoutMemberInfo);
        smsSummery = new StringBuilder();
        mMember.setMemberInfo(getContext(), layoutMemberInfo);
        loansBtn = mRootView.findViewById(R.id.loanAMF);
        loansBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LoanIssuedListActivity.class);
                intent.putExtra(LoanIssuedListActivity.ARG_ACCOUNT_NUMBER, mMember.getAccountNo());
                startActivity(intent);

            }
        });

        TextView depositsBtn = mRootView.findViewById(R.id.depositsAMF);
        depositsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), DepositListActivity.class);
                intent.putExtra(DepositListActivity.ARG_MEMBER_ID, mMember.getId());
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
        if (mMember.isAccountClosed()) {
            cvLoanReturnDueAMF.setVisibility(View.GONE);
            return;
        }
        if (mMember.hasLoanDue(getContext())) {
            TextView lblInstalmentAmount = cvLoanReturnDueAMF.findViewById(R.id.lblLoanReturnAmountAMF);
            TextView lblNextInstalmentCount = cvLoanReturnDueAMF.findViewById(R.id.lblLoanInstalmentTimeAMF);

            loanIssued = mMember.getActiveLoan(getContext());

            final String instalmentAmount = Utility.formatAmountInRupees(getContext(), loanIssued.getLoanInstalmentAmount());
            lblInstalmentAmount.setText(instalmentAmount);
            String nextInstalmentString = Utility.formatNumberSuffix(loanIssued.nextInstalmentNumber(getContext()))
                    + " Instalment of " + loanIssued.getLoanInstalmentTimes();
            lblNextInstalmentCount.setText(nextInstalmentString);

            cvLoanReturnDueAMF.setVisibility(View.VISIBLE);
            View loanReturn = cvLoanReturnDueAMF.findViewById(R.id.loanReturnAMF);
            loanReturn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ReturnLoanActivity.class);
                    intent.putExtra(ReturnLoanActivity.ARG_LOAN_ISSUE_ID, loanIssued.getId());
                    startActivity(intent);
                }
            });
        } else {
            cvLoanReturnDueAMF.setVisibility(View.GONE);
        }
    }

    private void drawDepositDue() {
        final View cvDepositDue = cvDepositDueAMF;
        if (mMember.isAccountClosed()) {
            cvDepositDueAMF.setVisibility(View.GONE);
            return;
        }
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
                    Intent intent = new Intent(getContext(), MakeDepositActivity.class);
                    intent.putExtra(MakeDepositActivity.ARG_MEMBER_ID, mMember.getId());
                    startActivity(intent);
                }
            });
            cvDepositDue.setVisibility(View.VISIBLE);
        } else {
            cvDepositDue.setVisibility(View.GONE);
        }
    }


    private void drawSummery() {
        if (mMember.isAccountClosed()) {
            cvSummeryAMF.setVisibility(View.GONE);
            return;
        }
        StringBuilder summery = new StringBuilder();
        smsSummery = new StringBuilder();
        TextView lblSummeryAMF = cvSummeryAMF.findViewById(R.id.lblSummeryAMF);
        double total = 0;
        boolean hasDepositDue = mMember.hasDepositDue(getContext());
        boolean hasLoanDue = mMember.hasLoanDue(getContext());
        smsSummery.append("Suraksha Reminder\n");
        if (hasDepositDue) {
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
            if (total > 0) {
                summery.append(summeryPendingMonths);
                summery.append("<b>Deposit Total : ")
                        .append(Utility.formatAmountInRupees(getContext(), total))
                        .append("</b><br/>");
                smsSummery.append("Deposit: Rs ").append(convertToLong(total));
            }
        }

        if (hasLoanDue) {
            if (loanIssued == null && mMember != null) {
                loanIssued = mMember.getActiveLoan(getContext());
            }
            String instalmentAmount = Utility.formatAmountInRupees(getContext(), loanIssued.getLoanInstalmentAmount());
            StringBuilder summeryPendingLoans = new StringBuilder();
            Double loanTotal = 0.0;
            summeryPendingLoans.append("<br/><b>Loans</b><br/>");
            int nextInstalmentNumber = loanIssued.nextInstalmentNumber(getContext());
            for (int i = nextInstalmentNumber; i <= loanIssued.getLoanInstalmentTimes(); i++) {
                if (!loanIssued.isInstalmentDue(i)) {
                    break;
                }
                summeryPendingLoans.append(Utility.formatNumberSuffix(i)).append(" Instalment : ").append(instalmentAmount).append("<br/>");
                loanTotal += loanIssued.getLoanInstalmentAmount();
            }
            if (loanTotal > 0) {
                summery.append(summeryPendingLoans);
                summery.append("<b>Loan Total : ")
                        .append(Utility.formatAmountInRupees(getContext(), loanTotal))
                        .append("</b>");
                smsSummery.append("\nLoan: Rs ").append(convertToLong(loanTotal));
                total += loanTotal;
            }
        }
        if (hasDepositDue && hasLoanDue) {
            String grandString = "<br/>Grand ";
            summery.append("<br/><b>").append(grandString).append("Total : ").append(Utility.formatAmountInRupees(getContext(), total)).append("</b>");
            smsSummery.append("\nTotal: Rs ").append(convertToLong(total));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            lblSummeryAMF.setText(Html.fromHtml(summery.toString(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            lblSummeryAMF.setText(Html.fromHtml(summery.toString()));
        }
        if (total == 0) {
            cvSummeryAMF.setVisibility(View.GONE);
        } else {
            cvSummeryAMF.setVisibility(View.VISIBLE);
        }

        final View summerySendReminderBtn = cvSummeryAMF.findViewById(R.id.summerySendReminderBtn);
        if (SmsUtils.smsEnabled(getContext()) && mMember.isSmsEnabled()) {
            summerySendReminderBtn.setVisibility(View.VISIBLE);
            summerySendReminderBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendSms();
                }
            });
        } else {
            summerySendReminderBtn.setVisibility(View.GONE);
        }
    }

    public static String convertToLong(double d) {
        if (d == (long) d)
            return String.format(Locale.ENGLISH, "%d", (long) d);
        else
            return String.format("%s", d);
    }

    private void sendSms() {
        if (SmsUtils.smsEnabled(getContext()) && mMember.isSmsEnabled()) {
//            boolean editSmsEnabled = SmsUtils.editSummery(getContext());
//            editSmsEnabled=false;
//            if (!editSmsEnabled) {
            new AlertDialog.Builder(getActivity())
                    .setTitle(mMember.getName() + ": " + mMember.getMobile())
                    .setMessage(smsSummery)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    boolean result = SmsUtils.sendSms(String.valueOf(smsSummery).trim(), mMember.getMobile());
                                    if (result) {
                                        Toast.makeText(getContext(), "SMS sent to " + mMember.getName(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                    )
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.cancel();
                                }
                            }
                    )
                    .create().show();

//            } else {
//                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
//                sendIntent.setData(Uri.parse("sms:" + mMember.getMobile()));
//                sendIntent.putExtra("sms_body", String.valueOf(smsSummery).trim());
//                startActivity(sendIntent);
//            }
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
        if (mMember.noOfLoansIssued(getContext()) > 0) {
            loansBtn.setVisibility(View.VISIBLE);
        } else {
            loansBtn.setVisibility(View.GONE);
        }
    }


    private void drawIssueLoanButton() {
        if (mMember.isAccountClosed()) {
            StringBuilder accountCloseInfo = new StringBuilder();
            accountCloseInfo.append("Account Closed On ")
                    .append(CalendarUtils.formatDate(mMember.getClosedAt()));
            Transaction txn = mMember.getCloseAccountTxn(getContext());
            if (txn != null) {
                if (txn.getAmount() == 0) {
                    accountCloseInfo.append("\n")
                            .append("No closing payments.");
                } else {
                    String fromOrTo;
                    if (txn.getVoucherType() == SurakshaContract.TxnEntry.PAYMENT_VOUCHER) {
                        fromOrTo = " to ";
                    } else {
                        fromOrTo = " from ";
                    }
                    accountCloseInfo.append("\n")
                            .append(WordUtils.toTitleCase(txn.getVoucherName()))
                            .append(" ")
                            .append(Utility.formatAmountInRupees(getContext(), txn.getAmount()))
                            .append(fromOrTo)
                            .append(mMember.getName());
                }

            }
            lblLoanBlockedReason.setText(accountCloseInfo);
            lblLoanBlockedReason.setVisibility(View.VISIBLE);
            cvIssueLoan.setVisibility(View.GONE);
            return;
        } else {
            lblLoanBlockedReason.setVisibility(View.GONE);
        }
        if (mMember.hasActiveLoan()) {
            cvIssueLoan.setVisibility(View.GONE);
            final LoanIssue activeLoan = mMember.getActiveLoan(getContext());
            int lastInstalmentNo = activeLoan.lastInstalmentNumber(getContext());
            int remainingInstalments = activeLoan.remainingInstalments(lastInstalmentNo);
            final Member securityMember = activeLoan.getSecurityMember(getContext());
            String securityMemberInfo = "";
            if (securityMember != null && !activeLoan.isByStanderReleased(lastInstalmentNo)) {
                securityMemberInfo = ". <br/><small>Under security of <b>" + securityMember.getName()
                        + " [" + securityMember.getAccountNo() + "]</small></b>";
            }
            lblLoanBlockedReason.setBackground(getResources().getDrawable(R.drawable.touch_selector));
            lblLoanBlockedReason.setText(Html.fromHtml(String.format("%s %s", remainingInstalments,
                    " remaining instalments" + securityMemberInfo)));
            lblLoanBlockedReason.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), LoanReturnedListActivity.class);
                    intent.putExtra(LoanReturnedListActivity.ARG_LOAN_ISSUE_ID, activeLoan.getId());
                    startActivity(intent);
                }
            });
            lblLoanBlockedReason.setVisibility(View.VISIBLE);
        } else if (mMember.isLoanBlocked()) {
            cvIssueLoan.setVisibility(View.GONE);
            LoanIssue bystanderLoan = mMember.getActiveBystanderLoan(getContext());
            //if the member is a bystander for another loan
            if (bystanderLoan != null) {
                int remainingInstalments = bystanderLoan.remainingInstalments(getContext());
                final Member bystanderForMember = bystanderLoan.getMember(getContext());
                //Show the details of bystander as reason to block loan
                if (bystanderForMember != null) {
                    // TODO: 29-06-2016 change to string resource.
                    lblLoanBlockedReason.setText(Html.fromHtml("Loan guarantor of <b>" + bystanderForMember.getName()
                            + " [" + bystanderForMember.getAccountNo() + "]</b>.<br/> Capable to issue loan after <b>"
                            + remainingInstalments + "</b> more instalments."));
                    lblLoanBlockedReason.setBackground(getResources().getDrawable(R.drawable.touch_selector_white));
                    lblLoanBlockedReason.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getContext(), MemberDetailActivity.class);
                            intent.putExtra(MemberDetailActivity.ARG_MEMBER_ID, bystanderForMember.getId());
                            intent.putExtra(MemberDetailActivity.ARG_MEMBER_NAME, bystanderForMember.getName());
                            startActivity(intent);
                        }
                    });
                    lblLoanBlockedReason.setVisibility(View.VISIBLE);
                } else {
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