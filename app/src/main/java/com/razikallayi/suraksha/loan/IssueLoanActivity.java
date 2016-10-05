package com.razikallayi.suraksha.loan;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.razikallayi.suraksha.NumberToWords;
import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.data.SurakshaContract;
import com.razikallayi.suraksha.member.Member;
import com.razikallayi.suraksha.txn.Transaction;
import com.razikallayi.suraksha.utils.AuthUtils;
import com.razikallayi.suraksha.utils.LetterAvatar;
import com.razikallayi.suraksha.utils.LoanUtils;
import com.razikallayi.suraksha.utils.SmsUtils;
import com.razikallayi.suraksha.utils.WordUtils;

import java.util.ArrayList;
import java.util.List;

public class IssueLoanActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String ARG_MEMBER_ID = "member_id";
    public static final String ARG_ACCOUNT_NUMBER = "account_number";
    private static final int SECURITY_MEMBER_NAME_LOADER = 0;

    private Member mMember;
    private LoanIssue mLoanIssue;
    private int mSecurityAccountNo = -1;

    private int mMaxLoanAmount = 10000;
    private int mDefaultInstalmentTimes = 10;

    private EditText txtAmountIssueLoan;
    private EditText txtPurposeIssueLoan;
    private AutoCompleteTextView txtSecurityMemberName;
    private EditText txtOfficeStatement;
    private Button btnIssueLoan;
    private EditText txtLoanInstalmentAmount;
    private EditText txtLoanInstalmentTimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loan_issue_activity);

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

        NestedScrollView sv = (NestedScrollView) findViewById(R.id.LoanIssueForm);
        long mMemberId = getIntent().getLongExtra(ARG_MEMBER_ID, -1);
        mMember = Member.getMemberFromId(getApplicationContext(), mMemberId);
//        mAccountNo = getIntent().getIntExtra(ARG_ACCOUNT_NUMBER, -1);

        //Declare and initialise member name and account text views
        TextView lblMemberNameLoanIssue = (TextView) sv.findViewById(R.id.lblMemberNameLoanIssue);
        TextView lblAddressLoanIssue = (TextView) sv.findViewById(R.id.lblAddressLoanIssue);
        TextView lblAccountNumberInLoanIssueTitle = (TextView) sv.findViewById(R.id.lblAccountNumberInLoanIssueTitle);
        ImageView imgAvatarMemberLoanIssue = (ImageView) sv.findViewById(R.id.imgAvatarMemberLoanIssue);

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

