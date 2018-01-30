package com.razikallayi.suraksha_ssf.loan;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.razikallayi.suraksha_ssf.BaseActivity;
import com.razikallayi.suraksha_ssf.DatePickerFragment;
import com.razikallayi.suraksha_ssf.NumberToWords;
import com.razikallayi.suraksha_ssf.R;
import com.razikallayi.suraksha_ssf.data.SurakshaContract;
import com.razikallayi.suraksha_ssf.member.Member;
import com.razikallayi.suraksha_ssf.txn.Transaction;
import com.razikallayi.suraksha_ssf.utils.AuthUtils;
import com.razikallayi.suraksha_ssf.utils.CalendarUtils;
import com.razikallayi.suraksha_ssf.utils.FontUtils;
import com.razikallayi.suraksha_ssf.utils.LoanUtils;
import com.razikallayi.suraksha_ssf.utils.SmsUtils;
import com.razikallayi.suraksha_ssf.utils.WordUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class IssueLoanActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        View.OnClickListener {

    public static final String ARG_MEMBER_ID = "member_id";
    public static final String ARG_LOAN_ISSUE_ID = "loan_issue_id";
    public static final String ARG_ACCOUNT_NUMBER = "account_number";
    private static final int SECURITY_MEMBER_NAME_LOADER = 0X43;

    private Member mMember;
    private LoanIssue mLoanIssue;
    private int mSecurityAccountNo = -1;

    private long mMaxLoanAmount = 20000;
    private int mDefaultInstalmentTimes = 10;

    private EditText txtDateIssueLoan;
    private EditText txtAmountIssueLoan;
    private EditText txtPurposeIssueLoan;
    private AutoCompleteTextView txtSecurityMemberName;
    private EditText txtOfficeStatement;
    private Button btnIssueLoan;
    private EditText txtLoanInstalmentAmount;
    private EditText txtLoanInstalmentTimes;
    private long mLoanIssueDate = 0;
    private boolean editMode = false;
    private ImageView clearAutoComplete;
    private TextView editNoteIssueLoan;
    private TextView calendar_fa_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loan_issue_activity);

        setupEnvironment();

        //Get Active Member Details from Intent
        long memberId = getIntent().getLongExtra(ARG_MEMBER_ID, -1);
        mMember = Member.getMemberFromId(getApplicationContext(), memberId);
        long loanIssueId = getIntent().getLongExtra(ARG_LOAN_ISSUE_ID, -1);
        if (loanIssueId != -1) {
            editMode = true;
            mLoanIssue = LoanIssue.getLoanIssue(this, loanIssueId);
        }
//        mAccountNo = getIntent().getIntExtra(ARG_ACCOUNT_NUMBER, -1);

        if (editMode) {
            getSupportActionBar().setTitle("Update Issued Loan");
        }
        getSupportLoaderManager().initLoader(SECURITY_MEMBER_NAME_LOADER, null, this);

        //Initialise Fields
        NestedScrollView sv = findViewById(R.id.LoanIssueForm);
        RelativeLayout layoutMemberInfo = sv.findViewById(R.id.layoutMemberInfo);
        mMember.setMemberInfo(this, layoutMemberInfo);
