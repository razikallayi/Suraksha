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
    public static final String ARG_LOAN_RETURN_TXT_ID = "loan_return_txt_id";
    public static final String ARG_LOAN_ISSUE_ID = "loan_issue_id";
    private LoanIssue mLoanIssue;
    private Transaction mLoanReturnTxn;
    private EditText txtDateLoanReturn;
    private EditText txtRemarks;
    private long loanReturnDate = 0;
    private List<Transaction> loanReturnList;
    private boolean editMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loan_return_activity);

        setupEnvironment();

        if (editMode) {
            getSupportActionBar().setTitle(R.string.update_loan_return);
        }

        long loanIssueId = getIntent().getLongExtra(ARG_LOAN_ISSUE_ID, -1);
        long loanReturnTxnId = getIntent().getLongExtra(ARG_LOAN_RETURN_TXT_ID, -1);
        if (loanReturnTxnId != -1) {
            editMode = true;
            mLoanReturnTxn = Transaction.getTxnFromId(this, loanReturnTxnId);
            loanIssueId = mLoanReturnTxn.getLoanPayedId();
        }
        if (loanIssueId != -1) {
            mLoanIssue = LoanIssue.getLoanIssue(this, loanIssueId);
        }

        loanReturnList = mLoanIssue.getLoanReturnTxnList(getApplicationContext());
        final String instalmentAmount = Utility.formatAmountInRupees(getApplicationContext(), mLoanIssue.getLoanInstalmentAmount());
        String receiveString = "Receive ";
        if (editMode) {
            receiveString = "Update ";
        }
        String loanReturnInfo = receiveString + Utility.formatNumberSuffix(loanReturnList.size() + 1) + " instalment "
                + instalmentAmount + " from " + mLoanIssue.getMember(getApplicationContext()).getName();
        TextView loanReturnInfoTv = findViewById(R.id.loanReturnInfo);
        loanReturnInfoTv.setText(loanReturnInfo);
        txtRemarks = findViewById(R.id.txtRemarks);
        if (editMode) {
            txtRemarks.setText(mLoanReturnTxn.getNarration());
        }
        txtDateLoanReturn = findViewById(R.id.txtDateLoanReturn);

        attachCalendar();

        //LoanReturnButton Click Listener
        Button btnCloseLoanReturn = findViewById(R.id.btnCloseLoanReturn);
        btnCloseLoanReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button btnConfirmLoanReturn = findViewById(R.id.btnLoanReturn);
        if (editMode) {
            btnConfirmLoanReturn.setText(R.string.update_loan_return);
        } else {
            btnConfirmLoanReturn.setText("Confirm Return");
        }
        btnConfirmLoanReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String remarks = txtRemarks.getText().toString();
                Context context = getApplicationContext();
                if (editMode) {
                    mLoanReturnTxn = mLoanIssue.updateLoanReturn(context, mLoanReturnTxn, loanReturnDate, remarks);
                } else {
                    mLoanReturnTxn = mLoanIssue.saveLoanReturn(context, loanReturnDate, remarks);
                }
                if (mLoanReturnTxn != null) {
                    if (SmsUtils.smsEnabledAfterLoanReturn(context) && !editMode) {
                        Member member = mLoanIssue.getMember(context);
                        String mobileNumber = member.getMobile();
                        String message = member.getName() + ", " + Utility.formatAmountInRupees(context, mLoanReturnTxn.getAmount())
                                + " is payed in " + context.getResources().getString(R.string.app_name)
                                + " account " + member.getAccountNo()
                                + " as Loan Return " + mLoanIssue.getLoanReturnTxnList(context).size();

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
        txtDateLoanReturn.setText(CalendarUtils.formatDate(nextReturnDate()));
        txtDateLoanReturn.setKeyListener(null); //disable user input
        txtDateLoanReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupCalendar();
            }
        });

        TextView calendar_fa_icon = findViewById(R.id.calendar_fa_icon);
        calendar_fa_icon.setTypeface(FontUtils.getTypeface(getApplicationContext(), FontUtils.FONTAWSOME));
        calendar_fa_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupCalendar();
            }
        });
    }

    private long nextReturnDate() {
        if (editMode) {
            return loanReturnDate = mLoanReturnTxn.getLoanReturnDate();
        }
        if (loanReturnDate != 0) {
            return loanReturnDate;
        }
        Calendar nextReturnDate = new GregorianCalendar();
        loanReturnList = mLoanIssue.getLoanReturnTxnList(getApplicationContext());
        if (loanReturnList.size() != 0) {
            nextReturnDate.setTimeInMillis(loanReturnList.get(loanReturnList.size() - 1).getLoanReturnDate());
        } else {
            nextReturnDate.setTimeInMillis(mLoanIssue.getIssuedAt());
        }
        nextReturnDate.add(Calendar.MONTH, 1);
        if (nextReturnDate.getTimeInMillis() > System.currentTimeMillis()) {
            return loanReturnDate = CalendarUtils.normalizeDate(System.currentTimeMillis());
        } else {
            return loanReturnDate = nextReturnDate.getTimeInMillis();
        }
    }

    private void popupCalendar() {
        final Calendar activeDate = Calendar.getInstance();
        activeDate.setTimeInMillis(loanReturnDate);
        final DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(activeDate);
        datePickerFragment.show(getSupportFragmentManager(), null);
        datePickerFragment.setMinDate(mLoanIssue.getIssuedAt());
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
        Toolbar toolbar = findViewById(R.id.toolbar);
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


