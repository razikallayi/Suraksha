package com.razikallayi.suraksha.loan;


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
import android.widget.TextView;
import android.widget.Toast;

import com.razikallayi.suraksha.AvatarActivity;
import com.razikallayi.suraksha.BaseActivity;
import com.razikallayi.suraksha.DatePickerFragment;
import com.razikallayi.suraksha.NumberToWords;
import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.data.SurakshaContract;
import com.razikallayi.suraksha.member.Member;
import com.razikallayi.suraksha.txn.Transaction;
import com.razikallayi.suraksha.utils.AuthUtils;
import com.razikallayi.suraksha.utils.CalendarUtils;
import com.razikallayi.suraksha.utils.FontUtils;
import com.razikallayi.suraksha.utils.LetterAvatar;
import com.razikallayi.suraksha.utils.LoanUtils;
import com.razikallayi.suraksha.utils.SmsUtils;
import com.razikallayi.suraksha.utils.WordUtils;

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

    private long mMaxLoanAmount = 10000;
    private int mDefaultInstalmentTimes = 10;

    private EditText txtDateIssueLoan;
    private EditText txtAmountIssueLoan;
    private EditText txtPurposeIssueLoan;
    private AutoCompleteTextView txtSecurityMemberName;
    private EditText txtOfficeStatement;
    private Button btnIssueLoan;
    private EditText txtLoanInstalmentAmount;
    private EditText txtLoanInstalmentTimes;
    private long loanIssueDate = 0;
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
        TextView lblMemberNameLoanIssue = sv.findViewById(R.id.lblMemberNameLoanIssue);
        TextView lblAddressLoanIssue = sv.findViewById(R.id.lblAddressLoanIssue);
        TextView lblAccountNumberInLoanIssueTitle = sv.findViewById(R.id.lblAccountNumberInLoanIssueTitle);
        ImageView imgAvatarMemberLoanIssue = sv.findViewById(R.id.imgAvatarMemberLoanIssue);

        //Set values of member name, address, avatar and account number
        lblMemberNameLoanIssue.setText(WordUtils.toTitleCase(mMember.getName()));
        lblAddressLoanIssue.setText(WordUtils.toTitleCase(mMember.getAddress()));
        lblAccountNumberInLoanIssueTitle.setText(String.valueOf(mMember.getAccountNo()));
        if (mMember.getAvatarDrawable() != null) {
            imgAvatarMemberLoanIssue.setImageDrawable(mMember.getAvatarDrawable());
            imgAvatarMemberLoanIssue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), AvatarActivity.class);
                    intent.putExtra("avatar", mMember.getAvatar());
                    startActivity(intent);
                }
            });
        } else {
            int color = getResources().getColor(R.color.colorPrimaryLight);
            imgAvatarMemberLoanIssue.setImageDrawable(new LetterAvatar(getApplicationContext(),
                    color, mMember.getName().substring(0, 1).toUpperCase(), 24));
        }

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

        if (clearAutoComplete != null) {
            clearAutoComplete.setVisibility(View.VISIBLE);
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
            mMaxLoanAmount = Long.parseLong(LoanUtils.getMaximumLoanAmount(getApplicationContext()));
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
                    securityAccountNo, times, officeStatement, loanIssueDate);
            mLoanIssue.setMember(mMember);
        } else {
            mLoanIssue.setAmount(amount);
            mLoanIssue.setPurpose(purpose);
            mLoanIssue.setLoanInstalmentTimes(times);
            mLoanIssue.setOfficeStatement(officeStatement);
            mLoanIssue.setIssuedAt(loanIssueDate);
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
        if (editMode) {
            loanIssueDate = mLoanIssue.getIssuedAt();
        }
        if (loanIssueDate == 0) {
            String defaultLoanIssueDate = LoanUtils.getDefaultLoanIssueDate(getApplicationContext());
            loanIssueDate = Long.parseLong(defaultLoanIssueDate);
        }
        txtDateIssueLoan.setText(CalendarUtils.formatDate(loanIssueDate));
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

    private void popupCalendar() {
        final Calendar activeDate = Calendar.getInstance();
        activeDate.setTimeInMillis(loanIssueDate);
        DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(activeDate);
        datePickerFragment.show(getSupportFragmentManager(), null);
        datePickerFragment.setOnDateSetListener(new DatePickerFragment.OnDateSetListener() {
            @Override
            public void getDate(Calendar cal) {
                Calendar calendar = CalendarUtils.normalizeDate(cal);
                loanIssueDate = calendar.getTimeInMillis();
                txtDateIssueLoan.setText(CalendarUtils.formatDate(loanIssueDate));
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        Long maxLoanAmount = Long.parseLong(LoanUtils.getMaximumLoanAmount(getApplicationContext()));
        // update the maxLoanAmount in our second pane using the fragment manager
        mMaxLoanAmount = maxLoanAmount;
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
        txtSecurityMemberName.setDropDownBackgroundResource(R.color.blueGray);
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
                    txtAmountIssueLoan.setError("Loan amount cannot be more than " + mMaxLoanAmount + ". Change maximum loanable amount in settings.");
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

    /**
     * Represents an asynchronous loanIssue task used to authenticate
     * the user.
     */
    public class IssueLoanTask extends AsyncTask<Void, Void, Boolean> {
        private LoanIssue mLoanIssue;
        private boolean isSmsSent = false;

        IssueLoanTask(LoanIssue mLoanIssue) {
            this.mLoanIssue = mLoanIssue;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Context context = getApplicationContext();
            Member securityMember = Member.getMemberFromAccountNumber(context,
                    mLoanIssue.getSecurityAccountNo());
            mLoanIssue.setSecurityMember(securityMember);

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

            //Set Member HasLoan Flag On
            mMember.saveHasLoan(context, true);
            mMember.saveIsLoanBlocked(context, true);
            //Set Security Member loan Blocked
            securityMember.saveIsLoanBlocked(context, true);
            if (!editMode && SmsUtils.smsEnabledAfterLoanIssued(context)) {
                String mobileNumber = mMember.getMobile();
                String message = mMember.getName() + ", Rs"
                        + (int) mLoanIssue.getAmount() + " loan sanctioned in your "
                        + getResources().getString(R.string.app_name) + " account "
                        + mLoanIssue.getAccountNumber()
                        + " by guarantee of "
                        + mLoanIssue.getSecurityMember(context).getName()
                        + "(" + mLoanIssue.getSecurityAccountNo() + ")";
                isSmsSent = SmsUtils.sendSms(message, mobileNumber);
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
                if (isSmsSent)
                    Toast.makeText(context, "SMS sent to " + mMember.getName(), Toast.LENGTH_SHORT).show();
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

}