//      Declaring editTexts of loan Amount and instalment...
        txtAmountIssueLoan = (EditText) sv.findViewById(R.id.txtAmountIssueLoan);
        txtLoanInstalmentTimes = (EditText) sv.findViewById(R.id.txtLoanInstalmentTimes);
        txtLoanInstalmentAmount = (EditText) sv.findViewById(R.id.txtLoanInstalmentAmount);
        txtPurposeIssueLoan = (EditText) sv.findViewById(R.id.txtPurposeIssueLoan);
        txtOfficeStatement = (EditText) sv.findViewById(R.id.txtOfficeStatement);
        txtSecurityMemberName = (AutoCompleteTextView) sv.findViewById(R.id.txtSecurityMemberName);
        final TextView lblAmountInWordsLoanIssue = (TextView) sv.findViewById(R.id.lblAmountInWordsLoanIssue);

        //Setting default values in editTexts
        mDefaultInstalmentTimes = Integer.parseInt(LoanUtils.getDefaultLoanInstalmentTimes(getApplicationContext()));
        txtLoanInstalmentTimes.setText(String.valueOf(mDefaultInstalmentTimes));

        mMaxLoanAmount = Integer.parseInt(LoanUtils.getMaximumLoanAmount(getApplicationContext()));
        txtAmountIssueLoan.setText(String.valueOf(mMaxLoanAmount));
        int instalmentAmount = (mMaxLoanAmount / mDefaultInstalmentTimes);
        txtLoanInstalmentAmount.setText(String.valueOf(instalmentAmount));

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
                        String inWords = NumberToWords.convert(Long.valueOf(String.valueOf(txtAmountIssueLoan.getText())));
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

        getSupportLoaderManager().initLoader(SECURITY_MEMBER_NAME_LOADER, null, this);

        btnIssueLoan = (Button) sv.findViewById(R.id.btnIssueLoan);
        btnIssueLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String times = txtLoanInstalmentTimes.getText().toString().trim();
                //Validating Loan Amount Column
                String loanAmount = String.valueOf(txtAmountIssueLoan.getText());
                if (TextUtils.isEmpty(loanAmount)) {
                    txtAmountIssueLoan.setError(getString(R.string.enter_a_valid_amount));
                    txtAmountIssueLoan.requestFocus();
                } else if (!TextUtils.isEmpty(loanAmount) && Integer.parseInt(loanAmount) == 0) {
                    txtAmountIssueLoan.setError(getString(R.string.enter_a_valid_amount));
                    txtAmountIssueLoan.requestFocus();
                } else if (!TextUtils.isEmpty(loanAmount) && Integer.parseInt(loanAmount) > mMaxLoanAmount) {
                    txtAmountIssueLoan.setError("Loan amount cannot be more than " + mMaxLoanAmount + ". Change maximum loanable amount in settings.");
                    txtAmountIssueLoan.requestFocus();
                } else if (TextUtils.isEmpty(times)) {
                    txtLoanInstalmentTimes.setError(getString(R.string.enter_a_valid_number));
                    txtLoanInstalmentTimes.requestFocus();
                } else if (!TextUtils.isEmpty(times) && Integer.parseInt(times) == 0) {
                    txtLoanInstalmentTimes.setError(getString(R.string.enter_a_valid_number));
                    txtLoanInstalmentTimes.requestFocus();
                } else if (!TextUtils.isEmpty(txtLoanInstalmentAmount.getText())
                        && Integer.parseInt(String.valueOf(txtLoanInstalmentAmount.getText())) == 0) {
                    txtLoanInstalmentAmount.setError(getString(R.string.enter_a_valid_number));
                    txtLoanInstalmentAmount.requestFocus();
                } else if (mSecurityAccountNo == -1) {
                    txtSecurityMemberName.setError(getString(R.string.security_member_cannot_be_empty));
                    txtSecurityMemberName.requestFocus();
                } else {   //no errors in input
                    btnIssueLoan.setEnabled(false);
                    mLoanIssue = getLoanDetailsFromInput();

                    //Add the member to database
                    new IssueLoanTask(mLoanIssue).execute();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        int maxLoanAmount = Integer.parseInt(LoanUtils.getMaximumLoanAmount(getApplicationContext()));
        // update the maxLoanAmount in our second pane using the fragment manager
        mMaxLoanAmount = maxLoanAmount;
    }

    private void calculateInstalmentAmount() {
        Double amount = Double.parseDouble(String.valueOf(txtAmountIssueLoan.getText()));
        String strTimes = String.valueOf(txtLoanInstalmentTimes.getText());
        if (!strTimes.isEmpty()) {
            int times = Integer.parseInt(strTimes);
            if (amount > 0 && times > 0) {
                txtLoanInstalmentAmount.setText(String.valueOf((int) (amount / times)));
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

    private LoanIssue getLoanDetailsFromInput() {
        double amount = Double.parseDouble(String.valueOf(txtAmountIssueLoan.getText()));
        int times = Integer.parseInt(String.valueOf(txtLoanInstalmentTimes.getText()));
        String officeStatement = String.valueOf(txtOfficeStatement.getText());

        String purpose = txtPurposeIssueLoan.getText().toString();
        int securityAccountNo = mSecurityAccountNo;

        LoanIssue loanIssue = new LoanIssue(mMember.getAccountNo(), amount, purpose,
                securityAccountNo, times, officeStatement);
        loanIssue.setCreatedAt(System.currentTimeMillis());
        loanIssue.setMember(mMember);
        return loanIssue;
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
            memberAccNoNameAddress.add(cursor.getString(MemberColumns.COL_MEMBER_ACCOUNT_NO) + ": "
                    + WordUtils.toTitleCase(cursor.getString(MemberColumns.COL_MEMBER_NAME)) + " " +
                    WordUtils.toTitleCase(cursor.getString(MemberColumns.COL_MEMBER_ADDRESS)));
            cursor.moveToNext();
        }

        addMemberNamesToAutoComplete(memberAccNoNameAddress);
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

                final ImageView clearAutoComplete = (ImageView) findViewById(R.id.clearAutoComplete);
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

        public IssueLoanTask(LoanIssue mLoanIssue) {
            this.mLoanIssue = mLoanIssue;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Context context = getApplicationContext();
            Member securityMember = Member.getMemberFromAccountNumber(context,
                    mLoanIssue.getSecurityAccountNo());
            mLoanIssue.setSecurityMember(securityMember);

            //Save Loan Issue
            ContentValues values = LoanIssue.getLoanIssuedContentValues(mLoanIssue);
            Uri loanIssueUri = context.getContentResolver().insert(
                    SurakshaContract.LoanIssueEntry.CONTENT_URI, values);
            long loanIssueId = SurakshaContract.LoanIssueEntry.getLoanIssueIdFromUri(loanIssueUri);

            //Save Loan Transaction
            Transaction txnIssueLoan = new Transaction(context,
                    mLoanIssue.getAccountNumber(),
                    mLoanIssue.getAmount(),
                    SurakshaContract.TxnEntry.PAYMENT_VOUCHER,
                    SurakshaContract.TxnEntry.LOAN_PAYED_LEDGER,
                    mLoanIssue.getPurpose(),
                    AuthUtils.getAuthenticatedOfficerId(context));
            txnIssueLoan.setLoanPayedId(loanIssueId);
            values = Transaction.getTxnContentValues(txnIssueLoan);
            context.getContentResolver().insert(SurakshaContract.TxnEntry.CONTENT_URI, values);

            //Set Member HasLoan Flag On
            mMember.saveHasLoan(context, true);
            mMember.saveIsLoanBlocked(context, true);
            //Set Security Member loan Blocked
            securityMember.saveIsLoanBlocked(context, true);
            if (SmsUtils.smsEnabledAfterLoanPayed(context)) {
                String mobileNumber = mMember.getMobile();
                String message = mMember.getName() + ", Rs"
                        + (int) mLoanIssue.getAmount() + " loan sanctioned in your "
                        + getResources().getString(R.string.app_name) + " account "
                        + mLoanIssue.getAccountNumber()
                        + " by guarantee of "
                        + mLoanIssue.getSecurityMember(context).getName()
                        + "(" + mLoanIssue.getSecurityAccountNo() + ")";
                SmsUtils.sendSms(message, mobileNumber);
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            super.onPostExecute(success);
            Context context = getApplicationContext();
            if (success) {
                Toast.makeText(context, getString(R.string.loan_issued_successfully), Toast.LENGTH_SHORT).show();
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
