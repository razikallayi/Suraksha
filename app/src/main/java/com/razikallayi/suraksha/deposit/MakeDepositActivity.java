package com.razikallayi.suraksha.deposit;

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

import com.razikallayi.suraksha.BaseActivity;
import com.razikallayi.suraksha.DatePickerFragment;
import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.member.Member;
import com.razikallayi.suraksha.txn.Transaction;
import com.razikallayi.suraksha.utils.AuthUtils;
import com.razikallayi.suraksha.utils.CalendarUtils;
import com.razikallayi.suraksha.utils.FontUtils;
import com.razikallayi.suraksha.utils.SmsUtils;
import com.razikallayi.suraksha.utils.Utility;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class MakeDepositActivity extends BaseActivity {
    public static final String ARG_DEPOSIT_TXN_ID = "deposit_txn_id";
    public static final String ARG_MEMBER_ID = "member_id";
    private Transaction mDepositTxn;
    private EditText txtPaymentDate;
    private EditText txtRemarks;
    private long mPaymentDate = 0;
    private boolean editMode = false;
    private Member mMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date_confirm_activity);

        setupEnvironment();

        long memberId = getIntent().getLongExtra(ARG_MEMBER_ID, -1);
        mMember = Member.getMemberFromId(this, memberId);
        long depositTxnId = getIntent().getLongExtra(ARG_DEPOSIT_TXN_ID, -1);
        if (depositTxnId != -1) {
            editMode = true;
            mDepositTxn = Transaction.getTxnFromId(this, depositTxnId);
        }

        RelativeLayout layoutMemberInfo = findViewById(R.id.layoutMemberInfo);
        mMember.setMemberInfo(this, layoutMemberInfo);

        final Calendar nextDepositMonth = mMember.getNextDepositMonthCalendar(this);

        String nextActionInfo;
        if (!mMember.isAccountClosed()) {
            final String depositAmount = Utility.formatAmountInRupees(this, Utility.getMonthlyDepositAmount());
            String updateString = "Deposit ";
            if (editMode) {
                updateString = "Update deposit ";
            }
            nextActionInfo = updateString + depositAmount + " of "
                    + CalendarUtils.readableDepositMonth(nextDepositMonth)
                    + " for " + mMember.getName() + "[" + mMember.getAccountNo() + "]";
        } else {
            nextActionInfo = "Account Closed on " + CalendarUtils.formatDate(mMember.getClosedAt());
        }
        TextView nextActionInfoTv = findViewById(R.id.paymentInfo);
        nextActionInfoTv.setText(nextActionInfo);
        txtRemarks = findViewById(R.id.txtRemarks);
        if (editMode) {
            txtRemarks.setText(mDepositTxn.getNarration());
        }
        txtPaymentDate = findViewById(R.id.txtpaymentDate);

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
        btnConfirm.setBackground(getDrawable(R.drawable.touch_selector_blue));
        if (editMode) {
            btnConfirm.setText("Update");
        } else {
            btnConfirm.setText("Confirm");
        }
        if (mMember.isAccountClosed()) {
            btnConfirm.setVisibility(View.GONE);
        } else {
            btnConfirm.setVisibility(View.VISIBLE);
        }
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnConfirm.setEnabled(false);
                String remarks = txtRemarks.getText().toString();
                Transaction txn;
                if (editMode) {
                    txn = mMember.updateDeposit(MakeDepositActivity.this, mDepositTxn, mPaymentDate, remarks);
                } else {
                    txn = makeDeposit(MakeDepositActivity.this, nextDepositMonth.getTimeInMillis(), mPaymentDate, remarks);
                }

                if (txn != null) {
                    Intent returnIntent = getIntent();
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                } else {
                    Toast.makeText(MakeDepositActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public Transaction makeDeposit(Context context, long depositMonth, long paymentDate, String remarks) {
        Transaction txn = mMember.makeDeposit(context, depositMonth, paymentDate, remarks);
        if (txn != null && SmsUtils.smsEnabledAfterDeposit(context) && mMember.isSmsEnabled()) {
            String mobileNumber = mMember.getMobile();
            String message = mMember.getName() + ", your " + context.getResources().getString(R.string.app_name)
                    + " account[" + mMember.getAccountNo()
                    + "] is credited with a deposit of " + (int) txn.getAmount()
                    + " for " + CalendarUtils.readableDepositMonth(txn.getDefinedDepositMonth());
            boolean result = SmsUtils.sendSms(message, mobileNumber);
            if (result) {
                Toast.makeText(context, "SMS sent to " + mMember.getName(), Toast.LENGTH_SHORT).show();
            }
        }
        return txn;
    }

    private void attachCalendar() {
        txtPaymentDate.setText(CalendarUtils.formatDate(nextDepositDate()));
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

    private long nextDepositDate() {
        if (editMode) {
            return mPaymentDate = CalendarUtils.normalizeDate(mDepositTxn.getPaymentDate());
        }
        if (mPaymentDate != 0) {
            return mPaymentDate = CalendarUtils.normalizeDate(mPaymentDate);
        }
        Calendar nextDepositDate = new GregorianCalendar();
        Transaction lastDeposit = mMember.getLastDepositMonthTxn(this);
        if (lastDeposit == null) {
            return mPaymentDate = CalendarUtils.normalizeDate(System.currentTimeMillis());
        } else {
            nextDepositDate.setTimeInMillis(lastDeposit.getPaymentDate());

            nextDepositDate.add(Calendar.MONTH, 1);
            if (nextDepositDate.getTimeInMillis() > System.currentTimeMillis()) {
                return mPaymentDate = CalendarUtils.normalizeDate(System.currentTimeMillis());
            } else {
                return mPaymentDate = CalendarUtils.normalizeDate(nextDepositDate.getTimeInMillis());
            }
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (AuthUtils.isAdmin(this) && editMode) {
            if (mDepositTxn == null) {
                long depositTxnId = getIntent().getLongExtra(ARG_DEPOSIT_TXN_ID, -1);
                mDepositTxn = Transaction.getTxnFromId(this, depositTxnId);
            }
            if (mMember == null) {
                long memberId = getIntent().getLongExtra(ARG_MEMBER_ID, -1);
                mMember = Member.getMemberFromId(this, memberId);
            }
            if (!mMember.isAccountClosed()) {
                Transaction lastMonthTxn = mMember.getLastDepositMonthTxn(this);
                if (mDepositTxn.getId() == lastMonthTxn.getId()) {
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
            if (mDepositTxn != null) {
                int rowsUpdated = mDepositTxn.destroy(this);
                if(rowsUpdated>0){
                    Intent returnIntent = getIntent();
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }else{
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
            toolbar.setTitle("Update Deposit");
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


