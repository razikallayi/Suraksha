package com.razikallayi.suraksha_ssf.loan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.razikallayi.suraksha_ssf.member.Member;
import com.razikallayi.suraksha_ssf.txn.Transaction;
import com.razikallayi.suraksha_ssf.utils.AuthUtils;
import com.razikallayi.suraksha_ssf.utils.CalendarUtils;
import com.razikallayi.suraksha_ssf.utils.FontUtils;
import com.razikallayi.suraksha_ssf.utils.SmsUtils;
import com.razikallayi.suraksha_ssf.utils.Utility;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class ReturnLoanActivity extends BaseActivity {
    public static final String ARG_LOAN_RETURN_TXN_ID = "loan_return_tx_id";
    public static final String ARG_LOAN_ISSUE_ID = "loan_issue_id";
    private LoanIssue mLoanIssue;
    private Transaction mLoanReturnTxn;
    private EditText txtDateLoanReturn;
    private EditText txtRemarks;
    private long mLoanReturnPaymentDate = 0;
    private boolean editMode = false;
    private Member mMember;
    private Transaction mLastLoanReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date_confirm_activity);

        setupEnvironment();


        long loanIssueId = getIntent().getLongExtra(ARG_LOAN_ISSUE_ID, -1);
        long loanReturnTxnId = getIntent().getLongExtra(ARG_LOAN_RETURN_TXN_ID, -1);
        if (loanReturnTxnId != -1) {
            editMode = true;
            mLoanReturnTxn = Transaction.getTxnFromId(this, loanReturnTxnId);
            loanIssueId = mLoanReturnTxn.getLoanPayedId();
        }
        if (loanIssueId != -1) {
            mLoanIssue = LoanIssue.fetchLoanIssue(this, loanIssueId);
        }

        RelativeLayout layoutMemberInfo = findViewById(R.id.layoutMemberInfo);
        mMember = mLoanIssue.getMember(this);
        mMember.setMemberInfo(this, layoutMemberInfo);

        String nextActionInfo;
        if (!mMember.isAccountClosed()) {
            final String instalmentAmount = Utility.formatAmountInRupees(this,
                    mLoanIssue.getLoanInstalmentAmount());
            String receiveString;
            int instalmentNo;
            if (editMode) {
                receiveString = "Update ";
                instalmentNo = mLoanReturnTxn.getInstalmentNumber();
            } else {
                receiveString = "Receive ";
                instalmentNo = mLoanIssue.nextInstalmentNumber(this);
            }
            nextActionInfo = receiveString + Utility.formatNumberSuffix(instalmentNo
            ) + " instalment "
                    + instalmentAmount + " from " + mLoanIssue.getMember(this).getName();

            TextView nextActionInfoDetailTv = findViewById(R.id.paymentInfoDetail);
            Member securityMember = mLoanIssue.getSecurityMember(this);
            nextActionInfoDetailTv.setText(securityMember.getInfo());
            nextActionInfoDetailTv.setVisibility(View.VISIBLE);
        } else {
            nextActionInfo = "Account Closed on " + CalendarUtils.formatDate(mMember.getClosedAt());
        }
        TextView nextActionInfoTv = findViewById(R.id.paymentInfo);
        nextActionInfoTv.setText(nextActionInfo);
        txtRemarks = findViewById(R.id.txtRemarks);
        if (editMode) {
            txtRemarks.setText(mLoanReturnTxn.getNarration());
        }
        txtDateLoanReturn = findViewById(R.id.txtpaymentDate);

        attachCalendar();

        //LoanReturnButton Click Listener
        Button btnCloseLoanReturn = findViewById(R.id.btnClose);
        btnCloseLoanReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = getIntent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });

        final Button btnConfirm = findViewById(R.id.btnConfirm);
        if (mMember.isAccountClosed()) {
            btnConfirm.setVisibility(View.GONE);
        } else {
            btnConfirm.setVisibility(View.VISIBLE);
        }
        if (editMode) {
            btnConfirm.setText(R.string.update_loan_return);
        } else {
            btnConfirm.setText("Confirm Return");
        }
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnConfirm.setEnabled(false);
                String remarks = txtRemarks.getText().toString();
                Context context = ReturnLoanActivity.this;
                Transaction txn;
                if (editMode) {
                    txn = mLoanIssue.updateLoanReturn(context, mLoanReturnTxn, mLoanReturnPaymentDate, remarks);
                } else {
                    txn = mLoanReturnTxn = mLoanIssue.saveLoanReturn(context, mLoanReturnPaymentDate, remarks);
                    if (SmsUtils.smsEnabledAfterLoanReturn(context) && !editMode) {
                        String mobileNumber = mMember.getMobile();
                        String message = mMember.getName() + ", "
                                + Utility.formatNumberSuffix(mLoanReturnTxn.getInstalmentNumber()) + " Instalment Rs "
                                + (long) mLoanReturnTxn.getAmount()
                                + " is payed in your " + context.getResources().getString(R.string.app_name)
                                + " account [" + mMember.getAccountNo()
                                + "]. Remaining Rs "
                                + (long) mLoanReturnTxn.getInstalmentNumber() * mLoanIssue.getLoanInstalmentAmount();

                        boolean result = SmsUtils.sendSms(message, mobileNumber);
                        if (result) {
                            Toast.makeText(context, "SMS sent to " + mMember.getName(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                if (txn != null) {
                    Intent returnIntent = getIntent();
                    setResult(Activity.RESULT_OK, returnIntent);
                    returnIntent.putExtra(LoanReturnedListActivity.ARG_LAST_INSTALMENT_NO, txn.getInstalmentNumber());
                    finish();
                } else {
                    Toast.makeText(ReturnLoanActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
        calendar_fa_icon.setTypeface(FontUtils.getTypeface(this, FontUtils.FONTAWSOME));
        calendar_fa_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupCalendar();
            }
        });
    }

    private long nextReturnDate() {
        if (editMode) {
            return mLoanReturnPaymentDate = CalendarUtils.normalizeDate(mLoanReturnTxn.getPaymentDate());
        }
        if (mLoanReturnPaymentDate != 0) {
            return CalendarUtils.normalizeDate(mLoanReturnPaymentDate);
        }
        int lastInstalmentNo = mLoanIssue.lastInstalmentNumber(this);
        long nextReturnDate;
        if (lastInstalmentNo == 0) {
            nextReturnDate = mLoanIssue.instalmentDueDate(mLoanIssue.nextInstalmentNumber(this));
        } else {
            mLastLoanReturn = mLoanIssue.getInstalmentTxn(this, lastInstalmentNo);
            Calendar calNextReturnDate = new GregorianCalendar();
            calNextReturnDate.setTimeInMillis(mLastLoanReturn.getPaymentDate());
            calNextReturnDate.add(Calendar.MONTH, 1);
            nextReturnDate = calNextReturnDate.getTimeInMillis();
        }

        if (nextReturnDate > CalendarUtils.normalizeDate(System.currentTimeMillis())) {
            return mLoanReturnPaymentDate = CalendarUtils.normalizeDate(System.currentTimeMillis());
        } else {
            return mLoanReturnPaymentDate = CalendarUtils.normalizeDate(nextReturnDate);
        }
    }

    private void popupCalendar() {
        final Calendar activeDate = Calendar.getInstance();
        activeDate.setTimeInMillis(mLoanReturnPaymentDate);
        final DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(activeDate);
        datePickerFragment.show(getSupportFragmentManager(), null);
        if (mLastLoanReturn == null) {
            datePickerFragment.setMinDate(mLoanIssue.getIssuedAt());
        } else {
            datePickerFragment.setMinDate(mLastLoanReturn.getPaymentDate());
        }
        datePickerFragment.setOnDateSetListener(new DatePickerFragment.OnDateSetListener() {
            @Override
            public void getDate(Calendar cal) {
                Calendar calendar = CalendarUtils.normalizeDate(cal);
                mLoanReturnPaymentDate = calendar.getTimeInMillis();
                txtDateLoanReturn.setText(CalendarUtils.formatDate(mLoanReturnPaymentDate));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (AuthUtils.isAdmin(this) && editMode) {
            if (mLoanIssue == null) {
                long loanIssueId = getIntent().getLongExtra(ARG_LOAN_RETURN_TXN_ID, -1);
                mLoanIssue = LoanIssue.fetchLoanIssue(this, loanIssueId);
            }
            if (mLoanReturnTxn == null) {
                long loanReturnTxnId = getIntent().getLongExtra(ARG_LOAN_RETURN_TXN_ID, -1);
                mLoanReturnTxn = Transaction.getTxnFromId(this, loanReturnTxnId);
            }
            if(mMember == null){
                mMember = mLoanIssue.getMember(this);
            }

            if (!mMember.isAccountClosed()) {
                if(mLastLoanReturn == null) {
                    mLastLoanReturn = mLoanIssue.lastInstalmentTxn(this);
                }
                if (mLoanReturnTxn.getId() == mLastLoanReturn.getId()) {
                    MenuInflater inflater = getMenuInflater();
                    inflater.inflate(R.menu.menu_payment_form_delete, menu);
                }
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.delete) {
            if (mLoanIssue != null) {
                int rowsUpdated =  mLoanIssue.deleteLastLoanReturn(this);
                if(rowsUpdated >=1){
                    Intent returnIntent = getIntent();
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }else if(rowsUpdated == 0){
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupEnvironment() {

//        //reduce window width size to 80%
//        DisplayMetrics metrics = getResources().getDisplayMetrics();
//        int screenWidth = (int) (metrics.widthPixels * 0.8);
//        getWindow().setLayout(screenWidth, ViewGroup.LayoutParams.MATCH_PARENT);

        //Setup the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (editMode) {
            toolbar.setTitle(R.string.update_loan_return);
        }
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