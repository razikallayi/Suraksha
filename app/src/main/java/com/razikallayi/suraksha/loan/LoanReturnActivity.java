package com.razikallayi.suraksha.loan;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.razikallayi.suraksha.BaseActivity;
import com.razikallayi.suraksha.DatePickerFragment;
import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.member.Member;
import com.razikallayi.suraksha.txn.Transaction;
import com.razikallayi.suraksha.utils.CalendarUtils;
import com.razikallayi.suraksha.utils.FontUtils;
import com.razikallayi.suraksha.utils.SmsUtils;
import com.razikallayi.suraksha.utils.Utility;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class LoanReturnActivity extends BaseActivity {
    private static LoanIssue sLoanIssue;
    private EditText txtDateLoanReturn;
    private long loanReturnDate = 0;
    private List<Transaction> loanReturnList;

    public static void setLoanIssue(LoanIssue loanIssue) {
        sLoanIssue = loanIssue;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loan_return_activity);

        setupEnvironment();

        loanReturnList = sLoanIssue.getLoanReturnTxnList(getApplicationContext());
        final String instalmentAmount = Utility.formatAmountInRupees(getApplicationContext(), sLoanIssue.getLoanInstalmentAmount());
        String loanReturnInfo = "Receive " + Utility.formatNumberSuffix(loanReturnList.size() + 1) + " instalment "
                + instalmentAmount + " from " + sLoanIssue.getMember(getApplicationContext()).getName();
        TextView loanReturnInfoTv = (TextView) findViewById(R.id.loanReturnInfo);
        loanReturnInfoTv.setText(loanReturnInfo);

        txtDateLoanReturn = (EditText) findViewById(R.id.txtDateLoanReturn);

        attachCalendar();

        //LoanReturnButton Click Listener
        Button btnCloseLoanReturn = (Button) findViewById(R.id.btnCloseLoanReturn);
        btnCloseLoanReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button btnConfirmLoanReturn = (Button) findViewById(R.id.btnLoanReturn);
        btnConfirmLoanReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String remarks = ((EditText) findViewById(R.id.txtRemarks)).getText().toString();
                Context context= getApplicationContext();
                Transaction loanReturnTxn = sLoanIssue.saveLoanReturn(context, loanReturnDate, remarks);
                if (loanReturnTxn != null) {
                    if (SmsUtils.smsEnabledAfterLoanReturn(context)) {
                        Member member = sLoanIssue.getMember(context);
                        String mobileNumber = member.getMobile();
                        String message = member.getName() + ", " + Utility.formatAmountInRupees(context, loanReturnTxn.getAmount())
                                + " is payed in " + context.getResources().getString(R.string.app_name)
                                + " account " + member.getAccountNo()
                                + " as Loan Return " + sLoanIssue.getLoanReturnTxnList(context).size();

                        boolean result = SmsUtils.sendSms(message, mobileNumber);
                        if (result) {
                            Toast.makeText(context, "SMS sent to " + member.getName(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    finish();
                } else {
                    Toast.makeText(LoanReturnActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void attachCalendar() {
        if (loanReturnDate == 0) {
            if (loanReturnList.size() == 0) {
                loanReturnDate = CalendarUtils.normalizeDate(System.currentTimeMillis());
            } else {
                Calendar lastReturnDate = new GregorianCalendar();
                lastReturnDate.setTimeInMillis(loanReturnList.get(loanReturnList.size() - 1).getLoanReturnedDate());
                lastReturnDate.add(Calendar.MONTH, 1);
                loanReturnDate = lastReturnDate.getTimeInMillis();
            }
        }
        txtDateLoanReturn.setText(CalendarUtils.formatDate(loanReturnDate));
        txtDateLoanReturn.setKeyListener(null); //disable user input
        txtDateLoanReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupCalendar();
            }
        });

        TextView calendar_fa_icon = (TextView) findViewById(R.id.calendar_fa_icon);
        calendar_fa_icon.setTypeface(FontUtils.getTypeface(getApplicationContext(), FontUtils.FONTAWSOME));
        calendar_fa_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupCalendar();
            }
        });
    }

    private void popupCalendar() {
        final Calendar activeDate = Calendar.getInstance();
        activeDate.setTimeInMillis(loanReturnDate);
        final DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(activeDate);
        datePickerFragment.show(getSupportFragmentManager(), null);
        datePickerFragment.setMinDate(sLoanIssue.getIssuedAt());
        datePickerFragment.setOnDateSetListener(new DatePickerFragment.OnDateSetListener() {
            @Override
            public void getDate(Calendar cal) {
                Calendar calendar = CalendarUtils.normalizeDate(cal);
                loanReturnDate = calendar.getTimeInMillis();
                txtDateLoanReturn.setText(CalendarUtils.formatDate(loanReturnDate));
            }
        });
    }

    private void setupEnvironment() {

//        //reduce window width size to 80%
//        DisplayMetrics metrics = getResources().getDisplayMetrics();
//        int screenWidth = (int) (metrics.widthPixels * 0.8);
//        getWindow().setLayout(screenWidth, ViewGroup.LayoutParams.MATCH_PARENT);

        //Setup the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close);
            actionBar.setHomeActionContentDescription("Close");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        //Enable full view scroll while soft keyboard is shown
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

}


