package com.razikallayi.suraksha_ssf.member;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.razikallayi.suraksha_ssf.BaseActivity;
import com.razikallayi.suraksha_ssf.DatePickerFragment;
import com.razikallayi.suraksha_ssf.R;
import com.razikallayi.suraksha_ssf.loan.LoanIssue;
import com.razikallayi.suraksha_ssf.utils.CalendarUtils;
import com.razikallayi.suraksha_ssf.utils.FontUtils;
import com.razikallayi.suraksha_ssf.utils.Utility;

import java.util.Calendar;

public class CloseAccountActivity extends BaseActivity {
    public static final String ARG_MEMBER_ID = "member_id";
    private EditText txtPaymentDate;
    private EditText txtRemarks;
    private long mPaymentDate = 0;
    private Member mMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date_confirm_activity);

        setupEnvironment();

        long memberId = getIntent().getLongExtra(ARG_MEMBER_ID, -1);
        mMember = Member.getMemberFromId(this, memberId);

        RelativeLayout layoutMemberInfo = findViewById(R.id.layoutMemberInfo);
        mMember.setMemberInfo(this, layoutMemberInfo);

        double sumOfDeposits = mMember.sumOfDeposits(CloseAccountActivity.this);
        final StringBuilder info = new StringBuilder();
        StringBuilder infoDetail = new StringBuilder();
        Double totalRepayable;
        LoanIssue activeBystanderLoan = mMember.getActiveBystanderLoan(CloseAccountActivity.this);
        if (activeBystanderLoan == null) {//if not a security for any loans
            infoDetail.append("Deposits: ")
                    .append(Utility.formatAmountInRupees(CloseAccountActivity.this, sumOfDeposits));
            totalRepayable = sumOfDeposits;
            LoanIssue activeLoan = mMember.getActiveLoan(CloseAccountActivity.this);
            if (activeLoan != null) {//if  has any active loan
                int lastInstalmentNo = activeLoan.lastInstalmentNumber(CloseAccountActivity.this);
                infoDetail.append("\n\nActive Loan: ")
                        .append(Utility.formatAmountInRupees(CloseAccountActivity.this,
                                activeLoan.getAmount()));
                if (lastInstalmentNo > 0) {
                    double sumOfLoanReturns = activeLoan.sumOfLoanReturns(this);
                    infoDetail.append("\nLoan Returns: ")
                            .append(Utility.formatAmountInRupees(this, sumOfLoanReturns));
                }
                if (!activeLoan.isByStanderReleased(lastInstalmentNo)) {
                    Member securityMember = activeLoan.getSecurityMember(CloseAccountActivity.this);
                    if (securityMember != null) {
                        infoDetail.append("\nSecurity Member: ")
                                .append(securityMember.getName());
                    }
                }
                double payedInstalmentAmount = lastInstalmentNo * activeLoan.getLoanInstalmentAmount();
                double pendingLoanAmount = activeLoan.getAmount() - payedInstalmentAmount;
                totalRepayable = totalRepayable - pendingLoanAmount;
            }

            if (totalRepayable == 0) {
                info.append("All transactions are clear. Close the account.");
            } else {
                String payOrReceive;
                String fromOrTo;
                if (totalRepayable > 0) {
                    payOrReceive = "Pay ";
                    fromOrTo = " to ";
                } else {
                    payOrReceive = "Receive ";
                    fromOrTo = " from ";
                }
                info.append(payOrReceive)
                        .append(Utility.formatAmountInRupees(CloseAccountActivity.this,
                                Math.abs(totalRepayable)))
                        .append(fromOrTo)
                        .append(mMember.getName())
                        .append(" and close account");
            }
        } else {
            String reason = mMember.getName() + " is a security member for "
                    + activeBystanderLoan.getMember(this).getName()
                    + ". Please close the loan first.";
            Toast.makeText(this, reason, Toast.LENGTH_SHORT).show();
            return;
        }


        TextView infoTv = findViewById(R.id.paymentInfo);
        TextView infoDetailTv = findViewById(R.id.paymentInfoDetail);
        infoTv.setText(Html.fromHtml(info.toString()));
        infoTv.setTextColor(Color.BLACK);
        infoDetailTv.setText(infoDetail.toString()
        );
        infoDetailTv.setVisibility(View.VISIBLE);
        txtRemarks = findViewById(R.id.txtRemarks);
        txtPaymentDate = findViewById(R.id.txtpaymentDate);
        TextView warning = findViewById(R.id.warning);
        warning.setText(R.string.close_account_note);
        warning.setVisibility(View.VISIBLE);
        attachCalendar();

        Button btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = getIntent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });

        final Button btnConfirm = findViewById(R.id.btnConfirm);
        btnConfirm.setBackground(getDrawable(R.drawable.touch_selector_orange));
        btnConfirm.setText("Confirm");
        final Double finalTotalRepayable = totalRepayable;
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnConfirm.setEnabled(false);
                new AlertDialog.Builder(CloseAccountActivity.this)
                        .setTitle("Good Bye!")
                        .setMessage("Final Confirmation. Are you sure want to close the account?\n" + info)
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        String remarks = txtRemarks.getText().toString();
                                        int rowsUpdated = mMember.closeAccount(CloseAccountActivity.this,
                                                finalTotalRepayable, mPaymentDate, remarks);
                                        if (rowsUpdated <= 0) {
                                            Toast.makeText(CloseAccountActivity.this,
                                                    "Failed. The account may be already closed or the member may be a security for another loan",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                        Intent returnIntent = getIntent();
                                        setResult(Activity.RESULT_OK, returnIntent);
                                        finish();
                                    }
                                }
                        )
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                btnConfirm.setEnabled(true);
                                dialog.cancel();
                            }
                        })
                        .create().show();

            }
        });
    }


    private void attachCalendar() {
        if (mPaymentDate != 0) {
            mPaymentDate = CalendarUtils.normalizeDate(mPaymentDate);
        } else {
            mPaymentDate = CalendarUtils.normalizeDate(System.currentTimeMillis());
        }
        txtPaymentDate.setText(CalendarUtils.formatDate(mPaymentDate));
        txtPaymentDate.setKeyListener(null); //disable user input
        txtPaymentDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupCalendar();
            }
        });

        TextView calendarFaIcon = findViewById(R.id.calendar_fa_icon);
        calendarFaIcon.setTypeface(FontUtils.getTypeface(getApplicationContext(), FontUtils.FONTAWSOME));
        calendarFaIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupCalendar();
            }
        });
    }

    private void popupCalendar() {
        final Calendar activeDate = Calendar.getInstance();
        activeDate.setTimeInMillis(mPaymentDate);
        final DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(activeDate);
        datePickerFragment.show(getSupportFragmentManager(), null);
        datePickerFragment.setMinDate(CalendarUtils.getSurakshaStartDate().getTimeInMillis());
        datePickerFragment.setOnDateSetListener(new DatePickerFragment.OnDateSetListener() {
            @Override
            public void getDate(Calendar cal) {
                Calendar calendar = CalendarUtils.normalizeDate(cal);
                mPaymentDate = calendar.getTimeInMillis();
                txtPaymentDate.setText(CalendarUtils.formatDate(mPaymentDate));
            }
        });
    }


    private void setupEnvironment() {

//        //reduce window width size to 80%
//        DisplayMetrics metrics = getResources().getDisplayMetrics();
//        int screenWidth = (int) (metrics.widthPixels * 0.8);
//        getWindow().setLayout(screenWidth, ViewGroup.LayoutParams.MATCH_PARENT);

        //Setup the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close);
            actionBar.setHomeActionContentDescription("Close");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Close Account");
        }

        //Enable full view scroll while soft keyboard is shown
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }


}