//        setMemberInfo(this, mMember.getName(), mMember.getAddress(),
//                mMember.getAccountNo(), mMember.getAvatarDrawable());
        //Declaring editTexts of loan Amount and instalment
        txtDateIssueLoan = sv.findViewById(R.id.txtDateIssueLoan);
        calendar_fa_icon = findViewById(R.id.calendar_fa_icon);
        txtAmountIssueLoan = sv.findViewById(R.id.txtAmountIssueLoan);
        txtLoanInstalmentTimes = sv.findViewById(R.id.txtLoanInstalmentTimes);
        txtLoanInstalmentAmount = sv.findViewById(R.id.txtLoanInstalmentAmount);
        txtPurposeIssueLoan = sv.findViewById(R.id.txtPurposeIssueLoan);
        txtOfficeStatement = sv.findViewById(R.id.txtOfficeStatement);
        txtSecurityMemberName = sv.findViewById(R.id.txtSecurityMemberName);
        clearAutoComplete = sv.findViewById(R.id.clearAutoComplete);
        editNoteIssueLoan = sv.findViewById(R.id.editNoteIssueLoan);
        editNoteIssueLoan.setVisibility(View.GONE);
        Button btnCloseLoanIssue = findViewById(R.id.btnCloseLoanIssue);
        btnCloseLoanIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final TextView lblAmountInWordsLoanIssue = sv.findViewById(R.id.lblAmountInWordsLoanIssue);

        txtSecurityMemberName.setThreshold(1);

        if (clearAutoComplete != null) {
            clearAutoComplete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtSecurityMemberName.setText("");
                    mSecurityAccountNo = -1;
                    clearAutoComplete.setVisibility(View.GONE);
                }
            });
        }

        //AttachCalendar
        attachCalendar();

        //Setting default values in editTexts
        if (editMode && mLoanIssue != null) {
            List<Transaction> loanReturnList = mLoanIssue.getLoanReturnTxnList(this);
            if (loanReturnList != null && !loanReturnList.isEmpty()) {
                editNoteIssueLoan.setVisibility(View.VISIBLE);
                txtLoanInstalmentTimes.setKeyListener(null);
                txtAmountIssueLoan.setKeyListener(null);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    txtAmountIssueLoan.setTextColor(getResources().getColor(R.color.gray, null));
                    txtDateIssueLoan.setTextColor(getResources().getColor(R.color.gray, null));
                    txtLoanInstalmentTimes.setTextColor(getResources().getColor(R.color.gray, null));
                } else {
                    txtAmountIssueLoan.setTextColor(getResources().getColor(R.color.gray));
                    txtDateIssueLoan.setTextColor(getResources().getColor(R.color.gray));
                    txtLoanInstalmentTimes.setTextColor(getResources().getColor(R.color.gray));
                }
                disableCalendar();
            }

            txtLoanInstalmentTimes.setText(String.valueOf(mLoanIssue.getLoanInstalmentTimes()));
            txtAmountIssueLoan.setText(String.valueOf(mLoanIssue.getAmount()));
            String inWords = NumberToWords.convert((long) mLoanIssue.getAmount());
            lblAmountInWordsLoanIssue.setText(WordUtils.toTitleCase(inWords));
            txtLoanInstalmentAmount.setText(String.valueOf(mLoanIssue.getLoanInstalmentAmount()).trim());


            String memberDetails = formatSecurityMemberDropdownText(mLoanIssue.getSecurityAccountNo()
                    , mLoanIssue.getSecurityMember(this).getName()
                    , mLoanIssue.getSecurityMember(this).getAddress());
            txtSecurityMemberName.setText(memberDetails);
            txtSecurityMemberName.setKeyListener(null);
            clearAutoComplete.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                txtSecurityMemberName.setTextColor(getResources().getColor(R.color.gray, null));
            } else {
                txtSecurityMemberName.setTextColor(getResources().getColor(R.color.gray));
            }
            mSecurityAccountNo = mLoanIssue.getSecurityAccountNo();
            txtOfficeStatement.setText(mLoanIssue.getOfficeStatement());
            txtPurposeIssueLoan.setText(mLoanIssue.getPurpose());
        } else {
            mDefaultInstalmentTimes = Integer.parseInt(LoanUtils.getDefaultLoanInstalmentTimes(getApplicationContext()));
            txtLoanInstalmentTimes.setText(String.valueOf(mDefaultInstalmentTimes));
            mMaxLoanAmount = (long) mMember.sumOfDeposits(this) * 2;
            txtAmountIssueLoan.setText(String.valueOf(mMaxLoanAmount));
            String inWords = NumberToWords.convert((long) Double.parseDouble(String.valueOf(mMaxLoanAmount)));
            lblAmountInWordsLoanIssue.setText(WordUtils.toTitleCase(inWords));
            Double instalmentAmount = (double) (mMaxLoanAmount / mDefaultInstalmentTimes);
            txtLoanInstalmentAmount.setText(String.valueOf(instalmentAmount));
        }

        txtAmountIssueLoan.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    if (TextUtils.isEmpty(txtAmountIssueLoan.getText())) {
                        //disable instalment EditTexts if loan amount is empty
                        txtLoanInstalmentTimes.setText(String.valueOf(LoanUtils.getDefaultLoanInstalmentTimes(getApplicationContext())));
                        txtLoanInstalmentTimes.setEnabled(false);
                        txtLoanInstalmentAmount.setEnabled(false);
                        lblAmountInWordsLoanIssue.setText("");
                    } else {
                        String inWords = NumberToWords.convert((long) Double.parseDouble(String.valueOf(txtAmountIssueLoan.getText())));
                        lblAmountInWordsLoanIssue.setText(WordUtils.toTitleCase(inWords));
                        txtLoanInstalmentTimes.setEnabled(true);
                        txtLoanInstalmentAmount.setEnabled(true);
                        if (!TextUtils.isEmpty(txtLoanInstalmentTimes.getText())) {
                            calculateInstalmentAmount();
                        } else if (!TextUtils.isEmpty(txtLoanInstalmentAmount.getText())) {
                            calculateInstalmentTimes();
                        }
                    }
                }
                return false;
            }
        });

        txtLoanInstalmentTimes.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    calculateInstalmentAmount();
                    return true;
                }
                return false;
            }
        });

        txtLoanInstalmentAmount.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    calculateInstalmentTimes();
                    return true;
                }
                return false;
            }
        });

        //IssueLoanButton Click Listener
        btnIssueLoan = sv.findViewById(R.id.btnIssueLoan);
        if (editMode) {
            btnIssueLoan.setText("Update Issued Loan");
        } else {
            btnIssueLoan.setText("Issue Loan");
        }
        btnIssueLoan.setOnClickListener(this);
    }


    private LoanIssue getLoanDetailsFromInput(LoanIssue mLoanIssue) {
        double amount = Double.parseDouble(String.valueOf(txtAmountIssueLoan.getText()));
        int times = Integer.parseInt(String.valueOf(txtLoanInstalmentTimes.getText()));
        String officeStatement = String.valueOf(txtOfficeStatement.getText());

        String purpose = txtPurposeIssueLoan.getText().toString();
        int securityAccountNo = mSecurityAccountNo;
        if (mLoanIssue == null) {
            mLoanIssue = new LoanIssue(mMember.getAccountNo(), amount, purpose,
                    securityAccountNo, times, officeStatement, mLoanIssueDate);
            mLoanIssue.setMember(mMember);
        } else {
            mLoanIssue.setAmount(amount);
            mLoanIssue.setPurpose(purpose);
            mLoanIssue.setLoanInstalmentTimes(times);
            mLoanIssue.setOfficeStatement(officeStatement);
            mLoanIssue.setIssuedAt(mLoanIssueDate);
        }
        return mLoanIssue;
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

    private void disableCalendar() {
        txtDateIssueLoan.setOnClickListener(null);
        calendar_fa_icon.setOnClickListener(null);
        txtDateIssueLoan.setClickable(false);
        calendar_fa_icon.setClickable(false);
    }

    private void attachCalendar() {
        setLoanIssueDate(fetchLoanIssueDate());
        txtDateIssueLoan.setKeyListener(null);
        txtDateIssueLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupCalendar();
            }
        });

        calendar_fa_icon.setTypeface(FontUtils.getTypeface(getApplicationContext(), FontUtils.FONTAWSOME));
        calendar_fa_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupCalendar();
            }
        });
    }

    private void setLoanIssueDate(long date) {
        if (txtDateIssueLoan != null) {
            txtDateIssueLoan.setText(CalendarUtils.formatDate(date));
        }
    }

    private long fetchLoanIssueDate() {
        if (editMode) {
            return mLoanIssueDate = mLoanIssue.getIssuedAt();
        }
        if (LoanUtils.useCurrentDateForLoanIssue(this)) {
            return mLoanIssueDate = System.currentTimeMillis();
        } else {
            String defaultLoanIssueDate = LoanUtils.getDefaultLoanIssueDate(this);
            return mLoanIssueDate = Long.parseLong(defaultLoanIssueDate);
        }
    }

    private void popupCalendar() {
        final Calendar activeDate = Calendar.getInstance();
        activeDate.setTimeInMillis(mLoanIssueDate);
        DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(activeDate);
        datePickerFragment.show(getSupportFragmentManager(), null);
        datePickerFragment.setOnDateSetListener(new DatePickerFragment.OnDateSetListener() {
            @Override
            public void getDate(Calendar cal) {
                Calendar calendar = CalendarUtils.normalizeDate(cal);
                mLoanIssueDate = calendar.getTimeInMillis();
                setLoanIssueDate(mLoanIssueDate);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        setLoanIssueDate(fetchLoanIssueDate());
    }

    private void calculateInstalmentAmount() {
        Double amount = Double.parseDouble(String.valueOf(txtAmountIssueLoan.getText()));
        String strTimes = String.valueOf(txtLoanInstalmentTimes.getText());
        if (!strTimes.isEmpty()) {
            int times = Integer.parseInt(strTimes);
            if (amount > 0 && times > 0) {
                txtLoanInstalmentAmount.setText(String.valueOf(amount / times));
            }
        } else {
            txtLoanInstalmentAmount.setText("");
        }
    }

    private void calculateInstalmentTimes() {
        Double amount = Double.parseDouble(String.valueOf(txtAmountIssueLoan.getText()));
        String strInstalmentAmount = String.valueOf(txtLoanInstalmentAmount.getText());
        if (!strInstalmentAmount.isEmpty()) {
            int instalmentAmount = Integer.parseInt(strInstalmentAmount);
            if (amount > 0 && instalmentAmount > 0) {
                int instalmentTimes = (int) (amount / instalmentAmount);
                txtLoanInstalmentTimes.setText(String.valueOf(instalmentTimes));
            }
        } else {
            txtLoanInstalmentTimes.setText("");
        }
    }


    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                SurakshaContract.MemberEntry.CONTENT_URI,
                MemberColumns.PROJECTION,
                SurakshaContract.MemberEntry.COLUMN_ACCOUNT_NO + " != ? AND "
                        + SurakshaContract.MemberEntry.COLUMN_HAS_LOAN + " != 1 AND "
                        + SurakshaContract.MemberEntry.COLUMN_IS_LOAN_BLOCKED + " != 1",
                new String[]{String.valueOf(mMember.getAccountNo())},
                SurakshaContract.MemberEntry.TABLE_NAME + "." + SurakshaContract.MemberEntry.COLUMN_ACCOUNT_NO
                        + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> memberAccNoNameAddress = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String formatedMember = formatSecurityMemberDropdownText(cursor.getInt(MemberColumns.COL_MEMBER_ACCOUNT_NO)
                    , cursor.getString(MemberColumns.COL_MEMBER_NAME)
                    , cursor.getString(MemberColumns.COL_MEMBER_ADDRESS));
            memberAccNoNameAddress.add(formatedMember);

//            memberAccNoNameAddress.add(cursor.getString(MemberColumns.COL_MEMBER_ACCOUNT_NO) + ": "
//                    + WordUtils.toTitleCase(cursor.getString(MemberColumns.COL_MEMBER_NAME)) + " " +
//                    WordUtils.toTitleCase(cursor.getString(MemberColumns.COL_MEMBER_ADDRESS)));
            cursor.moveToNext();
        }

        addMemberNamesToAutoComplete(memberAccNoNameAddress);
    }

    String formatSecurityMemberDropdownText(int account_number, String name, String address) {
        return String.valueOf(account_number) + ": "
                + WordUtils.toTitleCase(name) + " " +
                WordUtils.toTitleCase(address);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void addMemberNamesToAutoComplete(List<String> memberAccNoNameAddress) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        final ArrayAdapter<String> adapter =
                new ArrayAdapter<>(getApplicationContext(),
                        android.R.layout.simple_dropdown_item_1line, memberAccNoNameAddress);
        txtSecurityMemberName.setDropDownBackgroundResource(R.color.green_dull);
        txtSecurityMemberName.setAdapter(adapter);
        txtSecurityMemberName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (TextUtils.isEmpty(txtSecurityMemberName.getText())) {
                    txtSecurityMemberName.setText("");
                    mSecurityAccountNo = -1;
                }
                return true;
            }
        });
        txtSecurityMemberName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && mSecurityAccountNo == -1) {
                    txtSecurityMemberName.setText("");
                }
            }
        });
        txtSecurityMemberName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String memberNameAndAddress = adapter.getItem(position);
                mSecurityAccountNo = Integer.parseInt(memberNameAndAddress.split(":")[0]);
                clearAutoComplete.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            setResult(Activity.RESULT_CANCELED);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnIssueLoan:
                String times = txtLoanInstalmentTimes.getText().toString().trim();
                //Validating Loan Amount Column
                String loanAmount = String.valueOf(txtAmountIssueLoan.getText());
                if (TextUtils.isEmpty(loanAmount)) {
                    txtAmountIssueLoan.setError(getString(R.string.enter_a_valid_amount));
                    txtAmountIssueLoan.requestFocus();
                } else if (!TextUtils.isEmpty(loanAmount) && Double.parseDouble(loanAmount) < 1000) {
                    txtAmountIssueLoan.setError(getString(R.string.amount_cannot_be_that_small));
                    txtAmountIssueLoan.requestFocus();
                } else if (!TextUtils.isEmpty(loanAmount) && Double.parseDouble(loanAmount) > mMaxLoanAmount) {
                    txtAmountIssueLoan.setError(mMember.getName() + " has not enough deposits. Loan amount cannot be more than " + mMaxLoanAmount + ".");
                    txtAmountIssueLoan.requestFocus();
                } else if (TextUtils.isEmpty(times)) {
                    txtLoanInstalmentTimes.setError(getString(R.string.enter_a_valid_number));
                    txtLoanInstalmentTimes.requestFocus();
                } else if (!TextUtils.isEmpty(times) && Integer.parseInt(times) == 0) {
                    txtLoanInstalmentTimes.setError(getString(R.string.enter_a_valid_number));
                    txtLoanInstalmentTimes.requestFocus();
                } else if (!TextUtils.isEmpty(txtLoanInstalmentAmount.getText())
                        && Double.parseDouble(String.valueOf(txtLoanInstalmentAmount.getText())) == 0) {
                    txtLoanInstalmentAmount.setError(getString(R.string.enter_a_valid_number));
                    txtLoanInstalmentAmount.requestFocus();
                } else if (mSecurityAccountNo == -1) {
                    txtSecurityMemberName.setError(getString(R.string.security_member_cannot_be_empty));
                    txtSecurityMemberName.requestFocus();
                } else {   //no errors in input
                    btnIssueLoan.setEnabled(false);
                    if (editMode) {
                        mLoanIssue = getLoanDetailsFromInput(mLoanIssue);
                    } else {
                        mLoanIssue = getLoanDetailsFromInput(null);
                    }
                    //Add the member to database
                    new IssueLoanTask(mLoanIssue).execute();
                }
                break;
            default:
                break;
        }

    }


    /**
     * Represents an asynchronous loanIssue task used to authenticate
     * the user.
     */
    public class IssueLoanTask extends AsyncTask<Void, Void, Boolean> {
        private LoanIssue mLoanIssue;
        private boolean isSmsSent = false;
        private boolean isSmsSentToSecurity = false;
        private Member mSecurityMember;

        IssueLoanTask(LoanIssue mLoanIssue) {
            this.mLoanIssue = mLoanIssue;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Context context = getApplicationContext();
            mSecurityMember = Member.getMemberFromAccountNumber(context,
                    mLoanIssue.getSecurityAccountNo());
            mLoanIssue.setSecurityMember(mSecurityMember);

            ContentValues values = LoanIssue.getLoanIssuedContentValues(mLoanIssue);
            long loanIssueId;
            if (editMode) {
                loanIssueId = mLoanIssue.getId();
                getContentResolver().update(
                        SurakshaContract.LoanIssueEntry.CONTENT_URI, values,
                        SurakshaContract.LoanIssueEntry._ID + "=?",
                        new String[]{String.valueOf(loanIssueId)});
            } else {
                Uri loanIssueUri = context.getContentResolver().insert(
                        SurakshaContract.LoanIssueEntry.CONTENT_URI, values);
                loanIssueId = SurakshaContract.LoanIssueEntry.getLoanIssueIdFromUri(loanIssueUri);
            }

            //Save Loan Transaction
            Transaction txnIssueLoan = new Transaction(context,
                    mLoanIssue.getAccountNumber(),
                    mLoanIssue.getAmount(),
                    SurakshaContract.TxnEntry.PAYMENT_VOUCHER,
                    SurakshaContract.TxnEntry.LOAN_ISSUED_LEDGER,
                    mLoanIssue.getPurpose(),
                    AuthUtils.getAuthenticatedOfficerId(context));
            txnIssueLoan.setLoanPayedId(loanIssueId);
            txnIssueLoan.setPaymentDate(System.currentTimeMillis());
//            txnIssueLoan.setCreatedAt(System.currentTimeMillis());
            values = Transaction.getTxnContentValues(txnIssueLoan);

            if (editMode) {
                context.getContentResolver().update(
                        SurakshaContract.TxnEntry.CONTENT_URI, values,
                        SurakshaContract.TxnEntry._ID + "=?",
                        new String[]{String.valueOf(mLoanIssue.getTransaction().getId())});
            } else {
                context.getContentResolver().insert(SurakshaContract.TxnEntry.CONTENT_URI, values);
            }

            mMember.saveHasLoan(context, true);
            mMember.saveIsLoanBlocked(context, true);
            //Set Security Member loan Blocked
            mSecurityMember.saveIsLoanBlocked(context, true);
            if (!editMode && SmsUtils.smsEnabledAfterLoanIssued(context) && mMember.isSmsEnabled()) {
                String mobileNumber = mMember.getMobile();
                String message = mMember.getName() + ", Rs "
                        + (long) mLoanIssue.getAmount() + " loan sanctioned in your "
                        + getResources().getString(R.string.app_name) + " account ["
                        + mLoanIssue.getAccountNumber()
                        + "] by guarantee of "
                        + mSecurityMember.getName()
                        + "[" + mLoanIssue.getSecurityAccountNo() + "]";
                String securityMemberMessage = mMember.getName()
                        + " has sanctioned Rs " + (long) mLoanIssue.getAmount()
                        + " loan from Suraksha on your Guarantee.";
                isSmsSent = SmsUtils.sendSms(message, mobileNumber);
                if (isSmsSent)
                    isSmsSentToSecurity = SmsUtils.sendSms(securityMemberMessage, mSecurityMember.getMobile());
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            super.onPostExecute(success);
            Context context = getApplicationContext();
            if (success) {
                String status = getString(R.string.loan_issued_successfully);
                if (editMode) {
                    status = "Updated successfully";
                }
                Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
                if (isSmsSent) {
                    StringBuilder msg = new StringBuilder();
                    msg.append("SMS sent to ").append(mMember.getName());
                    if (isSmsSentToSecurity) {
                        msg.append(" and ").append(mSecurityMember.getName());
                    }
                    Toast.makeText(context, msg.toString(), Toast.LENGTH_SHORT).show();
                }

                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", mMember);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            } else {
                Toast.makeText(context, "Insertion Failed. ", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {

        }
    }

    public interface MemberColumns {
        String[] PROJECTION = {
                SurakshaContract.MemberEntry.TABLE_NAME + "." + SurakshaContract.MemberEntry._ID,
                SurakshaContract.MemberEntry.COLUMN_NAME,
                SurakshaContract.MemberEntry.COLUMN_ADDRESS,
                SurakshaContract.MemberEntry.COLUMN_ACCOUNT_NO
        };
        int COL_MEMBER_ID = 0;
        int COL_MEMBER_NAME = 1;
        int COL_MEMBER_ADDRESS = 2;
        int COL_MEMBER_ACCOUNT_NO = 3;
    }
}
